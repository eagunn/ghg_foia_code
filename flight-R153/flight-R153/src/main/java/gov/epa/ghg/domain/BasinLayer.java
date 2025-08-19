package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.locationtech.jts.geom.Geometry;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_BASIN_GEO")
public class BasinLayer implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String basinCode;
	private String basin;
	private Geometry geometry;
	
	@Id
	@Column(name = "BASIN_CODE", length = 10)
	public String getBasinCode() {
		return basinCode;
	}
	
	public void setBasinCode(String basinCode) {
		this.basinCode = basinCode;
	}
	
	@Column(name = "BASIN", length = 100)
	public String getBasin() {
		return basin;
	}
	
	public void setBasin(String basin) {
		this.basin = basin;
	}
	
	@Column(name = "GEOMETRY", nullable = true)
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}
