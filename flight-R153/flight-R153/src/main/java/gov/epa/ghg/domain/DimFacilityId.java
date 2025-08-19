package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Facility View entity.
 */
@Embeddable
public class DimFacilityId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long facilityId;
	private Long year;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimFacilityId() {
	}
	
	/**
	 * full constructor
	 */
	public DimFacilityId(
			Long facilityId, Long year) {
		this.facilityId = facilityId;
		this.year = year;
	}
	
	// Property accessors
	
	@Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "YEAR", nullable = false, precision = 4, scale = 0)
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
}
