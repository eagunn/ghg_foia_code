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
 * Created by lee@saic June 2017
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_FACILITY_PIPE_STATE_MV")
public class PubStatePipe implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private PubStatePipeId id;
	private DimFacilityPipe facility;
	private DimStateGeo geo;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public PubStatePipe() {
	}
	
	/**
	 * full constructor
	 */
	public PubStatePipe(PubStatePipeId id, DimFacilityPipe facility, DimStateGeo geo) {
		this.id = id;
		this.geo = geo;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "state", column = @Column(name = "STATE", nullable = false, length = 2)),
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	
	public PubStatePipeId getId() {
		return this.id;
	}
	
	public void setId(PubStatePipeId id) {
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "STATE", nullable = false, insertable = false, updatable = false)
	public DimStateGeo getGeo() {
		return geo;
	}
	
	public void setGeo(DimStateGeo geo) {
		this.geo = geo;
	}
}
