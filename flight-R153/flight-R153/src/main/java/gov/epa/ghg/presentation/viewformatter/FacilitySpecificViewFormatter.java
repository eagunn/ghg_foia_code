package gov.epa.ghg.presentation.viewformatter;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.PipeDetail;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.SpatialUtil;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by alabdullahwi on 8/31/2015.
 */
@Service
public class FacilitySpecificViewFormatter {

    @Resource(name="dataDate")
    private String dataDate;

    @Resource(name="helplinkurlbase")
    private String helplinkUrlBase;
    
    @Resource(name="reportingYears")
    private Map<String, String> reportingYears;

    public void populateViewModel(ModelMap viewModel, FacilityDetail fd, List<String> cmlSubparts, String dataSource, int year, boolean showPopup, Map<Long,ReportingStatus> rsMap) {

        viewModel.addAttribute("cmlSubparts", cmlSubparts);
        viewModel.addAttribute("fd", fd);
        viewModel.addAttribute("ds", dataSource);
        viewModel.addAttribute("popup", showPopup);

        if (fd.getFacility()!=null) {
            String latitude = fd.getFacility().getLatitude()!=null? SpatialUtil.decToSex(fd.getFacility().getLatitude(), 0, 1):"";
            String longitude = fd.getFacility().getLongitude()!=null?SpatialUtil.decToSex(fd.getFacility().getLongitude(), 0, 2):"";
            viewModel.addAttribute("latitude", latitude);
            viewModel.addAttribute("longitude", longitude);
            ReportingStatus rs = rsMap.get(fd.getFacility().getId().getFacilityId());
			viewModel.addAttribute("reportingStatus", rs);
        }

        viewModel.addAttribute("year", year);
        viewModel.addAttribute("dataDate", dataDate);
        viewModel.addAttribute("helplinkurlbase", helplinkUrlBase);
        viewModel.addAttribute("reportingYears", reportingYears);

        if (FacilityType.fromDataSource(dataSource) == FacilityType.EMITTERS) {
            viewModel.addAttribute("isEmitter", true);
        }

        DimFacility dimFacility = fd.getFacility();
        String reportedIndustryTypes = dimFacility.getReportedIndustryTypes();
        if( reportedIndustryTypes != null && (reportedIndustryTypes.contains("ONSH") || reportedIndustryTypes.contains("LDC")) ){
        	viewModel.addAttribute("showPerProcessSection", false); 
        }
        else {
            viewModel.addAttribute("showPerProcessSection", true);
        }

        viewModel.addAttribute("co2g", ServiceUtils.getIconInfo("co2g"));
        viewModel.addAttribute("co2b", ServiceUtils.getIconInfo("co2b"));
        viewModel.addAttribute("co2o", ServiceUtils.getIconInfo("co2o"));
        viewModel.addAttribute("co2gText", ServiceUtils.getIconText("co2g"));
        viewModel.addAttribute("co2bText", ServiceUtils.getIconText("co2b"));
        viewModel.addAttribute("co2oText", ServiceUtils.getIconText("co2o"));

    }

    public void populateViewPipe(ModelMap viewModel, PipeDetail pd, List<String> cmlSubparts, String dataSource, int year, boolean showPopup, Map<Long,ReportingStatus> rsMap) {

        viewModel.addAttribute("cmlSubparts", cmlSubparts);
        viewModel.addAttribute("fd", pd);
        viewModel.addAttribute("ds", dataSource);
        viewModel.addAttribute("popup", showPopup);

        if (pd.getFacility()!=null) {
            String latitude = pd.getFacility().getLatitude()!=null? SpatialUtil.decToSex(pd.getFacility().getLatitude(), 0, 1):"";
            String longitude = pd.getFacility().getLongitude()!=null?SpatialUtil.decToSex(pd.getFacility().getLongitude(), 0, 2):"";
            viewModel.addAttribute("latitude", latitude);
            viewModel.addAttribute("longitude", longitude);
            ReportingStatus rs = rsMap.get(pd.getFacility().getId().getFacilityId());
			viewModel.addAttribute("reportingStatus", rs);
        }

        viewModel.addAttribute("year", year);
        viewModel.addAttribute("dataDate", dataDate);
        viewModel.addAttribute("helplinkurlbase", helplinkUrlBase);
        viewModel.addAttribute("reportingYears", reportingYears);

        if (FacilityType.fromDataSource(dataSource) == FacilityType.EMITTERS) {
            viewModel.addAttribute("isEmitter", true);
        }

        DimFacilityPipe dimFacility = pd.getFacility();
        String reportedIndustryTypes = dimFacility.getReportedIndustryTypes();
        viewModel.addAttribute("showPerProcessSection", true);

        viewModel.addAttribute("co2g", ServiceUtils.getIconInfo("co2g"));
        viewModel.addAttribute("co2b", ServiceUtils.getIconInfo("co2b"));
        viewModel.addAttribute("co2o", ServiceUtils.getIconInfo("co2o"));
        viewModel.addAttribute("co2gText", ServiceUtils.getIconText("co2g"));
        viewModel.addAttribute("co2bText", ServiceUtils.getIconText("co2b"));
        viewModel.addAttribute("co2oText", ServiceUtils.getIconText("co2o"));

    }

}
