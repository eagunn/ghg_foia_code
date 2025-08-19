package gov.epa.ghg.dto;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.enums.ReportingStatus;

import java.util.List;

public class FacilityHoverTip {
	
	DimFacility facility;
	List<GasQuantity> emissions;
	ReportingStatus reportingStatus;

	public DimFacility getFacility() {
		return facility;
	}
	public void setFacility(DimFacility facility) {
		this.facility = facility;
	}
	public List<GasQuantity> getEmissions() {
		return emissions;
	}
	public void setEmissions(List<GasQuantity> emissions) {
		this.emissions = emissions;
	}
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
}
