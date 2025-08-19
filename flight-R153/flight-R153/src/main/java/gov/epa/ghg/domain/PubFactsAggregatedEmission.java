package gov.epa.ghg.domain;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_FACTS_AGGREGATED_EMISSION")
public class PubFactsAggregatedEmission implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private PubFactsAggregatedEmissionId id;
	private DimSector sector;
	private DimSubSector subSector;
	private BigDecimal co2eEmission;
	private String uuRandDExempt;
	private String notes;
	
	public PubFactsAggregatedEmission() {
		
	}
	
	public PubFactsAggregatedEmission(PubFactsAggregatedEmissionId id,
			DimSector sector, DimSubSector subSector,
			BigDecimal co2eEmission, String uuRandDExempt, String notes) {
		super();
		this.id = id;
		this.sector = sector;
		this.subSector = subSector;
		this.co2eEmission = co2eEmission;
		this.uuRandDExempt = uuRandDExempt;
		this.notes = notes;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "sectorId", column = @Column(name = "SECTOR_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "subSectorId", column = @Column(name = "SUBSECTOR_ID", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	
	public PubFactsAggregatedEmissionId getId() {
		return id;
	}
	
	public void setId(PubFactsAggregatedEmissionId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SECTOR_ID", nullable = false, insertable = false, updatable = false)
	public DimSector getSector() {
		return sector;
	}
	
	public void setSector(DimSector sector) {
		this.sector = sector;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSECTOR_ID", nullable = false, insertable = false, updatable = false)
	public DimSubSector getSubSector() {
		return subSector;
	}
	
	public void setSubSector(DimSubSector subSector) {
		this.subSector = subSector;
	}
	
	@Column(name = "CO2E_EMISSION")
	public BigDecimal getCo2eEmission() {
		return co2eEmission;
	}
	
	public void setCo2eEmission(BigDecimal co2eEmission) {
		this.co2eEmission = co2eEmission;
	}
	
	@Column(name = "UU_RD_EXEMPT", length = 1)
	public String getUuRandDExempt() {
		return uuRandDExempt;
	}
	
	public void setUuRandDExempt(String uuRandDExempt) {
		this.uuRandDExempt = uuRandDExempt;
	}
	
	@Column(name = "NOTES", length = 4000)
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
