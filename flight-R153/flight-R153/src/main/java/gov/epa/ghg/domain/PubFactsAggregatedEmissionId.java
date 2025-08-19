package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PubFactsAggregatedEmissionId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long year;
	private Long sectorId;
	private Long subSectorId;
	
	public PubFactsAggregatedEmissionId() {
		
	}
	
	public PubFactsAggregatedEmissionId(Long year, Long sectorId,
			Long subSectorId) {
		super();
		this.year = year;
		this.sectorId = sectorId;
		this.subSectorId = subSectorId;
	}
	
	@Column(name = "YEAR", nullable = false)
	public Long getYear() {
		return year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
	
	@Column(name = "SECTOR_ID", nullable = false, precision = 22, scale = 0)
	public Long getSectorId() {
		return sectorId;
	}
	
	public void setSectorId(Long sectorId) {
		this.sectorId = sectorId;
	}
	
	@Column(name = "SUBSECTOR_ID", nullable = false, precision = 22, scale = 0)
	public Long getSubSectorId() {
		return subSectorId;
	}
	
	public void setSubSectorId(Long subSectorId) {
		this.subSectorId = subSectorId;
	}
	
}
