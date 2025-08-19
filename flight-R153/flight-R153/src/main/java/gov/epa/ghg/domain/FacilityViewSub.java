package gov.epa.ghg.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;

import gov.epa.ghg.dto.ServiceArea;
import gov.epa.ghg.enums.ReportingStatus;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FacilityViewSub implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Double latitude;
	private Double longitude;
	private ServiceArea sa;
	private ReportingStatus reportingStatus;
	// added  for map overlays
	private BigDecimal emissions;
	private String state;
	
	// public FacilityViewSub() {
	//}
	
	public FacilityViewSub() {
	
	}
	
	public FacilityViewSub(Long id, Double lat, Double lon, BigDecimal emissions) {
		
		this.id = id;
		this.latitude = lat;
		this.longitude = lon;
		this.emissions = emissions;
		
	}
	
	public FacilityViewSub(Long id, Double latitude, Double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public FacilityViewSub(Long id, Double latitude, Double longitude, ServiceArea sa) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sa = sa;
	}
	
	public FacilityViewSub(Long id, Double latitude, Double longitude, ReportingStatus reportingStatus) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.reportingStatus = reportingStatus;
	}
	
	public FacilityViewSub(Long id, ReportingStatus reportingStatus, String state) {
		this.id = id;
		this.reportingStatus = reportingStatus;
		this.state = state;
	}
	
	public FacilityViewSub(Long id, ReportingStatus reportingStatus) {
		this.id = id;
		this.reportingStatus = reportingStatus;
	}
	
	// Property accessors
	
	@Id
	@Column(name = "FACILITY_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "LATITUDE", nullable = false, precision = 20, scale = 15)
	public Double getLt() {
		return this.latitude;
	}
	
	public void setLt(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "LONGITUDE", nullable = false, precision = 20, scale = 15)
	public Double getLn() {
		return this.longitude;
	}
	
	public void setLn(Double longitude) {
		this.longitude = longitude;
	}
	
	public ServiceArea getSa() {
		return sa;
	}
	
	public void setSa(ServiceArea sa) {
		this.sa = sa;
	}
	
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
	
	public BigDecimal getEmissions() {
		return emissions;
	}
	
	public void setEmissions(BigDecimal emissions) {
		this.emissions = emissions;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
