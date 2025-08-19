/**
 * Created by alabdullahwi on 5/26/2015.
 *
 * helpers are tiny methods that (for now) do not belong anywhere else
 */

function getBrowserWindowWidth() {
	var winW = 630, winH = 460;
	if (document.body && document.body.offsetWidth) {
		winW = document.body.offsetWidth;
	}
	if (document.compatMode == 'CSS1Compat' &&
		document.documentElement &&
		document.documentElement.offsetWidth) {
		winW = document.documentElement.offsetWidth;
	}
	if (window.innerWidth && window.innerHeight) {
		winW = window.innerWidth;
	}
	return winW;
}

function getBrowserWindowHeight() {
	var winH = 460;
	if (document.body && document.body.offsetWidth) {
		winH = document.body.offsetHeight;
	}
	if (document.compatMode == 'CSS1Compat' &&
		document.documentElement &&
		document.documentElement.offsetWidth) {
		winH = document.documentElement.offsetHeight;
	}
	if (window.innerWidth && window.innerHeight) {
		winH = window.innerHeight;
	}
	return winH;
}

function supportsSessionStorage() {
	return window.sessionStorage;
}

function arrayIntersection(x, y) {
	var ret = [];
	for (var i = 0; i < x.length; i++) {
		for (var z = 0; z < y.length; z++) {
			if (x[i].attr('id') == y[z].attr('id')) {
				ret.push(x[i]);
				break;
			}
		}
	}
	return ret;
}

function animationComplete() {
	lastAnimationComplete = true;
}

// ## Heat map
function appendZeros(str) {
	var zero = "0";
	var tmp = "";
	for (var i = 0; i < (2 - str.length); i++) {
		tmp = tmp.concat(zero);
	}
	tmp = tmp.concat(str);
	return tmp;
}

function pickColor(color, minValue, maxValue, value) {
	var hex = "#";
	if (color == "Orange") {
		var r = 245, g = 127, b = 37; //Orange
	} else if (color == "Other") {
		var r = 255, g = 255, b = 255; //White
	}
	var rEnd = 255, gEnd = 255, bEnd = 255; //White
	var rDiff, gDiff, bDiff;
	rDiff = rEnd - r;
	gDiff = gEnd - g;
	bDiff = bEnd - b;
	var percentage = (value - minValue) / (maxValue - minValue);
	r = Math.round(rEnd - (rDiff) * percentage);
	g = Math.round(gEnd - (gDiff) * percentage);
	b = Math.round(bEnd - (bDiff) * percentage);
	if (value == 0) {
		r = 255;
		g = 255;
		b = 255;
	}
	hex = hex.concat(appendZeros(r.toString(16)));
	hex = hex.concat(appendZeros(g.toString(16)));
	hex = hex.concat(appendZeros(b.toString(16)));
	return hex;
}

function transformGeoJsonToArray(str, response) {
	var retv = [];
	while (str.indexOf("</Polygon>") > 0) {
		//First remove all tags and leave just the coordinate data
		var startPos = str.indexOf("<coordinates>") + 13;
		var endPos = str.indexOf("</coordinates>");
		var currentPolygon = str.substring(startPos, endPos);
		//Now dump all the comma seperated text into an array
		startPos = 0;
		endPos = 0;
		var inEnd;
		var tmpStr;
		var transformed = [];
		if (response.getDataTable().getColumnLabel(2) == 'name') {
			for (var i = 0; i < currentPolygon.length; i++) {
				if (currentPolygon.charAt(i) == ' ') {
					endPos = i - 4;
					tmpStr = currentPolygon.substring(startPos, endPos);
					inEnd = tmpStr.indexOf(',');
					transformed.push(tmpStr.substring(0, inEnd));
					transformed.push(tmpStr.substring(inEnd + 1, tmpStr.length));
					startPos = endPos + 5;
				}
				if (i == (currentPolygon.length - 1)) {
					endPos = i - 4;
					tmpStr = currentPolygon.substring(startPos - 1, endPos);
					inEnd = tmpStr.indexOf(',');
					transformed.push(tmpStr.substring(0, inEnd));
					transformed.push(tmpStr.substring(inEnd + 1, tmpStr.length));
					startPos = endPos + 5;
				}
			}
		} else {
			for (var i = 0; i < currentPolygon.length; i++) {
				if (currentPolygon.charAt(i) == ' ') {
					endPos = i;
					tmpStr = currentPolygon.substring(startPos, endPos);
					inEnd = tmpStr.indexOf(',');
					transformed.push(tmpStr.substring(0, inEnd));
					transformed.push(tmpStr.substring(inEnd + 1, tmpStr.length));
					startPos = endPos + 1;
				}
				if (i == (currentPolygon.length - 1)) {
					endPos = i;
					tmpStr = currentPolygon.substring(startPos - 1, endPos);
					inEnd = tmpStr.indexOf(',');
					transformed.push(tmpStr.substring(0, inEnd));
					transformed.push(tmpStr.substring(inEnd + 1, tmpStr.length));
					startPos = endPos + 1;
				}
			}
		}
		startPos = str.indexOf("<Polygon>");
		endPos = str.indexOf("</Polygon>") + 10;
		retv.push(transformed);
		str = str.substring(endPos, str.length);
	}
	return retv;
}

function setCookies() {
	document.cookie = "visited_flight_before=yes";
}

function addLeadingZeros(str) {
	if (str == null) {
		return "";
	}
	str = str + "";
	//Fips need to be 5 digits long
	var zero = "0";
	if (str.length == 4) {
		return zero.concat(str);
	} else {
		return str;
	}
}

function addCommas(nStr) {
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

function sendMail() {
	var link = "mailto:"
		+ "&subject=" + escape("Green House Gas Data Publication Tool")
		+ "&body=" + escape(window.location);
	window.location.href = link;
}

function htmlEntities(str) {
	return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
