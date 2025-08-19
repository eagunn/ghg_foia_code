package gov.epa.ghg.service;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimFacilityDaoInterface;
import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.DimSubSectorDao;
import gov.epa.ghg.dao.FacilityViewDAO;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.service.view.list.viewmaker.ListViewMaker;
import gov.epa.ghg.service.view.list.viewmaker.ViewMakerFactory;
import gov.epa.ghg.util.AppConstants;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

/**
 * Created by alabdullahwi on 2/9/2016.
 */

@Log4j2
@Service
@Transactional
public class ListChartService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	ViewMakerFactory viewMakerFactory;
	
	@Inject
	DimFacilityDaoInterface facilityDao;
	
	@Inject
	DimSubSectorDao subSectorDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimStateDao stateDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	BasinLayerDAO basinDao;
	
	@Inject
	DimMsaDao msaDao;
	
	@Inject
	FacilityViewDAO facilityViewDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	@Inject
	ModeDomainResolver modeDomainResolver;
	
	public static Long startYear;
	public static Long endYear;
	
	private static NumberFormat df = NumberFormat.getInstance();
	private static String TOTAL_CO2_INJECTED = "Total CO2 Received for Injection";
	private static String TOTAL_REPORTED_EMISSIONS = "Total Reported Emissions";
	
	@Resource(name = "endYear")
	public void setEndYear(Long endYear) {
		this.endYear = endYear;
	}
	
	@Resource(name = "startYear")
	public void setStartYear(Long startYear) {
		this.startYear = startYear;
	}
	
	public JSONObject listChartEmitterSector(FlightRequest request) {
		
		log.info("listChartEmitterSector started...");
		
		boolean isLDC = !("E".equals(request.getDataSource()) || "P".equals(request.getDataSource()) || "T".equals(request.getDataSource()));
		
		// LOAD DATA
		List<Object[]> data = null;
		if (isLDC) {
			data = pubFactsDao.getListChartLdcSectorAggregate(request);
		} else {
			if ("TL".equals(request.getState()) && request.getTribalLandId() == null) {
				data = pubFactsDao.getTribalLandSectorAggregate(request);
			} else {
				if (request.isPipe()) {
					data = pubFactsDao.getListPipeGeography(request);
				} else {
					data = pubFactsDao.getListChartEmitterSectorAggregate(request);
				}
			}
		}
		
		// SETUP VIEW DATA
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		String domain = modeDomainResolver.resolveDomain(request);
		
		// SET LABELS
		List<String> labels = null;
		if (!isLDC) {
			// full label list
			labels = this.createSectorLabelList();
		} else {
			labels = new ArrayList<String>();
			labels.add("Petroleum and Natural Gas Systems");
		}
		
		ListViewMaker listViewMaker = null;
		if (request.isPipe()) {
			listViewMaker = viewMakerFactory.createPipeGeographyInstance(data, mode, labels);
		} else {
			listViewMaker = viewMakerFactory.createEmitterSectorInstance(data, mode, labels);
		}
		// unit will be created later by a call to ServiceUtils.getUnit inside the viewmaker, hence its absence here
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("mode", mode);
		args.put("domain", domain);
		args.put("year", request.getReportingYear());
		args.put("labels", labels);
		args.put("df", df);
		args.put("isNotLDC", !isLDC);
		
		return listViewMaker.createView(args);
		
	}
	
	public JSONObject listChartEmitterFacilities(FlightRequest request) {
		
		// UNROLL REQUEST
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		String facType = "E";
		String totalLabel = TOTAL_REPORTED_EMISSIONS;
		List<String> labels = null;
		
		// LOAD DATA
		List<Object[]> data;
		if (type == FacilityType.CO2_INJECTION) {
			data = pubFactsDao.getListChartCO2InjectionFacilities(request);
			
			facType = "I";
			totalLabel = TOTAL_CO2_INJECTED;
			labels = new ArrayList<String>();
			labels.add(totalLabel);
		} else if (type == FacilityType.RR_CO2) {
			data = pubFactsDao.getListRRCO2Facilities(request);
			facType = "A";
			labels = new ArrayList<String>();
			labels.add(totalLabel);
		} else {
			if (request.isBoosting()) {
				data = pubFactsDao.getListChartBasinFacilities(request);
			} else if (request.isPipe()) {
				data = pubFactsDao.getListPipeFacilities(request);
			}
            /*else if (request.isWholePetroNg()) {
            	data = pubFactsDao.getListPetroNgFacilities(request);
            }*/
			else {
				data = pubFactsDao.getListChartEmitterFacilities(request);
			}
			if (type == FacilityType.SUPPLIERS) {
				facType = "S";
			}
			labels = this.createSectorLabelList();
		}
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
		
		// SETUP VIEW DATA
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		String domain = modeDomainResolver.resolveDomain(request);
		
		ListViewMaker listViewMaker = null;
		if (request.isPipe() /*|| request.isWholePetroNg()*/) {
			listViewMaker = viewMakerFactory.createPipeInstance(data, type, rsMap, request.getCurrentYear());
		} else {
			listViewMaker = viewMakerFactory.createEmitterInstance(data, type, rsMap, request.getReportingYear());
		}
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("mode", mode);
		args.put("domain", domain);
		args.put("unit", AppConstants.MT);
		args.put("year", request.getReportingYear());
		args.put("df", df);
		args.put("type", type);
		args.put("labels", labels);
		args.put("totalLabel", totalLabel);
		
		return listViewMaker.createView(args);
	}
	
	public JSONObject listChartBasinFacilities(FlightRequest request) {
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		String facType = "E";
		if (type == FacilityType.SUPPLIERS) {
			facType = "S";
		} else if (type == FacilityType.CO2_INJECTION) {
			facType = "I";
		} else if (type == FacilityType.RR_CO2) {
			facType = "A";
		}
		
		String totalLabel = TOTAL_REPORTED_EMISSIONS;
		
		List<Object[]> results = pubFactsDao.getListChartBasinFacilities(request);
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
		
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		String domain = modeDomainResolver.resolveDomain(request);
		List<String> labels = this.createSectorLabelList();
		
		ListViewMaker listViewMaker = viewMakerFactory.createBasinInstance(results, totalLabel, rsMap, request.getCurrentYear());
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("domain", domain);
		args.put("mode", mode);
		args.put("unit", AppConstants.MT);
		args.put("labels", labels);
		args.put("totalLabel", totalLabel);
		args.put("year", request.getReportingYear());
		args.put("df", df);
		
		return listViewMaker.createView(args);
	}
	
	public JSONObject listChartBasinFacilitiesGeo(FlightRequest request) {
		
		log.info("listChartEmitterSector started...");
		
		// LOAD DATA
		List<Object[]> data = pubFactsDao.getListChartBasinFacilitiesGeo(request);
		
		// DOMAIN AND MODE
		String domain = modeDomainResolver.resolveDomain(request);
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		
		// CRATE LABELS
		List<String> labels = new ArrayList<String>();
		labels.add("Petroleum and Natural Gas Systems");
		
		// SETUP VIEW DATA
		ListViewMaker listViewMaker = viewMakerFactory.createBasinGeoInstance(data, labels, mode);
		
		// unit is added later inside the viewmaker
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("domain", domain);
		args.put("mode", mode);
		args.put("year", request.getReportingYear());
		args.put("labels", labels);
		args.put("df", df);
		
		return listViewMaker.createView(args);
	}
	
	public JSONObject listChartSuppliers(FlightRequest request) {
		
		log.info("listChartSuppliers started...");
		
		int year = request.getReportingYear();
		int supplierSector = request.getSupplierSector();
		
		List<Object[]> results = null;
		// if sector is picked, load data, otherwise show the view but it should be blank
		if (supplierSector != 0) {
			results = pubFactsDao.getListChartSuppliers(request);
		}
		
		// isTrend = false
		ListViewMaker listViewMaker = viewMakerFactory.createSupplierInstance(results, year, false);
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		args.put("domain", ServiceUtils.getSupplierType(supplierSector));
		args.put("unit", AppConstants.MT);
		args.put("year", year);
		args.put("df", df);
		
		log.info("listChartSuppliers completed...");
		return listViewMaker.createView(args);
		
	}
	
	public JSONObject listChartEmitterGas(FlightRequest request) {
		
		// DATA
		List<Object[]> results = pubFactsDao.getListChartEmitterGasAggregate(request);
		
		// MODE - no domain for gas
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		
		// LABELS (gas)
		List<String> labels = new ArrayList<String>();
		labels.add("Total");
		labels.add("Carbon Dioxide (CO<sub>2</sub>)");
		labels.add("Methane (CH<sub>4</sub>)");
		labels.add("Nitrous Oxide (N<sub>2</sub>O)");
		labels.add("SF<sub>6</sub>");
		labels.add("HFC-23");
		labels.add("NF<sub>3</sub>");
		labels.add("HFCs");
		labels.add("PFCs");
		labels.add("HFEs");
		labels.add("Other");
		
		ListViewMaker listViewMaker = viewMakerFactory.createGasInstance(results, mode, labels);
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		return listViewMaker.createView(args);
	}
	
	public List<String> createSectorLabelList() {
		
		List<String> labels = new ArrayList<String>();
		labels.add("Power Plants");
		labels.add("Petroleum and Natural Gas Systems");
		labels.add("Refineries");
		labels.add("Chemicals");
		labels.add("Other");
		labels.add("Minerals");
		labels.add("Waste");
		labels.add("Metals");
		labels.add("Pulp and Paper");
		labels.add("Total Reported Emissions");
		
		return labels;
	}
}
