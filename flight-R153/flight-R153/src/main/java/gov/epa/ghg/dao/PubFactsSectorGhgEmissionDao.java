package gov.epa.ghg.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.GasQuantity;
import gov.epa.ghg.dto.NewSectorFilter;
import gov.epa.ghg.dto.PipeDetail;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.util.DaoUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;

/**
 * A data access object (DAO) providing persistence and search support for
 * DimFacility entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 *
 * @author MyEclipse Persistence Tools
 * @see gov.epa.ghg.domain.DimFacility
 */

@Repository
@Transactional
public class PubFactsSectorGhgEmissionDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "startYear")
	private int startYear;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public List<GasQuantity> getSubpartEmissions(Long id, int year, String ds) throws RuntimeException {
		StringBuffer sb = new StringBuffer();
		sb.append("select sp.subpartId, sp.subpartCategory, sp.subpartName, sum(e.co2eEmission) ");
		sb.append("from PubFactsSubpartGhgEmission e ");
		sb.append("join e.facility f ");
		sb.append("join e.subpart sp ");
		sb.append("join e.gas g ");
		sb.append("where f.id.facilityId = :id and f.id.year = :year and sp.subpartType = :ds and g.gasCode <> 'BIOCO2' group by sp.subpartId, sp.subpartCategory, sp.subpartName order by sp.subpartId");
		final String hQuery = sb.toString();
		List<GasQuantity> gqList = new ArrayList<GasQuantity>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("ds", ds);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		for (Object[] result : results) {
			GasQuantity gq = new GasQuantity();
			gq.setType((String) result[1]);
			gq.setSubpartName((String) result[2]);
			if ((BigDecimal) result[3] != null) {
				gq.setQuantity(((BigDecimal) result[3]).setScale(0, RoundingMode.HALF_UP).longValue());
			} else {
				gq.setQuantity(null);
			}
			gqList.add(gq);
		}
		return gqList;
	}
	
	public List<GasQuantity> getGasEmissions(FacilityDetail fd, Long id, int year, String ds, String emissionsType) throws RuntimeException {
		StringBuffer sb = new StringBuffer();
		sb.append("select g.gasId, g.gasCode, g.gasLabel, sum(e.co2eEmission) ");
		sb.append(DaoUtils.emissionsTypeFromClause(emissionsType));
		sb.append("join e.facility f ");
		sb.append("join e.sector s ");
		sb.append("join e.gas g ");
		sb.append("where f.id.facilityId = :id and f.id.year = :year and s.sectorType = :ds group by g.gasId, g.gasCode, g.gasLabel order by g.gasId");
		final String hQuery = sb.toString();
		final FacilityDetail fcdtl = fd;
		List<GasQuantity> gqList = new ArrayList<GasQuantity>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("ds", ds);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		Long totalEmission = 0L;
		for (Object[] result : results) {
			GasQuantity gq = new GasQuantity();
			gq.setType((String) result[2]);
			if ((BigDecimal) result[3] != null) {
				gq.setQuantity(((BigDecimal) result[3]).setScale(0, RoundingMode.HALF_UP).longValue());
			} else {
				gq.setQuantity(null);
			}
			gqList.add(gq);
			if (!"BIOCO2".equals((String) result[1])) {
				if (gq.getQuantity() != null) {
					totalEmission += gq.getQuantity();
				}
			}
		}
		fcdtl.setTotalEmissions(totalEmission);
		return gqList;
	}
	
	public List getListChartEmitterSectorAggregate(FlightRequest request) {
		
		String state = request.getState();
		String emissionsType = request.getEmissionsType();
		String q = request.getQuery();
		int year = request.getReportingYear();
		SectorFilter sectors = request.sectors();
		GasFilter gases = request.gases();
		String lowE = request.lowE();
		String highE = request.highE();
		QueryOptions qo = request.queryOptions();
		String countyFips = request.countyFips();
		String msaCode = request.msaCode();
		Long tribalLandId = request.getTribalLandId();
		String rs = request.getReportingStatus();
		if (state == null || state.compareTo("") == 0) {
			final String hQuery = "select f.stateName, s.sectorName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"left join e.facility f " +
					"join f.facStatus fs " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"left join e.facility f " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = " + year + " and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			query.setCacheable(true);
			return query.list();
		} else if ((countyFips == null || countyFips.compareTo("") == 0) && msaCode.equals("") && tribalLandId == null) {
			final String hQuery = "select f.countyFips, s.sectorName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					"from DimFacility f " +
					"join f.facStatus fs " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where f.id IN (select distinct f.id " +
					"from DimFacility f " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = " + year + " and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.countyFips, s.sectorName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			query.setCacheable(true);
			return query.list();
		} else {
			if (!msaCode.equals("")) {
				state = "";
			}
			String s = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId " +
					"from DimFacility f " +
					"join f.facStatus fs " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = '" + msaCode + "') and ";
			}
			s += "f.id IN (select distinct f.id " +
					"from DimFacility f " +
					DaoUtils.emissionsTypeFilter(emissionsType) +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = " + year + " and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, s.sectorName";
			final String hQuery = s;
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			query.setCacheable(true);
			return query.list();
		}
	}
	
	public List getTribalLandSectorAggregate(FlightRequest request) {
		String emissionsType = request.getEmissionsType();
		SectorFilter sectors = request.sectors();
		GasFilter gases = request.gases();
		QueryOptions qo = request.queryOptions();
		String state = request.getState();
		String q = request.getQuery();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		String rs = request.getReportingStatus();
		final String hQuery = "select tl.tribalLandId, s.sectorName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				"left join f.tribalLand tl " +
				DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
				"where f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
						gases, sectors, qo) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by tl.tribalLandId, s.sectorName";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getListChartLdcSectorAggregate(FlightRequest request) {
		String state = request.getState();
		SectorFilter sectors = request.sectors();
		GasFilter gases = request.gases();
		QueryOptions qo = request.queryOptions();
		String countyFips = request.countyFips();
		int year = request.getReportingYear();
		String rs = request.getReportingStatus();
		String q = request.getQuery();
		String lowE = request.lowE();
		String highE = request.highE();
		if (state == null || state.compareTo("") == 0) {
			final String hQuery = "select plf.state, s.sector_name, sum(e.co2e_emission), count(distinct e.facility_id) " +
					"from pub_ldc_facility_geo plf " +
					"left join pub_facts_sector_ghg_emission e on e.facility_id = plf.ccd_pub_id " +
					"left join pub_dim_facility f on e.facility_id = f.facility_id " +
					"join PUB_DIM_FACILITY_STATUS_MV fs on f.facility_id = fs.facility_id " +
					"left join pub_dim_sector s on e.sector_id = s.sector_id " +
					"left join pub_dim_ghg g on e.gas_id = g.gas_id " +
					DaoUtils.ldcSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where " +
					"plf.ccd_pub_id != '0' and plf.year = :year and plf.type='W' and e.year= :year and " +
					"e.facility_id in (select distinct f.facility_id " +
					"from pub_facts_sector_ghg_emission e " +
					"left join pub_dim_facility f on e.facility_id = f.facility_id " +
					"left join pub_dim_sector s on e.sector_id = s.sector_id " +
					"left join pub_dim_ghg g on e.gas_id = g.gas_id " +
					DaoUtils.ldcSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where (s.sector_type is null or s.sector_type = 'E') " +
					DaoUtils.ldcWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.facility_id having " +
					(lowE.equals("0") ? "sum(e.co2e_emission) is null or " : "") +
					"(sum(e.co2e_emission) >= :lowE and sum(e.co2e_emission) <= :highE)) " +
					DaoUtils.ldcWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"and (g.gas_code is null or g.gas_code <> 'BIOCO2') and (s.sector_type is null or s.sector_type = 'E') and fs.year = :year and fs.facility_type = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by plf.state, s.sector_name";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			// SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(hQuery);
			List<Object[]> results = query.list();
			for (Object[] obj : results) {
				String facilityState = DaoUtils.abbreviationToState((String) obj[0]);
				obj[0] = facilityState;
				BigDecimal count = (BigDecimal) obj[3];
				Long facilityCount = count.longValue();
				obj[3] = facilityCount;
			}
			final String dfQuery = "select f.stateName, s.sectorName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"join f.facStatus fs " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where " +
					"f.id NOT IN (select plf.facility.id from PubLdcFacility plf where plf.id.facilityId !='0' and plf.id.year= :year) and " +
					"f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
			Query queryDf = sessionFactory.getCurrentSession().createQuery(dfQuery).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			queryDf.setCacheable(true);
			List<Object[]> dfResults = queryDf.list();
			for (Object[] obj : dfResults) {
				for (Object[] rObj : results) {
					String state1 = (String) obj[0];
					String state2 = (String) rObj[0];
					if (state1.equals(state2)) {
						if (obj[2] != null && rObj[2] != null) {
							BigDecimal emission1 = (BigDecimal) obj[2];
							BigDecimal emission2 = (BigDecimal) rObj[2];
							BigDecimal sum = emission1.add(emission2);
							rObj[2] = sum;
							rObj[3] = (Long) rObj[3] + (Long) obj[3];
							break;
						}
					}
				}
			}
			return results;
		} else {
			String s = "from DimFacility f " +
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
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id";//.facilityId, f.facilityName, s.sectorName";
			String dfQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					"where f.id NOT IN " +
					"(select distinct plf.facility.id from PubLdcFacility plf where plf.id.year = :year) and f.id IN (select f.id " + s + ") " +
					"group by f.facilityName, s.sectorName, f.id.facilityId";
			Query queryDf = sessionFactory.getCurrentSession().createQuery(dfQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			queryDf.setCacheable(true);
			List<Object[]> dfResults = queryDf.list();
			String st = state;
			state = "";
			s = "from DimFacility f " +
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
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id";
			final String plfQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId from DimFacility f " +
					"left join f.emissions e " +
					"left join e.sector s " +
					"left join e.gas g " +
					"where f.id.facilityId in " +
					"(select distinct l.id.facilityId from PubLdcFacility l where l.id.state= :st and l.id.facilityId != '0' and l.id.year= :year) and f.id IN (select distinct f.id " + s + ") " +
					"group by f.id.facilityId, f.facilityName, s.sectorName";
			Query queryPlf = sessionFactory.getCurrentSession().createQuery(plfQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year)
					.setParameter("st", st);
			queryPlf.setCacheable(true);
			List<Object[]> plfResults = queryPlf.list();
			List<Object[]> results = new ArrayList();
			for (Object[] plf : plfResults) {
				if (!results.contains(plf)) {
					results.add(plf);
				}
			}
			for (Object[] df : dfResults) {
				results.add(df);
			}
			return results;
		}
	}
	
	public List getListChartEmitterGasAggregate(FlightRequest request) {
		String state = request.getState();
		SectorFilter sectors = request.sectors();
		String countyFips = request.countyFips();
		String q = request.getQuery();
		int year = request.getReportingYear();
		GasFilter gases = request.gases();
		QueryOptions qo = request.queryOptions();
		String lowE = request.lowE();
		String highE = request.highE();
		String rs = request.getReportingStatus();
		if (state == null || state.compareTo("") == 0) {
			final String hQuery = "select f.stateName, g.gasLabel, sum(e.co2eEmission) " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, g.gasLabel";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else if (countyFips == null || countyFips.compareTo("") == 0) {
			final String hQuery = "select f.countyFips, g.gasLabel, sum(e.co2eEmission) " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.countyFips, g.gasLabel";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else {
			final String hQuery = "select f.facilityName, g.gasLabel, sum(e.co2eEmission), f.id.facilityId " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, g.gasLabel";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		}
	}
	
	public List getTrendListChart(FlightRequest request) {
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		// CO2 INJECTION NOT TRENDY ENOUGH :)
		switch (type) {
			case EMITTERS:
				return this.getListChartEmitterFacilitiesTrend(request);
			case SUPPLIERS:
				return this.getListChartSupplierFacilitiesTrend(request);
			case ONSHORE:
				return this.getListChartBasinFacilities(request);
		}
		return null;
	}
	
	public List getListChartCO2InjectionFacilities(FlightRequest request) {
		String msaCode = request.msaCode();
		String state = request.getState();
		int year = request.getReportingYear();
		String searchTerm = request.getQuery();
		String countyFips = request.countyFips();
		GasFilter gases = request.gases();
		QueryOptions searchTermOptions = request.queryOptions();
		int injectionSelection = request.getInjectionSelection();
		String lowE = request.lowE();
		String highE = request.highE();
		String rs = request.getReportingStatus();
		String s = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state " +
				", f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				"left join f.emissions e " +
				"left join e.sector s " +
				"left join e.gas g " +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		s += "f.id IN (select distinct f.id " +
				"from DimFacility f " +
				"left join f.emissions e " +
				"left join e.sector s " +
				"left join e.gas g " +
				"where s.sectorType = 'I' " +
				DaoUtils.co2InjectionWhereClause(searchTerm, year, state, countyFips,
						gases, searchTermOptions, injectionSelection) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.co2InjectionWhereClause(searchTerm, year, state, countyFips,
						gases, searchTermOptions, injectionSelection) +
				"and g.gasCode <> 'BIOCO2' and (s.sectorType is null or s.sectorType = 'I') and fs.id.year = :year and fs.facilityType = 'I' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, s.sectorName, f.city, f.state" +
				", f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt";
		final String hQuery = s;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE))
					.setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE))
					.setParameter("highE", new BigDecimal(highE));
		}
		query.setCacheable(true);
		return query.list();
	}
	
	// list view
	public List getListChartEmitterFacilities(FlightRequest request) {
		String state = request.getState();
		String msaCode = request.msaCode();
		String countyFips = request.countyFips();
		String emissionsType = request.getEmissionsType();
		SectorFilter sectors = request.sectors();
		String q = request.getQuery();
		QueryOptions qo = request.queryOptions();
		GasFilter gases = request.gases();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		Long tribalLandId = request.getTribalLandId();
		String rs = request.getReportingStatus();
		if (!msaCode.equals("")) {
			state = "";
		}
		String vQry = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		// PUB - 520
		/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
			vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
		}*/
		vQry += "f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and (" +
				DaoUtils.gasFilter(gases) +
				"g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) +
				" group by f.id.facilityId, f.facilityName, s.sectorName, f.city, f.state, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"order by f.facilityName";
		final String hQuery = vQry;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE))
					.setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		List results = query.list();
		return results;
	}
	
	public List getListChartEmitterFacilitiesTrend(FlightRequest request) {
		String q = request.getQuery();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		String state = request.getState();
		String countyFips = request.countyFips();
		GasFilter gases = new GasFilter(request.getGases());
		SectorFilter sectors = new NewSectorFilter(request.getSectors());
		QueryOptions qo = new QueryOptions(request.getSearchOptions());
		String msaCode = request.msaCode();
		String emissionsType = request.getEmissionsType();
		
		if (!msaCode.equals("")) {
			state = "";
		}
		String vQry = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, e.id.year, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
		}
			/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("")) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.type='W') and ";
			}*/
		vQry += "f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				"and e.id.year >= :startYear group by f.id.facilityId, f.facilityName, f.city, f.state, s.sectorName, e.id.year, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"order by f.facilityName, e.id.year";
		final String hQuery = vQry;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
					.setParameter("startYear", (long) startYear);
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
					.setParameter("startYear", (long) startYear);
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		return results;
	}
	
	public List getListChartBasinFacilities(FlightRequest request) {
		
		String q = request.getQuery();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		String basinCode = request.getBasin();
		GasFilter gases = new GasFilter(request.getGases());
		SectorFilter sectors = new NewSectorFilter(request.getSectors());
		QueryOptions qo = new QueryOptions(request.getSearchOptions());
		String rs = request.getReportingStatus();
		boolean trend = request.isTrendRequest();
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		final String hQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state " + DaoUtils.shouldTrendBeIncluded(trend) +
				", f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				"left join f.emissions e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.basinFilter(basinCode) +
				DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
				"where f.id IN (select distinct f.id " +
				"from DimFacility f " +
				"left join f.emissions e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
				"where s.sectorType = 'E' " +
				DaoUtils.basinWhereClause(basinCode) +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.basinWhereClause(basinCode) +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				DaoUtils.trendFilter(trend, year, startYear) + " and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(trend, rs, year) + " group by f.id.facilityId, f.facilityName, s.sectorName, f.city, f.state"
				+ DaoUtils.shouldTrendBeIncluded(trend)
				+ ", f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt "
				+ DaoUtils.shouldTrendOrderByClauseBeIncluded(trend, type);
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		query.setCacheable(true);
		return query.list();
	}
	
	public List getListChartBasinFacilitiesGeo(FlightRequest request) {
		// unroll request parameters
		final String hQuery;
		String bs = request.getBasin();
		SectorFilter sectors = request.sectors();
		GasFilter gases = request.gases();
		QueryOptions qo = request.queryOptions();
		String q = request.getQuery();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		String rs = request.getReportingStatus();
		Query query;
		if ("".equals(bs)) {
			hQuery = "select b.id.basinCode, s.sectorName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"join f.facStatus fs " +
					"left join e.sector s " +
					"left join e.gas g " +
					"join f.basins b " +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where s.sectorType = 'E' " +
					DaoUtils.basinWhereClause(bs) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.basinWhereClause(bs) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by b.id.basinCode, s.sectorName";
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			hQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"join f.facStatus fs " +
					"left join e.sector s " +
					"left join e.gas g " +
					"join f.basins b " +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where f.id IN (select distinct f.id " +
					"from PubFactsSectorGhgEmission e " +
					"left join e.facility f " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
					"where s.sectorType = 'E' " +
					DaoUtils.basinWhereClause(bs) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					DaoUtils.basinWhereClause(bs) +
					DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, s.sectorName";
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		return query.list();
	}
	
	public List getListChartSuppliers(FlightRequest request) {
		String q = request.getQuery();
		QueryOptions qo = request.queryOptions();
		int year = request.getReportingYear();
		int sc = request.getSupplierSector();
		String rs = request.getReportingStatus();
		String state = request.getState();
		final String hQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, ss.subSectorDescription " + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year) +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"where s.sectorType = 'S' group by f.id) " +
				DaoUtils.supplierWhereClause(q, qo, year, sc, state) +
				"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.facilityName, s.sectorName, f.id.facilityId, ss.subSectorDescription" +
				ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getListChartSupplierFacilitiesTrend(FlightRequest request) {
		int year = request.getReportingYear();
		String queryString = request.getQuery();
		QueryOptions queryOptions = new QueryOptions(request.getSearchOptions());
		int supplierSector = request.getSupplierSector();
		String state = request.getState();
		final String hQuery = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, e.id.year, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt, ss.subSectorDescription " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"where s.sectorType = 'S' group by f.id) " +
				DaoUtils.supplierWhereClause(queryString, queryOptions, year, supplierSector, state) +
				"and s.sectorType = 'S' and e.id.year >= " + startYear + " group by f.facilityName, s.sectorName, f.id.facilityId, f.city, f.state, e.id.year, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt, ss.subSectorDescription order by f.id.facilityId, e.id.year";
		Query dbQuery = sessionFactory.getCurrentSession().createQuery(hQuery);
		dbQuery.setCacheable(true);
		return dbQuery.list();
		
	}
	
	public List getSectorTrend(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		final String hQuery = "select s.sectorName, s.sectorColor, e.id.year, sum(e.co2eEmission), count(distinct f.id.facilityId) " + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year) +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"and s.sectorCode NOT IN ('PETRO_NG','CHEMICALS','WASTE','METALS','OTHER') " +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"and s.sectorCode NOT IN ('PETRO_NG','CHEMICALS','WASTE','METALS','OTHER') " +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' group by s.sectorName, s.sectorColor, e.id.year " + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		query.setCacheable(true);
		return query.list();
	}
	
	public List getSectorYearlyTrend(String q, int year, String lowE, String highE, String state, String countyFips, String msaCode, String basinCode, String dataType,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(dataType) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipeTrend(q, year, lowE, highE, state, countyFips, gases, sectors, qo, tribalLandId);
		} else {
			vQry = "select s.sectorName, s.sectorColor, e.id.year, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.basinFilter(basinCode) +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
			}
			// PUB-520
			/*if ((dataType.equals("L") || dataType.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					"and e.id.year >= :startYear " +
					DaoUtils.basinWhereClause(basinCode) +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' group by s.sectorName, s.sectorColor, e.id.year ";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(dataType) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("startYear", (long) startYear).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("startYear", (long) startYear).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("startYear", (long) startYear).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getBarGeo(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (state == null || state.compareTo("") == 0) {
			vQry = "select f.stateName, s.sectorName, sum(e.co2eEmission) " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterAllWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
		} else if (state.equals("TL") && tribalLandId == null) {
			vQry = "select f.tribalLand.tribalLandId, s.sectorName, sum(e.co2eEmission) " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where f.id IN (select distinct f.id " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, state, DaoUtils.forceExclude,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.tribalLand.tribalLandId, s.sectorName";
		} else if (state.equals("TL") && tribalLandId != null) {
			vQry = "select t.tribalLandName, s.sectorName, sum(e.co2eEmission) " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join f.tribalLand t " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					"join f.tribalLand t " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by t.tribalLandName, s.sectorName";
		} else if ((countyFips == null || countyFips.compareTo("") == 0) && msaCode.equals("") && tribalLandId == null) {
			vQry = "select f.countyFips, s.sectorName, sum(e.co2eEmission) " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where f.id IN (select distinct f.id " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, state, DaoUtils.forceExclude,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.countyFips, s.sectorName";
		} else {
			if (!msaCode.equals("")) {
				state = "";
			}
			vQry = "select f.stateName, s.sectorName, sum(e.co2eEmission) " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
			}
			vQry += "f.id IN (select distinct f.id " +
					"from FacilityAllSectorEmission e " +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
		}
		final String hQuery = vQry;
		Query query;
		if (state == null || state.compareTo("") == 0) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else if (state.equals("TL") && tribalLandId == null) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else if (state.equals("TL") && tribalLandId != null) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else if ((countyFips == null || countyFips.compareTo("") == 0) && msaCode.equals("") && tribalLandId == null) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
						.setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
						.setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		return query.list();
	}
	
	public List getBarSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipeBar(1, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select s.sectorName, ss.subSectorDescription, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by s.sectorName, ss.subSectorDescription" + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getBarSectorLevel2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipeBar(2, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select f.facilityName, ss.subSectorDescription, sum(e.co2eEmission), f.id.facilityId " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.facilityName, f.id.facilityId, ss.subSectorDescription";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getBarSectorLevel3(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipeBar(3, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select f.stateName, ss.subSectorDescription, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode+ and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") || ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, ss.subSectorDescription";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getBarPieTreeGas(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String vQry = "select g.gasLabel, sum(e.co2eEmission) " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		vQry += "f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by g.gasLabel";
		final String hQuery = vQry;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		return query.list();
	}
	
	public List getBarState(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		if (state == null || state.compareTo("") == 0) {
			final String hQuery = "select f.stateName, s.sectorName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else if (state.equals("TL") && tribalLandId == null) {
			final String hQuery = "select f.tribalLand.tribalLandId, s.sectorName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.tribalLand.tribalLandId, s.sectorName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else if (state.equals("TL") && tribalLandId != null) {
			String s = "select t.tribalLandName, s.sectorName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join f.tribalLand t " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					"join f.tribalLand t " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by t.tribalLandName, s.sectorName";
			final String hQuery = s;
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else if ((countyFips == null || countyFips.compareTo("") == 0) && msaCode.equals("") && tribalLandId == null) {
			final String hQuery = "select f.countyFips, s.sectorName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
							gases, sectors, qo) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, DaoUtils.forceExclude) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.countyFips, s.sectorName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else {
			if (!msaCode.equals("")) {
				state = "";
			}
			
			String s = "select f.stateName, s.sectorName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
			}
			s += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, s.sectorName";
			final String hQuery = s;
			Query query;
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			return query.list();
		}
	}
	
	public List getBarStateLevel2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String s = "select ss.subSectorDescription, sum(e.co2eEmission), e.id.sectorId " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				"join e.subSector ss " +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
		}
		s += "f.id IN (select distinct f.id " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by ss.subSectorDescription, e.id.sectorId";
		final String hQuery = s;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		return query.list();
		
	}
	
	public List getBarStateLevel3(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		if (ss != null) {
			String s = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId, e.id.sectorId " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
			}
			s += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and ss.subSectorDescription = :ss and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.facilityName, f.id.facilityId, e.id.sectorId";
			final String hQuery = s;
			Query query;
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss)
						.setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			return query.list();
		} else {
			String s = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year) +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
			}
			s += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterSectorFilter(sectors, state, countyFips) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and ss.subSectorDescription = :ss and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.facilityName, f.id.facilityId" + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
			final String hQuery = s;
			Query query;
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
						.setParameter("ss", ss).setParameter("year", (long) year);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss).setParameter("year", (long) year);
			}
			query.setCacheable(true);
			return query.list();
		}
	}
	
	public List getPieTreeSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipePie(1, null, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select s.sectorName, sum(e.co2eEmission), s.sectorColor " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") ||ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by s.sectorName, s.sectorColor";
		}
		
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getPieTreeSectorLevel2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipePie(2, null, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select ss.subSectorDescription, sum(e.co2eEmission), e.id.sectorId " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			/*if ((ds.equals("L") ||ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by ss.subSectorDescription, e.id.sectorId";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getPieTreeSectorLevel3(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipePie(3, ss, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId, e.id.sectorId " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") ||ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, state, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and ss.subSectorDescription = :ss and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, e.id.sectorId";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss).setParameter("year", (long) year);
			}
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getPieTreeSectorLevel4(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		String vQry = "";
		if (!msaCode.equals("")) {
			state = "";
		}
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			vQry = this.getPipePie(4, ss, q, year, lowE, highE, state, countyFips, gases, sectors, qo, rs, tribalLandId);
		} else {
			vQry = "select f.stateName, sum(e.co2eEmission) " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join f.facStatus fs " +
					"join e.sector s " +
					"join e.gas g " +
					"join e.subSector ss " +
					"where ";
			if (!msaCode.equals("")) {
				vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
			}
			// PUB-520
			/*if ((ds.equals("L") ||ds.equals("E")) && !state.equals("") && msaCode.equals("") && countyFips.equals("") && tribalLandId == null) {
				vQry += "f.id IN (select l.facility.id from PubLdcFacility l where l.id.state='"+state+"' and l.id.facilityId != '0' and l.id.year='"+year+"') and ";
			}*/
			vQry += "f.id IN (select distinct f.id " +
					DaoUtils.emissionsTypeFromClause(emissionsType) +
					"join e.facility f " +
					"join e.sector s " +
					"join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where s.sectorType = 'E' " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
					DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					DaoUtils.tribalLandWhereClause(state, tribalLandId) +
					"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and ss.subSectorDescription = :ss and fs.id.year = :year and fs.facilityType = 'E' " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName";
		}
		final String hQuery = vQry;
		Query query;
		if ("T".equals(ds) || sectors.isPipeSectorOnly()) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		} else {
			if (!msaCode.equals("")) {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE))
						.setParameter("ss", ss);
			} else {
				query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("ss", ss).setParameter("year", (long) year);
			}
