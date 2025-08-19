$(document).ready(function () {
	$(".myDatacredit").html("Data reported to EPA as of 08/04/2019");
});
var vYear = "2018";
var deviceGPS = "Android_Location";
if( navigator.userAgent.match(/iPhone/) ) {
	deviceGPS = "iPhone_Location";
}

var facilityDetailPage = "facility-screen.htm";
var facilityListPage = "results-list.htm";
var facilityMapPage = "results-map.htm";
var filterPage = "filter-screen.htm";
var homePage = "home-screen.htm";

// Save our Templates so they are preserved when rendering
var facilityInfoTmpl = 	$.templates("#facilityInformation");
var facilityListTmpl = 	$.templates("#facListTemplate");
var stateListTmpl = 	$.templates("#stateListTemplate");
var sectorListTmpl = 	$.templates("#sectorListTmpl");
var stateFilterTmpl = 	$.templates("#stateListTmpl");

// Globally saved variables that are passed between Page changes
var passedData = null;
//var curSelectedFacId = null;

var prevRequest = "";
var facilitiesList = new Array();
var states = "";
//var selectedFacId = null;
var curFacilityId = null;

//var map;
//var userLocation = new google.maps.LatLng( '42', '-71' );
var userLocation;
var isSharingLocation = false;
var curStateCode = "";
//var userLocMarker;
var stateList;
var sectorsObj;

// Requested Map actions, constants
var PAN_TO_USER = 0;
var PAN_TO_STATE = 1;
var PAN_TO_FACILITY = 2;
var PAN_TO_BESTFIT = 3;
var mapAction = 0;	// Default action, pan to user

//Initialize Page ID constants
var HOMEPAGE = "homeScreen";
var RESULTS_LIST = "ResultsList";
var RESULTS_MAP = "ResultsMap";
var FACILITY_INFO = "facilityScreen"; 
var FILTER = "filterScreen";
var prevPageId = HOMEPAGE;

var requestObj = {
	gases : {g1 : 1, g2 : 1, g3 : 1, g4 : 1, g5 : 1, g6 : 1},
    sectors : {s1 : 1, s2 : 1, s3 : 1, s4 : 1, s5 : 1, s6 : 1, s7 : 1, s8 : 1, s9 : 1},
    eRange : {lowE : 0, highE : 23000000},
    options : {st : "US", q : "", fc : "", sc : 0, so : 0, ds : 'E', pg : 0, ry : vYear }
};
/*
 * update filter.htm checkboxes with current selection
 */
function updateFiltersToRq(){
	
	$.each(requestObj.gases, function(idx, obj){
	    $("#"+idx).attr("checked", obj==1 ? true : false).checkboxradio("refresh");
	});

	$.each(requestObj.sectors, function(idx, obj){
	    $("#"+idx).attr("checked", obj==1 ? true : false).checkboxradio("refresh");
	});
	
	//update other options
	$("#stateMenu").val(requestObj.options.st).selectmenu("refresh");

	//update eRange
	$("#emission_slider_min").val(requestObj.eRange.lowE).slider("refresh");
	$("#emission_slider_max").val(requestObj.eRange.highE).slider("refresh");
}
	/*
     * options breakdown: 
     * ------------------------------------------------------------
     * var	|	backend name			|	expected type
     * ------------------------------------------------------------
     * st	|	state (US State) 		|	String
     * q 	| 	query (search box) 		|	String
     * fc 	|  	fipsCode				|	Integer
     * sc 	| 	sc (???)				|	int (not used on backend?)
     * so	|	SortOrder				|	int
     * ds 	|	sectorType				|	String
     * pg	|	pageNumber				|	int
     * ------------------------------------------------------------
     */ 


//call this when loading the homepage to start from the default state of the request object
function resetRQObj(){
	requestObj = {
			gases : {g1 : 1, g2 : 1, g3 : 1, g4 : 1, g5 : 1, g6 : 1},
		    sectors : {s1 : 1, s2 : 1, s3 : 1, s4 : 1, s5 : 1, s6 : 1, s7 : 1, s8 : 1, s9 : 1},
		    eRange : {lowE : 0, highE : 23000000},
		    options : {st : "US", q : "", fc : "", sc : 0, so : 0, ds : 'E', pg : 0 }
	};
}

function buildRequest(svcCall, year){
	
	var action = "service/";
	
	if(!year)
		year = vYear;
	if(svcCall)
		action += svcCall + "/" + year + "?";
	else
		action = "service/facilityList/"+vYear+"?";
	
	prevRequest = svcCall;
	
	//now get position info from position
	
	action += $.param(requestObj.eRange);
	action += "&" + $.param(requestObj.gases);
	action += "&" + $.param(requestObj.sectors);
	action += "&" + $.param(requestObj.options);
	
	if(prevRequest == "facilitiesWithin") {
		action += "&lt=" + mCenter.latitude + "&ln=" + mCenter.longitude;
		// Get the Southwest and Northeast boundaries
		sw = new Microsoft.Maps.Location( bMap.getBounds().getSouth(),bMap.getBounds().getWest() );
		ne = new Microsoft.Maps.Location( bMap.getBounds().getNorth(),bMap.getBounds().getEast() );
		// Edit the request object to include the map's bounds
		action += "&swlt=" + sw.latitude + "&swln=" + sw.longitude;
		action += "&nelt=" + ne.latitude + "&neln=" + ne.longitude;
	}
	
	return action;
}


// Social media URLs, functions
//prod ghgp URL may need to change
var ghgpUrl = "http://ghgdata.epa.gov/ghgp/main.do";
//twitter hashtags
var twtHT = "epa,ghgdata";
var socialUrls = {
			"facebook" 		: "http://www.facebook.com/sharer/sharer.php?u="+encodeURIComponent(ghgpUrl),
			"googlePlus" 	: "https://m.google.com/app/plus/x/?v=compose&content=EPA+GHGP+Data+Publication+tool+"+encodeURIComponent(ghgpUrl),
			"twitter" 		: "https://twitter.com/intent/tweet?text=EPA+GHGP+Data+Publication+tool+"+encodeURIComponent(ghgpUrl)+"+&hashtags="+twtHT,
			"reddit" 		: "http://i.reddit.com/submit?url="+encodeURIComponent(ghgpUrl)+"&title=EPA+GHGP+Data+Publication+tool"
};

function postToTwitter(){	
	window.open(socialUrls.twitter, "_blank");
}
function postToFacebook(){
	window.open(socialUrls.facebook, "_blank");
}
function postToGooglePlus(){
	window.open(socialUrls.googlePlus, "_blank");
}
function postToReddit(){
	window.open(socialUrls.reddit, "_blank");
}

