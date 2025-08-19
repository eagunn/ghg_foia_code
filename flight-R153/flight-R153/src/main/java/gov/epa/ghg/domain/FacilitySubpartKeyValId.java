package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Facility View entity.
 */
@Embeddable
public class FacilitySubpartKeyValId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long facilityId;
	private Long subpartId;
	private Long keyId;
	private Long year;
	private String notes;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public FacilitySubpartKeyValId() {
	}
	
	/**
	 * full constructor
	 */
	public FacilitySubpartKeyValId(
			Long facilityId, Long subpartId, Long keyId, Long year) {
		this.facilityId = facilityId;
		this.subpartId = subpartId;
		this.keyId = keyId;
		this.year = year;
		this.notes = notes;
	}
	
	// Property accessors
	
	@Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "SUBPART_ID", nullable = false, precision = 22, scale = 0)
	public Long getSubpartId() {
		return this.subpartId;
	}
	
	public void setSubpartId(Long subpartId) {
		this.subpartId = subpartId;
	}
	
	@Column(name = "KEY_ID", nullable = false, precision = 22, scale = 0)
	public Long getKeyId() {
		return this.keyId;
	}
	
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	
	@Column(name = "YEAR", nullable = false, precision = 4, scale = 0)
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
	
	@Column(name = "NOTES")
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
