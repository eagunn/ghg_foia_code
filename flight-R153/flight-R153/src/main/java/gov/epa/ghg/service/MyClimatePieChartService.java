package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateDao;
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
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
@Service
@Transactional
public class MyClimatePieChartService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimStateDao stateDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	DimMsaDao msaDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Resource(name = "dataCredit")
	private String dataCredit;
	
	public JSONObject pieChartSector(String q, int year, String lowE, String highE, String state, String countyFips,
			String msaCode, GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType, Long tribalLandId) {
		
		log.info("pieChartSector started...");
		
		List<Object[]> results = pubFactsDao.getPieTreeSector(q, year, lowE, highE, state, countyFips,
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
		
		log.info("pieChartSector completed...");
		
		return jsonParent;
	}
}
