package gov.epa.ghg.dto;

public class QueryOptions {

	private boolean nameSelected;
	private boolean citySelected;	
	private boolean countySelected;
	private boolean stateSelected;
	private boolean zipSelected;
	private boolean idSelected;
	private boolean naicsSelected;
	private boolean parentSelected;

	public QueryOptions() {
	}

	public String toString() {

		String retv="";

		boolean[] arr = {nameSelected, citySelected, countySelected, stateSelected, zipSelected, idSelected, naicsSelected, parentSelected};

		for (int i = 0 ; i<arr.length; i++) {

			if (arr[i]) {
				retv +="1";
			}

			else {
				retv+="0";
			}

		}

		return retv;
	}
	
	public QueryOptions(String options) {
		super();
		if (options!=null && options.length()==8) {
			if ("1".equals(options.substring(0, 1))) this.nameSelected=true;
			if ("1".equals(options.substring(1, 2))) this.citySelected=true;			
			if ("1".equals(options.substring(2, 3))) this.countySelected=true;
			if ("1".equals(options.substring(3, 4))) this.stateSelected=true;
			if ("1".equals(options.substring(4, 5))) this.zipSelected=true;
			if ("1".equals(options.substring(5, 6))) this.idSelected=true;
			if ("1".equals(options.substring(6, 7))) this.naicsSelected=true;
			if ("1".equals(options.substring(7, 8))) this.parentSelected=true;
		} else {
			this.nameSelected=true;
			this.citySelected=true;
			this.countySelected = false;
			this.stateSelected = false;
			this.zipSelected = true;
			this.idSelected = false;
			this.naicsSelected = false;
			this.parentSelected = false;
		}
	}

	public boolean isNameSelected() {
		return nameSelected;
	}

	public void setNameSelected(boolean nameSelected) {
		this.nameSelected = nameSelected;
	}

	public boolean isCitySelected() {
		return citySelected;
	}

	public void setCitySelected(boolean citySelected) {
		this.citySelected = citySelected;
	}

	public boolean isCountySelected() {
		return countySelected;
	}

	public void setCountySelected(boolean countySelected) {
		this.countySelected = countySelected;
	}

	public boolean isStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(boolean stateSelected) {
		this.stateSelected = stateSelected;
	}

	public boolean isZipSelected() {
		return zipSelected;
	}

	public void setZipSelected(boolean zipSelected) {
		this.zipSelected = zipSelected;
	}

	public boolean isIdSelected() {
		return idSelected;
	}

	public void setIdSelected(boolean idSelected) {
		this.idSelected = idSelected;
	}

	public boolean isNaicsSelected() {
		return naicsSelected;
	}

	public void setNaicsSelected(boolean naicsSelected) {
		this.naicsSelected = naicsSelected;
	}

	public boolean isParentSelected() {
		return parentSelected;
	}

	public void setParentSelected(boolean parentSelected) {
		this.parentSelected = parentSelected;
	}
}
