
var isPowerPlantsOrTotalSectorsSelected = function(series) {
    
    for (var i = 0 ; i < series.length ; i++ ) {
        if (series[i].name.indexOf("Power") != -1 || series[i].name.indexOf("Total Reported") != -1) {
            return true; 
        }
    }
    
    return false; 
}

var zoomToBottomOfTrendGraph = function(chart,yAxis,series) {

    var max = null;
    var min = null;

    for (var i = 0 ; i < series.length ; i++  ) {

        var _name = series[i].name;
        var padding = 3;

        //ignore the two highest series 
        if (_name.indexOf("Power") == -1 && _name.indexOf("Total Reported") == -1) {
            //find the highest y value for this series
            var currentMax = null;
            var currentMin = null;
            for (var j = 0; j < series[i].data.length; j++) {
                if (!currentMax || currentMax < series[i].data[j].y) {
                    currentMax = series[i].data[j].y;
                }
                if (!currentMin || currentMin > series[i].data[j].y) {
                    currentMin = series[i].data[j].y;
                }
            }

            if (!max || max < currentMax) {
                max = currentMax;
            }
            if (!min || min > currentMin) {
                min = currentMin;
            }


        }
    }
        
        yAxis.setExtremes(0,max+padding); 

}


function initTrendChart(data) {
    var highChartsOpts = {
    	chart: {
    		marginTop: 100,
    		marginBottom: 120,
    		zoomType: 'y',
    		events: {
                load: function () {
                    var label = this.renderer.label(data.vNotes).add();
                    label.align(Highcharts.extend(label.getBBox(), {
                        align: 'center',
                        verticalAlign: 'bottom',
                        x: 0,
                        y: -10
                    }), null, 'spacingBox');
                }
            }
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
        subtitle: {
            text: data.subtitle,
            align: 'center',
            style: {fontSize: '10px', width: '500px'},
            y: 50,
            useHTML: true
        },
        xAxis: data.xAxis,
        yAxis: {
            min: 0,
            type: 'linear',
            title: {
                text: 'Direct GHG Emissions ('+data.unit+' CO<sub>2</sub>e)',
                useHTML: true
            }
        },
        tooltip: {
        	valueDecimals: 1,
        	pointFormat: '{series.name}: <b>{point.y}</b> '+data.unit+' CO2e'
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            },
            series: {
               events: {

                  dblclick: function(event) {
                      
                      var _extremes  = this.chart.yAxis[0].getExtremes(); 
                      var currentMin = _extremes.userMin;
                      var currentMax = _extremes.userMax;
                      var padding = 5;
                      var min = null;  
                      var max = null;
                      
                      for (var i =0 ; i< this.data.length ; i++ ) {
                          if (!min || min > this.data[i].y) {
                              min = this.data[i].y;
                          }
                          if (!max || max < this.data[i].y ) {
                             max = this.data[i].y;
                          }
                      }
                      
                      //if already focused on this series, zoom in some more  
                      if (currentMin >= min && currentMax  <= max  ) {
                          min = min+padding;
                          max = max-padding; 
                      }
                      
                      this.chart.yAxis[0].setExtremes(0,max);
                      this.chart.showResetZoom(); 
                  }
               }
            }
        },
        series: data.series,
        exporting : {
          buttons :{
            contextButton: {
                enabled: false
            },
            exportButton: {
                text: 'Print or Download Chart',
                menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems
            }
        //this is the difference for the buttons object  
          }
        }
        ,navigation: {
        	buttonOptions: {
        		theme: {
        			'stroke-width': 1,
        			stroke: 'silver',
        			fill: '#EEE'
        		}
        	}
        }
    };
    
    $("#canvas-vis").highcharts(highChartsOpts);
    
    var chart ; 
    var i = 0; 
    
    while (!chart) {
        if (Highcharts.charts[i]) {
            chart = Highcharts.charts[i]
        }
        i++; 
    }

    //allow highcharts time to load, hence setTimeout
    
    setTimeout(function() {
        
        //add viewbox attribute to highchart's svg to scale properly to disetTImeoutv
        $("svg").attr("viewBox", "0 0 100 100 ");

        
    //need to know if we need to show the 'Bottom Sector Zoom' button or not  
            if (isPowerPlantsOrTotalSectorsSelected(data.series)) {


                
                //add zoom button
                var zoomButton = $('<input style="display:none" type="checkbox" name="bottomZoomToggle" id="bottomZoomToggle">' +
                    '<label id="bottomZoomToggleLabel" for="bottomZoomToggle"></label>')
                $('svg').after(zoomButton);
                $('document').ready(function() {

                    $("#bottomZoomToggle").change(function() {

                        if (this.checked) {
                            zoomToBottomOfTrendGraph(chart, chart.yAxis[0], chart.series);
                        }
                        else {
                            chart.zoom();
                        }

                    })
                })
               }}, 100);
}



function hasTrend(sc, urlChange) {
	var facOrLoc = $("#facOrLocInput").val();
    if(unescape(facOrLoc) == "Find a Facility or Location"){
        facOrLoc = "";
    }
    var stateAbbr = $("#parentState").val();
    jQuery.getJSON("service/hasTrend/?ds="+dataSource+"&sc="+sc, function(data) {
        if (data.hasTrend == true) {
        	if( facOrLoc !="" || reportingStatus != 'ALL' || stateAbbr !="" ) {
        		$("#vLine").hide();
                $("#lineLabel").hide();
        	} else {
        		$("#vLine").show();
                $("#lineLabel").show();
        	}
            if (visType == "line" && urlChange == true) {
                generateURL('trend');
            }
        } else {
            $("#vLine").hide();
            $("#lineLabel").hide();
            if (visType == "line" && urlChange == true) {
                visType = 'map';
                generateURL('facility');
            }
        }
    });
}

