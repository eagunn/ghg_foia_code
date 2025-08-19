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

/**
 * Created by lee@saic June 2017
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_FACTS_SUBP_PIPE_EMISSION")
public class PubFactsSubpartPipe implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubFactsSubpartPipeId id;
	private DimFacilityPipe facility;
	private DimSubpart subpart;
	private DimGhg gas;
	private BigDecimal co2eEmission;
	private String state;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubFactsSubpartPipe() {
	}
	
	/**
	 * full constructor
	 */
	public PubFactsSubpartPipe(PubFactsSubpartPipeId id, DimFacilityPipe facility, DimSubpart subpart, DimGhg gas, Long year, BigDecimal co2eEmission, String state) {
		this.id = id;
		this.facility = facility;
		this.subpart = subpart;
		this.gas = gas;
		this.co2eEmission = co2eEmission;
		this.state = state;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "sectorId", column = @Column(name = "SECTOR_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "gasId", column = @Column(name = "GAS_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	
	public PubFactsSubpartPipeId getId() {
		return this.id;
	}
	
	public void setId(PubFactsSubpartPipeId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "FACILITY_ID", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "YEAR", nullable = false, insertable = false, updatable = false)
	})
	public DimFacilityPipe getFacility() {
		return this.facility;
	}
	
	public void setFacility(DimFacilityPipe facility) {
		this.facility = facility;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_PART_ID", nullable = false, insertable = false, updatable = false)
	public DimSubpart getSubpart() {
		return subpart;
	}
	
	public void setSubpart(DimSubpart subpart) {
		this.subpart = subpart;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GAS_ID", nullable = false, insertable = false, updatable = false)
	public DimGhg getGas() {
		return gas;
	}
	
	public void setGas(DimGhg gas) {
		this.gas = gas;
	}
	
	@Column(name = "CO2E_EMISSION")
	public BigDecimal getCo2eEmission() {
		return co2eEmission;
	}
	
	public void setCo2eEmission(BigDecimal co2eEmission) {
		this.co2eEmission = co2eEmission;
	}
	
	@Column(name = "STATE")
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
