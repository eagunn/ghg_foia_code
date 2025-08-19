package gov.epa.ghg.service.view.list.viewmaker;

import static gov.epa.ghg.service.view.list.ColumnType.*;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.util.ServiceUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PipeListViewMaker extends AbstractListViewMaker {
	
	

	private Map<String,DimFacilityPipe> dimFacilityMap;
	private Map<String, Map<String,BigDecimal>> emissionsMap;

    private String totalLabel;
    private List<String> labels;
    private FacilityType type;
    
	public PipeListViewMaker(Map<String, String> lookupMap, Map<String, DimFacilityPipe> facilityMap, Map<String, Map<String, BigDecimal>> keyMap) {

		this.dimFacilityMap = facilityMap;
		this.emissionsMap = keyMap;
	}
	

	public JSONArray buildColumnHeaders() {


		JSONArray columns = new JSONArray();
		columns.add(ICONS.toJsonObject());
		columns.add(FACILITY.toJsonObject()); 
		columns.add(CITY.toJsonAnyname("City, State")); 
		columns.add(STATE.toJsonAnyname("Emission State")); 
		columns.add(TOTAL_LABEL.toJsonObjectWithName(totalLabel));
		columns.add(SECTOR.toJsonObject());

		return columns; 
		
	}
	
	
	public JSONArray buildRows() {


        JSONArray rows = new JSONArray();
        JSONObject item = new JSONObject();

        int i = 0;
        for (String key : emissionsMap.keySet()) {
            Map<String, BigDecimal> pipeEmMap = emissionsMap.get(key);
            DimFacilityPipe facility = dimFacilityMap.get(key);
            item.put("id", "id"+i);
            i++;
            item.put("icons", facility.getComments()); //PUB-619 special case for jqGrid list display 
            item.put("facility", key);
            item.put("city", facility.getCity());
            item.put("state", facility.getState());
            BigDecimal emission = null;
            for (String emissionKey : pipeEmMap.keySet()) {
                if (pipeEmMap.get(emissionKey) != null) {
                    if (emission == null) {
                        emission = BigDecimal.ZERO;
                    }
                    emission = emission.add(pipeEmMap.get(emissionKey));
                }
            }
            if (emission == null) {
            	item.put("total", ServiceUtils.formatPipeNullEmissionForDisplay(facility));
            }
            else {
            	item.put("total", df.format(ServiceUtils.convert(emission, unit)));
            }
            item.put("sectors", ServiceUtils.PetroleumAndNaturalGasSystems);

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