//			query = sessionFactory.getCurrentSession().createQuery(hQuery);
		}
//		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getPieTreeState(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		final String hQuery = "select f.stateName, sum(e.co2eEmission), f.state " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, DaoUtils.forceExclude, countyFips,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName, f.state";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getPieTreeStateLevel2(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		final String hQuery = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getPieTreeStateLevel3(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		final String hQuery = "select f.countyFips, sum(e.co2eEmission) " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, DaoUtils.forceExclude) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, state, DaoUtils.forceExclude,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.countyFips";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getBarBasin(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		final String hQuery = "select l.basin, sum(e.co2eEmission), l.basinCode " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				"join e.subSector ss " +
				"join f.basins b " +
				"join b.layer l " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by l.basin, l.basinCode";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getBarBasinL2(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		final String hQuery = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				"join f.basins b " +
				DaoUtils.emitterSubSectorFilter(sectors, StringUtils.EMPTY, StringUtils.EMPTY) +
				"where s.sectorType = 'E' " +
				"and b.id.basinCode = :basin " +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.emitterWhereClause(q, year, StringUtils.EMPTY, StringUtils.EMPTY,
						gases, sectors, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = :year and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("basin", basin).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getSupplierBarChart(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		final String hQuery = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"where s.sectorType = 'S' group by f.id) " +
				DaoUtils.supplierWhereClause(q, qo, year, sc, state) +
				"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getSupplierPieChart(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		final String hQuery = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"where s.sectorType = 'S' group by f.id) " +
				DaoUtils.supplierWhereClause(q, qo, year, sc, state) +
				"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getSupplierTreeChart(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		final String hQuery = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from PubFactsSectorGhgEmission e " +
				"join e.facility f " +
				"join e.sector s " +
				"where s.sectorType = 'S' group by f.id) " +
				DaoUtils.supplierWhereClause(q, qo, year, sc, state) +
				"and s.sectorType = 'S' and fs.id.year = :year and fs.facilityType = 'S' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		return query.list();
	}
	
	public List getFacilityTrend(Long id, String ds, String emissionsType) {
		
		String dataType = "E";
		if ("S".equals(ds)) {
			dataType = "S";
		} else if ("I".equals(ds)) {
			dataType = "I";
		} else if ("A".equals(ds)) {
			dataType = "A";
		}
		
		final String hQuery = "select e.id.year, sum(e.co2eEmission) " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				"where f.id.facilityId = :id and s.sectorType = :dataType " +
				"and g.gasCode <> 'BIOCO2' group by e.id.year having sum(e.co2eEmission) is not null";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("dataType", dataType);
		query.setCacheable(true);
		return query.list();
	}
	
	// Geologic Sequestration of CO2 datatype (Subpart RR)
	public List getListRRCO2Facilities(FlightRequest request) {
		
		String state = request.getState();
		String msaCode = request.msaCode();
		String countyFips = request.countyFips();
		String emissionsType = request.getEmissionsType();
		String q = request.getQuery();
		QueryOptions qo = request.queryOptions();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		String rs = request.getReportingStatus();
		
		String s = "select f.facilityName, s.sectorName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		s += "f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				"where s.sectorType = 'I' " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				"and g.gasCode <> 'BIOCO2' and (s.sectorType is null or s.sectorType = 'I') and fs.id.year = :year and fs.facilityType = 'A' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by f.id.facilityId, f.facilityName, s.sectorName, f.city, f.state, " +
				"f.comments, f.co2Captured, f.co2EmittedSupplied, f.uuRandDExempt " +
				"order by f.facilityName";
		final String hQuery = s;
		Query dbQuery;
		if (!msaCode.equals("")) {
			dbQuery = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			dbQuery = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		dbQuery.setCacheable(true);
		List results = dbQuery.list();
		
		return results;
	}
	
	public List getTrendRR(String q, int year, String lowE, String highE, String state, String countyFips, String msaCode, String basinCode, String dataType,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String s = "select s.sectorName, s.sectorColor, e.id.year, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode) and ";
		}
		s += "f.id IN (select distinct f.id " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				"where s.sectorType = 'I' " +
				"and e.id.year >= 2016 " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'I' group by s.sectorName, s.sectorColor, e.id.year ";
		
		final String hQuery = s;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getBarRR(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String s = "select s.sectorName, ss.subSectorDescription, sum(e.co2eEmission) " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				"join e.subSector ss " +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		s += "f.id IN (select distinct f.id " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'I' " +
				"and e.id.year >= 2016 " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'I' and fs.id.year = :year and fs.facilityType = 'A' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by s.sectorName, ss.subSectorDescription" + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
		
		final String hQuery = s;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public List getPieRR(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		if (!msaCode.equals("")) {
			state = "";
		}
		
		String s = "select s.sectorName, sum(e.co2eEmission), '#FFBD59' as sectorColor " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join f.facStatus fs " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			s += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year= :year) and ";
		}
		s += "f.id IN (select distinct f.id " +
				DaoUtils.emissionsTypeFromClause(emissionsType) +
				"join e.facility f " +
				"join e.sector s " +
				"join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'I' " +
				"and e.id.year >= 2016 " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				DaoUtils.rrCo2WhereClause(q, year, state, countyFips, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and g.gasCode <> 'BIOCO2' and s.sectorType = 'I' and fs.id.year = :year and fs.facilityType = 'A' " +
				ReportingStatusQueryFilter.filter(rs, year) + " group by s.sectorName, s.sectorColor";
		
		final String hQuery = s;
		Query query;
		if (!msaCode.equals("")) {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode).setParameter("year", (long) year).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE));
		} else {
			query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
		}
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
	public String qryPipeList(FlightRequest request) {
		
		String state = request.getState();
		String countyFips = request.countyFips();
		SectorFilter sectors = request.sectors();
		String q = request.getQuery();
		QueryOptions qo = request.queryOptions();
		GasFilter gases = request.gases();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		Long tribalLandId = request.getTribalLandId();
		String rs = request.getReportingStatus();
		
		String vQry = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, f.zip " +
				"from DimFacilityPipe f " +
				"join f.facStatus fs " +
				"left join f.emPipe e " +
				"where f.id IN (select distinct f.id " +
				"from DimFacilityPipe f " +
				"left join f.emPipe e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) " +
				"and fs.id.year = " + year + " and fs.facilityType = 'E' " +
				"AND f.stateName = e.state " +
				ReportingStatusQueryFilter.filter(rs, year) +
				"group by f.id.facilityId, f.facilityName, f.city, f.state, f.zip " +
				"order by UPPER(f.facilityName)";
		
		return vQry;
		
	}
	
	public List getListPipeFacilities(FlightRequest request) {
		
		final String hQuery = this.qryPipeList(request);
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		List results = query.list();
		return results;
		
	}
	
	public List getListPipeGeography(FlightRequest request) {
		
		String state = request.getState();
		String q = request.getQuery();
		int year = request.getReportingYear();
		SectorFilter sectors = request.sectors();
		GasFilter gases = request.gases();
		String lowE = request.lowE();
		String highE = request.highE();
		QueryOptions qo = request.queryOptions();
		String countyFips = request.countyFips();
		String rs = request.getReportingStatus();
		
		if (state == null || state.compareTo("") == 0) {
			final String hQuery = "select f.stateName, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
					"from DimFacilityPipe f " +
					"join f.facStatus fs " +
					"left join f.emPipe e " +
					"where f.id IN (select distinct f.id " +
					"from DimFacilityPipe f " +
					"left join f.emPipe e " +
					"left join e.sector s " +
					"left join e.gas g " +
					DaoUtils.emitterSubSectorFilter(sectors, DaoUtils.forceExclude, countyFips) +
					"where g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
					DaoUtils.emitterAllWhereClause(q, year, DaoUtils.forceExclude, countyFips,
							gases, sectors, qo) +
					"group by f.id having " +
					DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
					"(sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE)) " +
					"and fs.id.year = :year and fs.facilityType = 'E' " +
					"AND f.stateName = e.state " +
					ReportingStatusQueryFilter.filter(rs, year) + " group by f.stateName";
			
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("lowE", new BigDecimal(lowE)).setParameter("highE", new BigDecimal(highE)).setParameter("year", (long) year);
			query.setCacheable(true);
			return query.list();
		} else {
			
			return this.getListPipeFacilities(request);
			
		}
	}
	
	public List<GasQuantity> getPipeGasQuantity(PipeDetail pd, Long id, int year, String ds, String emissionsType) throws RuntimeException {
		StringBuffer sb = new StringBuffer();
		sb.append("select g.gasId, g.gasCode, g.gasLabel, sum(e.co2eEmission) ");
		sb.append("from DimFacilityPipe f ");
		sb.append("join f.emPipe e ");
		sb.append("join e.sector s ");
		sb.append("join e.gas g ");
		sb.append("where f.id.facilityId = :id and f.id.year = :year and s.sectorType = :ds AND f.stateName = e.state ");
		sb.append("group by g.gasId, g.gasCode, g.gasLabel order by g.gasId");
		final String hQuery = sb.toString();
		final PipeDetail pipeDetail = pd;
		List<GasQuantity> gqList = new ArrayList<GasQuantity>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("ds", ds);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		Long totalEmission = 0L;
		for (Object[] result : results) {
			GasQuantity gq = new GasQuantity();
			gq.setType((String) result[2]);
			if ((BigDecimal) result[3] != null) {
				gq.setQuantity(((BigDecimal) result[3]).setScale(0, RoundingMode.HALF_UP).longValue());
			} else {
				gq.setQuantity(null);
			}
			gqList.add(gq);
			if (!"BIOCO2".equals((String) result[1])) {
				if (gq.getQuantity() != null) {
					totalEmission += gq.getQuantity();
				}
			}
		}
		pipeDetail.setTotalEmissions(totalEmission);
		return gqList;
	}
	
	public String getPipeTrend(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, Long tribalLandId) {
		String vQry = "select s.sectorName, s.sectorColor, e.id.year, sum(e.co2eEmission), count(distinct f.id.facilityId) " +
				"from DimFacilityPipe f " +
				"left join f.emPipe e " +
				"where f.id IN (select distinct f.id " +
				"from DimFacilityPipe f " +
				"left join f.emPipe e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				"and e.id.year >= :startYear " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				"AND f.stateName = e.state group by s.sectorName, s.sectorColor, e.id.year ";
		
		return vQry;
	}
	
	public String getPipeBar(int level, String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, Long tribalLandId) {
		String vQry = "";
		String vMainQry = "from DimFacilityPipe f " +
				"join f.facStatus fs " +
				"left join f.emPipe e " +
				"left join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from DimFacilityPipe f " +
				"left join f.emPipe e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				"and fs.id.year = :year and fs.facilityType = 'E' " +
				"AND f.stateName = e.state " +
				ReportingStatusQueryFilter.filter(rs, year);
		if (level == 1) {
			vQry = "select s.sectorName, ss.subSectorDescription, sum(e.co2eEmission) " +
					vMainQry +
					"group by s.sectorName, ss.subSectorDescription" + ReportingStatusQueryFilter.shouldReportingStatusBeIncluded(year);
		} else if (level == 2) {
			vQry = "select f.facilityName, ss.subSectorDescription, sum(e.co2eEmission), f.id.facilityId " +
					vMainQry +
					"group by f.facilityName, f.id.facilityId, ss.subSectorDescription";
		} else if (level == 3) {
			vQry = "select f.stateName, ss.subSectorDescription, sum(e.co2eEmission) " +
					vMainQry +
					"group by f.stateName, ss.subSectorDescription";
		}
		
		return vQry;
	}
	
	public String getPipePie(int level, String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, Long tribalLandId) {
		String vQry = "";
		String vMainQry = "from DimFacilityPipe f " +
				"join f.facStatus fs " +
				"left join f.emPipe e " +
				"left join e.sector s " +
				"left join e.gas g " +
				"left join e.subSector ss " +
				"where f.id IN (select distinct f.id " +
				"from DimFacilityPipe f " +
				"left join f.emPipe e " +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where g.gasCode <> 'BIOCO2' and s.sectorType = 'E' " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having sum(e.co2eEmission) >= :lowE and sum(e.co2eEmission) <= :highE) " +
				"and fs.id.year = :year and fs.facilityType = 'E' " +
				"AND f.stateName = e.state " +
				ReportingStatusQueryFilter.filter(rs, year);
		if (level == 1) {
			vQry = "select s.sectorName, sum(e.co2eEmission), s.sectorColor " +
					vMainQry +
					"group by s.sectorName, s.sectorColor";
		} else if (level == 2) {
			vQry = "select ss.subSectorDescription, sum(e.co2eEmission), e.id.sectorId " +
					vMainQry +
					"group by ss.subSectorDescription, e.id.sectorId";
		} else if (level == 3) {
			vQry = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId, e.id.sectorId " +
					vMainQry +
					"group by f.id.facilityId, f.facilityName, e.id.sectorId";
		} else if (level == 4) {
			vQry = "select f.stateName, sum(e.co2eEmission) " +
					vMainQry +
					"group by f.stateName";
		}
		
		return vQry;
	}
	
	public String qryEmitterList(FlightRequest request) {
		
		String state = request.getState();
		String msaCode = request.msaCode();
		String countyFips = request.countyFips();
		String emissionsType = request.getEmissionsType();
		SectorFilter sectors = request.sectors();
		String q = request.getQuery();
		QueryOptions qo = request.queryOptions();
		GasFilter gases = request.gases();
		int year = request.getReportingYear();
		String lowE = request.lowE();
		String highE = request.highE();
		Long tribalLandId = request.getTribalLandId();
		String rs = request.getReportingStatus();
		
		String vQry = "select f.facilityName, sum(e.co2eEmission), f.id.facilityId, f.city, f.state, f.zip " +
				"from DimFacility f " +
				"join f.facStatus fs " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where ";
		if (!msaCode.equals("")) {
			vQry += "f.id IN (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = '" + msaCode + "' and f.id.year='" + year + "') and ";
		}
		vQry += "f.id IN (select distinct f.id " +
				"from DimFacility f " +
				DaoUtils.emissionsTypeFilter(emissionsType) +
				"left join e.sector s " +
				"left join e.gas g " +
				DaoUtils.emitterSubSectorFilter(sectors, state, countyFips) +
				"where s.sectorType = 'E' " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"group by f.id having " +
				DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
				"(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) " +
				DaoUtils.emitterAllWhereClause(q, year, state, countyFips,
						gases, sectors, qo) +
				DaoUtils.tribalLandWhereClause(state, tribalLandId) +
				"and (" +
				DaoUtils.gasFilter(gases) +
				"g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + year + " and fs.facilityType = 'E' " +
				ReportingStatusQueryFilter.filter(rs, year) +
				"group by f.id.facilityId, f.facilityName, f.city, f.state, f.zip " +
				"order by UPPER(f.facilityName)";
		
		return vQry;
		
	}
	
	public List getListPetroNgFacilities(FlightRequest request) {
		
		final String hQuery = this.qryEmitterList(request);
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		List results = query.list();
		
		return ListUtils.union(results, this.getListPipeFacilities(request));
		
	}
}
