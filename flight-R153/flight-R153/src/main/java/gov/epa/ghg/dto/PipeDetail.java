package gov.epa.ghg.dto;

import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.FacilitySubpartKeyVal;

import java.util.List;
import java.util.Map;

public class PipeDetail {
	
	DimFacilityPipe facility;
	List<String> parentCompanies;
	Long totalEmissions;
	List<GasQuantity> subpartEmissions;
	List<GasQuantity> gasEmissions;
	Map<String, List<FacilitySubpartKeyVal>> subpartDetails;
	List<String> facReportingYears;
	boolean hasTrend = false;
	boolean hasBT = false;

	public DimFacilityPipe getFacility() {
		return facility;
	}

	public void setFacility(DimFacilityPipe facility) {
		this.facility = facility;
	}

	public List<String> getParentCompanies() {
		return parentCompanies;
	}

	public void setParentCompanies(List<String> parentCompanies) {
		this.parentCompanies = parentCompanies;
	}

	public Long getTotalEmissions() {
		return totalEmissions;
	}

	public void setTotalEmissions(Long totalEmissions) {
		this.totalEmissions = totalEmissions;
	}

	public List<GasQuantity> getSubpartEmissions() {
		return subpartEmissions;
	}

	public void setSubpartEmissions(List<GasQuantity> subpartEmissions) {
		this.subpartEmissions = subpartEmissions;
	}

	public List<GasQuantity> getGasEmissions() {
		return gasEmissions;
	}

	public void setGasEmissions(List<GasQuantity> gasEmissions) {
		this.gasEmissions = gasEmissions;
	}

	public Map<String, List<FacilitySubpartKeyVal>> getSubpartDetails() {
		return subpartDetails;
	}

	public void setSubpartDetails(Map<String, List<FacilitySubpartKeyVal>> subpartDetails) {
		this.subpartDetails = subpartDetails;
	}

	public List<String> getFacReportingYears() {
		return facReportingYears;
	}

	public void setFacReportingYears(List<String> facReportingYears) {
		this.facReportingYears = facReportingYears;
	}

	public boolean isHasTrend() {
		return hasTrend;
	}

	public void setHasTrend(boolean hasTrend) {
		this.hasTrend = hasTrend;
	}

	public boolean isHasBT() {
		return hasBT;
	}

	public void setHasBT(boolean hasBT) {
		this.hasBT = hasBT;
	}
}
