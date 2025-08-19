package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.service.view.list.ModeDomainResolver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.NumberFormat;
import java.util.Map;

/**
 * Created by alabdullahwi on 2/8/2016.
 */
public abstract class AbstractListViewMaker implements ListViewMaker {

    protected String unit;
    protected String domain;
    protected ModeDomainResolver.Mode mode;
    protected int currentYear;
    protected int year;
    protected NumberFormat df;

    //not being used on the front-end side yet
    public String buildTableHeader() {

        if ( mode != null && unit != null) {
            return year  + " - " + "Total Reported Emissions by " + mode.getViewName() + "/Sector in " + unit
                    + " of CO<sub>2</sub>e "
                    ;
        }

        return "";
    }

    public void unrollArguments(Map<String, Object> args) {

        if (null != args.get("mode")) {
            this.mode =  (ModeDomainResolver.Mode) args.get("mode");
        }
        if (null != args.get("domain")) {
            this.domain = (String) args.get("domain");
        }
        if (null != args.get("unit")) {
            this.unit = (String) args.get("unit");
        }
        if (null != args.get("currentYear")) {
            this.currentYear = (Integer) args.get("currentYear");
        }
        if (null != args.get("year")) {
            this.year = (Integer) args.get("year");
        }
        if (null != args.get("df")) {
            this.df = (NumberFormat) args.get("df");
        }
    }


    public JSONObject buildListData(){

        JSONObject listData = new JSONObject();
        JSONArray columnHeaders = this.buildColumnHeaders();
        listData.put("cols", columnHeaders);
        JSONArray rows = this.buildRows();
        listData.put("rows", rows);

        return listData;
    }

    public JSONObject createView(Map<String,Object> args ) {

        unrollArguments(args);

        JSONObject retv = new JSONObject();

        retv.put("data", this.buildListData());

        //this should suffice instead of exposing domain/mode/unit/year and have the frontend
        //wrangle them together
        retv.put("tableHeader", this.buildTableHeader());

        if (domain != null) {
            retv.put("domain", domain);
        }
        if (mode != null) {
            retv.put("mode", mode.name());
        }
        if (unit != null) {
            retv.put("unit", unit);
        }
        if (currentYear != 0) {
            retv.put("currentYear", currentYear);
        }
        if (year != 0) {
            retv.put("year", year);
        }
        return retv;

    }

}
