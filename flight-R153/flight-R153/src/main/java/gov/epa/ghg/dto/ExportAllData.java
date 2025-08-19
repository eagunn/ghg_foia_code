package gov.epa.ghg.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExportAllData {
	
	private Map<Long, BigDecimal> em = new HashMap<>();
	private Map<Long, Long> repYears = new HashMap<>();
	
	public Map<Long, BigDecimal> getEm() {
		return em;
	}
	public void setEm(Map<Long, BigDecimal> em) {
		this.em = em;
	}
	public Map<Long, Long> getRepYears() {
		return repYears;
	}
	public void setRepYears(Map<Long, Long> repYears) {
		this.repYears = repYears;
	}
}
