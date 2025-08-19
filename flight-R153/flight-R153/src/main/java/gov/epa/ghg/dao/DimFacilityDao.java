package gov.epa.ghg.dao;

import static gov.epa.ghg.enums.FacilityViewType.MAP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.criteria.CriteriaBuilder;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.stat.Statistics;
import org.hibernate.type.IntegerType;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.PubBasinFacility;
import gov.epa.ghg.domain.PubLdcFacility;
import gov.epa.ghg.domain.PubSf6Territory;
import gov.epa.ghg.dto.ExportAllData;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.dto.GasQuantity;
import gov.epa.ghg.dto.PipeHoverTip;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.FacilityViewType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.util.DaoUtils;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;

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

@Log4j2
@Repository
@Transactional
public class DimFacilityDao extends AbstractFlightDao implements DimFacilityDaoInterface {
	
	private static final long serialVersionUID = 1L;
	
	// property constants
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String COUNTY_FIPS = "countyFips";
	public static final String COUNTY = "county";
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String FACILITY_NAME = "facilityName";
	
	public void save(DimFacility transientInstance) {
		log.debug("saving DimFacility instance");
		try {
			sessionFactory.getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	
	public void delete(DimFacility persistentInstance) {
		log.debug("deleting DimFacility instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public DimFacility findById2(Long id) {
		log.debug("getting DimFacility instance with id: " + id);
		try {
			DimFacility instance = (DimFacility) sessionFactory.getCurrentSession().get("gov.epa.ghg.domain.DimFacility", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public DimFacility findByFacilityIdAndReportingYear(Long id, int year) {
		try {
			final String queryString = "from DimFacility f where f.id.facilityId = :id and f.id.year = :year";
			Query query = createQuery(queryString).setParameter("id", id).setParameter("year", (long) year);
			List<DimFacility> results = query.list();
			return !results.isEmpty() ? results.get(0) : new DimFacility();
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<PubBasinFacility> getFacBasin(Long id, Long year) {
		try {
			//final String queryString = "from PubSf6Territory t where id.facilityId = :id";
			//Query query = createQuery(queryString).setParameter("id", id);
			//List<PubSf6Territory> results = query.list();
			final String queryString = String.format("Select * from  PUB_BASIN_FACILITY t where t.FACILITY_ID = %s and t.year = %s", id, year);
			@SuppressWarnings("unchecked")
			NativeQuery<PubBasinFacility> sqlQry = sessionFactory.getCurrentSession().createNativeQuery(queryString).addEntity(PubBasinFacility.class);
			//sqlQry.setParameter(1, id);
			//Query query = createQuery(queryString);
			List<PubBasinFacility> results = sqlQry.list();
			return results;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<PubSf6Territory> getFacTerritories(Long id, Long year) {
		try {
			//final String queryString = "from PubSf6Territory t where id.facilityId = :id";
			//Query query = createQuery(queryString).setParameter("id", id);
			//List<PubSf6Territory> results = query.list();
			final String queryString = String.format("Select * from  PUB_SERV_TERRITORIES_GEO t where t.PUB_ID = %s and t.year = %s", id, year);
			@SuppressWarnings("unchecked")
			NativeQuery<PubSf6Territory> sqlQry = sessionFactory.getCurrentSession().createNativeQuery(queryString).addEntity(PubSf6Territory.class);
			//sqlQry.setParameter(1, id);
			//Query query = createQuery(queryString);
			List<PubSf6Territory> results = sqlQry.list();
			return results;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<PubLdcFacility> getFacLayers(Long id, Long year) {
		try {
			//final String queryString = "from PubSf6Territory t where id.facilityId = :id";
			//Query query = createQuery(queryString).setParameter("id", id);
			//List<PubSf6Territory> results = query.list();
			final String queryString = String.format("Select * from  PUB_LDC_FACILITY_GEO t where t.CCD_PUB_ID = %s and t.year=%s", id, year);
			@SuppressWarnings("unchecked")
			NativeQuery<PubLdcFacility> sqlQry = sessionFactory.getCurrentSession().createNativeQuery(queryString).addEntity(PubLdcFacility.class);
			//sqlQry.setParameter(1, id);
			//Query query = createQuery(queryString);
			List<PubLdcFacility> results = sqlQry.list();
			return results;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<DimFacility> getDimFacilitiesInMsa(FlightRequest request, FacilityViewType viewType, boolean isLimited) {
		String selectionQuery = request.generateQuery(viewType, false);
		String msaCode = request.msaCode();
		int year = request.getReportingYear();
		String queryString = "from DimFacility f where f.id IN "
				+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = :msaCode and f.id.year = :year)" + " and f.id IN (select f.id " + selectionQuery + ")";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("msaCode", msaCode).setParameter("year", (long) year);
		if (isLimited) {
			query.setFirstResult(request.getPageNumber() * 100);
			query.setMaxResults(100);
		}
		return query.list();
	}
	
	public List<DimFacilityId> getDimFacilityIds(String queryString) {
		queryString = "select f.id " + queryString;
		Query query = createQuery(queryString);
		return query.list();
		
	}
	
	public Map<Long, BigDecimal> getFacilityIdAndEmissions(String queryString, FacilityViewType viewType) {
		queryString = "select f.id, sum(e.co2eEmission) " + queryString;
		Query query = createQuery(queryString);
		List<Object[]> results = query.list();
		Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
		for (Object[] result : results) {
			Long facId = (Long) ((DimFacilityId) result[0]).getFacilityId();
			BigDecimal emission = (BigDecimal) result[1];
			em.put(facId, emission);
		}
		return em;
	}
	
	@SuppressWarnings("unchecked")
	public ExportAllData getFacilityIdAndEmissionsSql(String queryString, FacilityViewType viewType, FlightRequest request) throws Exception {
		NativeQuery<Object[]> sqlQry = sessionFactory.getCurrentSession().createNativeQuery(queryString);
		sqlQry.setParameter(1, request.getReportingYear());
		//Query query = createQuery(queryString);
		List<Object[]> results = sqlQry.list();
		Map<Long, BigDecimal> em = new HashMap<Long, BigDecimal>();
		Map<Long, Long> repYears = new HashMap<Long, Long>();
		BigDecimal emission = null;
		for (Object[] result : results) {
			emission = (BigDecimal) result[0];
			Long facId = ((BigDecimal) result[1]).longValue();
			em.put(facId, emission);
			repYears.put(facId, ((BigDecimal) result[2]).longValue());
		}
		ExportAllData exportAllData = new ExportAllData();
		exportAllData.setEm(em);
		exportAllData.setRepYears(repYears);
		return exportAllData;
	}
	
	/**
	 * I presume this is only used in left-hand list to determine the total number of pages needed
	 * should change is desired in the future, a FacilityViewType parameter needs to be added, right now it's being
	 * created as a locl variable with value = LIST inside this method
	 *
	 * @param request : UI request object
	 *
	 * @return : total facilities count
	 */
	public int getTotalCount(FlightRequest request) {
		String queryString = "select count(*) ";
		FacilityViewType viewType = FacilityViewType.LIST;
		FacilityType facilityType = request.retrieveFacilityType();
		String msaCode = request.msaCode();
		SectorFilter sectors = request.sectors();
		String basinSql = "";
		switch (facilityType) {
			case EMITTERS:
				if (request.shouldLdcBeIncludedInEmitterResults()) {
					/*queryString += request.generateLDCQuery();
					int firstCount = DataAccessUtils.intResult(createQuery(queryString).list());*/
					String selectionQuery = request.generateQuery(viewType, false);
					String secondQuerystring = "select count(*) from DimFacility f where f.id IN"
							+ " (select f.id " + selectionQuery + ")";
					int secondCount = DataAccessUtils.intResult(createQuery(secondQuerystring).list());
					return /*firstCount + */secondCount;
					
				}
				if (!msaCode.equals("")) {
					String selectionQuery = request.generateQuery(viewType, false);
					queryString += "from DimFacility f where f.id IN "
							+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and"
							+ " m.cbsafp = '" + request.msaCode() + "' and f.id.year = '" + request.getReportingYear() + "')" + " and f.id IN (select f.id " + selectionQuery + ")";
					break;
				}
				if (request.isBoosting()) {
					if (!StringUtils.EMPTY.equals(request.getBasin()) && "GRAY".equals(request.getReportingStatus())) {
						basinSql += queryString + "as RECORD_COUNT_ from (" + request.sqlOnshoreBasin(viewType) + ")";
						break;
					} else {
						queryString += "from DimFacility f where f.id IN (select f.id " + request.generateOnshoreQuery(viewType, false) + " )";
						break;
					}
				}
				if (request.isPipe()) {
					queryString += "from DimFacility f where f.id IN (select f.id " + request.generatePipeQuery(viewType) + " )";
					break;
				}
				break;
			case ONSHORE:
				if (sectors.isOnshorePetroleumSectorOnly()) {
					if (!StringUtils.EMPTY.equals(request.getBasin()) && "GRAY".equals(request.getReportingStatus())) {
						basinSql += queryString + "as RECORD_COUNT_ from (" + request.sqlOnshoreBasin(viewType) + ")";
						break;
					} else {
						queryString += "from DimFacility f where f.id IN (select f.id " + request.generateOnshoreQuery(viewType, false) + " )";
						break;
					}
				}
				break;
			case SUPPLIERS:
				if (request.getSupplierSector() == 0) {
					return 0;
				}
				break;
			case CO2_INJECTION:
				if (!msaCode.equals("")) {
					String selectionQuery = request.generateQuery(viewType, false);
					queryString += "from DimFacility f where f.id IN "
							+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and"
							+ " m.cbsafp = '" + request.msaCode() + "' and f.id.year = '" + request.getReportingYear() + "')" + " and f.id IN (select f.id " + selectionQuery + ")";
					break;
				}
				break;
			default:
				break;
		}
		// if unchanged
		if ("select count(*) ".equals(queryString)) {
			queryString += "from DimFacility f where f.id IN (select f.id " + request.generateQuery(viewType, false) + " )";
		}
		if (!basinSql.equals("")) {
			NativeQuery sqlQry = sessionFactory.getCurrentSession().createNativeQuery(basinSql).addScalar("RECORD_COUNT_", IntegerType.INSTANCE);
			int recordCount = ((Number) sqlQry.uniqueResult()).intValue();
			return recordCount;
		} else {
			return DataAccessUtils.intResult(createQuery(queryString).list());
		}
	}
	
	public List<DimFacility> loadDimFacilities(FlightRequest request, FacilityViewType view, boolean isLimited) {
		FacilityType facilityType = request.retrieveFacilityType();
		String msaCode = request.msaCode();
		SectorFilter sectors = request.sectors();
		StopWatch sw = new StopWatch();
		switch (facilityType) {
			case EMITTERS:
				if (request.shouldLdcBeIncludedInEmitterResults()) {
					sw.start("PubLdc");
					List<DimFacility> f = this.getAndExcludePubLdcIntersection(request, view, isLimited);
					sw.stop();
					log.debug("DimFacilityDao: {}", sw.prettyPrint());
					return f;
				}
				if (!msaCode.equals("")) {
					sw.start("Msa");
					List<DimFacility> f = this.getDimFacilitiesInMsa(request, view, isLimited);
					sw.stop();
					log.debug("DimFacilityDao: " + sw.prettyPrint());
					return f;
				}
				if (request.isBoosting()) {
					sw.start("OnShore");
					List<DimFacility> f = this.getDimFacilitiesOnshore(request, view, isLimited);
					sw.stop();
					log.debug("DimFacilityDao: " + sw.prettyPrint());
					return f;
				}
				break;
			case ONSHORE:
				if (sectors.isOnshorePetroleumSectorOnly()) {
					return this.getDimFacilitiesOnshore(request, view, isLimited);
				}
				break;
			case SUPPLIERS:
				if (request.getSupplierSector() == 0) {
					return new ArrayList<>();
				}
				break;
			case CO2_INJECTION:
				if (!msaCode.equals("")) {
					return this.getDimFacilitiesInMsa(request, view, isLimited);
				}
				break;
			default:
				break;
		}
		sw.start("DimFacilities");
		List<DimFacility> fac = this.getDimFacilities(request, view, isLimited);
		sw.stop();
		log.debug("DimFacilityDao: " + sw.prettyPrint());
		return fac;
	}
	
	public List<DimFacility> loadDimFacilities(FlightRequest request, FacilityViewType view) {
		// first false: isLimited, second false: isCountQuery
		return this.loadDimFacilities(request, view, false);
	}
	
	public List<DimFacility> getDimFacilitiesOnshore(FlightRequest request, FacilityViewType viewType, boolean isLimited) {
		String selectionQuery = "";
		String queryString = "";
		Query query = null;
		List<DimFacility> results = null;
		if (request.getBasin() != "" && "GRAY".equals(request.getReportingStatus())) {
			selectionQuery = request.sqlOnshoreBasin(viewType);
			queryString = "SELECT * FROM pub_dim_facility f WHERE (f.facility_id, f.year) IN (" + selectionQuery + ")";
			NativeQuery sqlQry = sessionFactory.getCurrentSession().createNativeQuery(queryString).addEntity(DimFacility.class);
			return sqlQry.list();
		} else {
			switch (viewType) {
				case LIST:
					selectionQuery = request.generateOnshoreQuery(viewType, false);
					String orderBy = DaoUtils.createOrderByClauseFromSortOrder(request.getSortOrder());
					queryString = "from DimFacility f where f.id IN (select f.id " + selectionQuery + ") order by " + orderBy;
					query = createQuery(queryString, isLimited, request.getPageNumber());
					results = query.list();
					break;
				default:
					if (StringUtils.equalsAnyIgnoreCase(request.retrieveFacilityType().name(), "ONSHORE") 
							&& request.sectors().isOnshorePetroleumSectorOnly()) {
						selectionQuery = request.generateOnshoreQuery(viewType, true);
						StringBuilder queryBuilder = new StringBuilder();
						queryBuilder.append(getPubDimAllColumnsString(viewType));
						queryBuilder.append(selectionQuery);
						queryBuilder.append(")");
						results = getAllDimFacilities(queryBuilder.toString(), request, viewType);
					} else {
						selectionQuery = request.generateOnshoreQuery(viewType, false);
						queryString = "from DimFacility f where f.id IN (select f.id " + selectionQuery + ")";
						query = createQuery(queryString, isLimited, request.getPageNumber());
						results = query.list();
					}
			}
			return results;
		}
	}
	
	private final static NumberFormat NF = new DecimalFormat("0.0###");
	
	public List<DimFacility> getDimFacilities(FlightRequest request, FacilityViewType viewType, boolean isLimited) {
		String selectionQuery = StringUtils.EMPTY;
		if (isLimited) {
			selectionQuery = request.generateQuery(viewType, false);
		} else {
			//true param is to execute native sql
			selectionQuery = request.generateQuery(viewType, true);
		}
		List<DimFacility> df;
		String queryString;
		if (StringUtils.startsWith(selectionQuery, "SELECT") && !isLimited) {
			StringBuilder queryS = new StringBuilder();
			queryS.append(getPubDimAllColumnsString(viewType));
			queryS.append(selectionQuery);
			queryS.append(")");
			df = getAllDimFacilities(queryS.toString(), request, viewType);
		} else {
			switch (viewType) {
				case LIST:
					String orderBy = DaoUtils.createOrderByClauseFromSortOrder(request.getSortOrder());
					queryString = "from DimFacility f where f.id IN (select f.id " + selectionQuery + ") order by "	+ orderBy;
					break;
				case MAP:
				default:
					queryString = "from DimFacility f where f.id IN (select f.id " + selectionQuery + ")";
			}
			Statistics statistics = sessionFactory.getStatistics();
			statistics.setStatisticsEnabled(true);
			long hit0 = statistics.getQueryCacheHitCount();
			long miss0 = statistics.getSecondLevelCacheMissCount();
			Query query = createQuery(queryString, isLimited, request.getPageNumber());
			df = query.list();
			long hit1 = statistics.getQueryCacheHitCount();
			long miss1 = statistics.getSecondLevelCacheMissCount();
			double ratio = (double) hit1 / (hit1 + miss1);
			if (hit1 > hit0) {
				log.debug(String.format("CACHE HIT; Ratio=%s", NF.format(ratio)));
			} else if (miss1 > miss0) {
				log.debug(String.format("CACHE MISS; Ratio=%s", NF.format(ratio)));
			} else {
				log.debug("Query cache not used");
			}
		}
		return df;
	}
	
	public List<DimFacility> getAndExcludePubLdcIntersection(FlightRequest request, FacilityViewType viewType, boolean isLimited) {
		String selectionQuery = request.generateQuery(viewType, false);
		String queryString = "from DimFacility f where f.id IN (select f.id " + selectionQuery + ")";
		Query query = createQuery(queryString, isLimited, request.getPageNumber());
		List<DimFacility> dfResults = query.list();
		return dfResults;
	}
	
	public DimFacility findDimFacilityAndHtmlByFacilityIdAndReportingYear(Long id, int year) {
		try {
			final String queryString = "from DimFacility f left outer join fetch f.tribalLand where f.id.facilityId = :id and f.id.year = :year";
			Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("id", id).setParameter("year", (long) year);
			List<DimFacility> results = query.list();
			DimFacility df = !results.isEmpty() ? results.get(0) : new DimFacility();
			// I'm guessing this is to warm up the lazy-fetching HTML field -ahmed, aug 2015
			if (df.getId() != null) {
				df.getHtml();
			}
			return df;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	private List<GasQuantity> getSubSectorEmissions(Long id, int year, String ds, String emissionsType) throws RuntimeException {
		StringBuilder sb = new StringBuilder();
		sb.append("select ss.subSectorId, ss.subSectorDescription, sum(e.co2eEmission) ");
		sb.append(DaoUtils.emissionsTypeFromClause(emissionsType));
		sb.append("join e.facility f ");
		sb.append("join e.sector s ");
		sb.append("join e.subSector ss ");
		sb.append("where f.id.facilityId = :id and f.id.year = :year and s.sectorType = :ds and e.gas.gasId != '8' group by ss.subSectorId, ss.subSectorDescription order by ss.subSectorId");
		final String hQuery = sb.toString();
		List<GasQuantity> gqList = new ArrayList<GasQuantity>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("ds", ds);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		for (Object[] result : results) {
			GasQuantity gq = new GasQuantity();
			gq.setType((String) result[1]);
			if ((BigDecimal) result[2] != null) {
				gq.setQuantity(((BigDecimal) result[2]).setScale(0, RoundingMode.HALF_UP).longValue());
			} else {
				gq.setQuantity(null);
			}
			gqList.add(gq);
		}
		return gqList;
	}
	
	/*public String getProductsSupplied(Long id) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("from FacilitySubpartKeyVal as keyVal ");
			sb.append("inner join fetch keyVal.facility ");
			sb.append("inner join fetch keyVal.subpart ");
			sb.append("inner join fetch keyVal.luKey ");
			sb.append("where keyVal.facility.facilityId = "+id+" ");
			sb.append("and keyVal.luKey.keyName = 'PRODUCTS_SUPPLIED'");
			final Long facilityId = id;
			final String hQuery = sb.toString();
			return (String)sessionFactory.getCurrentSession().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
					query.setCacheable(true);
					List<FacilitySubpartKeyVal> results = query.list();
					if (results.size()>0 && results.get(0).getValue()!=null) {
						return results.get(0).getValue();
					}
					return StringUtils.EMPTY;
				}
			});
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}*/
	
	public FacilityHoverTip getFacilityHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs) {
		try {
			FacilityHoverTip fht = new FacilityHoverTip();
			fht.setFacility(findByFacilityIdAndReportingYear(id, year));
			fht.getFacility().setFacilityName(ServiceUtils.nullSafeHtmlUnescape(fht.getFacility().getFacilityName()));
			fht.setEmissions(this.getSubSectorEmissions(id, year, ds, emissionsType));
			fht.setReportingStatus(rs);
			return fht;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	/*public List<DimFacility> findByFullText(Object facilityName) {
		List<DimFacility> results = sessionFactory.getCurrentSession().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				FullTextSession fts = Search.getFullTextSession(session);
	            TermQuery tq = new TermQuery(new Term("applicant.emailAddress", emailAddress));
	            Query q = fts.createFullTextQuery(tq, DimFacility.class);
	            return q.list();
			}
		});
		return results;
	}*/
	
	public List<DimFacility> findById(Long id) {
		final String queryString = "from DimFacility f where f.id.facilityId = :id order by f.id.year DESC";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("id", id);
		List<DimFacility> dfList = query.list();
		return dfList;
	}
	
	public List findAll() {
		log.debug("finding all DimFacility instances");
		final String hQuery = "from DimFacility";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		return query.list();
	}
	
	public DimFacility merge(DimFacility detachedInstance) {
		log.debug("merging DimFacility instance");
		try {
			DimFacility result = (DimFacility) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}
	
	public void attachDirty(DimFacility instance) {
		log.debug("attaching dirty DimFacility instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public void attachClean(DimFacility instance) {
		log.debug("attaching clean DimFacility instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public List<DimFacility> find(int start, int max, Map<String, Serializable> propertyNameValuePair) {
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			Criteria criteria = (Criteria) cb.createQuery(DimFacility.class);
			Criterion criterion = Restrictions.allEq(propertyNameValuePair);
			criteria.add(criterion);
			criteria.setFirstResult(start);
			criteria.setMaxResults(max);
			return criteria.list();
		}
	}
	
	public Map<String, String> getReportedSubparts(Long id, String ds) throws RuntimeException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct sp.subpartId, sp.subpartName ");
		sb.append("from PubFactsSubpartGhgEmission e ");
		sb.append("join e.facility f ");
		sb.append("join e.subpart sp ");
		sb.append("join e.gas g ");
		sb.append("where f.id.facilityId = :id and sp.subpartType = :ds and g.gasCode <> 'BIOCO2' group by sp.subpartId, sp.subpartName order by sp.subpartId");
		final String hQuery = sb.toString();
		Map<String, String> spList = new HashMap<>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("ds", ds);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		for (Object[] result : results) {
			String subpart = (String) result[1];
			spList.put(subpart, subpart);
		}
		return spList;
	}
	
	public List<String> getFacReportingYears(Long id) {
		final String hQuery = "select f.id.year from DimFacility f where f.id.facilityId = :id order by f.id.year desc";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id);
		return query.list();
	}
	
	/**
	 * tranmission pipelines
	 */
	public List<DimFacilityPipe> loadPipeFacilities(FlightRequest request, FacilityViewType view) {
		return this.getPipeFacilities(request, view, false);
	}
	
	public List<DimFacilityPipe> getPipeFacilities(FlightRequest request, FacilityViewType viewType, boolean isLimited) {
		String selectionQuery = request.generatePipeQuery(viewType);
		String queryString = "";
		switch (viewType) {
			case LIST:
				String orderBy = DaoUtils.createOrderByClauseFromSortOrder(request.getSortOrder());
				queryString = "from DimFacilityPipe f where f.id IN (select f.id " + selectionQuery + ") order by " + orderBy;
				break;
			default:
				queryString = "from DimFacilityPipe f where f.id IN (select f.id " + selectionQuery + ")";
		}
		Query query = createQuery(queryString, isLimited, request.getPageNumber());
		return query.list();
	}
	
	public Map<String, Map<String, BigDecimal>> getPipeEmissions(String queryString, FacilityViewType viewType) {
		queryString = "select f.id, f.state, sum(e.co2eEmission) " + queryString;
		Query query = createQuery(queryString);
		List<Object[]> results = query.list();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		for (Object[] result : results) {
			Long facilityId = (Long) ((DimFacilityId) result[0]).getFacilityId();
			int year = ((DimFacilityId) result[0]).getYear().intValue();
			String facilityName = StringUtils.EMPTY;
			DimFacility f = findByFacilityIdAndReportingYear(facilityId, year);
			if (f != null) {
				facilityName = f.getFacilityName();
			}
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[1]);
			String key = facilityName + " [" + facilityId + "]" + "{" + facState + "}";
			
			BigDecimal emission = (BigDecimal) result[2];
			if (keyMap.containsKey(key)) {
				Map<String, BigDecimal> pipeEmMap = keyMap.get(key);
				pipeEmMap.put(facState, emission);
			} else {
				Map<String, BigDecimal> pipeEmMap = new HashMap<>();
				pipeEmMap.put(facState, emission);
				keyMap.put(key, pipeEmMap);
			}
		}
		return keyMap;
	}
	
	public DimFacilityPipe findPipeIdYear(Long id, int year) {
		try {
			final String queryString = "from DimFacilityPipe f left outer join fetch f.tribalLand where f.id.facilityId = :id and f.id.year = :year";
			Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("id", id).setParameter("year", (long) year);
			query.setCacheable(true);
			
			List<DimFacilityPipe> results = query.list();
			DimFacilityPipe df = !results.isEmpty() ? results.get(0) : new DimFacilityPipe();
			
			if (df.getId() != null) {
				df.getHtml();
			}
			return df;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	private List<GasQuantity> getPipeSubEmissions(Long id, int year, String ds, String emissionsType, String state) throws RuntimeException {
		StringBuilder sb = new StringBuilder();
		sb.append("select ss.subSectorDescription, sum(e.co2eEmission) ");
		sb.append("from PubFactsSectorPipe e ");
		sb.append("join e.facility f ");
		sb.append("join e.sector s ");
		sb.append("join e.subSector ss ");
		sb.append("where f.id.facilityId = :id and f.id.year = :year and s.sectorType = :ds and e.gas.gasId != '8' ");
		sb.append("and e.state = f.stateName ");
		sb.append("and f.state = :state ");
		sb.append("group by ss.subSectorDescription");
		final String hQuery = sb.toString();
		List<GasQuantity> gqList = new ArrayList<GasQuantity>();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("ds", ds).setParameter("state", state);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		for (Object[] result : results) {
			GasQuantity gq = new GasQuantity();
			gq.setType((String) result[0]);
			if ((BigDecimal) result[1] != null) {
				gq.setQuantity(((BigDecimal) result[1]).setScale(0, RoundingMode.HALF_UP).longValue());
			} else {
				gq.setQuantity(null);
			}
			gqList.add(gq);
		}
		return gqList;
	}
	
	public PipeHoverTip getPipeHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs, String state) {
		try {
			PipeHoverTip pht = new PipeHoverTip();
			pht.setFacility(findPipeIdYear(id, year));
			pht.getFacility().setFacilityName(ServiceUtils.nullSafeHtmlUnescape(pht.getFacility().getFacilityName()));
			pht.setEmissions(getPipeSubEmissions(id, year, ds, emissionsType, state));
			pht.setReportingStatus(rs);
			return pht;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	private String getPubDimAllColumnsString(FacilityViewType viewType) {
		StringBuilder queryS = new StringBuilder();
		queryS.append("Select dimfacilit0_.FACILITY_ID AS facilityId, dimfacilit0_.YEAR AS YEAR,");
		queryS.append(" dimfacilit0_.ADDRESS1 AS address1,dimfacilit0_.ADDRESS2 AS address2,");
		queryS.append(
				" dimfacilit0_.CEMS_USED AS cemsUsed,dimfacilit0_.CITY AS city,dimfacilit0_.CO2_CAPTURED AS co2Captured,");
		queryS.append(
				" dimfacilit0_.EMITTED_CO2_SUPPLIED AS emittedCo2Supplied,dimfacilit0_.COMMENTS AS comments,dimfacilit0_.COUNTY AS county,");
		queryS.append(
				" dimfacilit0_.COUNTY_FIPS AS countyFips,dimfacilit0_.EGGRT_FACILITY_ID AS eggrtFacilityId,dimfacilit0_.EMISSION_CLASSIFICATION_CODE AS emissionClassificationCode,");
		queryS.append(
				" dimfacilit0_.FACILITY_NAME AS facilityName,dimfacilit0_.FRS_ID AS frsId,dimfacilit0_.LATITUDE AS latitude,dimfacilit0_.LOCATION.Get_WKT() AS location,");
		queryS.append(
				" dimfacilit0_.LONGITUDE AS longitude,dimfacilit0_.NAICS_CODE AS naicsCode,dimfacilit0_.PARENT_COMPANY AS parentCompany,");
		queryS.append(
				" dimfacilit0_.PROCESS_STATIONARY_CML AS processStationaryCml,dimfacilit0_.PROGRAM_NAME AS programName,");
		queryS.append(
				" dimfacilit0_.PROGRAM_SYS_ID AS programSysId,dimfacilit0_.REPORTED_INDUSTRY_TYPES AS reportedIndustryTypes,");
		queryS.append(
				" dimfacilit0_.REPORTED_SUBPARTS AS reportedSubparts,dimfacilit0_.REPORTING_STATUS AS reportingStatus,");
		queryS.append(
				" dimfacilit0_.RR_MONITORING_PLAN_FILENAME AS rrMonitoringPlanFilename,dimfacilit0_.RR_MRV_PLAN_URL AS rrMrvPlanUrl,");
		queryS.append(
				" dimfacilit0_.RR_MONITORING_PLAN AS rrMonitoringPlan,dimfacilit0_.STATE AS state,dimfacilit0_.STATE_NAME AS stateName,dimfacilit0_.TRIBAL_LAND_ID AS tribalLandId,");
		if (viewType == MAP) {
			queryS.append(" (SELECT TRIBAL_LAND_NAME FROM PUB_LU_TRIBAL_LANDS WHERE TRIBAL_LAND_ID = dimfacilit0_.TRIBAL_LAND_ID) AS tribalLandName,");
		}
		queryS.append(" dimfacilit0_.UU_RD_EXEMPT AS uuRdExempt,");
		queryS.append(" dimfacilit0_.ZIP AS zip");
		queryS.append(
				" FROM PUB_DIM_FACILITY dimfacilit0_ WHERE (dimfacilit0_.FACILITY_ID,dimfacilit0_.YEAR) IN (");
		return queryS.toString();
	}
}
