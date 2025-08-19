
/**
 * Created by alabdullahwi on 5/26/2015.
 */

function downloadExcel(allRyNeeded) {
    
    var allReportingYears = (allRyNeeded) ? "&allReportingYears=yes" : "" 
    // I cannot use the flight request object here because this has to be a non-AJAX, non-POST call
    location.href = "service/export?" + generateUrlParameters() + allReportingYears + "&listExport=false";
}

function downloadListExcel() {
	var geoParam = ($('#isGeoList').val() == 1) ? "&listGeo=true" : ""
    location.href = "service/export?" + generateUrlParameters() + "&listExport=true" + geoParam;
}

$(document).ready(function() {
	$('#exportByChanges').hide();	
    Events.subscribe('canvas-switch', function(type) {
        if (type === 'list' && (trendSelection === 'trend' || $('#isGeoList').val() == 1)){
            $('#exportButton').hide();
            $('#exportByChanges').show();
        } else {
            $('#exportButton').show();
            $('#exportByChanges').hide();
        }
    });
	
})

