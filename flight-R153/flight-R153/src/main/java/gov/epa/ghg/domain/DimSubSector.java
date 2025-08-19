package gov.epa.ghg.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * DimSector entity. @author MyEclipse Persistence Tools
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_SUBSECTOR")
public class DimSubSector implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private Long subSectorId;
	private DimSector sector;
	private String subSectorName;
	private String subSectorDescription;
	private Set<PubFactsSectorGhgEmission> emissions = new HashSet<PubFactsSectorGhgEmission>(0);
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimSubSector() {
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "SUBSECTOR_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getSubSectorId() {
		return this.subSectorId;
	}
	
	public void setSubSectorId(Long subSectorId) {
		this.subSectorId = subSectorId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SECTOR_ID", nullable = false, insertable = false, updatable = false)
	public DimSector getSector() {
		return this.sector;
	}
	
	public void setSector(DimSector sector) {
		this.sector = sector;
	}
	
	@Column(name = "SUBSECTOR_NAME", length = 12)
	public String getSubSectorName() {
		return this.subSectorName;
	}
	
	public void setSubSectorName(String subSectorName) {
		this.subSectorName = subSectorName;
	}
	
	@Column(name = "SUBSECTOR_DESC", length = 30)
	public String getSubSectorDescription() {
		return this.subSectorDescription;
	}
	
	public void setSubSectorDescription(String subSectorDescription) {
		this.subSectorDescription = subSectorDescription;
	}
}
