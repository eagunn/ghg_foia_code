package gov.epa.ghg.dao;

import java.util.List;

import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityExport;
import gov.epa.ghg.dto.view.FacilityList;

public interface FacilityViewDaoInterface {
	
	FacilityList getEmitterList(String q, int year, String state, String countyFips, String msaCode, String lowE, String highE,
			int page, GasFilter gases, SectorFilter sectors, QueryOptions qo, int sortOrder, String sectorType, String rs, String emissionsType, Long tribalLandId);
	
	List<FacilityExport> getEmitters(String q, int year, String state, String countyFips, String msaCode, Long tribalLandId, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String ds, String rs, String emissionsType,
			int sc, int is, String basin);
	
	FacilityList getAutoCompleteSupplierList(String q, QueryOptions qo, int year, int sc, int sortOrder, String rs);
	
	List<DimCounty> getEmitterFacilityCounties(String q, int year, String state, String countyFips, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String rs);
	
	SectorAggregate getEmitterSectorAggregate(String q, int year, String state, String countyFips, String msaCode, String basin, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String sectorType, String rs, String emissionsType, Long tribalLandId);
	
	List<DimCounty> getFacilityCounties(String state, int year);
	
	FacilityList getAutoCompleteEmitterList(String q, int year, String state,
			String countyFips, String msaCode, String lowE, String highE, GasFilter gases,
			SectorFilter sectors, QueryOptions qo, int sortOrder,
			String rs, String emissionsType, Long tribalLandId);
	
	FacilityList getCO2InjectionList(String q, int year, String state,
			String countyFips, String msaCode, String lowE, String highE, int page,
			GasFilter gases, QueryOptions qo, int sortOrder, int is,
			String String);
	
	FacilityList getOnShoreProductionList(String q, int year, String basin,
			String lowE, String highE, int page, GasFilter gases,
			SectorFilter sectors, QueryOptions qo, int sortOrder,
			String String);
	
	List<DimMsa> getMsas(String stateCode);
	
}
