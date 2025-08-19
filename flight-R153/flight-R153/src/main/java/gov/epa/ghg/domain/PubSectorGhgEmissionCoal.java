package gov.epa.ghg.domain;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "PUB_SECTOR_GHG_EMISSION_COAL")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class PubSectorGhgEmissionCoal {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubFactsSectorGhgEmissionId id;
	private DimFacility facility;
	private DimSector sector;
	private DimGhg gas;
	private DimSubSector subSector;
	private BigDecimal co2eEmission;
	private String fuelType;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubSectorGhgEmissionCoal() {
	}
	
	/**
	 * full constructor
	 */
	public PubSectorGhgEmissionCoal(PubFactsSectorGhgEmissionId id, DimFacility facility, DimSector sector, DimGhg gas, DimSubSector subSector, BigDecimal co2eEmission, String fuelType) {
		this.id = id;
		this.facility = facility;
		this.sector = sector;
		this.gas = gas;
		this.subSector = subSector;
		this.co2eEmission = co2eEmission;
		this.fuelType = fuelType;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "sectorId", column = @Column(name = "SECTOR_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "gasId", column = @Column(name = "GAS_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "subSectorId", column = @Column(name = "SUBSECTOR_ID", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	
	public PubFactsSectorGhgEmissionId getId() {
		return this.id;
	}
	
	public void setId(PubFactsSectorGhgEmissionId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "FACILITY_ID", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "YEAR", nullable = false, insertable = false, updatable = false)
	})
	public DimFacility getFacility() {
		return this.facility;
	}
	
	public void setFacility(DimFacility facility) {
		this.facility = facility;
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
	@JoinColumn(name = "GAS_ID", nullable = false, insertable = false, updatable = false)
	public DimGhg getGas() {
		return gas;
	}
	
	public void setGas(DimGhg gas) {
		this.gas = gas;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSECTOR_ID", nullable = false, insertable = false, updatable = false)
	public DimSubSector getSubSector() {
		return this.subSector;
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
	
	@Column(name = "FUEL_TYPE")
	public String getFuelType() {
		return fuelType;
	}
	
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
}
