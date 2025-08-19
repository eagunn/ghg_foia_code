package gov.epa.ghg.presentation.controller.view;

import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.*;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 *
 * Created by alabdullahwi on 8/19/2015.
 *
 * This controller is responsible for all the methods related to the LIST view in FLIGHT
 */
@Controller
public class ListViewController {

    @Inject
    ListChartService listChartService;

    @Inject
    TrendChartService trendChartService;

    @Inject
    TrendListService trendListService;


    /**
     *
     * this method builds the bulk of the List Chart view, the center-table detailed facility list
     *
     * @param request
     * @return
     **/
    @RequestMapping(value = "/listFacility/", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject listFacility(@RequestBody FlightRequest request) {

        FacilityType type = FacilityType.fromDataSource(request.getDataSource());

        //right now, it looks like when someone picks suppliers, it goes immediately to the listSector method, this is probably legacy leftover
        if (type == FacilityType.SUPPLIERS) {
            return listChartService.listChartSuppliers(request);
        } else {
            if (request.isTrendRequest()) {
                return trendListService.generateResponse(request);
            } else {
                return listChartService.listChartEmitterFacilities(request);
            }
        }
    }

    /**
     *
     *
     * a method to populate the main list view in the application
     *
     *
     *
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/listSector/", method = RequestMethod.POST)
    public @ResponseBody JSONObject listSector(@RequestBody FlightRequest request ) {

        FacilityType type = FacilityType.fromDataSource(request.getDataSource());

        //this is confusing, but is carrying over from a legacy setup -- needs work
        if (request.isTrendRequest()) {
            if (type == FacilityType.SUPPLIERS && request.getSupplierSector() == 0) {
                return listChartService.listChartSuppliers(request);
            } else {
                return trendListService.generateResponse(request);
            }
        }

        if (type == FacilityType.SUPPLIERS) {
            return listChartService.listChartSuppliers(request);
        } else {
            return listChartService.listChartEmitterSector(request);
        }
    }


    @RequestMapping(value = "/listFacilityForBasin/", method = RequestMethod.POST)
    public @ResponseBody JSONObject listFacilityForBasin(
            @RequestBody FlightRequest request) {

        FacilityType type = FacilityType.fromDataSource(request.getDataSource());

        if (request.isTrendRequest()) {
        	return trendListService.generateResponse(request);
        }
        return (type == FacilityType.SUPPLIERS) ? listChartService.listChartSuppliers(request) : listChartService.listChartBasinFacilities(request);
    }

    @RequestMapping(value = "/listFacilityForBasinGeo/", method = RequestMethod.POST)
    public @ResponseBody JSONObject listFacilityForBasinGeo(
            @RequestBody FlightRequest request) {
        return listChartService.listChartBasinFacilitiesGeo(request);
    }

    @RequestMapping(value = "/listGas/", method = RequestMethod.GET)
    public @ResponseBody JSONObject listGas(@RequestBody FlightRequest request)  {

        FacilityType type = FacilityType.fromDataSource(request.getDataSource());

        if (type == FacilityType.SUPPLIERS) {return listChartService.listChartSuppliers(request);}
        return listChartService.listChartEmitterGas(request);
    }

}
