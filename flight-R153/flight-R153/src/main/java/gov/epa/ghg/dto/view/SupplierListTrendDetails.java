package gov.epa.ghg.dto.view;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.enums.ReportingStatus;

import java.math.BigDecimal;
import java.util.Map;

public class SupplierListTrendDetails implements SupplierListDetailsObject {


	public void populate(int year, BigDecimal quantity, DimFacility facility, ReportingStatus rs) {
		if (facility != null) {
			this.setFacility(facility);
			this.setReportingStatus(rs);
		}
		this.setEmissionForYear(year, quantity);
	}

	DimFacility facility;
	Map<Integer, BigDecimal> emissionsMap = new java.util.HashMap<Integer, BigDecimal>();
	ReportingStatus reportingStatus;
	
	public DimFacility getFacility() {return facility;}
	public void setFacility(DimFacility facility) {this.facility = facility; }
	public String getFacilityName() {
		return facility.getFacilityName(); 
	}
	public String getCity() {
		return facility.getCity();
	}
	public String getState() {
		return facility.getState(); 
	}
	public Map<Integer, BigDecimal> getEmissionsMap() {
		return emissionsMap; 
	}
	public BigDecimal getEmissionsForYear(Long year) {
		return emissionsMap.get(year.intValue());
	}
	public BigDecimal getEmissionsForYear(Integer year) {
		return emissionsMap.get(year); 
	}
	
	public void setEmissionForYear(Integer year, BigDecimal emissions) {
		
		BigDecimal val = emissionsMap.get(year);
		
		if (val != null && emissions != null) {
			val.add(emissions); 
		}
		
		else {
			val = emissions;  
		}
		
		if (val != null) {
			emissionsMap.put(year, val); 
		} 
	}
	
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}	
	
}