/*
 * ---------------------------
 * Initialize Global Methods |
 * ---------------------------
 */

$(document).on("pagechange", onPageChange);
$(document).on('pageshow', '#ResultsMap', resizeMap);
$(window).on('orientationchange', resizeMap );

//enable experimental touchOverflow for better transition performance and true fixed toolbars
$(document).bind("mobileinit", function() {
	// Mobile optimization
	$.support.touchOverflow = true;
	$.mobile.touchOverflowEnabled = true;
	// Speed up page transitions
	$.mobile.defaultPageTransition = 'none';
	// Turn off the custom jqm select menu because it's too buggy
	// Most mobile operating systems have a good way of simulating select menus
	// So this is not needed
	$.mobile.selectmenu.prototype.options.nativeMenu = true;
	// fixes issues with page transitioning back and forth after a link is clicked,
	// since Ajax navigation is disabled on the map page
	$.mobile.pushStateEnabled = false;
});

// Fix for Ajax loading progress bar \ spinner
$(document)	
	.ajaxStart(function() {
		// Show spinner at start of Ajax service call
		$.mobile.loading('show', { theme: "x" });
	})
	.ajaxStop(function() {
		// Hide spinner at end of Ajax service call
		$.mobile.loading('hide');
	});
/*
 * Sorting functions
 */
var NAME = 0;
var EMISSIONS = 1;
var DISTANCE = 2;

function sortBy( type ){
	curStateCode = $("#selectStateMenu option:selected").val();
	if( type == NAME )
		var upDown = requestObj.options.so == 1 ? 0 : 1;
	else if( type == EMISSIONS )
		var upDown = requestObj.options.so == 3 ? 2 : 3;
	requestObj.options.so = upDown;
	var request = buildRequest();
	$.getJSON( request, function( facilities ) {
		// Add commas to each Facility's emission value before appending to main list of facilities
		$.each( facilities, function(i, facility) {
			  facility.emissions = addCommas(facility.emissions);
		});
		// List of facilities within the selected state received
		facilitiesList = facilities;
		$.mobile.changePage('results-list.htm', {
			type: 'GET',
			data: {
				prevPage:		'HomeScreen',
				selectedState:	curStateCode
			}
		}); 		
	});	
}

function resizeMap(event) {
	if( $.mobile.activePage[0].id == RESULTS_MAP ) {
		$("#ResultsMap").height( $(window).height() - 44 );
		$("#map_content").height( $(window).height() - 44 );
	}
}

// Search for a facility
function search(event) {		
	var searchQuery = $("#" + this.id + " #searchFacilities").val();
		
	requestObj.options.q = searchQuery;
	var serviceCall = buildRequest("facilityList", vYear);
	
	$.getJSON( serviceCall, function( matchingFacilities ) {
		// Add commas to each Facility's emission value before appending to main list of facilities
		$.each( matchingFacilities, function(i, facility) {
			  facility.emissions = addCommas(facility.emissions);
		});
		// Retrieve the list of Facilities matching the search form
		facilitiesList = matchingFacilities;
		// If we are in the Results-List page then simply refresh the view
		if( $.mobile.activePage.attr('id') == "ResultsList" ) 
			$("#facList").html( $("#facListTemplate").render(facilitiesList) ).listview("refresh");
		// Go to the Results-List page if we are not already there
		else 
			$.mobile.changePage('results-list.htm');
	});
	return false;
}




// onPageChange is called on every page change AND after pageInit
function onPageChange(event, data) {
	    
    var toPageId = data.toPage.attr("id");

    if( toPageId != RESULTS_LIST ) 
        pagingEnabled = false;
    
    if( data.options.fromPage )
    	prevPageId = data.options.fromPage.attr("id");
	
    switch (toPageId) {    	

	    case HOMEPAGE:
	    	changeToHomePage(data); // works
	    	break;
        case RESULTS_LIST:
        	changeToResultsPage(data);
            break;
        case RESULTS_MAP:
        	changeToMap(data);
        	break;
        case FACILITY_INFO:
        	renderFacilityDetails(data);
        	break;
        case FILTER:
            //if(prevPageId != "stateMenu-dialog")
                renderFilterPage(data);
        	break;        	
    }

}

function changeToHomePage(data) {
	// Make sure that we only reset the State Code if we are 
	// returning from an actual page not a Modal Popover
	if( data.options.fromPage && data.options.fromPage.attr("id") != "" && data.options.fromPage.attr("id") != "selectStateMenu-dialog") {
		resetRQObj();
		curStateCode = "";
		// Refresh the State Selection menu so that "Select State" is displayed the next time the page is shown
		$('#selectStateMenu option').removeAttr('selected');
		$("#selectStateMenu").selectmenu('refresh', true);
	}
}

function changeToResultsPage(data) {		
	// If the user selected another state, different than previously selected
	// retrieve the Facilities in the newly selected State
    if( data.options.data && data.options.data.selectedState != curStateCode ) {
    	curStateCode = data.options.data.selectedState;
		// Retrieve Facility List data information and render it in the front end 
    	prevRequest = "service/facilityList/"+vYear+"?q=&lowE=0&highE=23000000&fc=&st=" + curStateCode + "&pg=0&g1=1&g2=1&g3=1&g4=1&g5=1" 
    		+ "&g6=1&g7=1&g8=1&g9=1&g10=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s10=1&ds=E&sc=0&so=0"; 
		$.getJSON( prevRequest, function(data) {
    		// Add commas to each Facility's emission value before appending to main list of facilities
    		$.each( data, function(i, facility) {
    			  facility.emissions = addCommas(facility.emissions);
    		});
			// Render the first page (facilities 0-9) and save the list of facilities
			$("#facList").html( $("#facListTemplate").render(data) ).listview("refresh");
			facilitiesList = data;
		});
    }
    // If the User selected the same State as previously then no need to make another AJAX call
    else {    	
    	// If a Facility list was previously used, display it again
    	$("#facList").html( $("#facListTemplate").render(facilitiesList) ).listview("refresh");
    }
    // Only enable paging if we did not just come from the Map page,
    // in which case all the facilities should be already loaded into the cache
    if( prevPageId != RESULTS_MAP )
    	pagingEnabled = true; 
}

