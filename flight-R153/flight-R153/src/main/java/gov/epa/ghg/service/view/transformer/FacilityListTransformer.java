package gov.epa.ghg.service.view.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.FacilityView;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.dto.view.PipeList;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;

/**
 * Created by alabdullahwi on 8/17/2015.
 */
@Service
public class FacilityListTransformer {
	
	public FacilityList transform(List<DimFacility> dimFacilities, Map<Long, BigDecimal> emissions, int reportingYear, FacilityType facilityType, int sortOrder, Map<Long, ReportingStatus> rsMap) {
		
		FacilityList retv = new FacilityList();
		List<FacilityView> fvList = new ArrayList<FacilityView>();
		retv.setTotalCount(dimFacilities.size());
		for (DimFacility df : dimFacilities) {
			FacilityView fv = new FacilityView();
			fv.setFacilityId(df.getId().getFacilityId());
			fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
			fv.setAddress1(df.getAddress1());
			fv.setAddress2(df.getAddress2());
			fv.setLatitude(df.getLatitude());
			fv.setLongitude(df.getLongitude());
			fv.setCity(df.getCity());
			fv.setState(df.getState());
			fv.setStateName(df.getStateName());
			fv.setZip(df.getZip());
			fv.setCo2Captured(df.getCo2Captured());
			fv.setCo2EmittedSupplied(df.getCo2EmittedSupplied());
			fv.setUuRandDExempt(df.getUuRandDExempt());
			fv.setComments(df.getComments());
			BigDecimal emission = emissions.get(df.getId().getFacilityId());
			if (emission != null) {
				fv.setTotalCo2e(emission);
			} else if (facilityType == FacilityType.EMITTERS || facilityType == FacilityType.ONSHORE) {
				// if String is on, we want this to remain null so the velocity logic in facility_summary_panel.htm will render ("N/A") as it expects null in this case
				if (!ReportingStatusQueryFilter.isReportingStatusEnabled(reportingYear)) {
					fv.setTotalCo2e(BigDecimal.ZERO);
				}
			}
			ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
			fv.setReportingStatus(rs);
			fvList.add(fv);
		}
		
		// sort order by facility name
		if (sortOrder == 0) {
			Collections.sort(fvList, new AscendingComparator());
		} else {
			Collections.sort(fvList, Collections.reverseOrder(new AscendingComparator()));
		}
		
		retv.setFacilities(fvList);
		return retv;
		
	}
	
	public PipeList transformPipe(Map<String, DimFacilityPipe> dimFacilityMap,
			Map<String, Map<String, BigDecimal>> keyMap,
			int reportingYear,
			FacilityType facilityType,
			int sortOrder,
			Map<Long, ReportingStatus> rsMap,
			int totalCount) {
		
		PipeList retv = new PipeList();
		List<FacilityView> fvList = new ArrayList<FacilityView>();
		retv.setTotalCount(totalCount);
		for (String key : keyMap.keySet()) {
			Map<String, BigDecimal> pipeEmMap = keyMap.get(key);
			DimFacilityPipe df = dimFacilityMap.get(key);
			FacilityView fv = new FacilityView();
			fv.setFacilityId(df.getId().getFacilityId());
			fv.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
			fv.setCity(df.getCity());
			fv.setState(df.getState());
			fv.setStateName(df.getStateName());
			fv.setZip(df.getZip());
			BigDecimal emission = null;
			for (String emissionKey : pipeEmMap.keySet()) {
				if (pipeEmMap.get(emissionKey) != null) {
					if (emission == null) {
						emission = BigDecimal.ZERO;
					}
					emission = pipeEmMap.get(emissionKey);
				}
			}
			fv.setTotalCo2e(emission);
			ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
			fv.setReportingStatus(rs);
			fvList.add(fv);
		}
		
		// sort order by facility name
		if (sortOrder == 0) {
			Collections.sort(fvList, new AscendingComparator());
		} else {
			Collections.sort(fvList, Collections.reverseOrder(new AscendingComparator()));
		}
		
		retv.setFacilities(fvList);
		return retv;
		
	}
	
	class AscendingComparator implements Comparator<FacilityView> {
		
		public int compare(FacilityView one, FacilityView two) {
			return one.getFacilityName().compareToIgnoreCase(two.getFacilityName());
		}
		
	}
	
}
