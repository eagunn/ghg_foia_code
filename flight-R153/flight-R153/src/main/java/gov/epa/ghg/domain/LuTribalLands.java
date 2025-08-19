package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Facility View entity.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_LU_TRIBAL_LANDS")
public class LuTribalLands implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long tribalLandId;
	private String tribalLandName;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public LuTribalLands() {
	}
	
	/**
	 * full constructor
	 */
	public LuTribalLands(
			Long tribalLandId, String tribalLandName) {
		this.tribalLandId = tribalLandId;
		this.tribalLandName = tribalLandName;
	}
	
	// Property accessors
	
	@Id
	@Column(name = "TRIBAL_LAND_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getTribalLandId() {
		return this.tribalLandId;
	}
	
	public void setTribalLandId(Long tribalLandId) {
		this.tribalLandId = tribalLandId;
	}
	
	@Column(name = "TRIBAL_LAND_NAME", length = 200)
	public String getTribalLandName() {
		return this.tribalLandName;
	}
	
	public void setTribalLandName(String tribalLandName) {
		this.tribalLandName = tribalLandName;
	}
}
