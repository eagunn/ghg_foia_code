package gov.epa.ghg.dto;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.dto.view.SupplierListDetailsObject;
import gov.epa.ghg.enums.ReportingStatus;

import java.math.BigDecimal;

public class SupplierListDetails implements SupplierListDetailsObject {
	
	DimFacility facility;
	String productsSupplied;
	BigDecimal totalCo2e;
	ReportingStatus reportingStatus;

	public DimFacility getFacility() {
		return facility;
	}
	
	public void setFacility(DimFacility facility) {
		this.facility = facility;
	}

	public String getProductsSupplied() {
		return productsSupplied;
	}

	public void setProductsSupplied(String productsSupplied) {
		this.productsSupplied = productsSupplied;
	}

	public BigDecimal getTotalCo2e() {
		return totalCo2e;
	}

	public void setTotalCo2e(BigDecimal totalCo2e) {
		this.totalCo2e = totalCo2e;
	}

	public void populate(int year, BigDecimal quantity, DimFacility facility, ReportingStatus rs)  {
		//new object
		if (facility != null) {
			this.setFacility(facility);
			this.setTotalCo2e(quantity);
			this.setReportingStatus(rs);
		}
		//accumulate
		else {
			this.setTotalCo2e(this.getTotalCo2e().add(quantity)) ;
		}
	}

	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}

	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
}
