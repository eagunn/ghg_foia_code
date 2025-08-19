package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.DimSubSectorDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.dto.Emission;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
@Service
@Transactional
public class PieChartService implements Serializable {
	
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
	DimMsaDao msaDao;
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Resource(name = "dataCredit")
	private String dataCredit;
	
	private static final int maxFacilities = 55;
	private static final int maxCounties = 55;
	private static final String[] colorArr = new String[]{"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB", "#AA88AA"};
	
	public JSONObject pieChartSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieChartSector started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSector(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		if (results.size() == 1) {
			return pieChartSectorL2(q, year, lowE, highE, state, countyFips,
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
		
		List<String> palette = new ArrayList<String>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, Emission> emissionsMap = new HashMap<String, Emission>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			String color = (String) result[2];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				// emissionsSort.add(e);
				if (!emissionsMap.containsKey(key)) {
					emissionsMap.put(key, e);
				} else {
					e = emissionsMap.get(key);
					e.setEmission(e.getEmission().add(emission));
				}
				if (!paletteMap.containsKey(key)) {
					paletteMap.put(key, color);
				}
			}
		}
		
		for (Emission e : emissionsMap.values()) {
			emissionsSort.add(e);
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
				palette.add(paletteMap.get(e.getKey()));
				// jsonChild.put("label", e.getKey());
				// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
				jsonChild.put("name", e.getKey());
				jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
				jsonChild.put("color", paletteMap.get(e.getKey()));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartSector completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartSectorL2(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieChartSectorL2 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSectorLevel2(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		if (results.size() == 1) {
			String ss = (String) results.get(0)[0];
			return pieChartSectorL3(ss, q, year, lowE, highE, state, countyFips,
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
				domain = domain + dc.getCountyName() + " - " + ServiceUtils.getSector(sectors) + " - ";
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
		
		if (results.size() > 0) {
			jsonParent.put("sectorId", (Long) results.get(0)[2]);
		}
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		int colorIndex = 0;
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
				// jsonChild.put("label", e.getKey());
				// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
				jsonChild.put("name", e.getKey());
				jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
				jsonChild.put("color", colorArr[colorIndex]);
				if (colorIndex == colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		// jsonParent.put("colors", colorArr);
		// jsonParent.put("values", jsonArray);
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartSectorL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartSectorL3(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieChartSectorL3 started...");
		
		if (ss.equals("Offshore Petroleum ")) {
			ss = "Offshore Petroleum & Natural Gas Production";
		}
		
		if ("Onshore Petroleum ".equals(ss)) {
			return pieChartBasin(q, year, lowE, highE, "", gases, sectors, qo, rs, ds);
		} else {
			List<Object[]> results = pubFactsDao.getPieTreeSectorLevel3(ss, q, year, lowE, highE, state, countyFips,
					msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
			if (!StringUtils.hasLength(state) && results.size() > maxFacilities && !ds.equals("F")) {
				return pieChartSectorL4(ss, q, year, lowE, highE, state, countyFips,
						msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
			}
			
			String mode = EMPTY;
			String domain = EMPTY;
			if (!StringUtils.hasLength(state)) {
				mode = "STATE";
				domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
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
				DimMsa dm = msaDao.getMsaByCode(msaCode);
				if (dm != null) {
					domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
				}
				if (tribalLandId != null) {
					LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
					if (tl != null) {
						domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
					}
				}
			}
			
			JSONObject jsonParent = new JSONObject();
			jsonParent.put("view", "SECTOR3");
			
			if (results.size() > 0) {
				jsonParent.put("sectorId", (Long) results.get(0)[3]);
			}
			
			Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
			Map<String, String> facilityNameLookup = new HashMap<String, String>();
			List<BigDecimal> emissions = new ArrayList<BigDecimal>();
			List<Emission> emissionsSort = new ArrayList<Emission>();
			
			for (Object[] result : (List<Object[]>) results) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				String key = facilityName + " (" + (Long) result[2] + ")";
				BigDecimal emission = (BigDecimal) result[1];
				if (emission != null) {
					facilityIdLookup.put(key, (Long) result[2]);
					facilityNameLookup.put(key, facilityName);
					ServiceUtils.addToUnitList(emissions, emission);
					Emission e = new Emission(key, emission);
					emissionsSort.add(e);
				}
			}
			
			Collections.sort(emissionsSort, new Comparator<Emission>() {
				public int compare(Emission e1, Emission e2) {
					return e1.getEmission().compareTo(e2.getEmission());
				}
			});
			
			String unit = ServiceUtils.getUnit(emissions);
			jsonParent.put("domain", domain);
			jsonParent.put("mode", mode);
			jsonParent.put("unit", unit);
			
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonChild = new JSONObject();
			
			int colorIndex = 0;
			for (Emission e : emissionsSort) {
				if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
					// jsonChild.put("label", facilityNameLookup.get(e.getKey()));
					// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
					jsonChild.put("name", facilityNameLookup.get(e.getKey()));
					jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
					jsonChild.put("id", facilityIdLookup.get(e.getKey()));
					jsonChild.put("color", colorArr[colorIndex]);
					if (colorIndex == colorArr.length - 1) {
						colorIndex = 0;
					} else {
						colorIndex++;
					}
					jsonArray.add(jsonChild);
					jsonChild.clear();
				}
			}
			
			// jsonParent.put("values", jsonArray);
			
			JSONArray series = new JSONArray();
			JSONObject data = new JSONObject();
			data.put("type", "pie");
			data.put("name", "Emissions");
			data.put("data", jsonArray);
			series.add(data);
			// hc.put("series", series);
			jsonParent.put("series", series);
			
			jsonParent.put("credits", dataCredit);
			
			log.info("pieChartSectorL3 completed...");
			
			return jsonParent;
		}
	}
	
	public JSONObject pieChartSectorL4(String ss, String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieChartSectorL4 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSectorLevel4(ss, q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
		
		String mode = EMPTY;
		String domain = EMPTY;
		if (!StringUtils.hasLength(state)) {
			mode = "STATE";
			domain = "U.S. - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
		} else if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode) && tribalLandId == null) {
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
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (dm != null) {
				domain = domain + dm.getCbsa_title().split(",")[0] + " Metro Area - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
			}
			if (tribalLandId != null) {
				LuTribalLands tl = tribalLandsDao.findById(tribalLandId);
				if (tl != null) {
					domain = domain + tl.getTribalLandName() + " - " + ServiceUtils.getSector(sectors) + " - " + ss + " - ";
				}
			}
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR4");
		jsonParent.put("subsector", ss);
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		int colorIndex = 0;
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
				// jsonChild.put("label", e.getKey());
				// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
				jsonChild.put("name", e.getKey());
				jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
				jsonChild.put("color", colorArr[colorIndex]);
				if (colorIndex == colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartSectorL4 completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartGas(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		List<Object[]> results = pubFactsDao.getBarPieTreeGas(q, year, lowE, highE, state, countyFips,
				msaCode, gases, sectors, qo, rs);
		
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		JSONObject jsonParent = new JSONObject();
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
				jsonChild.put("label", e.getKey());
				jsonChild.put("values", ServiceUtils.convert(e.getEmission(), unit));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		return jsonParent;
	}
	
	public JSONObject pieChartState(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("pieChartState started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeState(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		if (results.size() == 1) {
			state = (String) results.get(0)[2];
			return pieChartStateL2(q, year, lowE, highE, state, countyFips,
					gases, sectors, qo, rs);
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "STATE1");
		
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
				keyMap.put(key, emission);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(keyMap.get(e.getKey()), unit) > 0L) {
				jsonChild.put("label", e.getKey());
				jsonChild.put("values", ServiceUtils.convert(keyMap.get(e.getKey()), unit));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartState completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartStateL2(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("pieChartStateL2 started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeStateLevel2(q, year, lowE, highE, state, countyFips,
				gases, sectors, qo, rs);
		if (!StringUtils.hasLength(countyFips) && results.size() > maxFacilities) {
			return pieChartStateL3(q, year, lowE, highE, state, countyFips,
					gases, sectors, qo, rs);
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "STATE2");
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : (List<Object[]>) results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
				keyMap.put(key, emission);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(keyMap.get(e.getKey()), unit) > 0L) {
				// jsonChild.put("label", org.apache.commons.lang.StringUtils.substring(facilityNameLookup.get(e.getKey()),0,18));
				jsonChild.put("label", facilityNameLookup.get(e.getKey()));
				// jsonChild.put("facility", facilityNameLookup.get(e.getKey()));
				jsonChild.put("info", facilityIdLookup.get(e.getKey()));
				jsonChild.put("values", ServiceUtils.convert(keyMap.get(e.getKey()), unit));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartStateL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartStateL3(String q, int year, String lowE, String highE, String state, String countyFips,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("pieChartStateL3 started...");
		
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
		
		if (emissionsSort.size() <= maxCounties) {
			// Lowest measure to highest measure
			for (int i = 0; i < emissionsSort.size(); i++) {
				Emission e = emissionsSort.get(i);
				jsonChild.put("label", e.getKey());
				jsonChild.put("info", countyLookup.get(e.getKey()));
				jsonChild.put("values", ServiceUtils.convert(e.getEmission(), unit));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		} else {
			// Highest measure to lowest measure
			for (int i = emissionsSort.size() - maxCounties + 1; i < emissionsSort.size(); i++) {
				Emission e = emissionsSort.get(i);
				jsonChild.put("label", e.getKey());
				jsonChild.put("info", countyLookup.get(e.getKey()));
				jsonChild.put("values", ServiceUtils.convert(e.getEmission(), unit));
				jsonArray.add(jsonChild);
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
				jsonChild.put("label", "Other");
				jsonChild.put("info", "");
				jsonChild.put("values", total);
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		jsonParent.put("values", jsonArray);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartStateL3 completed...");
		
		return jsonParent;
		
		/*int max = 55;
		if(results.size() < max)
			max = results.size();
		for(int i = 0; i < max; i++){
			if(((Object [])results.get(i))[2] != null)
				jsonChild.put("label", ((Object [])results.get(i))[0]+" - "+((Object [])results.get(i))[2].toString());
			else 
				jsonChild.put("label", ((Object [])results.get(i))[0]);
			jsonChild.put("values", ((Object [])results.get(i))[1]);
			jsonArray.add(jsonChild);
			jsonChild.clear();
		}
		if(results.size() > 55){
			long sumTotal = 0;
			for(int i = 55; i < results.size(); i++){
				sumTotal += (long) Float.parseFloat(((Object [])results.get(i))[1].toString());
			}
			jsonChild.put("label", "Other");
			jsonChild.put("values", sumTotal);
			jsonArray.add(jsonChild);
			jsonChild.clear();
		}
		jsonParent.put("values", jsonArray);
		return jsonParent;*/
	}
	
	public JSONObject pieChartBasin(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String ds) {
		
		log.info("pieChartBasin started...");
		
		if (StringUtils.hasLength(basin)) {
			return pieChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
		} else {
			List<Object[]> results = pubFactsDao.getBarBasin(q, year, lowE, highE, basin, gases, sectors, qo, rs);
			if (results.size() == 1) {
				return pieChartBasinL2(q, year, lowE, highE, basin, gases, sectors, qo, rs);
			}
			
			String mode = EMPTY;
			String domain = EMPTY;
			if (!StringUtils.hasLength(basin)) {
				mode = "STATE";
				domain = "Onshore Oil and Natural Gas Production - ";
				if (StringUtils.hasLength(ds) && "B".equals(ds)) {
					domain = FacilityType.getFullName(ds) + " - ";
				}
			}
			
			JSONObject jsonParent = new JSONObject();
			jsonParent.put("view", "BASIN1");
			
			List<BigDecimal> emissions = new ArrayList<BigDecimal>();
			List<Emission> emissionsSort = new ArrayList<Emission>();
			
			for (Object[] result : results) {
				String key = (String) result[2];
				BigDecimal emission = (BigDecimal) result[1];
				if (emission != null) {
					ServiceUtils.addToUnitList(emissions, emission);
					Emission e = new Emission(key, emission);
					emissionsSort.add(e);
				}
			}
			
			Collections.sort(emissionsSort, new Comparator<Emission>() {
				public int compare(Emission e1, Emission e2) {
					return e1.getEmission().compareTo(e2.getEmission());
				}
			});
			
			String unit = ServiceUtils.getUnit(emissions);
			jsonParent.put("domain", domain);
			jsonParent.put("mode", mode);
			jsonParent.put("unit", unit);
			
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonChild = new JSONObject();
			
			int colorIndex = 0;
			for (Emission e : emissionsSort) {
				if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
					// jsonChild.put("label", basinLayerDao.getBasinByCode(e.getKey()).getBasin());
					// jsonChild.put("info", e.getKey());
					// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
					jsonChild.put("name", basinLayerDao.getBasinByCode(e.getKey()).getBasin());
					jsonChild.put("basin", e.getKey());
					jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
					jsonChild.put("color", colorArr[colorIndex]);
					if (colorIndex == colorArr.length - 1) {
						colorIndex = 0;
					} else {
						colorIndex++;
					}
					jsonArray.add(jsonChild);
					jsonChild.clear();
				}
			}
			
			// jsonParent.put("values", jsonArray);
			
			JSONArray series = new JSONArray();
			JSONObject data = new JSONObject();
			data.put("type", "pie");
			data.put("name", "Emissions");
			data.put("data", jsonArray);
			series.add(data);
			// hc.put("series", series);
			jsonParent.put("series", series);
			
			jsonParent.put("credits", dataCredit);
			
			log.info("pieChartBasin completed...");
			
			return jsonParent;
		}
	}
	
	public JSONObject pieChartBasinL2(String q, int year, String lowE, String highE, String basin,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs) {
		
		log.info("pieChartBasinL2 started...");
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		List<Object[]> results = pubFactsDao.getBarBasinL2(q, year, lowE, highE, basin,
				gases, sectors, qo, rs);
		
		for (Object[] result : (List<Object[]>) results) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
			String key = facilityName + " (" + (Long) result[2] + ")";
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				facilityIdLookup.put(key, (Long) result[2]);
				facilityNameLookup.put(key, facilityName);
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				emissionsSort.add(e);
				keyMap.put(key, emission);
			}
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "BASIN2");
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		jsonParent.put("label", "Emissions");
		jsonParent.put("domain", basinLayerDao.getBasinByCode(basin).getBasin() + " - ");
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		int colorIndex = 0;
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(keyMap.get(e.getKey()), unit) > 0L) {
				// jsonChild.put("label", facilityNameLookup.get(e.getKey()));
				// jsonChild.put("info", facilityIdLookup.get(e.getKey()));
				// jsonChild.put("values", ServiceUtils.convertEmitter(keyMap.get(e.getKey()), unit));
				jsonChild.put("name", facilityNameLookup.get(e.getKey()));
				jsonChild.put("id", facilityIdLookup.get(e.getKey()));
				jsonChild.put("y", ServiceUtils.convert(keyMap.get(e.getKey()), unit));
				jsonChild.put("color", colorArr[colorIndex]);
				if (colorIndex == colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		// jsonParent.put("values", jsonArray);
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartBasinL2 completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieChartSupplier(String q, QueryOptions qo, int year, int sc, String rs, String state) {
		
		log.info("pieChartSupplier started...");
		
		Map<String, Long> facilityIdLookup = new HashMap<String, Long>();
		Map<String, String> facilityNameLookup = new HashMap<String, String>();
		Map<String, BigDecimal> keyMap = new HashMap<String, BigDecimal>();
		List<BigDecimal> quantities = new ArrayList<BigDecimal>();
		List<Emission> quantitiesSort = new ArrayList<Emission>();
		
		if (sc != 0) {
			List<Object[]> results = pubFactsDao.getSupplierPieChart(q, qo, year, sc, rs, state);
			
			for (Object[] result : results) {
				String facilityName = ServiceUtils.nullSafeHtmlUnescape((String) result[0]);
				String key = facilityName + " (" + (Long) result[2] + ")";
				BigDecimal quantity = (BigDecimal) result[1];
				if (quantity != null) {
					facilityIdLookup.put(key, (Long) result[2]);
					facilityNameLookup.put(key, facilityName);
					ServiceUtils.addToUnitList(quantities, quantity);
					Emission e = new Emission(key, quantity);
					quantitiesSort.add(e);
					keyMap.put(key, quantity);
				}
			}
			
			Collections.sort(quantitiesSort, new Comparator<Emission>() {
				public int compare(Emission e1, Emission e2) {
					return e1.getEmission().compareTo(e2.getEmission());
				}
			});
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SUPPLIER");
		
		String unit = ServiceUtils.getUnit(quantities);
		jsonParent.put("domain", ServiceUtils.getSupplierType(sc));
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		int colorIndex = 0;
		for (Emission e : quantitiesSort) {
			if (ServiceUtils.convert(keyMap.get(e.getKey()), unit) > 0L) {
				// jsonChild.put("label", facilityNameLookup.get(e.getKey()));
				// jsonChild.put("info", facilityIdLookup.get(e.getKey()));
				// jsonChild.put("values", ServiceUtils.convertEmitter(keyMap.get(e.getKey()), unit));
				jsonChild.put("name", facilityNameLookup.get(e.getKey()));
				jsonChild.put("id", facilityIdLookup.get(e.getKey()));
				jsonChild.put("y", ServiceUtils.convert(keyMap.get(e.getKey()), unit));
				jsonChild.put("color", colorArr[colorIndex]);
				if (colorIndex == colorArr.length - 1) {
					colorIndex = 0;
				} else {
					colorIndex++;
				}
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		// jsonParent.put("values", jsonArray);
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieChartSupplier completed...");
		
		return jsonParent;
	}
	
	public JSONObject pieRR(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieRR started...");
		
		List<Object[]> results = pubFactsDao.getPieRR(q, year, lowE, highE, state, countyFips,
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
		
		List<String> palette = new ArrayList<String>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, Emission> emissionsMap = new HashMap<String, Emission>();
		List<Emission> emissionsSort = new ArrayList<Emission>();
		
		for (Object[] result : results) {
			String key = (String) result[0];
			String color = (String) result[2];
			BigDecimal emission = (BigDecimal) result[1];
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				Emission e = new Emission(key, emission);
				// emissionsSort.add(e);
				if (!emissionsMap.containsKey(key)) {
					emissionsMap.put(key, e);
				} else {
					e = emissionsMap.get(key);
					e.setEmission(e.getEmission().add(emission));
				}
				if (!paletteMap.containsKey(key)) {
					paletteMap.put(key, color);
				}
			}
		}
		
		for (Emission e : emissionsMap.values()) {
			emissionsSort.add(e);
		}
		
		Collections.sort(emissionsSort, new Comparator<Emission>() {
			public int compare(Emission e1, Emission e2) {
				return e1.getEmission().compareTo(e2.getEmission());
			}
		});
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonChild = new JSONObject();
		
		for (Emission e : emissionsSort) {
			if (ServiceUtils.convert(e.getEmission(), unit) > 0L) {
				palette.add(paletteMap.get(e.getKey()));
				// jsonChild.put("label", e.getKey());
				// jsonChild.put("values", ServiceUtils.convertEmitter(e.getEmission(), unit));
				jsonChild.put("name", e.getKey());
				jsonChild.put("y", ServiceUtils.convert(e.getEmission(), unit));
				jsonChild.put("color", paletteMap.get(e.getKey()));
				jsonArray.add(jsonChild);
				jsonChild.clear();
			}
		}
		
		JSONArray series = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("type", "pie");
		data.put("name", "Emissions");
		data.put("data", jsonArray);
		series.add(data);
		jsonParent.put("series", series);
		
		jsonParent.put("credits", dataCredit);
		
		log.info("pieRR completed...");
		
		return jsonParent;
	}
}