function changeToMap(data) {	
	// TODO Every time we change to the Map page we should clear the map of old pins and infoboxes
	removePinsOutOfBounds( null );	
	// Where should we pan the Map to?
	switch( mapAction ) {
	case PAN_TO_USER:
		// Check if we have the User's location, if so pan to it
		if( userLocation ) {
			// Add a handler to check for a change in the map's view, zoom or pan
//			Microsoft.Maps.Events.addHandler( bMap, 'viewchangestart', viewChangeStart ); 
			setVectorPosition( userLocation );
			displayUserPin();
		}
		/*
		else {
			// If not, the user declined to share his location or there was an error retrieving it		
			console.log("Unable to find your position");
		}
		*/
		break;
	case PAN_TO_STATE:
		// TODO: Check if we have a StateCode to pan to, if so pan to the state given the state code
		if( curStateCode ) {
			// Pan to state
			var stateAbbrev = curStateCode;
			// Service call to get the bounding coordinates for the given State
			$.getJSON( 'service/getStateBounds/' + stateAbbrev, function(bounds) {
		    	// Remove the Handler called when the Map view is changed
//				Microsoft.Maps.Events.removeHandler(viewChangeStartHandler); 
				// Get the bounding rectangle's coordinates
				var boundary = Microsoft.Maps.LocationRect.fromEdges(
					bounds['ne']['lat'],	// North 
					bounds['sw']['lng'],	// West
					bounds['sw']['lat'],	// South
					bounds['ne']['lng']		// East
				);
								
				// Set the map's new bounding box
				bMap.setView({
					bounds: boundary
				});				
				// Set the new map center
		    	mCenter = boundary.center;
			});				
		}
		// TODO: onPageChange() is being called twice, find a fix
		// The first time it is being called data.options.data is blank and is only defined on the second call		
		else {
			// We should have a state code if we have reached here, bug found
			console.log("No State selected, panning to best view of currently selected facilities.");
			// Pan the map to include all facilities currently selected
			coordinates = [];
			$.each( facilitiesList, function(i, facility) {
				// Create a coordinate from the facility's position
				var latLng = new Microsoft.Maps.Location(facility.latitude, facility.longitude);
				// Save the facility coordinates for future use
				coordinates.push(latLng);				
			});
			if( coordinates.length != 0 ) {
		    	// Remove the Handler called when the Map view is changed
//		    	Microsoft.Maps.Events.removeHandler(viewChangeStartHandler); 
				var bestview = Microsoft.Maps.LocationRect.fromLocations(coordinates);
				bMap.setView({
					bounds:	bestview 
				});
			}
		}		
		break;
	case PAN_TO_FACILITY:
		if( curFacilityId ) {
			
	    	// Remove the Handler called when the Map view is changed
//	    	Microsoft.Maps.Events.removeHandler(viewChangeStartHandler); 
			
			// Pan to the Facility's location and zoom in
			$.getJSON( "service/facilitiesInfo/"+vYear+"?id=" + curFacilityId + "&ds=E&et=", function(facilityInfo) {
//				var facility = getFacilityById( curFacilityId );
				setCoordsPosition( facilityInfo.facility.latitude, facilityInfo.facility.longitude );
				bMap.setView({zoom: 14});	
			});
		}
		/*
		else {
			// If we do not have a Facility ID then show last view and log error message
			console.log("No Facility ID was given");
		}
		*/
		break;
	}	
}

function renderFacilityDetails(data) {
	
	var facId = 0;
	// Get the facility ID if passed in through the page URL as a GET parameter
	if( data.options.data && data.options.data.facilityId != null )
		facId = data.options.data.facilityId;
	// Get the facility ID if it was not passed in through the URL but was previously saved Globally
	else if( curFacilityId )
		facId = curFacilityId;
		
	// Get the passed in Facility ID and Retrieve the Facility Information from the backend using it's ID and render it using JsRender 
	$.getJSON( "service/facilitiesInfo/"+vYear+"?id=" + facId + "&ds=E&et=", function(facility) {
		// Add commas to the Facility's emission values before rendering     		
		facility.totalEmissions = addCommas(facility.totalEmissions);
		if(facility.gasEmissions[0])
			facility.gasEmissions[0].quantity = addCommas(facility.gasEmissions[0].quantity);
		if(facility.gasEmissions[1])
			facility.gasEmissions[1].quantity = addCommas(facility.gasEmissions[1].quantity);    		
		if(facility.gasEmissions[2])
			facility.gasEmissions[2].quantity = addCommas(facility.gasEmissions[2].quantity);    		
		$("#facilityInfoContainer").html( $("#facilityInformation").render(facility) );
	});
	
}

function renderFilterPage(data) {
	//only call if page isn't 
	var fromId = data.options.fromPage[0].id;
	var toId = data.toPage[0].id;
	if(fromId && fromId != toId)
		updateFiltersToRq();
}

// Generate a String for Sector filters (NOT CURRENTLY BEING USED)
function generateSectors() {
	var sectorStr = '';
	// If no Sectors were selected, display all Sectors (default)
	if( sectorsObj == null ) {
		sectorStr = 's1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s10=1&';
	}
	// Generate the Sectors filter based on what is currently selected
	else {
		sectorsObj.each( function() {
			sectorStr = sectorStr + '&' + $(this).id + '=';
			if( $(this).on )
				sectorStr = sectorStr + '1';
			else
				sectorStr = sectorStr + '0';		
		});
	}
	// Return the constructed sector flags
	return sectorStr;
}


// These functions will be moved out of this enclosing function
function getFacilitiesByState() {
	
	curStateCode = $("#selectStateMenu option:selected").val();
	
//	$.mobile.changePage('results-list.htm', {
//		type: 'GET',
//		data: {
//			prevPage:		'HomeScreen',
//			selectedState:	curStateCode
//		}
//	}); 
	
	//reset the request obj to default (all inclusive) state
	resetRQObj();
	//update to selected U.S. state and build request
	requestObj.options.st = curStateCode;
	var request = buildRequest();

	$.getJSON( request, function( facilities ) {
		// Add commas to each Facility's emission value before appending to main list of facilities
		$.each( facilities, function(i, facility) {
			  facility.emissions = addCommas(facility.emissions);
		});
		// List of facilities within the selected state received
		facilitiesList = facilities;
		// Update the Map's future service call so that we pan to the currently selected state
		mapAction = PAN_TO_STATE;
		// change pages
		$.mobile.changePage('results-list.htm', {
			type: 'GET',
			data: {
				prevPageId:		HOMEPAGE,
//				selectedState:	state
				selectedState:	requestObj.options.st
			}
		}); 		
	});	
}

	
/* 
 * *************
 * | Home Page |
 * *************
 */

