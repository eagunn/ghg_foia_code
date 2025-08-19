package gov.epa.ghg.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * HelpLinks entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "PUB_HELP_LINKS")
public class HelpLinks implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private Long helpLinkId;
	private Page page;
	private String linkText;
	private String linkHref;
	private BigDecimal sortOrder;
	private String isFullLink;
	private String createdDate;
	private BigDecimal createdUserId;
	private String lastUpdateDate;
	private BigDecimal lastUpdateUserId;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public HelpLinks() {
	}
	
	/**
	 * minimal constructor
	 */
	public HelpLinks(Page page, String linkText, String linkHref) {
		this.page = page;
		this.linkText = linkText;
		this.linkHref = linkHref;
	}
	
	/**
	 * full constructor
	 */
	public HelpLinks(Page page, String linkText, String linkHref,
			BigDecimal sortOrder, String isFullLink, String createdDate,
			BigDecimal createdUserId, String lastUpdateDate,
			BigDecimal lastUpdateUserId) {
		this.page = page;
		this.linkText = linkText;
		this.linkHref = linkHref;
		this.sortOrder = sortOrder;
		this.isFullLink = isFullLink;
		this.createdDate = createdDate;
		this.createdUserId = createdUserId;
		this.lastUpdateDate = lastUpdateDate;
		this.lastUpdateUserId = lastUpdateUserId;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "HELP_LINK_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getHelpLinkId() {
		return this.helpLinkId;
	}
	
	public void setHelpLinkId(Long helpLinkId) {
		this.helpLinkId = helpLinkId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAGE_ID", nullable = false)
	public Page getPage() {
		return this.page;
	}
	
	public void setPage(Page page) {
		this.page = page;
	}
	
	@Column(name = "LINK_TEXT", nullable = false)
	public String getLinkText() {
		return this.linkText;
	}
	
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}
	
	@Column(name = "LINK_HREF", nullable = false)
	public String getLinkHref() {
		return this.linkHref;
	}
	
	public void setLinkHref(String linkHref) {
		this.linkHref = linkHref;
	}
	
	@Column(name = "SORT_ORDER", precision = 22, scale = 0)
	public BigDecimal getSortOrder() {
		return this.sortOrder;
	}
	
	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@Column(name = "IS_FULL_LINK", length = 1)
	public String getIsFullLink() {
		return this.isFullLink;
	}
	
	public void setIsFullLink(String isFullLink) {
		this.isFullLink = isFullLink;
	}
	
	@Column(name = "CREATED_DATE")
	public String getCreatedDate() {
		return this.createdDate;
	}
	
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	@Column(name = "CREATED_USER_ID", precision = 22, scale = 0)
	public BigDecimal getCreatedUserId() {
		return this.createdUserId;
	}
	
	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}
	
	@Column(name = "LAST_UPDATE_DATE")
	public String getLastUpdateDate() {
		return this.lastUpdateDate;
	}
	
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	@Column(name = "LAST_UPDATE_USER_ID", precision = 22, scale = 0)
	public BigDecimal getLastUpdateUserId() {
		return this.lastUpdateUserId;
	}
	
	public void setLastUpdateUserId(BigDecimal lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	
}
