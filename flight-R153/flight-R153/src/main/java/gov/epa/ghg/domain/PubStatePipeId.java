package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by lee@saic June 2017
 */
@Embeddable
public class PubStatePipeId implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String state;
	private Long facilityId;
	private Long year;
	
	public PubStatePipeId() {
	}
	
	public PubStatePipeId(
			String state, Long facilityId, Long year) {
		this.state = state;
		this.facilityId = facilityId;
		this.year = year;
	}
	
	@Column(name = "STATE", nullable = false, length = 2)
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
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
