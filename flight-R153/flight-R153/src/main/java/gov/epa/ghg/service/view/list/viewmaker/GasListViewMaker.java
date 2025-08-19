package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alabdullahwi on 2/10/2016.
 */
public class GasListViewMaker extends AbstractListViewMaker {


    private Map<String,String> facilityNameLookup;
    private Map<String,Map<String,BigDecimal>> keyMap;
    private List<BigDecimal> emissions;
    private List<String> labels;


    public GasListViewMaker(Map<String, String> facilityNameLookup, Map<String, Map<String, BigDecimal>> keyMap, List<BigDecimal> emissions) {
        this.facilityNameLookup = facilityNameLookup;
        this.keyMap = keyMap;
        this.emissions = emissions;
    }

    //not used in gas
    public JSONArray buildColumnHeaders() {return null;}

    public JSONArray buildRows() {

        JSONArray rows = new JSONArray();

        JSONObject jsonChild = new JSONObject();

        Map<String, Long> totalEmissions = new HashMap<String, Long>();
        for (String key : keyMap.keySet()) {
            Map<String, BigDecimal> gasMap = keyMap.get(key);
            if (ModeDomainResolver.Mode.FACILITY == mode) {
                jsonChild.put("label", facilityNameLookup.get(key));
            } else {
                jsonChild.put("label", key);
            }
            Long totalEmission = 0L;
            for (String gas : labels) {
                BigDecimal emission = BigDecimal.ZERO;
                if ("Total".equals(gas)) {
                    for (String emissionKey : gasMap.keySet()) {
                        emission = emission.add(gasMap.get(emissionKey));
                    }
                    totalEmission = ServiceUtils.convert(emission, unit);
                } else {
                    emission = gasMap.get(gas);
                }
                if (totalEmissions.containsKey(gas)) {
                    Long te = totalEmissions.get(gas);
                    te += ServiceUtils.convert(emission, unit);
                    totalEmissions.put(gas, te);
                } else {
                    totalEmissions.put(gas, ServiceUtils.convert(emission, unit));
                }
                jsonChild.accumulate("emissions", ServiceUtils.convert(emission, unit));
            }
            if (totalEmission > 0) {
                rows.add(jsonChild);
            }
            jsonChild.clear();
        }

        if (keyMap.size() > 1) {
            if ("STATE".equals(mode)) {
                jsonChild.put("label", "US Totals");
            } else if ("COUNTY".equals(mode)) {
                jsonChild.put("label", "State Totals");
            } else if ("FACILITY".equals(mode)) {
                jsonChild.put("label", "Facility Totals");
            }
            for (String sector : labels) {
                Long te = totalEmissions.get(sector);
                jsonChild.accumulate("emissions", te);
            }
            rows.add(jsonChild);
        }
        return rows;
    }

    public void unrollArguments(Map<String,Object> args) {

        super.unrollArguments(args);
        this.labels = (List<String>)args.get("labels");

    }

    public JSONObject createView(Map<String,Object> args) {

        JSONObject jsonParent = new JSONObject();

        //columns are baked directly in the jsonParent here
        for (String label : labels) {
            if (!"Carbon Dioxide (CO<sub>2</sub>)".equals(label))
                jsonParent.accumulate("label", label+" ("+unit+" CO<sub>2</sub>e)");
            else
                jsonParent.accumulate("label", label);
        }

        jsonParent.put("values", buildRows());

        //that's it
        return jsonParent;

    }


}
