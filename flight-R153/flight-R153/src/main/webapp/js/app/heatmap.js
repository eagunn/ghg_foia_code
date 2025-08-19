/**
 * Created by alabdullahwi on 5/26/2015.
 */
/**
 *
 * this file contains all heatmap-related logic
 * I culled this logic from main.htm and main.js and put it here, to reduce clutter
 *
 * HISTORY:
 *
 * Heatmap was a functionality that provide a heat-map view of emissions projected on maps (and possibly other views)
 * it was another 'mode' besides regular Facility markers
 *
 * EPA decided to shelve it from the initial production launch in 2010 and as of now (June 2015) it remains shelved.
 * There is a possibility that they may want it at some point, hence we have decided to keep the code
**/


 function getHeatMapData(facOrLoc,state,fipsCode,lowE,highE, reportingStatus) {
     state = stateToAbbreviation(state);
     if(num != 0){ //COUNTIES
         jQuery.getJSON("service/heatmap/"+ryear+"?q="+facOrLoc+"&lowE="+lowE+"&highE="+highE+"&fc="+fipsCode+"&st="+state+"&rs="+reportingStatus+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(states) {
         currentJsonObject = states;
         drawStateOverlays(states);
            });
        }
     else {
        jQuery.getJSON("service/heatmap/"+ryear+"?q="+facOrLoc+"&lowE="+lowE+"&highE="+highE+"&fc="+fipsCode+"&rs="+reportingStatus+"&st="+state+generateGasFilterQueryString()+generateSectorFilterQueryString()+generateSearchQuery(), function(states) {
        currentJsonObject = states;
        drawStateOverlays(states);
        });
     }//else
 }


function getPolygonHeatMapData(response) {
    var numRows = response.getDataTable().getNumberOfRows();

    var min = Infinity;
    var max = -Infinity;

    var tmp = 0;
    for(var i in dbData) {
        tmp = dbData[i].emission;
        if(tmp > max){
            max = tmp;
        }
        if(tmp < min){
            min = tmp;
        }
    }
    for(var i = 0; i < numRows; i++) {
        str = response.getDataTable().getValue(i,0);
        transformGeoJsonToArray(str,response,i,min,max);
    }

    // Create a div to hold the control.
    var legendControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    legendControlDiv.style.margin = '0px 0px 0px 10px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
    //controlUI.style.width = '250px';
    controlUI.style.height = '15px';
    //controlUI.style.backgroundColor = 'white';
    //controlUI.style.borderStyle = 'solid';
    //controlUI.style.borderWidth = '1px';
    //controlUI.style.borderColor = '#777';
    //controlUI.style.textAlign = 'center';
    legendControlDiv.appendChild(controlUI);

    var controlText = document.createElement('div');
    controlText.style.display = 'inline';
    controlText.style.fontFamily = 'Arial,sans-serif';
    controlText.style.fontSize = '11px';
    controlText.style.color = '#555';
    controlText.style.paddingLeft = '0px';
    controlText.style.paddingRight = '4px';
    controlText.style.verticalAlign = 'top';
    controlText.innerHTML = addCommas(Math.round(min))+'&nbsp;CO<sub>2</sub>e';
    controlUI.appendChild(controlText);

    var controlText = document.createElement('div');
    controlText.style.display = 'inline';
    controlText.innerHTML = '<img src="img/intensity.png">';
    controlUI.appendChild(controlText);

    var controlText = document.createElement('div');
    controlText.style.display = 'inline';
    controlText.style.fontFamily = 'Arial,sans-serif';
    controlText.style.fontSize = '11px';
    controlText.style.color = '#555';
    controlText.style.paddingLeft = '4px';
    controlText.style.paddingRight = '0px';
    controlText.style.verticalAlign = 'top';
    controlText.innerHTML = addCommas(Math.round(max))+'&nbsp;CO<sub>2</sub>e';
    controlUI.appendChild(controlText);

    map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(legendControlDiv);
}