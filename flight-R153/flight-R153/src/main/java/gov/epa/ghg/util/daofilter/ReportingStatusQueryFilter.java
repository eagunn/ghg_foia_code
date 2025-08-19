package gov.epa.ghg.util.daofilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gov.epa.ghg.util.DaoUtils;

/**
 * Created by alabdullahwi on 9/14/2015.
 */
public class ReportingStatusQueryFilter {
	
	public static final Map<String, String> rsDictionary;
	public static final Map<String, String> sqlDictionary;
	
	// init rsDictionary
	static {
		Map<String, String> _t = new HashMap<String, String>();
		_t.put("ORANGE", " fs.reportingStatus in ('POTENTIAL_DATA_QUALITY_ISSUE', 'NOT_VERIFIED_SUBMITTED_LATE') ");
		_t.put("RED", " fs.reportingStatus in ('STOPPED_REPORTING_UNKNOWN_REASON', 'IS_VERIFIED_SUBMITTED_LATE') ");
		_t.put("GRAY", " fs.reportingStatus = 'STOPPED_REPORTING_VALID_REASON'");
		_t.put("BLACK", " fs.reportingStatus is null ");
		_t.put("STILL_REPORTING", "(fs.reportingStatus is null or fs.reportingStatus not in ('STOPPED_REPORTING_UNKNOWN_REASON', 'STOPPED_REPORTING_VALID_REASON') ) ");
		_t.put("STOPPED_REPORTING", "fs.reportingStatus in ('STOPPED_REPORTING_UNKNOWN_REASON', 'STOPPED_REPORTING_VALID_REASON') ");
		rsDictionary = Collections.unmodifiableMap(_t);
		_t = new HashMap<String, String>();
		
	}
	
	// init sqlDictionary
	static {
		Map<String, String> _fs = new HashMap<String, String>();
		_fs.put("ORANGE", " fs.reporting_status in ('POTENTIAL_DATA_QUALITY_ISSUE', 'NOT_VERIFIED_SUBMITTED_LATE') ");
		_fs.put("RED", " fs.reporting_status in ('STOPPED_REPORTING_UNKNOWN_REASON', 'IS_VERIFIED_SUBMITTED_LATE') ");
		_fs.put("GRAY", " fs.reporting_status = 'STOPPED_REPORTING_VALID_REASON'");
		_fs.put("BLACK", " fs.reporting_status is null ");
		_fs.put("STILL_REPORTING", "(fs.reporting_status is null or fs.reporting_status not in ('STOPPED_REPORTING_UNKNOWN_REASON', 'STOPPED_REPORTING_VALID_REASON') ) ");
		_fs.put("STOPPED_REPORTING", "fs.reporting_status in ('STOPPED_REPORTING_VALID_REASON') ");
		sqlDictionary = Collections.unmodifiableMap(_fs);
		_fs = new HashMap<String, String>();
		
	}
	
	/*
	 * REPORTING STATUS FILTER METHODS
	 */
	// provides proper hql formatting based on whether reportingStatus is null or not
	public static String filter(boolean isTrend, String facilitySymbol, int year) {
		
		if (isTrend) {
			return "";
		}
		return ReportingStatusQueryFilter.filter(facilitySymbol, year);
	}
	
	public static String filter(String facilitySymbol, int year) {
		
		String retVal = "";
		
		if (!isReportingStatusEnabled(year)) {
			return retVal;
		}
		
		// SPECIAL CASE: PUB-488
//        if ("STOPPED_REPORTING".equals(facilitySymbol)) {
//            retVal = " and (" + rsDictionary.get("RED") + " or " + rsDictionary.get("GRAY") + ") ";
//        }
//
//        else {
		String symbolVal = rsDictionary.get(facilitySymbol);
		if (symbolVal != null) {
			retVal = " and " + symbolVal;
		}

//        }
		
		return retVal;
	}
	
	public static String sqlFilter(String facilitySymbol, int year) {
		
		String retVal = "";
		
		if (!isReportingStatusEnabled(year)) {
			return retVal;
		}
		
		String symbolVal = sqlDictionary.get(facilitySymbol);
		if (symbolVal != null) {
			retVal = " and " + symbolVal;
		}
		
		return retVal;
	}
	
	// this is needed when you want to see the history of a facility with a reporting status, a different method because for example a facility with a 2013 ReportingStatus as 'POTENTIAL DATA' might have different RS for previous years, hence the DAO
	// query would be different
	public static String reportingStatusQueryFilterInHistoricContext(String yearField, String facilitySymbol, int year) {
		String retVal = "";
		
		if (!isReportingStatusEnabled(year)) {
			return retVal;
		} else {
			
			String symbolVal = rsDictionary.get(facilitySymbol);
			
			if (symbolVal != null) {
				retVal = yearField + " >= " + year + " and " + symbolVal;
			}
			
		}
		
		return retVal;
		
	}
	
	public static String filterEmissionsRange(String facilitySymbol, String lowE, String highE) {
		
		if ("STOPPED_REPORTING".equals(facilitySymbol)) {
			return ")";
		} else {
			return
					"group by f.id having " +
							DaoUtils.shouldFacilitiesWithNullEmissionsBeIncluded(lowE) +
							"(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) ";
		}
		
	}
	
	// this does checks to see if there's a need to display the "Reporting Status" column or not
	public static String shouldReportingStatusBeIncluded(int year) {
		return isReportingStatusEnabled(year) ? ", fs.reportingStatus " : "";
	}
	
	public static boolean isReportingStatusEnabled(int year) {
		return (year > 2012);
	}
	
}
