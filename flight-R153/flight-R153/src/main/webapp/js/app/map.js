/**
 *
 * Created by alabdullahwi on 5/26/2015.
 */
/**
 * plots state boundaries
 *
 * @param array
 * @param response
 * @param row
 * @param minValue
 * @param maxValue
 */

var OurMap = (function () {
	//future reference to google maps object should be through _self and not global map var
	var _self = undefined;
	var _BubbleOverlay = undefined;
	var currentlyFocusedFacility = {
		id: undefined
		, year: undefined
	}
	var facilityBubbleInfoBox = (function () {
		var createBubbleFacilityInfoBoxCloseButton = function () {
			//create close button
			var closeButton = L.DomUtil.create('button');
			closeButton.style.float = 'right';
			closeButton.setAttribute('class', 'close');
			closeButton.innerText = 'x';
			return closeButton;
		}
		var getSelf = function () {
			return $('#bubbleFacilityInfoBox');
		}
		return {
			display: function (facility) {
				var vHover = 'service/facilityHover/';
				var vSt = "";
				if (DataSource.get() == "T") {
					vHover = 'service/pipeHover/';
					vSt = "&st=" + facility.state;
				}
				jQuery.ajax({
					type: 'GET'
					, url: vHover + ryear
					, data: "id=" + facility.id + "&ds=" + DataSource.get() + "&et=" + EmissionsType.get() + "&containerType=bubble" + vSt
					, success: function (response) {
						currentlyFocusedFacility.id = facility.id;
						currentlyFocusedFacility.year = ryear;
						var _self = getSelf();
						_self.html(response);
						var closeButton = createBubbleFacilityInfoBoxCloseButton();
						$(closeButton).click(function () {
							getSelf().hide();
						});
						_self.prepend(closeButton);
						if (_self.is(':hidden')) {
							_self.show();
						}
					}
				});
			}
			, close: function () {
				getSelf().hide();
			}
		}
	})();
	var facilityHoverTip = (function () {
		var _self = undefined;
		var currentlyFocusedFacility = {
			id: undefined
			, year: undefined
		};
		var retv = {
			init: function () {
				var popupOptions = {
					maxWidth: 480,
					maxHeight: 280,
					offset: [0, -25],
					keepInView: true,
					autoClose: false,
					closeOnEscapeKey: false
				};
				_self = L.popup(popupOptions);
				retv.close = (_self._close).bind(_self);
			}
			, display: function (facility) {
				if (facility.lt != undefined && facility.ln != undefined) {
					var latLng = L.latLng(facility.lt, facility.ln);
					var vHover = 'service/facilityHover/';
					var vSt = "";
					if (DataSource.get() == "T") {
						vHover = 'service/pipeHover/';
						vSt = "&st=" + facility.state;
					}
					if (_self == undefined) {
						this.init();
					}
					jQuery.ajax({
									type: 'GET',
									url: vHover + ReportingYear.get(),
									data: "id=" + facility.id + "&ds=" + DataSource.get() + "&et=" + EmissionsType.get() + "&containerType=hover" + vSt,
									success: function (response) {
										currentlyFocusedFacility.id = facility.id;
										currentlyFocusedFacility.year = ReportingYear.get();
										_self.setLatLng(latLng).setContent(response).openOn(map);
									}
								});
					L.DomEvent.addListener(_self, 'domready', function () {
						L.DomUtil.get('hoverTip').parentNode.style.overflow = '';
					});
				}
			}
			, close: undefined
		};
		return retv;
	})();
	var _Plotter = (function () {
		return {
			//basin
			plotBasin: function (data, basinCode) {
				var color = data.color;
				var shapes = data.shapes;
				jQuery.each(shapes, function () {
					var shell = this.shell;
					var holes = this.holes;
					var shellCoords = [];
					var paths = [];
					jQuery.each(shell, function () {
						if (this.lat != undefined && this.lng != undefined) {
							var latLng = L.latLng(this.lat, this.lng);
							shellCoords.push(latLng);
						}
					});
					jQuery.each(holes, function () {
						hole = this;
						var holeCoords = [];
						jQuery.each(hole, function () {
							if (this.lat != undefined && this.lng != undefined) {
								var latLng = L.latLng(this.lat, this.lng);
								holeCoords.push(latLng);
							}
						});
						paths.push(holeCoords);
					});
					paths.push(shellCoords);
					var poly;
					if (data.id == basinCode) {
						var polygonOptions = {
							color: '#000',
							opacity: .8,
							weight: 3,
							fillColor: color,
							fillOpacity: .4
						}
						poly = L.polygon(paths, polygonOptions);
					} else {
						var polygonOptions = {
							color: '#555',
							opacity: .8,
							weight: 3,
							fillColor: color,
							fillOpacity: .4
						}
						poly = L.polygon(paths, polygonOptions);
					}
					poly.setObject("basinCode", data.id);
					poly.setObject("basinName", data.name);
					poly.bindTooltip(poly.getObject("basinName"), {sticky: true}).addTo(map);
				});
			}
			//counties
			, plotCounties: function (data, fipsCode) {
				var color = data.color;
				var shapes = data.shapes;
				jQuery.each(shapes, function () {
					var shell = this.shell;
					var holes = this.holes;
					var shellCoords = [];
					var paths = [];
					jQuery.each(shell, function () {
						if (this.lat != undefined && this.lng != undefined) {
							var latLng = L.latLng(this.lat, this.lng);
							shellCoords.push(latLng);
						}
					});
					jQuery.each(holes, function () {
						hole = this;
						var holeCoords = [];
						jQuery.each(hole, function () {
							if (this.lat != undefined && this.lng != undefined) {
								var latLng = L.latLng(this.lat, this.lng);
								holeCoords.push(latLng);
							}
						});
						paths.push(holeCoords);
					});
					paths.push(shellCoords);
					var poly;
					if (data.id == fipsCode) {
						var polygonOptions = {
							color: '#FF0000',
							opacity: 1,
							weight: 1,
							fillColor: '#FFFFFF',
							fillOpacity: .5,
							zIndex: 20000
						}
						poly = L.polygon(paths, polygonOptions);
					} else {
						var polygonOptions = {
							color: '#FF0000',
							opacity: .6,
							weight: 1,
							fillColor: '#FFFFFF',
							fillOpacity: .4,
							zIndex: 20000
						}
						poly = L.polygon(paths, polygonOptions);
					}
					poly.setObject("fipsCode", data.id);
					poly.setObject("countyName", data.name);
					poly.bindTooltip(poly.getObject("countyName"), {sticky: true}).addTo(map);
				});
			}
			//msa
			, plotMsas: function (data, msaCode) {
				var color = data.color;
				var shapes = data.shapes;
				jQuery.each(shapes, function () {
					var shell = this.shell;
					var holes = this.holes;
					var shellCoords = [];
					var paths = [];
					jQuery.each(shell, function () {
						if (this.lat != undefined && this.lng != undefined) {
							var latLng = L.latLng(this.lat, this.lng);
							shellCoords.push(latLng);
						}
					});
					jQuery.each(holes, function () {
						hole = this;
						var holeCoords = [];
						jQuery.each(hole, function () {
							if (this.lat != undefined && this.lng != undefined) {
								var latLng = L.latLng(this.lat, this.lng);
								holeCoords.push(latLng);
							}
						});
						paths.push(holeCoords);
					});
					paths.push(shellCoords);
					var poly;
					if (data.id == msaCode) {
						var polygonOptions = {
							color: '#FF0000',
							opacity: 1,
							weight: 1,
							fillColor: '#FFFFFF',
							fillOpacity: .5
						}
						poly = L.polygon(paths, polygonOptions);
					} else {
						var polygonOptions = {
							color: '#FF0000',
							opacity: .6,
							weight: 1,
							fillColor: '#FFFFFF',
							fillOpacity: .4
						}
						poly = L.polygon(paths, polygonOptions);
					}
					poly.setObject("msaCode", data.id);
					poly.setObject("msaName", data.name);
					poly.bindTooltip(poly.getObject("msaName"), {sticky: true}).addTo(map);
				});
			}
			//state
			, plotState: function (array, response, row, minValue, maxValue) {
				var arr = [];
				arr = array;
				var coords = [];
				var color;
				var stateNum;
				var emission = 0;
				var label;
				if (response.getDataTable().getColumnLabel(2) == 'name') {
					stateNum = isState(response.getDataTable().getValue(row, 2));
					label = response.getDataTable().getValue(row, 2);
					jQuery.each(dbData, function (i, item) {
						if (dbData[i].id == stateNum) {
							emission = dbData[i].emission;
						}
					});
					if (mapSelector == 0) {
						color = '#FFFFFF'; // Facility
					} else {
						color = pickColor('Orange', minValue, maxValue, emission); // Heat
					}
				} else {
					stateNum = response.getDataTable().getValue(row, 2);
					label = response.getDataTable().getValue(row, 3);
					jQuery.each(dbData, function (i, item) {
						if (dbData[i].id == stateNum) {
							emission = dbData[i].emission;
						}
					});
					if (mapSelector == 0) {
						color = '#FFFFFF';
					} else {
						color = pickColor('Orange', minValue, maxValue, emission);
					}
				}
				//Now for every pair of values fill a data variable with the polygon coordinates
				coords = [];
				for (var j = 0; j < (arr.length / 2); j++) {
					var num1 = arr[(j * 2)];
					var num2 = arr[(j * 2) + 1];
					if (num1 != undefined && num2 != undefined) {
						num1 = num1.replace(/\,/g,'');
						num2 = num2.replace(/\,/g,'');
						var latLng = L.latLng(num2, num1);
						coords.push(latLng);
					}
				}
				var fOpacity;
				if (mapSelector == 0) {
					fOpacity = .1 // Facility - make the background show
				} else {
					fOpacity = .7 // Heat - make the heat map more opaque
				}
				var polygonOptions = {
					color: '#999',
					opacity: .8,
					weight: 1,
					fillColor: color,
					fillOpacity: fOpacity,
					zIndex: 100
				}
				var poly = L.polygon(coords, polygonOptions);
				poly.setObject("countyNum", response.getDataTable().getValue(row, 2));
				poly.setObject("label", label);
				poly.bindTooltip(poly.getObject("label"), {sticky: true}).addTo(map);
				L.DomEvent.addListener(poly, "click", openInfoWindow);
				/*L.DomEvent.addListener(poly, 'mouseover', function (e) {
					if (map.getZoom() <= 8) {
						L.Util.setOptions(this, {fillOpacity: .7});
					} else {
						L.Util.setOptions(this, {fillOpacity: .3});
					}
				});*/
			}
		}
	})();
	var _Controls = (function () {
		var _LoadingSpinner = {};
		return {
			//will be defined once Google Maps API is loaded
			LoadingSpinner: _LoadingSpinner
			, init: function () {
				var loadingSpinnerController = undefined;
				_LoadingSpinner.show = function () {
					//prevents occurrence of more than one spinner
					if (loadingSpinnerController) {
						return;
					}
					var ajaxLoaderControlDiv = L.DomUtil.create('div');
					// Set CSS for the control border
					var controlUI = L.DomUtil.create('div');
					ajaxLoaderControlDiv.appendChild(controlUI);
					var controlText = L.DomUtil.create('div');
					controlText.innerHTML = '<img src="img/loadingBigTransparent.gif" alt="loading image">';
					controlUI.appendChild(controlText);
					loadingSpinnerController = L.control({position: 'topcenter'});
					loadingSpinnerController.onAdd = function (map) {
						return ajaxLoaderControlDiv;
					}
					loadingSpinnerController.addTo(map);
				}
				_LoadingSpinner.hide = function () {
					map.removeControl(loadingSpinnerController);
					loadingSpinnerController = undefined;
				}
			}
			, addBubbleToggle: function () {
				var createOverlaySelector = function () {
					var overlaySelector = L.DomUtil.create('div');
					var overheadTitle = L.DomUtil.create('div');
					overheadTitle.innerHTML = '<p>Display Facilities</p>';
					overheadTitle.setAttribute('id', 'bubbleToggleOverheadTitle');
					overheadTitle.style.background = '#fff';
					overheadTitle.style.borderRadius = '12px 12px 0px 0px';
					overheadTitle.style.border = '1px solid #dcdcdc';
					overheadTitle.style.width = '109px';
					overheadTitle.style.marginLeft = '33px';
					overheadTitle.style.textAlign = 'center';
					overheadTitle.style.fontWeight = 'bold';
					var facilityBtn = L.DomUtil.create('div');
					facilityBtn.setAttribute('class', 'softBtnLeftMap');
					facilityBtn.setAttribute('id', 'facilityBtnMap');
					facilityBtn.innerHTML = '<a href="javascript:void(0)" title="One-size">One-size</a>';
					facilityBtn.style.borderRightStyle = 'solid';
					facilityBtn.style.borderRightWidth = '1px';
					facilityBtn.style.borderRightColor = '#CCC';
					facilityBtn.style.fontSize = '9px';
					facilityBtn.style.width = "70px";
					var bubbleBtn = L.DomUtil.create('div');
					bubbleBtn.setAttribute('class', 'softBtnRightMap');
					bubbleBtn.setAttribute('id', 'bubbleBtnMap');
					bubbleBtn.style.marginRight = '15px';
					bubbleBtn.innerHTML = '<a href="javascript:void(0)" title="Relative to Emissions">Relative to Emissions</a>';
					bubbleBtn.style.fontSize = '9px';
					bubbleBtn.style.width = '100px';
					var bubbleWhatsThis = L.DomUtil.create('div');
					bubbleWhatsThis.innerHTML = '<a target="_blank" href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=141983784" title="What&apos;s this?">What&apos;s this?</a>';
					bubbleWhatsThis.setAttribute('id', 'bubbleWhatsThis');
					bubbleWhatsThis.style.marginLeft = '52px';
					bubbleWhatsThis.style.width = '80px';
					bubbleWhatsThis.style.border = '1px solid #dcdcdc';
					bubbleWhatsThis.style.background = '#fff';
					bubbleWhatsThis.style.textAlign = 'center';
					bubbleWhatsThis.style.borderRadius = '0px 0px 11px 11px';
					if ($('#selectedOverlay').val() == 0) {
						facilityBtn.removeAttribute('class');
						facilityBtn.setAttribute('class', 'softBtnLeftOnMap');
						bubbleBtn.removeAttribute('class');
						bubbleBtn.setAttribute('class', 'softBtnRightMap');
					} else {
						facilityBtn.removeAttribute('class');
						facilityBtn.setAttribute('class', 'softBtnLeftMap');
						bubbleBtn.removeAttribute('class');
						bubbleBtn.setAttribute('class', 'softBtnRightOnMap');
					}
					overlaySelector.style.padding = '4px';
					overlaySelector.style.paddingLeft = '80px';
					overlaySelector.style.width = '350px';
					L.DomEvent.addListener(facilityBtn, 'click', function () {
						setOverlaySelector(0);
						var $this = $(this);
						$(this).removeClass().addClass('softBtnLeftOnMap');
						$(bubbleBtn).removeClass().addClass('softBtnRightMap');
						$('#emissionRangePopover').hide();
						OurMap.Overlay.clear();
						generateURL('');
					});
					L.DomEvent.addListener(bubbleBtn, 'click', function () {
						OurMap.Controls.LoadingSpinner.show();
						setOverlaySelector(1);
						var $this = $(this);
						$(this).removeClass().addClass('softBtnRightOnMap');
						$(facilityBtn).removeClass().addClass('softBtnLeftMap');
						OurMap.Overlay.clear();
						generateURL('');
					});
					overlaySelector.appendChild(overheadTitle);
					overlaySelector.appendChild(facilityBtn);
					overlaySelector.appendChild(bubbleBtn);
					overlaySelector.appendChild(bubbleWhatsThis);
					return overlaySelector;
				}
				var createBubbleSlider = function () {
					var bubbleFacilityInfoBox = L.DomUtil.create('div');
					bubbleFacilityInfoBox.setAttribute('id', 'bubbleFacilityInfoBox');
					bubbleFacilityInfoBox.setAttribute('class', 'mPopover');
					bubbleFacilityInfoBox.style.zIndex = 1;
					bubbleFacilityInfoBox.style.width = '300px';
					bubbleFacilityInfoBox.style.right = '117px';
					bubbleFacilityInfoBox.style.top = '15px';
					$(bubbleFacilityInfoBox).hide();
				};
				var createBubbleFacilityInfoBox = function () {
					var bubbleFacilityInfoBox = L.DomUtil.create('div');
					bubbleFacilityInfoBox.setAttribute('id', 'bubbleFacilityInfoBox');
					bubbleFacilityInfoBox.setAttribute('class', 'mPopover');
					bubbleFacilityInfoBox.style.zIndex = 1;
					bubbleFacilityInfoBox.style.marginTop = '190px';
					bubbleFacilityInfoBox.style.width = '300px';
					bubbleFacilityInfoBox.style.right = '117px';
					bubbleFacilityInfoBox.style.top = '15px';
					$(bubbleFacilityInfoBox).hide();
					return bubbleFacilityInfoBox;
				}
				var overlaySelector = createOverlaySelector();
				var bubbleFacilityInfoBox = createBubbleFacilityInfoBox();
				var overlaySelectorControl = L.control({position: 'topleft'});
				overlaySelectorControl.onAdd = function (map) {
					return overlaySelector;
				};
				overlaySelectorControl.addTo(map);
//				map.controls.get('topleft').push(overlaySelectorControl);
				var bubbleFacilityInfoBoxControl = L.control({position: 'topright'});
				bubbleFacilityInfoBoxControl.onAdd = function (map) {
					return bubbleFacilityInfoBox;
				}
				bubbleFacilityInfoBoxControl.addTo(map);
//				map.controls.get('topright').push(bubbleFacilityInfoBoxControl);
			}
		}
	})();
	var _Overlay = (function () {
		var bubbleMap = undefined;
		
		function facilityMarkerOverlay(facilities) {
			var stylesArr = resolveMarkerClusterStyle(reportingStatus);
			var markerClusterGroupOptions = {
				maxClusterRadius: 50,
				iconCreateFunction: function (cluster) {
					var childCount = cluster.getChildCount();
					var divIconOptions = {
						html: childCount,
						className: 'marker-cluster-style',
						iconSize: L.point(stylesArr[0].height, stylesArr[0].width)
					}
					return L.divIcon(divIconOptions);
				},
				spiderLegPolylineOptions: {weight: 1, color: '#FFFAFA', opacity: 1},
				showCoverageOnHover: false,
				zoomToBoundsOnClick: true,
				spiderfyOnMaxZoom: false,
				removeOutsideVisibleBounds: false
				// polygonOptions: {
				// 	color: '#FFFAFA',
				// 	opacity: 1,
				// 	weight: 1,
				// 	fillColor: '#FFFAFA',
				// 	fillOpacity: .5
				// }
			}
			markerCluster = L.markerClusterGroup(markerClusterGroupOptions);
//			markerCluster = L.markerClusterGroup.layerSupport(markerClusterGroupOptions);
			jQuery.each(facilities, function () {
				var facility = this;
				if (this.lt == undefined || this.ln == undefined) {
					console.log("facility is: " + facility);
					console.log("lt is: " + this.lt);
					console.log("ln is: " + this.ln);
				}
				if (this.lt != undefined && this.ln != undefined) {
					if (this.sa == null) {
						var dType = "factory";
						var color = "black";
						if (dataSource == 'I') {
							dType = "co2"
						} // CO2 Injection (UU)
						if (dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) {
							dType = "derrick"
						} // Onshore Oil & Gas Production, Onshore Oil & Gas Gathering & Boosting
						if (dataSource == 'F') {
							dType = "electricity"
						} // SF6 from Elect. Dist. Systems
						if (dataSource == 'S') {
							dType = "fuel"
						} // Suppliers
						if (dataSource == 'L' || isLDCOnly() || dataSource == 'T' || isPipeOnly()) {
							dType = "pipeline"
						} // Local Distribution Companies, Onshore Gas Transmission Pipelines
						if (dataSource == 'A') {
							dType = "sequestration"
						} // Geologic Sequestration of CO2 (RR)
						if (this.reportingStatus != null) {
							if (this.reportingStatus == 'POTENTIAL_DATA_QUALITY_ISSUE'
								|| this.reportingStatus == 'NOT_VERIFIED_SUBMITTED_LATE') {
								color = "orange"
							}
							if (this.reportingStatus == 'STOPPED_REPORTING_UNKNOWN_REASON'
								|| this.reportingStatus == 'IS_VERIFIED_SUBMITTED_LATE') {
								color = "red"
							}
							if (this.reportingStatus == 'STOPPED_REPORTING_VALID_REASON') {
								color = "gray"
							}
						}
						var latLng = L.latLng(this.lt, this.ln);
						var marker = L.marker(latLng, {
							icon: L.icon({
								iconUrl: 'img/icon_marker/' + dType + '_' + color + '.png'
							}),
							alt: 'facilityId ' + facility.id
						});
						L.DomEvent.addListener(marker, 'click', function () {
							displayFacilityDetail(facility.id);
						});
						L.DomEvent.addListener(marker, 'mouseover', function () {
							facilityHoverTip.display(facility);
						});
						L.DomEvent.addListener(marker, 'mouseout', function () {
							facilityHoverTip.close();
						});
						markerCluster.addLayer(marker);
					} else {
						var id = this.sa.id;
						var color = this.sa.color;
						var shapes = this.sa.shapes;
						jQuery.each(shapes, function () {
							var shell = this.shell;
							var holes = this.holes;
							var shellCoords = [];
							var paths = [];
							jQuery.each(shell, function () {
								var latLng = L.latLng(this.lat, this.lng);
								shellCoords.push(latLng);
							});
							jQuery.each(holes, function () {
								hole = this;
								var holeCoords = [];
								jQuery.each(hole, function () {
									var latLng = L.latLng(this.lat, this.lng);
									holeCoords.push(latLng);
								});
								paths.push(holeCoords);
							});
							paths.push(shellCoords);
							var polygonOptions = {
								color: '#000000',
								opacity: 1,
								weight: 1,
								fillColor: color,
								fillOpacity: .5
							}
							var poly = L.polygon(paths, polygonOptions).addTo(map);
							L.DomEvent.addListener(poly, 'click', function () {
								displayFacilityDetail(facility.id);
							});
							L.DomEvent.addListener(poly, 'mouseover', function () {
								facilityHoverTip.display(facility);
							});
							L.DomEvent.addListener(poly, 'mouseout', function () {
								L.Util.setOptions(this, {fillOpacity: .5});
								facilityHoverTip.close();
							});
						});
					}
				}
			});
			map.addLayer(markerCluster);
			OurMap.Controls.LoadingSpinner.hide();
		}
		
		function bubbleOverlay(data) {
			var facilities = data;
			var state = State.get();
			var fipsCode = County.get();
			var msaCode = MSA.get();
			if (dataSource != 'O' && !isOnshoreOnly() && dataSource != 'B' && !isBoostingOnly()) {
				if (num != 0) { //COUNTIES
					if (stateLevel == 0) {
						var url = "service/getCountyShapes/" + state;
						jQuery.getJSON(url, function (data) {
							jQuery.each(data, function () {
								OurMap.Plotter.plotCounties(this, fipsCode);
							});
							bubbleMap = new _BubbleOverlay(facilities);
							bubbleMap.onAdd();
						});
					} else {
						var url = "service/getStateMsaShapes/" + state;
						jQuery.getJSON(url, function (data) {
							jQuery.each(data, function () {
								OurMap.Plotter.plotMsas(this, msaCode);
							});
							bubbleMap = new _BubbleOverlay(facilities);
							bubbleMap.onAdd();
						});
					}
				} else {//STATES
					currentJsonObject = facilities;
					dbData = facilities;
					//load states from local JSON file instead from webservice
					jQuery.ajax({
						type: 'GET',
						url: 'service/getStateBounds/local',
						success: function (response) {
							response.getDataTable = function () {
								return response;
							};
							response.getNumberOfRows = function () {
								return response.length;
							};
							response.getValue = function (x, y) {
								return response[x].c[y].v;
							};
							response.getColumnLabel = function (x) {
								if (x == 0) {
									return "geometry";
								}
								if (x == 1) {
									return "value";
								}
								if (x == 2) {
									return "name";
								} else {
									return "Error: should be in the range of [0...2]";
								}
							};
							var numRows = response.getDataTable().getNumberOfRows();
							var resultArr = [];
							for (var i = 0; i < numRows; i++) {
								var str = response.getDataTable().getValue(i, 0);
								var retv = transformGeoJsonToArray(str, response);
								resultArr.push(retv);
							}
							for (var i = 0; i < resultArr.length; i++) {
								var currentArr = resultArr[i];
								for (var j = 0; j < currentArr.length; j++) {
									OurMap.Plotter.plotState(currentArr[j], response, i, 0, 0);
								}
							}
							bubbleMap = new _BubbleOverlay(facilities);
							bubbleMap.onAdd();
						}
					});
				}
			} else {
				bubbleMap = new _BubbleOverlay(facilities);
				bubbleMap.onAdd();
			}
		}
		
		return {
			setMarkers: facilityMarkerOverlay
			, setBubbles: bubbleOverlay
			, clear: function () {
				if (markerCluster) {
					markerCluster.clearLayers();
					markerCluster = undefined;
				}
				if (bubbleMap) {
					bubbleMap.onRemove();
					bubbleMap = undefined;
				}
			}
		}
	})();
	return {
		Controls: _Controls
		, Plotter: _Plotter
		, Overlay: _Overlay
		//once the Google Maps is loaded and its classes become available, init our custom types
		, init: function () {
			_Controls.init();
			_BubbleOverlay = L.Circle.extend({
				_data: undefined,
				_bubbles: undefined,
				_options: undefined,
				_totalSize: undefined,
				_max: undefined,
				initialize: function (facilities, options) {
					_bubbles = [];
					_options = [];
					if (options) {
						L.Util.setOptions(this, options);
					} else {
						_options = {
							radiusForPercentage: 200
							, text: {
								visible: false
								, minimumZoom: 13
								, maximumZoom: 15
							}
							, bubble: {
								fill: {
									color: "#FFA400"
									, opacity: 0.6
								}
								, stroke: {
									color: "black"
									, weight: 1
									, opacity: 0.6
								}
							}
						}
						L.Util.setOptions(this, _options);
					}
					_data = _.orderBy(facilities, 'emissions', 'desc');
					_totalSize = this._getTotalEmissions(_data);
					_max = _.maxBy(_data, 'emissions').emissions;
				},
				onAdd: function () {
					for (var i = 0; i < _data.length; i++) {
						_bubbles.push(this._drawBubble(_max, _options, _data[i], i));
					}
				},
				onRemove: function () {
					_bubbles.forEach(function (bubble) {
						try {
//							L.DomEvent.removeListener(bubble, 'click');
//							L.DomEvent.removeListener(bubble, 'mouseover');
//							L.DomEvent.removeListener(bubble, 'mouseout');
							bubble.removeFrom(map);
						} catch (e) {
							console.error("failed to clear _bubbles: " + bubble + e);
						}
					})
					_bubbles = undefined;
				},
				_getTotalEmissions: function (data) {
					var totalEmissions = 0;
					for (var i = 0; i < data.length; i++) {
						totalEmissions += data[i].emissions;
					}
					return totalEmissions;
				},
				_drawBubble: function (totalEmissions, options, facility, ind) {
					var minVal = 50;
					var facilityEmissions = facility.emissions;
					if (facilityEmissions === null || facilityEmissions === undefined) {
						facilityEmissions = 0;
					}
					var radiusForLocation = Math.max(1000, 24000 * (Math.sqrt(facilityEmissions / totalEmissions)));
					var bubbleOptions = {
						fillColor: options.bubble.fill.color
						, fillOpacity: options.bubble.fill.opacity
						, color: options.bubble.stroke.color
						, weight: options.bubble.stroke.weight
						, opacity: options.bubble.stroke.opacity
						, radius: radiusForLocation
						, zIndex: 21000 + ind
					}
					var bubble = undefined;
					if (facility.lt != undefined && facility.ln != undefined) {
						bubble = L.circle(L.latLng(facility.lt, facility.ln), bubbleOptions).addTo(map);
					}
					if (bubble != undefined) {
						L.DomEvent.addListener(bubble, 'click', function () {
							displayFacilityDetail(facility.id);
						});
						L.DomEvent.addListener(bubble, 'mouseover', function () {
							facilityBubbleInfoBox.display(facility);
							L.Util.setOptions(this, {
								fillColor: 'lightblue'
								, strokeWeight: 1.5
							});
						});
						L.DomEvent.addListener(bubble, 'mouseout', function () {
							facilityBubbleInfoBox.close();
							L.Util.setOptions(this, {
								fillColor: 'green'
								, strokeWeight: 1
							});
						});
					}
					return bubble;
				}
			});
		}
	}
})();

