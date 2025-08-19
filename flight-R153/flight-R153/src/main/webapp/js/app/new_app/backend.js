var Backend = {};

Backend.Dropdowns = (function() {


	return {
		/**
		 * this returns a list of county names, used to populate county drop down for a state/year combo
		 */
		loadCountyNames: function(callback) {
			var backendUrl = 'service/getCountiesFromState/' + ReportingYear.get() + Parameters.buildUrl([State]);
			$.getJSON(backendUrl, function (countyData) {
				County.populateUI(countyData);
				callback();
			});
		},
		/**
		 * this returns a list of msa names, used to populate MSA drop down for a state/year combo
		 */
		loadMSANames: function () {
			$.getJSON('service/getMSAsFromState/' + ReportingYear.get() + Parameters.buildUrl([State]), function (msaData) {
				MSA.populateUI(msaData);
			});
		}
	}
}());

Backend.Map = (function(){

	return {
		loadFacilitiesData: function () {
			var flightRequest = generateData(FacOrLoc.get(), State.get(), County.get(), MSA.get(), stateLevel, null, LowE.get(), HighE.get(), null, null, reportingStatus, emissionsType);
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: 'service/facilities',
				dataType: 'json',
				data: flightRequest,
				success: function (facilities) {
					if (DataSource.isNot('O') && !isOnshoreOnly()) {

						//if not US Mainland
						if (State.getCode() != 0) {
							if (MSA.get()) { //COUNTIES
								currentJsonObject = facilities;
								Backend.Shapes.loadForMSA();
								loadFacilities(facilities);
							}
							else {
								Backend.Shapes.loadForCounty();
								loadFacilities(facilities);
							}
						}
						//if US mainland, draw state overlays
						else {
							currentJsonObject = facilities;
							drawStateOverlays(facilities);
						}
					}
					//if not Onshore DS
					else {
						loadFacilities(facilities);
					}
				}
			});
		}
	}

}());

Backend.Bounds = (function() {

	return {
		loadForState : function(){
			$.getJSON('service/getStateBounds/'+State.get(), Map.fit);
		},
		loadForCounty: function() {
			$.getJSON('service/getCountyBounds/'+County.get(), Map.fit);
		},
		loadForMSA: function() {
			$.getJSON('service/getMsaBounds/'+MSA.get(), Map.fit);
		}
	}

}());

Backend.Shapes = (function() {

	return {

		//TODO fill this
		loadForState: function () {
		},
		loadForCounty: function () {
			$.getJSON('service/getCountyShapes/' + State.get(), function (data) {
				$.each(data, function () {
					loadCounty(this, County.get());
				});
			});
		},
		loadForMSA: function () {
			$.getJSON('service/getStateMsaShapes/' + State.get(), function (data) {
				$.each(data, function () {
					loadMsa(this, MSA.get());
				});
			});
		}
	}

}());



function getFacilityMapData(facOrLoc,state,fipsCode,msaCode,lowE,highE, reportingStatus, stateLevel, emissionsType) {
	state = State.get();
	var flightRequest = generateData(facOrLoc, state, fipsCode, msaCode, stateLevel, null, lowE, highE, null, null, reportingStatus,emissionsType);
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: 'service/facilities',
			dataType: 'json',
			data: flightRequest,
			success: function(facilities) {
				if (DataSource.isNot('O') && !isOnshoreOnly()) {

					//if not US Mainland
					if (State.getCode() != 0) {
						if(MSA.get()) { //COUNTIES
							currentJsonObject = facilities;
							Backend.Shapes.loadForMSA();
							loadFacilities(facilities);
						}
						else {
							Backend.Shapes.loadForCounty();
							loadFacilities(facilities);
						}
					}
					//if US mainland, draw state overlays
					else {
						currentJsonObject = facilities;
						drawStateOverlays(facilities);
					}
				}
				//if not Onshore DS
				else {
					loadFacilities(facilities);
				}
			}});
}

