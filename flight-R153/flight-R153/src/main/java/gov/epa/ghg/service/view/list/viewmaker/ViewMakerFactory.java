package gov.epa.ghg.service.view.list.viewmaker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.service.view.list.viewmaker.trend.EmitterTrendListViewMaker;
import gov.epa.ghg.service.view.list.viewmaker.trend.SupplierTrendListViewMaker;
import gov.epa.ghg.service.view.transformer.TrendDataTransformer;
import gov.epa.ghg.util.ServiceUtils;

import static gov.epa.ghg.service.view.list.ModeDomainResolver.Mode.BASIN;
import static gov.epa.ghg.service.view.list.ModeDomainResolver.Mode.FACILITY;

/*
 *
 * This class is mainly responsible for transforming the results of a db query into a viewmaker that builds the rows and columns of a list view
 *
 */
@Service
public class ViewMakerFactory {
	
	@Inject
	BasinLayerDAO basinDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Inject
	TrendDataTransformer trendDataTransformer;
	
	@Resource(name = "dataDate")
	private String dataDate;
	
	private static String TOTAL_CO2_INJECTED = "Total CO2 Received for Injection";
	
	public ListViewMaker createGasInstance(List<Object[]> results, ModeDomainResolver.Mode mode, List<String> labels) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			if (result.length > 3) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				key = facilityName + " (" + (Long) result[3] + ")";
				facilityNameLookup.put(key, facilityName);
			} else {
				if (ModeDomainResolver.Mode.COUNTY == mode) {
					if ((String) result[0] != null) {
						DimCounty dc = countyDao.findById((String) result[0]);
						if (dc != null) {
							key = dc.getCountyName();
						} else {
							key = "Unknown";
						}
					} else {
						key = "Unknown";
					}
				} else {
					if ((String) result[0] != null) {
						key = (String) result[0];
					} else {
						key = "Unknown";
					}
				}
			}
			
			String gas = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (keyMap.containsKey(key)) {
					Map<String, BigDecimal> gasMap = keyMap.get(key);
					gasMap.put(gas, emission);
				} else {
					Map<String, BigDecimal> gasMap = new HashMap<String, BigDecimal>();
					for (String label : labels) {
						gasMap.put(label, BigDecimal.ZERO);
					}
					gasMap.put(gas, emission);
					keyMap.put(key, gasMap);
				}
			}
		}
		
		return new GasListViewMaker(facilityNameLookup, keyMap, emissions);
	}
	
	public ListViewMaker createBasinInstance(List<Object[]> results, String totalLabel, Map<Long, ReportingStatus> rsMap, Integer year) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, DimFacility> facilityMap = new HashMap<String, DimFacility>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : results) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			Long facilityId = (Long) result[3];
			key = facilityName + " [" + (Long) result[3] + "]";
			String facCity = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[5]);
			String vIcons = "";
			String facComment = ServiceUtils.nullSafeHtmlUnescape((String) result[6]);
			if (facComment != null) {
				vIcons = vIcons + "<img src='img/co2y.jpg' title='" + facComment + "' alt='" + facComment + "' width='15' height='15' border='0'> ";
			}
			String co2Captured = ServiceUtils.nullSafeHtmlUnescape((String) result[7]);
			if (co2Captured != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2g");
			}
			String co2EmittedSupplied = ServiceUtils.nullSafeHtmlUnescape((String) result[8]);
			if (co2EmittedSupplied != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2b");
			}
			String uuRandDExempt = ServiceUtils.nullSafeHtmlUnescape((String) result[9]);
			if (uuRandDExempt != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2o");
			}
			ReportingStatus rs = rsMap.get(facilityId);
			if (rs != null && rs.getShorthand() != "VALID") {
				String rsTxt = rs.getTextBoxContents();
				rsTxt = rsTxt.replace("'", "&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}
			
			facilityNameLookup.put(key, facilityName);
			if (!facilityMap.containsKey(key)) {
				DimFacility facility = new DimFacility();
				facility.setCity(facCity);
				facility.setState(facState);
				if (facility != null) {
					facilityMap.put(key, facility);
				}
				facility.setComments(vIcons); // PUB-619 special case for jqGrid list display
			}
			
			String sector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			
			if (keyMap.containsKey(key)) {
				Map<String, BigDecimal> sectorMap = keyMap.get(key);
				sectorMap.put(sector, emission);
			} else {
				Map<String, BigDecimal> sectorMap = new HashMap<String, BigDecimal>();
				sectorMap.put(totalLabel, null);
				sectorMap.put(sector, emission);
				keyMap.put(key, sectorMap);
			}
		}
		
		return new BasinListViewMaker(facilityNameLookup, facilityMap, keyMap, totalLabel);
		
	}
	
	public ListViewMaker createBasinGeoInstance(List<Object[]> results, List<String> labels, ModeDomainResolver.Mode mode) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, DimFacility> facilityLookup = new HashMap<String, DimFacility>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		Map<String, Map<String, Long>> countMap = new HashMap<String, Map<String, Long>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			
			if (FACILITY == mode) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				key = facilityName + " (" + (Long) result[3] + ")";
				facilityNameLookup.put(key, facilityName);
			} else {
				if (BASIN == mode) {
					if ((String) result[0] != null) {
						BasinLayer bl = basinDao.getBasinByCode((String) result[0]);
						if (bl != null) {
							key = bl.getBasin();
						} else {
							key = "Unknown";
						}
					} else {
						key = "Unknown";
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
				if (countMap.containsKey(key)) {
					Map<String, Long> facilityCountMap = countMap.get(key);
					facilityCountMap.put(sector, facilityCount);
				} else {
					Map<String, Long> facilityCountMap = new HashMap<String, Long>();
					for (String label : labels) {
						facilityCountMap.put(label, 0L);
					}
					facilityCountMap.put(sector, facilityCount);
					countMap.put(key, facilityCountMap);
				}
			}
		}
		
		return new BasinGeoListViewMaker(facilityNameLookup, keyMap, countMap, emissions);
	}
	
	public ListViewMaker createEmitterInstance(List<Object[]> results, FacilityType type, Map<Long, ReportingStatus> rsMap, Integer year) {
		
		String totalLabel = "Total Reported Emissions";
		if (type == FacilityType.CO2_INJECTION) {
			totalLabel = TOTAL_CO2_INJECTED;
		}
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, DimFacility> facilityMap = new HashMap<String, DimFacility>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : results) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			Long facilityId = (Long) result[3];
			key = facilityName + " [" + facilityId + "]";
			String facCity = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[5]);
			String vIcons = "";
			String facComment = ServiceUtils.nullSafeHtmlUnescape((String) result[6]);
			if (facComment != null) {
				vIcons = vIcons + "<img src='img/co2y.jpg' title='" + facComment + "' alt='" + facComment + "' width='15' height='15' border='0'> ";
			}
			String co2Captured = ServiceUtils.nullSafeHtmlUnescape((String) result[7]);
			if (co2Captured != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2g");
			}
			String co2EmittedSupplied = ServiceUtils.nullSafeHtmlUnescape((String) result[8]);
			if (co2EmittedSupplied != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2b");
			}
			String uuRandDExempt = ServiceUtils.nullSafeHtmlUnescape((String) result[9]);
			if ((type == FacilityType.CO2_INJECTION || type == FacilityType.RR_CO2) && uuRandDExempt != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2o");
			}
			ReportingStatus rs = rsMap.get(facilityId);
			if (rs != null && rs.getShorthand() != "VALID") {
				String rsTxt = rs.getTextBoxContents();
				rsTxt = rsTxt.replace("'", "&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}
			
			facilityNameLookup.put(key, facilityName);
			if (!facilityMap.containsKey(key)) {
				DimFacility facility = new DimFacility();
				facility.setCity(facCity);
				facility.setState(facState);
				facility.setComments(vIcons); // PUB-619 special case for jqGrid list display
				
				if (facility != null) {
					facilityMap.put(key, facility);
				}
			}
			
			String sector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			
			if (keyMap.containsKey(key)) {
				Map<String, BigDecimal> sectorMap = keyMap.get(key);
				sectorMap.put(sector, emission);
			} else {
				Map<String, BigDecimal> sectorMap = new HashMap<String, BigDecimal>();
				sectorMap.put(totalLabel, null);
				sectorMap.put(sector, emission);
				keyMap.put(key, sectorMap);
			}
			
		}
		
		return new EmitterListViewMaker(facilityNameLookup, facilityMap, keyMap);
	}
	
	public ListViewMaker createEmitterSectorInstance(List<Object[]> data, ModeDomainResolver.Mode mode, List<String> labels) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		Map<String, Map<String, Long>> countMap = new HashMap<String, Map<String, Long>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : data) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			if (ModeDomainResolver.Mode.FACILITY == mode) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				key = facilityName + " (" + (Long) result[3] + ")";
				facilityNameLookup.put(key, facilityName);
			} else {
				if (ModeDomainResolver.Mode.COUNTY == mode) {
					if ((String) result[0] != null) {
						DimCounty dc = countyDao.findById((String) result[0]);
						if (dc != null) {
							key = dc.getCountyName();
						} else {
							key = "Unknown";
						}
					} else {
						key = "Unknown";
					}
				} else if (ModeDomainResolver.Mode.TRIBAL_LAND == mode) {
					if ((Long) result[0] != null) {
						LuTribalLands tl = tribalLandsDao.findById((Long) result[0]);
						if (tl != null) {
							key = tl.getTribalLandName();
						} else {
							key = "Unknown";
						}
					} else {
						key = "Unknown";
					}
				} else {
					if ((String) result[0] != null) {
						key = (String) result[0];
					} else {
						key = "Unknown";
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
				if (countMap.containsKey(key)) {
					Map<String, Long> facilityCountMap = countMap.get(key);
					facilityCountMap.put(sector, facilityCount);
				} else {
					Map<String, Long> facilityCountMap = new HashMap<String, Long>();
					for (String label : labels) {
						facilityCountMap.put(label, 0L);
					}
					facilityCountMap.put(sector, facilityCount);
					countMap.put(key, facilityCountMap);
				}
			}
		}
		
		return new EmitterSectorListViewMaker(facilityNameLookup, keyMap, countMap, emissions);
	}
	
	public ListViewMaker createTrendEmitterInstance(List<Object[]> results, FacilityType type, Map<Long, ReportingStatus> rsMap, Integer year) {
		
		Map<String, Map<String, Object>> facilityMap = new LinkedHashMap<String, Map<String, Object>>();
		
		for (Object[] result : results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String sector = (String) result[1];
			BigDecimal emissions = (BigDecimal) result[2];
			if (emissions == null) {
				emissions = BigDecimal.ZERO;
			}
			Long facilityId = (Long) result[3];
			String facCity = (String) result[4];
			String facState = (String) result[5];
			Long emissionYear = (Long) result[6];
			String vIcons = "";
			String facComment = ServiceUtils.nullSafeHtmlUnescape((String) result[7]);
			if (facComment != null) {
				vIcons = vIcons + "<img src='img/co2y.jpg' title='" + facComment + "' alt='" + facComment + "' width='15' height='15' border='0'> ";
			}
			String co2Captured = ServiceUtils.nullSafeHtmlUnescape((String) result[8]);
			if (co2Captured != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2g");
			}
			String co2EmittedSupplied = ServiceUtils.nullSafeHtmlUnescape((String) result[9]);
			if (co2EmittedSupplied != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2b");
			}
			String uuRandDExempt = ServiceUtils.nullSafeHtmlUnescape((String) result[10]);
			if ((type == FacilityType.CO2_INJECTION || type == FacilityType.RR_CO2) && uuRandDExempt != null) {
				vIcons = vIcons + ServiceUtils.getIconInfo("co2o");
			}
			ReportingStatus rs = rsMap.get(facilityId);
			if (rs != null && rs.getShorthand() != "VALID") {
				String rsTxt = rs.getTextBoxContents();
				rsTxt = rsTxt.replace("'", "&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}
			
			String facKey = facilityName + " [" + facilityId + "]";
			if (!facilityMap.containsKey(facKey)) {
				facilityMap.put(facKey, new HashMap<String, Object>());
			}
			Map<String, Object> dataMap = facilityMap.get(facKey);
			dataMap.put("icons", vIcons); // PUB-619 special case for jqGrid list display
			dataMap.put("name", facKey);
			dataMap.put("city", facCity);
			dataMap.put("state", facState);
			List<String> sectorList = (List<String>) dataMap.get("sectors");
			if (sectorList == null) {
				sectorList = new ArrayList<String>();
			}
			if (!sectorList.contains(sector)) {
				sectorList.add(sector);
			}
			dataMap.put("sectors", sectorList);
			Map<Long, BigDecimal> emissionsMap =
					(Map<Long, BigDecimal>) dataMap.get("emissions");
			if (emissionsMap == null) {
				emissionsMap = new HashMap<Long, BigDecimal>();
			}
			if (emissionsMap.get(emissionYear) == null) {
				emissionsMap.put(emissionYear, emissions);
			} else {
				if (emissionsMap.get(emissionYear) != null && emissions != null) {
					emissionsMap.put(emissionYear, emissionsMap.get(emissionYear).add(emissions));
				}
			}
			dataMap.put("emissions", emissionsMap);
			facilityMap.put(facKey, dataMap);
		}
		
		return new EmitterTrendListViewMaker(facilityMap);
		
	}
	
	public ListViewMaker createSupplierInstance(List<Object[]> results, Integer year, boolean isTrend) {
		
		Map<String, SupplierListDetailsObject> supplierData = trendDataTransformer.transformToSupplierTrendMap(results, year, isTrend);
		return (isTrend) ? new SupplierTrendListViewMaker(supplierData) : new SupplierListViewMaker(supplierData);
		
	}
	
	public ListViewMaker createPipeInstance(List<Object[]> results, FacilityType type, Map<Long, ReportingStatus> rsMap, int year) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, DimFacilityPipe> facilityMap = new HashMap<String, DimFacilityPipe>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : results) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			Long facilityId = (Long) result[2];
			String facCity = ServiceUtils.nullSafeHtmlUnescape((String) result[3]);
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
			key = facilityName + " [" + facilityId + "]" + "{" + facState + "}";
			String vIcons = "";
			ReportingStatus rs = rsMap.get(facilityId);
			if (rs != null && rs.getShorthand() != "VALID") {
				String rsTxt = rs.getTextBoxContents();
				rsTxt = rsTxt.replace("'", "&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}
			
			facilityNameLookup.put(key, facilityName);
			if (!facilityMap.containsKey(key)) {
				DimFacilityPipe facility = new DimFacilityPipe();
				facility.setCity(facCity);
				facility.setState(facState);
				facility.setComments(vIcons); // PUB-619 special case for jqGrid list display
				
				if (facility != null) {
					facilityMap.put(key, facility);
				}
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
		
		return new PipeListViewMaker(facilityNameLookup, facilityMap, keyMap);
	}
	
	public ListViewMaker createPipeGeographyInstance(List<Object[]> data, ModeDomainResolver.Mode mode, List<String> labels) {
		
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, Map<String, BigDecimal>> keyMap = new TreeMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : data) {
			String key = org.apache.commons.lang3.StringUtils.EMPTY;
			String facState = org.apache.commons.lang3.StringUtils.EMPTY;
			
			if (ModeDomainResolver.Mode.STATE == mode) {
				if ((String) result[0] != null) {
					key = (String) result[0];
				} else {
					key = "Unknown";
				}
			} else {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				Long facilityId = (Long) result[2];
				facState = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
				key = facilityName + " [" + facilityId + "]" + "{" + facState + "}";
				facilityNameLookup.put(key, facilityName);
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
		
		return new PipeListGeographyViewMaker(facilityNameLookup, keyMap);
	}
	
}
