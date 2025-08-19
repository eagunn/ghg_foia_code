package gov.epa.ghg.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Facility View entity.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_FACILITY_SUBPART_KEYVAL")
public class FacilitySubpartKeyVal implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	private FacilitySubpartKeyValId id;
	private DimFacility facility;
	private DimSubpart subpart;
	private LuKey luKey;
	private String value;
	private String notes;
	// private Long year;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public FacilitySubpartKeyVal() {
	}
	
	/**
	 * full constructor
	 */
	public FacilitySubpartKeyVal(
			FacilitySubpartKeyValId id, DimFacility facility, DimSubpart subpart, LuKey luKey, String value, Long year, String notes) {
		this.id = id;
		this.facility = facility;
		this.subpart = subpart;
		this.luKey = luKey;
		this.value = value;
		this.notes = notes;
		// this.year = year;
	}
	
	// Property accessors
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "facilityId", column = @Column(name = "FACILITY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "subpartId", column = @Column(name = "SUBPART_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "keyId", column = @Column(name = "KEY_ID", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "notes", column = @Column(name = "NOTES", nullable = false))
	})
	
	public FacilitySubpartKeyValId getId() {
		return this.id;
	}
	
	public void setId(FacilitySubpartKeyValId id) {
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SUBPART_ID", nullable = false, insertable = false, updatable = false)
	public DimSubpart getSubpart() {
		return this.subpart;
	}
	
	public void setSubpart(DimSubpart subpart) {
		this.subpart = subpart;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "KEY_ID", nullable = false, insertable = false, updatable = false)
	public LuKey getLuKey() {
		return this.luKey;
	}
	
	public void setLuKey(LuKey luKey) {
		this.luKey = luKey;
	}
	
	@Column(name = "KEY_VAL", length = 4000)
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name = "NOTES", nullable = false, insertable = false, updatable = false)
	public String getNotes() {
		return this.notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/*@Column(name = "YEAR", nullable = false, insertable = false, updatable = false, precision = 4, scale = 0)
	public Long getYear() {
		return this.year;
	}

	public void setYear(Long year) {
		this.year = year;
	}*/
	public String resolveKey(boolean wMulti) {
		String retv = "Information on " + this.getSubpart().getSubpartCategory();
		
		// PUB-572 : Subpart W custom headers
		if ("W".equalsIgnoreCase(this.getSubpart().getSubpartName())) {
			String note = this.getNotes();
			if ("1".equals(note)) {
				retv = "Emissions from Offshore Petroleum and Natural Gas Production (mt CO<sub>2</sub>e)";
			} else if ("2".equals(note)) {
				retv = "Emissions from Onshore Petroleum and Natural Gas Production (mt CO<sub>2</sub>e)";
			} else if ("3".equals(note) ||
					"4".equals(note) ||
					"5".equals(note) ||
					"6".equals(note) ||
					"7".equals(note)) {
				retv = "Emissions from Petroleum and Natural Gas Systems, by Source (mt CO<sub>2</sub>e)";
			} else if ("8".equals(note)) {
				retv = "Emissions from Natural Gas Distribution, by Source (mt CO<sub>2</sub>e)";
			} else if ("9".equals(note)) {
				retv = "Emissions from Onshore Petroleum and Natural Gas Gathering & Boosting (mt CO<sub>2</sub>e)";
			} else if ("10".equals(note)) {
				retv = "Emissions from Onshore Gas Transmission Pipelines (mt CO<sub>2</sub>e)";
			} else if ("2i".equals(note)) {
				retv = "Information on Petroleum and Natural Gas Production";
			} else if ("3i".equals(note) ||
					"4i".equals(note) ||
					"5i".equals(note) ||
					"6i".equals(note) ||
					"7i".equals(note)) {
				retv = "Information on Petroleum and Natural Gas Systems";
			} else if ("8i".equals(note)) {
				retv = "Information on Natural Gas Distribution";
			} else if ("9i".equals(note)) {
				retv = "Information on Onshore Oil & Gas Gathering & Boosting";
			} else if ("10i".equals(note)) {
				retv = "Information on Onshore Gas Transmission Pipelines";
			}
			if (wMulti) {
				if ("3".equals(note) ||
						"3i".equals(note)) {
					retv += " - Natural Gas Processing";
				} else if ("4".equals(note) ||
						"4i".equals(note)) {
					retv += " - Natural Gas Transmission/Compression";
				} else if ("5".equals(note) ||
						"5i".equals(note)) {
					retv += " - Underground Natural Gas Storage";
				} else if ("6".equals(note) ||
						"6i".equals(note)) {
					retv += " - Liquefied Natural Gas Storage";
				} else if ("7".equals(note) ||
						"7i".equals(note)) {
					retv += " - Liquefied Natural Gas Imp./Exp. Equipment";
				}
			}
		}
		
		return retv;
	}
}
