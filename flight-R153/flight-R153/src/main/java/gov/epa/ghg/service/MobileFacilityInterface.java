package gov.epa.ghg.service;

import java.util.List;

import org.locationtech.jts.geom.Geometry;

import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;

public interface MobileFacilityInterface {

	public List<Facility> getFacilityList(int page, String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, String rs);
	public List<Facility> getFacilityListAround(String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String sectorType, int sc, LatLng center, double radius, String rs);
	public List<Facility> getFacilityListWithin(String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String sectorType, int sc, LatLng location, Geometry bounds, String rs);
}
