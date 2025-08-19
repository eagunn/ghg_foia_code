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
@Table(name = "PUB_DIM_SUBPART")
public class DimSubpart implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long subpartId;
	private String subpartName;
	private String subpartCategory;
	private String subpartType;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimSubpart() {
	}
	
	/**
	 * full constructor
	 */
	public DimSubpart(
			Long subpartId, String subpartName, String subpartCategory) {
		this.subpartId = subpartId;
		this.subpartName = subpartName;
		this.subpartCategory = subpartCategory;
	}
	
	// Property accessors
	
	@Id
	@Column(name = "SUBPART_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getSubpartId() {
		return this.subpartId;
	}
	
	public void setSubpartId(Long subpartId) {
		this.subpartId = subpartId;
	}
	
	@Column(name = "SUBPART_NAME", length = 50)
	public String getSubpartName() {
		return this.subpartName;
	}
	
	public void setSubpartName(String subpartName) {
		this.subpartName = subpartName;
	}
	
	@Column(name = "SUBPART_CATEGORY", length = 300)
	public String getSubpartCategory() {
		return this.subpartCategory;
	}
	
	public void setSubpartCategory(String subpartCategory) {
		this.subpartCategory = subpartCategory;
	}
	
	@Column(name = "SUBPART_TYPE", length = 1)
	public String getSubpartType() {
		return subpartType;
	}
	
	public void setSubpartType(String subpartType) {
		this.subpartType = subpartType;
	}
}
