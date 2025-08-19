package gov.epa.ghg.domain;

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
 * Facility View entity.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_BASIN_FACILITY")
public class PubBasinFacility implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubBasinFacilityId id;
	private DimFacility facility;
	private BasinLayer layer;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubBasinFacility() {
	}
	
	/**
	 * full constructor
	 */
	public PubBasinFacility(PubBasinFacilityId id, DimFacility facility, BasinLayer layer) {
		this.id = id;
		this.layer = layer;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "basinCode", column = @Column(name = "BASIN_CODE", nullable = false, length = 10)),
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	
	public PubBasinFacilityId getId() {
		return this.id;
	}
	
	public void setId(PubBasinFacilityId id) {
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BASIN_CODE", nullable = false, insertable = false, updatable = false)
	public BasinLayer getLayer() {
		return layer;
	}
	
	public void setLayer(BasinLayer layer) {
		this.layer = layer;
	}
}
