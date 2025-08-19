package gov.epa.ghg.service.view.transformer;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.dto.view.FacilityExport;
import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.dto.view.SupplierListTrendDetails;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.TrendListService;
import gov.epa.ghg.util.AppConstants;
import gov.epa.ghg.util.ServiceUtils;

import static gov.epa.ghg.service.ListChartService.endYear;
import static gov.epa.ghg.service.ListChartService.startYear;

/**
 * Created by alabdullahwi on 8/18/2015.
 */
@Service
public class FacilityExportTransformer {

    @Inject
    TrendDataTransformer trendDataTransformer;

    @Inject
    TrendListService trendListService;

    @Inject
    DimFacilityStatusDAO dimFacilityStatusDao;

    private static NumberFormat df = NumberFormat.getInstance();

    public List<FacilityExport> transformTrend(List<Object[]> data, FlightRequest request ) {

        List<FacilityExport> feList = new ArrayList<FacilityExport>();

        Map<String,SupplierListDetailsObject> supplierData = trendDataTransformer.transformToSupplierTrendMap(
                data, request.getReportingYear(), request.isTrendRequest()
        );

        Set<Long> years  = trendListService.buildTimeline(request.getCurrentYear());

        for (String facKey : supplierData.keySet()) {
            FacilityExport fe = new FacilityExport();
            SupplierListTrendDetails map = (SupplierListTrendDetails) supplierData.get(facKey);
            fe.setFacilityId(map.getFacility().getId().getFacilityId());
            fe.setFacilityName(map.getFacilityName());
            fe.setCity(map.getCity());
            fe.setState(map.getState());
            Map<String,String> emissionsMap = new HashMap<String,String>();
            for (Long emissionYear : years) {
                BigDecimal val = map.getEmissionsForYear(emissionYear);
                emissionsMap.put("total" + emissionYear, df.format(ServiceUtils.convert(val, AppConstants.MT)));
            }

            BigDecimal _end = map.getEmissionsForYear(endYear);
            BigDecimal _endMinus1 = map.getEmissionsForYear(endYear-1);
            BigDecimal _start = map.getEmissionsForYear(startYear);

            emissionsMap.put("diff"+(endYear-1), df.format(ServiceUtils.convert(_end.subtract(_endMinus1), AppConstants.MT)));
            emissionsMap.put("diff"+startYear, df.format(ServiceUtils.convert(_end.subtract(_start), AppConstants.MT)));

            fe.setTrendEmissions(emissionsMap);
            feList.add(fe);
        }

        return feList;
    }

    public List<FacilityExport> transform(List<DimFacility> dimFacilities, Map<Long,BigDecimal> emissions, Map<Long,Long> repYears, FacilityType facilityType) {
    	
    	String facType = "E";
		if (facilityType == FacilityType.SUPPLIERS) facType = "S";
		else if (facilityType == FacilityType.CO2_INJECTION) facType = "I";
		else if (facilityType == FacilityType.RR_CO2) facType = "A";
		
        List<FacilityExport> feList = new ArrayList<FacilityExport>();

        for (DimFacility df : dimFacilities) {
            FacilityExport fe = new FacilityExport();
            fe.setFacilityId(df.getId().getFacilityId());
            fe.setFacilityName(ServiceUtils.nullSafeHtmlUnescape(df.getFacilityName()));
            fe.setAddress1(df.getAddress1());
            fe.setAddress2(df.getAddress2());
            fe.setLatitude(df.getLatitude());
            fe.setLongitude(df.getLongitude());
            fe.setCounty(df.getCounty());
            if (facilityType == FacilityType.ONSHORE) {
            	fe.setBasinDetails(df.retrieveBasinNameAndNumber());
            }
            fe.setCity(df.getCity());
            fe.setState(df.getState());
            fe.setStateName(df.getStateName());
            fe.setZip(df.getZip());
            
            BigDecimal emission = emissions.get(df.getId().getFacilityId());
            fe.setTotalCo2e(emission != null ? emission : BigDecimal.ZERO);
            
            fe.setParentCompanies(df.getParentCompany());
            fe.setSubParts(df.getReportedSubparts());
            fe.setReportingYear(repYears.get(df.getId().getFacilityId()));
            
            if (facilityType == FacilityType.SUPPLIERS) {
	            Map<Long,ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(df.getId().getFacilityId(), df.getId().getYear(), facType);
	            ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
	            fe.setReportingStatus(rs);
            }
            
            feList.add(fe);
        }

        Collections.sort(feList, new Comparator<FacilityExport>() {
            public int compare(FacilityExport facility1, FacilityExport facility2) {
                return facility1.getFacilityName().compareTo(facility2.getFacilityName());
            }
        });

        return feList;

    }
}
