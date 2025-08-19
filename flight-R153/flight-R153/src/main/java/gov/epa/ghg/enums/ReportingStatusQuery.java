package gov.epa.ghg.enums;

//this represents front-end queries related to Reporting Status
public enum ReportingStatusQuery {

	//black isn't associated with a particular RS but could be one of two
	BLACK(null),
	ORANGE(ReportingStatus.POTENTIAL_DATA_QUALITY_ISSUE),
	RED(ReportingStatus.STOPPED_REPORTING_UNKNOWN_REASON),
	GRAY(ReportingStatus.STOPPED_REPORTING_VALID_REASON),
	STOPPED_REPORTING(null),
	STILL_REPORTING(null);

	private ReportingStatus rs;
	private ReportingStatusQuery(ReportingStatus rs) {
		this.rs = rs;
	}

	public ReportingStatus getReportingStatus() { return rs; }

	public static ReportingStatusQuery fromString(String color) {

		if ("BLACK".equalsIgnoreCase(color)) {
			return BLACK ;
		}
		if ("GRAY".equalsIgnoreCase(color)) {
			return GRAY;
		}
		if ("RED".equalsIgnoreCase(color)) {
			return RED;
		}
		if ("ORANGE".equalsIgnoreCase(color)) {
			return ORANGE;
		}

		return null;
	}
}
