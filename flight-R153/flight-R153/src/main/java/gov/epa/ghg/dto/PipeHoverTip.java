package gov.epa.ghg.dto;

import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.enums.ReportingStatus;

import java.util.List;

public class PipeHoverTip {
	
	DimFacilityPipe facility;
	List<GasQuantity> emissions;
	ReportingStatus reportingStatus;

	public DimFacilityPipe getFacility() {
		return facility;
	}
	public void setFacility(DimFacilityPipe facility) {
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
