package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.service.view.list.ColumnType;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import gov.epa.ghg.service.view.list.ModeDomainResolver.Mode;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alabdullahwi on 2/9/2016.
 */
public class BasinGeoListViewMaker extends AbstractListViewMaker {

    private Map<String,String> facilityNameLookup;
    private Map<String,Map<String,BigDecimal>> keyMap;
    private Map<String, Map<String, Long>> countMap;
    private List<BigDecimal> emissions;
    private List<String> labels;

    public BasinGeoListViewMaker(Map<String, String> facilityNameLookup, Map<String, Map<String, BigDecimal>> keyMap, Map<String, Map<String, Long>> countMap, List<BigDecimal> emissions) {
        this.facilityNameLookup = facilityNameLookup;
        this.keyMap = keyMap;
        this.countMap = countMap;
        this.emissions = emissions;
    }

    @Override
    public JSONArray buildColumnHeaders() {
        JSONArray cols = new JSONArray();

        if (mode == Mode.BASIN ) {cols.add(ColumnType.BASIN.toJsonObject());}
        else {cols.add(ColumnType.FACILITY.toJsonObject());}
        cols.add(ColumnType.PETROLEUM.toJsonObject());
        return cols;
    }

    @Override
    public JSONArray buildRows() {
        JSONArray rows = new JSONArray();

        JSONObject item = new JSONObject();

        Map<String, Long> totalEmissions = new HashMap<String, Long>();
        Map<String, Long> totalFacilities = new HashMap<String, Long>();
        int i = 0;
        for (String key : keyMap.keySet()) {
            Map<String, BigDecimal> sectorMap = keyMap.get(key);
            Map<String, Long> facilityCountMap = countMap.get(key);
            item.put("id", "id"+i);
            i++;
            if (Mode.BASIN == mode) {
                item.put("basin", key);
            } else {
                item.put("facility", facilityNameLookup.get(key));
            }
            Long totalEmission = 0L;
            Long count = 0L;
            for (String sector : labels) {
                BigDecimal emission = BigDecimal.ZERO;
                if ("Total Reported Emissions".equals(sector)) {
                    for (String emissionKey : sectorMap.keySet()) {
                        emission = emission.add(sectorMap.get(emissionKey));
                    }
                    totalEmission = ServiceUtils.convert(emission, unit);
                } else {
                    emission = sectorMap.get(sector);
                    count = facilityCountMap.get(sector);
                }
                if (totalEmissions.containsKey(sector)) {
                    Long te = totalEmissions.get(sector);
                    te += ServiceUtils.convert(emission, unit);
                    totalEmissions.put(sector, te);
                } else {
                    totalEmissions.put(sector, ServiceUtils.convert(emission, unit));
                }
                if (totalFacilities.containsKey(sector)) {
                    Long tf = totalFacilities.get(sector);
                    tf += count;
                    totalFacilities.put(sector, tf);
                } else {
                    totalFacilities.put(sector, count);
                }

                if ("Petroleum and Natural Gas Systems".equals(sector)) {
                    item.put("petroleum", df.format(ServiceUtils.convert(emission, unit)));
                }

            }
            rows.add(item);
            item.clear();
        }

        return rows;
    }

    public void unrollArguments(Map<String,Object> args) {

        super.unrollArguments(args);

        this.unit = ServiceUtils.getUnit(emissions);
        this.labels = (List<String>) args.get("labels");

    }





}
