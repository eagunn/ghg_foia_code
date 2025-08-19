package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Facility View entity.
 */
@Embeddable
public class PubFactsSubpartGhgEmissionId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long facilityId;
	private Long subpartId;
	private Long gasId;
	private Long year;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubFactsSubpartGhgEmissionId() {
	}
	
	/**
	 * full constructor
	 */
	public PubFactsSubpartGhgEmissionId(
			Long facilityId, Long subpartId, Long gasId, Long year) {
		this.facilityId = facilityId;
		this.subpartId = subpartId;
		this.gasId = gasId;
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
	
	@Column(name = "SUB_PART_ID", nullable = false, precision = 22, scale = 0)
	public Long getSubpartId() {
		return subpartId;
	}
	
	public void setSubpartId(Long subpartId) {
		this.subpartId = subpartId;
	}
	
	@Column(name = "GAS_ID", nullable = false, precision = 22, scale = 0)
	public Long getGasId() {
		return gasId;
	}
	
	public void setGasId(Long gasId) {
		this.gasId = gasId;
	}
	
	@Column(name = "YEAR")
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
}
