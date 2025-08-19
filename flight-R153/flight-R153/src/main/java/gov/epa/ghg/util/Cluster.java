package gov.epa.ghg.util;

import gov.epa.ghg.domain.FacilityViewSub;
import gov.epa.ghg.dto.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	LatLng center;
	LatLng averageCenter;
	List<FacilityViewSub> facilities = new ArrayList<FacilityViewSub>();

	public LatLng getCenter() {
		return center;
	}

	public void setCenter(LatLng center) {
		this.center = center;
	}

	public List<FacilityViewSub> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<FacilityViewSub> facilities) {
		this.facilities = facilities;
	}
	
	private boolean isMarkerAlreadyAdded(FacilityViewSub facility) {
		if (facilities.contains(facility))
			return true;
		else
			return false;
	}
	
	public boolean addMarker(FacilityViewSub facility) {
		if (isMarkerAlreadyAdded(facility)) {
			return true;
		}
		
		if (center == null) {
			center = new LatLng(facility.getLt(), facility.getLn());
		} else {
			if (averageCenter != null) {
				int l = facilities.size() + 1;
				Double lat = (this.center.getLat() * (l-1) + facility.getLt())/l;
				Double lng = (this.center.getLng() * (l-1) + facility.getLn())/l;
				center = new LatLng(lat, lng);
			}
		}
		
		facilities.add(facility);
		
		return true;
	}
}
