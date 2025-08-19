package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.service.view.list.ColumnType;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by alabdullahwi on 2/10/2016.
 */
public class BasinListViewMaker extends AbstractListViewMaker {

    //from constructor
    private Map<String,String> facilityNameLookup;
    private Map<String,DimFacility> facilityLookup;
    private Map<String,Map<String,BigDecimal>> keyMap;
    private String totalLabel;

    //from ags
    private List<String> labels;

    public BasinListViewMaker(Map<String, String> facilityNameLookup, Map<String, DimFacility> facilityMap, Map<String, Map<String, BigDecimal>> keyMap, String totalLabel) {
        this.facilityNameLookup = facilityNameLookup;
        this.facilityLookup = facilityMap;
        this.keyMap = keyMap;
        this.totalLabel = totalLabel;
    }



    public JSONArray buildColumnHeaders() {

        JSONArray cols = new JSONArray();
        cols.add(ColumnType.ICONS.toJsonObject());
        cols.add(ColumnType.FACILITY.toJsonObject());
        cols.add(ColumnType.CITY.toJsonObject());
        cols.add(ColumnType.STATE.toJsonObject());
        cols.add(ColumnType.TOTAL_LABEL.toJsonObjectWithName(totalLabel));
        cols.add(ColumnType.SECTOR.toJsonObject());

        return cols;
    }


    public JSONArray buildRows() {

        JSONArray rows = new JSONArray();
        JSONObject item = new JSONObject();

        int i = 0;
        for (String key : keyMap.keySet()) {
            Map<String, BigDecimal> sectorMap = keyMap.get(key);
            DimFacility facility = facilityLookup.get(key);
            item.put("id", "id"+i);
            i++;
            item.put("icons", facility.getComments()); //PUB-619 special case for jqGrid list display 
            item.put("facility", key);
            item.put("city", facility.getCity());
            item.put("state", facility.getState());

            String emitterSectors = "";
            for (String sector : labels) {
                BigDecimal emission = null;
                if (totalLabel.equals(sector)) {
                    for (String emissionKey : sectorMap.keySet()) {
                        if (sectorMap.get(emissionKey) != null) {
                            if (emission == null) {
                                emission = BigDecimal.ZERO;
                            }
                            emission = emission.add(sectorMap.get(emissionKey));
                        }
                    }
                } else {
                    emission = sectorMap.get(sector);
                }
                if (totalLabel.equals(sector)) {
                    if (emission == null) {
                        item.put("total", ServiceUtils.formatNullEmissionForDisplay(facility));
                    } else {
                        item.put("total", df.format(ServiceUtils.convert(emission, unit)));
                    }
                } else {
                    if (sectorMap.containsKey(sector)) {
                        if (emitterSectors.length() == 0) {
                            emitterSectors = sector;
                        } else {
                            emitterSectors += ", "+sector;
                        }
                    }
                }
            }
            item.put("sectors", emitterSectors);
            rows.add(item);
            item.clear();
        }

        return rows;

    }


    public void unrollArguments(Map<String,Object> args) {

        super.unrollArguments(args);
        this.labels = (List<String>) args.get("labels");
    }

}
