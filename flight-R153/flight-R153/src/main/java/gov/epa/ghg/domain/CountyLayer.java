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
@Table(name = "CTY_LAYER")
public class CountyLayer implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Geometry sdoGeometry;
	
	@Id
	@Column(name = "OBJECTID", unique = true, nullable = false, precision = 22, scale = 0)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "POLYS", nullable = true)
	public Geometry getSdoGeometry() {
		return sdoGeometry;
	}
	
	public void setSdoGeometry(Geometry sdoGeometry) {
		this.sdoGeometry = sdoGeometry;
	}
}
