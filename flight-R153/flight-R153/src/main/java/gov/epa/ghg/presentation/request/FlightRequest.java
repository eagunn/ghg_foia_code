package gov.epa.ghg.presentation.request;

import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.NewSectorFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.FacilityViewType;
import gov.epa.ghg.util.DaoUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import java.lang.StringBuilder;

import static gov.epa.ghg.enums.FacilityViewType.MAP;
import static gov.epa.ghg.enums.FacilityViewType.EXPORT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/*
'bs' : generateBasinFilter(),
'gases'  : generateGasFilter(),
'sectors' : generateSectorFilter(),
'sc' : supplierSector,
'rs' : reportingStatus,
'fs' : generateSearchQueryJson()
*/

public class FlightRequest {
	
	DecimalFormat df = new DecimalFormat("00000");
	String trend;
	String dataSource;
	int reportingYear;
	int currentYear;
	int injectionSelection;
	int sortOrder;
	String basin;
	String query;
	String state;
	Integer countyFips;
	Integer msaCode;
	Long tribalLandId;
	int stateLevel;
	int overlayLevel;
	Long facId; // fid
	String searchOptions; // sf
	Long lowE;
	Long highE;
	Boolean[] gases = new Boolean[12];
	Boolean[][] sectors = new Boolean[9][];
	String reportingStatus;
	int supplierSector;
	int pageNumber;
	String fs;
	String emissionsType;
	String visType;
	
	private DimFacilityQueryGenerator queryGenerator = new DimFacilityQueryGenerator();
	
	/**
	 * constructors
	 */
	public FlightRequest() {
	}
	
	public FlightRequest(String dataSource, int reportingYear, int currentYear, String query, String state, Integer countyFips, Integer msaCode, Long tribalLandId, String searchOptions, long lowE, long highE, String emissionsType, String trend,
			int supplierSector, int injectionSelection, String reportingStatus, String basin, Byte[] gases, Byte[] sectors, int overlayLevel, int stateLevel) {
		this.dataSource = dataSource;
		this.reportingYear = reportingYear;
		this.currentYear = currentYear;
		this.query = query;
		this.state = state;
		this.countyFips = countyFips;
		this.msaCode = msaCode;
		this.tribalLandId = tribalLandId;
		this.searchOptions = searchOptions;
		this.lowE = lowE;
		this.highE = highE;
		this.emissionsType = emissionsType;
		this.trend = trend;
		this.supplierSector = supplierSector;
		this.injectionSelection = injectionSelection;
		this.reportingStatus = reportingStatus;
		this.basin = basin;
		this.overlayLevel = overlayLevel;
		this.stateLevel = stateLevel;
		ArrayConverter converter = new ArrayConverter();
		if (gases != null) {
			this.gases = converter.convertGasesByteArrayToBooleanArray(gases);
		}
		if (sectors != null) {
			this.sectors = converter.convertSectorsByteArrayToBooleanArray(sectors);
		}
		this.visType = visType;
	}
	
	/**
	 * getters & setters
	 */
	
	public int getOverlayLevel() {
		return overlayLevel;
	}
	
	public void setOverlayLevel(int overlayLevel) {
		this.overlayLevel = overlayLevel;
	}
	
	public int getStateLevel() {
		return stateLevel;
	}
	
	public void setStateLevel(int stateLevel) {
		this.stateLevel = stateLevel;
	}
	
	public Integer getMsaCode() {
		return msaCode;
	}
	
	public void setMsaCode(Integer msaCode) {
		this.msaCode = msaCode;
	}
	
	public String msaCode() {
		return msaCode != null ? String.valueOf(df.format(msaCode)) : "";
	}
	
	public int getInjectionSelection() {
		return injectionSelection;
	}
	
	public void setInjectionSelection(int injectionSelection) {
		this.injectionSelection = injectionSelection;
	}
	
