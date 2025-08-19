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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;

/**
 * Facility View entity.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_LDC_FACILITY_GEO")
public class PubLdcFacility implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubLdcFacilityId id;
	private DimFacility facility;
	// private LdcLayer layer;
	private Geometry geometry;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubLdcFacility() {
	}
	
	/**
	 * full constructor
	 */
	public PubLdcFacility(PubLdcFacilityId id, DimFacility facility) {
		this.id = id;
		// this.layer = layer;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "CCD_PUB_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "charId", column = @Column(name = "CHARID", nullable = false, length = 5)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "type", column = @Column(name = "TYPE", nullable = false, length = 10))
	})
	
	public PubLdcFacilityId getId() {
		return this.id;
	}
	
	public void setId(PubLdcFacilityId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CCD_PUB_ID", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "YEAR", nullable = false, insertable = false, updatable = false)
	})
	
	public DimFacility getFacility() {
		return this.facility;
	}
	
	public void setFacility(DimFacility facility) {
		this.facility = facility;
	}

	/*@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LDC_CHARID", nullable = false, insertable = false, updatable = false)
	public LdcLayer getLayer() {
		return layer;
	}

	public void setLayer(LdcLayer layer) {
		this.layer = layer;
	}*/
	
	@Column(name = "GEOMETRY", nullable = true)
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}
