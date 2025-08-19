package gov.epa.ghg.service.view.transformer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import gov.epa.ghg.dao.DimFacilityDao;
import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.dto.SupplierListDetails;
import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.dto.view.SupplierListTrendDetails;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ServiceUtils;

/**
 * Created by alabdullahwi on 5/10/2016.
 */
@Service
public class TrendDataTransformer {
	
	@Inject
	DimFacilityDao facilityDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	public Map<String, SupplierListDetailsObject> transformToSupplierTrendMap(List<Object[]> results, Integer year, boolean isTrend) {
		
		Map<Long, SupplierListDetailsObject> _map = new TreeMap<Long, SupplierListDetailsObject>();
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(year, "S");
		if (results != null) {
			for (Object[] result : results) {
				Long facilityId = (Long) result[3];
				BigDecimal suppliedQuantity = (BigDecimal) result[2];
				SupplierListDetailsObject sld = _map.get(facilityId);
				
				if (isTrend) {
					year = ((Long) result[6]).intValue();
				}
				
				if (sld == null) {
					DimFacility facility = facilityDao.findByFacilityIdAndReportingYear(facilityId, year);
					ReportingStatus rs = rsMap.get(facilityId);
					if (facility != null) {
						sld = (isTrend) ? new SupplierListTrendDetails() : new SupplierListDetails();
						sld.populate(year, suppliedQuantity, facility, rs);
						_map.put(facilityId, sld);
					}
				} else {
					sld.populate(year, suppliedQuantity, null, null);
				}
			}
		}
		
		// create actuals returned map, here we are merely transforming the key to _map from long to the string below for the benefits of the view layer (legacy compatibility?)
		Map<String, SupplierListDetailsObject> retVal = new TreeMap<String, SupplierListDetailsObject>();
		for (SupplierListDetailsObject sld : _map.values()) {
			String facilityName = ServiceUtils.nullSafeHtmlUnescape(sld.getFacility().getFacilityName()) + " (" + sld.getFacility().getId().getFacilityId() + ")";
			retVal.put(facilityName, sld);
		}
		
		return retVal;
	}
	
}
