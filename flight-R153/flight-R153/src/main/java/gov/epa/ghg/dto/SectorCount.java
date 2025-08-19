package gov.epa.ghg.dto;

/**
 * POJO for storing a count of facilities by sector
 */
public class SectorCount implements java.io.Serializable {

	// Fields
	private Long powerplantCount;
	private Long landfillCount;
	private Long metalCount;
	private Long mineralCount;
	private Long refineryCount;
	private Long pulpAndPaperCount;
	private Long chemicalCount;
	private Long otherGovernmentAndCommercialCount;
	private Long otherIndustrialCount;	

	//Constructors

	/** default constructor */
	public SectorCount() {
		this.powerplantCount = (long)0;
		this.landfillCount = (long)0;
		this.metalCount = (long)0;
		this.mineralCount = (long)0;
		this.refineryCount = (long)0;
		this.pulpAndPaperCount = (long)0;
		this.chemicalCount = (long)0;
		this.otherGovernmentAndCommercialCount = (long)0;
		this.otherIndustrialCount = (long)0;
	}

	// Property accessors

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
	
	public Long getOtherGovernmentAndCommercialCount() {
		return otherGovernmentAndCommercialCount;
	}

	public void setOtherGovernmentAndCommercialCount(
			Long otherGovernmentAndCommercialCount) {
		this.otherGovernmentAndCommercialCount = otherGovernmentAndCommercialCount;
	}

	public Long getOtherIndustrialCount() {
		return otherIndustrialCount;
	}

	public void setOtherIndustrialCount(Long otherIndustrialCount) {
		this.otherIndustrialCount = otherIndustrialCount;
	}
}