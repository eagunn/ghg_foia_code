package gov.epa.ghg.service;

import gov.epa.ghg.domain.*;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.dto.*;
import gov.epa.ghg.presentation.request.FlightRequest;

import java.util.List;

public interface FacilityViewInterface {

	public FacilityList getFacilityViewList(int page, String queryString, int year, String stateCode, String fipsCode, String msaCode, String basin, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, int is, String rs_string, String emissionsType, Long tribalLandId);
	public FacilityList getAutoCompleteFacilityList(String queryString, int year, String stateCode, String fipsCode, String msaCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, String rs_string, String emissionsType, Long tribalLandId);
	public List<FacilityViewSub> getFacilityGeoData(FlightRequest request);
	public String getCountyName(String fipsCode);
	public List<DimState> getStates();
	public List<DimCounty> getFacilityCounties(String stateCode, int year);	
	public List<Basin> getBasins();
	public int getStoppedReportingFacilitiesCount(int pageNumber, String q,
			int year, String stateCode, String string, String msaCode, String string2,
			GasFilter gases,
			SectorFilter sectors, QueryOptions qo, String ds, int sc,
			int sortOrder, int is, String emissionsType, Long tribalLandId);
	public List<DimMsa> getMsas(String stateCode);
	public int getFips(String state, String county);
	public int getTribalLandId(String tribalLandName);
	public List<LuTribalLands> getTribalLands();
}
