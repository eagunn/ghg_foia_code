
/*!
 * This file will have all the Egret Specific common js files
 */
function isEmpty(value) {
	if (undefined == value) {
		return true;
	}
	if (null == value) {
		return true;
	}
	if ("" == value) {
		return true;
	}
	if ("undefined" == value) {
		return true;
	}
	if ("null" == value) {
		return true;
	}
	if ("" == value.replace(/^\s+|\s+$/g, "")) {
		return true;
	}
	return false;
}

function transformPhone( obj ) {
	var val = obj.value.replace( /\D/g, '' );
	if ( /^(\d{3})(\d{3})(\d{4})$/.test( val ) ) {
		obj.value = RegExp.$1 + '-' + RegExp.$2 + '-' + RegExp.$3;
	}
}
 
function validateUSPhone( strValue ) {
	var regExpPattern = /^[0-9\\-]{10,14}/;
	return regExpPattern.test(strValue);
}

function isInternationalPhoneNumber( strValue ) {
	if(!isNaN(strValue) && strValue.length >=10 &&  strValue.length <=15 ) {
		return true;
	} else {
		return false;
	}
}

