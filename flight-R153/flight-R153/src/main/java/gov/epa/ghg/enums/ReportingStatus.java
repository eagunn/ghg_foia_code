package gov.epa.ghg.enums;

import org.apache.commons.lang.StringUtils;

public enum ReportingStatus {
	
	NULL(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
	POTENTIAL_DATA_QUALITY_ISSUE("Potential Data Quality Issue",
			"-orange",
			"Verification of this facility's report was still in progress as of $!{dataDate}.",
			"QUALITY",
			"ORANGE"),
	STOPPED_REPORTING_UNKNOWN_REASON("Stopped Reporting - Unknown Reason",
			"-red",
			"Facility discontinued reporting without a valid reason as of $!{dataDate}.",
			"UNKNOWN",
			"RED"),
	STOPPED_REPORTING_VALID_REASON("Stopped Reporting - Valid Reason",
			"-gray",
			"Facility discontinued reporting for a valid reason.",
			"VALID",
			"GRAY"
			),
	NOT_VERIFIED_SUBMITTED_LATE("Not Verified - Submitted Late",
			"-orange",
			"Verification of this facility's report was still in progress as of $!{dataDate}.  Facility certified this report after the annual reporting deadline.",
			"LATE",
			"ORANGE"
			),
	IS_VERIFIED_SUBMITTED_LATE("Is Verified - Submitted Late",
			"-red",
			"Facility certified this report after the annual reporting deadline.",
			"EXTENDED",
			"RED"
			);
	
	private final String label;
	private final String markerColorSuffix;
	private final String textBoxContents;
	private final String shorthand;
	private final String color;
	
	ReportingStatus(String label, String markerColor, String textboxContents, String shorthand, String color) {
		this.label = label;
		this.markerColorSuffix = markerColor;
		this.textBoxContents = textboxContents;
		this.shorthand = shorthand;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getShorthand() {
		return shorthand;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getMarkerColorSuffix() {
		return markerColorSuffix;
	}
	
	public String getTextBoxContents() {
		return textBoxContents;
	}
	
	public String label() {
		return getLabel();
	}
	
	public String markerColorSuffix() {
		return getMarkerColorSuffix();
	}
	
	public String textboxContents() {
		return getTextBoxContents();
	}
}