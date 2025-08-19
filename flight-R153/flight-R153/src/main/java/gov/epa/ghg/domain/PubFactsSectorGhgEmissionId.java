package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Facility View entity.
 */
@Embeddable
public class PubFactsSectorGhgEmissionId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long facilityId;
	private Long sectorId;
	private Long gasId;
	private Long subSectorId;
	private Long year;
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubFactsSectorGhgEmissionId() {
	}
	
	/**
	 * full constructor
	 */
	public PubFactsSectorGhgEmissionId(
			Long facilityId, Long sectorId, Long gasId, Long subSectorId, Long year) {
		this.facilityId = facilityId;
		this.sectorId = sectorId;
		this.gasId = gasId;
		this.subSectorId = subSectorId;
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
	
	@Column(name = "SECTOR_ID", nullable = false, precision = 22, scale = 0)
	public Long getSectorId() {
		return sectorId;
	}
	
	public void setSectorId(Long sectorId) {
		this.sectorId = sectorId;
	}
	
	@Column(name = "GAS_ID", nullable = false, precision = 22, scale = 0)
	public Long getGasId() {
		return gasId;
	}
	
	public void setGasId(Long gasId) {
		this.gasId = gasId;
	}
	
	@Column(name = "SUBSECTOR_ID", nullable = false, precision = 22, scale = 0)
	public Long getSubSectorId() {
		return this.subSectorId;
	}
	
	public void setSubSectorId(Long subSectorId) {
		this.subSectorId = subSectorId;
	}
	
	@Column(name = "YEAR")
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
}
