package gov.epa.ghg.dto.view;

import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.FacilityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeList {
	
	int totalCount;
	List<FacilityView> facilities;
	Map<String,DimFacilityPipe> pipeFacilityMap;

	public PipeList() {
		super();
		this.totalCount = 0;
		this.facilities = new ArrayList<FacilityView>();
		this.pipeFacilityMap = new HashMap<String, DimFacilityPipe>();
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<FacilityView> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<FacilityView> facilities) {
		this.facilities = facilities;
	}

	public Map<String, DimFacilityPipe> getPipeFacilityMap() {
		return pipeFacilityMap;
	}

	public void setPipeFacilityMap(Map<String, DimFacilityPipe> pipeFacilityMap) {
		this.pipeFacilityMap = pipeFacilityMap;
	}
}