function listFacilityForBasin(queryString,basinCode,facId,lowE,highE, cyear, trend, reportingStatus){
	Events.publish('canvas-switch',['list']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 455px; overflow-y:auto; overflow-x:auto;');
	
	var flightRequest = generateData(queryString, null, null, null, null, facId, lowE, highE, cyear, trend, reportingStatus);
	$.ajax({
		type: 'POST', 
		contentType: 'application/json',
		url: 'service/listFacilityForBasin/',
		data: flightRequest,
		dataType: "json", 
		success: function(data) {
			var div = document.getElementById("table-label");
			
			if (trend == "current") {
				div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - " + data.domain + " - Total Reported Emissions by Facility in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			} else {
				div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.domain + " Total Reported Emissions by Facility, by Year in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			}
			
			$("#titleDiv").attr('style', 'display:none');
			initEmitterFacilityListView(data,"Facility");
		}			
		
	});
	
}

function listFacilityForBasinGeo(queryString,basinCode,facId,lowE,highE, cyear, trend, reportingStatus){
	Events.publish('canvas-switch',['list']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 455px; overflow-y:auto; overflow-x:auto;');
	
	var flightRequest = generateData(queryString, null, null, null, null, facId, lowE, highE, cyear, trend, reportingStatus);
	$.ajax({
		type: 'POST', 
		contentType: 'application/json',
		url: 'service/listFacilityForBasinGeo/',
		data: flightRequest,
		dataType: "json", 
		success: function(data) {
			var mode;
			if (data.mode == "BASIN") {
				mode = "Basin";
			} else {
				mode = "Facility";
			}
			var div = document.getElementById("table-label");
			
			div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - Total Reported Emissions by "+mode+"/Sector in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			
			$("#titleDiv").attr('style', 'display:none');
			initEmitterFacilityListView(data,"Facility");
		}			
		
	});
	
}

function listSector(queryString,stateAbbr,fipsCode,msaCode,stateLevel,facId,lowE,highE, cyear, reportingStatus, trend, dataSource, emissionsType){
	Events.publish('canvas-switch',['list']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 455px; overflow-y:auto; overflow-x:auto;');
	var flightRequest = generateData(queryString, stateAbbr, fipsCode, msaCode, stateLevel, facId, lowE, highE, cyear, trend, reportingStatus, emissionsType);
	$.ajax({
	
		type: 'POST', 
		contentType: 'application/json',
		url: 'service/listSector/' + stateAbbr,
		data: flightRequest,
		dataType: "json", 
		success: function(data) {
			var div = document.getElementById("table-label");
			var mode;
			if (data.mode == "STATE") {
				mode = "State";
			} else if (data.mode == "COUNTY") {
				mode = "County";
			} else if (data.mode == "MSA") {
				mode = "Metro Area";
			} else if (data.mode == "TRIBAL LAND") {
				mode = "Tribal Name";
			} 
			else {
				mode = "Facility";
			}
			
			if (dataSource == "E" || dataSource == "L" || dataSource == "P" || dataSource == "B" || dataSource == "T") {
				div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - " + "Total Reported Emissions by " + mode + "/Sector in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			} else
			if (trend == "current" && dataSource != "E") {
				div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - " + data.domain + " - Total Reported GHG Quantity by Facility in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			} else {
				div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.domain + "Total Reported GHG Quantity by Facility, by Year in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
			}
			$("#titleDiv").attr('style', 'display:none');
			if (dataSource == 'E' || dataSource == 'L') {
				if(stateAbbr == "" || stateAbbr == null){
					initEmitterFacilityListView(data,"State");
				} else if(fipsCode == "" || fipsCode == null){
					initEmitterFacilityListView(data,"County");
				} else {
					initEmitterFacilityListView(data,"Facility");
				}
			} else if (dataSource == 'S') {
				initEmitterFacilityListView(data,"Facility");
			}
		}			
		
	});
	
	
	
}

function listGas(queryString,stateAbbr,fipsCode,facId,lowE,highE, reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 455px; overflow-y:auto; overflow-x:auto;');
	jQuery.getJSON("service/listGas/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery()+"&sc="+supplierSector+"&rs="+reportingStatus, function(data) {
		if (dataSource == 'E') {
			if(stateAbbr == "" || stateAbbr == null){
				initEmitterListView(data,"State");
			} else if(fipsCode == "" || fipsCode == null){
				initEmitterListView(data,"County");
			} else {
				initEmitterListView(data,"Facility");
			}
		} else  if (dataSource == 'S') {
			initEmitterFacilityListView(data,"Facility");
		}
	});
}

function listFacility(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,trend,cyr, reportingStatus, dataSource, emissionsType){
	Events.publish('canvas-switch',['list']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 455px; overflow-y:auto; overflow-x:auto;');

	var flightRequest = generateData(queryString, stateAbbr, fipsCode, msaCode, null, facId, lowE, highE, cyear, trend, reportingStatus, emissionsType);

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: 'service/listFacility/' + stateAbbr,
		data: flightRequest,
		dataType: "json",
		success: function(data) {
			var div = document.getElementById("table-label");
			if (trend == "current") {
				if (dataSource == "I") {
					div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - " + data.domain + "Total CO2 Received for Injection by Facility in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
				} else {
					div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.year + " - " + data.domain + "Total Reported Emissions by Facility in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
				}
			} else {
				if (dataSource == "I") {
					div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.domain + "Total CO2 Received for Injection by Facility, by Year in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
				} else {
					div.innerHTML = div.innerHTML + "<div style='line-height:7px'><br></div><div style='margin-left: 5px'>" + data.domain + "Total Reported Emissions by Facility, by Year in <span style='text-transform: capitalize; text-decoration: underline'>" + data.unit + "</span> of CO<sub>2</sub>e</br></div>";
				}
			}
			$("#titleDiv").attr('style', 'display:none');
			if (trend != null && trend == "trend") {
				initEmitterFacilityListView(data,"Facility");
				} else {
					initEmitterFacilityListView(data,"Facility");
				}
			}
		});


}

function chartTrend(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType,sc){
	Events.publish('canvas-switch',['line']);	
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	var sectorName = 'by Sector';
	if($("#dataType").val() == 'O'){
		sectorName = 'from Onshore Oil & Gas Production';
	}
	else if($("#dataType").val() == 'L'){
		sectorName = 'by Local Distribution Companies';
	}
	else if($("#dataType").val() == 'B'){
		sectorName = 'by Onshore Oil & Gas Gathering & Boosting';
	}
	jQuery.getJSON("service/sectorTrend/"+dataSource+"/"+ryear+"/"+stateAbbr+"?q="+queryString+"&lowE="+lowE+"&highE="+highE+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+generateBasinFilter(dataSource)+"&tl="+Parameters.getTribalLand()+"&et="+emissionsType+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery()+"&tr=trend"+"&rs="+reportingStatus + "&sc=" + sc, function(data) {
		var arrYears = data.yearRange;
		var startYr = arrYears[0];
		var endYr = arrYears[arrYears.length-1];
		if (dataSource == 'S') {
			data.title = data.domain+' - GHGs Associated with Products Supplied by Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e (' + startYr + '-' + endYr + ')';
		} else if (dataSource == 'I') {
			data.title = data.domain+' - Direct GHG Emissions'+gasSelectionString()+'Reported by Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e (' + startYr + '-' + endYr + ')';
		} else {
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported '+sectorName+' in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e (' + startYr + '-' + endYr + ')';
		}
		initTrendChart(data);
	});
}

function barSector(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,trend,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barSector/"+dataSource+"/"+ryear+"/"+stateAbbr+"?q="+queryString+"&lowE="+lowE+"&highE="+highE+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+generateBasinFilter(dataSource)+"&tl="+Parameters.getTribalLand()+"&et="+emissionsType+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery()+"&tr="+trend+"&rs="+reportingStatus, function(data) {
		divHeight = data.xAxis.categories.length * 40;
		$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height:'+divHeight+'px;  overflow-x:hidden; overflow-y:hidden;');
		if (dataSource == 'O') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e'; 
		} else {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Sector/Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		}
		if (trend == "current") {
			initBarChart(data);	
		} else {
			initBarChartTrend(data);
		}
	});
}

