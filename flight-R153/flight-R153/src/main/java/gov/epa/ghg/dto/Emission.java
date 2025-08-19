package gov.epa.ghg.dto;

import java.math.BigDecimal;

public class Emission {
	
	String key;
	BigDecimal emission;

	public Emission(String key, BigDecimal emission) {
		this.key = key;
		this.emission = emission;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public BigDecimal getEmission() {
		return emission;
	}
	public void setEmission(BigDecimal emission) {
		this.emission = emission;
	}
}
