package gov.epa.ghg.util;

import gov.epa.ghg.domain.FacilityViewSub;
import gov.epa.ghg.dto.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MarkerClusterer {

	List<Cluster> clusters = new ArrayList<Cluster>();
	
	public List<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	public double distanceBetweenPoints(LatLng p1, LatLng p2) {
		
		if (p1 == null || p2 == null) {
			return new Double(0);
		}
		
		Integer R = 6371;
		Double dLat = (p2.getLat() - p1.getLat()) * Math.PI/180;
		Double dLng = (p2.getLng() - p1.getLng()) * Math.PI/180;
		Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
					Math.cos(p1.getLat() * Math.PI/180) * Math.cos(p2.getLat() * Math.PI/180) *
					Math.sin(dLng/2) * Math.sin(dLng/2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;
	}
	
	public void addToClosestCluster(FacilityViewSub facility) {
		
		double distance = 500;
		Cluster clusterToAddTo = null;
		LatLng pos = new LatLng(facility.getLt(), facility.getLn());
		for (Cluster cluster : clusters) {
			LatLng center = cluster.getCenter();
			Double d = distanceBetweenPoints(center, pos);
			if (d < distance) {
				distance = d;
				clusterToAddTo = cluster;
			}
		}
		
		if (clusterToAddTo != null /*&& clusterToAddTo.isMarkerInClusterBounds*/) {
			clusterToAddTo.addMarker(facility);
		} else {
			Cluster cluster = new Cluster();
			cluster.addMarker(facility);
			clusters.add(cluster);
		}
	}
}
