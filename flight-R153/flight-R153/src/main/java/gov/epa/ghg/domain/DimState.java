package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * DimState entity. @author MyEclipse Persistence Tools
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_STATE_GEO")
public class DimState implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private String state;
	private String region;
	private String stateFipsCode;
	private String stateName;
	private Integer sortOrder;
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimState() {
	}
	
	/**
	 * full constructor
	 */
	public DimState(String region, String stateFipsCode, String stateName) {
		this.region = region;
		this.stateFipsCode = stateFipsCode;
		this.stateName = stateName;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "STATE", unique = true, nullable = false, length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "REGION", length = 2)
	public String getRegion() {
		return this.region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	@Column(name = "STATE_FIPS_CODE", length = 2)
	public String getStateFipsCode() {
		return this.stateFipsCode;
	}
	
	public void setStateFipsCode(String stateFipsCode) {
		this.stateFipsCode = stateFipsCode;
	}
	
	@Column(name = "STATE_NAME", length = 160)
	public String getStateName() {
		return this.stateName;
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	@Column(name = "SORT_ORDER", precision = 3, scale = 0)
	public Integer getSortOrder() {
		return this.sortOrder;
	}
	
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