function drawMap(facOrLoc, stateIn, fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId) {
	if (map != undefined) {
		map.off();
		map.remove();
	}
	var usZoom = 4;
	var mxZoom = 16;
	num = isState(unescape((stateIn)));
	var location = L.latLng(39, -97);
	var container = L.DomUtil.get('canvas-map');
	if (container != null) {
		container._leaflet_id = null;
		var openStreetMapMapnik = L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
			attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
			minZoom: usZoom,
			maxZoom: mxZoom
		});
		var EsriWorldImagery = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
			attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community',
			minZoom: usZoom,
			maxZoom: mxZoom
		});	
		var ejZoom = 10;
		var colorDemo = new Array();
		colorDemo[0] = 'transparent';
		colorDemo[5] = '#FFFFFF';
		colorDemo[6] = '#FFF6BD';
		colorDemo[7] = '#FFF6BD';
		colorDemo[8] = '#FFF6BD';
		colorDemo[9] = '#FCD777';
		colorDemo[10] = '#FC8D3D';
		colorDemo[11] = '#BD0029';
		var ejLayer = L.esri.featureLayer({
		    url: 'https://ejscreen.epa.gov/arcgis/rest/services/ejscreen/socioeconomic_indicators_state_2024_public/MapServer/0',
			minZoom: ejZoom,
			maxZoom: mxZoom,
		    style: function (ftr) {
		    	var idx = ftr.properties.B_DEMOGIDX_2;
		      	var cl;
		      	if (idx !="" && idx <= 5) {
		          	cl = colorDemo[5];
		      	} else {
		          	cl = colorDemo[idx];
		      	}
		   		return { fillColor: cl, fillOpacity: 0.5, color: colorDemo[0], weight: 0.5 };
		   	}
		});
		ejLayer.bindPopup(function (lyr) {
		    return L.Util.template('<b>ID: {ID}</b><br/>ST_ABBREV: {ST_ABBREV}<br/>% people of color: {T_PEOPCOLORPCT}<br/>% low income: {T_LOWINCPCT}<br/>Demographic Index: {T_DEMOGIDX_2}', lyr.feature.properties);
		});
		var fedLayer = L.esri.dynamicMapLayer({
		    url: 'https://geopub.epa.gov/arcgis/rest/services/NEPAssist/Boundaries/MapServer',
		    minZoom: usZoom,
			maxZoom: mxZoom,
		    layers: [4],
		    f: 'image',
		    opacity: 0.5
        });
		overlayMaps = {
			"Demographic Index": ejLayer,
			"National Map Federal Lands": fedLayer
		};
		var mapOptions = {
			attributionControl: false,
			zoomControl: true,
			tap: false,
			center: location,
			zoom: usZoom,
			fullscreenControl: true,
			fullscreenControlOptions: {
				position: 'topright',
				title: 'View Fullscreen',
				titleCancel: 'Exit Fullscreen'
			},
			layers: [openStreetMapMapnik]
		}
		map = new L.Map(L.DomUtil.get('canvas-map'), mapOptions);
		var baseMaps = {
			"Roadmap": openStreetMapMapnik,
			"Satellite": EsriWorldImagery
		};	
		map.on('zoomend', function() {
			var zoomlevel = map.getZoom();
		    if (zoomlevel < ejZoom){
		        if (map.hasLayer(ejLayer)) {
		            map.removeLayer(ejLayer);
		        }
		    }
		    if (zoomlevel >= ejZoom){
		        if (!map.hasLayer(ejLayer)){
		            L.Util.setOptions(map, {layers: [openStreetMapMapnik,ejLayer]});
		        }
		    }
			console.log("Current Zoom Level =" + zoomlevel);
		});
		L.control.layers(baseMaps, overlayMaps, {position: 'topleft', collapsed: false }).addTo(map);
		L.control.zoomLabel({position: 'bottomleft'}).addTo(map);
	}
	if (stateIn == "Tribal Land") {
		map.panTo(location);
		map.setZoom(usZoom);
	} else if (stateIn != "") {
		var stateCode = stateToAbbreviation(stateIn);
		if (stateCode == 'MA') {
			showPopupState(stateCode);
		}
		var url = "service/getStateBounds/" + stateCode;
		if (fipsCode != "" && msaCode == "") {
			url = "service/getCountyBounds/" + fipsCode;
		}
		if (msaCode != "" && fipsCode == "") {
			url = "service/getMsaBounds/" + msaCode;
		}
		jQuery.getJSON(url, function (data) {
			var corner1 = L.latLng(data.sw.lat, data.sw.lng);
			var corner2 = L.latLng(data.ne.lat, data.ne.lng);
			map.fitBounds([corner1, corner2]);
		});
	} else {
		map.panTo(location);
		map.setZoom(usZoom);
	}
	var basinCode = $('#basin').val();
	if (dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) {
		jQuery.getJSON("service/basinsGeo/", function (data) {
			jQuery.each(data, function () {
				OurMap.Plotter.plotBasin(this, basinCode);
			});
		});
	}
	if ((dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) && basinCode != undefined && basinCode != null && basinCode != '') {
		var url = "service/getBasinBounds/" + basinCode;
		jQuery.getJSON(url, function (data) {
			var corner1 = L.latLng(data.sw.lat, data.sw.lng);
			var corner2 = L.latLng(data.ne.lat, data.ne.lng);
			map.fitBounds([corner1, corner2]);
		});
	}
	OurMap.Controls.LoadingSpinner.show();
	// Create a div to hold the control.
	var usaControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	usaControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to U.S. Mainland';
	usaControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'US') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'U.S. Mainland';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var alaskaControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	alaskaControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Alaska';
	alaskaControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'AK') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Alaska';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var hawaiiControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	hawaiiControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Hawaii';
	hawaiiControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'HI') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Hawaii';
	controlUI.appendChild(controlText);
	if (dataSource == 'E') {
		// Create a div to hold the control.
		var tribalControlDiv = L.DomUtil.create('div');
		// Set CSS styles for the DIV containing the control
		// Setting padding to 5 px will offset the control
		// from the edge of the map
		tribalControlDiv.style.padding = '0px 0px 1px';
		// Set CSS for the control border
		var controlUI = L.DomUtil.create('div');
		controlUI.style.width = '100px';
		controlUI.style.backgroundColor = 'white';
		controlUI.style.borderStyle = 'solid';
		controlUI.style.borderWidth = '1px';
		controlUI.style.borderColor = '#777';
		controlUI.style.cursor = 'pointer';
		controlUI.style.textAlign = 'center';
		controlUI.title = 'Click to set the map to Tribal Land';
		tribalControlDiv.appendChild(controlUI);
		// Set CSS for the control interior
		var controlText = L.DomUtil.create('div');
		controlText.style.fontFamily = 'Arial,sans-serif';
		controlText.style.fontSize = '11px';
		controlText.style.color = '#555';
		if (state == 'TL') {
			controlText.style.backgroundColor = '#DDDDDD';
		}
		controlText.style.paddingLeft = '4px';
		controlText.style.paddingRight = '4px';
		controlText.innerHTML = 'Tribal Land';
		controlUI.appendChild(controlText);
	}
	// Create a div to hold the control.
	var samoaControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	samoaControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to American Samoa';
	samoaControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'AS') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'American Samoa';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var marianaControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	marianaControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Mariana Islands';
	marianaControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'MP') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Mariana Islands';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var guamControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	guamControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Guam';
	guamControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'GU') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Guam';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var prControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	prControlDiv.style.padding = '0px 0px 1px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Puerto Rico';
	prControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'PR') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Puerto Rico';
	controlUI.appendChild(controlText);
	// Create a div to hold the control.
	var viControlDiv = L.DomUtil.create('div');
	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	//viControlDiv.style.padding = '0px';
	// Set CSS for the control border
	var controlUI = L.DomUtil.create('div');
	controlUI.style.width = '100px';
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '1px';
	controlUI.style.borderColor = '#777';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Virgin Islands';
	viControlDiv.appendChild(controlUI);
	// Set CSS for the control interior
	var controlText = L.DomUtil.create('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '11px';
	controlText.style.color = '#555';
	if (state == 'VI') {
		controlText.style.backgroundColor = '#DDDDDD';
	}
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Virgin Islands';
	controlUI.appendChild(controlText);
	//if not at national level, show bubble toggle
	if (State.isStateLevel()) {
		OurMap.Controls.addBubbleToggle();
	}
	if ((dataSource == 'E' || dataSource == 'I' || dataSource == 'P' || dataSource == 'A') && stateIn != "" && stateIn != "Tribal Land") {
		var stateLevelSelector = L.DomUtil.create('div');
		var countyBtn = L.DomUtil.create('div');
		countyBtn.setAttribute('class', 'softBtnLeftMap');
		countyBtn.setAttribute('id', 'countyBtnMap');
		countyBtn.style.borderRightStyle = 'solid';
		countyBtn.style.borderRightWidth = '1px';
		countyBtn.style.borderRightColor = '#CCC';
		countyBtn.innerHTML = '<a href="javascript:setStateLevel(ryear, stateAbbr, 0, 1)" title="County">County</a>';
		var msaBtn = L.DomUtil.create('div');
		msaBtn.setAttribute('class', 'softBtnRightMap');
		msaBtn.setAttribute('id', 'msaBtnMap');
		msaBtn.style.marginRight = '15px';
		msaBtn.innerHTML = '<a href="javascript:setStateLevel(ryear, stateAbbr, 1, 1)" title="Metro Area">Metro Area</a>';
		if ($("#selectedLevel").val() == 0) {
			countyBtn.removeAttribute('class');
			countyBtn.setAttribute('class', 'softBtnLeftOnMap');
			msaBtn.removeAttribute('class');
			msaBtn.setAttribute('class', 'softBtnRightMap');
		} else {
			countyBtn.removeAttribute('class');
			countyBtn.setAttribute('class', 'softBtnLeftMap');
			msaBtn.removeAttribute('class');
			msaBtn.setAttribute('class', 'softBtnRightOnMap');
		}
		stateLevelSelector.appendChild(countyBtn);
		stateLevelSelector.appendChild(msaBtn);
		stateLevelSelector.style.padding = '4px';
		stateLevelSelector.style.paddingLeft = '80px';
		var stateLevelSelectorControl = L.control({position: 'topleft'});
		stateLevelSelectorControl.onAdd = function (map) {
			return stateLevelSelector;
		}
		stateLevelSelectorControl.addTo(map);
	}
	L.DomEvent.addListener(usaControlDiv, 'click', function () {
		state = 'US';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState(''), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	L.DomEvent.addListener(alaskaControlDiv, 'click', function () {
		state = 'AK';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('AK');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('AK'), fipsCode, msaCode, lowE, highE, reportingStatus), tribalLandId;
		}
	});
	L.DomEvent.addListener(hawaiiControlDiv, 'click', function () {
		state = 'HI';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('HI');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('HI'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	if (dataSource == 'E') {
		L.DomEvent.addListener(tribalControlDiv, 'click', function () {
			state = 'TL';
			if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
				$("#parentState").val('TL');
				generateURL('');
			} else {
				drawMap(facOrLoc, abbreviationToState('TL'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
			}
		});
	}
	L.DomEvent.addListener(samoaControlDiv, 'click', function () {
		state = 'AS';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('AS');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('AS'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	L.DomEvent.addListener(marianaControlDiv, 'click', function () {
		state = 'MP';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('MP');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('MP'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	L.DomEvent.addListener(guamControlDiv, 'click', function () {
		state = 'GU';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('GU');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('GU'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	L.DomEvent.addListener(prControlDiv, 'click', function () {
		state = 'PR';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('PR');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('PR'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	L.DomEvent.addListener(viControlDiv, 'click', function () {
		state = 'VI';
		if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A') {
			$("#parentState").val('VI');
			generateURL('');
		} else {
			drawMap(facOrLoc, abbreviationToState('VI'), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	});
	var viControlDivControl = L.control({position: 'bottomright'});
	viControlDivControl.onAdd = function (map) {
		return viControlDiv;
	}
	viControlDivControl.addTo(map);
	var prControlDivControl = L.control({position: 'bottomright'});
	prControlDivControl.onAdd = function (map) {
		return prControlDiv;
	}
	prControlDivControl.addTo(map);
	var guamControlDivControl = L.control({position: 'bottomright'});
	guamControlDivControl.onAdd = function (map) {
		return guamControlDiv;
	}
	guamControlDivControl.addTo(map);
	var marianaControlDivControl = L.control({position: 'bottomright'});
	marianaControlDivControl.onAdd = function (map) {
		return marianaControlDiv;
	}
	marianaControlDivControl.addTo(map);
	var samoaControlDivControl = L.control({position: 'bottomright'});
	samoaControlDivControl.onAdd = function (map) {
		return samoaControlDiv;
	}
	samoaControlDivControl.addTo(map);
	if (dataSource == 'E') {
		var tribalControlDivControl = L.control({position: 'bottomright'});
		tribalControlDivControl.onAdd = function (map) {
			return tribalControlDiv;
		}
		tribalControlDivControl.addTo(map);
	}
	var hawaiiControlDivControl = L.control({position: 'bottomright'});
	hawaiiControlDivControl.onAdd = function (map) {
		return hawaiiControlDiv;
	}
	hawaiiControlDivControl.addTo(map);
	var alaskaControlDivControl = L.control({position: 'bottomright'});
	alaskaControlDivControl.onAdd = function (map) {
		return alaskaControlDiv;
	}
	alaskaControlDivControl.onAdd(map);
	var usaControlDivControl = L.control({position: 'bottomright'});
	usaControlDivControl.onAdd = function (map) {
		return usaControlDiv;
	}
	usaControlDivControl.addTo(map);
	if (overlayLevel == 0) {
		getFacilityMapData(stateIn, fipsCode, msaCode, stateLevel);
	} else {
		getMapOverlayData('bubble');
	}
}

function onloadDrawMap() {
//	stateAbbr = $("#parentState").val();
//	var facOrLoc = $("#facOrLocInput").val();
//	if (unescape(facOrLoc) == "Find a Facility or Location") {
//		facOrLoc = "";
//	}
//	var fipsCode = addLeadingZeros($("#countyState").val());
//	var msaCode = "";
//	var tribalLandId = "";
//	lowE = $("#lowEmissionRange").val();
//	highE = $("#highEmissionRange").val();
//	var urlString = window.location + "";
//	if (urlString.indexOf("/facilityDetail/") != -1) {
//		getFacilityInfo();
//	} else {
//		drawMap(facOrLoc, abbreviationToState(stateAbbr), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
//	}
}

function runCustomStateQuery() {
	closeWelcomeWindow();
//	if (welcomeScreenAlreadyShown == true) {
//		$("#welcomeWindowEmitters").attr('style', 'display:none');
//		$("#welcomeWindowSuppliers").attr('style', 'display:none');
//		$("#welcomeCloseEmitter").attr('style', 'display:none');
//		$("#welcomeCloseSupplier").attr('style', 'display:none');
//		$("#welcomeCloseOnshore").attr('style', 'display:none');
//		$("#welcomeCloseLDC").attr('style', 'display:none');
//		$("#welcomeCloseCO2Injection").attr('style', 'display:none');
//		$("#welcomeCloseSF6").attr('style', 'display:none');
//		$("#welcomeClosePointSource").attr('style', 'display:none');
//		$("#welcomeWindow").css('top', getBrowserWindowHeight() / 2 - 150);
//		$("#welcomeWindow").css('left', getBrowserWindowWidth() / 2 - 427);
//		$("#welcomeWindow").fadeIn(1000);
//		welcomeScreenAlreadyShown = false;
//	}
	setURLPath();
}