$(document).on('pageinit', '#homeScreen', function(event) {

	// This will prevent event triggering more then once
	if(event.handled !== true)      
        event.handled = true;
    else 
    	console.log("Page already initialized"); 
	
	$(".j-mobileShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeIn(500);
    });
    $(".closeShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeOut(500);
    });

	// Search Form
	$("#searchForm-HP").submit( search );
	
	/* Retrieve a list of States and render them in a List view */
	$.getJSON( "service/states", function(data) {
		stateList = data;
		$("#selectStateMenu").html( $("#stateListTemplate").render(stateList) ).selectmenu('refresh', true); //.listview("refresh");			
	});

	// When a state is selected from the list of states, find all facilities in that state
	$('#selectStateMenu', $.mobile.activePage).change( getFacilitiesByState );

	// If the "Near Me" button is clicked display the map
	$("#nearMeBtn").on('tap', initiate_geolocation);
	
	// If LOGO is clicked take user back to the home page
	$('.homeLogo').on('tap', function(event) {
		$.mobile.changePage( "home-screen.htm" );		
	});
	
	// When the "By State" link is clicked, open the State Menu popup
	$("#selectStateLink").on('tap', function(e) {
		$('#selectStateMenu-button').trigger('click');
	});
	

});




/*
 * ===================
 * Results List Page |
 * ===================
 */

//setup an interval so we can throttle the `scroll` event handler since there will be tons of `scroll` events fired
/*
var timer = setInterval(function () {
        scrollOK = true;
    }, 100),			//run this every tenth of a second
    scrollOK = true;	//setup flag to check if it's OK to run the event handler
*/

var pagingEnabled = false;
$(window).bind('scroll', function () {

    //check if it's OK to run code
    if(pagingEnabled) {
        //check if the user has scrolled within 100px of the bottom of the page
    	if( $.mobile.activePage && 
    			facilitiesList.length > 0 &&
    			$.mobile.activePage.attr('id') == "ResultsList" &&
    			( $(this).scrollTop() + $(this).height() ) >= ($(document).height() - 100)) {
            // load more list-items 
    		// Start the loading animation
    		$("#loadingImg").show();
    		$("#loadMoreText").hide();
            //set flag so the interval has to reset it to run this event handler again
            pagingEnabled = false;
        	// string manipulation to get the next page's number
//        	var i = prevRequest.indexOf("pg=");
//        	var curPage = parseInt(prevRequest[i+3]);
//        	var nextPage = curPage + 1;
        	var nextPage = parseInt(requestObj.options.pg, 10) + 1;
        	requestObj.options.pg = nextPage;
        	
        	// Create a new URL using the next page number
//        	prevRequest = prevRequest.replace('pg=' + curPage, "pg=" + nextPage);
        	var request = buildRequest(prevRequest);
        	// Get a list of facilities of the next page
        	$.getJSON( request, function(data) {
        		// Add commas to each Facility's emission value before appending to main list of facilities
        		$.each( data, function(i, facility) {
        			  facility.emissions = addCommas(facility.emissions);
        		});
        		// Append the next page to the current one
				$("#facList").append( $("#facListTemplate").render(data) ).listview("refresh");
				// Append the returned list of facilities to our current list of Facilities
				facilitiesList = facilitiesList.concat( data );
				// After loading list items allow app to reload more items on next scroll
				pagingEnabled = true;
				// If no facilities were found let the use know
				if( facilitiesList.length == 0 ) {
					$("#loadMoreText").hide();
					$("#noResultsFounds").show();
					pagingEnabled = false;
				}
				// Hide the 'Load More' text if there are no more facilities to load
				else if( data.length < 9 ) {
					$("#loadMoreText").hide();
					$("#noResultsFounds").hide();
					pagingEnabled = false;
				}
				else 
					$("#loadMoreText").show();
				$("#loadingImg").hide();
        	});
        }
    }
});


var originalChangePage = $.mobile.changePage;

// Redefine changePage so that it always calls $.mobile.loading
$.mobile.changePage = function(to, options) {
	// Show the loading animation without the box
	$.mobile.loading('show', { theme: "x" });
	// Call the original changePage method
	originalChangePage(to, options);
	// Hide the loading animation
	$.mobile.loading('hide');
};


$(document).on('pageinit', '#ResultsList', function(event) {

	// This will prevent event triggering more then once
	if(event.handled !== true)      
        event.handled = true;
    else 
    	console.log("Page already initialized"); 
	
	$(".j-mobileShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeIn(500);
    });
    $(".closeShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeOut(500);
    });

	$('.homeLogo').on('tap', function(event) {
		$.mobile.changePage( "home-screen.htm" );		
	});

	// Search Form fired when user clicks Enter or Submits form
	$("#searchForm-RL").submit( search );

	// Register click listener for Map button	
	$("#ResultsList li a#map_btn").on('tap', function(event) {
		event.preventDefault();
//		var curState = getParameterByName('selectedState'); 	// <-- wrong variable name		
		$.mobile.changePage( "results-map.htm");
		/*
		$.mobile.changePage( "results-map.htm", {
		    type : "GET",
		    data : {
		    	prevPageId: 	RESULTS_LIST, 
		    	selectedState:	curStateCode
//		    	mapActionL:		PAN_TO_STATE
		    }
		});
		*/
		return false;
	});
	
	// Register click listener for Filter button
	$("li a[href='filter.htm']").on('tap', function(event) {
		event.preventDefault();		
		$.mobile.changePage( "filter.htm", {
		    type : "GET",
		    data : {
		    	prevPageId: 	RESULTS_LIST, 
		    	selectedState:	curStateCode
		    }
		});				
		return false;
	});
        
    // If a Facility is selected from the List of Facilities switch to the Facility Details Page
	// NOT WORKING NOT SURE WHY
    $('#facList').on('tap', 'a', function(event) {
    	// Prevent the Hyperlink from working
    	event.preventDefault();    	
    	// Update the currently selected facility ID
    	// Using (this) instead of event.target.id 
    	curFacilityId = $(this).attr("id");
    	// Change to the Facility Details page with the selected Facility's ID
    	$.mobile.changePage( facilityDetailPage, {
    		data: {
		    	prevPageId: RESULTS_LIST, 
    			facilityId: curFacilityId
    		}
    	});    	
    	return false;
    });

});




/*
 * =======================
 * Facility Details Page |
 * =======================
 */

