	
	var SEARCH_FOR_FACILITY = 1;
	var SEARCH_FOR_USER = 2;
	
	var REPORT_TYPE_FACILITY_BY_USER_ID = 1;
	var REPORT_TYPE_FACILITY_COR_STATE = 2;
	var REPORT_TYPE_FACILITY_INVITATION_INFO = 3;
	var REPORT_TYPE_FACILITY_NOD_INFO = 4;
	var REPORT_TYPE_IDENTIFY_DR_REPLACEMENTS = 5;
	
	// submit the form with the user entered value for Facility Name 
	function handleRequest(param) {
	    jQuery("#selectmsg").remove();
	    jQuery("#results").empty();
	    jQuery("#reqmsg").remove();
	    var frmInput = '';
		
	    var facilityIdVal = document.searchFacility.facilityId.value;
	    // alert("facilityIdVal -->" + facilityIdVal);
	    
	    // facility lookup
	    frmInput += 'facilityId=' + escape(jQuery("#facilityId").val());
	    frmInput += "&" + 'facilityName=' + escape(jQuery("#facilityName").val());
	
	    // user lookup
	    frmInput += "&" +'userName=' + escape(jQuery("#userName").val());
	    frmInput += "&" +'lastName=' + escape(jQuery("#lastName").val());
	    frmInput += "&" +'email=' + escape(jQuery("#email").val());
	    
	    // search type
	    frmInput += "&" +'searchType=' + param;
	    
	    // report type
	    frmInput += "&" +'reportType=' + escape(jQuery("#reportType").val());  
	    
	    // report type
	    frmInput += "&" +'userSelectedItem=' + escape(jQuery("#userSelectedItem").val());  
	    
	    jQuery.ajax({
	        type: "POST",
	        url: '#springUrl("/helpdesk/searchFacility.ajx")',
	        data: param + frmInput,
	        success: handleResponse
	    });
	}
	/*
	 * Manage the response of Ajax calls.
	 */
	function handleResponse(response) {
	    jQuery("#results").html(response);
	}
	
	/**
	 * Generate report based on user selection.
	 * Validate the following rules: 
	 * 		1) 'Facility By User Email' is valid when search type is by user id.
	 * 		2) For all other reports, facility id/name will be primary criteria.
	 */
	function generateReport() {
		// report type.
		var reportTypeVal = escape(jQuery("#searchType").val());
		
		// user has not selected valid report.
		if (reportType == -1) {
			alert("Please select a valid report name.");
			return;
		}
		
		// Validate the following rules
		// 'Facility By User Email' is valid when search type is by user id 
		// For all other reports, facility id/name will be primary criteria. 
		var searchTypeVal = escape(jQuery("#searchType").val());
		
		// For Facility By User Email' report.
		var errMsg = "";
		if (reportType == REPORT_TYPE_FACILITY_BY_USER_ID && searchTypeVal != SEARCH_FOR_USER) {
			errMsg = "Invalid report option. Please select 'Facility By User Email' report.";
			alert(errMsg);
			return;					
		} else if ((reportType == REPORT_TYPE_FACILITY_COR_STATE || reportType == REPORT_TYPE_FACILITY_INVITATION_INFO 
					|| reportType == REPORT_TYPE_FACILITY_NOD_INFO) && searchTypeVal != SEARCH_FOR_FACILITY) {
			// Facility reports
			
			errMsg = "Invalid report option. Please select one of the following reports:";
			errMsg += "\r\n" + "State of a Facility/COR"; 
			errMsg += "\r\n" + "Facility Invitation Info";
			errMsg += "\r\n" + "Facility NOD Info";
			alert(errMsg);
			return;					
		}
		
		
	    jQuery("#selectmsg").remove();
	    jQuery("#results").empty();
	    jQuery("#reqmsg").remove();
	    var frmInput = '';
		
	    var facilityIdVal = document.searchFacility.facilityId.value;
	    // alert("facilityIdVal -->" + facilityIdVal);
	    
	    // facility lookup
	    frmInput += 'facilityId=' + escape(jQuery("#facilityId").val());
	    frmInput += "&" + 'facilityName=' + escape(jQuery("#facilityName").val());
	
	    // user lookup
	    frmInput += "&" +'userName=' + escape(jQuery("#userName").val());
	    frmInput += "&" +'lastName=' + escape(jQuery("#lastName").val());
	    frmInput += "&" +'email=' + escape(jQuery("#email").val());
	    
	    // search type
	    frmInput += "&" +'searchType=' + escape(jQuery("#searchType").val());
	    
	    // report type
	    frmInput += "&" +'reportType=' + escape(jQuery("#reportType").val());                
	    
	    // selected item
	    frmInput += "&" +'userSelectedItem=' + escape(jQuery("#userSelectedItem").val());
	    
	    
	    // submit user selection using Ajax method.
	    jQuery.ajax({
	        type: "POST",
	        url: '#springUrl("/helpdesk/generateReportForUserCriteria.do")',
	        data: frmInput,
	        success: handleResponse
	    });
	    
	}
	
	 /*
	  * Saves the selected item on Facility/User look up.
	  */
	function saveUserSelection(userSelItem) {
		document.searchFacility.userSelectedItem.value = userSelItem;
	}
	