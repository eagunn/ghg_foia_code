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
@Table(name = "PUB_LU_KEY")
public class LuKey implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long keyId;
	private String keyName;
	private String keyDescription;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public LuKey() {
	}
	
	/**
	 * full constructor
	 */
	public LuKey(
			Long keyId, String keyName, String keyDescription) {
		this.keyId = keyId;
		this.keyName = keyName;
		this.keyDescription = keyDescription;
	}
	
	// Property accessors
	
	@Id
	@Column(name = "KEY_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getKeyId() {
		return this.keyId;
	}
	
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	
	@Column(name = "KEY_NAME", length = 50)
	public String getKeyName() {
		return this.keyName;
	}
	
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	@Column(name = "KEY_DESCRIPTION", length = 300)
	public String getKeyDescription() {
		return this.keyDescription;
	}
	
	public void setKeyDescription(String keyDescription) {
		this.keyDescription = keyDescription;
	}
}