	public int getCurrentYear() {
		return currentYear;
	}
	
	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	public FacilityType retrieveFacilityType() {
		return FacilityType.fromDataSource(dataSource);
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public int getReportingYear() {
		return reportingYear;
	}
	
	public void setReportingYear(int reportingYear) {
		this.reportingYear = reportingYear;
	}
	
	public String getFs() {
		return fs;
	}
	
	public void setFs(String fs) {
		this.fs = fs;
	}
	
	public String getBasin() {
		return basin;
	}
	
	public void setBasin(String basin) {
		this.basin = basin;
	}
	
	public String facId() {
		return facId != null ? String.valueOf(facId) : "";
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String q) {
		this.query = q;
	}
	
	public String getState() {
		if (!"".equals(msaCode()) || "US".equalsIgnoreCase(state)) {
			return "";
		} else {
			return state;
		}
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String countyFips() {
		return countyFips != null ? String.valueOf(df.format(countyFips)) : "";
	}
	
	public Integer getCountyFips() {
		return countyFips;
	}
	
	public void setCountyFips(Integer countyFips) {
		this.countyFips = countyFips;
	}
	
	public Long getFacId() {
		return facId;
	}
	
	public void setFacId(Long facId) {
		this.facId = facId;
	}
	
	public String getSearchOptions() {
		return searchOptions;
	}
	
	public void setSearchOptions(String searchOptions) {
		this.searchOptions = searchOptions;
	}
	
	public QueryOptions queryOptions() {
		return new QueryOptions(searchOptions);
	}
	
	public void setQueryOptions(QueryOptions qo) {
		this.searchOptions = qo.toString();
	}
	
	public String lowE() {
		return lowE != null ? String.valueOf(lowE) : "";
	}
	
	public String highE() {
		return String.valueOf(highE);
	}
	
	public Long getLowE() {
		return lowE;
	}
	
	public void setLowE(Long lowE) {
		this.lowE = lowE;
	}
	
	public Long getHighE() {
		return highE;
	}
	
	public void setHighE(Long highE) {
		this.highE = highE;
	}
	
	public String getReportingStatus() {
		if (this.reportingYear <= 2012) {
			return "";
		} else {
			return reportingStatus;
		}
	}
	
	public void setReportingStatus(String rs) {
		this.reportingStatus = rs;
	}
	
	public int getSupplierSector() {
		return supplierSector;
	}
	
	public void setSupplierSector(int sc) {
		this.supplierSector = sc;
	}
	
	public GasFilter gases() {
		return new GasFilter(gases);
	}
	
	public Boolean[] getGases() {
		return gases;
	}
	
	public void setGases(Boolean[] gases) {
		this.gases = gases;
	}
	
	public SectorFilter sectors() {
		return new NewSectorFilter(sectors);
	}
	
	public Boolean[][] getSectors() {
		return sectors;
	}
	
	public void setSectors(Boolean[][] sectors) {
		this.sectors = sectors;
	}
	
	public boolean isTrendRequest() {
		return "trend".equals(trend);
	}
	
	public String getTrend() {
		return trend;
	}
	
	public void setTrend(String trend) {
		this.trend = trend;
	}
	
	public String getEmissionsType() {
		return emissionsType;
	}
	
	public void setEmissionsType(String emissionsType) {
		this.emissionsType = emissionsType;
	}
	
	public Long getTribalLandId() {
		return tribalLandId;
	}
	
	public void setTribalLandId(Long tribalLandId) {
		this.tribalLandId = tribalLandId;
	}
	
	public int getSortOrder() {
		return sortOrder;
	}
	
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public String getVisType() {
		return visType;
	}
	
	public void setVisType(String visType) {
		this.visType = visType;
	}
	
	/**
	 * helper methods
	 */
	public boolean shouldLdcBeIncludedInEmitterResults() {
		return (
			// data source is either LDC or Emitters
			("L".equals(dataSource) || "E".equals(dataSource))
				&& (!"".equals(state) && state != null) && msaCode == null // there is a state without any subfiltering
				&& countyFips == null
				&& tribalLandId == null);
	}
	
	public boolean isBoosting() {
		return ("B".equals(dataSource)
				|| (sectors().isPetroleumAndNaturalGas() && !sectors().isS901() && !sectors().isS902() && !sectors().isS903() && !sectors().isS904() && !sectors().isS905()
				&& !sectors().isS906() && !sectors().isS907() && !sectors().isS908() && sectors().isS910() && !sectors().isS911()));
	}
	
	public boolean isPipe() {
		return ("T".equals(dataSource)
				|| (sectors().isPetroleumAndNaturalGas() && !sectors().isS901() && !sectors().isS902() && !sectors().isS903() && !sectors().isS904() && !sectors().isS905()
				&& !sectors().isS906() && !sectors().isS907() && !sectors().isS908() && !sectors().isS910() && sectors().isS911()));
	}
	
	public boolean isWholePetroNg() {
		return (sectors().isPetroleumAndNaturalGas() && sectors().isS901() && sectors().isS902() && sectors().isS903() && sectors().isS904() && sectors().isS905()
				&& sectors().isS906() && sectors().isS907() && sectors().isS908() && sectors().isS910() && sectors().isS911());
	}
	
	/**
	 * this is the gateway to generating DimFacility queries, the principal way of loading facility data in FLIGHT
	 *
	 * @param viewType : the current FLIGHT view (e.g. MAP, LIST, EXPORT)
	 *
	 * @return : a Database Query String
	 */
	public String generateQuery(FacilityViewType viewType, boolean selectByNativeQuery) {
		FacilityType facilityType = this.retrieveFacilityType();
		switch (facilityType) {
			case EMITTERS:
				return queryGenerator.generateEmitterQuery(viewType, selectByNativeQuery);
			case SUPPLIERS:
				return queryGenerator.generateSupplierQuery(viewType);
			case ONSHORE:
				return queryGenerator.generateOnshoreQuery(viewType, selectByNativeQuery);
			case CO2_INJECTION:
				return queryGenerator.generateCo2InjectionQuery(viewType);
			case RR_CO2:
				return queryGenerator.generateRrCo2Query(viewType);
			default:
				return queryGenerator.generateEmitterQuery(viewType, selectByNativeQuery);
		}
	}
	
	public String generateSectorDashboardQuery() {
		return queryGenerator.generateSectorDashboardQuery();
	}
	
	public String generateLdcQuery() {
		return queryGenerator.generateLdcQuery();
	}
	
	// wrapper to expose querygen method to outside world
	public String generatePlcSectorDashboardQuery() {
		return queryGenerator.generateSectorDashboardExcludeLdcQuery();
	}
	
	// this needs a public hook as sometimes (i.e. Emitter but Onshore we need to override the natural
	// flow process by specifying that we want onshore
	public String generateOnshoreQuery(FacilityViewType viewType, boolean useNativeQuery) {
		return queryGenerator.generateOnshoreQuery(viewType, useNativeQuery);
	}
	
	public String generatePipeQuery(FacilityViewType viewType) {
		return queryGenerator.generatePipeQuery(viewType);
	}
	
	public String generatePipeDashboardQuery() {
		return queryGenerator.generatePipeDashboardQuery();
	}
	
	/**
	 * Added by Erin Le @ SAIC February 2018
	 * PUB-704: Special case for Onshore Non-Reporting Facilities per selected basin
	 */
	public String sqlOnshoreBasin(FacilityViewType viewType) {
		SectorFilter sectors = sectors();
		GasFilter gases = gases();
		QueryOptions qo = queryOptions();
		return "SELECT facility_id, year FROM (Select facility_id, year, Rank() over (partition by facility_id order by year) RankOrder From pub_dim_facility Where facility_id IN ("
				+ "select f.facility_id from pub_dim_facility f "
				+ "join pub_dim_facility_status_mv fs ON f.facility_id = fs.facility_id "
				+ "left join pub_facts_sector_ghg_emission e ON f.facility_id = e.facility_id and f.year = e.year "
				+ "left join pub_dim_sector s ON e.sector_id = s.sector_id "
				+ "left join pub_dim_ghg g ON e.gas_id = g.gas_id "
				+ DaoUtils.sqlBasinFilter(basin)
				+ DaoUtils.ldcSubSectorFilter(sectors, EMPTY, EMPTY)
				+ "where (f.facility_id) in (select distinct f.facility_id "
				+ "from pub_dim_facility f "
				+ "left join pub_facts_sector_ghg_emission e ON f.facility_id = e.facility_id and f.year = e.year "
				+ "left join pub_dim_sector s ON e.sector_id = s.sector_id "
				+ "left join pub_dim_ghg g ON e.gas_id = g.gas_id "
				+ DaoUtils.sqlBasinFilter(basin)
				+ DaoUtils.ldcSubSectorFilter(sectors, EMPTY, EMPTY)
				+ "where (s.sector_type is null or s.sector_type = 'E') "
				+ DaoUtils.sqlBasinWhereClause(basin)
				+ DaoUtils.sqlEmitterWhereClause(query, reportingYear, EMPTY, EMPTY,
						gases, sectors, qo)
				+ "group by f.facility_id, f.year having "
				+ "sum(e.co2e_emission) is null or "
				+ "(sum(e.co2e_emission) >= " + lowE + " and sum(e.co2e_emission) <= " + highE + ")) "
				+ DaoUtils.sqlBasinWhereClause(basin)
				+ DaoUtils.sqlEmitterWhereClause(query, reportingYear, EMPTY, EMPTY, gases, sectors, qo)
				+ "and (g.gas_code is null or g.gas_code <> 'BIOCO2') and (s.sector_type = 'E') and fs.year = " + reportingYear + " and fs.facility_type = 'E' "
				+ ReportingStatusQueryFilter.sqlFilter(reportingStatus, reportingYear) + " group by f.facility_id) "
				+ ") T WHERE RankOrder = 1 ";
	}
	
	class ArrayConverter {
		
		Boolean[] convertGasesByteArrayToBooleanArray(Byte[] gases) {
			Boolean[] retv = new Boolean[gases.length];
			for (int i = 0; i < gases.length; i++) {
				if (gases[i] == 1) {
					retv[i] = true;
				} else {
					retv[i] = false;
				}
			}
			return retv;
		}
		
		Boolean[][] convertSectorsByteArrayToBooleanArray(Byte[] sectors) {
			Boolean[][] retv = new Boolean[9][];
			retv[0] = new Boolean[1];
			retv[1] = new Boolean[5];
			retv[2] = new Boolean[8];
			retv[3] = new Boolean[6];
			retv[4] = new Boolean[1];
			retv[5] = new Boolean[3];
			retv[6] = new Boolean[12];
			retv[7] = new Boolean[11];
			retv[8] = new Boolean[12];
			Integer i = 0;
			for (int k = 0; k < retv.length; k++) {
				fill(i, sectors, retv[k]);
				i = i + retv[k].length;
			}
			return retv;
		}
		
		private void fill(Integer i, Byte[] sectors, Boolean[] retv) {
			for (int j = 0; j < retv.length; j++, i++) {
				retv[j] = (sectors[i] != null && sectors[i] > 0);
			}
		}
	}
	
	class DimFacilityQueryGenerator {
		
		String generateLdcQuery() {
			SectorFilter sectors = sectors();
			GasFilter gases = gases();
			String state = getState();
			String countyFips = countyFips();
			QueryOptions qo = queryOptions();
			String lowE = lowE();
			String highE = highE();
			String tribalLandState = state;
			String standardSnippet = "from DimFacility f "
					+ "join f.facStatus fs "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where f.id IN (select distinct f.id "
					+ "from DimFacility f "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where s.sectorType = 'E' "
					+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(tribalLandState, tribalLandId)
					+ ReportingStatusQueryFilter.filterEmissionsRange(reportingStatus, lowE, highE)
					+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(tribalLandState, tribalLandId)
					+ "and ("
					+ DaoUtils.gasFilter(gases)
					+ "g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
			final String pubLdcSnippet = "from PubLdcFacility l where l.id.state='" + tribalLandState + "' and l.id.facilityId != '0' and l.id.year='" + reportingYear + "' and l.facility.id in (select f.id " + standardSnippet + ")";
			return pubLdcSnippet;
		}
		
		// this includes LDC facilities
		String generateSectorDashboardQuery() {
			String msaCode = msaCode();
			String countyFips = countyFips();
			String state = getState();
			String lowE = lowE();
			String highE = highE();
			SectorFilter sectors = sectors();
			GasFilter gases = gases();
			QueryOptions qo = queryOptions();
			if (!msaCode.equals("")) {
				state = "";
			}
			String retv = "select s.sectorCode, "
					+ "sum(e.co2eEmission), "
					+ "count(distinct f.id.facilityId) "
					+ "from "
					+ "DimFacility f "
					+ "join f.facStatus fs "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where ";
			if (!msaCode.equals("")) {
				retv += "f.id IN "
						+ "( "
						+ "select "
						+ "f.id "
						+ "from "
						+ "DimFacility f, "
						+ "DimMsa m "
						+ "where "
						+ "sdo_inside(f.location, m.geometry) = 'TRUE' and "
						+ "m.cbsafp = '" + msaCode + "' and "
						+ "f.id.year = '" + reportingYear + "' "
						+ ") and ";
			}
			retv += "f.id IN "
					+ "( "
					+ "select "
					+ "distinct f.id "
					+ "from "
					+ "DimFacility f "
					+ "join f.facStatus fs "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.basinFilter(basin)
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where "
					+ "s.sectorType = 'E' "
					+ DaoUtils.basinWhereClause(basin)
					+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(state, tribalLandId) + " and "
					+ "("
					+ DaoUtils.gasFilter(gases)
					+ "g.gasCode <> 'BIOCO2' "
					+ ") and "
					+ "fs.id.year = " + reportingYear + " and "
					+ "fs.facilityType = 'E' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " "
					+ (("STOPPED_REPORTING".equals(reportingStatus) || "GRAY".equals(reportingStatus) || "RED".equals(reportingStatus))
							? EMPTY :
							"group by "
							+ "f.id "
							+ "having "
							+ "sum(e.co2eEmission) is null or "
							+ "sum(e.co2eEmission) >= " + lowE + " and "
							+ "sum(e.co2eEmission) <= " + highE + " ")
							+ ") and "
							+ "s.sectorType = 'E' "
							+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
							+ DaoUtils.tribalLandWhereClause(state, tribalLandId) + " and "
							+ "("
							+ DaoUtils.gasFilter(gases)
							+ "g.gasCode <> 'BIOCO2' "
							+ ") and "
							+ "fs.id.year = " + reportingYear + " and "
							+ "fs.facilityType = 'E' "
							+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " "
							+ "group by "
							+ "s.sectorCode";
			return retv;
		}
		
		String generateSectorDashboardExcludeLdcQuery() {
			SectorFilter sectors = sectors();
			String countyFips = countyFips();
			GasFilter gases = gases();
			String state = getState();
			return "select s.sectorCode, sum(e.co2eEmission), count(distinct f.id.facilityId) "
					+ "from DimFacility f "
					+ "join f.facStatus fs "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where "
					+ "f.id NOT IN (select plf.facility.id from PubLdcFacility plf where plf.id.year='" + reportingYear + "') and "
					+ "f.id IN (select distinct f.id "
					+ "from DimFacility f "
					+ DaoUtils.emissionsTypeFilter(emissionsType)
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.basinFilter(basin)
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where s.sectorType = 'E' "
					+ DaoUtils.basinWhereClause(basin)
					+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, queryOptions())
					+ "group by f.id having sum(e.co2eEmission) >= " + lowE() + " and sum(e.co2eEmission) <= " + highE() + ") "
					+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, queryOptions())
					+ "and ("
					+ DaoUtils.gasFilter(gases)
					+ "g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by s.sectorCode";
		}
		
		// panel list
		String generateEmitterQuery(FacilityViewType viewType, boolean doNativeQuery) {
			// MSA requires state to be blank, have to call getState which does that filtering for us
			String state = getState();
			SectorFilter sectors = sectors();
			String lowE = lowE();
			String highE = highE();
			String countyFips = countyFips();
			GasFilter gases = gases();
			QueryOptions qo = queryOptions();
			String formedQuery = StringUtils.EMPTY;
			StringBuilder subQueryStr = new StringBuilder();
			if (doNativeQuery) {
				subQueryStr.append("SELECT dimfacilit1_.FACILITY_ID, dimfacilit1_.YEAR FROM PUB_DIM_FACILITY dimfacilit1_");
				subQueryStr.append(" INNER JOIN PUB_DIM_FACILITY_STATUS_MV fs ON dimfacilit1_.FACILITY_ID = fs.FACILITY_ID AND dimfacilit1_.YEAR = fs.YEAR");
				if (viewType == EXPORT) {
					subQueryStr.append(" AND (fs.REPORTING_STATUS NOT IN ('STOPPED_REPORTING_VALID_REASON', 'STOPPED_REPORTING_UNKNOWN_REASON') OR  fs.REPORTING_STATUS IS NULL)");
				}
				subQueryStr.append(DaoUtils.emissionsTypeFilterSql(emissionsType, "emissions3_ ON dimfacilit1_.FACILITY_ID = emissions3_.FACILITY_ID AND dimfacilit1_.YEAR = emissions3_.YEAR"));
				subQueryStr.append(" LEFT OUTER JOIN PUB_DIM_SECTOR dimsector4_ ON emissions3_.SECTOR_ID = dimsector4_.SECTOR_ID");
				subQueryStr.append(" LEFT OUTER JOIN PUB_DIM_GHG dimghg5_ ON emissions3_.GAS_ID = dimghg5_.GAS_ID");
				if (viewType == MAP || (state != null && state.equals("TL") && tribalLandId != null)) {
					subQueryStr.append(" LEFT OUTER JOIN PUB_LU_TRIBAL_LANDS lutriballa6_ ON dimfacilit1_.TRIBAL_LAND_ID = lutriballa6_.TRIBAL_LAND_ID");
				}
				subQueryStr.append(DaoUtils.emitterSubSectorFilterSql(sectors, state, countyFips, "ss ON ss.subsector_id = emissions3_.subsector_id"));
				subQueryStr.append(" WHERE ");
				subQueryStr.append(" ((dimfacilit1_.FACILITY_ID, dimfacilit1_.YEAR) IN (");
				subQueryStr.append(" SELECT DISTINCT dimfacilit7_.FACILITY_ID, dimfacilit7_.YEAR FROM");
				subQueryStr.append(" PUB_DIM_FACILITY dimfacilit7_ ");
				subQueryStr.append(DaoUtils.emissionsTypeFilterSql(emissionsType, "emissions8_ ON dimfacilit7_.FACILITY_ID = emissions8_.FACILITY_ID AND dimfacilit7_.YEAR = emissions8_.YEAR"));
				subQueryStr.append(" LEFT OUTER JOIN PUB_DIM_SECTOR dimsector9_ ON emissions8_.SECTOR_ID = dimsector9_.SECTOR_ID");
				subQueryStr.append(" LEFT OUTER JOIN PUB_DIM_GHG dimghg10_ ON emissions8_.GAS_ID = dimghg10_.GAS_ID");
				subQueryStr.append(DaoUtils.emitterSubSectorFilterSql(sectors, state, countyFips, "sss ON sss.subsector_id = emissions8_.subsector_id"));
				subQueryStr.append(" WHERE dimsector9_.SECTOR_TYPE = 'E'");
				subQueryStr.append(DaoUtils.emitterWhereClauseSql(query, reportingYear, state, countyFips,
						gases, sectors, qo, "dimghg10_.GAS_CODE", "dimsector9_.SECTOR_CODE", "sss", "dimfacilit7_"));
				subQueryStr.append(" GROUP BY dimfacilit7_.FACILITY_ID, dimfacilit7_.YEAR HAVING");
				subQueryStr.append(DaoUtils.shouldFacilitiesWithNullEmissionsBeIncludedSql(lowE));
				subQueryStr.append(" sum(emissions8_.CO2E_EMISSION) >= " + lowE + " and sum(emissions8_.CO2E_EMISSION) <= " + highE);
				if (viewType == EXPORT) {
					subQueryStr.append(" and sum(emissions8_.CO2E_EMISSION) <> 0 ");
				}
				subQueryStr.append(")) ");
				subQueryStr.append(DaoUtils.emitterWhereClauseSql(query, reportingYear, state, countyFips,	gases, sectors, qo,
						"dimghg5_.GAS_CODE", "dimsector4_.SECTOR_CODE", "ss", "dimfacilit1_"));
				subQueryStr.append(DaoUtils.tribalLandWhereClauseSql(state, tribalLandId));
				subQueryStr.append(" and (");
				subQueryStr.append(DaoUtils.gasFilterSql(gases));
				subQueryStr.append(" dimghg5_.gas_code <> 'BIOCO2') and dimsector4_.SECTOR_TYPE = 'E' and fs.year = ? and fs.FACILITY_TYPE = 'E' ");
				subQueryStr.append(ReportingStatusQueryFilter.sqlFilter(reportingStatus, reportingYear));
				subQueryStr.append(" GROUP BY dimfacilit1_.FACILITY_ID,dimfacilit1_.YEAR");
				formedQuery = subQueryStr.toString();
			} else {
				formedQuery = "from DimFacility f "
						+ "join f.facStatus fs "
						+ DaoUtils.emissionsTypeFilter(emissionsType)
						+ "left join e.sector s "
						+ "left join e.gas g "
						+ ((viewType == MAP) ? "left join f.tribalLand " : "")
						+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
						+ "where f.id IN (select distinct f.id "
						+ "from DimFacility f "
						+ DaoUtils.emissionsTypeFilter(emissionsType)
						+ "left join e.sector s "
						+ "left join e.gas g "
						+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
						+ "where s.sectorType = 'E' "
						+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
						+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
						+ "group by f.id having "
						+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
						+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
						+ DaoUtils.emitterWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
						+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
						+ "and ("
						+ DaoUtils.gasFilter(gases)
						+ "g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
						+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
			}
			return formedQuery;
		}
		
		String generateCo2InjectionQuery(FacilityViewType viewType) {
			// MSA requires state to be blank, have to call getState which does that filtering for us
			String state = getState();
			QueryOptions qo = queryOptions();
			GasFilter gases = gases();
			String countyFips = countyFips();
			String lowE = lowE();
			return "from DimFacility f "
					+ "join f.facStatus fs "
					+ "left join f.emissions e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ "where f.id IN (select distinct f.id "
					+ "from DimFacility f "
					+ "left join f.emissions e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ "where s.sectorType = 'I' "
					+ DaoUtils.co2InjectionWhereClause(query, reportingYear, state, countyFips, gases, qo, injectionSelection)
					+ "group by f.id having "
					+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
					+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
					+ DaoUtils.co2InjectionWhereClause(query, reportingYear, state, countyFips, gases, qo, injectionSelection)
					+ "and g.gasCode <> 'BIOCO2' and s.sectorType = 'I' and fs.id.year = " + reportingYear + " and fs.facilityType = 'I' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
		}
		
		String generateOnshoreQuery(FacilityViewType viewType, boolean doNativeQuery) {
			String qtr = StringUtils.EMPTY;
			SectorFilter sectors = sectors();
			GasFilter gases = gases();
			QueryOptions qo = queryOptions();
			if (doNativeQuery) {
				StringBuilder queryStr = new StringBuilder();
				queryStr.append(" SELECT dimfacilit1_.FACILITY_ID, dimfacilit1_.YEAR FROM PUB_DIM_FACILITY dimfacilit1_");
				queryStr.append(" INNER JOIN PUB_DIM_FACILITY_STATUS_MV fs ON dimfacilit1_.FACILITY_ID = fs.FACILITY_ID AND dimfacilit1_.YEAR = fs.YEAR");
				if (viewType == EXPORT) {
					queryStr.append(" AND (fs.REPORTING_STATUS NOT IN ('STOPPED_REPORTING_VALID_REASON', 'STOPPED_REPORTING_UNKNOWN_REASON') OR  fs.REPORTING_STATUS IS NULL)");
				}
				queryStr.append(" LEFT OUTER JOIN PUB_FACTS_SECTOR_GHG_EMISSION emissions3_ ON dimfacilit1_.FACILITY_ID = emissions3_.FACILITY_ID AND dimfacilit1_.YEAR = emissions3_.YEAR");
				queryStr.append(" LEFT OUTER JOIN PUB_DIM_SECTOR dimsector4_ ON emissions3_.SECTOR_ID = dimsector4_.SECTOR_ID");
				queryStr.append(" LEFT OUTER JOIN PUB_DIM_GHG dimghg5_ ON emissions3_.GAS_ID = dimghg5_.GAS_ID");
				if (basin != null && basin.length() > 0) {
					queryStr.append(" INNER JOIN pub_basin_facility b on dimfacilit1_.facility_id = b.facility_id");
				}
				if (viewType == MAP || (state != null && state.equals("TL") && tribalLandId != null)) {
					queryStr.append(" LEFT OUTER JOIN PUB_LU_TRIBAL_LANDS lutriballa6_ ON dimfacilit1_.TRIBAL_LAND_ID = lutriballa6_.TRIBAL_LAND_ID");
				}
				queryStr.append(DaoUtils.emitterSubSectorFilterSql(sectors, StringUtils.EMPTY, StringUtils.EMPTY, "dimsubsect6_ ON dimsubsect6_.subsector_id = emissions3_.subsector_id"));
				queryStr.append(" WHERE ( (dimfacilit1_.FACILITY_ID, dimfacilit1_.YEAR) IN (");
				//subquery
				queryStr.append(" SELECT DISTINCT dimfacilit7_.FACILITY_ID, dimfacilit7_.YEAR FROM PUB_DIM_FACILITY dimfacilit7_");
				queryStr.append(" LEFT OUTER JOIN PUB_FACTS_SECTOR_GHG_EMISSION emissions8_ ON dimfacilit7_.FACILITY_ID = emissions8_.FACILITY_ID AND dimfacilit7_.YEAR = emissions8_.YEAR");
				queryStr.append(" LEFT OUTER JOIN PUB_DIM_SECTOR dimsector9_ ON emissions8_.SECTOR_ID = dimsector9_.SECTOR_ID");
				queryStr.append(" LEFT OUTER JOIN PUB_DIM_GHG dimghg10_ ON emissions8_.GAS_ID = dimghg10_.GAS_ID");
				if (basin != null && basin.length() > 0) {
					queryStr.append(" INNER JOIN pub_basin_facility bb on dimfacilit7_.facility_id = bb.facility_id");
				}
				queryStr.append(DaoUtils.emitterSubSectorFilterSql(sectors, StringUtils.EMPTY, StringUtils.EMPTY, "dimsubsect11_ ON dimsubsect11_.subsector_id = emissions8_.subsector_id"));
				queryStr.append(" WHERE");
				queryStr.append(" dimsector9_.SECTOR_TYPE = 'E'");
				if (basin != null && basin.length() > 0) {
					queryStr.append(" and bb.basin_code = '" + basin + "' ");
				}
				queryStr.append(DaoUtils.emitterWhereClauseSql(query, reportingYear, EMPTY, EMPTY,
						gases, sectors, qo, "dimghg10_.GAS_CODE", "dimsubsect11_.SECTOR_CODE", "dimsubsect11_", "dimfacilit7_"));
				queryStr.append(" GROUP BY dimfacilit7_.FACILITY_ID, dimfacilit7_.YEAR");
				queryStr.append(" HAVING ");
				queryStr.append(DaoUtils.shouldFacilitiesWithNullEmissionsBeIncludedSql(lowE()));
				queryStr.append(" sum(emissions8_.CO2E_EMISSION)>=-20000 and sum(emissions8_.CO2E_EMISSION)<= 23000000 ");
				if (viewType == EXPORT) {
					queryStr.append(" and sum(emissions8_.CO2E_EMISSION) <> 0 ");
				}
				queryStr.append("))");
				if (basin != null && basin.length() > 0) {
					queryStr.append(" and b.basin_code = '" + basin + "' ");
				}
				//dimghg5_.GAS_CODE IS NULL OR
				queryStr.append(DaoUtils.emitterWhereClauseSql(query, reportingYear, EMPTY, EMPTY,
						gases, sectors, qo, "dimghg5_.GAS_CODE", "dimsector4_.SECTOR_CODE", "dimsubsect6_", "dimfacilit1_"));
				queryStr.append(" and ( dimghg5_.GAS_CODE <> 'BIOCO2')");
				queryStr.append(" and dimsector4_.SECTOR_TYPE = 'E' and fs.YEAR = ? and fs.FACILITY_TYPE = 'E'");
				queryStr.append(ReportingStatusQueryFilter.sqlFilter(reportingStatus, reportingYear));
				queryStr.append(" GROUP BY dimfacilit1_.FACILITY_ID,dimfacilit1_.YEAR");
				return queryStr.toString();
			} else {
				qtr = "from DimFacility f "
						+ "join f.facStatus fs "
						+ "left join f.emissions e "
						+ "left join e.sector s "
						+ "left join e.gas g "
						+ DaoUtils.basinFilter(basin)
						+ DaoUtils.emitterSubSectorFilter(sectors, EMPTY, EMPTY)
						+ "where f.id IN (select distinct f.id "
						+ "from DimFacility f "
						+ "left join f.emissions e "
						+ "left join e.sector s "
						+ "left join e.gas g "
						+ DaoUtils.basinFilter(basin)
						+ DaoUtils.emitterSubSectorFilter(sectors, EMPTY, EMPTY)
						+ "where s.sectorType = 'E' "
						+ DaoUtils.basinWhereClause(basin)
						+ DaoUtils.emitterWhereClause(query, reportingYear, EMPTY, EMPTY, gases, sectors, qo)
						+ "group by f.id having "
						+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE())
						+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
						+ DaoUtils.basinWhereClause(basin)
						+ DaoUtils.emitterWhereClause(query, reportingYear, EMPTY, EMPTY, gases, sectors, qo)
						+ "and ("
						+ DaoUtils.gasFilter(gases)
						+ "g.gasCode <> 'BIOCO2') and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
						+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
			}
			return qtr;
		}
		
		String generateSupplierQuery(FacilityViewType viewType) {
			return "from DimFacility f "
					+ "join f.facStatus fs "
					+ ((viewType == MAP) ? "left join f.layers l " : "")
					+ "join f.emissions e "
					+ "join e.sector s "
					+ "join e.subSector ss "
					+ "where "
					+ ((viewType == MAP) ? (supplierSector == 32 ? "(l.id.type is null or l.id.type = 'NN') and " : "") : "")
					+ "f.id IN (select distinct f.id "
					+ "from DimFacility f "
					+ "join f.emissions e "
					+ "join e.sector s "
					+ "where s.sectorType = 'S' group by f.id) "
					+ DaoUtils.supplierWhereClause(query, queryOptions(), reportingYear, supplierSector, getState())
					+ "and s.sectorType = 'S' and fs.id.year = " + reportingYear + " and fs.facilityType = 'S' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
		}
		
		// Geologic Sequestration of CO2 datatype (Subpart RR)
		String generateRrCo2Query(FacilityViewType viewType) {
			String state = getState();
			QueryOptions qo = queryOptions();
			String countyFips = countyFips();
			GasFilter gases = gases();
			String lowE = lowE();
			return "from DimFacility f "
					+ "join f.facStatus fs "
					+ "left join f.emissions e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ "where f.id IN (select distinct f.id "
					+ "from DimFacility f "
					+ "left join f.emissions e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ "where s.sectorType = 'I' "
					+ DaoUtils.rrCo2WhereClause(query, reportingYear, state, countyFips, qo)
					+ "group by f.id having "
					+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
					+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
					+ DaoUtils.rrCo2WhereClause(query, reportingYear, state, countyFips, qo)
					+ "and g.gasCode <> 'BIOCO2' and s.sectorType = 'I' and fs.id.year = " + reportingYear + " and fs.facilityType = 'A' "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id";
		}
		
		String generatePipeQuery(FacilityViewType viewType) {
			SectorFilter sectors = sectors();
			GasFilter gases = gases();
			QueryOptions qo = queryOptions();
			String state = getState();
			String countyFips = countyFips();
			String lowE = lowE();
			String highE = highE();
			return "from DimFacilityPipe f "
					+ "join f.facStatus fs "
					+ "left join f.emPipe e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where f.id IN (select distinct f.id "
					+ "from DimFacilityPipe f "
					+ "left join f.emPipe e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where s.sectorType = 'E' "
					+ DaoUtils.emitterAllWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
					+ "group by f.id having "
					+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
					+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
					+ DaoUtils.emitterAllWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
					+ "and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
					+ "and f.stateName = e.state "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by f.id, f.state";
		}
		
		String generatePipeDashboardQuery() {
			SectorFilter sectors = sectors();
			GasFilter gases = gases();
			QueryOptions qo = queryOptions();
			String state = getState();
			String countyFips = countyFips();
			String lowE = lowE();
			String highE = highE();
			return "select s.sectorCode, sum(e.co2eEmission), count(distinct f.id.facilityId) "
					+ "from DimFacilityPipe f "
					+ "join f.facStatus fs "
					+ "left join f.emPipe e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where f.id IN (select distinct f.id "
					+ "from DimFacilityPipe f "
					+ "left join f.emPipe e "
					+ "left join e.sector s "
					+ "left join e.gas g "
					+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
					+ "where s.sectorType = 'E' "
					+ DaoUtils.emitterAllWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
					+ "group by f.id having "
					+ DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
					+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
					+ DaoUtils.emitterAllWhereClause(query, reportingYear, state, countyFips, gases, sectors, qo)
					+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
					+ "and g.gasCode <> 'BIOCO2' and s.sectorType = 'E' and fs.id.year = " + reportingYear + " and fs.facilityType = 'E' "
					+ "and f.stateName = e.state "
					+ ReportingStatusQueryFilter.filter(reportingStatus, reportingYear) + " group by s.sectorCode";
		}
	}
}