$(document).on( 'pageinit', '#facilityScreen', function(event) {

	// This will prevent event triggering more then once
	if(event.handled !== true)      
        event.handled = true;
    else 
    	console.log("Page already initialized"); 
	
	$(".j-mobileShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeIn(500);
    });
	
    $(".closeShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeOut(500);
    });

	$('.homeLogo').on('tap', function(event) {
		$.mobile.changePage( "home-screen.htm" );		
	});

	// Register Search Form
	$("#searchForm-FD").submit( search );

	// Register click listener for Filter button
	$("#facilityScreen li a[href='filter.htm']").on('tap', function(event) {
    	// Prevent the Hyperlink from working
    	event.preventDefault();
    	// Get the current Facility's ID from the URL
    	var facId = getParameterByName('facilityId');
		$.mobile.changePage( "filter.htm", {
		    type : "GET",
		    data : {
		    	prevPageId: 	FACILITY_INFO, 
		    	facilityId:		facId
		    }
		});				
		return false;
	});
	
	$("#facilityScreen li a[href='results-map.htm']").on('tap', function(event) {
    	// Prevent the Hyperlink from working
    	event.preventDefault();
    	// Get the current Facility's ID from the URL
    	curFacilityId = getParameterByName('facilityId');
    	var facility = getFacilityById( curFacilityId );
//    	var facLatitude = facility.latitude;
//    	var facLongitude = facility.longitude;
    	// Map will pan to the Facility location and zoom in
		mapAction = PAN_TO_FACILITY;
		// TODO: (******) Figure out why FacilitiesList is empty when changePage is called from Map 
		$.mobile.changePage( "results-map.htm");
		/*
		$.mobile.changePage( "results-map.htm", {
		    type : "GET",
		    data : {
		    	prevPageId: RESULTS_LIST, 
		    	facilityId:	facId
//		    	latitude:	facLatitude,
//		    	longitude:	facLongitude,
//		    	mapActionL:	PAN_TO_FACILITY
		    }
		});
		*/	    	
    	return false;
	});
});
                    
// Keep the Footer and Header bars from auto hiding at every user click
//$(document).on('taphold', ':jqmData(role=page)', function() {
//    $('[data-position=fixed]').fixedtoolbar('toggle');
//});


/*
 * ========== Filter.htm ===========
 * Everything related to Filter.htm 
 */

var originalSerializeArray = $.fn.serializeArray;
// Override the Serialize function so that it now returns 1 or 0 for true and false
$.fn.extend({
    serializeArray: function () {
        var brokenSerialization = originalSerializeArray.apply(this);
        var checkboxValues = $(this).find('input[type=checkbox]').map(function () {
            return { 'name': this.name, 'value': this.checked ? '1' : '0' };
        }).get();
        var checkboxKeys = $.map(checkboxValues, function(element) { return element.name; });
        var withoutCheckboxes = $.grep(brokenSerialization, function (element) {
            return $.inArray(element.name, checkboxKeys) == -1;
        });

        return $.merge(withoutCheckboxes, checkboxValues);
    }
});

$.fn.digits = function() { 
    return this.each(function(){ 
        $(this).text( $(this).text().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") ); 
    });
};

function generateQuery( formDiv ) {
	var formData = formDiv.serializeArray();	
	return formData;
//	.serialize()
//	return 'service/listSector/2011?q=&lowE=0&highE=23000000&fc=&st=&g1=1&g2=1&g3=1&g4=1&g5=1&g6=1&g7=1&g8=1&g9=1&g10=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&ds=E&sc=0';
}

//function hideAllDivs() {
//	$('#sectorFilter').hide();
//	$('#stateFilter').hide();
//	$('#ghgFilter').hide();
//	$('#emissionRangeFilter').hide();
//	// Deactive all tags so that none are selected
//	$('[data-role="navbar"] ul li a').removeClass("ui-btn-active ui-state-persist");
//}


function hideAllDivsExcept(divName) {
	// Hide all other divs
	$("#filterForm  .menuSection").hide();
	// Deactive all tags so that none are selected
	$('[data-role="navbar"] ul li a').removeClass("ui-btn-active");
	// Show the selected Div
	$(divName).show();
	// Set the corresponding menu tab to active
	$('[data-role="navbar"] ul li' + divName + 'Link a').addClass("ui-btn-active");
}

$(document).on('pageinit', '#filterScreen', function(event) {

	// This will prevent event triggering more then once
	if(event.handled !== true)      
        event.handled = true;
    else 
    	console.log("Page already initialized"); 
	
	$('.homeLogo').on('tap', function(event) {
		$.mobile.changePage( "home-screen.htm" );		
	});

	$(".j-mobileShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeIn(500);
    });
    $(".closeShare").on('tap', function(l) {
        l.preventDefault();
        $(".mobileShare").fadeOut(500);
    });

	// If the list of states is already in memory, no need to refetch from backend
	if( stateList != null ) {
		$("#stateMenu").html( $("#stateListTmpl").render(stateList) ).selectmenu('refresh', true);
	}
	else {
		$.getJSON( "service/states", function(data) {
			$("#stateMenu").html( $("#stateListTmpl").render(data) ).selectmenu('refresh', true); 		
		});
	}
		
	// Fetch the list of Sectors from backend
	$.getJSON( 'mobile/json-data/sectorList.json', function(sectorsList) {
		$("#sectorFilter fieldset:jqmData(role='controlgroup')").append( $("#sectorListTmpl").render(sectorsList) );
		// Create the Checkboxes
		$("#sectorFilter fieldset:jqmData(role='controlgroup')").trigger( "create" );
		// Reset styling on the group of checkboxes
		$("#sectorFilter fieldset:jqmData(role='controlgroup')").controlgroup("refresh");
	});
	
	// Fetch the list of Green House Gases from the backend
	$.getJSON( "mobile/json-data/gasList.json", function(gases) {
		$("#ghgFilter fieldset:jqmData(role='controlgroup')").html( $("#ghgListTmpl").render(gases) );
		$("#ghgFilter fieldset:jqmData(role='controlgroup')").trigger("create");
		$("#ghgFilter fieldset:jqmData(role='controlgroup')").controlgroup("refresh");		
	});
	
	
	// Register our click listeners
	$('#sectorFilterLink').on('tap', function() {
		hideAllDivsExcept('#sectorFilter');
		$("#sectorFilter fieldset:jqmData(role='controlgroup')").controlgroup("refresh");
	});
	
	$('#stateFilterLink').on('tap', function() {
		hideAllDivsExcept('#stateFilter');
	});
	
	$('#ghgFilterLink').on('tap', function() {
		hideAllDivsExcept('#ghgFilter');
		$("#ghgFilter fieldset:jqmData(role='controlgroup')").controlgroup("refresh");		
	});

	$('#emissionFilterLink').on('tap', function() {
		hideAllDivsExcept('#emissionRangeFilter');
	});
	
	
	// Code for Dual Range Slider
	$('#emission_slider_min').change( function() {
	    var min = parseInt( $(this).val() );
	    var max = parseInt( $('#emission_slider_max').val()  );
	    // move the slider but do not pass the other slider
	    if (min > max - 4000000) {
	        $(this).val(max - 4000000);
	        $(this).slider('refresh');
	    }
	    // Display the new values
	    $('#rangeValues').html( $('#emission_slider_min').val() + ' - ' + $('#emission_slider_max').val() );
	});

	$('#emission_slider_max').change( function() {
	    var min = parseInt( $('#emission_slider_min').val() );
	    var max = parseInt( $(this).val() );
	    // move the slider but do not pass the other slider
	    if (min  + 4000000 > max) {
	        $(this).val(min  + 4000000);
	        $(this).slider('refresh');
	    }
	    // Display the new values
	    $('#rangeValues').html( $('#emission_slider_min').val() + ' - ' + $('#emission_slider_max').val() );	    
	});
	
	$('#saveBtn').on('tap', function() {  
		// Convert the Filter page's inputs to an Ajax readable data array
		var formData = $("#filterForm").serializeArray();
		// TODO :: Quick fix for a bug
		// JQM adds a hidden input for each state the user selects multiple times.  
		// Here we simply get the last selected state the user selected.
		var st = "st";
		// Assuming that the array starts with "STATES"
		$.each( formData, function(i, obj) {
			// If we reached the end of the list of states, only keep the last state
		    if( st != formData[i].name ) {
		        // remove all previously selected states        
		    	formData.splice(0,i-1);
		    	// break out of the loop
		        return false;
		    }
		    st = formData[i].name;
		});
		
		//update RQ to match fromData
		var tempArr = new Array();
		tempArr.push(requestObj.gases);
		tempArr.push(requestObj.sectors);
		tempArr.push(requestObj.eRange);
		tempArr.push(requestObj.options);
		$.each(formData, function(idx, obj){
			$.each(tempArr, function(tIdx, tObj){
				if(tObj[obj.name]){//match found
					var rqName = obj.name;  //matched rq field
					tObj[obj.name] = obj.value;
				}
			});
		});
		
		// User must select a specific state, pan to the state when Map is clicked
		mapAction = PAN_TO_STATE;
		// Update the request service call
		prevRequest = "facilityList";
		
        $.ajax({
            type: 		"GET",
            url: 		"service/facilityList/"+vYear,
            cache: 		false,
            data: 		formData,
            dataType:	"JSON",
            success: 	onSuccess,
            error: 		onError
        });
	});
	
	// Hide all filter sections except for Sector Filter
	hideAllDivsExcept('#sectorFilter');

});

