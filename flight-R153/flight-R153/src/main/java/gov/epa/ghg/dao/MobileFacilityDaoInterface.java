package gov.epa.ghg.dao;

import java.util.List;

import org.locationtech.jts.geom.Geometry;

import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;

public interface MobileFacilityDaoInterface {
	
	public List<Facility> getEmitterList(String q, int year, String state, String countyFips, String lowE, String highE,
			int page, GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String rs);
	
	public List<Facility> getEmitterListAround(String q, int year, String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, LatLng center, double radius, String rs);
	
	public List<Facility> getEmitterListWithin(String q, int year, String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, LatLng location, Geometry bounds, String rs);
}
