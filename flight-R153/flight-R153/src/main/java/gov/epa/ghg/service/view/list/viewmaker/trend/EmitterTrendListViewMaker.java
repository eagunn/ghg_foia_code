package gov.epa.ghg.service.view.list.viewmaker.trend;


import static gov.epa.ghg.service.view.list.ColumnType.CHANGE_EMISSIONS_ALLYEARS;
import static gov.epa.ghg.service.view.list.ColumnType.CHANGE_EMISSIONS_ENDYEAR;
import static gov.epa.ghg.service.view.list.ColumnType.CITY;
import static gov.epa.ghg.service.view.list.ColumnType.EMISSION_YEAR;
import static gov.epa.ghg.service.view.list.ColumnType.FACILITY;
import static gov.epa.ghg.service.view.list.ColumnType.SECTOR;
import static gov.epa.ghg.service.view.list.ColumnType.STATE;
import static gov.epa.ghg.service.view.list.ColumnType.ICONS;

import gov.epa.ghg.service.view.list.viewmaker.AbstractListViewMaker;
import gov.epa.ghg.util.ServiceUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by alabdullahwi on 2/3/2016.
 */
public class EmitterTrendListViewMaker extends AbstractListViewMaker {

    private Map<String, Map<String, Object>> trendFacilityMap;
    private Set<Long> years;
    private Long startYear;
    private Long endYear;

    public EmitterTrendListViewMaker(Map<String, Map<String, Object>> facilityMap) {
        this.trendFacilityMap = facilityMap;
    }


    public JSONArray buildColumnHeaders() {

        JSONArray columns = new JSONArray();
        columns.add(ICONS.toJsonObject());
        columns.add(FACILITY.toJsonObject());
        columns.add(CITY.toJsonObject());
        columns.add(STATE.toJsonObject());

        for (Long emissionYear : years) {
            columns.add(EMISSION_YEAR.toJsonObjectWithYear(emissionYear));
        }

        columns.add(CHANGE_EMISSIONS_ENDYEAR.toJsonObject());
        columns.add(CHANGE_EMISSIONS_ALLYEARS.toJsonObject());
        columns.add(SECTOR.toJsonObject());

        return columns;

    }



    public JSONArray buildRows() {

        JSONArray rows = new JSONArray();
        JSONObject rowItem = new JSONObject();

        int i = 0;
        for (String facKey : trendFacilityMap.keySet()) {
            Map<String, Object> dataMap = trendFacilityMap.get(facKey);
            rowItem.put("id", "id"+i);
            i++;
            rowItem.put("icons", dataMap.get("icons")); //PUB-619 special case for jqGrid list display
            rowItem.put("facility", dataMap.get("name"));
            rowItem.put("city", dataMap.get("city"));
            rowItem.put("state", dataMap.get("state"));
            Map<Long, BigDecimal> emissionsMap = (Map<Long, BigDecimal>)dataMap.get("emissions");
            for (Long emissionYear : years) {
                if (emissionsMap.get(emissionYear) != null) {
                    rowItem.put("total"+emissionYear, df.format(ServiceUtils.convert(emissionsMap.get(emissionYear), unit)));
                } else {
                    rowItem.put("total"+emissionYear, "---");
                }
            }
            if (emissionsMap.get(endYear-1) != null && emissionsMap.get(endYear) != null) {
                rowItem.put("diff"+(endYear-1), df.format(ServiceUtils.convert(emissionsMap.get(endYear).subtract(emissionsMap.get(endYear-1)), unit)));
            } else {
                rowItem.put("diff"+(endYear-1), "---");
            }
            if (emissionsMap.get(startYear) != null && emissionsMap.get(endYear) != null) {
                rowItem.put("diff"+startYear, df.format(ServiceUtils.convert(emissionsMap.get(endYear).subtract(emissionsMap.get(startYear)), unit)));
            } else {
                rowItem.put("diff"+startYear, "---");
            }
            String sectorString = "";
            List<String> sectorList = (ArrayList<String>)dataMap.get("sectors");
            for (String sector : sectorList) {
                if (StringUtils.hasText(sectorString)) {
                    sectorString += ", ";
                }
                sectorString += sector;
            }
            rowItem.put("sectors", sectorString);
            rows.add(rowItem);
            rowItem.clear();
        }

        return rows;

    }


    public void unrollArguments(Map<String, Object> args) {

        super.unrollArguments(args);

        this.years = (Set<Long>) args.get("years");
        this.startYear = (Long) args.get("startYear");
        this.endYear = (Long) args.get("endYear");
    }


    public JSONObject createView(Map<String, Object> args) {

        JSONObject retv = super.createView(args);
        retv.put("trend", "trend");
        return retv;
    }
}