function onSuccess(response) {
	// Add commas to each Facility's emission value before appending to main list of facilities
	$.each( response, function(i, facility) {
		  facility.emissions = addCommas(facility.emissions);
	});
	facilitiesList = response;
	$.mobile.changePage(facilityListPage);	
}

function onError() {
	alert('Error Submitting Form');
}

// Get last page visited before this one\
//$("#page-id").live('pagebeforeshow', function(event, data) {
//    console.log("the previous page was: " + data.prevPage.attr('id'));
//	OR
//		history.back();
//		return false;
//	OR
//		$('backbtn').trigger('click');
//});


/*
 * **********************************
 * Results-Map page using Bing Maps *
 * **********************************
 */

var bMap;
var mCenter;
var infoBox;
var pins = {};

var mapCanvas;
var myMarker;

// 'pageInit' is called before 'pageShow' and is only called once,
// Create the initial map and save a pointer reference to the map for later use
var pinLayer = [];
var infoboxLayer = [];
//var viewChangeStartHandler;

$(document).on('pageinit', '#ResultsMap', function(event) {
	
	// This will prevent event triggering more then once
	if(event.handled !== true)      
        event.handled = true;
    else 
    	console.log("ResultsMap already initialized"); 
    
	
	$(".j-mobileShare").on("tap", function(l) {
        l.preventDefault();
        $(".mobileShare").fadeIn(500);
    });
    $(".closeShare").on("tap", function(l) {
        l.preventDefault();
        $(".mobileShare").fadeOut(500);
    });

	$('.homeLogo').on('tap', function(event) {
		$.mobile.changePage( "home-screen.htm" );		
	});

	// Create our map if it has not been already created or initialized
	/* TODO: (*******) Map dissappears when navigating in this order: (fixed)
		ResultsList -> FacilityInfo -> Map -> back -> back -> "Near Me" -> No Map */
	if( !mapCanvas ) {
		// Initially center map around the center of the US
		if( !mCenter )
			mCenter = new Microsoft.Maps.Location(39, -97);
		
		// Create our Map and save a pointer when done
	    $('#map_canvas').gmap({
			credentials:		'ArPc7TK61sxfYZKAlkoUwL2Mg-e-W9wH34qYEf0VwQbmm_Lr2c63-4Gd-W02V9PF',	
			enableSearchLogo: 	false, 
			showBreadcrumb: 	false, 
			showMapTypeSelector: false,
			enableClickableLogo: false, 
			showScalebar:		false, 
			center: 	mCenter,	// Initially center map around the center of the US
			zoom : 		8,
			mapTypeId : Microsoft.Maps.MapTypeId.auto,
			callback: 	function() {
				mapCanvas = this;
			}
		}).bind( 'init', function(event, temp_map) {
		    // Save the Map pointer globally
		    bMap = temp_map; 
			// Define the Map's maximum and minimum zoom range
			bMap.getZoomRange = function() {
				return {
					max: 22,
					min: 5
				};
			};
			//Create two layers, one for pushpins, the other for the infobox. This way the infobox will always be above the pushpins.
            pinLayer = new Microsoft.Maps.EntityCollection();
            infoboxLayer = new Microsoft.Maps.EntityCollection();
            // Add the two layers to the map
            bMap.entities.push(pinLayer);
            bMap.entities.push(infoboxLayer);
		    // Make sure Facilities are shown whenever User pans the map
            viewChangedHandler = Microsoft.Maps.Events.addThrottledHandler( bMap, 'viewchangeend', viewChangeEnd, 250);
			// Attach a handler to the event that gets fired whenever the map's view is about to change 
			// Forcing the User to a minimum/maximum zoom level, can be jittery
	        // close the infobox when the map is panned or zoomed
			Microsoft.Maps.Events.addHandler( bMap, 'viewchangestart', viewChangeStart ); 
		});
	}
});

function viewChangeStart(event) {
	restrictZoom(event);
	closeInfoBox(event);
}

function viewChangeEnd(event) {
	// If the current Infobox is visible and outside the Map's boundary, hide it
	if (pinInfobox != undefined &&
			pinInfobox.getVisible() &&
			!bMap.getBounds().contains( pinInfobox.getLocation() )) {
		pinInfobox.setOptions({visible:false});
	}
	// Only show Facilities in the current view if User previously selected "Near Me"
//	if( prevPageId != FACILITY_INFO && $.mobile.activePage.attr("id") == RESULTS_MAP )
	if( $.mobile.activePage.attr("id") == RESULTS_MAP )		
		showFacilitiesNearby(event);
}