function barSectorL2(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);	
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%; overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barSectorL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.view == 'SECTOR2') {
			divHeight = data.xAxis.categories.length * 60;
			$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Facility/Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else if (data.view == 'SECTOR3') {
			divHeight = data.xAxis.categories.length * 40;
			$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		}
		initBarChart(data);
	});
}

function barGas(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus) {
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barGas/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&lowE="+lowE+"&highE="+highE+"&fc="+fipsCode+"&mc="+msaCode+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initBarChart(data);
	});
}	  

function barState(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barState/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		divHeight = data.xAxis.categories.length * 40;
		$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
		$("#titleDiv").attr('style', 'display:none');
		data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		initBarChart(data);
	});
}

function barStateL2(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barStateL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.segment == 'Facility') {
			divHeight = data.xAxis.categories.length * 60;
		} else {
			divHeight = data.xAxis.categories.length * 40;			
		}
		$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
		$("#titleDiv").attr('style', 'display:none');
		data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by '+data.segment+' in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		initBarChart(data);
	});
}

function barStateL22(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barStateL3/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		divHeight = data.xAxis.categories.length * 40;
		$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
		$("#titleDiv").attr('style', 'display:none');
		data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		initBarChart(data);
	});
}

function barStateL3(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,ss,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	jQuery.getJSON("service/barStateL3/"+dataSource+"/"+ryear+"?ss="+ss+"&q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		divHeight = data.xAxis.categories.length * 60;
		$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
		$("#titleDiv").attr('style', 'display:none');
		data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by '+data.segment+' in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';		
		initBarChart(data);
	});
}

function barSupplier(queryString){
	Events.publish('canvas-switch',['bar']);
	$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: 100%;  overflow-x:hidden; overflow-y:hidden;');
	if (supplierSector != 0) {
		jQuery.getJSON("service/barSupplier/"+ryear+"?q="+queryString+generateSearchQuery()+"&sc="+supplierSector, function(data) {
			divHeight = data.xAxis.categories.length * 60;
			$("#canvas-vis").attr('style', 'background-color:#fff; min-height: 498px; height: '+divHeight+'px; overflow-x:hidden; overflow-y:hidden;');
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+' - GHGs Associated with Products Supplied by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
			initBarChart(data);
		});
	}
}

