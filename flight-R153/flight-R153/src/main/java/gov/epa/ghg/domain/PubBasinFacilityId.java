package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PubBasinFacilityId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String basinCode;
	private Long facilityId;
	private Long year;
	
	public PubBasinFacilityId() {
	}
	
	public PubBasinFacilityId(String basinCode, Long facilityId, Long year) {
		this.basinCode = basinCode;
		this.facilityId = facilityId;
		this.year = year;
	}
	
	@Column(name = "BASIN_CODE", nullable = false, length = 10)
	public String getBasinCode() {
		return basinCode;
	}
	
	public void setBasinCode(String basinCode) {
		this.basinCode = basinCode;
	}
	
	@Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "YEAR")
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
}