function closeInfoBox(e) {	
	// If the current Infobox is visible and outside the Map's boundary, hide it
	if (pinInfobox != undefined &&
			pinInfobox.getVisible() &&
			!bMap.getBounds().contains( pinInfobox.getLocation() )) {
		pinInfobox.setOptions({visible:false});
	}
}

function setCoordsPosition(latitude, longitude) {
	mCenter = new Microsoft.Maps.Location(latitude, longitude);
	bMap.setView({
		center: mCenter,
		zoom: 	9
	});	
}
function setVectorPosition(position) {
	setCoordsPosition( position.latitude, position.longitude );
}


function restrictZoom() {
	if (bMap.getZoom() < bMap.getZoomRange().min) {
		bMap.setView({
			zoom: bMap.getZoomRange().min,
			animate: false
		});
	} else if (bMap.getZoom() > bMap.getZoomRange().max) {
		bMap.setView({
			zoom: bMap.getZoomRange().max,
			animate: false
		});
	}
}

var location_timeout;
function initiate_geolocation(event) {
	// Prevent the external Hyperlink from working
	event.preventDefault();    
	event.stopPropagation();
	//since we've selected near-me, remove state boundaries
	requestObj.options.st = "US";
    // Attempt to retrieve the current user's Geo Location
	if( userLocation == undefined && navigator.geolocation) { 
//		location_timeout = setTimeout("geolocation_timedout()", 3000);
    	navigator.geolocation.getCurrentPosition( handle_geolocation_success, handle_geolocation_error, {
    		maximumAge : 60000,
            timeout : 12000,
            enableHighAccuracy : false
        });    	
    }
	// If the User's location is already known then simply pan to it
	else if( userLocation ) {
		mapAction = PAN_TO_USER;
//		$.mobile.changePage('results-map.htm', {data: {prevPageId:	HOMEPAGE} });
		$.mobile.changePage('results-map.htm');
	}
	else {
    	alert("Sorry, your device does not support Geolocation");
    }
	return false;
}  

// Call back function called after the user's Geo Location has been found
function handle_geolocation_success(position) {
	// Reset the timeout
//	clearTimeout(location_timeout);
	
	isSharingLocation = true;
	userLocation = new Microsoft.Maps.Location(position.coords.latitude, position.coords.longitude);

	// If we are initiating Geolocation we are probably panning to the User's location
	mapAction = PAN_TO_USER;
	// show the Map page
	$.mobile.changePage('results-map.htm');
	/*
	$.mobile.changePage('results-map.htm', {
		data: {
			prevPageId:	HOMEPAGE
//			mapActionL:	PAN_TO_USER
		}
	});
	*/
} 

function geolocation_timedout() {
	// Reset the timeout
	clearTimeout(location_timeout);	
	alert("Sorry, Position timed out");
}

function handle_geolocation_error(err) {
	// Reset the timeout
//	clearTimeout(location_timeout);
	// TODO: Make these messages more meaningful
	switch(err.code) {
	case 1:
		alert("This feature requires the use of location services, please enable them on your device and try again.");
		break;
	case 2:
		alert("Your position is currently unavailable, please check your connection and try again.");
		break;
	case 3:
		alert("We could not find your position in a timely manner, please check your connection and try again.");
		break;
	}
}

function displayUserPin() {
	if (pins[0] == undefined) {
//		map.entities.clear(); //
		var myLocation = new Microsoft.Maps.Pushpin(userLocation, {
			icon: 'mobile/img/bluedot_retina.png',
//			htmlContent: '<img src="mobile/img/bluedot_retina.png" style="width: 16px; height: auto; display: none;">',
//			height: 17,
//			width: 17,
			height: 32,
			width: 32,
			anchor: new Microsoft.Maps.Point(8, 8),
			typeName: 'myLocation2',
			draggable: false				
		});
		myLocation.id = 0;
		pinLayer.push(myLocation);
//		bMap.entities.push(myLocation);
		pins[0] = myLocation;
	} 
}

var coordinates = [];

function removePinsOutOfBounds( mapBounds ) {
	// If no bounds are given, assume that we want to clear the map
	if( mapBounds == null ) {
		// Reset the array of pin objects
//		pins.length = 0;
//		pins.splice(0, pins.length);
		pins = [];
		// clear the visual pins from the map
		pinLayer.clear();
		// No need to continue this function
		return;
	}
	
	// If Map bounds are given, then loop through all Map 
	// entities and remove only what is not visible on screen
	for ( var i=0; i < pinLayer.getLength(); i++ ) {
		var item = pinLayer.get(i);
		// If this entity is not the User location beacon and is not within the Map view remove ti
		if (item.id != 0 && !mapBounds.contains(item.getLocation())) {
			delete pins[item.id];
//			bMap.entities.remove(item);
			pinLayer.remove(item);
		}
	}	
}

