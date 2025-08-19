/**
 * 
 */
package gov.epa.ghg.util;

/**
 * Holds all the constants utilized in the application.
 *  
 * @author		dasarisa
 * @created  	02/28/2011
 * 
 */
public interface AppConstants {
	
	// empty string literal
	public static final String EMPTY_VALUE = "";
	
	// Search Utility for Facility and Users
	public static final String SEARCH_FOR_FACILITY = "1";
	public static final String SEARCH_FOR_USER = "2";
	
	public static final String REPORT_DATA_PLACE_HOLDER = "?";
	
	// report types
	public static final int REPORT_TYPE_UNIT_TEST = -2;
	public static final int REPORT_TYPE_FACILITY_BY_USER_ID = 1;
	public static final int REPORT_TYPE_FACILITY_COR_STATE = 2;
	public static final int REPORT_TYPE_FACILITY_INVITATION_INFO = 3;
	public static final int REPORT_TYPE_FACILITY_NOD_INFO = 4;
	public static final int REPORT_TYPE_IDENTIFY_DR_REPLACEMENTS = 5;
	public static final int REPORT_VERIFICATION_TOOL = 6;
	public static final int REPORT_GET_BLOB = 7;
		
	// report names
	public static final String REPORT_NAME_UNIT_TEST = "unit_test";
	public static final String REPORT_NAME_FACILITY_BY_USER_ID = "facility_by_user_id";
	public static final String REPORT_NAME_FACILITY_COR_STATE = "facility_cor_state";
	public static final String REPORT_NAME_FACILITY_INVITATION_INFO = "facility_invitation_info";
	public static final String REPORT_NAME_FACILITY_NOD_INFO = "facility_nod_info";
	public static final String REPORT_NAME_IDENTIFY_DR_REPLACEMENTS = "identify_dr_replacements";
	public static final String REPORT_NAME_VERIFICATION_TOOL_REPORT = "verification_tool_report";
	public static final String REPORT_NAME_GET_BLOB = "get_blob";

	public static final int REPORT_DEFAULT_SELECTED_ITEM_ID = -1;
	
	// report page Size
	public static final int REPORT_PAGE_SIZE = 20;
	
	// search characters
	public static final String SEARCH_WILD_CHAR = "*";
	public static final String SEARCH_SQL_LIKE_CHAR = "%";
	
	// Facility domain column names
	public static final String FAC_FACILITY_ID = "facilityId";
	public static final String FAC_FACILITY_NAME = "username";
	
	// User domain column names
	public static final String USER_USER_NAME = "username";
	public static final String USER_LAST_NAME = "lastName";
	public static final String USER_EMAIL = "email"; 
	public static final String MT = "Metric Tons";
	public static final String MMT = "Million Metric Tons";
}
