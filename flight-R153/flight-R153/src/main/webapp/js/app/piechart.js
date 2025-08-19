function rgradient(color) {
	return {
		radialGradient: { cx: 0.5, cy: 0.5, r: 0.5},
		stops: [
		    [0, color],
		    [1, new Highcharts.Color(color).brighten(-0.3).get('rgb')]
		]
	};
}

function initPieChart(data) {
	jQuery.each(data.series[0].data, function() {
		this.color = rgradient(this.color);
	});
	if (data.sectorId != null) {
		$('#sectorId').val(data.sectorId);
	}
	$("#canvas-vis").highcharts({
        chart: {
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
        legend: {y: -30},
        title: {
        	text: data.title,
            align: 'left',
            style: {fontSize: '12px', width: '500px'},
            useHTML: true
        },
        tooltip: {
    	    pointFormat: '{series.name}: <b>{point.y}</b> '+data.unit+' CO2e',
        	percentageDecimals: 1
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                borderWidth: 0,
                cursor: 'pointer',
                shadow: true,
                startAngle: 90,
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    //format: '<b>{point.name}</b>: {point.y:,.0f} ({percentage: .1f} %)'
                    formatter: function() {
                    	if (data.view == 'SECTOR3' || data.view == 'SUPPLIER' || data.view == 'BASIN1' || data.view == 'BASIN2') {
                    		return "<b>"+this.point.name.substring(0,10)+"</b>...: "+Highcharts.numberFormat(this.percentage, 1, ".", ",")+" %";
                    	} else {
                    		return "<b>"+this.point.name+"</b>: "+Highcharts.numberFormat(this.point.y, 0, ".", ",")+" ("+Highcharts.numberFormat(this.percentage, 1, ".", ",")+" %)";
                    	}
                    }
                }
            },
        	series: {
        		point: {
            		events: {
            			click: function(event) {
            				if (data.view == 'SECTOR1') {
               					resetFilters(this.name);				
               					generateURL('pieSectorL2');
            				} else if (data.view == 'SECTOR2') {
            					$('#subsectorName').val(this.name);
            					var si = $('#sectorId').val();
            					var ss = $('#subsectorName').val();
            					updateSectorCheckbox(si, ss);
            					generateURL('pieSectorL3');
            				} else if (data.view == 'SECTOR3' || data.view == 'SUPPLIER' || data.view == 'BASIN2') {
            					displayFacilityDetail(this.id);
            				} else if (data.view == 'SECTOR4') {
            					$('#subsectorName').val(data.subsector);
            					$('#parentState').val(stateToAbbreviation(this.name));
            					generateURL('pieSectorL3');
            				} else if (data.view == 'BASIN1') {
            					$('#basin').val(this.basin);
            					generateURL('pieSector');
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