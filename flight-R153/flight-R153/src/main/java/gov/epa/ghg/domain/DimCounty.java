package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Geometry;

/**
 * PubDimCounty entity. @author MyEclipse Persistence Tools
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_COUNTY_GEO")
public class DimCounty implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String countyFips;
	private String countyName;
	private String state;
	private String stateName;
	private Geometry geometry;
	
	public DimCounty() {
	}
	
	public DimCounty(String countyName, String state, String stateName) {
		this.countyName = countyName;
		this.state = state;
		this.stateName = stateName;
	}
	
	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "COUNTY_FIPS", unique = true, nullable = false, length = 5)
	public String getCountyFips() {
		return this.countyFips;
	}
	
	public void setCountyFips(String countyFips) {
		this.countyFips = countyFips;
	}
	
	@Column(name = "COUNTY_NAME", length = 44)
	public String getCountyName() {
		return this.countyName;
	}
	
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "STATE_NAME", length = 66)
	public String getStateName() {
		return this.stateName;
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	@Column(name = "GEOMETRY", nullable = true)
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}
