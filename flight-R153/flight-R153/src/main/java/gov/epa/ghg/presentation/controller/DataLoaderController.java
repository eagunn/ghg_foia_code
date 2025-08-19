package gov.epa.ghg.presentation.controller;

import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.dto.*;
import gov.epa.ghg.service.FacilityViewService;
import gov.epa.ghg.service.GisService;
import gov.epa.ghg.service.LayerService;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alabdullahwi on 8/20/2015.
 *
 * this controller takes several related methods from GoogleMapsController, it always returns JSON -- so it's like a @RestController
 */

@Controller
public class DataLoaderController {

    @Inject
    FacilityViewService facilityViewService;

    @Inject
    LayerService layerService;

    @Inject
    GisService gisService;


    @RequestMapping(value = "/getCountiesFromState/{year}", method = RequestMethod.GET)
    public @ResponseBody
    List<County> getCounties(
            @PathVariable int year,
            @RequestParam(value="st", required=false) String stateCode){
        List<DimCounty> counties = facilityViewService.getFacilityCounties(stateCode, year);
        List<County> c = new ArrayList<County>();
        for (DimCounty dc : counties) {
            c.add(new County(dc.getCountyFips(), dc.getCountyName()));
        }
        return c;
    }

    @RequestMapping(value = "/getMSAsFromState/{year}", method = RequestMethod.GET)
    public @ResponseBody List<Metro> getMsas(
            @PathVariable int year,
            @RequestParam(value="st", required=false) String stateCode){
        List<DimMsa> metros = facilityViewService.getMsas(stateCode);

        List<Metro> m = new ArrayList<Metro>();
        for (DimMsa msa : metros) {
            m.add(new Metro(msa.getCbsafp(), msa.getCbsa_title()));
        }
        return m;
    }

    @RequestMapping(value = "/getTribalLands", method = RequestMethod.GET)
    public @ResponseBody List<LuTribalLands> getTribalLands() {
        return facilityViewService.getTribalLands();
    }

    @RequestMapping(value = "/basins", method = RequestMethod.GET)
    public @ResponseBody List<Basin> getBasins(
            @RequestParam(value="bs", required=false) String basinCode){
        return facilityViewService.getBasins();
    }

    @RequestMapping(value = "/basinsGeo", method = RequestMethod.GET)
    public @ResponseBody List<ServiceArea> getBasinShapes(HttpSession session, ModelMap model) {
        return layerService.getBasinShapes();
    }

    @RequestMapping(value = "/getCountyFromFips", method = RequestMethod.GET)
    public @ResponseBody String getCountyName(
            @RequestParam(value="fc", required=false) int fipsCode) {
        return facilityViewService.getCountyName(String.valueOf(fipsCode));
    }

    @RequestMapping(value = "/getFipsFromCountyAndState/", method = RequestMethod.GET)
    public @ResponseBody int getFips(
            @RequestParam(value="st", required=false) String state,
            @RequestParam(value="c", required=false) String county) {
        return facilityViewService.getFips(state, county);
    }

    @RequestMapping(value = "/getIdFromTribalLand/", method = RequestMethod.GET)
    public @ResponseBody int getTribalLandId(
            @RequestParam(value="tl", required=false) String tribalLandName) {
        return facilityViewService.getTribalLandId(tribalLandName);
    }

    @RequestMapping(value="/getStateBounds/local", method = RequestMethod.GET)
    public @ResponseBody
    JSONArray getStateBoundsLocal() {
        JSONArray arr=null;
        InputStream jsonStream=null;
        String jsonString = "";
        try{
            jsonStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("statelocal.json");
            java.util.Scanner s = new java.util.Scanner(jsonStream).useDelimiter("\\A");
            if (s.hasNext()) {
                jsonString= s.next();

            }
            arr = JSONArray.fromObject(jsonString);
        }
        finally {
            try {
                jsonStream.close();
            }
            catch(IOException ioex){
                ioex.printStackTrace();
            }

        }


        return arr;
    }

    @RequestMapping(value = "/getStateMsaShapes/{stateAbbr}", method = RequestMethod.GET)
    public @ResponseBody List<ServiceArea> getStateMsaShapes(
            @PathVariable String stateAbbr){
        return layerService.getMsaShapes(stateAbbr);
    }

    @RequestMapping(value = "/getCountyShapes/{stateAbbr}", method = RequestMethod.GET)
    public @ResponseBody List<ServiceArea> getCountyShapes(
            @PathVariable String stateAbbr){
        return layerService.getCountyShapes(stateAbbr);
    }

    @RequestMapping(value = "/getStateBounds/{stateCode}", method = RequestMethod.GET)
    public @ResponseBody
    LatLngBounds getStateEnvelope(
            @PathVariable String stateCode){
        return gisService.getStateBounds(stateCode);
    }

    @RequestMapping(value = "/getCountyBounds/{fipsCode}", method = RequestMethod.GET)
    public @ResponseBody LatLngBounds getCountyEnvelope(
            @PathVariable String fipsCode){
        return gisService.getCountyBounds(fipsCode);
    }

    @RequestMapping(value = "/getBasinBounds/{basinCode}", method = RequestMethod.GET)
    public @ResponseBody LatLngBounds getBasinEnvelope(
            @PathVariable String basinCode){
        return gisService.getBasinBounds(basinCode);
    }

    @RequestMapping(value = "/getMsaBounds/{msaCode}", method = RequestMethod.GET)
    public @ResponseBody LatLngBounds getMsaEnvelope(
            @PathVariable Integer msaCode){
        return gisService.getMsaBounds(String.valueOf(msaCode));
    }

    @RequestMapping(value = "/ldcs", method = RequestMethod.GET)
    public @ResponseBody List<ServiceArea> getLdcShapes(HttpSession session, ModelMap model) {
        return layerService.getBasinShapes();
    }
}
