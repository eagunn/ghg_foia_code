package gov.epa.ghg.service.view.list.viewmaker;

import gov.epa.ghg.dto.SupplierListDetails;
import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.service.view.list.ColumnType;
import gov.epa.ghg.util.ServiceUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by alabdullahwi on 2/9/2016.
 */
public class SupplierListViewMaker extends AbstractListViewMaker {
	
	@Resource(name="dataDate")
	private String dataDate;

    Map<String, SupplierListDetailsObject> lookupMap;


    public SupplierListViewMaker(Map<String, SupplierListDetailsObject> _map) {
        this.lookupMap = _map;
    }


    @Override
    public JSONArray buildColumnHeaders() {

        JSONArray cols = new JSONArray();
        cols.add(ColumnType.ICONS.toJsonObject());
        cols.add(ColumnType.FACILITY.toJsonObject());
        cols.add(ColumnType.CITY.toJsonObject());
        cols.add(ColumnType.STATE.toJsonObject());
        cols.add(ColumnType.TOTAL_LABEL.toJsonObjectWithName("Total Reported GHG Quantity"));

        return cols;
    }

    @Override
    public JSONArray buildRows() {

        JSONArray rows = new JSONArray();
        JSONObject item = new JSONObject();

        int i = 0;
        for (String facilityName : lookupMap.keySet()) {
            SupplierListDetails sld = (SupplierListDetails) lookupMap.get(facilityName);
            item.put("id", "id"+i);
            i++;
            String vIcons = "";
			String facComment = sld.getFacility().getComments();
			if (facComment != null) {
				vIcons = vIcons + "<img src='img/co2y.jpg' title='" + facComment + "' alt='" + facComment + "' width='15' height='15' border='0'> ";
			}
			if (sld.getReportingStatus() != null && sld.getReportingStatus().getShorthand() != "VALID") {
				String rsTxt = sld.getReportingStatus().getTextBoxContents();
				rsTxt = rsTxt.replace("'","&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}			
			item.put("icons", vIcons); //PUB-619 special case for jqGrid list display
			item.put("facility", sld.getFacility().getFacilityName() +" ["+ sld.getFacility().getId().getFacilityId() +"]");
            item.put("city", sld.getFacility().getCity());
            item.put("state", sld.getFacility().getState());

            if (sld.getTotalCo2e() == null) {
                item.put("total", ServiceUtils.formatNullEmissionForDisplay(sld.getFacility()));
            } else {
                long co2e = ServiceUtils.convert(sld.getTotalCo2e(), unit);
                item.put("total", df.format(co2e));
            }
            rows.add(item);
            item.clear();
        }
        return rows;
    }
}
