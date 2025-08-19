function formatNumber(value, decimals) {
	if (value == "" || value.substr(0, 7) == "[object") return "";
	else if (isNaN(value)) return value;
	else return ((Number)(value)).toFixed(decimals);
}

function formatNumber(value) {
	if (value == "" || value.substr(0, 7) == "[object") return "";
	else if (isNaN(value)) return value;
	else return ((Number)(value)).toPrecision();
}

function isNumberValid(textbox) {
	if (textbox.value != "" && isNaN(textbox.value)) return false;
	else return true;
}

var startMinute = 15;
var warningMinute = 2;
var minute = startMinute;
var second = 0;
var wait = 1000; // 1000 milliseconds = 1 second

function timer() {
  if (minute == warningMinute && second == 0) {
    //if (confirm("Your session will end in approximately " + warningMinute + 
	// " minutes and any unsaved data will be lost.  Please click OK within the next " + 
	// warningMinute + " minutes to keep your session active.")) {
	window.focus();
	alert("Your session will end in approximately " + warningMinute + 
          " minutes and any unsaved data will be lost.  Please click OK within the next " + 
          warningMinute + " minutes to keep your session active.");
    pingServer();	// Call method to "ping" server.
    minute = startMinute;  // Reset timer minute and second.
    second = 0;
    // }
  }
  if (second == 0) {
    minute -= 1;
    second = 59;
  }
  else second -= 1;
  setTimeout("timer()", wait);
}

function pingServer() {
	jQuery.ajax({
		type: "GET",
		url: "/ghg/datareporting/pingserver.ajx",
		success: handleResponse
	});
}

function handleResponse(response) {
}

jQuery(document).ready(function() { 
	try { initForm(); } catch (ex) { }	// Run page-specific initForm() function if it exists.
	
	jQuery('input.jqdecimal').bind("paste",function(e) {
		e.preventDefault();		// Disables paste in IE to prevent negative values.
		var length = this.value.length;
		var maxlength = 40;
		if (length >= maxlength) {
			this.value = this.value.substring(0, maxlength);
			return false;
		}
	});
	
	jQuery('input.jqdecimal').live("paste",function(e) {
		var length = this.value.length;
		var maxlength = 40;
		if (length >= maxlength) {
			this.value = this.value.substring(0, maxlength);
			return false;
		}
	});

	jQuery('input.jqdecimal').keypress(function (e) {
		//allows only one period and allows only one - sign 
		if ((e.which == 46 && jQuery(this).val().indexOf('.') != -1)) {  
		    return false;
		}
		if (e.which != 8 && e.which != 0 && e.which != 46 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		if (e.which != 8 && e.which != 0 && e.which != 127) {
			var length = this.value.length;
			var maxlength = 40;
			if (length >= maxlength) {
				return false;
			}
		}
	});

	jQuery('input.jqinteger').bind("paste",function(e) {
		e.preventDefault();		// Disables paste in IE to prevent negative values.
		var length = this.value.length;
		var maxlength = 40;
		if (length >= maxlength) {
			this.value = this.value.substring(0, maxlength);
			return false;
		}
	});
	
	jQuery('input.jqinteger').live("paste",function(e) {
		e.preventDefault();
		var length = this.value.length;
		var maxlength = 40;
		if (length >= maxlength) {
			this.value = this.value.substring(0, maxlength);
			return false;
		}
	});
	
	jQuery('input.jqinteger').keypress(function (e) {
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		if (e.which != 8 && e.which != 0 && e.which != 127) {
			var length = this.value.length;
			var maxlength = 40;
			if (length >= maxlength) {
				return false;
			}
		}
	});

	jQuery('a.jqsubmit').live("click", function(event) {
		jQuery("input[name='redirectPage']").val(jQuery(this).attr('href'));	// Sets the value for the hidden field “redirectPage” to the href.
		try { 
			submitForm();	// Calls a page-specific submit function if it exists.
		} 
		catch (ex) { 
			jQuery("form:first").submit(); 
		}
		event.preventDefault();   // Disables the hyperlink.
	});
	
});

timer();	// Start timer.
