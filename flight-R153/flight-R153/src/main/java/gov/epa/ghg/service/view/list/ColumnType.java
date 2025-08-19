package gov.epa.ghg.service.view.list;


import net.sf.json.JSONObject;
import static gov.epa.ghg.service.ListChartService.endYear;
import static gov.epa.ghg.service.ListChartService.startYear;



public enum ColumnType {



	FACILITY					("facility", "Facility", true, "string", "list-item")
	,COUNTY						("county", "County", true, "string", "list-item")
	,BASIN						("basin", "Basin", true, "string", "list-item")
	,TRIBAL_LAND				("tribal land", "Tribe Name",true, "string", "list-item")
	,CITY						("city","City",true,"string","list-item")
	,STATE						("state","State",true,"string","list-item")
	,EMISSION_YEAR				("total", "Total Reported Emissions, ", true, "number", "list-item list-number")
	,CHANGE_EMISSIONS_ENDYEAR	("diff"+(endYear-1), "Change in Emissions ("+(endYear-1)+ " to " + endYear+")", true, "number", "list-item list-number")
	,CHANGE_EMISSIONS_ALLYEARS	("diff"+ startYear, "Change in Emissions (" + startYear + " to " + endYear+")" , true, "number", "list-item list-number")
	,SECTOR						("sectors","Sectors", true,"string", "list-item")
	,TOTAL_LABEL				("total", "Total Reported Emissions", true, "number", "list-item list-number" )
	,POWER_PLANT				("power", "Power Plants",true,"number", "list-item list-number")
	,PETROLEUM					("petroleum", "Petroleum and Natural Gas Systems",true,"number", "list-item list-number")
	,REFINERIES					("refineries", "Refineries",true,"number", "list-item list-number")
	,CHEMICALS					("chemicals", "Chemicals",true,"number", "list-item list-number")
	,OTHER						("other", "Other",true,"number", "list-item list-number")
	,WASTE						("waste", "Waste",true,"number", "list-item list-number")
	,MINERALS					("minerals", "Minerals",true,"number", "list-item list-number")
	,METALS						("metals", "Metals",true,"number", "list-item list-number")
	,PULP						("pulp", "Pulp and Paper",true,"number", "list-item list-number")
	,ICONS					    ("icons","", false,"string","")
	;



	ColumnType(String id, String name, boolean isSortable, String type, String cssClass) {
		this.id = id;
		this.name = name;
		this.field = id;
		this.isSortable = isSortable;
		this.type = type;
		this.cssClass = cssClass; 
	}
	
	

	String name;
	String id;
	String field; 
	boolean isSortable;
	String type;
	String cssClass;


	//this is useful sometimes when the TOTAL_REPORTED_EMISSIONS's name should change
	public JSONObject toJsonObjectWithName(String totalLabel) {

		JSONObject retv = this.toJsonObject();
		//just for extra precaution, this should be only used with TOTAL_LABEL
		if (this == TOTAL_LABEL) {
			retv.put("name", totalLabel);
		}

		return retv;

	}

	public JSONObject toJsonAnyname(String anyName) {

		JSONObject retv = this.toJsonObject();
		retv.put("name", anyName);

		return retv;

	}
	
	public JSONObject toJsonObjectWithYear(Long year) {
		
		if (this == EMISSION_YEAR) {
			JSONObject retVal= new JSONObject(); 
			retVal.put("id", id+year);
			retVal.put("name", name+year);
			retVal.put("field", field+year);
			retVal.put("sortable", isSortable);
			retVal.put("type", type);
			retVal.put("cssClass", cssClass);
			return retVal; 
		}
		
		else {
			return toJsonObject(); 
		}
		
	}
	
	
	public JSONObject toJsonObject() {
	
		JSONObject retVal= new JSONObject(); 
		retVal.put("id", id);
		retVal.put("name", name);
		retVal.put("field", field);
		retVal.put("sortable", isSortable);
		retVal.put("type", type);
		retVal.put("cssClass", cssClass);
	
		return retVal; 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
	

