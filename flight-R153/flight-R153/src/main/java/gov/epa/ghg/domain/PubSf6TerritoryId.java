package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by lee@saic May 2018
 */
@Embeddable
public class PubSf6TerritoryId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long facilityId;
	private Long recId;
	private Long year;
	private String state;
	
	public PubSf6TerritoryId() {
	}
	
	public PubSf6TerritoryId(
			Long facilityId, Long recId, Long year, String state) {
		this.facilityId = facilityId;
		this.recId = recId;
		this.year = year;
		this.state = state;
	}
	
	@Column(name = "PUB_ID", nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "REC_ID", nullable = false, precision = 22, scale = 0)
	public Long getRecId() {
		return recId;
	}
	
	public void setRecId(Long recId) {
		this.recId = recId;
	}
	
	@Column(name = "YEAR")
	public Long getYear() {
		return this.year;
	}
	
	public void setYear(Long year) {
		this.year = year;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
