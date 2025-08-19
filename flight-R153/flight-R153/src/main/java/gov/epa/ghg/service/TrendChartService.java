package gov.epa.ghg.service;

import java.io.Serializable;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimSectorDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.DimSubSectorDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dao.PubFactsAggregatedEmissionDao;
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
public class TrendChartService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimSectorDao sectorDao;
	
	@Inject
	DimSubSectorDao subSectorDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	PubFactsAggregatedEmissionDao aggregatedEmissionDao;
	
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
	
	@Resource(name = "helplinkurlbase")
	private String helplinkurlbase;
	
	public JSONObject sectorYearlyTrend(String q, int year, String lowE, String highE, String state, String countyFips, String msaCode, String basinCode, String dataType,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId, Byte sectorOpt) {
		
		log.info("SectorTrend started...");
		
		List<Object[]> results = pubFactsDao.getSectorYearlyTrend(q, year, lowE, highE, state, countyFips, msaCode, basinCode, dataType,
				gases, sectors, qo, emissionsType, tribalLandId);
		
		String mode = EMPTY;
		String domain = EMPTY;
		mode = "STATE";
		if (StringUtils.hasLength(basinCode) && ((StringUtils.hasLength(dataType) && dataType.equalsIgnoreCase("O")) || dataType.equalsIgnoreCase("B"))) {
			domain = basinLayerDao.getBasinByCode(basinCode).getBasin() + " - ";
		} else {
			domain = "U.S. - ";
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
		
		Map<String, Map<String, BigDecimal>> sectorMap =
				new LinkedHashMap<String, Map<String, BigDecimal>>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, String> years = new TreeMap<String, String>();
		if (sectorOpt == 1) {
			sectorMap.put("Total Reported Emissions", new LinkedHashMap<String, BigDecimal>());
		}
		for (Object[] result : (List<Object[]>) results) {
			String sector = (String) result[0];
			String color = (String) result[1];
			String tyear = Long.toString((Long) result[2]);
			BigDecimal emission = (BigDecimal) result[3];
			if (!years.containsKey(tyear)) {
				years.put(tyear, tyear);
			}
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!sectorMap.containsKey(sector)) {
					sectorMap.put(sector, new LinkedHashMap<String, BigDecimal>());
					paletteMap.put(sector, color);
				}
				Map<String, BigDecimal> yearMap = sectorMap.get(sector);
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				} else {
					yearMap.put(tyear, yearMap.get(tyear).add(emission));
				}
				if (sectorOpt == 1) {
					yearMap = sectorMap.get("Total Reported Emissions");
					if (!yearMap.containsKey(tyear)) {
						yearMap.put(tyear, emission);
					} else {
						yearMap.put(tyear, yearMap.get(tyear).add(emission));
					}
				}
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		JSONObject xAxis = new JSONObject();
		for (String category : years.keySet()) {
			categories.add(category);
		}
		xAxis.put("categories", categories);
		jsonParent.put("xAxis", xAxis);
		jsonParent.put("yearRange", categories);
		
		JSONArray series = new JSONArray();
		JSONObject item = new JSONObject();
		JSONArray emissionsArray = new JSONArray();
		List<DimSector> emitterSectors = sectorDao.getEmitterSectors();
		if (sectorOpt == 1) {
			emitterSectors.add(new DimSector("Total", "Total Reported Emissions"));
		}
		for (DimSector sector : emitterSectors) {
			String sectorName = sector.getSectorName();
			Map<String, BigDecimal> yearMap = sectorMap.get(sectorName);
			if (yearMap != null) {
				int i = 0;
				for (String tyear : years.keySet()) {
					if (yearMap.get(tyear) != null) {
						if (ServiceUtils.PetroleumAndNaturalGasSystems.equals(sectorName)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else if (ServiceUtils.Chemicals.equals(sectorName) && !isChemicals2010Comparable(sectors)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else if (ServiceUtils.Other.equals(sectorName) && !isOther2010Comparable(sectors)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else if (ServiceUtils.Waste.equals(sectorName) && !isWaste2010Comparable(sectors)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else if (ServiceUtils.Metals.equals(sectorName) && !isMetals2010Comparable(sectors)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else if (sectorOpt == 1 && ServiceUtils.TotalReportedEmissions.equals(sectorName) && !(sectorMap.size() == 2) && !isTotals2010Comparable(sectors)) {
							addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
						} else {
							if (!(sectorMap.size() == 2 && ServiceUtils.TotalReportedEmissions.equals(sectorName))) {
								emissionsArray.add(ServiceUtils.convert(yearMap.get(tyear), unit, 1));
							}
						}
					}
					i++;
				}
			}
			if (!emissionsArray.isEmpty()) {
				String name = sectorName;
				if (ServiceUtils.TotalReportedEmissions.equals(sectorName)
						&& (!sectors.isPetroleumAndNaturalGas() || !sectors.isPetroleumAndNaturalGas() || !sectors.isRefineries() || !sectors.isChemicals() || !sectors.isOther()
						|| !sectors.isWaste() || !sectors.isMetals() || !sectors.isMinerals() || !sectors.isPulpAndPaper())) {
					name += " from Selected Industries";
				}
				if (StringUtils.hasLength(dataType)) {
					if (dataType.equalsIgnoreCase("O")) {
						name = "Onshore Oil & Gas Production";
					} else if (dataType.equalsIgnoreCase("L")) {
						name = "Local Distribution Companies";
					}
				}
				item.put("name", name);
				item.put("color", paletteMap.get(sectorName) != null ? paletteMap.get(sectorName) : "#000000");
				item.put("data", emissionsArray);
				emissionsArray.clear();
				series.add(item);
				item.clear();
			}
		}
		
		jsonParent.put("series", series);
		jsonParent.put("subtitle", "Go to <a target='_blank' href='http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139279'>http://goo.gl/57sQhk</a> to learn more about trends.");
		jsonParent.put("credits", dataCredit);
		
		log.info("SectorTrend completed...");
		
		return jsonParent;
	}
	
	public JSONObject trendRR(String q, int year, String lowE, String highE, String state, String countyFips, String msaCode, String basinCode, String dataType,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs, String emissionsType, Long tribalLandId, Byte sectorOpt) {
		
		log.info("trendRR started...");
		
		List<Object[]> results = pubFactsDao.getTrendRR(q, year, lowE, highE, state, countyFips, msaCode, basinCode, dataType,
				gases, sectors, qo, emissionsType, tribalLandId);
		
		String mode = EMPTY;
		String domain = EMPTY;
		mode = "STATE";
		domain = "U.S. - ";
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
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR1");
		
		Map<String, Map<String, BigDecimal>> sectorMap =
				new LinkedHashMap<String, Map<String, BigDecimal>>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, String> years = new TreeMap<String, String>();
		sectorMap.put("Total Reported Emissions", new LinkedHashMap<String, BigDecimal>());
		for (Object[] result : (List<Object[]>) results) {
			String sector = (String) result[0];
			String color = (String) result[1];
			String tyear = Long.toString((Long) result[2]);
			BigDecimal emission = (BigDecimal) result[3];
			if (!years.containsKey(tyear)) {
				years.put(tyear, tyear);
			}
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!sectorMap.containsKey(sector)) {
					sectorMap.put(sector, new LinkedHashMap<String, BigDecimal>());
					paletteMap.put(sector, color);
				}
				Map<String, BigDecimal> yearMap = sectorMap.get(sector);
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				} else {
					yearMap.put(tyear, yearMap.get(tyear).add(emission));
				}
				yearMap = sectorMap.get("Total Reported Emissions");
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				} else {
					yearMap.put(tyear, yearMap.get(tyear).add(emission));
				}
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		JSONObject xAxis = new JSONObject();
		for (String category : years.keySet()) {
			categories.add(category);
		}
		xAxis.put("categories", categories);
		jsonParent.put("xAxis", xAxis);
		jsonParent.put("yearRange", categories);
		
		JSONArray series = new JSONArray();
		JSONObject item = new JSONObject();
		JSONArray emissionsArray = new JSONArray();
		List<DimSector> emitterSectors = sectorDao.getSectorsByType("I");
		
		for (DimSector sector : emitterSectors) {
			String sectorName = sector.getSectorName();
			Map<String, BigDecimal> yearMap = sectorMap.get(sectorName);
			if (yearMap != null) {
				int i = 0;
				for (String tyear : years.keySet()) {
					if (yearMap.get(tyear) != null) {
						addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
					}
					i++;
				}
			}
			if (!emissionsArray.isEmpty()) {
				String name = sectorName;
				
				item.put("name", name);
				item.put("color", paletteMap.get(sectorName) != null ? paletteMap.get(sectorName) : "#000000");
				item.put("data", emissionsArray);
				emissionsArray.clear();
				series.add(item);
				item.clear();
			}
		}
		
		jsonParent.put("series", series);
		jsonParent.put("subtitle", "Go to <a target='_blank' href='http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139279'>http://goo.gl/57sQhk</a> to learn more about trends.");
		jsonParent.put("credits", dataCredit);
		
		log.info("trendRR completed...");
		
		return jsonParent;
	}
	
	public JSONObject aggregatedSectorYearlyTrend(int sc, String dataType) {
		
		log.info("SectorTrend started...");
		
		List<Object[]> results;
		
		if ("S".equals(dataType)) {
			results = aggregatedEmissionDao.getSuppliersSectorYearlyTrend(sc);
		} else {
			results = aggregatedEmissionDao.getCO2InjectionSectorYearlyTrend(sc);
		}
		
		String mode = EMPTY;
		String domain = EMPTY;
		mode = "STATE";
		domain = "U.S. - ";
		if ("S".equals(dataType)) {
			domain += ServiceUtils.getSupplierType(sc);
		} else {
			domain += ServiceUtils.getCO2InjectionType(sc);
		}
		String sectorName = EMPTY;
		if (results.size() > 0) {
			sectorName = (String) results.get(0)[0];
		}
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("view", "SECTOR1");
		
		Map<String, Map<String, BigDecimal>> sectorMap =
				new LinkedHashMap<String, Map<String, BigDecimal>>();
		Map<String, String> paletteMap = new HashMap<String, String>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, String> years = new TreeMap<String, String>();
		sectorMap.put("Total Reported Emissions", new LinkedHashMap<String, BigDecimal>());
		String vNotes = "";
		for (Object[] result : (List<Object[]>) results) {
			String sector = (String) result[0];
			String color = "#000000";
			String tyear = Long.toString((Long) result[1]);
			BigDecimal emission = (BigDecimal) result[2];
			vNotes = (String) result[3];
			if (!years.containsKey(tyear)) {
				years.put(tyear, tyear);
			}
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!sectorMap.containsKey(sector)) {
					sectorMap.put(sector, new LinkedHashMap<String, BigDecimal>());
					paletteMap.put(sector, color);
				}
				Map<String, BigDecimal> yearMap = sectorMap.get(sector);
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				} else {
					yearMap.put(tyear, yearMap.get(tyear).add(emission));
				}
				yearMap = sectorMap.get("Total Reported Emissions");
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				} else {
					yearMap.put(tyear, yearMap.get(tyear).add(emission));
				}
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("domain", domain);
		jsonParent.put("mode", mode);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		JSONObject xAxis = new JSONObject();
		for (String category : years.keySet()) {
			categories.add(category);
		}
		xAxis.put("categories", categories);
		jsonParent.put("xAxis", xAxis);
		jsonParent.put("yearRange", categories);
		
		JSONArray series = new JSONArray();
		JSONObject item = new JSONObject();
		JSONArray emissionsArray = new JSONArray();
		List<DimSector> emitterSectors = new ArrayList<DimSector>();
		emitterSectors.add(new DimSector("Total", sectorName));
		
		Map<String, BigDecimal> yearMap = sectorMap.get(sectorName);
		if (yearMap != null) {
			int i = 0;
			for (String tyear : years.keySet()) {
				if (yearMap.get(tyear) != null) {
					addToEmissions(emissionsArray, sectorName, tyear, yearMap, unit, i);
				}
				i++;
			}
		}
		if (!emissionsArray.isEmpty()) {
			String name = sectorName;
			
			if (StringUtils.hasLength(dataType)) {
				if (dataType.equalsIgnoreCase("S")) {
					name = ServiceUtils.getSupplierType(sc);
				} else if (dataType.equalsIgnoreCase("I")) {
					name = ServiceUtils.getCO2InjectionType(sc);
				}
			}
			item.put("name", name);
			item.put("color", paletteMap.get(sectorName) != null ? paletteMap.get(sectorName) : "#000000");
			item.put("data", emissionsArray);
			emissionsArray.clear();
			series.add(item);
			item.clear();
		}
		
		jsonParent.put("series", series);
		jsonParent.put("subtitle", "Go to <a target='_blank' href='http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139279'>http://goo.gl/57sQhk</a> to learn more about trends.");
		jsonParent.put("credits", dataCredit);
		jsonParent.put("vNotes", vNotes);
		log.info("SectorTrend completed...");
		
		return jsonParent;
	}
	
	public JSONObject supplierHasTrend(String ds, int sc) {
		boolean hasTrend;
		
		if (sc == 0) {
			hasTrend = false;
		} else {
			hasTrend = aggregatedEmissionDao.supplierHasTrend(sc);
		}
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("hasTrend", hasTrend);
		
		return jsonParent;
	}
	
	public JSONObject co2InjectionHasTrend(String ds, int sc) {
		boolean hasTrend = aggregatedEmissionDao.co2InjectionHasTrend(sc);
		
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("hasTrend", hasTrend);
		
		return jsonParent;
	}
	
	private void addToEmissions(JSONArray emissions, String sector, String year, Map<String, BigDecimal> yearMap, String unit, int index) {
		JSONObject value = new JSONObject();
		value.put("x", index);
		value.put("y", ServiceUtils.convert(yearMap.get(year), unit, 1));
		if ("2010".equals(year)) {
			// sectorEmissions.add(value);
		} else if ("2011".equals(year)) {
			// sectorEmissions.add(value);
			emissions.add(value);
		} else {
			emissions.add(value);
		}
	}
	
	private boolean isChemicals2010Comparable(SectorFilter sectors) {
		if (sectors.isChemicals() && sectors.isS703()) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isOther2010Comparable(SectorFilter sectors) {
		if (sectors.isOther() && (sectors.isS801() || sectors.isS807() || sectors.isS808() || sectors.isS808())) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isWaste2010Comparable(SectorFilter sectors) {
		if (sectors.isWaste() && (sectors.isS202() || sectors.isS203())) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isMetals2010Comparable(SectorFilter sectors) {
		if (sectors.isMetals() && (sectors.isS305())) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isTotals2010Comparable(SectorFilter sectors) {
		if (sectors.isPetroleumAndNaturalGas() || !isChemicals2010Comparable(sectors) || !isOther2010Comparable(sectors) || !isWaste2010Comparable(sectors)
				|| !isMetals2010Comparable(sectors)) {
			return false;
		} else {
			return true;
		}
	}
}
