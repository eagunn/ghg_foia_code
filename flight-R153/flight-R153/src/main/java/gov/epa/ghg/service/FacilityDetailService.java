package gov.epa.ghg.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.DimFacilityDaoInterface;
import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.dao.FacilitySubpartKeyValDao;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.FacilitySubpartKeyVal;
import gov.epa.ghg.domain.LuKey;
import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ParentComparator;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
@Service
@Transactional
public class FacilityDetailService implements Serializable, FacilityDetailInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimFacilityDaoInterface dimFacilityDao;
	
	@Inject
	FacilitySubpartKeyValDao facilitySubpartKeyValDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	public FacilityDetail getFacilityDetails(Long id, int year, String ds, String emissionsType) {
		
		FacilityDetail fd = new FacilityDetail();
		try {
			
			final Long facilityId = id;
			final int rYear = year;
			String dataSource = ds;
			if ("A".equals(ds)) {
				dataSource = "I";
			}
			DimFacility facility = this.getFacilityForFacilityDetail(id, year, ds);
			
			// if null, just return a blank object, that's what it's been doing so I'm keeping it this way
			if (facility == null) {
				return fd;
			} else {
				fd.setFacility(facility);
			}
			this.addParentCompaniesToFacilityDetail(fd, facility.getParentCompany());
			
			if ("E".equals(dataSource)
					|| "O".equals(dataSource)
					|| "L".equals(dataSource)
					|| "F".equals(dataSource)
					|| "P".equals(dataSource)
					|| "B".equals(dataSource)
					|| "I".equals(dataSource)) {
				fd.setGasEmissions(pubFactsDao.getGasEmissions(fd, facilityId, rYear, dataSource, emissionsType));
				fd.setSubpartEmissions(pubFactsDao.getSubpartEmissions(facilityId, rYear, dataSource));
			}
			
			fd.setSubpartDetails(new LinkedHashMap<String, List<FacilitySubpartKeyVal>>());
			
			List<FacilitySubpartKeyVal> facSubKeyVals = facilitySubpartKeyValDao.getByIdAndYear(id, year);
			
			boolean foundW = false;
			boolean foundNN = false;
			String nnKey = null;
			boolean wMulti = false;
			List<LuKey> wIndustries = facilitySubpartKeyValDao.getLuKeybyName(id, year, "W_INDUSTRY_SEGMENTS");
			if (wIndustries.size() > 1) {
				wMulti = true;
			}
			for (FacilitySubpartKeyVal kv : facSubKeyVals) {
				String key = kv.resolveKey(wMulti);
				
				if ("W".equals(kv.getSubpart().getSubpartName())) {
					foundW = true;
				}
				if ("NN".equals(kv.getSubpart().getSubpartName())) {
					foundNN = true;
					nnKey = key;
				}
				
				if (!fd.getSubpartDetails().containsKey(key)) {
					fd.getSubpartDetails().put(key, new ArrayList<FacilitySubpartKeyVal>());
				}
				FacilitySubpartKeyVal fkv = new FacilitySubpartKeyVal();
				fkv.setLuKey(new LuKey(kv.getLuKey().getKeyId(), kv.getLuKey().getKeyName(), kv.getLuKey().getKeyDescription()));
				fkv.setValue(kv.getValue());
				fkv.setNotes(kv.getNotes());
				fd.getSubpartDetails().get(key).add(fkv);
			}
			
			List<String> reportingYearList = dimFacilityDao.getFacReportingYears(id);
			fd.setFacReportingYears((List<String>) reportingYearList);
			
			// PUB-592: show subpart W attributes before subpart NN attributes: take NN out and then put it back to make it last
			if (foundW && foundNN) {
				List<FacilitySubpartKeyVal> nnValue = fd.getSubpartDetails().remove(nnKey);
				fd.getSubpartDetails().put(nnKey, nnValue);
			}
			
			List yearEmissions = pubFactsDao.getFacilityTrend(id, ds, emissionsType);
			if (yearEmissions.size() > 0) {
				fd.setHasTrend(true);
			}
			List bNotes = facilitySubpartKeyValDao.getKeyNotes(id, "9");
			List tNotes = facilitySubpartKeyValDao.getKeyNotes(id, "10");
			if (yearEmissions.size() > 1 && (bNotes.size() > 0 || tNotes.size() > 0)) {
				fd.setHasBT(true);
			}
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
		return fd;
	}
	
	public FacilityHoverTip getFacilityHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs) {
		return dimFacilityDao.getFacilityHoverTip(id, year, ds, emissionsType, rs);
	}
	
	public String getFacilityName(Long id, int year) throws UnsupportedEncodingException {
		DimFacility f = dimFacilityDao.findByFacilityIdAndReportingYear(id, year);
		if (f != null) {
			return f.getFacilityName();
		} else {
			return EMPTY;
		}
	}
	
	public JSONObject getFacilityTrend(Long id, String ds, String yr, String emissionsType) {
		
		List<Object[]> results = pubFactsDao.getFacilityTrend(id, ds, emissionsType);
		
		JSONObject jsonParent = new JSONObject();
		
		Map<String, BigDecimal> yearMap = new TreeMap<String, BigDecimal>();
		List<BigDecimal> emissions = new ArrayList<BigDecimal>();
		Map<String, String> years = new TreeMap<String, String>();
		
		for (Object[] result : (List<Object[]>) results) {
			String tyear = Long.toString((Long) result[0]);
			BigDecimal emission = (BigDecimal) result[1];
			years.put(tyear, tyear);
			if (emission != null) {
				ServiceUtils.addToUnitList(emissions, emission);
				if (!yearMap.containsKey(tyear)) {
					yearMap.put(tyear, emission);
				}
			}
		}
		
		if ("E".equals(ds) || "O".equals(ds) || "L".equals(ds)) {
			Map<String, String> spList = dimFacilityDao.getReportedSubparts(id, "E");
			
			if (!("2010".equals(yr)) && (spList.containsKey("I")
					|| spList.containsKey("L")
					|| spList.containsKey("T")
					|| spList.containsKey("W")
					|| spList.containsKey("DD")
					|| spList.containsKey("FF")
					|| spList.containsKey("II")
					|| spList.containsKey("SS")
					|| spList.containsKey("TT"))) {
				years.remove("2010");
			}
			
			if (spList.containsKey("L")) {
				jsonParent.put("subtitle", "* Emissions reported from Fluorinated GHG Production for 2011 and 2012 were reporting using GWP's from the IPCC's second assessment report. All other GHG data is displayed using GWPS's from the fourth assessment report.");
			}
		}
		
		String unit = ServiceUtils.getUnit(emissions);
		jsonParent.put("unit", unit);
		
		JSONArray categories = new JSONArray();
		JSONObject xAxis = new JSONObject();
		for (String category : years.keySet()) {
			categories.add(category);
		}
		xAxis.put("categories", categories);
		jsonParent.put("xAxis", xAxis);
		
		JSONArray series = new JSONArray();
		JSONObject item = new JSONObject();
		JSONArray emissionsArray = new JSONArray();
		
		for (String tyear : years.keySet()) {
			emissionsArray.add(ServiceUtils.convert(yearMap.get(tyear), unit, 1));
		}
		if ("S".equals(ds)) {
			item.put("name", "Net GHG Quantity");
		} else if ("I".equals(ds)) {
			item.put("name", "CO2 Injection (UU)");
		} else if ("A".equals(ds)) {
			item.put("name", "Geologic Sequestration of CO2 (RR)");
		} else {
			item.put("name", "Emissions");
		}
		
		item.put("data", emissionsArray);
		item.put("color", "#000");
		item.put("showInLegend", false);
		series.add(item);
		jsonParent.put("series", series);
		
		return jsonParent;
	}
	
	public FacilityDetail getLatestFacilityDetails(Long id, String ds, String emissionsType) {
		
		try {
			List<FacilitySubpartKeyVal> results = facilitySubpartKeyValDao.getById(id);
			FacilitySubpartKeyVal fkv = new FacilitySubpartKeyVal();
			FacilitySubpartKeyVal kv = new FacilitySubpartKeyVal();
			
			if (!results.isEmpty()) {
				kv = results.get(0);
				fkv.setLuKey(new LuKey(kv.getLuKey().getKeyId(), kv.getLuKey().getKeyName(), kv.getLuKey().getKeyDescription()));
				fkv.setValue(kv.getValue());
			} else {
				return null;
			}
			
			final Long facilityId = id;
			long year = kv.getId().getYear();
			final int rYear = (int) year;
			final String dataSource = ds;
			String facType = "E";
			if ("S".equals(dataSource)) {
				facType = "S";
			} else if ("I".equals(dataSource)) {
				facType = "I";
			} else if ("A".equals(dataSource)) {
				facType = "A";
			}
			
			FacilityDetail fd = new FacilityDetail();
			DimFacility f = dimFacilityDao.findDimFacilityAndHtmlByFacilityIdAndReportingYear(facilityId, rYear);
			if (f.getId() != null) {
				DimFacility df = new DimFacility();
				df.setLatitude(f.getLatitude());
				df.setLongitude(f.getLongitude());
				df.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(f.getFacilityName()));
				df.setAddress1(f.getAddress1());
				df.setAddress2(f.getAddress2());
				df.setCity(f.getCity());
				df.setState(f.getState());
				df.setZip(f.getZip());
				df.setFrsId(f.getFrsId());
				df.setNaicsCode(f.getNaicsCode());
				df.setProgramName(f.getProgramName());
				df.setProgramSysId(f.getProgramSysId());
				if (f.getParentCompany() != null) {
					String[] pc = f.getParentCompany().split(";");
					if (pc.length > 0) {
						fd.setParentCompanies(new ArrayList<String>());
						for (int i = 0; i < pc.length; i++) {
							fd.getParentCompanies().add(pc[i]);
						}
					}
				}
				df.setHtml(f.getHtml());
				df.setPublicXml(f.getPublicXml());
				df.setTribalLand(f.getTribalLand());
				df.setCemsUsed(f.getCemsUsed());
				df.setCo2Captured(f.getCo2Captured());
				df.setCo2EmittedSupplied(f.getCo2EmittedSupplied());
				df.setUuRandDExempt(f.getUuRandDExempt());
				df.setId(new DimFacilityId(f.getId().getFacilityId(), f.getId().getYear()));
				df.setComments(f.getComments());
				Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), facType);
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				df.setReportingStatus(rs);
				fd.setFacility(df);
				if ("E".equals(dataSource)
						|| "O".equals(dataSource)
						|| "L".equals(dataSource)
						|| "F".equals(dataSource)
						|| "P".equals(dataSource)
						|| "B".equals(dataSource)
						|| "I".equals(dataSource)) {
					fd.setGasEmissions(pubFactsDao.getGasEmissions(fd, facilityId, rYear, dataSource, emissionsType));
					fd.setSubpartEmissions(pubFactsDao.getSubpartEmissions(facilityId, rYear, dataSource));
				}
				
				fd.setSubpartDetails(new LinkedHashMap<String, List<FacilitySubpartKeyVal>>());
				
				if (!fd.getSubpartDetails().containsKey(kv.getSubpart().getSubpartCategory())) {
					fd.getSubpartDetails().put(kv.getSubpart().getSubpartCategory(), new ArrayList<FacilitySubpartKeyVal>());
				}
				
				fd.getSubpartDetails().get(kv.getSubpart().getSubpartCategory()).add(fkv);
			}
			
			return fd;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public FacilityDetail getLatestFacilityDetails2(Long id, String dataSource, String emissionsType) {
		
		try {
			FacilityDetail fd = new FacilityDetail();
			List<DimFacility> dfList = dimFacilityDao.findById(id);
			DimFacility df = new DimFacility();
			String facType = "E";
			if ("S".equals(dataSource)) {
				facType = "S";
			} else if ("I".equals(dataSource)) {
				facType = "I";
			} else if ("A".equals(dataSource)) {
				facType = "A";
			}
			
			for (DimFacility dfObj : dfList) {
				Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(dfObj.getId().getFacilityId(), dfObj.getId().getYear(), facType);
				ReportingStatus rs = rsMap.get(dfObj.getId().getFacilityId());
				if (rs == null) {
					df = dfObj;
					break;
				} else if (!rs.getLabel().equals("Potential Data Quality Issue") &&
						!rs.getLabel().equals("Stopped Reporting - Valid Reason")) {
					df = dfObj;
					break;
				}
			}
			
			fd.setFacility(df);
			long facilityId = df.getId().getFacilityId();
			long year = df.getId().getYear();
			int rYear = (int) year;
			
			if ("E".equals(dataSource)
					|| "O".equals(dataSource)
					|| "L".equals(dataSource)
					|| "F".equals(dataSource)
					|| "P".equals(dataSource)
					|| "B".equals(dataSource)
					|| "I".equals(dataSource)) {
				fd.setGasEmissions(pubFactsDao.getGasEmissions(fd, facilityId, rYear, dataSource, emissionsType));
				fd.setSubpartEmissions(pubFactsDao.getSubpartEmissions(facilityId, rYear, dataSource));
			}
			
			fd.setSubpartDetails(new LinkedHashMap<String, List<FacilitySubpartKeyVal>>());
			
			return fd;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
		
	}
	
	/**
	 * Taken out for now while EPA thinks it over
	 ***/
	// PUB-136: links to the other data sources' facility pages
	/*
	public List<String> checkOtherDataSources(Long id, String ds, Long subSectorId, int year, String emissionsType) {
		List<Object[]> results = pubFactsDao.getEmissionsByFacilityAndYear(id, year, emissionsType);
		
		List<String> otherDataSources = new ArrayList<String>();
		
		if (subSectorId == null) {
			subSectorId = new Long(0); //place holder if no subSectorId was passed
		}
		
		for (Object[] result : results) {
			String dataSource = null;
			Long ssId = (Long)result[3];
			String sectorType = (String)result[1];
			
			if (ssId == 62 && subSectorId != 62) {
				dataSource = "F";
				otherDataSources.add(dataSource);
			} else if (ssId == 56 && subSectorId != 56) {
				dataSource = "L";
				otherDataSources.add(dataSource);
			} else if (ssId == 53 && subSectorId != 53) {
				dataSource = "O";
				otherDataSources.add(dataSource);
			} 
			
			if (sectorType.equals("S") && !ds.equals("S")) {
				dataSource = "S";
				if (!otherDataSources.contains(dataSource)) {
					otherDataSources.add(dataSource);
				}
			} else if (sectorType.equals("I") && !ds.equals("I")) {
				dataSource = "I";
				if (!otherDataSources.contains(dataSource)) {
					otherDataSources.add(dataSource);
				}
			}
			
			if (!ds.equals("E") && sectorType.equals("E")) {
				dataSource = "E";
				if (!otherDataSources.contains(dataSource)) {
					otherDataSources.add(dataSource);
				}
			}
		}
		
		return otherDataSources;
	}
	*/
	private void addParentCompaniesToFacilityDetail(FacilityDetail fd, String parentCompanies) {
		
		if (parentCompanies != null) {
			String[] pc = parentCompanies.split(";");
			
			HashMap<String, Float> unsortedMap = new HashMap<String, Float>();
			ParentComparator parentComparator = new ParentComparator(unsortedMap);
			TreeMap<String, Float> sortedMap = new TreeMap<String, Float>(parentComparator);
			
			if (pc.length > 0) {
				
				for (int i = 0; i < pc.length; i++) {
					String[] temp = pc[i].split(" ");
					String key = "";
					for (int x = 0; x < temp.length - 1; x++) {
						key = key + temp[x] + " ";
					}
					
					String s = temp[temp.length - 1];
					String c = s.substring(1, s.length() - 2);
					Float value = null;
					if (!c.equals("")) {
						value = Float.parseFloat(c);
					}
					
					unsortedMap.put(key, value);
				}
				fd.setParentCompanies(new ArrayList<String>());
				sortedMap.putAll(unsortedMap);
				addParentCompanies(sortedMap, fd);
			}
		}
		
	}
	
	private DimFacility getFacilityForFacilityDetail(Long facilityId, int rYear, String dataSource) {
		
		DimFacility f = dimFacilityDao.findDimFacilityAndHtmlByFacilityIdAndReportingYear(facilityId, rYear);
		
		if (f.getId() == null) {
			return null;
		}
		
		String facType = "E";
		if ("S".equals(dataSource)) {
			facType = "S";
		} else if ("I".equals(dataSource)) {
			facType = "I";
		} else if ("A".equals(dataSource)) {
			facType = "A";
		}
		
		DimFacility df = new DimFacility();
		df.setLatitude(f.getLatitude());
		df.setLongitude(f.getLongitude());
		df.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(f.getFacilityName()));
		df.setAddress1(f.getAddress1());
		df.setAddress2(f.getAddress2());
		df.setCity(f.getCity());
		df.setState(f.getState());
		df.setZip(f.getZip());
		df.setFrsId(f.getFrsId());
		df.setNaicsCode(f.getNaicsCode());
		df.setProgramName(f.getProgramName());
		df.setProgramSysId(f.getProgramSysId());
		df.setHtml(f.getHtml());
		df.setPublicXml(f.getPublicXml());
		df.setTribalLand(f.getTribalLand());
		df.setCemsUsed(f.getCemsUsed());
		df.setCo2Captured(f.getCo2Captured());
		df.setCo2EmittedSupplied(f.getCo2EmittedSupplied());
		df.setUuRandDExempt(f.getUuRandDExempt());
		df.setId(new DimFacilityId(f.getId().getFacilityId(), f.getId().getYear()));
		df.setReportedIndustryTypes(f.getReportedIndustryTypes());
		df.setProcessStationaryCml(f.getProcessStationaryCml());
		df.setComments(f.getComments());
		df.setRrMonitoringPlan(f.getRrMonitoringPlan());
		df.setRrFilename(f.getRrFilename());
		df.setRrLink(f.getRrLink());
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(f.getId().getFacilityId(), f.getId().getYear(), facType);
		ReportingStatus rs = rsMap.get(f.getId().getFacilityId());
		df.setReportingStatus(rs);
		
		return df;
	}
	
	public static void addParentCompanies(Map<String, Float> map, FacilityDetail fd) {
		for (Map.Entry<String, Float> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				if (entry.getValue() < .001) {
					fd.getParentCompanies().add(entry.getKey() + "(" + String.format("%f", entry.getValue()) + "%)");
				} else {
					fd.getParentCompanies().add(entry.getKey() + "(" + entry.getValue() + "%)");
				}
			} else if (entry.getKey() != null) {
				fd.getParentCompanies().add(entry.getKey());
			}
		}
	}
}
