package gov.epa.ghg.service;

import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.enums.ReportingStatus;

import java.io.UnsupportedEncodingException;
import net.sf.json.JSONObject;

public interface FacilityDetailInterface {

	public FacilityDetail getFacilityDetails(Long id, int year, String ds, String emissionsType);
	public FacilityDetail getLatestFacilityDetails(Long facilityId, String ds, String emissionsType);
	public FacilityDetail getLatestFacilityDetails2(Long facilityId, String ds, String emissionsType);

	public FacilityHoverTip getFacilityHoverTip(Long id, int year, String ds, String emissionsType, ReportingStatus rs);

	public String getFacilityName(Long id, int year) throws UnsupportedEncodingException;
	public JSONObject getFacilityTrend(Long id, String ds, String yr, String emissionsType);
	/**Taken out for now while EPA thinks it over***/
	//PUB-136: links to the other data sources' facility pages
	//public List<String> checkOtherDataSources(Long id, String ds, Long subSectorId, int year, String emissionsType);
}
