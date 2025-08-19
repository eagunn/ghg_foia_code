function lgradient(color) {
	return {
		linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1},
		stops: [
		    [0, color],
		    [1, new Highcharts.Color(color).brighten(-0.3).get('rgb')]
		]
	};
}

function initBarChart(data) {
	jQuery.each(data.series, function () {
		this.color = lgradient(this.color);
	});
	if (data.sectorId != null) {
		$('#sectorId').val(data.sectorId);
	}
	//if (data.mode == "TRIBAL LAND") {
		data.xAxis.labels = {
			formatter: function () {
				return this.value;
			},
			style: {
				width: '250px'
			},
			useHTML: true
		};
	//}
	$('#canvas-vis').highcharts({
		chart: {
			type: 'bar',
			marginTop: 75,
			marginBottom: 75
		},
		credits: {
			text: data.credits,
			href: null,
			position: {
				align: 'center',
				y: -10
			}
		},
		legend: {
			enabled: false
		},
		title: {
			text: data.title,
			align: 'left',
			style: {fontSize: '12px', width: '500px'},
			useHTML: true
		},
		xAxis: data.xAxis,
		yAxis: {
			min: 0,
			title: {
				text: null
			},
			labels: {overflow: "justify"}
		},
		tooltip: {
			pointFormat: '{series.name}: <b>{point.y}</b> ' + data.unit + ' CO2e'
		},
		plotOptions: {
			bar: {
				borderWidth: 0
			},
			series: {
				stacking: 'normal',
				point: {
					events: {
						click: function (event) {
							if (data.view == 'SECTOR1') {
								var subSector = this.series.name;
								resetFilters(this.category);
								if (subSector != "Power Plants" || subSector != "Refineries") {
									jQuery('#subSectorName').val(subSector);
									var sId = getSectorId(this.category);
									updateSectorCheckbox(sId, subSector);
								}
								generateURL('barSectorL2');
							} else if (data.view == 'SECTOR2' || data.view == "STATE1L3" || data.view == 'SUPPLIER' || data.view == 'BASIN2') {
								displayFacilityDetail(this.id);
							} else if (data.view == 'SECTOR3') {
								$('#parentState').val(stateToAbbreviation(this.category));
								generateURL('barSectorL2');
							} else if (data.view == 'STATE1') {
								resetFilters(this.series.name);
								var stateAbbr = stateToAbbreviation(this.category);
								if (stateAbbr != "") {
									$('#parentState').val(stateAbbr);
								} else {
									if ($('#parentState').val() == 'TL') {
										var tribalLand = this.category;
										jQuery.ajax({
											type: 'GET',
											url: 'service/getIdFromTribalLand/',
											data: "tl=" + tribalLand,
											success: function (tribalLandId) {
												$('#countyState').val(tribalLandId);
												generateURL('barStateL2');
											}

										});
									} else {
										var county = this.category;
										if (county.indexOf(',') == -1) {
											jQuery.ajax({
												type: 'GET',
												url: 'service/getFipsFromCountyAndState/',
												data: "st=" + $('#parentState').val() + "&c=" + county,
												success: function (fips) {
													var fipsCode = addLeadingZeros(fips);
													$('#countyState').val(fipsCode);
													generateURL('barStateL2');
												}
											});
										} else {
											$('#subsectorName').val("");
											generateURL('barStateL2');
										}
									}
								}
								if (stateAbbr != "") {
									$('#subsectorName').val("");
									generateURL('barStateL2');
								}
							} else if (data.view == 'STATE1L2') {
								jQuery('#subsectorName').val(this.category);
								var si = $('#sectorId').val();
								var ss = $('#subsectorName').val();
								updateSectorCheckbox(si, ss);
								generateURL('barStateL3');
							} else if (data.view == 'BASIN1') {
								$('#basin').val(this.id);
								generateURL('barSector');
							}
						}
					}
				}
			}
		},
		series: data.series,
		exporting: {
			buttons: {
				contextButton: {
					enabled: false
				},
				exportButton: {
					text: 'Print or Download Chart',
					menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems
				}
			}
		},
		navigation: {
			buttonOptions: {
				theme: {
					'stroke-width': 1,
					stroke: 'silver',
					fill: '#EEE'
				}
			}
		}
	});
}

function initBarChartTrend(data) {
	jQuery.each(data.series, function () {
		this.color = lgradient(this.color);
	});
	$('#canvas-vis').highcharts({
		chart: {
			type: 'column'
		},
		credits: {
			text: data.credits,
			href: null,
			position: {
				align: 'center',
				y: -10
			}
		},
		title: {
			text: data.title,
			align: 'left',
			style: {fontSize: '12px', width: '500px'},
			useHTML: true
		},
		xAxis: data.xAxis,
		yAxis: {
			min: 0,
			title: {
				text: 'Direct GHG Emissions (' + data.unit + ' CO2e)'
			}
		},
		tooltip: {
			headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			'<td style="padding:0"><b>{point.y}</b></td></tr>',
			footerFormat: '</table>',
			shared: true,
			useHTML: true
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
		series: data.series,
		exporting: {
			buttons: {
				contextButton: {
					enabled: false
				},
				exportButton: {
					text: 'Print or Download Chart',
					menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems
				}
			}
		},
		navigation: {
			buttonOptions: {
				theme: {
					'stroke-width': 1,
					stroke: 'silver',
					fill: '#EEE'
				}
			}
		}
	});
}


