package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.dao.TrendListDAO;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.domain.PubBasinFacility;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.view.list.ColumnType;
import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.util.AppConstants;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Created by lee@saic October 2016.
 */

@Log4j2
@Service
@Transactional
public class ExportListService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static NumberFormat df = NumberFormat.getInstance();
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	TrendListDAO trendListDAO;
	
	@Inject
	DimMsaDao msaDao;
	
	@Inject
	BasinLayerDAO basinDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Inject
	ExportService exportService;
	
	@Inject
	ListChartService listChartService;
	
	@Inject
	TrendListService trendListService;
	
	@Inject
	ModeDomainResolver modeDomainResolver;
	
	@Resource(name = "startYear")
	Long startYear;
	
	@Resource(name = "endYear")
	Long endYear;
	
	public Workbook exportToExcel(FlightRequest request, boolean listGeo) {
		
		Workbook wb = new HSSFWorkbook();
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		
		String sheetTitle = "Total Reported Emissions by Facility, by Year in Metric Tons of CO2e";
		if (ModeDomainResolver.Mode.STATE == mode) {
			sheetTitle = "Total Reported Emissions by State|Sector in Million Metric Tons of CO2e";
		} else if (ModeDomainResolver.Mode.COUNTY == mode) {
			sheetTitle = "Total Reported Emissions by County|Sector in Metric Tons of CO2e";
		} else if (ModeDomainResolver.Mode.BASIN == mode) {
			sheetTitle = "Total Reported Emissions by Basin|Sector in Metric Tons of CO2e";
		}
		
		// UNROLL REQUEST
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		boolean isLDC = !("E".equals(request.getDataSource()) || "P".equals(request.getDataSource()));
		
		// LOAD DATA
		List<Object[]> results = null;
		if (listGeo) {
			if (ModeDomainResolver.Mode.BASIN == mode) {
				results = pubFactsDao.getListChartBasinFacilitiesGeo(request);
			} else {
				if (request.isPipe()) {
					results = pubFactsDao.getListPipeGeography(request);
				} else {
					results = pubFactsDao.getListChartEmitterSectorAggregate(request);
				}
			}
			
			if (request.isPipe()) {
				sheetTitle = "Total Reported Emissions by State|Sector in Metric Tons of CO2e";
				this.createGeoPipeWorksheet(wb, sheetTitle, request, results, mode);
			} else {
				this.createGeoWorksheet(wb, sheetTitle, request, results, mode, isLDC);
			}
		} else {
			results = pubFactsDao.getTrendListChart(request);
			
			Map<Long, DimFacility> dfMap = trendListDAO.getFacilityByYear(request.getCurrentYear());
			Map<Long, PubBasinFacility> basinMap = null;
			if (ModeDomainResolver.Mode.BASIN == mode) {
				basinMap = trendListDAO.getBasinFacilityByYear(request.getCurrentYear());
			}
			
			this.createWorksheet(wb, sheetTitle, request, results, dfMap, basinMap);
		}
		
		return wb;
	}
	
	public List<String> createHeaders(FacilityType type, String msaCode, int stateLevel, Set<Long> years) {
		
		List<String> headers = new ArrayList<String>();
		
		headers.add(ColumnType.FACILITY.getName().toUpperCase());
		headers.add("GHGRP ID");
		if (type == FacilityType.ONSHORE) {
			headers.add(ColumnType.BASIN.getName().toUpperCase());
		}
		headers.add("REPORTED ADDRESS");
		headers.add("LATITUDE");
		headers.add("LONGITUDE");
		headers.add(ColumnType.CITY.getName().toUpperCase());
		if (StringUtils.hasLength(msaCode) || stateLevel == 1) {
			headers.add("METRO AREA NAME");
		} else {
			headers.add(ColumnType.COUNTY.getName().toUpperCase());
		}
		headers.add(ColumnType.STATE.getName().toUpperCase());
		headers.add("ZIP CODE");
		headers.add("PARENT COMPANIES");
		headers.add("SUBPARTS");
		for (Long year : years) {
			headers.add(ColumnType.EMISSION_YEAR.getName().toUpperCase() + year);
		}
		headers.add(ColumnType.CHANGE_EMISSIONS_ENDYEAR.getName().toUpperCase());
		headers.add(ColumnType.CHANGE_EMISSIONS_ALLYEARS.getName().toUpperCase());
		headers.add(ColumnType.SECTOR.getName().toUpperCase());
		
		return headers;
		
	}
	
	private Sheet createWorksheet(Workbook wb, String sheetTitle, FlightRequest request, List<Object[]> results, Map<Long, DimFacility> dfMap, Map<Long, PubBasinFacility> basinMap) {
		
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		Sheet ws = wb.createSheet(sheetTitle);
		
		Set<Long> years = trendListService.buildTimeline(request.getCurrentYear());
		
		Integer rowIndex = 0;
		rowIndex = exportService.createPreHeader(ws, rowIndex, exportService.createSearchParameters(request, true), false);
		
		// do rowIndex++ twice because we need to leave a blank row between headers and pre-headers
		rowIndex++;
		Row row = ws.createRow(rowIndex++);
		
		// create header
		int colIndex = 0;
		List<String> headers = this.createHeaders(type, request.msaCode(), request.getStateLevel(), years);
		for (String header : headers) {
			row.createCell(colIndex++).setCellValue(header);
		}
		
		Map<String, Map<String, Object>> facilityMap = this.getFacilityListMap(results, request);
		
		for (String facKey : facilityMap.keySet()) {
			
			Map<String, Object> dataMap = facilityMap.get(facKey);
			
			// emissions
			Map<Long, BigDecimal> emissionsMap = (Map<Long, BigDecimal>) dataMap.get("emissions");
			List<String> arrayEmissions = new ArrayList<String>();
			for (Long year : years) {
				String vEmissions = "---";
				if (emissionsMap.get(year) != null) {
					vEmissions = df.format(ServiceUtils.convert(emissionsMap.get(year), AppConstants.MT));
				}
				arrayEmissions.add(vEmissions);
			}
			String emEndyear = "---";
			if (emissionsMap.get(endYear - 1) != null && emissionsMap.get(endYear) != null) {
				emEndyear = df.format(ServiceUtils.convert(emissionsMap.get(endYear).subtract(emissionsMap.get(endYear - 1)), AppConstants.MT));
			}
			String emAllyear = "---";
			if (emissionsMap.get(startYear) != null && emissionsMap.get(endYear) != null) {
				emAllyear = df.format(ServiceUtils.convert(emissionsMap.get(endYear).subtract(emissionsMap.get(startYear)), AppConstants.MT));
			}
			
			// sectors
			List<String> sectorList = (ArrayList<String>) dataMap.get("sectors");
			String sectorString = "";
			for (String sector : sectorList) {
				if (StringUtils.hasText(sectorString)) {
					sectorString += ", ";
				}
				sectorString += sector;
			}
			
			// additional columns
			Long facilityId = (Long) dataMap.get("ghgid");
			String facAddress = "";
			String facLatitude = "";
			String facLongitude = "";
			String facCounty = "";
			String facZip = "";
			String facParent = "";
			String facSubparts = "";
			String facBasin = "";
			String facMsa = "";
			
			DimFacility df = dfMap.get(facilityId);
			if (df != null) {
				facAddress = (df.getAddress1() != null ? df.getAddress1() : "");
				facAddress += (df.getAddress2() != null ? " " + df.getAddress2() : "");
				facLatitude = (df.getLatitude() != null ? df.getLatitude().toString() : "");
				facLongitude = (df.getLongitude() != null ? df.getLongitude().toString() : "");
				facCounty = (df.getCounty() != null ? df.getCounty() : "");
				facZip = (df.getZip() != null ? df.getZip() : "");
				facParent = (df.getParentCompany() != null ? df.getParentCompany() : "");
				facSubparts = (df.getReportedSubparts() != null ? df.getReportedSubparts() : "");
			}
			if (type == FacilityType.ONSHORE) {
				PubBasinFacility bf = basinMap.get(facilityId);
				if (bf != null) {
					facBasin = (bf.getLayer().getBasin() != null ? bf.getLayer().getBasin() : "");
				}
			}
			if (StringUtils.hasLength(request.msaCode()) || request.getStateLevel() == 1) {
				facMsa = msaDao.getMsaByCode(request.msaCode()).getCbsa_title();
			}
			
			colIndex = 0;
			row = ws.createRow(rowIndex++);
			row.createCell(colIndex++).setCellValue(dataMap.get("name").toString());
			row.createCell(colIndex++).setCellValue(facilityId);
			if (type == FacilityType.ONSHORE) {
				row.createCell(colIndex++).setCellValue(facBasin);
			}
			row.createCell(colIndex++).setCellValue(facAddress);
			row.createCell(colIndex++).setCellValue(facLatitude);
			row.createCell(colIndex++).setCellValue(facLongitude);
			row.createCell(colIndex++).setCellValue(dataMap.get("city").toString());
			if (StringUtils.hasLength(request.msaCode()) || request.getStateLevel() == 1) {
				row.createCell(colIndex++).setCellValue(facMsa);
			} else {
				row.createCell(colIndex++).setCellValue(facCounty);
			}
			row.createCell(colIndex++).setCellValue(dataMap.get("state").toString());
			row.createCell(colIndex++).setCellValue(facZip);
			row.createCell(colIndex++).setCellValue(facParent);
			row.createCell(colIndex++).setCellValue(facSubparts);
			for (String emissions : arrayEmissions) {
				row.createCell(colIndex++).setCellValue(emissions);
			}
			row.createCell(colIndex++).setCellValue(emEndyear);
			row.createCell(colIndex++).setCellValue(emAllyear);
			row.createCell(colIndex++).setCellValue(sectorString);
		}
		
		return ws;
	}
	
	/**
	 * cloned from ViewMakerFactory.createEmitterInstance
	 */
	public Map<String, Map<String, Object>> getFacilityListMap(List<Object[]> results, FlightRequest request) {
		
		Map<String, Map<String, Object>> facilityMap = new LinkedHashMap<String, Map<String, Object>>();
		
		for (Object[] result : results) {
			
			String facilityName = (String) result[0];
			String facSector = (String) result[1];
			BigDecimal facEmissions = (BigDecimal) result[2];
			if (facEmissions == null) {
				facEmissions = BigDecimal.ZERO;
			}
			Long facilityId = (Long) result[3];
			String facCity = (result[4] != null ? (String) result[4] : "");
			String facState = (result[5] != null ? (String) result[5] : "");
			Long facEmissionYear = (Long) result[6];
			
			String facKey = facilityName + " [" + facilityId + "]";
			if (!facilityMap.containsKey(facKey)) {
				facilityMap.put(facKey, new HashMap<String, Object>());
			}
			
			Map<String, Object> dataMap = facilityMap.get(facKey);
			
			// emissions
			Map<Long, BigDecimal> emissionsMap = (Map<Long, BigDecimal>) dataMap.get("emissions");
			if (emissionsMap == null) {
				emissionsMap = new HashMap<Long, BigDecimal>();
			}
			if (emissionsMap.get(facEmissionYear) == null) {
				emissionsMap.put(facEmissionYear, facEmissions);
			} else {
				if (emissionsMap.get(facEmissionYear) != null && facEmissions != null) {
					emissionsMap.put(facEmissionYear, emissionsMap.get(facEmissionYear).add(facEmissions));
				}
			}
			// sectors
			List<String> sectorList = (List<String>) dataMap.get("sectors");
			if (sectorList == null) {
				sectorList = new ArrayList<String>();
			}
			if (!sectorList.contains(facSector)) {
				sectorList.add(facSector);
			}
			
			dataMap.put("ghgid", facilityId);
			dataMap.put("name", facilityName);
			dataMap.put("city", facCity);
			dataMap.put("state", facState);
			dataMap.put("emissions", emissionsMap);
			dataMap.put("sectors", sectorList);
			
			facilityMap.put(facKey, dataMap);
		}
		
		return facilityMap;
	}
	
	public List<String> createGeoHeaders(FlightRequest request) {
		
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		boolean isLDC = !("E".equals(request.getDataSource()) || "P".equals(request.getDataSource()));
		
		List<String> headers = new ArrayList<String>();
		
		if (ModeDomainResolver.Mode.STATE == mode) {
			headers.add(ColumnType.STATE.getName());
		} else if (ModeDomainResolver.Mode.COUNTY == mode) {
			headers.add(ColumnType.COUNTY.getName());
		} else if (ModeDomainResolver.Mode.TRIBAL_LAND == mode) {
			headers.add(ColumnType.TRIBAL_LAND.getName());
		} else if (ModeDomainResolver.Mode.BASIN == mode) {
			headers.add(ColumnType.BASIN.getName());
		} else {
			headers.add(ColumnType.FACILITY.getName());
		}
		
		if (!isLDC) {
			headers.add(ColumnType.POWER_PLANT.getName());
			headers.add(ColumnType.REFINERIES.getName());
			headers.add(ColumnType.CHEMICALS.getName());
			headers.add(ColumnType.OTHER.getName());
			headers.add(ColumnType.MINERALS.getName());
			headers.add(ColumnType.WASTE.getName());
			headers.add(ColumnType.METALS.getName());
			headers.add(ColumnType.PULP.getName());
			headers.add(ColumnType.TOTAL_LABEL.getName());
		} else if (ModeDomainResolver.Mode.BASIN == mode) {
			headers.add(ColumnType.PETROLEUM.getName());
		}
		
		return headers;
		
	}
	
	private Sheet createGeoWorksheet(Workbook wb, String sheetTitle, FlightRequest request, List<Object[]> results, ModeDomainResolver.Mode mode, boolean isLDC) {
		
		Sheet ws = wb.createSheet(sheetTitle);
		
		Integer rowIndex = 0;
		rowIndex = exportService.createPreHeader(ws, rowIndex, exportService.createSearchParameters(request, false), false);
		
		// do rowIndex++ twice because we need to leave a blank row between headers and pre-headers
		rowIndex++;
		Row row = ws.createRow(rowIndex++);
		
		// create header
		int colIndex = 0;
		List<String> headers = this.createGeoHeaders(request);
		for (String header : headers) {
			row.createCell(colIndex++).setCellValue(header);
		}
		
		// SET LABELS
		List<String> labels = null;
		if (!isLDC) {
			// full label list
			labels = listChartService.createSectorLabelList();
		} else if (ModeDomainResolver.Mode.BASIN == mode) {
			labels = new ArrayList<String>();
			labels.add("Petroleum and Natural Gas Systems");
		}
		
		String unit = AppConstants.MT;
		if (ModeDomainResolver.Mode.STATE == mode) {
			unit = AppConstants.MMT;
		}
		
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		if (ModeDomainResolver.Mode.BASIN == mode) {
			keyMap = this.getGeoBasinMap(results, mode);
		} else {
			keyMap = this.getGeoListMap(results, mode);
		}
		
		Map<String, Long> totalEmissions = new HashMap<String, Long>();
		Map<String, Long> totalFacilities = new HashMap<String, Long>();
		for (String key : keyMap.keySet()) {
			Map<String, BigDecimal> sectorMap = keyMap.get(key);
			
			colIndex = 0;
			row = ws.createRow(rowIndex++);
			row.createCell(colIndex++).setCellValue(key);
			
			Long totalEmission = 0L;
			Long count = 0L;
			for (String sector : labels) {
				BigDecimal emission = BigDecimal.ZERO;
				if ("Total Reported Emissions".equals(sector)) {
					for (String emissionKey : sectorMap.keySet()) {
						emission = emission.add(sectorMap.get(emissionKey));
					}
					totalEmission = ServiceUtils.convert(emission, unit);
				} else {
					emission = sectorMap.get(sector);
				}
				
				if (emission == null) {
					emission = BigDecimal.ZERO;
				}
				
				if (totalEmissions.containsKey(sector)) {
					Long te = totalEmissions.get(sector);
					te += ServiceUtils.convert(emission, unit);
					totalEmissions.put(sector, te);
				} else {
					totalEmissions.put(sector, ServiceUtils.convert(emission, unit));
				}
				
				if (totalFacilities.containsKey(sector)) {
					Long tf = totalFacilities.get(sector);
					tf += count;
					totalFacilities.put(sector, tf);
				} else {
					totalFacilities.put(sector, count);
				}
				
				if (isLDC) {
					row.createCell(colIndex++).setCellValue(df.format(ServiceUtils.convert(emission, unit)));
				} else {
					if (!"Petroleum and Natural Gas Systems".equals(sector)) {
						row.createCell(colIndex++).setCellValue(df.format(ServiceUtils.convert(emission, unit)));
					}
				}
			}
		}
		
		return ws;
	}
	
	/**
	 * cloned from ViewMakerFactory.createEmitterSectorInstance
	 */
	public Map<String, Map<String, BigDecimal>> getGeoListMap(List<Object[]> results, ModeDomainResolver.Mode mode) {
		
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<String> labels = listChartService.createSectorLabelList();
		
		for (Object[] result : results) {
			String key = "Unknown";
			if (ModeDomainResolver.Mode.FACILITY == mode) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				key = facilityName + " (" + (Long) result[3] + ")";
			} else {
				if (ModeDomainResolver.Mode.COUNTY == mode) {
					if ((String) result[0] != null) {
						DimCounty dc = countyDao.findById((String) result[0]);
						if (dc != null) {
							key = dc.getCountyName();
						}
					}
				} else if (ModeDomainResolver.Mode.TRIBAL_LAND == mode) {
					if ((Long) result[0] != null) {
						LuTribalLands tl = tribalLandsDao.findById((Long) result[0]);
						if (tl != null) {
							key = tl.getTribalLandName();
						}
					}
				} else {
					if ((String) result[0] != null) {
						key = (String) result[0];
					}
				}
			}
			
			String sector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			Long facilityCount = (Long) result[3]; // in STATE or COUNTY mode only
			if (emission == null) {
				emission = BigDecimal.ZERO;
			}
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (keyMap.containsKey(key)) {
					Map<String, BigDecimal> sectorMap = keyMap.get(key);
					sectorMap.put(sector, emission);
				} else {
					Map<String, BigDecimal> sectorMap = new HashMap<String, BigDecimal>();
					for (String label : labels) {
						sectorMap.put(label, BigDecimal.ZERO);
					}
					sectorMap.put(sector, emission);
					keyMap.put(key, sectorMap);
				}
			}
		}
		
		return keyMap;
	}
	
	/**
	 * cloned from ViewMakerFactory.createBasinGeoInstance
	 */
	public Map<String, Map<String, BigDecimal>> getGeoBasinMap(List<Object[]> results, ModeDomainResolver.Mode mode) {
		
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<String> labels = new ArrayList<String>();
		labels.add("Petroleum and Natural Gas Systems");
		
		for (Object[] result : results) {
			String key = "Unknown";
			
			if (ModeDomainResolver.Mode.FACILITY == mode) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				key = facilityName + " (" + (Long) result[3] + ")";
			} else if (ModeDomainResolver.Mode.BASIN == mode) {
				if ((String) result[0] != null) {
					BasinLayer bl = basinDao.getBasinByCode((String) result[0]);
					if (bl != null) {
						key = bl.getBasin();
					}
				}
			}
			
			String sector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			Long facilityCount = (Long) result[3]; // in STATE or COUNTY mode only
			if (emission == null) {
				emission = BigDecimal.ZERO;
			}
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (keyMap.containsKey(key)) {
					Map<String, BigDecimal> sectorMap = keyMap.get(key);
					sectorMap.put(sector, emission);
				} else {
					Map<String, BigDecimal> sectorMap = new HashMap<String, BigDecimal>();
					for (String label : labels) {
						sectorMap.put(label, BigDecimal.ZERO);
					}
					sectorMap.put(sector, emission);
					keyMap.put(key, sectorMap);
				}
			}
		}
		
		return keyMap;
	}
	
	/**
	 * datatype T: Onshore Gas Transmission Pipelines
	 */
	public List<String> createGeoPipeHeaders(FlightRequest request) {
		
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		
		List<String> headers = new ArrayList<String>();
		
		if (ModeDomainResolver.Mode.STATE == mode) {
			headers.add(ColumnType.STATE.getName());
		} else {
			headers.add(ColumnType.FACILITY.getName());
		}
		headers.add(ColumnType.PETROLEUM.getName());
		
		return headers;
		
	}
	
	private Sheet createGeoPipeWorksheet(Workbook wb, String sheetTitle, FlightRequest request, List<Object[]> results, ModeDomainResolver.Mode mode) {
		
		Sheet ws = wb.createSheet(sheetTitle);
		
		Integer rowIndex = 0;
		rowIndex = exportService.createPreHeader(ws, rowIndex, exportService.createSearchParameters(request, false), false);
		
		// do rowIndex++ twice because we need to leave a blank row between headers and pre-headers
		rowIndex++;
		Row row = ws.createRow(rowIndex++);
		
		// create header
		int colIndex = 0;
		List<String> headers = this.createGeoPipeHeaders(request);
		for (String header : headers) {
			row.createCell(colIndex++).setCellValue(header);
		}
		
		String unit = AppConstants.MT;
		
		Map<String, Map<String, BigDecimal>> keyMap = this.getGeoPipeMap(results, mode);
		
		for (String key : keyMap.keySet()) {
			Map<String, BigDecimal> pipeEmMap = keyMap.get(key);
			
			colIndex = 0;
			row = ws.createRow(rowIndex++);
			row.createCell(colIndex++).setCellValue(key);
			
			BigDecimal emission = null;
			for (String emissionKey : pipeEmMap.keySet()) {
				if (pipeEmMap.get(emissionKey) != null) {
					if (emission == null) {
						emission = BigDecimal.ZERO;
					}
					emission = emission.add(pipeEmMap.get(emissionKey));
				}
			}
			
			row.createCell(colIndex++).setCellValue(df.format(ServiceUtils.convert(emission, unit)));
		}
		
		return ws;
	}
	
	/**
	 * cloned from ViewMakerFactory.createPipeGeographyInstance
	 */
	public Map<String, Map<String, BigDecimal>> getGeoPipeMap(List<Object[]> results, ModeDomainResolver.Mode mode) {
		
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : results) {
			String key = "Unknown";
			String facState = EMPTY;
			
			if (ModeDomainResolver.Mode.STATE == mode) {
				if ((String) result[0] != null) {
					key = (String) result[0];
				}
			} else {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				Long facilityId = (Long) result[2];
				facState = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
				key = facilityName + " [" + facilityId + "]" + "{" + facState + "}";
			}
			
			BigDecimal emission = (BigDecimal) result[1];
			
			if (keyMap.containsKey(key)) {
				Map<String, BigDecimal> pipeEmMap = keyMap.get(key);
				pipeEmMap.put(facState, emission);
			} else {
				Map<String, BigDecimal> pipeEmMap = new HashMap<String, BigDecimal>();
				pipeEmMap.put(facState, emission);
				keyMap.put(key, pipeEmMap);
			}
		}
		
		return keyMap;
	}
}
