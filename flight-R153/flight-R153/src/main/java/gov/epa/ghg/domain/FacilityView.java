package gov.epa.ghg.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import gov.epa.ghg.enums.ReportingStatus;

/**
 * Facility View entity.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_FACILITY")
public class FacilityView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private Long facilityId;
	private String facilityName;
	private Double latitude;
	private Double longitude;
	private String city;
	private String state;
	private String stateName;
	private String zip;
	private String countyFips;
	private String county;
	private String address1;
	private String address2;
	private String co2Captured;
	private String co2EmittedSupplied;
	private String uuRandDExempt;
	private BigDecimal totalCo2e;
	private ReportingStatus reportingStatus;
	private String comments;
	/*private Long year;
	private Long co2;
	private Long ch4;
	private Long n2o;
	private Long cf4;
	private Long c2f6;
	private Long sf6;
	private Long chf3;
	private Long powerplant;
	private Long landfill;
	private Long metal;
	private Long mineral;
	private Long refinery;
	private Long pulpAndPaper;
	private Long chemical;
	private Long other;*/
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public FacilityView() {
	}
	
	/**
	 * full constructor
	 */
	public FacilityView(
			String facilityName, Double latitude, Double longitude,
			String city, String state, String stateName,
			String zip, String countyFips, String county,
			String address1, String address2,
			BigDecimal totalCo2e, ReportingStatus reportingStatus,
			Long year, Long co2, Long ch4, Long n2o, Long cf4, Long c2f6, Long sf6,
			Long chf3, Long powerplant, Long landfill,
			Long metal, Long mineral, Long refinery, Long pulpAndPaper,
			Long chemical, Long other, String comments
	) {
		this.facilityName = facilityName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.state = state;
		this.stateName = stateName;
		this.zip = zip;
		this.countyFips = countyFips;
		this.county = county;
		this.address1 = address1;
		this.address2 = address2;
		this.totalCo2e = totalCo2e;
		this.reportingStatus = reportingStatus;
		this.comments = comments;
		/*this.year = year;
		this.co2 = co2;
		this.ch4 = ch4;
		this.n2o = n2o;
		this.cf4 = cf4;
		this.c2f6 = c2f6;
		this.sf6 = sf6;
		this.chf3 = chf3;
		this.powerplant = powerplant;
		this.landfill = landfill;
		this.metal = metal;
		this.mineral = mineral;
		this.refinery = refinery;
		this.pulpAndPaper = pulpAndPaper;
		this.chemical = chemical;
		this.other = other;*/
	}
	
	// Property accessors
	
	@Id
	@Column(name = "FACILITY_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}
	
	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}
	
	@Column(name = "FACILITY_NAME", length = 200)
	public String getFacilityName() {
		return this.facilityName;
	}
	
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	
	@Column(name = "LATITUDE", nullable = false, precision = 20, scale = 15)
	public Double getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "LONGITUDE", nullable = false, precision = 20, scale = 15)
	public Double getLongitude() {
		return this.longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "CITY", length = 50)
	public String getCity() {
		return this.city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "STATE_NAME", length = 50)
	public String getStateName() {
		return this.stateName;
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	@Column(name = "ZIP", length = 15)
	public String getZip() {
		return this.zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Column(name = "COUNTY_FIPS", length = 5)
	public String getCountyFips() {
		return this.countyFips;
	}
	
	public void setCountyFips(String countyFips) {
		this.countyFips = countyFips;
	}
	
	@Column(name = "COUNTY", length = 50)
	public String getCounty() {
		return this.county;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	
	@Column(name = "ADDRESS1", length = 200)
	public String getAddress1() {
		return this.address1;
	}
	
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	@Column(name = "ADDRESS2", length = 200)
	public String getAddress2() {
		return this.address2;
	}
	
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getCo2Captured() {
		return co2Captured;
	}
	
	public void setCo2Captured(String co2Captured) {
		this.co2Captured = co2Captured;
	}
	
	public String getCo2EmittedSupplied() {
		return co2EmittedSupplied;
	}
	
	public void setCo2EmittedSupplied(String co2EmittedSupplied) {
		this.co2EmittedSupplied = co2EmittedSupplied;
	}
	
	/*@Column(name = "YEAR", nullable = false, precision = 4, scale = 0)
	public Long getYear() {
		return this.year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	@Column(name = "CO2", nullable = false, precision = 22, scale = 0)
	public Long getCo2() {
		return this.co2;
	}

	public void setCo2(Long co2) {
		this.co2 = co2;
	}

	@Column(name = "CH4", nullable = false, precision = 22, scale = 0)
	public Long getCh4() {
		return this.ch4;
	}

	public void setCh4(Long ch4) {
		this.ch4 = ch4;
	}

	@Column(name = "N2O", nullable = false, precision = 22, scale = 0)
	public Long getN2o() {
		return this.n2o;
	}

	public void setN2o(Long n2o) {
		this.n2o = n2o;
	}

	@Column(name = "CF4", nullable = false, precision = 22, scale = 0)
	public Long getCf4() {
		return this.cf4;
	}

	public void setCf4(Long cf4) {
		this.cf4 = cf4;
	}

	@Column(name = "C2F6", nullable = false, precision = 22, scale = 0)
	public Long getC2f6() {
		return this.c2f6;
	}

	public void setC2f6(Long c2f6) {
		this.c2f6 = c2f6;
	}
	
	@Column(name = "SF6", nullable = false, precision = 22, scale = 0)
	public Long getSf6() {
		return this.sf6;
	}

	public void setSf6(Long sf6) {
		this.sf6 = sf6;
	}

	@Column(name = "CHF3", nullable = false, precision = 22, scale = 0)
	public Long getChf3() {
		return this.chf3;
	}

	public void setChf3(Long chf3) {
		this.chf3 = chf3;
	}*/
	
	public String getUuRandDExempt() {
		return uuRandDExempt;
	}
	
	public void setUuRandDExempt(String uuRandDExempt) {
		this.uuRandDExempt = uuRandDExempt;
	}
	
	@Column(name = "TOTAL_CO2E", nullable = false, precision = 22, scale = 0)
	public BigDecimal getTotalCo2e() {
		return this.totalCo2e;
	}
	
	public void setTotalCo2e(BigDecimal totalCo2e) {
		this.totalCo2e = totalCo2e;
	}
	
	public ReportingStatus getReportingStatus() {
		return reportingStatus;
	}
	
	public void setReportingStatus(ReportingStatus reportingStatus) {
		this.reportingStatus = reportingStatus;
	}
	
	@Column(name = "COMMENTS", length = 4000)
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/*@Column(name = "POWERPLANT", nullable = false, precision = 22, scale = 0)
	public Long getPowerplant() {
		return this.powerplant;
	}

	public void setPowerplant(Long powerplant) {
		this.powerplant = powerplant;
	}
	
	@Column(name = "LANDFILL", nullable = false, precision = 22, scale = 0)
	public Long getLandfill() {
		return this.landfill;
	}

	public void setLandfill(Long landfill) {
		this.landfill = landfill;
	}

	@Column(name = "METAL", nullable = false, precision = 22, scale = 0)
	public Long getMetal() {
		return this.metal;
	}

	public void setMetal(Long metal) {
		this.metal = metal;
	}
	
	@Column(name = "MINERAL", nullable = false, precision = 22, scale = 0)
	public Long getMineral() {
		return this.mineral;
	}

	public void setMineral(Long mineral) {
		this.mineral = mineral;
	}

	@Column(name = "REFINERY", nullable = false, precision = 22, scale = 0)
	public Long getRefinery() {
		return this.refinery;
	}

	public void setRefinery(Long refinery) {
		this.refinery = refinery;
	}

	@Column(name = "PULP_AND_PAPER", nullable = false, precision = 22, scale = 0)
	public Long getPulpAndPaper() {
		return this.pulpAndPaper;
	}

	public void setPulpAndPaper(Long pulpAndPaper) {
		this.pulpAndPaper = pulpAndPaper;
	}
	
	@Column(name = "CHEMICAL", nullable = false, precision = 22, scale = 0)
	public Long getChemical() {
		return this.chemical;
	}

	public void setChemical(Long chemical) {
		this.chemical = chemical;
	}
	
	@Column(name = "OTHER", nullable = false, precision = 22, scale = 0)
	public Long getOther() {
		return this.other;
	}

	public void setOther(Long other) {
		this.other = other;
	}*/
	
}
