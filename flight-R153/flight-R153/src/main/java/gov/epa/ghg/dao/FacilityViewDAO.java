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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.FacilityView;
import gov.epa.ghg.domain.PubLdcFacility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityExport;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.DaoUtils;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import lombok.extern.log4j.Log4j2;

/**
 * A data access object (DAO) providing persistence and search support for
 * Facility entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 *
 * @see gov.epa.ghg.domain.DimFacility
 */

@Log4j2
@Repository
@Transactional
public class FacilityViewDAO implements Serializable, FacilityViewDaoInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	public FacilityList getEmitterListStoppedReporting(String q, int year, String state, String countyFips, String msaCode, int page,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		String reportingStatus = "STOPPED_REPORTING";
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					")" +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and (" +
					DaoUtils.gasFilter(gases) +
					"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery;
			if (msaCode.equals("")) {
				hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			} else {
				hQuery = "from DimFacility f where f.id IN "
						+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year = :year)" + " and f.id IN (select f.id " + s + ") order by " + orderBy;
			}
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			final int pageNumber = page;
			Query query;
			if (msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				if (msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
				} else {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
				}
				query.setCacheable(true);
				query.setFirstResult(pageNumber * 100);
				query.setMaxResults(100);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					} else {
						// if String is on, we want this to remain null so the velocity logic in facility_summary_panel.htm will render ("N/A") as it expects null in this case
						if (!ReportingStatusQueryFilter.isReportingStatusEnabled(year)) {
							fv.setTotalCo2e(BigDecimal.ZERO);
						}
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "E");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	public FacilityList getEmitterList(String q, int year, String state, String countyFips, String msaCode, String lowE, String highE, int page,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String sectorType, String reportingStatus, String emissionsType, Long tribalLandId) {
		String st = state;
		if (!msaCode.equals("") || (("L".equals(sectorType) || "E".equals(sectorType)) && !state.equals("") && msaCode.equals("") && countyFips.equals(""))) {
			state = "";
		}
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(st, tribalLandId) +
					ReportingStatusQueryFilter.filterEmissionsRange(reportingStatus, lowE, highE) +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(st, tribalLandId) +
					"and (" +
					DaoUtils.gasFilter(gases) +
					"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery;
			
			String dfQuery = null;
			int dfCount = 0;
			// PUB - 520
			if (("L".equals(sectorType) || "E".equals(sectorType)) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				hQuery = "from PubLdcFacility l where l.id.state= :st and l.id.facilityId != '0' and l.id.year= :year and l.facility.id in (select f.id " + s + ")";
				
				state = st;
				String ss = "from DimFacility f " +
						"join f.facStatus fs " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where f.id IN (select distinct f.id " +
						"from DimFacility f " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where (s.sectorType is null or s.sectorType = 'E') " +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						ReportingStatusQueryFilter.filterEmissionsRange(reportingStatus, lowE, highE) +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						"and (" +
						DaoUtils.gasFilter(gases) +
						"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
				
				dfQuery = "from DimFacility f where f.id NOT IN (select plf.facility.id from PubLdcFacility plf where plf.id.year = :year) and f.id IN (select f.id " + ss + ")";
				final String cQuery = "select count(*) " + dfQuery;
				Query query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("year", (long) year);
				query.setCacheable(true);
				dfCount = DataAccessUtils.intResult(query.list());
			} else if (msaCode.equals("")) {
				hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			} else {
				hQuery = "from DimFacility f where f.id IN "
						+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year = :year)" + " and f.id IN (select f.id " + s + ") order by " + orderBy;
			}
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			final int pageNumber = page;
			Query query;
			if (("L".equals(sectorType) || "E".equals(sectorType)) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("st", st).setParameter("year", (long) year);
			} else if (msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0 || dfCount > 0) {
				
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				if (("L".equals(sectorType) || "E".equals(sectorType)) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("st", st).setParameter("year", (long) year);
				} else if (msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
				} else {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
				}
				query.setCacheable(true);
				query.setFirstResult(pageNumber * 100);
				query.setMaxResults(100);
				List<DimFacility> dfList;
				// PUB - 520
				if (("L".equals(sectorType) || "E".equals(sectorType)) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
					Query queryDf = sessionFactory.getCurrentSession().createQuery(dfQuery).setParameter("year", (long) year);
					queryDf.setCacheable(true);
					List<DimFacility> dfResults = queryDf.list();
					
					dfList = new ArrayList<DimFacility>();
					
					List<PubLdcFacility> plfResults = query.list();
					
					for (PubLdcFacility plf : plfResults) {
						DimFacility df = plf.getFacility();
						if (!dfList.contains(df)) {
							dfList.add(df);
						}
					}
					
					for (DimFacility df : dfResults) {
						dfList.add(df);
					}
					
					Collections.sort(dfList, new Comparator<DimFacility>() {
						public int compare(DimFacility facility1, DimFacility facility2) {
							return facility1.getFacilityName().compareTo(facility2.getFacilityName());
						}
					});
					f.setTotalCount(dfList.size());
				} else {
					dfList = query.list();
				}
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					} else {
						// if String is on, we want this to remain null so the velocity logic in facility_summary_panel.htm will render ("N/A") as it expects null in this case
						if (!ReportingStatusQueryFilter.isReportingStatusEnabled(year)) {
							fv.setTotalCo2e(BigDecimal.ZERO);
						}
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "E");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	public List<FacilityExport> getEmitters(String q, int year, String state, String countyFips, String msaCode, Long tribalLandId, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String reportingStatus, String emissionsType,
			int sc, int is, String basin) {
		if (!msaCode.equals("")) {
			state = "";
		}
		Character sectorType = 'E';
		if (ds.equals("S") || ds.equals("I")) {
			sectorType = ds.charAt(0);
		}
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		String s = null;
		try {
			// Data Type = Suppliers
			if (ds.equals("S") && sc != 0) {
				s = "from DimFacility f " +
						"join f.facStatus fs " +
						"join f.emissions e " +
						"join e.sector s " +
						"join e.subSector ss " +
						"where f.id IN (select distinct f.id " +
						"from DimFacility f " +
						"join f.emissions e " +
						"join e.sector s " +
						"where s.sectorType = 'S' group by f.id) " +
						DaoUtils.supplierWhereClause(q, qo, year, sc, state) +
						"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			}
			// Data Type = CO2 Injectors
			else if (ds.equals("I")) {
				s = "from DimFacility f " +
						"join f.facStatus fs " +
						"left join f.emissions e " +
						"left join e.sector s " +
						"left join e.gas g " +
						"where f.id IN (select distinct f.id " +
						"from DimFacility f " +
						"left join f.emissions e " +
						"left join e.sector s " +
						"left join e.gas g " +
						"where s.sectorType = 'I' " +
						DaoUtils.co2InjectionWhereClause(q, year, state, countyFips,
								gases, qo, is) +
						"group by f.id having " +
						DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
						"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
						DaoUtils.co2InjectionWhereClause(q, year, state, countyFips,
								gases, qo, is) +
						"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and s.sectorType = 'I' and fs.id.year = :year and fs.facilityType = 'I' " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			}
			// Data Type = Onshore production
			else if (ds.equals("O")) {
				s = "from DimFacility f " +
						"join f.facStatus fs " +
						"left join f.emissions e " +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.basinFilter(basin) +
						DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
						"where f.id IN (select distinct f.id " +
						"from DimFacility f " +
						"left join f.emissions e " +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.basinFilter(basin) +
						DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
						"where (s.sectorType is null or s.sectorType = 'E') " +
						DaoUtils.basinWhereClause(basin) +
						DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
								gases, sectors, qo) +
						"group by f.id having " +
						DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
						"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
						DaoUtils.basinWhereClause(basin) +
						DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
								gases, sectors, qo) +
						"and (" +
						DaoUtils.gasFilter(gases) +
						"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			} else {
				s = "from DimFacility f " +
						"join f.facStatus fs " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where ";
				if (!msaCode.equals("")) {
					s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
				}
				// PUB-520
						/*if ((ds.equals("L") ||ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
							s += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
						}*/
				s += "f.id IN (select distinct f.id " +
						"from DimFacility f " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where (s.sectorType is null or s.sectorType = :sectorType) " +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						DaoUtils.tribalLandWhereClause(state, tribalLandId) +
						"group by f.id having " +
						(lowE.equals("0") ? "sum(e.co2eEmission) is null or " : "") +
						"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						DaoUtils.tribalLandWhereClause(state, tribalLandId) +
						"and (" +
						DaoUtils.gasFilter(gases) +
						"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = :sectorType) and fs.id.year = :year and fs.facilityType = :sectorType " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			}
			final String hQuery;
			if (ds.equals("I") && !msaCode.equals("")) {
				hQuery = "from DimFacility f where f.id IN "
						+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year = :year)" + " and f.id IN (select f.id " + s + ") order by " + orderBy;
			} else {
				hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
				// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			}
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			List<FacilityExport> feList = new ArrayList<FacilityExport>();
			Query query;
			if (ds.equals("S") && sc != 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("year", (long) year);
			} else if (ds.equals("I")) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			} else if (ds.equals("O")) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			} else {
				if (!msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("sectorType", sectorType)
							.setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
				} else {
					query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("sectorType", sectorType).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
							.setParameter("year", (long) year);
				}
			}
			query.setCacheable(true);
			// Mapping for a Facility's emission values
			Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
			// Mapping for a Facility's reporting year
			Map<Long, Long> repYear = new HashMap<Long, Long>();
			
			List<Object[]> results = query.list();
			for (Object[] result : results) {
				DimFacilityId dimFacId = (DimFacilityId) result[0];
				Long facId = dimFacId.getFacilityId();
				Long reportingYear = dimFacId.getYear();
				BigDecimal emission = (BigDecimal) result[1];
				em.put(facId, emission);
				repYear.put(facId, reportingYear);
			}
			if (results.size() > 0) {
				if (ds.equals("I") && !msaCode.equals("")) {
					if (ds.equals("S") && sc != 0) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
					} else if (ds.equals("I")) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE))
								.setParameter("highE", new BigDecimal(highE));
					} else if (ds.equals("O")) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE))
								.setParameter("highE", new BigDecimal(highE));
					} else {
						if (!msaCode.equals("")) {
							query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("msaCode", msaCode)
									.setParameter("sectorType", sectorType).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
						} else {
							query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("sectorType", sectorType)
									.setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
						}
					}
				} else {
					if (ds.equals("S") && sc != 0) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
					} else if (ds.equals("I")) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
					} else if (ds.equals("O")) {
						query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
					} else {
						if (!msaCode.equals("")) {
							query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("sectorType", sectorType)
									.setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
						} else {
							query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("sectorType", sectorType).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
									.setParameter("year", (long) year);
						}
					}
				}
				// query = sessionFactory.getCurrentSession().createQuery(hQuery);
				query.setCacheable(true);
				List<DimFacility> dfList = query.list();
				
				for (DimFacility df : dfList) {
					FacilityExport fe = new FacilityExport();
					fe.setFacilityId(df.getId().getFacilityId());
					fe.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fe.setAddress1(df.getAddress1());
					fe.setAddress2(df.getAddress2());
					fe.setLatitude(df.getLatitude());
					fe.setLongitude(df.getLongitude());
					fe.setCounty(df.getCounty());
					fe.setCity(df.getCity());
					fe.setState(df.getState());
					fe.setStateName(df.getStateName());
					fe.setZip(df.getZip());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fe.setTotalCo2e(emission);
					} else {
						fe.setTotalCo2e(BigDecimal.ZERO);
					}
					fe.setParentCompanies(df.getParentCompany());
					fe.setSubParts(df.getReportedSubparts());
					fe.setReportingYear(repYear.get(df.getId().getFacilityId()));
					feList.add(fe);
				}
			}
			return feList;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	public FacilityList getAutoCompleteEmitterList(String q, int year, String state, String countyFips, String msaCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String reportingStatus, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			s += "f.id IN (select distinct f.id " +
					"from DimFacility f " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.autoCompleteEmitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.autoCompleteEmitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and (" +
					DaoUtils.gasFilter(gases) +
					"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			Query query;
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0) {
				if (!msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
				} else {
					query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				}
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				if (!msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
				} else {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				}
				query.setCacheable(true);
				query.setFirstResult(0);
				query.setMaxResults(100);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					} else {
						fv.setTotalCo2e(BigDecimal.ZERO);
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "E");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	public FacilityList getOnShoreProductionList(String q, int year, String basin, String lowE, String highE, int page,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String reportingStatus) {
		
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.basinFilter(basin) +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.basinFilter(basin) +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.basinWhereClause(basin) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.basinWhereClause(basin) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"and (" +
					DaoUtils.gasFilter(gases) +
					"g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			final int pageNumber = page;
			Query query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				query.setFirstResult(pageNumber * 100);
				query.setMaxResults(100);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					} else {
						// if String is on, we want this to remain null so the velocity logic in facility_summary_panel.htm will render ("N/A") as it expects null in this case
						if (!ReportingStatusQueryFilter.isReportingStatusEnabled(year)) {
							fv.setTotalCo2e(BigDecimal.ZERO);
						}
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "E");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	@Override
	public FacilityList getCO2InjectionList(String q, int year, String state, String countyFips, String msaCode, String lowE, String highE, int page,
			GasFilter gases, QueryOptions qo, int sortOrder, int is, String reportingStatus) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					"where s.sectorType = 'I' " +
					DaoUtils.co2InjectionWhereClause(q, year, state, countyFips,
							gases, qo, is) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.co2InjectionWhereClause(q, year, state, countyFips,
							gases, qo, is) +
					"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and s.sectorType = 'I' and fs.id.year = :year and fs.facilityType = 'I' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery;
			
			if (msaCode.equals("")) {
				hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			} else {
				hQuery = "from DimFacility f where f.id IN "
						+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year = :year)" + " and f.id IN (select f.id " + s + ") order by " + orderBy;
			}
			final String cQuery = "select count(*) " + hQuery;
			// Concat a instead of s if total co2e needs to show total CO2e for facility that does not vary based on filter selection
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			final int pageNumber = page;
			Query query;
			if (msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			}
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				if (msaCode.equals("")) {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
				} else {
					query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
				}
				query.setCacheable(true);
				query.setFirstResult(pageNumber * 100);
				query.setMaxResults(100);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					fv.setUuRandDExempt(df.getUuRandDExempt());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "I");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	@Override
	public FacilityList getAutoCompleteSupplierList(String q, QueryOptions qo, int year, int sc, int sortOrder, String reportingStatus) {
		log.debug("finding FacilityView instance ");
		String orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(f.facilityName,'NLS_SORT=BINARY_CI') desc";
			case 2:
			case 3:
		}
		try {
			String s = "from DimFacility f " +
					"join f.facStatus fs " +
					"join f.emissions e " +
					"join e.sector s " +
					"join e.subSector ss " +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					"join f.emissions e " +
					"join e.sector s " +
					"where s.sectorType = 'S' group by f.id) " +
					DaoUtils.autoCompleteSupplierWhereClause(q, qo, year, sc) +
					"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
			final String hQuery = "from DimFacility f where f.id IN (select f.id " + s + ") order by " + orderBy;
			final String cQuery = "select count(*) " + hQuery;
			final String aQuery = "select f.id, sum(e.co2eEmission) " + s;
			Query query = sessionFactory.getCurrentSession().createQuery(cQuery).setParameter("year", (long) year);
			query.setCacheable(true);
			int numResults = DataAccessUtils.intResult(query.list());
			FacilityList f = new FacilityList();
			List<FacilityView> fvList = new ArrayList<FacilityView>();
			f.setTotalCount(numResults);
			if (numResults > 0) {
				query = sessionFactory.getCurrentSession().createQuery(aQuery).setParameter("year", (long) year);
				query.setCacheable(true);
				Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
					BigDecimal emission = (BigDecimal) result[1];
					em.put(facId, emission);
				}
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
				query.setCacheable(true);
				query.setFirstResult(0);
				query.setMaxResults(100);
				List<DimFacility> dfList = query.list();
				for (DimFacility df : dfList) {
					FacilityView fv = new FacilityView();
					fv.setFacilityId(df.getId().getFacilityId());
					fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
					fv.setAddress1(df.getAddress1());
					fv.setAddress2(df.getAddress2());
					fv.setLatitude(df.getLatitude());
					fv.setLongitude(df.getLongitude());
					fv.setCity(df.getCity());
					fv.setState(df.getState());
					fv.setStateName(df.getStateName());
					fv.setZip(df.getZip());
					fv.setCo2Captured(df.getCo2Captured());
					fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
					BigDecimal emission = em.get(df.getId().getFacilityId());
					if (emission != null) {
						fv.setTotalCo2e(emission);
					}
					Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), "S");
					ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
					fv.setReportingStatus(rs);
					fvList.add(fv);
				}
			}
			f.setFacilities(fvList);
			return f;
		} catch (RuntimeException re) {
			log.error("find FacilityViews failed", re);
			throw re;
		}
	}
	
	@Override
	public List<DimCounty> getEmitterFacilityCounties(String q, int year, String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String reportingStatus) {
		try {
			String s = "select distinct f.countyFips from DimFacility f " +
					"join f.facStatus fs " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where (s.sectorType is null or s.sectorType = 'E') " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"and (g.gasCode is null or g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.countyFips";
			final String hQuery = "from DimCounty c where c.countyFips in (" + s + ")";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			List<DimCounty> result = query.list();
			Collections.sort(result, new Comparator<DimCounty>() {
				public int compare(DimCounty county1, DimCounty county2) {
					return county1.getCountyName().compareTo(county2.getCountyName());
				}
			});
			return result;
		} catch (RuntimeException re) {
			log.error("findSub FacilityViews failed", re);
			throw re;
		}
	}
	
	public List<DimCounty> getFacilityCounties(String state, int year) {
		try {
			String s = "select distinct f.countyFips from DimFacility f " +
					"join f.emissions e " +
					"join e.sector s " +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					"join f.emissions e " +
					"join e.sector s " +
					"where s.sectorType = 'E' and f.state = :state group by f.id) " +
					"and s.sectorType = 'E' and f.id.year = :year group by f.countyFips";
			final String hQuery = "from DimCounty c where c.countyFips in (" + s + ")";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("state", state).setParameter("year", (long) year);
			query.setCacheable(true);
			List<DimCounty> result = query.list();
			Collections.sort(result, new Comparator<DimCounty>() {
				public int compare(DimCounty county1, DimCounty county2) {
					return county1.getCountyName().compareTo(county2.getCountyName());
				}
			});
			return result;
		} catch (RuntimeException re) {
			log.error("findSub FacilityViews failed", re);
			throw re;
		}
	}
	
	public SectorAggregate getEmitterSectorAggregateForStoppedReportingFacilities(
			String q, int year, String state, String countyFips, String msaCode,
			String basin, GasFilter gases, SectorFilter sectors,
			QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String vQry = "select s.sectorCode, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and";
		}
		// PUB-520
			/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
		vQry += " f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.basinFilter(basin) +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.basinWhereClause(basin) +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				")" +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and (" +
				DaoUtils.gasFilter(gases) +
				"g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by s.sectorCode";
		final String hQuery = vQry;
		try {
			SectorAggregate sectorAggregate = new SectorAggregate();
			Query query;
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
			}
			// Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
			query.setCacheable(true);
			List<Object[]> results = query.list();
			
			for (Object[] result : results) {
				if (result[0].equals(DaoUtils.POWERPLANTS)) {
					sectorAggregate.setPowerplantEmission((BigDecimal) result[1]);
					sectorAggregate.setPowerplantCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.WASTE)) {
					sectorAggregate.setLandfillEmission((BigDecimal) result[1]);
					sectorAggregate.setLandfillCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.METALS)) {
					sectorAggregate.setMetalEmission((BigDecimal) result[1]);
					sectorAggregate.setMetalCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.MINERALS)) {
					sectorAggregate.setMineralEmission((BigDecimal) result[1]);
					sectorAggregate.setMineralCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.REFINERIES)) {
					sectorAggregate.setRefineryEmission((BigDecimal) result[1]);
					sectorAggregate.setRefineryCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.PULPANDPAPER)) {
					sectorAggregate.setPulpAndPaperEmission((BigDecimal) result[1]);
					sectorAggregate.setPulpAndPaperCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.CHEMICALS)) {
					sectorAggregate.setChemicalEmission((BigDecimal) result[1]);
					sectorAggregate.setChemicalCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.OTHER)) {
					sectorAggregate.setOtherEmission((BigDecimal) result[1]);
					sectorAggregate.setOtherCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.PETRO_NG)) {
					sectorAggregate.setPetroleumAndNaturalGasEmission((BigDecimal) result[1]);
					sectorAggregate.setPetroleumAndNaturalGasCount((Long) result[2]);
				}
			}
			return sectorAggregate;
		} catch (RuntimeException re) {
			log.error("getSectorAggregate failed", re);
			throw re;
		}
	}
	
	@Override
	public SectorAggregate getEmitterSectorAggregate(String q, int year, String state, String countyFips, String msaCode, String basin, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String sectorType, String reportingStatus, String emissionsType, Long tribalLandId) {
		String st = state;
		// PUB - 520
		if (!msaCode.equals("") || ((sectorType.equals("L") || sectorType.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals(""))) {
			state = "";
		}
		
		String s = "select s.sectorCode, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = '" + msaCode + "' and f.id.year='" + year + "') and";
		}
		// PUB - 520
		if ((sectorType.equals("L") || sectorType.equals("E")) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
			s += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='" + st + "' and l.id.facilityId != '0' and l.id.year='" + year + "') and";
		}
		s += " f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.basinFilter(basin) +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.basinWhereClause(basin) +
				DaoUtils.tribalLandWhereClause(st, tribalLandId) +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				(("STOPPED_REPORTING".equals(reportingStatus)
						||
						"RED".equals(reportingStatus)
						||
						"GRAY".equals(reportingStatus)
				) ? ")" :
						"group by f.id having sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ") ") +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(st, tribalLandId) +
				"and (" +
				DaoUtils.gasFilter(gases) +
				"g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + year + " and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by s.sectorCode";
		final String hQuery = s;
		try {
			SectorAggregate sectorAggregate = new SectorAggregate();
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
			query.setCacheable(true);
			List<Object[]> results = query.list();
			// PUB - 520
			if ((sectorType.equals("L") || sectorType.equals("E")) && !st.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				
				state = st;
				final String dfQuery = "select s.sectorCode, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
						"from DimFacility f " +
						"join f.facStatus fs " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where " +
						" f.id NOT IN (select plf.facility.id from PubLdcFacility plf where plf.id.year= :year) and " +
						" f.id IN (select distinct f.id " +
						"from DimFacility f " +
						DaoUtils.emissionsTypeFilter(emissionsType) +
						"left join e.sector s " +
						"left join e.gas g " +
						DaoUtils.basinFilter(basin) +
						DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
						"where s.sectorType = 'E' " +
						DaoUtils.basinWhereClause(basin) +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
						DaoUtils.emitterWhereClause(q, year, state, countyFips,
								gases, sectors, qo) +
						"and (" +
						DaoUtils.gasFilter(gases) +
						"g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
						ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by s.sectorCode";
				
				Query queryDf = sessionFactory.getCurrentSession().createQuery(dfQuery).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
				queryDf.setCacheable(true);
				List<Object[]> dfResults = queryDf.list();
				
				if (results.size() > 0 && dfResults.size() > 0) {
					for (Object[] result : results) {
						String plfSectorCode = (String) result[0];
						for (Object[] dfResult : dfResults) {
							String dfSectorCode = (String) dfResult[0];
							if (plfSectorCode.equals(dfSectorCode)) {
								BigDecimal plfEmission = (BigDecimal) result[1];
								BigDecimal dfEmission = (BigDecimal) dfResult[1];
								BigDecimal totalEmission = plfEmission.add(dfEmission);
								result[1] = totalEmission;
								
								Long plfCount = (Long) result[2];
								Long dfCount = (Long) dfResult[2];
								plfCount = plfCount + dfCount;
								result[2] = plfCount;
							}
						}
					}
					for (Object[] dfResult : dfResults) {
						results.add(dfResult);
					}
				} else if (results.size() == 0 && dfResults.size() > 0) {
					results = dfResults;
				}
			}
			for (Object[] result : results) {
				if (result[0].equals(DaoUtils.POWERPLANTS)) {
					sectorAggregate.setPowerplantEmission((BigDecimal) result[1]);
					sectorAggregate.setPowerplantCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.WASTE)) {
					sectorAggregate.setLandfillEmission((BigDecimal) result[1]);
					sectorAggregate.setLandfillCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.METALS)) {
					sectorAggregate.setMetalEmission((BigDecimal) result[1]);
					sectorAggregate.setMetalCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.MINERALS)) {
					sectorAggregate.setMineralEmission((BigDecimal) result[1]);
					sectorAggregate.setMineralCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.REFINERIES)) {
					sectorAggregate.setRefineryEmission((BigDecimal) result[1]);
					sectorAggregate.setRefineryCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.PULPANDPAPER)) {
					sectorAggregate.setPulpAndPaperEmission((BigDecimal) result[1]);
					sectorAggregate.setPulpAndPaperCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.CHEMICALS)) {
					sectorAggregate.setChemicalEmission((BigDecimal) result[1]);
					sectorAggregate.setChemicalCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.OTHER)) {
					sectorAggregate.setOtherEmission((BigDecimal) result[1]);
					sectorAggregate.setOtherCount((Long) result[2]);
				} else if (result[0].equals(DaoUtils.PETRO_NG)) {
					sectorAggregate.setPetroleumAndNaturalGasEmission((BigDecimal) result[1]);
					sectorAggregate.setPetroleumAndNaturalGasCount((Long) result[2]);
				}
			}
			return sectorAggregate;
		} catch (RuntimeException re) {
			log.error("getSectorAggregate failed", re);
			throw re;
		}
	}
	
	public List<DimMsa> getMsas(String state) {
		try {
			final String hQuery = "from DimMsa d"
					+ " where d.cbsafp in "
					+ "(select distinct j.cbsafp from JctStateMsa j "
					+ "where state = :state) order by d.cbsa_title";
			
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("state", state);
			query.setCacheable(true);
			List<DimMsa> result = query.list();
			
			return result;
		} catch (RuntimeException re) {
			log.error("Find Metros failed", re);
			throw re;
		}
	}
}
