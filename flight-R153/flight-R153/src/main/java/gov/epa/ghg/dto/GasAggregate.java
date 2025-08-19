package gov.epa.ghg.dto;

/**
 * Facility View entity.
 */
public class GasAggregate implements java.io.Serializable {

	private Long co2Emission;
	private Long ch4Co2eEmission;
	private Long n2oCo2eEmission;
	private Long cf4Co2eEmission;
	private Long c2f6Co2eEmission;
	private Long sf6Co2eEmission;
	private Long chf3Co2eEmission;
	private String stateName;
	private String countyName;
	private String facilityName;

	//Constructors

	/** default constructor */
	public GasAggregate() {
		this.co2Emission = (long)0;
		this.ch4Co2eEmission = (long)0;
		this.n2oCo2eEmission = (long)0;
		this.cf4Co2eEmission = (long)0;
		this.c2f6Co2eEmission = (long)0;
		this.sf6Co2eEmission = (long)0;
		this.chf3Co2eEmission = (long)0;
		this.stateName = null;
		this.countyName = null;
		this.facilityName = null;
	}

	/** full constructor */
	public GasAggregate(
			//GasAggregateId id, Long facilityId, Long year, Long sectorId, 
			Long co2Emission, Long ch4Co2eEmission, Long n2oCo2eEmission, 
			Long cf4Co2eEmission, Long c2f6Co2eEmission, Long sf6Co2eEmission, 
			Long chf3Co2eEmission, String stateName, String countyName, String facilityName
	) 
	{
		//this.id = id;
		this.co2Emission = co2Emission;
		this.ch4Co2eEmission = ch4Co2eEmission;
		this.n2oCo2eEmission = n2oCo2eEmission;
		this.cf4Co2eEmission = cf4Co2eEmission;
		this.c2f6Co2eEmission = c2f6Co2eEmission;
		this.sf6Co2eEmission = sf6Co2eEmission;
		this.chf3Co2eEmission = chf3Co2eEmission;
		this.stateName = stateName;
		this.countyName = countyName;
		this.facilityName = facilityName;
	}

	// Property accessors
	public Long getCo2Emission() {
		return this.co2Emission;
	}

	public void setCo2Emission(Long co2Emission) {
		this.co2Emission = co2Emission==null?0:co2Emission;
	}
	
	public Long getCh4Co2eEmission() {
		return this.ch4Co2eEmission;
	}

	public void setCh4Co2eEmission(Long ch4Co2eEmission) {
		this.ch4Co2eEmission = ch4Co2eEmission==null?0:ch4Co2eEmission;
	}

	public Long getN2oCo2eEmission() {
		return this.n2oCo2eEmission;
	}

	public void setN2oCo2eEmission(Long n2oCo2eEmission) {
		this.n2oCo2eEmission = n2oCo2eEmission==null?0:n2oCo2eEmission;
	}
	
	public Long getCf4Co2eEmission() {
		return this.cf4Co2eEmission;
	}

	public void setCf4Co2eEmission(Long cf4Co2eEmission) {
		this.cf4Co2eEmission = cf4Co2eEmission==null?0:cf4Co2eEmission;
	}

	public Long getC2f6Co2eEmission() {
		return this.c2f6Co2eEmission;
	}

	public void setC2f6Co2eEmission(Long c2f6Co2eEmission) {
		this.c2f6Co2eEmission = c2f6Co2eEmission==null?0:c2f6Co2eEmission;
	}

	public Long getSf6Co2eEmission() {
		return this.sf6Co2eEmission;
	}

	public void setSf6Co2eEmission(Long sf6Co2eEmission) {
		this.sf6Co2eEmission = sf6Co2eEmission==null?0:sf6Co2eEmission;
	}
	
	public Long getChf3Co2eEmission() {
		return this.chf3Co2eEmission;
	}

	public void setChf3Co2eEmission(Long chf3Co2eEmission) {
		this.chf3Co2eEmission = chf3Co2eEmission==null?0:chf3Co2eEmission;
	}

	public String getStateName() {
		return this.stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountyName() {
		return this.countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	public String getFacilityName() {
		return this.facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
}