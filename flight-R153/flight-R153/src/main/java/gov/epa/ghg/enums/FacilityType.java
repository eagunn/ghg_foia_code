package gov.epa.ghg.enums;

public enum FacilityType {
	EMITTERS("E"), SUPPLIERS("S"), ONSHORE("O"), CO2_INJECTION("I"), RR_CO2("A");

	FacilityType(String _initial) {
		this.initial = _initial;
	}
	private String initial;


	public static String getFullName(String letter) {
	
		if ("E".equalsIgnoreCase(letter)) {
			return "All Direct Emitters";
		}
		if ("P".equalsIgnoreCase(letter)) {
			return "Point Sources";
		}
		if ("O".equalsIgnoreCase(letter)) {
			return "Onshore Oil & Gas Production";
		}
		if ("L".equalsIgnoreCase(letter)) {
			return "Local Distribution Companies";
		}
		if ("F".equalsIgnoreCase(letter)) {
			return "SF6 From Electrical Distribution Systems";
		}
		if ("B".equalsIgnoreCase(letter)) {
			return "Onshore Oil & Gas Gathering & Boosting";
		}
		if ("T".equalsIgnoreCase(letter)) {
			return "Onshore Gas Transmission Pipelines";
		}
		if ("S".equalsIgnoreCase(letter)) {
			return "Suppliers";
		}
		if ("I".equalsIgnoreCase(letter)) {
			return "CO2 Injection (UU)";
		}
		if ("A".equalsIgnoreCase(letter)) {
			return "Geologic Sequestration of CO2 (RR)";
		}
		else {
			return "";
		}
	}
	
	public String asInitial() {
		return initial;
	}

	public static FacilityType fromDataSource(String dataSource) {
		
		//all emitter subtypes return E, for methods that have the same approach for all emitters
		if ("E".equals(dataSource) 
				|| "L".equals(dataSource) 
				|| "F".equals(dataSource) 
				|| "P".equals(dataSource) 
				|| "B".equals(dataSource) 
				|| "T".equals(dataSource)) {
			return EMITTERS; 
		}
		if ("O".equals(dataSource)) {
			return ONSHORE;
		}
        if ("S".equals(dataSource)) {
			return SUPPLIERS; 
		}
		if ("I".equals(dataSource)) {
			return CO2_INJECTION;
		}
		if ("A".equals(dataSource)) {
			return RR_CO2;
		}

		return null; 
	}
}
