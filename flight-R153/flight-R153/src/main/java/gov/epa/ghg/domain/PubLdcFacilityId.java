package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PubLdcFacilityId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long facilityId;
	private String charId;
	private Long year;
	private String type;
	private String state;
	
	public PubLdcFacilityId() {
	}
	
	public PubLdcFacilityId(
			Long facilityId, String charId, Long year, String type) {
		this.facilityId = facilityId;
		this.charId = charId;
		this.year = year;
		this.type = type;
	}
	
	@Column(name = "CCD_PUB_ID", nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "CHARID", nullable = false, length = 5)
	public String getCharId() {
		return charId;
	}
	
	public void setCharId(String charId) {
		this.charId = charId;
	}
	
	@Column(name = "YEAR")
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
	
	@Column(name = "TYPE", nullable = false, length = 5)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
