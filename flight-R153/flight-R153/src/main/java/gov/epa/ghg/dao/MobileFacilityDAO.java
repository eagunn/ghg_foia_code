package gov.epa.ghg.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.util.DaoUtils;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.SpatialUtil;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
@Transactional
public class MobileFacilityDAO implements Serializable, MobileFacilityDaoInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;

	@Override
	public List<Facility> getEmitterList(String q, int year, String state,
			String countyFips, String lowE, String highE, int page,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String rs) {
		log.debug("finding Facility instances");
		String orderBy = "f.facilityName asc";
		switch (sortOrder) {
		case 1:
			orderBy = "f.facilityName desc";
		case 2:
		case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s "	+
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id in (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"group by f.id having " +
					(lowE.equals("0") ? "sum(e.co2eEmission) is null or ":"") +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE )) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " + 
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id in (select f.id "
					+ s + ") order by " + orderBy;
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for
			// facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			final int pageNumber = page;
			Query query = sessionFactory.getCurrentSession().createQuery(cQuery);
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			List<Facility> fList = new ArrayList<Facility>();
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0])
							.getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				query.setFirstResult(pageNumber * 30);
				query.setMaxResults(30);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					Facility f = new Facility();
					f.setId(df.getId().getFacilityId());
					// format Facility name string so that it is properly capitalized
//					f.setName(ServiceUtils.nullSafeHtmlUnescape(WordUtils.capitalizeFully(df.getFacilityName())));
                    f.setName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName().toUpperCase()));
					f.setAddress1(df.getAddress1());
					f.setAddress2(df.getAddress2());
					f.setCity(df.getCity());
					f.setState(df.getState());
					f.setZip(df.getZip());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					f.setEmissions(emission);
					f.setLatitude(df.getLatitude());
					f.setLongitude(df.getLongitude());
					fList.add(f);
				}
			}
			return fList;
		} catch (RuntimeException re) {
			log.error("find Facilities failed", re);
			throw re;
		}
	}

	@Override
	public List<Facility> getEmitterListAround(String q, int year,
			String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, LatLng center, double radius, String rs) {
		log.debug("finding Facility instances");
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s "	+
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id in (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"group by f.id having " +
					(lowE.equals("0") ? "sum(e.co2eEmission) is null or ":"") +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE )) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id in (select f.id " + s +
					") and sdo_within_distance(f.location,?,'unit=mile distance= :radius ) = 'TRUE'";
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for
			// facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(center.getLng(), center.getLat()));			
			Query query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year).setParameter("radius", radius);
			query.setCacheable(true);
			// query.setParameter(0, p, GeometryUserType.TYPE);
			query.setParameter(0, p);
			int numResults = DataAccessUtils.intResult(query.list());
			List<Facility> fList = new ArrayList<Facility>();
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0])
							.getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year).setParameter("radius", radius);
				// query.setParameter(0, p, GeometryUserType.TYPE);
				query.setParameter(0, p);
				query.setCacheable(true);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					Facility f = new Facility();
					f.setId(df.getId().getFacilityId());
                    // format Facility name string so that it is properly capitalized
//                    f.setName(ServiceUtils.nullSafeHtmlUnescape(WordUtils.capitalizeFully(df.getFacilityName())));
                    f.setName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName().toUpperCase()));
					f.setAddress1(df.getAddress1());
					f.setAddress2(df.getAddress2());
					f.setCity(df.getCity());
					f.setState(df.getState());
					f.setZip(df.getZip());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					f.setEmissions(emission);
					f.setLatitude(df.getLatitude());
					f.setLongitude(df.getLongitude());
					f.setDistance(SpatialUtil.distanceInMiles(p, df.getLocation()));
					fList.add(f);
				}
				Collections.sort(fList, new Comparator<Facility>() {
					public int compare(Facility f1, Facility f2) {
						return f1.getDistance().compareTo(f2.getDistance());
					}
				});
			}
			return fList;
		} catch (RuntimeException re) {
			log.error("find Facilities failed", re);
			throw re;
		}
	}
	
	@Override
	public List<Facility> getEmitterListWithin(String q, int year,
			String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, LatLng location, Geometry bounds, String rs) {
		log.debug("finding Facility instances");
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s "	+
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id in (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"group by f.id having " +
					(lowE.equals("0") ? "sum(e.co2eEmission) is null or ":"") +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE )) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo) +
					"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id in (select f.id " + s +
					") and sdo_inside(f.location,?) = 'TRUE'";
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for
			// facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(location.getLng(), location.getLat()));			
			Query query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			// query.setParameter(0, bounds, GeometryUserType.TYPE);
			query.setParameter(0, bounds);
			int numResults = DataAccessUtils.intResult(query.list());
			List<Facility> fList = new ArrayList<Facility>();
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0])
							.getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				// query.setParameter(0, bounds, GeometryUserType.TYPE);
				query.setParameter(0, bounds);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					Facility f = new Facility();
					f.setId(df.getId().getFacilityId());
                    // format Facility name string so that it is properly capitalized
//                    f.setName(ServiceUtils.nullSafeHtmlUnescape(WordUtils.capitalizeFully(df.getFacilityName())));
					f.setName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName().toUpperCase()));
					f.setAddress1(df.getAddress1());
					f.setAddress2(df.getAddress2());
					f.setCity(df.getCity());
					f.setState(df.getState());
					f.setZip(df.getZip());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					f.setEmissions(emission);
					f.setLatitude(df.getLatitude());
					f.setLongitude(df.getLongitude());
					f.setDistance(SpatialUtil.distanceInMiles(p, df.getLocation()));
					fList.add(f);
				}
				Collections.sort(fList, new Comparator<Facility>() {
					public int compare(Facility f1, Facility f2) {
						return f1.getDistance().compareTo(f2.getDistance());
					}
				});
			}
			return fList;
		} catch (RuntimeException re) {
			log.error("find Facilities failed", re);
			throw re;
		}
	}
}
