package gov.epa.ghg.dto.view;

import java.math.BigDecimal;
import java.util.Map;

import gov.epa.ghg.enums.ReportingStatus;

public class FacilityExport implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long facilityId;
	private String facilityName;
	private Double latitude;
	private Double longitude;
	private String city;
	private String state;
	private String stateName;
	private String zip;
	private String countyFips;
	private String basinDetails;
	private String county;
	private String address1;
	private String address2;
	private BigDecimal totalCo2e;
	private String parentCompanies;
	private String subParts;
	private String sectors;
	private Long reportingYear;
	private Map<String,String> trendEmissions;
	ReportingStatus reportingStatus;
	
	public Long getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getCountyFips() {
		return countyFips;
	}
	public void setCountyFips(String countyFips) {
		this.countyFips = countyFips;
	}
	public String getBasinDetails() {
		return basinDetails;
	}
	
	public void setBasinDetails(String basinDetails) {
		this.basinDetails = basinDetails;
	}
	public String getCounty() {
		return county;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public BigDecimal getTotalCo2e() {
		return totalCo2e;
	}
	public void setTotalCo2e(BigDecimal totalCo2e) {
		this.totalCo2e = totalCo2e;
	}
	
	public String getParentCompanies() {
		return parentCompanies;
	}
	public void setParentCompanies(String parentCompanies) {
		this.parentCompanies = parentCompanies;
	}
	
	public String getSubParts() {
		return subParts;
	}
	public void setSubParts(String subParts) {
		this.subParts = subParts;
	}
	
	public String getSectors() {
		return sectors;
	}
	public void setSectors(String sectors) {
		this.sectors = sectors;
	}
	
	public Long getReportingYear() {
		return reportingYear;
	}
	public void setReportingYear(Long reportingYear) {
		this.reportingYear = reportingYear;
	}
	
	public Map<String, String> getTrendEmissions() {
		return trendEmissions;
	}
	public void setTrendEmissions(Map<String, String> trendEmissions) {
		this.trendEmissions = trendEmissions;
	}
	
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
}
