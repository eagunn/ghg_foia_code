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
 * DimGhg entity. @author MyEclipse Persistence Tools
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_GHG")
public class DimGhg implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long gasId;
	private String gasCode;
	private String gasName;
	private String gasLabel;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimGhg() {
	}
	
	/**
	 * full constructor
	 */
	public DimGhg(String gasCode, String gasName) {
		this.gasCode = gasCode;
		this.gasName = gasName;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "GAS_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getGasId() {
		return this.gasId;
	}
	
	public void setGasId(Long gasId) {
		this.gasId = gasId;
	}
	
	@Column(name = "GAS_CODE", length = 10)
	public String getGasCode() {
		return this.gasCode;
	}
	
	public void setGasCode(String gasCode) {
		this.gasCode = gasCode;
	}
	
	@Column(name = "GAS_NAME", length = 50)
	public String getGasName() {
		return this.gasName;
	}
	
	public void setGasName(String gasName) {
		this.gasName = gasName;
	}
	
	@Column(name = "GAS_LABEL", length = 50)
	public String getGasLabel() {
		return this.gasLabel;
	}
	
	public void setGasLabel(String gasLabel) {
		this.gasLabel = gasLabel;
	}
}
