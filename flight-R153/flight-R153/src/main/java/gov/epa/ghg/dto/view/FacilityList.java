package gov.epa.ghg.dto.view;

import gov.epa.ghg.domain.FacilityView;

import java.util.ArrayList;
import java.util.List;

public class FacilityList {
	
	int totalCount;
	List<FacilityView> facilities;

	public FacilityList() {
		super();
		this.totalCount = 0;
		this.facilities = new ArrayList<FacilityView>();
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
}
