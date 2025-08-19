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
 * Created by lee@saic May 2018
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_SERV_TERRITORIES_GEO")
public class PubSf6Territory implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubSf6TerritoryId id;
	private DimFacility facility;
	private Geometry geometry;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubSf6Territory() {
	}
	
	/**
	 * full constructor
	 */
	public PubSf6Territory(PubSf6TerritoryId id, DimFacility facility) {
		this.id = id;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "PUB_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "state", column = @Column(name = "STATE", nullable = false, length = 2))
	})
	
	public PubSf6TerritoryId getId() {
		return this.id;
	}
	
	public void setId(PubSf6TerritoryId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PUB_ID", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "YEAR", nullable = false, insertable = false, updatable = false)
	})
	
	public DimFacility getFacility() {
		return this.facility;
	}
	
	public void setFacility(DimFacility facility) {
		this.facility = facility;
	}
	
	@Column(name = "GEOMETRY", nullable = true)
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}
