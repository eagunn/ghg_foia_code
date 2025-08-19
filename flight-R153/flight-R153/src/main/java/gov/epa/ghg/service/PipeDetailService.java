package gov.epa.ghg.service;

import java.io.Serializable;
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
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.FacilitySubpartKeyVal;
import gov.epa.ghg.domain.LuKey;
import gov.epa.ghg.dto.PipeDetail;
import gov.epa.ghg.dto.PipeHoverTip;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ParentComparator;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class PipeDetailService implements Serializable, PipeDetailInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimFacilityDaoInterface dimFacilityDao;
	
	@Inject
	FacilitySubpartKeyValDao facilitySubpartKeyValDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	public PipeDetail getPipeDetails(Long id, int year, String ds, String emissionsType) {
		
		PipeDetail pd = new PipeDetail();
		try {
			
			final Long facilityId = id;
			final int rYear = year;
			String dataSource = ds;
			
			DimFacilityPipe facility = this.getFacilityPipe(id, year, ds);
			
			// if null, just return a blank object, that's what it's been doing so I'm keeping it this way
			if (facility == null) {
				return pd;
			} else {
				pd.setFacility(facility);
			}
			this.addParentCompaniesToPipe(pd, facility.getParentCompany());
			
			pd.setGasEmissions(pubFactsDao.getPipeGasQuantity(pd, facilityId, rYear, dataSource, emissionsType));
			pd.setSubpartEmissions(pubFactsDao.getSubpartEmissions(facilityId, rYear, dataSource));
			
			pd.setSubpartDetails(new LinkedHashMap<String, List<FacilitySubpartKeyVal>>());
			
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
				
				if (!pd.getSubpartDetails().containsKey(key)) {
					pd.getSubpartDetails().put(key, new ArrayList<FacilitySubpartKeyVal>());
				}
				FacilitySubpartKeyVal fkv = new FacilitySubpartKeyVal();
				fkv.setLuKey(new LuKey(kv.getLuKey().getKeyId(), kv.getLuKey().getKeyName(), kv.getLuKey().getKeyDescription()));
				fkv.setValue(kv.getValue());
				fkv.setNotes(kv.getNotes());
				pd.getSubpartDetails().get(key).add(fkv);
			}
			
			List<String> reportingYearList = dimFacilityDao.getFacReportingYears(id);
			pd.setFacReportingYears((List<String>) reportingYearList);
			
			// PUB-592: show subpart W attributes before subpart NN attributes: take NN out and then put it back to make it last
			if (foundW && foundNN) {
				List<FacilitySubpartKeyVal> nnValue = pd.getSubpartDetails().remove(nnKey);
				pd.getSubpartDetails().put(nnKey, nnValue);
			}
			
			List yearEmissions = pubFactsDao.getFacilityTrend(id, ds, emissionsType);
			if (yearEmissions.size() > 0) {
				pd.setHasTrend(true);
			}
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
		return pd;
	}
	
	private DimFacilityPipe getFacilityPipe(Long id, int year, String dataSource) {
		
		DimFacilityPipe f = dimFacilityDao.findPipeIdYear(id, year);
		
		if (f.getId() == null) {
			return null;
		}
		
		String facType = "E";
		
		DimFacilityPipe df = new DimFacilityPipe();
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
	
	@SuppressWarnings("unchecked")
	private void addParentCompaniesToPipe(PipeDetail pd, String parentCompanies) {
		
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
				pd.setParentCompanies(new ArrayList<String>());
				sortedMap.putAll(unsortedMap);
				addParentCompanies(sortedMap, pd);
			}
		}
		
	}
	
	public static void addParentCompanies(Map<String, Float> map, PipeDetail pd) {
		for (Map.Entry<String, Float> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				if (entry.getValue() < .001) {
					pd.getParentCompanies().add(entry.getKey() + "(" + String.format("%f", entry.getValue()) + "%)");
				} else {
					pd.getParentCompanies().add(entry.getKey() + "(" + entry.getValue() + "%)");
				}
			} else if (entry.getKey() != null) {
				pd.getParentCompanies().add(entry.getKey());
			}
		}
	}
	
	public PipeHoverTip getPipeHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs, String state) {
		return dimFacilityDao.getPipeHoverTip(id, year, ds, emissionsType, rs, state);
		
	}
	
}
