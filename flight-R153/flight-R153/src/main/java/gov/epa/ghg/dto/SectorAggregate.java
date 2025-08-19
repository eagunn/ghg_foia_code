package gov.epa.ghg.dto;

import java.math.BigDecimal;

import static gov.epa.ghg.util.ServiceUtils.addBigDecimalNullSafe;

/**
 * POJO for storing the sum of co2 equivalent facility emissions by sector
 */
public class SectorAggregate implements java.io.Serializable {


	public BigDecimal sumAllSectors() {

		return addBigDecimalNullSafe(
				powerplantEmission,
				landfillEmission,
				metalEmission,
				mineralEmission,
				refineryEmission,
				pulpAndPaperEmission,
				chemicalEmission,
				petroleumAndNaturalGasEmission,
				otherEmission)
				;

	}

	// Fields
	private BigDecimal powerplantEmission;
	private BigDecimal landfillEmission;
	private BigDecimal metalEmission;
	private BigDecimal mineralEmission;
	private BigDecimal refineryEmission;
	private BigDecimal pulpAndPaperEmission;
	private BigDecimal chemicalEmission;
	private BigDecimal petroleumAndNaturalGasEmission;
	private BigDecimal otherEmission;
	private Long powerplantCount;
	private Long landfillCount;
	private Long metalCount;
	private Long mineralCount;
	private Long refineryCount;
	private Long pulpAndPaperCount;
	private Long chemicalCount;
	private Long petroleumAndNaturalGasCount;
	private Long otherCount;
	private String stateName;
	private String countyName;
	private Long facilityId;	
	private String facilityName;

	public SectorAggregate() {
		this.powerplantEmission = BigDecimal.ZERO;
		this.landfillEmission = BigDecimal.ZERO;
		this.metalEmission = BigDecimal.ZERO;
		this.mineralEmission = BigDecimal.ZERO;
		this.refineryEmission = BigDecimal.ZERO;
		this.pulpAndPaperEmission = BigDecimal.ZERO;
		this.chemicalEmission = BigDecimal.ZERO;
		this.petroleumAndNaturalGasEmission = BigDecimal.ZERO;
		this.otherEmission = BigDecimal.ZERO;
		this.powerplantCount = 0L;
		this.landfillCount = 0L;
		this.metalCount = 0L;
		this.mineralCount = 0L;
		this.refineryCount = 0L;
		this.pulpAndPaperCount = 0L;
		this.chemicalCount = 0L;
		this.petroleumAndNaturalGasCount = 0L;
		this.otherCount = 0L;
		this.stateName = null;
		this.countyName = null;
		this.facilityName = null;
	}

	public BigDecimal getPowerplantEmission() {
		return this.powerplantEmission;
	}

	public void setPowerplantEmission(BigDecimal powerplantEmission) {
		this.powerplantEmission = powerplantEmission==null?BigDecimal.ZERO:powerplantEmission;
	}
	
	public BigDecimal getLandfillEmission() {
		return this.landfillEmission;
	}

	public void setLandfillEmission(BigDecimal landfillEmission) {
		this.landfillEmission = landfillEmission==null?BigDecimal.ZERO:landfillEmission;
	}

	public BigDecimal getMetalEmission() {
		return this.metalEmission;
	}

	public void setMetalEmission(BigDecimal metalEmission) {
		this.metalEmission = metalEmission==null?BigDecimal.ZERO:metalEmission;
	}
	
	public BigDecimal getMineralEmission() {
		return this.mineralEmission;
	}

	public void setMineralEmission(BigDecimal mineralEmission) {
		this.mineralEmission = mineralEmission==null?BigDecimal.ZERO:mineralEmission;
	}

	public BigDecimal getRefineryEmission() {
		return this.refineryEmission;
	}

	public void setRefineryEmission(BigDecimal refineryEmission) {
		this.refineryEmission = refineryEmission==null?BigDecimal.ZERO:refineryEmission;
	}

	public BigDecimal getPulpAndPaperEmission() {
		return this.pulpAndPaperEmission;
	}

	public void setPulpAndPaperEmission(BigDecimal pulpAndPaperEmission) {
		this.pulpAndPaperEmission = pulpAndPaperEmission==null?BigDecimal.ZERO:pulpAndPaperEmission;
	}
	
	public BigDecimal getChemicalEmission() {
		return this.chemicalEmission;
	}

	public void setChemicalEmission(BigDecimal chemicalEmission) {
		this.chemicalEmission = chemicalEmission==null?BigDecimal.ZERO:chemicalEmission;
	}

	public BigDecimal getOtherEmission(){
		return this.otherEmission;
	}

	public void setOtherEmission(BigDecimal otherEmission) {
		this.otherEmission = otherEmission==null?BigDecimal.ZERO:otherEmission;
	}

	public BigDecimal getPetroleumAndNaturalGasEmission(){
		return this.petroleumAndNaturalGasEmission;
	}

