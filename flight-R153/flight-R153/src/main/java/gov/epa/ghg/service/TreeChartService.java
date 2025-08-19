package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.DimSubSectorDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.dto.Emission;
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
public class TreeChartService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	
	private static final int maxFacilities = 55;
	private static final int maxCounties = 55;
	
	private static DecimalFormat df = new DecimalFormat("###,###");
	
	public JSONObject treeChartSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType) {
		
		log.info("treeChartSector started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSector(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, null);
		if (results.size() == 1) {
			return treeChartSectorL2(q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType);
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
		
		Map<String, String> paletteMap = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			String color = (String) result[2];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				paletteMap.put(key, color);
				keyMap.put(key, emission);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", paletteMap.get(key));
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", key);
				jsonChild.put("id", key);
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartSector completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartSectorL2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType) {
		
		log.info("treeChartSectorL2 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSectorLevel2(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, null);
		if (results.size() == 1) {
			String ss = (String) results.get(0)[0];
			return treeChartSectorL3(ss, q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType);
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - ";
		} else if (!StringUtils.hasLength(countyFips)) {
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
				domain = domain + dc.getCountyName() + " - " + ServiceUtils.getSector(sectors) + " - ";
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "SECTOR2");
		// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
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
		jsonParent.put("unit", unit);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", key);
				jsonChild.put("id", key);
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartSectorL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartSectorL3(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType) {
		
		log.info("treeChartSectorL3 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSectorLevel3(ss, q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, null);
		if (!StringUtils.hasLength(state) && results.size() > maxFacilities) {
			return treeChartSectorL4(ss, q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType);
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
		} else if (!StringUtils.hasLength(countyFips)) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR3");
		// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
				ServiceUtils.addToUnitList(emissions, emission);
				keyMap.put(key, emission);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", facilityIdLookup.get(key));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", facilityNameLookup.get(key));
				jsonChild.put("id", facilityNameLookup.get(key));
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartSectorL3 completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartSectorL4(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType) {
		
		log.info("treeChartSectorL4 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSectorLevel4(ss, q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, null);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
		} else if (!StringUtils.hasLength(countyFips)) {
			mode = "COUNTY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			}
		} else {
			mode = "FACILITY";
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName() + " - ";
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				domain = domain + dc.getCountyName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR4");
		// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
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
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$subsector", ss);
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", key);
				jsonChild.put("id", key);
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartSectorL4 completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartGas(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		List<Object[]> results = pubFactsDao.getBarPieTreeGas(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, rs);
		
		JSONObject jsonParent = new JSONObject();
		
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
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
		jsonParent.put("unit", unit); JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", key);
				jsonChild.put("id", key);
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		return jsonParent;
	}
	
	public JSONObject treeChartState(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("treeChartState started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeState(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		if (results.size() == 1) {
			state = (String) results.get(0)[2];
			return treeChartStateL2(q, year, lowE, highE, state, countyFips,
					gases, sectors, qo, rs);
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "STATE1");
		// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
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
		jsonParent.put("unit", unit);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", key);
				jsonChild.put("id", key);
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartState completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartStateL2(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("treeChartStateL2 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeStateLevel2(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		if (!StringUtils.hasLength(countyFips) && results.size() > maxFacilities) {
			return treeChartStateL3(q, year, lowE, highE, state, countyFips,
					gases, sectors, qo, rs);
		}
		
		JSONObject jsonParent = new JSONObject();
		
		jsonParent.put("view", "STATE2");
		// String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
				ServiceUtils.addToUnitList(emissions, emission);
				keyMap.put(key, emission);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", facilityIdLookup.get(key));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", facilityNameLookup.get(key));
				jsonChild.put("id", facilityNameLookup.get(key));
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartStateL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartStateL3(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("treeChartStateL3 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeStateLevel3(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "STATE3");
		
		Map<String, String> countyLookup = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : (List<Object[]>) results) {
			String fipsCode = (String) result[0];
			String key = "Unknown";
			if (fipsCode != null) {
				DimCounty dc = countyDao.findById(fipsCode);
				if (dc != null) {
					key = dc.getCountyName();
				}
			}
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				countyLookup.put(key, fipsCode);
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		
		Iterator<Emission> it = emissionsSort.iterator();
		while (it.hasNext()) {
			Emission e = it.next();
			if (ServiceUtils.convert(e.getEmission(), unit) == 0L) {
				it.remove();
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long ghgTotal = 0L;
		if (emissionsSort.size() <= maxCounties) {
			// Lowest measure to highest measure
			for (int i = 0; i < emissionsSort.size(); i++) {
				Emission e = emissionsSort.get(i);
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(e.getEmission(), unit);
				ghgTotal += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", countyLookup.get(e.getKey()));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", e.getKey());
				jsonChild.put("id", e.getKey());
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
			}
		} else {
			// Highest measure to lowest measure
			for (int i = emissionsSort.size() - maxCounties + 1; i < emissionsSort.size(); i++) {
				Emission e = emissionsSort.get(i);
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(e.getEmission(), unit);
				ghgTotal += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", countyLookup.get(e.getKey()));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", e.getKey());
				jsonChild.put("id", e.getKey());
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
			}
		}
		
		if (emissionsSort.size() > maxCounties) {
			long total = 0L;
			for (int i = 0; i <= emissionsSort.size() - maxCounties; i++) {
				Emission e = emissionsSort.get(i);
				total += ServiceUtils.convert(e.getEmission(), unit);
			}
			if (total > 0) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[0]);
				ghgTotal += total;
				jsonSubChild.put("$area", total);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", "");
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", "Other");
				jsonChild.put("id", "Other");
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				
			}
		}
		
		jsonSubChild.put("$area", ghgTotal);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(ghgTotal) + ")");
		
		log.info("treeChartStateL3 completed...");
		
		return jsonParent;
				
		/*int max = 55;
		if(results.size() < max)
			max = results.size();
		for(int i = 0; i < max; i++){
			jsonSubChild.put("$color", ServiceUtils.colorArr[i%9]);
			jsonSubChild.put("$area", Math.round(Float.parseFloat(((Object [])results.get(i))[1].toString())*.001));
			jsonSubChild.put("$unit", "kMT");
			jsonChild.put("data", jsonSubChild);
			if(((Object [])results.get(i))[2] != null){
				jsonChild.put("name", ((Object [])results.get(i))[0]);
				jsonChild.put("id", ((Object [])results.get(i))[2]);
			} else {
				jsonChild.put("name", ((Object [])results.get(i))[0]);
				jsonChild.put("id", ((Object [])results.get(i))[0]);
			}
			jsonArray.add(jsonChild);
			jsonSubChild.clear();
			jsonChild.clear();
		}
		if(results.size() > 55){
			long sumTotal = 0;
			for(int i = 55; i < results.size(); i++){
				sumTotal += (long) Float.parseFloat(((Object [])results.get(i))[1].toString());
			}
			jsonSubChild.put("$color", ServiceUtils.colorArr[0]);
			jsonSubChild.put("$area", Math.round(sumTotal*.001));
			jsonSubChild.put("$unit", "kMT");
			jsonChild.put("data", jsonSubChild);
			jsonChild.put("name", "Other");
			jsonChild.put("id", "Other");
			jsonArray.add(jsonChild);
			jsonSubChild.clear();
			jsonChild.clear();
		}
		jsonParent.put("data",null);
		jsonParent.put("children", jsonArray);		
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (MMT CO<sub>2</sub>e)");
		return jsonParent;*/
	}
	
	public JSONObject treeChartBasin(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("treeChartBasin started...");
		
		if (StringUtils.hasLength(basin)) {
			return treeChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
		} else {
			List<Object[]> results = pubFactsDao.getBarBasin(q, year, lowE, highE, basin, gases, sectors, qo, rs);
			if (results.size() == 1) {
				return treeChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
			}
			
			String mode = EMPTY;
			String domain = EMPTY;
			if (!StringUtils.hasLength(basin)) {
				mode = "STATE";
				domain = "U.S. - ";
			}
			
			JSONObject jsonParent = new JSONObject();
			
			jsonParent.put("view", "BASIN1");
			
			Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
			List<BigDecimal> emissions = new ArrayList<BigDecimal>();
			
			for (Object[] result : results) {
				String key = (String) result[2];
				BigDecimal emission = (BigDecimal) result[1];
				if (emission != null) {
					ServiceUtils.addToUnitList(emissions, emission);
					keyMap.put(key, emission);
				}
			}
			
			String unit = ServiceUtils.getUnit(emissions);
			jsonParent.put("domain", domain);
			jsonParent.put("mode", mode);
			jsonParent.put("unit", unit);
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonChild = new JSONObject();
			JSONObject jsonSubChild = new JSONObject();
			
			long total = 0L;
			int i = 0;
			for (String key : keyMap.keySet()) {
				if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
					jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
					long emission = ServiceUtils.convert(keyMap.get(key), unit);
					total += emission;
					jsonSubChild.put("$area", emission);
					jsonSubChild.put("$unit", unit);
					jsonSubChild.put("$info", key);
					jsonChild.put("data", jsonSubChild);
					jsonChild.put("name", basinLayerDao.getBasinByCode(key).getBasin());
					jsonChild.put("id", key);
					jsonArray.add(jsonChild);
					jsonSubChild.clear();
					jsonChild.clear();
					i++;
				}
			}
			
			jsonSubChild.put("$area", total);
			jsonSubChild.put("$unit", unit);
			jsonParent.put("data", jsonSubChild);
			jsonParent.put("children", jsonArray);
			jsonParent.put("id", "root");
			jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
			
			log.info("treeChartBasin completed...");
			
			return jsonParent;
		}
	}
	
	public JSONObject treeChartBasinL2(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("treeChartBasinL2 started...");
		
		List<Object[]> results = pubFactsDao.getBarBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(basin)) {
			mode = "STATE";
			domain = "U.S. - ";
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "BASIN2");
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		
		for (Object[] result : results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
				ServiceUtils.addToUnitList(emissions, emission);
				keyMap.put(key, emission);
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long emission = ServiceUtils.convert(keyMap.get(key), unit);
				total += emission;
				jsonSubChild.put("$area", emission);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", facilityIdLookup.get(key));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", facilityNameLookup.get(key));
				jsonChild.put("id", facilityNameLookup.get(key));
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Emissions (" + df.format(total) + ")");
		
		log.info("treeChartBasinL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject treeChartSupplier(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		
		log.info("treeChartSupplier started...");
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new TreeMap<String, BigDecimal>();
		List<BigDecimal> quantities = new ArrayList<BigDecimal>();
		
		if (sc != 0) {
			List<Object[]> results = pubFactsDao.getSupplierTreeChart(q, qo, year, sc, rs, state);
			
			for (Object[] result : results) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				String key = facilityName + " (" + (Long) result[2] + ")";
				BigDecimal quantity = (BigDecimal) result[1];
				if (quantity != null) {
					facilityIdLookup.put(key, (Long) result[2]);
					facilityNameLookup.put(key, facilityName);
					ServiceUtils.addToUnitList(quantities, quantity);
					keyMap.put(key, quantity);
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SUPPLIER");
		
		String unit = ServiceUtils.getUnit(quantities);
		jsonParent.put("domain", ServiceUtils.getSupplierType(sc));
		jsonParent.put("unit", unit);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		JSONObject jsonSubChild = new JSONObject();
		
		long total = 0L;
		int i = 0;
		for (String key : keyMap.keySet()) {
			if (ServiceUtils.convert(keyMap.get(key), unit) > 0L) {
				jsonSubChild.put("$color", ServiceUtils.colorArr[i % 9]);
				long quantity = ServiceUtils.convert(keyMap.get(key), unit);
				total += quantity;
				jsonSubChild.put("$area", quantity);
				jsonSubChild.put("$unit", unit);
				jsonSubChild.put("$info", facilityIdLookup.get(key));
				jsonChild.put("data", jsonSubChild);
				jsonChild.put("name", facilityNameLookup.get(key));
				jsonChild.put("id", facilityNameLookup.get(key));
				jsonArray.add(jsonChild);
				jsonSubChild.clear();
				jsonChild.clear();
				i++;
			}
		}
		
		jsonSubChild.put("$area", total);
		jsonSubChild.put("$unit", unit);
		jsonParent.put("data", jsonSubChild);
		jsonParent.put("children", jsonArray);
		jsonParent.put("id", "root");
		jsonParent.put("name", "GHG Quantities");
		
		log.info("treeChartSupplier completed...");
		
		return jsonParent;
	}
}
