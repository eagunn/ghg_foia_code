package gov.epa.ghg.service.view.list.viewmaker.trend;

import static gov.epa.ghg.service.view.list.ColumnType.CHANGE_EMISSIONS_ALLYEARS;
import static gov.epa.ghg.service.view.list.ColumnType.CHANGE_EMISSIONS_ENDYEAR;
import static gov.epa.ghg.service.view.list.ColumnType.CITY;
import static gov.epa.ghg.service.view.list.ColumnType.EMISSION_YEAR;
import static gov.epa.ghg.service.view.list.ColumnType.FACILITY;
import static gov.epa.ghg.service.view.list.ColumnType.STATE;
import static gov.epa.ghg.service.view.list.ColumnType.ICONS;

import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.dto.view.SupplierListTrendDetails;
import gov.epa.ghg.service.view.list.viewmaker.AbstractListViewMaker;
import gov.epa.ghg.util.ServiceUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SupplierTrendListViewMaker extends AbstractListViewMaker {
	
	@Resource(name="dataDate")
	private String dataDate;
	
	Map<String, SupplierListDetailsObject> _map;
	Set<Long> years;
	Long startYear;
	Long endYear;
	
	public SupplierTrendListViewMaker(Map<String, SupplierListDetailsObject> _map) {
		this._map = _map;
	}
	

	public JSONArray buildRows() {

		JSONArray rows = new JSONArray();
		JSONObject rowItem = new JSONObject();
		
		int i = 0;
		for (String facKey : _map.keySet()) {
			SupplierListTrendDetails map = (SupplierListTrendDetails) _map.get(facKey);
			rowItem.put("id", "id"+i);
			i++;
			String vIcons = "";
			String facComment = map.getFacility().getComments();
			if (facComment != null) {
				vIcons = vIcons + "<img src='img/co2y.jpg' title='" + facComment + "' alt='" + facComment + "' width='15' height='15' border='0'> ";
			}
			if (map.getReportingStatus() != null && map.getReportingStatus().getShorthand() != "VALID") {
				String rsTxt = map.getReportingStatus().getTextBoxContents();
				rsTxt = rsTxt.replace("'","&apos;");
				vIcons = vIcons + "<img src='img/notification.gif' title='" + rsTxt + "' alt='" + rsTxt + "' width='15' height='15' border='0'> ";
			}
			rowItem.put("icons", vIcons); //PUB-619 special case for jqGrid list display
			rowItem.put("facility", map.getFacilityName() +" ["+ map.getFacility().getId().getFacilityId() +"]");
			rowItem.put("city", map.getCity());
			rowItem.put("state", map.getState());
			for (Long emissionYear : years) {
				BigDecimal val = map.getEmissionsForYear(emissionYear); 
				if (val  != null) {
					rowItem.put("total"+emissionYear, df.format(ServiceUtils.convert(val, unit)));
				} else {
					rowItem.put("total"+emissionYear, "---");
				}
			}
			
				BigDecimal _end = map.getEmissionsForYear(endYear);
				BigDecimal _endMinus1 = map.getEmissionsForYear(endYear-1); 
				BigDecimal _start = map.getEmissionsForYear(startYear); 
				if (_endMinus1  != null && _end != null) {
					rowItem.put("diff"+(endYear-1), df.format(ServiceUtils.convert(_end.subtract(_endMinus1), unit)));
				} else {
					rowItem.put("diff"+(endYear-1), "---");
				}
				if (_start != null && _end  != null) {
					rowItem.put("diff"+startYear, df.format(ServiceUtils.convert(_end.subtract(_start), unit)));
				} else {
					rowItem.put("diff"+startYear, "---");
				}
			rows.add(rowItem);
			rowItem.clear();
		}
		
		return rows; 
		
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
		
		return columns; 
		
	}

	public void unrollArguments(Map<String,Object> args) {

		super.unrollArguments(args);

		this.years = (Set<Long>) args.get("years");
		this.startYear = (Long) args.get("startYear");
		this.endYear = (Long) args.get("endYear");
	}


	
}
