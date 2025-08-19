package gov.epa.ghg.dao;

import java.util.List;
import java.util.Map;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.dto.PipeHoverTip;
import gov.epa.ghg.enums.ReportingStatus;

public interface DimFacilityDaoInterface {
	
	public DimFacility findByFacilityIdAndReportingYear(Long id, int year);
	
	public DimFacility findDimFacilityAndHtmlByFacilityIdAndReportingYear(Long facilityId, int rYear);
	
	public FacilityHoverTip getFacilityHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs);
	
	public List<DimFacility> findById(Long id);
	
	public Map<String, String> getReportedSubparts(Long id, String ds);
	
	public List<String> getFacReportingYears(Long id);
	
	public DimFacilityPipe findPipeIdYear(Long id, int year);
	
	public PipeHoverTip getPipeHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs, String state);
}
