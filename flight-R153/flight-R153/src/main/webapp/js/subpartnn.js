function setReporterSpecific(parameter, source) {
	var frm = document.forms[0];
	var defaultValue;
	var isReporterSpecific;
	var enteredValue;
	var standard;
	var days;
	var enterDiv;
	if (parameter == "hhv") {
		defaultValue = frm.defaultHhv;
		isReporterSpecific = document.getElementsByName("hhv.isReporterSpecific");
		enteredValue = document.getElementById("hhv.hhvValue");
		standard = document.getElementById("hhv.standardOfDevelopment");
		days = document.getElementById("hhv.daysSubstituteDataProceduresUsed");
		enterDiv = document.getElementById("overridehhv");
	}
	else if (parameter == "ef1") {
		defaultValue = frm.defaultEf1;
		isReporterSpecific = document.getElementsByName("ef1.isReporterSpecific");
		enteredValue = document.getElementById("ef1.efValue");
		standard = document.getElementById("ef1.standardOfDevelopment");
		days = document.getElementById("ef1.daysSubstituteDataProceduresUsed");
		enterDiv = document.getElementById("overrideef");
	}
	else if (parameter == "ef2" || parameter == "ef3" || parameter == "ef4" || 
			parameter == "ef5" || parameter == "ef7") {
		defaultValue = frm.defaultEf2;
		isReporterSpecific = document.getElementsByName(parameter + ".isReporterSpecific");
		enteredValue = document.getElementById(parameter + ".efValue");
		standard = document.getElementById(parameter + ".standardOfDevelopment");
		days = document.getElementById(parameter + ".daysSubstituteDataProceduresUsed");
		enterDiv = document.getElementById("overrideef");
	}
	if (isReporterSpecific[0].checked) {	// Used default value is checked.
		if (source == 2 || enteredValue.value == "") enteredValue.value = defaultValue.value;
		enteredValue.readOnly = true;
		//standard.value = "";
		//days.value = "";
		enterDiv.style.display = "none";
	}
	else {		// Use reporter specific value is checked.
		enterDiv.style.display = "inline";
		enteredValue.readOnly = false;
		if (source == 2) enteredValue.select();
		// enteredValue.focus();
	}
}

function setOtherStandardDisplay(parameter) {
	var standardDiv;
	var standard;
	var standardOther;
	if (parameter == "fuel") {
		standard = document.getElementById("fuelVolumeNN1_NN2.standardOfMeasure");
		standardOther = document.getElementById("fuelVolumeNN1_NN2.standardOfMeasureOtherDesc");
		standardDiv = document.getElementById("standardfuel");
	}
	else if (parameter == "hhv") {
		standard = document.getElementById("hhv.standardOfDevelopment");
		standardOther = document.getElementById("hhv.standardOfDevelopmentOtherDesc");
		standardDiv = document.getElementById("standardhhv");
	}
	else if (parameter == "ef1") {
		standard = document.getElementById("ef1.standardOfDevelopment");
		standardOther = document.getElementById("ef1.standardOfDevelopmentOtherDesc");
		standardDiv = document.getElementById("standardef");
	}
	else if (parameter == "ef2" || parameter == "ef3" || parameter == "ef4" ||
			parameter == "ef5" || parameter == "ef7") {
		standard = document.getElementById(parameter + ".standardOfDevelopment");
		standardOther = document.getElementById(parameter + ".standardOfDevelopmentOtherDesc");
		standardDiv = document.getElementById("standardef");
	}
	if (standard.value == "Other") {	// Display other standard text box.
		standardDiv.style.display = "inline";
	}
	else {		// Hide other standard text box.
		standardDiv.style.display = "none";
	}
}


function checkOtherStandard(parameter) {
	var standard;
	var standardOther;
	if (parameter == "fuel") {
		standard = document.getElementById("fuelVolumeNN1_NN2.standardOfMeasure");
		standardOther = document.getElementById("fuelVolumeNN1_NN2.standardOfMeasureOtherDesc");
	}
	else if (parameter == "hhv") {
		standard = document.getElementById("hhv.standardOfDevelopment");
		standardOther = document.getElementById("hhv.standardOfDevelopmentOtherDesc");
	}
	else if (parameter == "ef1") {
		standard = document.getElementById("ef1.standardOfDevelopment");
		standardOther = document.getElementById("ef1.standardOfDevelopmentOtherDesc");
	}
	else if (parameter == "ef2" || parameter == "ef3" || parameter == "ef4" || 
			parameter == "ef5" || parameter == "ef7") {
		standard = document.getElementById(parameter + ".standardOfDevelopment");
		standardOther = document.getElementById(parameter + ".standardOfDevelopmentOtherDesc");
	}
	if (standard.value != "Other") standardOther.value = "";
}
