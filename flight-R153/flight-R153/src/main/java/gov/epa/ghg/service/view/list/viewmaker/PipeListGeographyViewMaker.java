package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.service.view.list.ColumnType;
import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PipeListGeographyViewMaker extends AbstractListViewMaker {

    private List<String> labels;
    private Boolean isNotLDC = true;
    private Map<String,String> facilityNameLookup;
    private Map<String,Map<String,BigDecimal>> emissionsMap;

    public PipeListGeographyViewMaker(Map<String, String> facilityNameLookup, Map<String, Map<String, BigDecimal>> keyMap) {

        this.facilityNameLookup = facilityNameLookup;
        this.emissionsMap = keyMap;

    }

    @Override
    public JSONArray buildColumnHeaders() {

        JSONArray columns = new JSONArray();

        if (ModeDomainResolver.Mode.STATE == mode) {
            columns.add(ColumnType.STATE.toJsonObject());
        }
        else {
            columns.add(ColumnType.FACILITY.toJsonObject());
        }
        columns.add(ColumnType.PETROLEUM.toJsonObject());

        return columns;
    }

    @Override
    public JSONArray buildRows() {

        JSONArray rows = new JSONArray();
        JSONObject item = new JSONObject();

        int i = 0;
        for (String key : emissionsMap.keySet()) {
        	Map<String, BigDecimal> pipeEmMap = emissionsMap.get(key);
            item.put("id", "id"+i);
            i++;
            if (ModeDomainResolver.Mode.STATE  == mode ) {
                //jsonChild.put("label", facilityNameLookup.get(key));
                item.put("state", key);
            }
            else {
                item.put("facility", key);
            }
            
            BigDecimal emission = null;
            for (String emissionKey : pipeEmMap.keySet()) {
                if (pipeEmMap.get(emissionKey) != null) {
                    if (emission == null) {
                        emission = BigDecimal.ZERO;
                    }
                    emission = emission.add(pipeEmMap.get(emissionKey));
                }
            }
            
            item.put("petroleum", df.format(ServiceUtils.convert(emission, unit)));

            rows.add(item);
            item.clear();
        }

        return rows;

    }

    public void unrollArguments(Map<String,Object> args) {

        super.unrollArguments(args);

        this.labels = (List<String>) args.get("labels");
        this.isNotLDC = (Boolean) args.get("isNotLDC");
        if (isNotLDC == null) {
            isNotLDC = true;
        }

    }

}
