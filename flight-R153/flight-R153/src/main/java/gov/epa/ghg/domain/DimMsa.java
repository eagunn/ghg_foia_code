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

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_MSA_GEO")
public class DimMsa implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private String cbsafp;
	private String cbsa_title;
	private String m_status;
	private String csa_title;
	private Geometry geometry;
	
	// Constructors
	public DimMsa() {
		
	}
	
	public DimMsa(String cbsafp, String cbsa_title, String m_status, String csa_title, Geometry geometry) {
		this.cbsafp = cbsafp;
		this.cbsa_title = cbsa_title;
		this.m_status = m_status;
		this.csa_title = csa_title;
		this.geometry = geometry;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "CBSAFP", unique = true, nullable = false, length = 5)
	public String getCbsafp() {
		return cbsafp;
	}
	
	public void setCbsafp(String cbsafp) {
		this.cbsafp = cbsafp;
	}
	
	@Column(name = "CBSA_TITLE", length = 120)
	public String getCbsa_title() {
		return cbsa_title;
	}
	
	public void setCbsa_title(String cbsa_title) {
		this.cbsa_title = cbsa_title;
	}
	
	@Column(name = "M_STATUS", length = 35)
	public String getM_status() {
		return m_status;
	}
	
	public void setM_status(String m_status) {
		this.m_status = m_status;
	}
	
	@Column(name = "CSA_TITLE", length = 60)
	public String getCsa_title() {
		return csa_title;
	}
	
	public void setCsa_title(String csa_title) {
		this.csa_title = csa_title;
	}
	
	@Column(name = "GEOMETRY", nullable = true)
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
}
