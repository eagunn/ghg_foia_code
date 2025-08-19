package gov.epa.ghg.dto;

import java.math.BigDecimal;

public class FacilityEmission {
	
	Integer id;
	BigDecimal emission;
	Double latitude;
	Double longitude;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getEmission() {
		return emission;
	}
	public void setEmission(BigDecimal emission) {
		this.emission = emission;
	}
}
