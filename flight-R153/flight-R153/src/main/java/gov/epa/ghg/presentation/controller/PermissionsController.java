package gov.epa.ghg.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Created by alabdullahwi on 8/20/2015.
 */

@Controller
public class PermissionsController {

    @RequestMapping(value = "/sectorPopupPermission", method = RequestMethod.GET)
    public @ResponseBody String getSectorPopupPermission(
            @RequestParam(value="id", required=false) String popupId,
            HttpSession session,
            ModelMap model) {

        boolean properID = false;

        ArrayList<String> popupIds = new ArrayList<String>();
        popupIds.add("electronics_manufacturing");
        popupIds.add("electrical_power");
        popupIds.add("magnesium");
        popupIds.add("oilandgas");
        popupIds.add("municipal_landfill");
        popupIds.add("aluminum");
        popupIds.add("underground_coal_mines");
        popupIds.add("gwp_4th");
        //subsectors
        popupIds.add("petroUnderground");

        for( String id : popupIds )
            if( popupId.equals(id) )
                properID = true;

        if( properID && session.getAttribute(popupId) == null ) {
            session.setAttribute(popupId, true);
            return "show";
        }
        else
            return "hide";
    }


    @RequestMapping(value = "/statePopupPermission", method = RequestMethod.GET)
    public @ResponseBody
    String getStatePopupPermission(
            @RequestParam(value="id", required=false) String popupId,
            HttpSession session,
            ModelMap model) {

        boolean properID = false;

        ArrayList<String> popupIds = new ArrayList<String>();
        popupIds.add("MA");
        popupIds.add("WA");

        for( String id : popupIds )
            if( popupId.equals(id) )
                properID = true;

        if( properID && session.getAttribute(popupId) == null ) {
            return "show";
        }
        else
            return "hide";
    }

    @RequestMapping(value = "/ghgPopupPermission", method = RequestMethod.GET)
    public @ResponseBody String getGhgPopupPermission(
            @RequestParam(value="id", required=false) String popupId,
            HttpSession session,
            ModelMap model) {

        boolean properID = false;

        ArrayList<String> popupIds = new ArrayList<String>();
        popupIds.add("co2");
        popupIds.add("methane");
        popupIds.add("n2o");
        popupIds.add("flourinated_gases");

        for( String id : popupIds )
            if( popupId.equals(id) )
                properID = true;

        if( properID && session.getAttribute(popupId) == null ) {
            session.setAttribute(popupId, true);
            return "show";
        }
        else
            return "hide";
    }
}
