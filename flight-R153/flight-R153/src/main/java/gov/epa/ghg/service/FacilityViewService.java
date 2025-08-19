package gov.epa.ghg.service;

import static gov.epa.ghg.enums.FacilityViewType.EXPORT;
import static gov.epa.ghg.enums.FacilityViewType.LIST;
import static gov.epa.ghg.enums.FacilityViewType.MAP;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimFacilityDao;
import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.FacilityViewDaoInterface;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.domain.FacilityView;
import gov.epa.ghg.domain.FacilityViewSub;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.dto.Basin;
import gov.epa.ghg.dto.ExportAllData;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityExport;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.dto.view.PipeList;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.view.transformer.FacilityExportTransformer;
import gov.epa.ghg.service.view.transformer.FacilityListTransformer;
import gov.epa.ghg.service.view.transformer.FacilityViewSubTransformer;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class FacilityViewService implements Serializable, FacilityViewInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	FacilityViewDaoInterface facilityViewDao;
	
	@Inject
	FacilityExportTransformer facilityExportTransformer;
	
	@Inject
	FacilityListTransformer facilityListTransformer;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimFacilityDao dimFacilityDao;
	
	@Inject
	FacilityViewSubTransformer facilityViewSubTransformer;
	
	@Inject
	DimStateDao stateDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	public int getStoppedReportingFacilitiesCount(FlightRequest request) {
		String realReportingStatus = request.getReportingStatus();
		request.setReportingStatus("STOPPED_REPORTING");
		int retv = this.getTotalCount(request);
		request.setReportingStatus(realReportingStatus);
		return retv;
	}
	
	@Override
	public int getStoppedReportingFacilitiesCount(int page, String q, int year, String stateCode, String fipsCode, String msaCode, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, int is, String emissionsType, Long tribalLandId) {
		return getFacilityViewList(page, q, year, stateCode, fipsCode, msaCode, basin, "0", "0", gases, sectors, qo, sectorType, sc, sortOrder, is, "STOPPED_REPORTING", emissionsType, tribalLandId)
				.getTotalCount();
	}
	
	public List<FacilityViewSub> getFacilityEmissions(FlightRequest request) {
		List<DimFacility> facilities = dimFacilityDao.loadDimFacilities(request, MAP);
		String selectionQuery = request.generateQuery(MAP, false);
		Map<Long, BigDecimal> emissionMap = dimFacilityDao.getFacilityIdAndEmissions(selectionQuery, MAP);
		return facilityViewSubTransformer.transformBubbleMap(facilities, emissionMap);
	}
	
	public List<FacilityViewSub> getFacilityGeoData(FlightRequest request) {
		List<FacilityViewSub> retv = new ArrayList<FacilityViewSub>();
		FacilityType facilityType = FacilityType.fromDataSource(request.getDataSource());
		SectorFilter sectors = request.sectors();
		String state = request.getState();
		String basin = request.getBasin();
		// load data
		StopWatch sw = new StopWatch();
		sw.start("LoadDimFacilities");
		List<DimFacility> dimFacilities = dimFacilityDao.loadDimFacilities(request, MAP);
		sw.stop();
		String facType = "E";
		if ("S".equals(request.getDataSource())) {
			facType = "S";
		} else if ("I".equals(request.getDataSource())) {
			facType = "I";
		} else if ("A".equals(request.getDataSource())) {
			facType = "A";
		}
		sw.start("GetAllFacilitybyYearType");
		Map<Long, ReportingStatus> rsMap =
				dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
		sw.stop();
		// transform data into view-data
		switch (facilityType) {
			case SUPPLIERS:
				if (request.getSupplierSector() != 0) {
					retv = facilityViewSubTransformer.transformSupplier(dimFacilities, request.getSupplierSector(), rsMap);
				}
				break;
			case ONSHORE:
				if (sectors.isOnshorePetroleumSectorOnly()) {
					retv = facilityViewSubTransformer.transformOnshore(dimFacilities, basin, rsMap);
				}
				break;
			case CO2_INJECTION:
			case RR_CO2:
				retv = facilityViewSubTransformer.transformCo2Injection(dimFacilities, rsMap);
			default:
				if (request.isBoosting()) {
					retv = facilityViewSubTransformer.transformBasin(dimFacilities, basin, rsMap);
				} else if (request.isPipe()) {
					// List<DimFacilityPipe> pipeFacilities = dimFacilityDao.loadPipeFacilities(request, MAP );
					// retv = facilityViewSubTransformer.transformPipeEmitter(pipeFacilities, state, rsMap);
					List<Object[]> dimPipes = pubFactsDao.getListPipeFacilities(request);
					retv = facilityViewSubTransformer.transformPipeEmitter(dimPipes, state, rsMap);
				} else if ("F".equals(request.getDataSource())) {
					retv = facilityViewSubTransformer.transformSf6(dimFacilities, request.getDataSource(), request.sectors(), rsMap);
				} else {
					sw.start("TransformEmitter");
					retv = facilityViewSubTransformer.transformEmitter(dimFacilities, request.getDataSource(), request.sectors(), rsMap);
					sw.stop();
				}
			
		}
		log.debug("FacilityViewService: " + sw.prettyPrint());
		return retv;
	}
	
	public List<FacilityExport> getExportData(FlightRequest request) throws Exception {
		List<FacilityExport> retv;
		if (request.isTrendRequest()) {
			List<Object[]> data = pubFactsDao.getTrendListChart(request);
			retv = facilityExportTransformer.transformTrend(data, request);
		} else {
			retv = exportAllReportingYearsData(request);
		}
		return retv;
	}
	
	public List<FacilityExport> exportAllReportingYearsData(FlightRequest request) throws Exception {
		List<FacilityExport> retv = new ArrayList<>();
		List<DimFacility> dimFacilities = dimFacilityDao.loadDimFacilities(request, EXPORT);
		String queryString;
		//retrieveFacilityType().name()
		if (!dimFacilities.isEmpty()) {
			if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(request.retrieveFacilityType().name(), FacilityType.EMITTERS.toString())) {
				queryString = request.generateQuery(EXPORT, true);
				queryString = queryString.replaceFirst("SELECT", "SELECT sum(emissions3_.CO2E_EMISSION) AS CO2E_EMISSION,");
				ExportAllData allData = dimFacilityDao.getFacilityIdAndEmissionsSql(queryString, EXPORT, request);
				Map<Long, BigDecimal> facilityEmissions = allData.getEm();
				Map<Long, Long> repYears = allData.getRepYears();
				retv = facilityExportTransformer.transform(dimFacilities, facilityEmissions, repYears, request.retrieveFacilityType());
			} else {
				queryString = request.generateQuery(EXPORT, false);
				Map<Long, BigDecimal> facilityEmissions = dimFacilityDao.getFacilityIdAndEmissions(queryString, EXPORT);
				Map<Long, Long> repYears = new HashMap<>();
				List<DimFacilityId> dimFacilityIds = dimFacilityDao.getDimFacilityIds(queryString);
				for (DimFacilityId dfi : dimFacilityIds) {
					repYears.put(dfi.getFacilityId(), dfi.getYear());
				}
				retv = facilityExportTransformer.transform(dimFacilities, facilityEmissions, repYears, request.retrieveFacilityType());
			}
		}
		return retv;
	}
	
	public int getTotalCount(FlightRequest request) {
		return dimFacilityDao.getTotalCount(request);
	}
	
	public int getTotalCountMinusStoppedReporting(FlightRequest request) {
		int retv = this.getTotalCount(request);
		if ("ALL".equals(request.getReportingStatus()) && ReportingStatusQueryFilter.isReportingStatusEnabled(request.getReportingYear())) {
			int numFacilitiesStoppedReporting = this.getStoppedReportingFacilitiesCount(request);
			retv = retv - numFacilitiesStoppedReporting;
		}
		return retv;
	}
	
	public FacilityList getFacilityViewList(FlightRequest request) {
		FacilityType facilityType = request.retrieveFacilityType();
		final boolean isLimited = true;
		FacilityList retv = new FacilityList();
		List<DimFacility> dimFacilities = dimFacilityDao.loadDimFacilities(request, LIST, isLimited);
		String facType = "E";
		if ("S".equals(request.getDataSource())) {
			facType = "S";
		} else if ("I".equals(request.getDataSource())) {
			facType = "I";
		} else if ("A".equals(request.getDataSource())) {
			facType = "A";
		}
		if (!dimFacilities.isEmpty()) {
			String queryString = request.generateQuery(LIST, false);
			Map<Long, BigDecimal> facilityEmissions = dimFacilityDao.getFacilityIdAndEmissions(queryString, LIST);
			Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
			retv = facilityListTransformer.transform(dimFacilities, facilityEmissions, request.getReportingYear(), facilityType, request.getSortOrder(), rsMap);
		}
		return retv;
	}
	
	public List<FacilityViewSub> getPipeFacilityEmissions(FlightRequest request) {
		List<DimFacilityPipe> facilities = dimFacilityDao.loadPipeFacilities(request, MAP);
		String selectionQuery = request.generatePipeQuery(MAP);
		Map<String, Map<String, BigDecimal>> keyMap = dimFacilityDao.getPipeEmissions(selectionQuery, MAP);
		return facilityViewSubTransformer.transformPipeBubbleMap(facilities, keyMap);
	}
	
	public PipeList getPipePanelList(FlightRequest request) {
		FacilityType facilityType = request.retrieveFacilityType();
		PipeList retv = new PipeList();
		List<Object[]> dimFacilities = pubFactsDao.getListPipeFacilities(request);
		String facType = "E";
		Map<String, DimFacilityPipe> facilityMap = new HashMap<>();
		int totalCount = dimFacilities.size();
		for (Object[] result : dimFacilities) {
			String key;
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			Long facilityId = (Long) result[2];
			String facCity = ServiceUtils.nullSafeHtmlUnescape((String) result[3]);
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
			String facZip = ServiceUtils.nullSafeHtmlUnescape((String) result[5]);
			key = facilityName + " [" + facilityId + "]" + "{" + facState + "}";
			int yr = request.getReportingYear();
			DimFacilityId dimFacId = new DimFacilityId(facilityId, (long) yr);
			if (!facilityMap.containsKey(key)) {
				DimFacilityPipe facility = new DimFacilityPipe();
				facility.setId(dimFacId);
				facility.setFacilityName(facilityName);
				facility.setCity(facCity.split(",")[0]);
				facility.setState(facCity.split(",")[1].trim());
				facility.setZip(facZip + " [" + facState + "]");
				facilityMap.put(key, facility);
			}
		}
		if (totalCount > 0) {
			String queryString = request.generatePipeQuery(LIST);
			Map<String, Map<String, BigDecimal>> keyMap = dimFacilityDao.getPipeEmissions(queryString, LIST);
			Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
			retv = facilityListTransformer.transformPipe(facilityMap, keyMap, request.getReportingYear(), facilityType, request.getSortOrder(), rsMap, totalCount);
		}
		return retv;
	}
	
	@Deprecated
	public FacilityList getFacilityViewList(int page, String q, int year, String stateCode, String fipsCode, String msaCode, String basin, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, int is, String rs, String emissionsType, Long tribalLandId) {
		return null;
	}
	
	public FacilityList getAutoCompleteFacilityList(FlightRequest request) {
		// return dimFacilityDao.loadAutoCompleteFacilities(request);
		return null;
	}
	
	public FacilityList getAutoCompleteFacilityList(String q, int year, String stateCode, String fipsCode, String msaCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, String rs, String emissionsType, Long tribalLandId) {
		if (StringUtils.hasLength(sectorType) && "S".equals(sectorType)) {
			if (sc != 0) {
				return facilityViewDao.getAutoCompleteSupplierList(q, qo, year, sc, sortOrder, rs);
			} else {
				return new FacilityList();
			}
		} else {
			return facilityViewDao.getAutoCompleteEmitterList(q, year, stateCode, fipsCode, msaCode, lowE, highE,
					gases, sectors, qo,
					sortOrder, rs, emissionsType, tribalLandId);
		}
	}
	
	public String getCountyName(String fipsCode) {
		DimCounty county = countyDao.findById(fipsCode);
		if (county != null) {
			return county.getCountyName();
		}
		return "";
	}
	
	public int getFips(String state, String county) {
		return countyDao.getFips(state, county);
	}
	
	public int getTribalLandId(String tribalLandName) {
		return tribalLandsDao.getIdByName(tribalLandName);
	}
	
	public List<DimState> getStates() {
		return stateDao.getStates();
	}
	
	public List<DimCounty> getFacilityCounties(String stateCode, int year) {
		return facilityViewDao.getFacilityCounties(stateCode, year);
	}
	
	public List<Basin> getBasins() {
		return basinLayerDao.getBasins();
	}
	
	public List<DimMsa> getMsas(String stateCode) {
		return facilityViewDao.getMsas(stateCode);
	}
	
	public List<LuTribalLands> getTribalLands() {
		return tribalLandsDao.getTribalLands();
	}
	
	class FacilityViewComparator implements Comparator<FacilityView> {
		@Override
		public int compare(FacilityView fv1, FacilityView fv2) {
			if (fv1.getFacilityName().compareToIgnoreCase(fv2.getFacilityName()) < 0) {
				return -1;
			} else {
				return 1;
			}
		}
		
	}
}
