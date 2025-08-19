package gov.epa.ghg.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * DimSector entity. @author MyEclipse Persistence Tools
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_SECTOR")
public class DimSector implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private Long sectorId;
	private String sectorCode;
	private String sectorName;
	private String sectorType;
	private String sectorColor;
	private Integer sortOrder;
	private Set<PubFactsSectorGhgEmission> emissions = new HashSet<PubFactsSectorGhgEmission>(0);
	private Set<DimSubSector> subSectors = new HashSet<DimSubSector>(0);
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimSector() {
	}
	
	/**
	 * full constructor
	 */
	public DimSector(String sectorCode, String sectorName) {
		this.sectorCode = sectorCode;
		this.sectorName = sectorName;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "SECTOR_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getSectorId() {
		return this.sectorId;
	}
	
	public void setSectorId(Long sectorId) {
		this.sectorId = sectorId;
	}
	
	@Column(name = "SECTOR_CODE", length = 12)
	public String getSectorCode() {
		return this.sectorCode;
	}
	
	public void setSectorCode(String sectorCode) {
		this.sectorCode = sectorCode;
	}
	
	@Column(name = "SECTOR_NAME", length = 30)
	public String getSectorName() {
		return this.sectorName;
	}
	
	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}
	
	@Column(name = "SECTOR_TYPE", length = 1)
	public String getSectorType() {
		return sectorType;
	}
	
	public void setSectorType(String sectorType) {
		this.sectorType = sectorType;
	}
	
	@Column(name = "SECTOR_COLOR", length = 10)
	public String getSectorColor() {
		return sectorColor;
	}
	
	public void setSectorColor(String sectorColor) {
		this.sectorColor = sectorColor;
	}
	
	@Column(name = "SORT_ORDER", precision = 3, scale = 0)
	public Integer getSortOrder() {
		return this.sortOrder;
	}
	
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sector")
	public Set<PubFactsSectorGhgEmission> getEmissions() {
		return emissions;
	}
	
	public void setEmissions(Set<PubFactsSectorGhgEmission> emissions) {
		this.emissions = emissions;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sector")
	public Set<DimSubSector> getSubSectors() {
		return subSectors;
	}
	
	public void setSubSectors(Set<DimSubSector> subSectors) {
		this.subSectors = subSectors;
	}
}
