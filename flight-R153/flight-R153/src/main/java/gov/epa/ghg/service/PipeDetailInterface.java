package gov.epa.ghg.service;

import gov.epa.ghg.dto.PipeDetail;
import gov.epa.ghg.dto.PipeHoverTip;
import gov.epa.ghg.enums.ReportingStatus;

public interface PipeDetailInterface {
	
	public PipeDetail getPipeDetails(Long id, int year, String ds, String emissionsType);
	
	public PipeHoverTip getPipeHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs, String state);
}
