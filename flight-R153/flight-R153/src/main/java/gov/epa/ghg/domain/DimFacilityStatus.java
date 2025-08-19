package gov.epa.ghg.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.enums.converter.ReportingStatusConverter;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_FACILITY_STATUS_MV")
public class DimFacilityStatus implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private DimFacilityId id;
	private DimFacility facility;
	private String facilityType;
	private ReportingStatus reportingStatus;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public DimFacilityStatus() {
	}
	
	/**
	 * full constructor
	 */
	public DimFacilityStatus(DimFacilityId id, DimFacility facility, String facilityType) {
		this.id = id;
		this.facility = facility;
		this.facilityType = facilityType;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0))
	})
	public DimFacilityId getId() {
		return this.id;
	}
	
	public void setId(DimFacilityId id) {
		
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "FACILITY_ID", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "YEAR", nullable = false, insertable = false, updatable = false)
	})
	public DimFacility getFacility() {
		return this.facility;
	}
	
	public void setFacility(DimFacility facility) {
		this.facility = facility;
	}
	
	@Column(name = "FACILITY_TYPE", length = 1)
	public String getFacilityType() {
		return facilityType;
	}
	
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	
	@Enumerated(EnumType.STRING)
	@Convert(converter = ReportingStatusConverter.class)
	@Column(name = "REPORTING_STATUS")
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
	
}
