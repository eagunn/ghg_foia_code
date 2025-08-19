package gov.epa.ghg.service.view.list.viewmaker;

import static gov.epa.ghg.service.view.list.ColumnType.CITY;
import static gov.epa.ghg.service.view.list.ColumnType.FACILITY;
import static gov.epa.ghg.service.view.list.ColumnType.SECTOR;
import static gov.epa.ghg.service.view.list.ColumnType.STATE;
import static gov.epa.ghg.service.view.list.ColumnType.TOTAL_LABEL;
import static gov.epa.ghg.service.view.list.ColumnType.ICONS;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.util.ServiceUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EmitterListViewMaker extends AbstractListViewMaker {
	
	

	private Map<String,String> facilityLookupMap;
	private Map<String,DimFacility> dimFacilityMap;
	private Map<String, Map<String,BigDecimal>> emissionsMap;

    private String totalLabel;
    private List<String> labels;
    private FacilityType type;

	public EmitterListViewMaker(Map<String, String> lookupMap, Map<String, DimFacility> facilityMap, Map<String, Map<String, BigDecimal>> keyMap) {

		this.facilityLookupMap = lookupMap;
		this.dimFacilityMap = facilityMap;
		this.emissionsMap = keyMap;
	}
	

	public JSONArray buildColumnHeaders() {


		JSONArray columns = new JSONArray();
		columns.add(ICONS.toJsonObject());
		columns.add(FACILITY.toJsonObject()); 
		columns.add(CITY.toJsonObject()); 
		columns.add(STATE.toJsonObject());
		columns.add(TOTAL_LABEL.toJsonObjectWithName(totalLabel));
		if (type != FacilityType.CO2_INJECTION && type != FacilityType.RR_CO2) {
			columns.add(SECTOR.toJsonObject());
		}

		return columns; 
		
	}
	
	
    public JSONArray buildRows() {


        JSONArray rows = new JSONArray();
        JSONObject item = new JSONObject();

        int i = 0;
        for (String key : emissionsMap.keySet()) {
            Map<String, BigDecimal> sectorMap = emissionsMap.get(key);
            DimFacility facility = dimFacilityMap.get(key);
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
            if (type != FacilityType.CO2_INJECTION && type != FacilityType.RR_CO2) {
                item.put("sectors", emitterSectors);
            }

            rows.add(item);
            item.clear();
        }

            return rows;

    }


    public void unrollArguments(Map<String,Object> args) {

        super.unrollArguments(args);
        this.totalLabel = (String) args.get("totalLabel");
        this.labels = (List<String>) args.get("labels");
        this.type = (FacilityType) args.get("type");
    }


}
