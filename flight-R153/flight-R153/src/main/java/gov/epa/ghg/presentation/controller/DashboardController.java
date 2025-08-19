package gov.epa.ghg.presentation.controller;

import gov.epa.ghg.domain.FacilityView;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.dto.view.PipeList;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.EmitterAggregateService;
import gov.epa.ghg.service.ExportListService;
import gov.epa.ghg.service.ExportService;
import gov.epa.ghg.service.FacilityViewService;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alabdullahwi on 8/19/2015.
 *
 * this controller contains all methods related to the dashboard section of FLIGHT
 * we define the dashboard as: all components that are available on all the five major views (MAP, LIST, BAR, TREE, PIE)
 *
 * -- those consist of three main pieces:
 *
 * //1. UPPER DASHBOARD : parameters.
 * //2. LOWER DASHBOARD : sector parameters.
 * //3. LEFT HAND SIDE: facility summary panel
 *
 * the top section that deals with parameter selection, autocomplete, etc...
 * and the bottom which contains the sector dashboard
 */

@Controller
public class DashboardController {

    @Inject
    ExportService exportService;

    @Inject
    ExportListService exportListService;

    @Inject
    private EmitterAggregateService emitterAggregateService;

    @Inject
    FacilityViewService facilityViewService;

    @Resource(name="dataDate")
    private String dataDate;


    /**
     *  this method builds the facility list left-side summary panel
     *  //3 in classification above
     */
    @RequestMapping(value = "/populateFacilitySummaryPanel", method = RequestMethod.POST)
    public String populateFacilitySummaryPanel(@RequestBody FlightRequest request, ModelMap viewModel) {

       String westPanel = "map_legend_panel"; 
       if (!"map".equalsIgnoreCase(request.getVisType())) {
	    	FacilityList facilities = null;
	        PipeList pipes = null;
	        int totalCount = 0;
	        List<FacilityView> listFacView = null;
	        if(request.isPipe()) {
	            pipes = facilityViewService.getPipePanelList(request);
	            listFacView = pipes.getFacilities();
	        } /*else if(request.isWholePetroNg()) {
	        	facilities = facilityViewService.getFacilityViewList(request);
	            pipes = facilityViewService.getPipePanelList(request);
	            listFacView = ListUtils.union(facilities.getFacilities(),pipes.getFacilities());
	        }*/ else {
	        	facilities = facilityViewService.getFacilityViewList(request);
	            listFacView = facilities.getFacilities();
	        }
	        //totalCount = facilityViewService.getTotalCountMinusStoppedReporting(request);
	    	totalCount = listFacView.size();
	        //find the number of pages
	        int numPages = determineNumberOfPages(totalCount) ;
	
	        viewModel.addAttribute("year", request.getReportingYear());
	        viewModel.addAttribute("dataDate", dataDate);
	        viewModel.addAttribute("ds", request.getDataSource());
	        viewModel.addAttribute("numFacilities", totalCount);
	        viewModel.addAttribute("page", request.getPageNumber() + 1);
	        viewModel.addAttribute("pages", numPages);
	        viewModel.addAttribute("facilities", listFacView);
	        viewModel.addAttribute("previousPage", request.getPageNumber() > 0 ? request.getPageNumber() - 1 : 0);
	        viewModel.addAttribute("nextPage", request.getPageNumber() < numPages - 1 ? request.getPageNumber() + 1 : numPages - 1);
	        viewModel.addAttribute("lastPage", numPages - 1);
	        viewModel.addAttribute("co2g", ServiceUtils.getIconInfo("co2g"));
	        viewModel.addAttribute("co2b", ServiceUtils.getIconInfo("co2b"));
	        viewModel.addAttribute("co2o", ServiceUtils.getIconInfo("co2o")); 
	        viewModel.addAttribute("facilityTypeFullName", FacilityType.getFullName(request.getDataSource()));

	        westPanel = "facility_summary_panel"; 
        }
        return westPanel;
    }

