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

import org.hibernate.annotations.GenericGenerator;

/**
 * Page entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "PUB_PAGE")
public class Page implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private Long pageId;
	private String pageProperty;
	private String pageName;
	private String description;
	private Set<HelpLinks> helpLinkses = new HashSet<HelpLinks>(0);
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public Page() {
	}
	
	/**
	 * minimal constructor
	 */
	public Page(String pageProperty, String pageName) {
		this.pageProperty = pageProperty;
		this.pageName = pageName;
	}
	
	/**
	 * full constructor
	 */
	public Page(String pageProperty, String pageName, String description,
			Set<HelpLinks> helpLinkses) {
		this.pageProperty = pageProperty;
		this.pageName = pageName;
		this.description = description;
		this.helpLinkses = helpLinkses;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "PAGE_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getPageId() {
		return this.pageId;
	}
	
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	
	@Column(name = "PAGE_PROPERTY", nullable = false, length = 150)
	public String getPageProperty() {
		return this.pageProperty;
	}
	
	public void setPageProperty(String pageProperty) {
		this.pageProperty = pageProperty;
	}
	
	@Column(name = "PAGE_NAME", nullable = false, length = 100)
	public String getPageName() {
		return this.pageName;
	}
	
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "page")
	public Set<HelpLinks> gethelpLinkses() {
		return this.helpLinkses;
	}
	
	public void sethelpLinkses(Set<HelpLinks> helpLinkses) {
		this.helpLinkses = helpLinkses;
	}
	
}