	public void setPetroleumAndNaturalGasEmission(BigDecimal petroleumAndNaturalGasEmission) {
		this.petroleumAndNaturalGasEmission = petroleumAndNaturalGasEmission==null?BigDecimal.ZERO:petroleumAndNaturalGasEmission;
	}
	
	public Long getPowerplantCount() {
		return this.powerplantCount;
	}

	public void setPowerplantCount(Long powerplantCount) {
		this.powerplantCount = powerplantCount==null?0:powerplantCount;
	}
	
	public Long getLandfillCount() {
		return this.landfillCount;
	}

	public void setLandfillCount(Long landfillCount) {
		this.landfillCount = landfillCount==null?0:landfillCount;
	}

	public Long getMetalCount() {
		return this.metalCount;
	}

	public void setMetalCount(Long metalCount) {
		this.metalCount = metalCount==null?0:metalCount;
	}
	
	public Long getMineralCount() {
		return this.mineralCount;
	}

	public void setMineralCount(Long mineralCount) {
		this.mineralCount = mineralCount==null?0:mineralCount;
	}

	public Long getRefineryCount() {
		return this.refineryCount;
	}

	public void setRefineryCount(Long refineryCount) {
		this.refineryCount = refineryCount==null?0:refineryCount;
	}

	public Long getPulpAndPaperCount() {
		return this.pulpAndPaperCount;
	}

	public void setPulpAndPaperCount(Long pulpAndPaperCount) {
		this.pulpAndPaperCount = pulpAndPaperCount==null?0:pulpAndPaperCount;
	}
	
	public Long getChemicalCount() {
		return this.chemicalCount;
	}

	public void setChemicalCount(Long chemicalCount) {
		this.chemicalCount = chemicalCount==null?0:chemicalCount;
	}
	
	public Long getOtherCount() {
		return otherCount;
	}

	public void setOtherCount(
			Long otherCount) {
		this.otherCount = otherCount;
	}

	public Long getPetroleumAndNaturalGasCount() {
		return petroleumAndNaturalGasCount;
	}

	public void setPetroleumAndNaturalGasCount(Long petroleumAndNaturalGasCount) {
		this.petroleumAndNaturalGasCount = petroleumAndNaturalGasCount;
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

	public Long getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return this.facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	
	public void zero() {
		
		BigDecimal[] numbers = { this.powerplantEmission,
		this.landfillEmission, 
		this.metalEmission ,
		this.mineralEmission ,
		this.refineryEmission ,
		this.pulpAndPaperEmission,
		this.chemicalEmission ,
		this.petroleumAndNaturalGasEmission, 
		this.otherEmission }; 
		Long[] counts = { 
		this.powerplantCount,
		this.landfillCount ,
		this.metalCount ,
		this.mineralCount,
		this.refineryCount,
		this.pulpAndPaperCount,
		this.chemicalCount ,
		this.petroleumAndNaturalGasCount } ;
		
		for (BigDecimal number : numbers)  {
			if (number == null) {
				number = BigDecimal.ZERO; 
			}
		}
		for (Long count : counts) {
			if (count == null) {
				count = 0L; 
			}
		}
		
	}
	
	public void merge(SectorAggregate sa2) {
		
		sa2.zero();
		
			this.powerplantEmission = this.powerplantEmission.add(sa2.getPowerplantEmission()); 
			this.metalEmission = this.metalEmission.add(sa2.getMetalEmission()); 
			this.landfillEmission = this.landfillEmission.add(sa2.getLandfillEmission()); 
			this.mineralEmission = this.mineralEmission.add(sa2.getMineralEmission());
			this.refineryEmission = this.refineryEmission.add(sa2.getRefineryEmission()); 
			this.pulpAndPaperEmission = this.pulpAndPaperEmission.add(sa2.getPulpAndPaperEmission()); 
			this.chemicalEmission = this.chemicalEmission.add(sa2.getChemicalEmission()); 
			this.petroleumAndNaturalGasEmission = this.petroleumAndNaturalGasEmission.add(sa2.getPetroleumAndNaturalGasEmission()); 
			this.otherEmission = this.otherEmission.add(sa2.getOtherEmission()); 
			this.powerplantCount = this.powerplantCount + sa2.getPowerplantCount(); 
			this.landfillCount = this.landfillCount + sa2.getLandfillCount(); 
			this.metalCount += sa2.getMetalCount();
			this.mineralCount += sa2.getMineralCount();
			this.refineryCount += sa2.getRefineryCount();
			this.pulpAndPaperCount += sa2.getPulpAndPaperCount();
			this.chemicalCount += sa2.getChemicalCount();
			this.petroleumAndNaturalGasCount += sa2.getPetroleumAndNaturalGasCount();
			this.otherCount += sa2.getOtherCount(); 
		}
}