function pieSector(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieSector/"+dataSource+"/"+ryear+"/"+stateAbbr+"?q="+queryString+"&lowE="+lowE+"&highE="+highE+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&et="+emissionsType+"&rs="+reportingStatus+generateBasinFilter(dataSource)+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (dataSource == 'O') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Sector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		}
		initPieChart(data);
	});
}

function pieSectorL2(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['pie']);	
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieSectorL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.view == 'SECTOR2') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else if (data.view == 'SECTOR3') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else if (data.view == 'SECTOR4') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		}
		initPieChart(data);
	});
}

function pieSectorL3(ss,queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieSectorL3/"+dataSource+"/"+ryear+"?ss="+ss+"&q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.view == 'SECTOR3') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else if (data.view == 'SECTOR4') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		} else if (data.view == 'BASIN1') {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Basin in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		}
		initPieChart(data);
	});
}

function pieSectorL4(ss,queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus,emissionsType){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieSectorL4/"+dataSource+"/"+ryear+"?ss="+ss+"&q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&tl="+Parameters.getTribalLand()+"&lowE="+lowE+"&highE="+highE+"&et="+emissionsType+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		$("#titleDiv").attr('style', 'display:none');
		data.title = data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
		initPieChart(data);
	});
}

function pieGas(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE, reportingStatus){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieGas/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initPieChart(data);
	});
}

function pieState(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieState/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initPieChart(data);
	});
}

function pieStateL2(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieStateL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initPieChart(data);
	});
}

function pieStateL3(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/pieStateL3/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initPieChart(data);
	});
}

function pieSupplier(queryString){
	Events.publish('canvas-switch',['pie']);
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-x:hidden; overflow-y:hidden;');
	if (supplierSector != 0) {
		jQuery.getJSON("service/pieSupplier/"+ryear+"?q="+queryString+generateSearchQuery()+"&sc="+supplierSector, function(data) {
			$("#titleDiv").attr('style', 'display:none');
			data.title = data.domain+' - GHGs Associated with Products Supplied by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e';
			initPieChart(data);
		});
	}
}

function treeSector(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeSector/"+dataSource+"/"+ryear+"?q="+queryString+"&lowE="+lowE+"&highE="+highE+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&rs="+reportingStatus+generateBasinFilter(dataSource)+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (dataSource == 'O') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		} else {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Sector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		}
		initTreeMap(data);
	});
}

function treeSectorL2(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeSectorL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.view == 'SECTOR2') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Subsector in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		} else if (data.view == 'SECTOR3') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		} else if (data.view == 'SECTOR4') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		}
		initTreeMap(data);
	});
}

function treeSectorL3(ss,queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeSectorL3/"+dataSource+"/"+ryear+"?ss="+ss+"&q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		if (data.view == 'SECTOR3') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		} else if (data.view == 'SECTOR4') {
			$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		}
		initTreeMap(data);
	});
}

function treeSectorL4(ss,queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeSectorL4/"+dataSource+"/"+ryear+"?ss="+ss+"&q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		$('#titleSpan').html(data.domain+'Direct GHG Emissions'+gasSelectionString()+'Reported by State in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');
		initTreeMap(data);
	});
}

function treeGas(queryString,stateAbbr,fipsCode,msaCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeGas/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&mc="+msaCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initTreeMap(data);
	});
}

function treeState(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeState/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initTreeMap(data);
	});
}

function treeStateL2(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeStateL2/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initTreeMap(data);
	});
}

function treeStateL3(queryString,stateAbbr,fipsCode,facId,lowE,highE,reportingStatus){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-y:hidden; overflow-x:hidden;');
	jQuery.getJSON("service/treeStateL3/"+dataSource+"/"+ryear+"?q="+queryString+"&st="+stateAbbr+"&fc="+fipsCode+"&lowE="+lowE+"&highE="+highE+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(data) {
		initTreeMap(data);
	});
}

function treeSupplier(queryString){
	$("#canvas-vis").attr('style', 'background-color:#fff; height: 498px; overflow-x:hidden; overflow-y:hidden;');
	if (supplierSector != 0) {
		jQuery.getJSON("service/treeSupplier/"+ryear+"?q="+queryString+generateSearchQuery()+"&sc="+supplierSector, function(data) {
			$("#titleDiv").attr('style', 'padding: 5px; font-weight: bold');
			$('#titleSpan').html(data.domain+' - GHGs Associated with Products Supplied by Facility in <span style="text-transform: capitalize; text-decoration: underline">'+data.unit+'</span> of CO<sub>2</sub>e');				
			initTreeMap(data);
		});
	}
}