function showFacilitiesNearby(position) {
	// Get the Southwest and Northeast boundaries
	sw = new Microsoft.Maps.Location( bMap.getBounds().getSouth(),bMap.getBounds().getWest() );
	ne = new Microsoft.Maps.Location( bMap.getBounds().getNorth(),bMap.getBounds().getEast() );
	// Get the actual Map boundaries and remove
	// all pins outside of the current viewable area
	removePinsOutOfBounds( bMap.getBounds() );
	
	//update state to any/all, as this is 'near me' option
//	requestObj.options.st = "US";
	var request = buildRequest("facilitiesWithin", vYear);
	// TODO: Bounds set in the buildRequest function
//	request += "&lt=" + mCenter.latitude + "&ln=" + mCenter.longitude;
//	request += "&swlt=" + sw.latitude + "&swln=" + sw.longitude;
//	request += "&nelt=" + ne.latitude + "&neln=" + ne.longitude;
		
	$.getJSON(request, function(facilities) {
		// Add commas to each Facility's emission value before appending to main list of facilities
		$.each( facilities, function(i, facility) {
			  facility.emissions = addCommas(facility.emissions);
		});
		// Update the list of facilities to match the facilities the user
		// is currently viewing on the map, only if the user clicked "NearMe" on the homepage
		if( prevPageId == HOMEPAGE || prevPageId == RESULTS_LIST ) {
			// Save the viewable facilities
			facilitiesList = facilities;
			// Disable paging since we already have the facilities on the Map loaded into memory
			pagingEnabled = false;
		}
		// Clear the list of coordinates first
		coordinates = [];
		// loop through the returned list of Facilities
		$.each(facilities, function(i, facility) {
			if (pins[facility.id] == undefined) {
				// Create a coordinate from the facility's position
				var latLng = new Microsoft.Maps.Location(facility.latitude, facility.longitude);
				// Save the facility coordinates for future use
				coordinates.push(latLng);
				// Create the Map pin
				pin = new Microsoft.Maps.Pushpin(latLng, {
					icon: 'img/factory.png',
					typeName: 'factoryPushpin'
				});
				pin.id = facility.id;
				// Listener for Facility icon
				Microsoft.Maps.Events.addHandler(pin, 'click', displayInfobox);
				// Infobox displayed when a Facility icon is clicked
				info = new Microsoft.Maps.Infobox( latLng, {
					title: 			facility.name, 
					description: 	'<p>' + facility.city + ', ' + facility.state + '</p>',
					typeName: 		'infoBoxWrapper',
					width:			240,
//						height:			60,
					pushpin: 		pin, 
					visible: 		false, 
					showCloseButton: 	false,
					showPointer: 	true
				});
				info.id = facility.id;
				// Add Click listener, when infobox is clicked open the Facility Detail page
				Microsoft.Maps.Events.addHandler(info, 'click', function() {
			    	// Change to the Facility Details page with the selected Facility's ID
			    	$.mobile.changePage( facilityDetailPage, {
			    		data: {
			    			prevPageId:	RESULTS_MAP,
			    			facilityId: facility.id
			    		}
			    	});    	
				});
				// Add the pins and infoboxes to the Bing Map list of entities
				pinLayer.push(pin);
				infoboxLayer.push(info);
				pins[facility.id] = facility;
			}
		});
	});

}

/*
 * Helper methods
 */


function pinClicked(e) {
	if (infoBox != undefined) {
		infoBox.setOptions({visible:false});
	}
	infoBox = e.target._infobox;
	e.target._infobox.setOptions({visible:true});
}


var pinInfobox;

// Display an Infobox when a facility icon is clicked on the map
// and if need be, reposition the map so that the infobox is viewable
function displayInfobox(e) {
    if (e.targetType == "pushpin") {

    	if (pinInfobox != undefined) {
    		pinInfobox.setOptions({visible:false});
    	}
    	pinInfobox = e.target._infobox;
    	e.target._infobox.setOptions({visible:true});
    	
        pinInfobox.setOptions({ 
        	title: 			e.target.Title, 
        	description: 	e.target.Description, 
        	visible: 		true,
			offset: 		new Microsoft.Maps.Point(-120,84)
//        	offset: 		new Microsoft.Maps.Point( -pinInfobox.getWidth()/2, 80 )        	
        });
        pinInfobox.setLocation(e.target.getLocation());

        //A buffer limit to use to specify the infobox must be away from the edges of the map.
        var buffer = 4;

        var infoboxOffset = pinInfobox.getOffset();
        var infoboxAnchor = pinInfobox.getAnchor();
        var infoboxLocation = bMap.tryLocationToPixel(e.target.getLocation(), Microsoft.Maps.PixelReference.control);

        var dx = infoboxLocation.x + infoboxOffset.x - infoboxAnchor.x;
        var dy = infoboxLocation.y - 84 - infoboxAnchor.y;

        if (dy < buffer) {    //Infobox overlaps with top of map.
            //Offset in opposite direction.
            dy *= -1;

            //add buffer from the top edge of the map.
            dy += buffer;
        } else {
            //If dy is greater than zero than it does not overlap.
            dy = 0;
        }

        if (dx < buffer) {    //Check to see if overlapping with left side of map.
            //Offset in opposite direction.
            dx *= -1;

            //add a buffer from the left edge of the map.
            dx += buffer;
        } else {              //Check to see if overlapping with right side of map.
            dx = bMap.getWidth() - (infoboxLocation.x + infoboxAnchor.x + pinInfobox.getWidth()/2);

            //If dx is greater than zero then it does not overlap.
            if (dx > buffer) {
                dx = 0;
            } else {
                //add a buffer from the right edge of the map.
                dx -= buffer;
            }
        }

        //Adjust the map so infobox is in view
        if (dx != 0 || dy != 0) {
            bMap.setView({ centerOffset: new Microsoft.Maps.Point(dx, dy), center: bMap.getCenter() });
        }
    }
}

// Get a parameter from the URL by name
function getParameterByName( name ) {
  name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
  var regexS = "[\\?&]" + name + "=([^&#]*)";
  var regex = new RegExp(regexS);
  var results = regex.exec(window.location.search);
  if(results == null)
    return "";
  else
    return decodeURIComponent(results[1].replace(/\+/g, " "));
}

// Find a Facility from the list of facilities given its ID
function getFacilityById( id ) {
	var mFacility = null;
	$.each( facilitiesList, function(i, facility) {
		if( facility.id == id ) {
			mFacility = facility;
		}
	});
	return mFacility;
}

// Add commas to any number (taken from Main.js)
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

function stylizeFacilities(facility) {}


/*
 * Backend Service Calls
 */

/*
// Send User Feedback Email
$('#send-feedback').live("click", function() {
    
	var url = 'service/sendUserFeedback';
	var error = 0;
	var $contactpage = $(this).closest('.ui-page');
	var $contactform = $(this).closest('.contact-form');
	$('.required', $contactform).each(function (i) {
        if ($(this).val() === '') {
			error++;
        } 
	}); // each
	if (error > 0) {
			alert('Please fill in all the mandatory fields. Mandatory fields are marked with an asterisk *.');	
	} else {
		var name = $contactform.find('input[name="name"]').val();
//		var email = $contactform.find('input[name="email"]').val();	
		var message = $contactform.find('textarea[name="message"]').val();	

		//submit the form
		$.ajax({
			type: "GET",
			url: url,
			data: {
				name:name, 
//				email: email, 
				message: message
			},
            success: function (data) {
				if (data == 'success') {
					// show thank you 
					$contactpage.find('.contact-thankyou').show();
					$contactpage.find('.contact-form').hide();
				}  else {
					alert('Unable to send your message. Please try again.');
				}
			}
		}); //$.ajax

	}
	return false;
});
*/

/*
function getStateBounds( stateAbbrev ) {
	$.getJSON( 'service/getStateBounds' + stateAbbrev, function(data) {
		return data;
	});
}
*/

/* Show Elegant Error Message **/
/*
//show error message
$.mobile.showPageLoadingMsg( $.mobile.pageLoadErrorMessageTheme, $.mobile.pageLoadErrorMessage, true );
// hide after delay
setTimeout( $.mobile.hidePageLoadingMsg, 1500 );
*/