    /**
     *
     * this method populates the bottom 'sector dashboard' with emissions values and number of facilities
     * all logic was moved to the service layer
     * @param request
     * @return
     */
    @RequestMapping(value="/populateSectorDashboard", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject getSectorAggregatesNew(@RequestBody FlightRequest request) {
        return emitterAggregateService.getAggregates(request);
    }

    // I cannot use the flight request object here because this has to be a non-AJAX, non-POST call
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportToExcel(
                              @RequestParam(value="ds") String sectorType,
                              @RequestParam(value="ryr") int year,
                              @RequestParam(value="q", required=false) String q,
                              @RequestParam(value="st", required=false) String state,
                              @RequestParam(value="fc", required=false) Integer countyFips,
                              @RequestParam(value="mc", required=false) Integer msaCode,
                              @RequestParam(value="tl", required=false) Long tribalLandId,
                              @RequestParam(value="sf", required=false) String searchOptions,
                              @RequestParam(value="lowE", required=false) Long lowE,
                              @RequestParam(value="highE", required=false) Long highE,
                              @RequestParam(value="et", required=false) String emissionsType,
                              @RequestParam(value="tr", required=false) String trend,
                              @RequestParam(value="cyr", required=false) Integer currentYear,
                              @RequestParam(value="g1", required=false) Byte g1,
                              @RequestParam(value="g2", required=false) Byte g2,
                              @RequestParam(value="g3", required=false) Byte g3,
                              @RequestParam(value="g4", required=false) Byte g4,
                              @RequestParam(value="g5", required=false) Byte g5,
                              @RequestParam(value="g6", required=false) Byte g6,
                              @RequestParam(value="g7", required=false) Byte g7,
                              @RequestParam(value="g8", required=false) Byte g8,
                              @RequestParam(value="g9", required=false) Byte g9,
                              @RequestParam(value="g10", required=false) Byte g10,
                              @RequestParam(value="g11", required=false) Byte g11,
                              @RequestParam(value="g12", required=false) Byte g12,
                              @RequestParam(value="s1", required=false) Byte s1,
                              @RequestParam(value="s2", required=false) Byte s2,
                              @RequestParam(value="s3", required=false) Byte s3,
                              @RequestParam(value="s4", required=false) Byte s4,
                              @RequestParam(value="s5", required=false) Byte s5,
                              @RequestParam(value="s6", required=false) Byte s6,
                              @RequestParam(value="s7", required=false) Byte s7,
                              @RequestParam(value="s8", required=false) Byte s8,
                              @RequestParam(value="s9", required=false) Byte s9,
                              @RequestParam(value="s201", required=false) Byte s201,
                              @RequestParam(value="s202", required=false) Byte s202,
                              @RequestParam(value="s203", required=false) Byte s203,
                              @RequestParam(value="s204", required=false) Byte s204,
                              @RequestParam(value="s301", required=false) Byte s301,
                              @RequestParam(value="s302", required=false) Byte s302,
                              @RequestParam(value="s303", required=false) Byte s303,
                              @RequestParam(value="s304", required=false) Byte s304,
                              @RequestParam(value="s305", required=false) Byte s305,
                              @RequestParam(value="s306", required=false) Byte s306,
                              @RequestParam(value="s307", required=false) Byte s307,
                              @RequestParam(value="s401", required=false) Byte s401,
                              @RequestParam(value="s402", required=false) Byte s402,
                              @RequestParam(value="s403", required=false) Byte s403,
                              @RequestParam(value="s404", required=false) Byte s404,
                              @RequestParam(value="s405", required=false) Byte s405,
                              @RequestParam(value="s601", required=false) Byte s601,
                              @RequestParam(value="s602", required=false) Byte s602,
                              @RequestParam(value="s701", required=false) Byte s701,
                              @RequestParam(value="s702", required=false) Byte s702,
                              @RequestParam(value="s703", required=false) Byte s703,
                              @RequestParam(value="s704", required=false) Byte s704,
                              @RequestParam(value="s705", required=false) Byte s705,
                              @RequestParam(value="s706", required=false) Byte s706,
                              @RequestParam(value="s707", required=false) Byte s707,
                              @RequestParam(value="s708", required=false) Byte s708,
                              @RequestParam(value="s709", required=false) Byte s709,
                              @RequestParam(value="s710", required=false) Byte s710,
                              @RequestParam(value="s711", required=false) Byte s711,
                              @RequestParam(value="s801", required=false) Byte s801,
                              @RequestParam(value="s802", required=false) Byte s802,
                              @RequestParam(value="s803", required=false) Byte s803,
                              @RequestParam(value="s804", required=false) Byte s804,
                              @RequestParam(value="s805", required=false) Byte s805,
                              @RequestParam(value="s806", required=false) Byte s806,
                              @RequestParam(value="s807", required=false) Byte s807,
                              @RequestParam(value="s808", required=false) Byte s808,
                              @RequestParam(value="s809", required=false) Byte s809,
                              @RequestParam(value="s810", required=false) Byte s810,
                              @RequestParam(value="s901", required=false) Byte s901,
                              @RequestParam(value="s902", required=false) Byte s902,
                              @RequestParam(value="s903", required=false) Byte s903,
                              @RequestParam(value="s904", required=false) Byte s904,
                              @RequestParam(value="s905", required=false) Byte s905,
                              @RequestParam(value="s906", required=false) Byte s906,
                              @RequestParam(value="s907", required=false) Byte s907,
                              @RequestParam(value="s908", required=false) Byte s908,
                              @RequestParam(value="s909", required=false) Byte s909,
                              @RequestParam(value="s910", required=false) Byte s910,
                              @RequestParam(value="s911", required=false) Byte s911,
                              @RequestParam(value="sc", required=false) Integer sc,
                              @RequestParam(value="is", required=false) Integer is,
                              @RequestParam(value="rs", required=false) String rs,
                              @RequestParam(value="bs", required=false) String basin,
                              @RequestParam(value="allReportingYears",required=false) boolean  allReportingYears,
                              @RequestParam(value="listExport",required=false) boolean listExport,
                              @RequestParam(value="listGeo",required=false) boolean listGeo,
            HttpServletResponse response) {

        try {

            Byte[] gases =  { g1, g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12 } ;
            Byte[] sectors =  { s1, s2, s201, s202, s203, s204, s3, s301, s302, s303, s304, s305,s306,s307, s4, s401, s402, s403, s404,s405, s5, s6, s601, s602 ,s7, s701, s702, s703, s704, s705,
            s706, s707, s708, s709, s710, s711, s8, s801, s802,s803,s804,s805,s806,s807,s808,s809,s810, s9, s901,s902,s903,s904,s905,s906,s907,s908,s909,s910,s911} ;

            //allReportingYears is not part of the FlightRequest because it is an export-specific parameter that has no semantic value to the "filters" on the dashboard which could be expressed in different ways(views)
            FlightRequest request = new FlightRequest(sectorType, year, currentYear, q, state, countyFips, msaCode, tribalLandId, searchOptions, lowE, highE, emissionsType, trend, sc, is, rs, basin, gases, sectors, 0, 0);
            Workbook wb = null;
            if (!listExport) wb = exportService.exportToExcel(request, allReportingYears);
            else wb = exportListService.exportToExcel(request, listGeo);
            
            response.setContentType("application/ms-excel");
            response.setHeader("Content-Disposition","attachment; filename=flight.xls");
            response.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");

            wb.write( response.getOutputStream() );
            response.getOutputStream().flush();

        } catch (IOException ioe) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException see) {
                see.printStackTrace();
            }
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    @RequestMapping(value = "/autocomplete" , method = RequestMethod.POST )
    public @ResponseBody List<String> getAutoComplete(@RequestBody FlightRequest request ) {


        List<String> entries = new ArrayList<String>();
        String query = request.getQuery();


        if(query != null && query.length()>1){
            FacilityList fac = facilityViewService.getAutoCompleteFacilityList(request);
            for(FacilityView view : fac.getFacilities()){
                if(view.getFacilityName()!=null && view.getFacilityName().toLowerCase().startsWith(query.toLowerCase()))
                    entries.add(view.getFacilityName());
                if(view.getCity()!= null && view.getCity().toLowerCase().startsWith(query.toLowerCase()))
                    entries.add(view.getCity());
                if(view.getStateName()!=null && view.getStateName().toLowerCase().startsWith(query.toLowerCase()))
                    entries.add(view.getStateName());
                if(view.getZip() != null && view.getZip().toLowerCase().startsWith(query.toLowerCase()))
                    entries.add(view.getZip());
            }
        }
        // remove duplicate
        List<String> newList = new ArrayList<String>();
        for (String s : entries) {
            if (!newList.contains(s))
                newList.add(s);
        }
        if(newList.size()>10)
            return newList.subList(0, 10);
        else
            return newList;

    }

    @RequestMapping(value = "/autocomplete/{year}", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getAutoComplete(
            @PathVariable int year,
            @RequestParam("term") String q,
            @RequestParam(value="rs", required=false) String rs,
            @RequestParam(value="st", required=false) String stateCode,
            @RequestParam(value="fc", required=false) Integer fipsCode,
            @RequestParam(value="mc", required=false) Integer msaCode,
            @RequestParam(value="tl", required=false) Long tribalLandId,
            @RequestParam(value="sf", required=false) String searchOptions,
            @RequestParam(value="lowE", required=false) Long lowE,
            @RequestParam(value="highE", required=false) Long highE,
            @RequestParam(value="et", required=false) String emissionsType,
            @RequestParam(value="g1", required=false) Byte g1,
            @RequestParam(value="g2", required=false) Byte g2,
            @RequestParam(value="g3", required=false) Byte g3,
            @RequestParam(value="g4", required=false) Byte g4,
            @RequestParam(value="g5", required=false) Byte g5,
            @RequestParam(value="g6", required=false) Byte g6,
            @RequestParam(value="g7", required=false) Byte g7,
            @RequestParam(value="g8", required=false) Byte g8,
            @RequestParam(value="g9", required=false) Byte g9,
            @RequestParam(value="g10", required=false) Byte g10,
            @RequestParam(value="g11", required=false) Byte g11,
            @RequestParam(value="g12", required=false) Byte g12,
            @RequestParam(value="s1", required=false) Byte s1,
            @RequestParam(value="s2", required=false) Byte s2,
            @RequestParam(value="s3", required=false) Byte s3,
            @RequestParam(value="s4", required=false) Byte s4,
            @RequestParam(value="s5", required=false) Byte s5,
            @RequestParam(value="s6", required=false) Byte s6,
            @RequestParam(value="s7", required=false) Byte s7,
            @RequestParam(value="s8", required=false) Byte s8,
            @RequestParam(value="s9", required=false) Byte s9,
            @RequestParam(value="s201", required=false) Byte s201,
            @RequestParam(value="s202", required=false) Byte s202,
            @RequestParam(value="s203", required=false) Byte s203,
            @RequestParam(value="s204", required=false) Byte s204,
            @RequestParam(value="s301", required=false) Byte s301,
            @RequestParam(value="s302", required=false) Byte s302,
            @RequestParam(value="s303", required=false) Byte s303,
            @RequestParam(value="s304", required=false) Byte s304,
            @RequestParam(value="s305", required=false) Byte s305,
            @RequestParam(value="s306", required=false) Byte s306,
            @RequestParam(value="s307", required=false) Byte s307,
            @RequestParam(value="s401", required=false) Byte s401,
            @RequestParam(value="s402", required=false) Byte s402,
            @RequestParam(value="s403", required=false) Byte s403,
            @RequestParam(value="s404", required=false) Byte s404,
            @RequestParam(value="s405", required=false) Byte s405,
            @RequestParam(value="s601", required=false) Byte s601,
            @RequestParam(value="s602", required=false) Byte s602,
            @RequestParam(value="s701", required=false) Byte s701,
            @RequestParam(value="s702", required=false) Byte s702,
            @RequestParam(value="s703", required=false) Byte s703,
            @RequestParam(value="s704", required=false) Byte s704,
            @RequestParam(value="s705", required=false) Byte s705,
            @RequestParam(value="s706", required=false) Byte s706,
            @RequestParam(value="s707", required=false) Byte s707,
            @RequestParam(value="s708", required=false) Byte s708,
            @RequestParam(value="s709", required=false) Byte s709,
            @RequestParam(value="s710", required=false) Byte s710,
            @RequestParam(value="s711", required=false) Byte s711,
            @RequestParam(value="s801", required=false) Byte s801,
            @RequestParam(value="s802", required=false) Byte s802,
            @RequestParam(value="s803", required=false) Byte s803,
            @RequestParam(value="s804", required=false) Byte s804,
            @RequestParam(value="s805", required=false) Byte s805,
            @RequestParam(value="s806", required=false) Byte s806,
            @RequestParam(value="s807", required=false) Byte s807,
            @RequestParam(value="s808", required=false) Byte s808,
            @RequestParam(value="s809", required=false) Byte s809,
            @RequestParam(value="s810", required=false) Byte s810,
            @RequestParam(value="s901", required=false) Byte s901,
            @RequestParam(value="s902", required=false) Byte s902,
            @RequestParam(value="s903", required=false) Byte s903,
            @RequestParam(value="s904", required=false) Byte s904,
            @RequestParam(value="s905", required=false) Byte s905,
            @RequestParam(value="s906", required=false) Byte s906,
            @RequestParam(value="s907", required=false) Byte s907,
            @RequestParam(value="s908", required=false) Byte s908,
            @RequestParam(value="s909", required=false) Byte s909,
            @RequestParam(value="s910", required=false) Byte s910,
            @RequestParam(value="s911", required=false) Byte s911,
            @RequestParam(value="ds", required=false) String ds,
            @RequestParam(value="sc", required=false) Integer sc,
            @RequestParam(value="so", required=false) Integer sortOrder) {

        GasFilter gases = new GasFilter(g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12);
        SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9,
                s201, s202, s203, s204,
                s301, s302, s303, s304, s305, s306, s307,
                s401, s402, s403, s404, s405,
                s601, s602,
                s701, s702, s703, s704, s705, s706,	s707, s708, s709, s710, s711,
                s801, s802, s803, s804, s805, s806, s807, s808, s809, s810,
                s901, s902, s903, s904, s905, s906, s907, s908, s909, s910, s911);
        QueryOptions qo = new QueryOptions(searchOptions);

        List<String> entries = new ArrayList<String>();
        if(q != null && q.length()>1){
            FacilityList fac = facilityViewService.getAutoCompleteFacilityList(q,year,stateCode,
                    fipsCode!=null?String.valueOf(fipsCode):"",msaCode!=null?String.valueOf(msaCode):"",
                    lowE!=null?String.valueOf(lowE):"",highE!=null?String.valueOf(highE):"",
                    gases, sectors, qo,
                    ds, sc, sortOrder,rs, emissionsType, tribalLandId);
            for(FacilityView view : fac.getFacilities()){
                if(view.getFacilityName()!=null && view.getFacilityName().toLowerCase().startsWith(q.toLowerCase()))
                    entries.add(view.getFacilityName());
                if(view.getCity()!= null && view.getCity().toLowerCase().startsWith(q.toLowerCase()))
                    entries.add(view.getCity());
                if(view.getStateName()!=null && view.getStateName().toLowerCase().startsWith(q.toLowerCase()))
                    entries.add(view.getStateName());
                if(view.getZip() != null && view.getZip().toLowerCase().startsWith(q.toLowerCase()))
                    entries.add(view.getZip());
            }
        }
        // remove duplicate
        List<String> newList = new ArrayList<String>();
        for (String s : entries) {
            if (!newList.contains(s))
                newList.add(s);
        }
        if(newList.size()>10)
            return newList.subList(0, 10);
        else
            return newList;
    }

    @RequestMapping(value = "/facilitySectorAggregate/{year}", method = RequestMethod.GET)
    @Deprecated
    public @ResponseBody JSONObject getSectorAggregates(
            @PathVariable int year,
            @RequestParam(value="ds", required=false) String ds,
            @RequestParam(value="q", required=false) String q,
            @RequestParam(value="st", required=false) String stateCode,
            @RequestParam(value="fc", required=false) Integer fipsCode,
            @RequestParam(value="mc", required=false) Integer msaCode,
            @RequestParam(value="bs", required=false) String basin,
            @RequestParam(value="tl", required=false) Long tribalLandId,
            @RequestParam(value="sf", required=false) String searchOptions,
            @RequestParam(value="lowE", required=false) Long lowE,
            @RequestParam(value="highE", required=false) Long highE,
            @RequestParam(value="rs", required=false) String rs,
            @RequestParam(value="et", required=false) String emissionsType,
            @RequestParam(value="g1", required=false) Byte g1,
            @RequestParam(value="g2", required=false) Byte g2,
            @RequestParam(value="g3", required=false) Byte g3,
            @RequestParam(value="g4", required=false) Byte g4,
            @RequestParam(value="g5", required=false) Byte g5,
            @RequestParam(value="g6", required=false) Byte g6,
            @RequestParam(value="g7", required=false) Byte g7,
            @RequestParam(value="g8", required=false) Byte g8,
            @RequestParam(value="g9", required=false) Byte g9,
            @RequestParam(value="g10", required=false) Byte g10,
            @RequestParam(value="g11", required=false) Byte g11,
            @RequestParam(value="g12", required=false) Byte g12,
            @RequestParam(value="s1", required=false) Byte s1,
            @RequestParam(value="s2", required=false) Byte s2,
            @RequestParam(value="s3", required=false) Byte s3,
            @RequestParam(value="s4", required=false) Byte s4,
            @RequestParam(value="s5", required=false) Byte s5,
            @RequestParam(value="s6", required=false) Byte s6,
            @RequestParam(value="s7", required=false) Byte s7,
            @RequestParam(value="s8", required=false) Byte s8,
            @RequestParam(value="s9", required=false) Byte s9,
            @RequestParam(value="s201", required=false) Byte s201,
            @RequestParam(value="s202", required=false) Byte s202,
            @RequestParam(value="s203", required=false) Byte s203,
            @RequestParam(value="s204", required=false) Byte s204,
            @RequestParam(value="s301", required=false) Byte s301,
            @RequestParam(value="s302", required=false) Byte s302,
            @RequestParam(value="s303", required=false) Byte s303,
            @RequestParam(value="s304", required=false) Byte s304,
            @RequestParam(value="s305", required=false) Byte s305,
            @RequestParam(value="s306", required=false) Byte s306,
            @RequestParam(value="s307", required=false) Byte s307,
            @RequestParam(value="s401", required=false) Byte s401,
            @RequestParam(value="s402", required=false) Byte s402,
            @RequestParam(value="s403", required=false) Byte s403,
            @RequestParam(value="s404", required=false) Byte s404,
            @RequestParam(value="s405", required=false) Byte s405,
            @RequestParam(value="s601", required=false) Byte s601,
            @RequestParam(value="s602", required=false) Byte s602,
            @RequestParam(value="s701", required=false) Byte s701,
            @RequestParam(value="s702", required=false) Byte s702,
            @RequestParam(value="s703", required=false) Byte s703,
            @RequestParam(value="s704", required=false) Byte s704,
            @RequestParam(value="s705", required=false) Byte s705,
            @RequestParam(value="s706", required=false) Byte s706,
            @RequestParam(value="s707", required=false) Byte s707,
            @RequestParam(value="s708", required=false) Byte s708,
            @RequestParam(value="s709", required=false) Byte s709,
            @RequestParam(value="s710", required=false) Byte s710,
            @RequestParam(value="s711", required=false) Byte s711,
            @RequestParam(value="s801", required=false) Byte s801,
            @RequestParam(value="s802", required=false) Byte s802,
            @RequestParam(value="s803", required=false) Byte s803,
            @RequestParam(value="s804", required=false) Byte s804,
            @RequestParam(value="s805", required=false) Byte s805,
            @RequestParam(value="s806", required=false) Byte s806,
            @RequestParam(value="s807", required=false) Byte s807,
            @RequestParam(value="s808", required=false) Byte s808,
            @RequestParam(value="s809", required=false) Byte s809,
            @RequestParam(value="s810", required=false) Byte s810,
            @RequestParam(value="s901", required=false) Byte s901,
            @RequestParam(value="s902", required=false) Byte s902,
            @RequestParam(value="s903", required=false) Byte s903,
            @RequestParam(value="s904", required=false) Byte s904,
            @RequestParam(value="s905", required=false) Byte s905,
            @RequestParam(value="s906", required=false) Byte s906,
            @RequestParam(value="s907", required=false) Byte s907,
            @RequestParam(value="s908", required=false) Byte s908,
            @RequestParam(value="s909", required=false) Byte s909,
            @RequestParam(value="s910", required=false) Byte s910,
            @RequestParam(value="s911", required=false) Byte s911,
            @RequestParam(value="pg", required=false) int pageNumber,
            @RequestParam(value="sc", required=false) int sc,
            @RequestParam(value="so", required=false) int sortOrder,
            @RequestParam(value="is", required=false) int is) {

        GasFilter gases = new GasFilter(g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12);
        SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9,
                s201, s202, s203, s204,
                s301, s302, s303, s304, s305, s306, s307,
                s401, s402, s403, s404, s405,
                s601, s602,
                s701, s702, s703, s704, s705, s706,	s707, s708, s709, s710, s711,
                s801, s802, s803, s804, s805, s806, s807, s808, s809, s810,
                s901, s902, s903, s904, s905, s906, s907, s908, s909, s910, s911);
        QueryOptions qo = new QueryOptions(searchOptions);

        if(stateCode.equals("US")) {
            stateCode = "";
        }

        return emitterAggregateService.getAggregates(q,year,stateCode,fipsCode!=null?String.valueOf(fipsCode):"",msaCode!=null?String.valueOf(msaCode):"",basin!=null?String.valueOf(basin):"",
                lowE!=null?String.valueOf(lowE):"",highE!=null?String.valueOf(highE):"",
                gases, sectors, qo, ds, rs, emissionsType, tribalLandId,
                ds, pageNumber, sc, sortOrder, is);
    }


        private int determineNumberOfPages(int numFacilities) {

            int retv = 0;

            //if number of facilities can fit in one page, then we only need one page
            if (numFacilities < 100) {
                retv = 1;
            }

            else {
                //if the number of facilities can be evenly divided in 100-size chunks, we need a number of pages equal to the number of chunks
                if (numFacilities % 100 == 0 ) {
                    retv = numFacilities/100;
                }
                //if there is some reminder that's less than a 100, put the reminder in an extra page (the 1 below)
                else {
                    retv = (numFacilities/100)+1;
                }
            }

            return retv ;

    }
}
