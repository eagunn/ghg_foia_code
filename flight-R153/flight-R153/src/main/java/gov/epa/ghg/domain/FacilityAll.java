package gov.epa.ghg.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.enums.converter.ReportingStatusConverter;

/**
 * Created by lee@saic Oct 2017
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_DIM_FACILITY_MV")
//@Indexed
//@Analyzer(impl=org.apache.lucene.analysis.standard.StandardAnalyzer.class)
public class FacilityAll implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private DimFacilityId id;
	private Double latitude;
	private Double longitude;
	private String city;
	private String state;
	private String zip;
	private String countyFips;
	private String county;
	private String address1;
	private String address2;
	private String facilityName;
	private String stateName;
	private String naicsCode;
	private String emissionClassification;
	private String programName;
	private String programSysId;
	private String frsId;
	private String cemsUsed;
	private String co2Captured;
	private String reportedSubparts;
	private String co2EmittedSupplied;
	// private String html;
	private String parentCompany;
	private Long eggrtFacilityId;
	private String uuRandDExempt;
	// private Point location;
	private LuTribalLands tribalLand;
	private ReportingStatus reportingStatus;
	private String processStationaryCml;
	private String reportedIndustryTypes;
	private String comments;
	// private byte[] rrMonitoringPlan;
	private String rrFilename;
	private String rrLink;
	
	private Set<FacilityAllSectorEmission> emissions = new HashSet<FacilityAllSectorEmission>(0);
	private Set<PubSectorGhgEmissionPE> peEmissions = new HashSet<PubSectorGhgEmissionPE>(0);
	private Set<PubSectorGhgEmissionFC> fcEmissions = new HashSet<PubSectorGhgEmissionFC>(0);
	private Set<PubSectorGhgEmissionUC> ucEmissions = new HashSet<PubSectorGhgEmissionUC>(0);
	private Set<PubSectorGhgEmissionCoal> emCoal = new HashSet<PubSectorGhgEmissionCoal>(0);
	private Set<PubSectorGhgEmissionNg> emNg = new HashSet<PubSectorGhgEmissionNg>(0);
	private Set<PubSectorGhgEmissionPet> emPet = new HashSet<PubSectorGhgEmissionPet>(0);
	private Set<PubSectorGhgEmissionOther> emOther = new HashSet<PubSectorGhgEmissionOther>(0);
	private Set<PubSectorGhgEmissionSorb> emSorb = new HashSet<PubSectorGhgEmissionSorb>(0);
	private Set<PubLdcFacility> layers = new HashSet<PubLdcFacility>(0);
	private Set<PubBasinFacility> basins = new HashSet<PubBasinFacility>(0);
	private Set<DimFacilityStatus> facStatus = new HashSet<DimFacilityStatus>(0);
	
	// private FactFacilityEmission factFacilityEmission;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public FacilityAll() {
	}
	
	/**
	 * full constructor
	 */
	public FacilityAll(DimFacilityId id, Double latitude, Double longitude, String city,
			String state, String zip, String countyFips, String county,
			String address1, String address2, String facilityName, String comments,
			String rrFilename, String rrLink) {
		//, FactFacilityEmission factFacilityEmission) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.countyFips = countyFips;
		this.county = county;
		this.address1 = address1;
		this.address2 = address2;
		this.facilityName = facilityName;
		this.comments = comments;
		// this.rrMonitoringPlan = rrMonitoringPlan;
		this.rrFilename = rrFilename;
		this.rrLink = rrLink;
		// this.factFacilityEmission = factFacilityEmission;
	}
	
	// Property accessors
	/*@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "FACILITY_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getFacilityId() {
		return this.facilityId;
	}

	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}*/
	
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
	
	@Column(name = "LATITUDE", precision = 20, scale = 15)
	public Double getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "LONGITUDE", precision = 20, scale = 15)
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
	
	@Column(name = "ZIP", length = 5)
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
	
	@Column(name = "FACILITY_NAME", length = 200)
	public String getFacilityName() {
		return this.facilityName;
	}
	
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	
	@Column(name = "STATE_NAME", length = 50)
	@FullTextField(searchable = Searchable.YES, projectable = Projectable.YES)
	public String getStateName() {
		return stateName;
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	@Column(name = "NAICS_CODE", length = 255)
	public String getNaicsCode() {
		return naicsCode;
	}
	
	public void setNaicsCode(String naicsCode) {
		this.naicsCode = naicsCode;
	}
	
	@Column(name = "EMISSION_CLASSIFICATION_CODE", length = 10)
	public String getEmissionClassification() {
		return emissionClassification;
	}
	
	public void setEmissionClassification(String emissionClassification) {
		this.emissionClassification = emissionClassification;
	}
	
	@Column(name = "PROGRAM_NAME", length = 100)
	public String getProgramName() {
		return programName;
	}
	
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	@Column(name = "PROGRAM_SYS_ID", length = 30)
	public String getProgramSysId() {
		return programSysId;
	}
	
	public void setProgramSysId(String programSysId) {
		this.programSysId = programSysId;
	}
	
	@Column(name = "FRS_ID", length = 30)
	public String getFrsId() {
		return frsId;
	}
	
	public void setFrsId(String frsId) {
		this.frsId = frsId;
	}
	
	@Column(name = "CEMS_USED", length = 1)
	public String getCemsUsed() {
		return cemsUsed;
	}
	
	public void setCemsUsed(String cemsUsed) {
		this.cemsUsed = cemsUsed;
	}
	
	@Column(name = "CO2_CAPTURED", length = 1)
	public String getCo2Captured() {
		return co2Captured;
	}
	
	public void setCo2Captured(String co2Captured) {
		this.co2Captured = co2Captured;
	}
	
	@Column(name = "REPORTED_SUBPARTS", length = 200)
	public String getReportedSubparts() {
		return reportedSubparts;
	}
	
	public void setReportedSubparts(String reportedSubparts) {
		this.reportedSubparts = reportedSubparts;
	}
	
	@Column(name = "EMITTED_CO2_SUPPLIED", length = 1)
	public String getCo2EmittedSupplied() {
		return co2EmittedSupplied;
	}
	
	public void setCo2EmittedSupplied(String co2EmittedSupplied) {
		this.co2EmittedSupplied = co2EmittedSupplied;
	}

	/*@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "PUBLIC_HTML")
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}*/
	
	@Column(name = "PARENT_COMPANY", length = 4000)
	public String getParentCompany() {
		return parentCompany;
	}
	
	public void setParentCompany(String parentCompany) {
		this.parentCompany = parentCompany;
	}
	
	@Column(name = "EGGRT_FACILITY_ID", precision = 22, scale = 0)
	public Long getEggrtFacilityId() {
		return eggrtFacilityId;
	}
	
	public void setEggrtFacilityId(Long eggrtFacilityId) {
		this.eggrtFacilityId = eggrtFacilityId;
	}

	/*@Column(name="LOCATION", nullable=true)
	@Type(type="org.hibernatespatial.GeometryUserType", parameters = {@Parameter(name="dialect", value="org.hibernatespatial.oracle.OracleSpatial10gDialect")})
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}*/
	
	@Column(name = "UU_RD_EXEMPT", length = 1)
	public String getUuRandDExempt() {
		return uuRandDExempt;
	}
	
	public void setUuRandDExempt(String uuRandDExempt) {
		this.uuRandDExempt = uuRandDExempt;
	}
	
	//@ManyToOne(fetch = FetchType.EAGER)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRIBAL_LAND_ID")
	public LuTribalLands getTribalLand() {
		return tribalLand;
	}
	
	public void setTribalLand(LuTribalLands tribalLand) {
		this.tribalLand = tribalLand;
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
	
	@Column(name = "PROCESS_STATIONARY_CML", nullable = true)
	public String getProcessStationaryCml() {
		return processStationaryCml;
	}
	
	public void setProcessStationaryCml(String processStationaryCml) {
		
		this.processStationaryCml = processStationaryCml;
	}
	
	@Column(name = "REPORTED_INDUSTRY_TYPES", nullable = true)
	public String getReportedIndustryTypes() {
		return reportedIndustryTypes;
		
	}
	
	public void setReportedIndustryTypes(String reportedIndustryTypes) {
		this.reportedIndustryTypes = reportedIndustryTypes;
	}
	
	@Column(name = "COMMENTS", length = 4000)
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	/*@Column(name="RR_MONITORING_PLAN")
	public byte[] getRrMonitoringPlan() {
		return rrMonitoringPlan;
	}

	public void setRrMonitoringPlan(byte[] rrMonitoringPlan) {
		this.rrMonitoringPlan = rrMonitoringPlan;
	}*/
	
	@Column(name = "RR_MONITORING_PLAN_FILENAME", length = 100)
	public String getRrFilename() {
		return rrFilename;
	}
	
	public void setRrFilename(String rrFilename) {
		this.rrFilename = rrFilename;
	}
	
	@Column(name = "RR_MRV_PLAN_URL", length = 1000)
	public String getRrLink() {
		return rrLink;
	}
	
	public void setRrLink(String rrLink) {
		this.rrLink = rrLink;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<FacilityAllSectorEmission> getEmissions() {
		return emissions;
	}
	
	public void setEmissions(Set<FacilityAllSectorEmission> emissions) {
		this.emissions = emissions;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionPE> getPeEmissions() {
		return peEmissions;
	}
	
	public void setPeEmissions(Set<PubSectorGhgEmissionPE> peEmissions) {
		this.peEmissions = peEmissions;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionFC> getFcEmissions() {
		return fcEmissions;
	}
	
	public void setFcEmissions(Set<PubSectorGhgEmissionFC> fcEmissions) {
		this.fcEmissions = fcEmissions;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionUC> getUcEmissions() {
		return ucEmissions;
	}
	
	public void setUcEmissions(Set<PubSectorGhgEmissionUC> ucEmissions) {
		this.ucEmissions = ucEmissions;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionCoal> getEmCoal() {
		return emCoal;
	}
	
	public void setEmCoal(Set<PubSectorGhgEmissionCoal> emCoal) {
		this.emCoal = emCoal;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionNg> getEmNg() {
		return emNg;
	}
	
	public void setEmNg(Set<PubSectorGhgEmissionNg> emNg) {
		this.emNg = emNg;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionPet> getEmPet() {
		return emPet;
	}
	
	public void setEmPet(Set<PubSectorGhgEmissionPet> emPet) {
		this.emPet = emPet;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionOther> getEmOther() {
		return emOther;
	}
	
	public void setEmOther(Set<PubSectorGhgEmissionOther> emOther) {
		this.emOther = emOther;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubSectorGhgEmissionSorb> getEmSorb() {
		return emSorb;
	}
	
	public void setEmSorb(Set<PubSectorGhgEmissionSorb> emSorb) {
		this.emSorb = emSorb;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubLdcFacility> getLayers() {
		return layers;
	}
	
	public void setLayers(Set<PubLdcFacility> layers) {
		this.layers = layers;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<PubBasinFacility> getBasins() {
		return basins;
	}
	
	public void setBasins(Set<PubBasinFacility> basins) {
		this.basins = basins;
	}
	
	public String retrieveBasinNameAndNumber() {
		
		String retv = "";
		if (basins != null && basins.size() != 0) {
			for (PubBasinFacility basin : basins) {
				retv = basin.getLayer().getBasinCode() + " - " + basin.getLayer().getBasin();
			}
		}
		
		return retv;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<DimFacilityStatus> getFacStatus() {
		return facStatus;
	}
	
	public void setFacStatus(Set<DimFacilityStatus> facStatus) {
		this.facStatus = facStatus;
	}
	
}
