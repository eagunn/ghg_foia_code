package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimSectorDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.DimSubSectorDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.DimSector;
import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
@Service
@Transactional
public class BarChartService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimSectorDao sectorDao;
	
	@Inject
	DimSubSectorDao subSectorDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimStateDao stateDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	DimMsaDao msaDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Resource(name = "dataCredit")
	private String dataCredit;
	
	private static final int maxFacilities = 500;
	
	public JSONObject barChartSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		log.info("barChartSector started...");
		
		List<Object[]> results = pubFactsDao.getBarSector(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		
		if (results.size() == 1) {
			return barChartSectorL2(q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		}
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - ";
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " County - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					domain = domain + tl.getTribalLandName() + " - ";
				}
			}
		}
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "SECTOR1");
		
		List<DimSector> emitterSectors = sectorDao.getEmitterSectors();
		
		Map<String, Map<String, BigDecimal>> subSectorMap = new HashMap<String, Map<String, BigDecimal>>();
		Map<String, String> categoryMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		Map<String, BigDecimal> sectorMap = new LinkedHashMap<String, BigDecimal>();
		for (Object[] result : (List<Object[]>) results) {
			String sector = (String) result[0];
			String subpart = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!subSectorMap.containsKey(subpart)) {
					sectorMap = new LinkedHashMap<String, BigDecimal>();
				} else {
					sectorMap = subSectorMap.get(subpart);
				}
				for (DimSector emitterSector : emitterSectors) {
					if (emitterSector.getSectorName().equals(sector)) {
						if (!sectorMap.containsKey(emitterSector.getSectorName())) {
							sectorMap.put(emitterSector.getSectorName(), emission);
						} else {
							sectorMap.put(emitterSector.getSectorName(), sectorMap.get(emitterSector.getSectorName()).add(emission));
						}
						
					} else {
						sectorMap.put(emitterSector.getSectorName(), BigDecimal.ZERO);
					}
				}
				subSectorMap.put(subpart, sectorMap);
			}
			categoryMap.put(sector, sector);
		}
		
		JSONArray categories = new JSONArray();
		for (DimSector sector : emitterSectors) {
			if (categoryMap.get(sector.getSectorName()) != null) {
				categories.add(sector.getSectorName());
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		List<String> subSectors = subSectorDao.getEmitterSubSectors();
		
		int colorIndex = 0;
		for (String subSector : subSectors) {
			sectorMap = subSectorMap.get(subSector);
			if (sectorMap != null) {
				jsonChild.put("name", subSector);
				for (String sector : sectorMap.keySet()) {
					if (categoryMap.get(sector) != null) {
						BigDecimal emission = sectorMap.get(sector);
						values.add(ServiceUtils.convert(emission, unit));
					}
				}
				jsonChild.put("data", values);
				jsonChild.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				values.clear();
				jsonChild.clear();
			}
		}
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartSector completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartSectorL2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		log.info("barChartSectorL2 started...");
		
		List<Object[]> results = pubFactsDao.getBarSectorLevel2(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		if (!StringUtils.hasLength(state) && results.size() > maxFacilities) {
			return barChartSectorL3(q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " County - " + ServiceUtils.getSector(sectors) + " - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "SECTOR2");
		
		Map<String, BigDecimal> facilities = new TreeMap<String, BigDecimal>();
		
		for (Object[] result : (List<Object[]>) results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[3] + ")";
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				if (facilities.get(key) == null) {
					facilities.put(key, emission);
				} else {
					facilities.put(key, facilities.get(key).add(emission));
				}
			}
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (String key : facilities.keySet()) {
			if (facilities.get(key).compareTo(BigDecimal.ZERO) > 0) {
				ServiceUtils.addToUnitList(emissions, facilities.get(key));
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		
		Iterator<Map.Entry<String, BigDecimal>> iter = facilities.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			}
		}
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, Map<String, BigDecimal>> subSectorMap = new HashMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : (List<Object[]>) results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[3] + ")";
			String subSector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				for (String facilityKey : facilities.keySet()) {
					facilityIdLookup.put(key, (Long) result[3]);
					facilityNameLookup.put(key, facilityName);
					Map<String, BigDecimal> facilityMap = subSectorMap.get(subSector);
					if (facilityMap == null) {
						facilityMap = new TreeMap<String, BigDecimal>();
						subSectorMap.put(subSector, facilityMap);
					}
					if (facilityKey.equals(key)) {
						facilityMap.put(facilityKey, emission);
					} else {
						if (facilityMap.get(facilityKey) == null) {
							facilityMap.put(facilityKey, BigDecimal.ZERO);
						}
					}
				}
			}
		}
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		for (String facilityKey : facilities.keySet()) {
			categories.add(facilityNameLookup.get(facilityKey));
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		List<String> subSectors = subSectorDao.getEmitterSubSectors();
		
		JSONObject data = new JSONObject();
		
		int colorIndex = 0;
		for (String subSector : subSectors) {
			Map<String, BigDecimal> facilityMap = subSectorMap.get(subSector);
			if (facilityMap != null) {
				jsonChild.put("name", subSector);
				
				for (Map.Entry<String, BigDecimal> facilityEmission : facilityMap.entrySet()) {
					data.put("id", facilityIdLookup.get(facilityEmission.getKey()));
					data.put("y", ServiceUtils.convert(facilityEmission.getValue(), unit));
					values.add(data);
					data.clear();
				}
				jsonChild.put("data", values);
				jsonChild.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				values.clear();
				jsonChild.clear();
			}
		}
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartSectorL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartSectorL3(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		log.info("barChartSectorL3 started...");
		
		List<Object[]> results = pubFactsDao.getBarSectorLevel3(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " County - " + ServiceUtils.getSector(sectors) + " - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "SECTOR3");
		
		Map<String, BigDecimal> states = new TreeMap<String, BigDecimal>();
		for (Object[] result : (List<Object[]>) results) {
			String stateKey = "Unknown";
			BigDecimal emission = (BigDecimal) result[2];
			if (result[0] != null) {
				stateKey = (String) result[0];
			}
			if (emission != null) {
				// ServiceUtils.addToUnitList(emissions, emission);
				if (states.get(stateKey) == null) {
					states.put(stateKey, emission);
				} else {
					states.put(stateKey, states.get(stateKey).add(emission));
				}
			}
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (String stateKey : states.keySet()) {
			if (states.get(stateKey).compareTo(BigDecimal.ZERO) > 0) {
				ServiceUtils.addToUnitList(emissions, states.get(stateKey));
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		
		Iterator<Map.Entry<String, BigDecimal>> iter = states.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			}
		}
		
		Map<String, Map<String, BigDecimal>> subSectorMap = new HashMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : (List<Object[]>) results) {
			String stateKey = "Unknown";
			if (result[0] != null) {
				stateKey = (String) result[0];
			}
			String subSector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				for (String stateName : states.keySet()) {
					Map<String, BigDecimal> stateMap = subSectorMap.get(subSector);
					if (stateMap == null) {
						stateMap = new TreeMap<String, BigDecimal>();
						subSectorMap.put(subSector, stateMap);
					}
					if (stateName.equals(stateKey)) {
						stateMap.put(stateName, emission);
					} else {
						if (stateMap.get(stateName) == null) {
							stateMap.put(stateName, BigDecimal.ZERO);
						}
					}
				}
			}
		}
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		for (String stateKey : states.keySet()) {
			categories.add(stateKey);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		List<String> subSectors = subSectorDao.getEmitterSubSectors();
		
		int colorIndex = 0;
		for (String subSector : subSectors) {
			Map<String, BigDecimal> stateMap = subSectorMap.get(subSector);
			if (stateMap != null) {
				jsonChild.put("name", subSector);
				for (BigDecimal emission : stateMap.values()) {
					values.add(ServiceUtils.convert(emission, unit));
				}
				jsonChild.put("data", values);
				jsonChild.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				values.clear();
				jsonChild.clear();
			}
		}
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartSectorL3 completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartGas(String q, int year, String state, String countyFips, String lowE, String highE,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		List<Object[]> results = pubFactsDao.getBarPieTreeGas(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, rs);
		
		List<String> labels = new ArrayList<String>();
		labels.add("Carbon Dioxide (CO<sub>2</sub>)");
		labels.add("Methane (CH<sub>4</sub>)");
		labels.add("Nitrous Oxide (N<sub>2</sub>O)");
		labels.add("PFC-14");
		labels.add("PFC-116");
		labels.add("SF<sub>6</sub>");
		labels.add("HFC-23");
		
		Map<String, BigDecimal> keyMap = new LinkedHashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				keyMap.put(key, emission);
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		jsonParent.accumulate("label", "Emissions");
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (String label : labels) {
			if (keyMap.containsKey(label)) {
				jsonChild.put("label", label);
				jsonChild.put("values", ServiceUtils.convert(keyMap.get(label), unit));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		return jsonParent;
	}
	
	public JSONObject barChartState(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("barChartState started...");
		
		List<Object[]> results = null;
		
		if (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && sectors.isS902() && sectors.isS903() && sectors.isS904() && sectors.isS905() && sectors.isS906() && sectors.isS907() && sectors.isS908() && sectors.isS910() && sectors.isS911()) {
			results = pubFactsDao.getBarGeo(q, year, lowE, highE, state, countyFips, msaCode, gases, sectors, qo, rs, emissionsType, tribalLandId);
		} else {
			results = pubFactsDao.getBarState(q, year, lowE, highE, state, countyFips, msaCode, gases, sectors, qo, rs, emissionsType, tribalLandId);
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
				domain = "U.S. - ";
			} else {
				domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
			}
			
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			if (state.equals("TL")) {
				mode = "TRIBAL LAND";
			} else {
				mode = "COUNTY";
			}
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = st.getStateName() + " - ";
				} else {
					domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(countyFips)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dc.getCountyName() + " County - ";
				} else {
					domain = domain + dc.getCountyName() + " County - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(msaCode)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
				} else {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
						domain = domain + tl.getTribalLandName() + " - ";
					} else {
						domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - ";
					}
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "STATE1");
		
		Map<String, BigDecimal> states = new TreeMap<String, BigDecimal>();
		
		for (Object[] result : (List<Object[]>) results) {
			String stateKey = "Unknown";
			BigDecimal emission = (BigDecimal) result[2];
			if (result[0] != null) {
				if (StringUtils.hasLength(msaCode)) {
					stateKey = msaDao.getMsaByCode(msaCode).getCbsa_title();
				} else if (mode.equalsIgnoreCase("COUNTY")) {
					DimCounty county = countyDao.findById((String) result[0]);
					if (county != null) {
						stateKey = county.getCountyName();
					}
				} else if (mode.equalsIgnoreCase("TRIBAL LAND")) {
					LuTribalLands tl = tribalLandsDao.findById((Long) result[0]);
					if (tl != null) {
						stateKey = tl.getTribalLandName();
					}
				} else {
					stateKey = (String) result[0];
				}
			}
			if (emission != null) {
				// ServiceUtils.addToUnitList(emissions, emission);
				if (states.get(stateKey) == null) {
					states.put(stateKey, emission);
				} else {
					states.put(stateKey, states.get(stateKey).add(emission));
				}
			}
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		String unit = ServiceUtils.getUnit(emissions);
		
		Iterator<Map.Entry<String, BigDecimal>> iter = states.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			}
		}
		
		Map<String, Map<String, BigDecimal>> sectorMap = new HashMap<String, Map<String, BigDecimal>>();
		
		for (Object[] result : (List<Object[]>) results) {
			String stateKey = "Unknown";
			if (result[0] != null) {
				if (StringUtils.hasLength(msaCode)) {
					stateKey = msaDao.getMsaByCode(msaCode).getCbsa_title();
				} else if (mode.equalsIgnoreCase("COUNTY")) {
					DimCounty county = countyDao.findById((String) result[0]);
					if (county != null) {
						stateKey = county.getCountyName();
					}
				} else if (mode.equalsIgnoreCase("TRIBAL LAND")) {
					LuTribalLands tl = tribalLandsDao.findById((Long) result[0]);
					if (tl != null) {
						stateKey = tl.getTribalLandName();
					}
				} else {
					stateKey = (String) result[0];
				}
			}
			String sector = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				for (String stateName : states.keySet()) {
					Map<String, BigDecimal> stateMap = sectorMap.get(sector);
					if (stateMap == null) {
						stateMap = new TreeMap<String, BigDecimal>();
						sectorMap.put(sector, stateMap);
					}
					if (stateName.equals(stateKey)) {
						if (stateMap.get(stateName) == null) {
							stateMap.put(stateName, emission);
						} else {
							stateMap.put(stateName, stateMap.get(stateName).add(emission));
						}
					} else {
						if (stateMap.get(stateName) == null) {
							stateMap.put(stateName, BigDecimal.ZERO);
						}
					}
				}
			}
		}
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		for (String stateKey : states.keySet()) {
			categories.add(stateKey);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		List<DimSector> emitterSectors = sectorDao.getEmitterSectors();
		
		for (DimSector sector : emitterSectors) {
			Map<String, BigDecimal> stateMap = sectorMap.get(sector.getSectorName());
			if (stateMap != null) {
				jsonChild.put("name", sector.getSectorName());
				for (BigDecimal emission : stateMap.values()) {
					values.add(ServiceUtils.convert(emission, unit));
				}
				jsonChild.put("data", values);
				jsonChild.put("color", sector.getSectorColor());
				jsonArray.add(jsonChild);
				values.clear();
				jsonChild.clear();
			}
		}
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartState completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartStateL2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("barChartStateL2 started...");
		
		List<Object[]> results = null;
		
		results = pubFactsDao.getBarStateLevel2(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, rs, emissionsType, tribalLandId);
		
		JSONObject jsonParent = new JSONObject();
		if (results.size() == 1) {
			String ss = (String) results.get(0)[0];
			return barChartStateL3(ss, q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, rs, emissionsType, tribalLandId);
		} else {
			jsonParent.put("sectorId", (Long) results.get(0)[2]);
			jsonParent.put("view", "STATE1L2");
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
				domain = "U.S. - ";
			} else {
				domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
			}
			
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			if (state.equals("TL")) {
				mode = "TRIBAL LAND";
			} else {
				mode = "COUNTY";
			}
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = st.getStateName() + " - ";
				} else {
					domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(countyFips)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dc.getCountyName() + " County - ";
				} else {
					domain = domain + dc.getCountyName() + " County - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(msaCode)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
				} else {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
						domain = domain + tl.getTribalLandName() + " - ";
					} else {
						domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - ";
					}
				}
			}
		}
		
		Map<String, BigDecimal> keyMap = new TreeMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			
			BigDecimal emission = (BigDecimal) result[1];
			
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				keyMap.put(key, emission);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		
		JSONArray categories = new JSONArray();
		
		Iterator<Map.Entry<String, BigDecimal>> iter = keyMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			} else {
				categories.add(item.getKey());
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("segment", "SubSector");
		jsonParent.put("unit", unit);
		
		JSONObject data = new JSONObject();
		jsonChild.put("name", "Emissions");
		int colorIndex = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				data.put("y", ServiceUtils.convert(keyMap.get(key), unit));
				data.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				values.add(data);
				data.clear();
			}
		}
		jsonChild.put("data", values);
		jsonArray.add(jsonChild);
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartStateL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartStateL3(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId) {
		if (ss.equals("Offshore Petroleum ")) {
			ss = "Offshore Petroleum & Natural Gas Production";
		}
		
		List<Object[]> results = null;
		
		results = pubFactsDao.getBarStateLevel3(ss, q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, rs, emissionsType, tribalLandId);
		
		JSONObject jsonParent = new JSONObject();
		
		if (results.size() > 0) {
			jsonParent.put("sectorId", (Long) results.get(0)[3]);
		}
		jsonParent.put("view", "STATE1L3");
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
				domain = "U.S. - ";
			} else {
				domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
			}
			
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = st.getStateName() + " - ";
				} else {
					domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(countyFips)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dc.getCountyName() + " County - ";
				} else {
					domain = domain + dc.getCountyName() + " County - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else if (StringUtils.hasLength(msaCode)) {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
				} else {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - ";
				}
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					if (EMPTY.equals(ServiceUtils.getSector(sectors))) {
						domain = domain + tl.getTribalLandName() + " - ";
					} else {
						domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - ";
					}
				}
			}
		}
		
		Map<String, BigDecimal> facilities = new TreeMap<String, BigDecimal>();
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		for (Object[] result : (List<Object[]>) results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				if (facilities.get(key) == null) {
					facilityIdLookup.put(key, (Long) result[2]);
					facilityNameLookup.put(key, facilityName);
					facilities.put(key, emission);
				} else {
					facilities.put(key, facilities.get(key).add(emission));
				}
			}
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (String key : facilities.keySet()) {
			if (facilities.get(key).compareTo(BigDecimal.ZERO) > 0) {
				ServiceUtils.addToUnitList(emissions, facilities.get(key));
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		
		JSONArray categories = new JSONArray();
		
		Iterator<Map.Entry<String, BigDecimal>> iter = facilities.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			} else {
				categories.add(facilityNameLookup.get(item.getKey()));
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("segment", "Facility");
		jsonParent.put("unit", unit);
		
		JSONObject data = new JSONObject();
		jsonChild.put("name", "Emissions");
		int colorIndex = 0;
		for (String key : facilities.keySet()) {
			if (ServiceUtils.convert(facilities.get(key), unit) > 0L) {
				data.put("id", facilityIdLookup.get(key));
				data.put("y", ServiceUtils.convert(facilities.get(key), unit));
				data.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				values.add(data);
				data.clear();
			}
		}
		jsonChild.put("data", values);
		jsonArray.add(jsonChild);
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartStateL3 completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartBasin(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("barChartBasin started...");
		
		if (StringUtils.hasLength(basin)) {
			return barChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
		} else {
			List<Object[]> results = pubFactsDao.getBarBasin(q, year, lowE, highE, basin,
					gases, sectors, qo, rs);
			if (results.size() == 1) {
				return barChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
			}
			
			String mode = EMPTY;
			String domain = EMPTY;
			if (!StringUtils.hasLength(basin)) {
				mode = "STATE";
				domain = "Onshore Oil and Natural Gas Production - ";
			} else {
				/*mode = "FACILITY";
				DimState st = stateDao.getStateByStateAbbr(state);
				if (st != null) {
					domain = st.getStateName()+" - ";
				}
				DimCounty dc = countyDao.findById(countyFips);
				if (dc != null) {
					domain = domain + dc.getCountyName()+" County - ";
				}*/
			}
			
			JSONObject jsonParent = new JSONObject();
			
			jsonParent.put("view", "BASIN1");
			// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
			// jsonParent.put("color", ServiceUtils.colorArr);
			
			/**
			 List<Basin> basins = basinLayerDao.getBasins();
			 
			 for (Basin b : basins) {
			 jsonParent.accumulate("label", b.getName());
			 }**/
			
			Map<String, BigDecimal> basinMap = new TreeMap<String, BigDecimal>();
			Map<String, String> basinCodeMap = new HashMap<String, String>();
			List<BigDecimal> emissions = new ArrayList<BigDecimal>();
			
			for (Object[] result : (List<Object[]>) results) {
				String key = (String) result[0];
				BigDecimal emission = (BigDecimal) result[1];
				String basinCode = (String) result[2];
				if (emission != null) {
					ServiceUtils.addToUnitList(emissions, emission);
					basinMap.put(key, emission);
					if (basinCode != null) {
						basinCodeMap.put(key, basinCode);
					}
				}
			}
			
			String unit = ServiceUtils.getUnit(emissions);
			
			JSONArray categories = new JSONArray();
			
			Iterator<Map.Entry<String, BigDecimal>> iter = basinMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, BigDecimal> item = iter.next();
				if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
					iter.remove();
				} else {
					categories.add(item.getKey());
				}
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("categories", categories);
			
			jsonParent.put("xAxis", jsonObject);
			
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonChild = new JSONObject();
			JSONArray values = new JSONArray();
			
			jsonParent.put("domain", domain);
			jsonParent.put("mode", mode);
			jsonParent.put("unit", unit);
			
			jsonChild.put("name", "Onshore Oil & Gas Production");
			JSONObject data = new JSONObject();
			for (String b : basinMap.keySet()) {
				data.put("id", basinCodeMap.get(b));
				data.put("y", ServiceUtils.convert(basinMap.get(b), unit));
				values.add(data);
				data.clear();
			}
			jsonChild.put("data", values);
			jsonChild.put("color", "#F7D869");
			jsonArray.add(jsonChild);
			
			jsonParent.put("series", jsonArray);
			
			jsonParent.put("credits", dataCredit);
			
			log.info("barChartBasin completed...");
			
			return jsonParent;
		}
	}
	
	public JSONObject barChartBasinL2(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("barChartBasinL2 started...");
		
		List<Object[]> results = pubFactsDao.getBarBasinL2(q, year, lowE, highE, basin,
				gases, sectors, qo, rs);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(basin)) {
			mode = "STATE";
			domain = "Onshore Oil and Natural Gas Production - ";
		} else {
			/*mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName()+" - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName()+" County - ";
			}*/
		}
		
		Map<String, BigDecimal> facilities = new TreeMap<String, BigDecimal>();
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		
		for (Object[] result : results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilities.put(key, emission);
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
			}
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (String key : facilities.keySet()) {
			if (facilities.get(key).compareTo(BigDecimal.ZERO) > 0) {
				ServiceUtils.addToUnitList(emissions, facilities.get(key));
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		
		JSONArray categories = new JSONArray();
		
		Iterator<Map.Entry<String, BigDecimal>> iter = facilities.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			} else {
				categories.add(facilityNameLookup.get(item.getKey()));
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "BASIN2");
		
		jsonParent.put("xAxis", jsonObject);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		// jsonParent.put("label", "Emissions");
		jsonParent.put("domain", basinLayerDao.getBasinByCode(basin).getBasin() + " - ");
		
		jsonChild.put("name", "Onshore Oil & Gas Production");
		JSONObject data = new JSONObject();
		for (String f : facilities.keySet()) {
			data.put("id", facilityIdLookup.get(f));
			data.put("y", ServiceUtils.convert(facilities.get(f), unit));
			values.add(data);
			data.clear();
		}
		jsonChild.put("data", values);
		jsonChild.put("color", "#F7D869");
		jsonArray.add(jsonChild);
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartBasinL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject barChartSupplier(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		
		log.info("barChartSupplier started...");
		
		Map<String, BigDecimal> facilities = new TreeMap<String, BigDecimal>();
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		
		if (sc != 0) {
			List<Object[]> results = pubFactsDao.getSupplierBarChart(q, qo, year, sc, rs, state);
			
			for (Object[] result : results) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				String key = facilityName + " (" + (Long) result[2] + ")";
				BigDecimal quantity = (BigDecimal) result[1];
				if (quantity != null) {
					facilities.put(key, quantity);
					facilityIdLookup.put(key, (Long) result[2]);
					facilityNameLookup.put(key, facilityName);
				}
			}
		}
		
		List<BigDecimal> quantities = new ArrayList<BigDecimal>();
		
		for (String key : facilities.keySet()) {
			if (facilities.get(key).compareTo(BigDecimal.ZERO) > 0) {
				ServiceUtils.addToUnitList(quantities, facilities.get(key));
			}
		}
		
		String unit = ServiceUtils.getUnit(quantities);
		
		JSONArray categories = new JSONArray();
		
		Iterator<Map.Entry<String, BigDecimal>> iter = facilities.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BigDecimal> item = iter.next();
			if (ServiceUtils.convert(item.getValue(), unit) == 0L) {
				iter.remove();
			} else {
				categories.add(facilityNameLookup.get(item.getKey()));
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SUPPLIER");
		
		jsonParent.put("xAxis", jsonObject);
		
		jsonParent.put("domain", ServiceUtils.getSupplierType(sc));
		jsonParent.put("unit", unit);
		// jsonParent.accumulate("label", "Quantity supplied");
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		
		jsonChild.put("name", ServiceUtils.getSupplierType(sc));
		JSONObject data = new JSONObject();
		for (String f : facilities.keySet()) {
			data.put("id", facilityIdLookup.get(f));
			data.put("y", ServiceUtils.convert(facilities.get(f), unit));
			values.add(data);
			data.clear();
		}
		jsonChild.put("data", values);
		jsonChild.put("color", "#F7D869");
		jsonArray.add(jsonChild);
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartSupplier completed...");
		
		return jsonParent;
	}
	
	/*public JSONObject barChartSectorTrend(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo) {
		
		log.info("barChartSectorTrend started...");
		
		List<Object[]> results = facilityEmission.getBarSectorTrend(q,year,lowE,highE,state,countyFips,
				gases, sectors, qo);
		if (results.size() == 1) {
			return barChartSectorL2(q,year,lowE,highE,state,countyFips,
					gases, sectors, qo);
		}
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - ";
		} else if (!StringUtils.hasLength(countyFips)) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName()+" - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName()+" - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName()+" County - ";
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "SECTOR1");
		//String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		jsonParent.put("color", ServiceUtils.colorArr);
		
		List<String> subSectors = subSectorDao.getEmitterSubSectors();
		
		for (String subSector : subSectors) {
			jsonParent.accumulate("label", subSector);
		}

		Map<String, Map<String, Map<String, BigDecimal>>> sectorMap = 
			new LinkedHashMap<String, Map<String, Map<String, BigDecimal>>>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result:(List<Object[]>)results) {
			String sector = (String)result[1];
			String tyear = Long.toString((Long)result[2]);
			String subpart = (String)result[3];
			BigDecimal emission = (BigDecimal)result[4];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!sectorMap.containsKey(sector)) {
					sectorMap.put(sector, new LinkedHashMap<String, Map<String, BigDecimal>>());
				}
				Map<String, Map<String, BigDecimal>> yearMap = sectorMap.get(sector);
				if (!yearMap.containsKey(tyear)) {
					Map<String, BigDecimal> subpartMap = new LinkedHashMap<String, BigDecimal>();
					for (String subSector : subSectors) {
						subpartMap.put(subSector, BigDecimal.ZERO);						
					}
					yearMap.put(tyear, subpartMap);
				}
				Map<String, BigDecimal> subpartMap = yearMap.get(tyear);
				subpartMap.put(subpart, emission);
				yearMap.put(tyear, subpartMap);
				sectorMap.put(sector, yearMap);
			}
		}
		JSONArray jsonSectorArray = new JSONArray();
		JSONArray jsonYearArray = new JSONArray();
		JSONObject jsonSubChild = new JSONObject();
		JSONObject jsonChild = new JSONObject();
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		List<DimSector> emitterSectors = sectorDao.getEmitterSectors();
		
		for (DimSector sector : emitterSectors) {
			jsonYearArray.clear();
			String sectorName = sector.getSectorName();
			Map<String, Map<String, BigDecimal>> yearMap = sectorMap.get(sectorName);
			if (yearMap != null) {
				// jsonChild.put("label", sectorName);
				for (String tyear : yearMap.keySet()) {
					Map<String, BigDecimal> subpartMap = yearMap.get(tyear);
					if (subpartMap != null) {
						if (ServiceUtils.aggregate(subpartMap, unit)>0L) {
							jsonChild.put("label", sectorName + " - " + tyear);
							for (String subpart : subSectors) {
								if (subpartMap.get(subpart) != null) {
									jsonChild.accumulate("values", ServiceUtils.convertEmitter(subpartMap.get(subpart), unit));
								}
							}
							if (!jsonChild.isEmpty()) {
								jsonSectorArray.add(jsonChild);
								// jsonYearArray.add(jsonSubChild);
								// jsonChild.accumulate("values", jsonYearArray);
								// jsonChild.accumulate("values", jsonSubChild);
								// jsonSubChild.clear();
								jsonChild.clear();
							}
						}
					}
				}
			}
			//jsonChild.put("values", jsonYearArray);
			//if (!jsonChild.isEmpty()) {
			//	jsonSectorArray.add(jsonChild);
			//	jsonChild.clear();
			//}
		}

		// jsonParent.put("values", jsonYearArray);
		jsonParent.put("values", jsonSectorArray);
		
		log.info("barChartSectorTrend completed...");
		
		return jsonParent;
	}*/
	
	public JSONObject barChartSectorTrend(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("SectorTrend started...");
		
		List<Object[]> results = pubFactsDao.getSectorTrend(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - ";
		} else if (!StringUtils.hasLength(countyFips)) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " County - ";
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR1");
		
		Map<String, Map<String, BigDecimal>> sectorMap =
				new LinkedHashMap<String, Map<String, BigDecimal>>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Set<String> years = new TreeSet<String>();
		
		for (Object[] result : (List<Object[]>) results) {
			String sector = (String) result[0];
			String color = (String) result[1];
			String tyear = Long.toString((Long) result[2]);
			BigDecimal emission = (BigDecimal) result[3];
			years.add(tyear);
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!sectorMap.containsKey(sector)) {
					sectorMap.put(sector, new LinkedHashMap<String, BigDecimal>());
					paletteMap.put(sector, color);
				}
				Map<String, BigDecimal> yearMap = sectorMap.get(sector);
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				}
			}
		}
		
		JSONArray categories = new JSONArray();
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONObject xAxis = new JSONObject();
		for (String category : years) {
			categories.add(category);
		}
		xAxis.put("categories", categories);
		jsonParent.put("xAxis", xAxis);
		
		JSONArray series = new JSONArray();
		JSONObject item = new JSONObject();
		JSONArray emissionsArray = new JSONArray();
		List<DimSector> emitterSectors = sectorDao.getEmitterSectors();
		
		for (DimSector sector : emitterSectors) {
			String sectorName = sector.getSectorName();
			Map<String, BigDecimal> yearMap = sectorMap.get(sectorName);
			if (yearMap != null) {
				for (String tyear : years) {
					if (yearMap.get(tyear) != null) {
						emissionsArray.add(ServiceUtils.convert(yearMap.get(tyear), unit));
					} else {
						emissionsArray.add(0L);
					}
				}
			}
			if (!emissionsArray.isEmpty()) {
				item.put("name", sectorName);
				// item.put("color", paletteMap.get(sectorName));
				item.put("color", paletteMap.get(sectorName));
				item.put("data", emissionsArray);
				emissionsArray.clear();
				series.add(item);
			}
		}
		
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barChartSectorTrend completed...");
		
		return jsonParent;
	}
	
	public JSONObject barRR(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		log.info("barRR started...");
		
		List<Object[]> results = pubFactsDao.getBarRR(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - ";
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " County - ";
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					domain = domain + tl.getTribalLandName() + " - ";
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR1");
		
		List<DimSector> emitterSectors = sectorDao.getSectorsByType("I");
		
		Map<String, Map<String, BigDecimal>> subSectorMap = new HashMap<String, Map<String, BigDecimal>>();
		Map<String, String> categoryMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		Map<String, BigDecimal> sectorMap = new LinkedHashMap<String, BigDecimal>();
		for (Object[] result : results) {
			String sector = (String) result[0];
			String subpart = (String) result[1];
			BigDecimal emission = (BigDecimal) result[2];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!subSectorMap.containsKey(subpart)) {
					sectorMap = new LinkedHashMap<String, BigDecimal>();
				} else {
					sectorMap = subSectorMap.get(subpart);
				}
				for (DimSector emitterSector : emitterSectors) {
					if (emitterSector.getSectorName().equals(sector)) {
						if (!sectorMap.containsKey(emitterSector.getSectorName())) {
							sectorMap.put(emitterSector.getSectorName(), emission);
						} else {
							sectorMap.put(emitterSector.getSectorName(), sectorMap.get(emitterSector.getSectorName()).add(emission));
						}
						
					} else {
						sectorMap.put(emitterSector.getSectorName(), BigDecimal.ZERO);
					}
				}
				subSectorMap.put(subpart, sectorMap);
			}
			categoryMap.put(sector, sector);
		}
		
		JSONArray categories = new JSONArray();
		for (DimSector sector : emitterSectors) {
			if (categoryMap.get(sector.getSectorName()) != null) {
				categories.add(sector.getSectorName());
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("categories", categories);
		
		jsonParent.put("xAxis", jsonObject);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONArray values = new JSONArray();
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		List<String> subSectors = subSectorDao.getSubSectorsByType("I");
		
		int colorIndex = 0;
		for (String subSector : subSectors) {
			sectorMap = subSectorMap.get(subSector);
			if (sectorMap != null) {
				jsonChild.put("name", subSector);
				for (String sector : sectorMap.keySet()) {
					if (categoryMap.get(sector) != null) {
						BigDecimal emission = sectorMap.get(sector);
						values.add(ServiceUtils.convert(emission, unit));
					}
				}
				jsonChild.put("data", values);
				jsonChild.put("color", ServiceUtils.colorArr[colorIndex]);
				if (colorIndex == ServiceUtils.colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				values.clear();
				jsonChild.clear();
			}
		}
		
		jsonParent.put("series", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("barRR completed...");
		
		return jsonParent;
	}
}
