/**
 *
 * Created by alabdullahwi on 5/26/2015.
 */

/**
 * an abstraction to control the Map in the Map View and all that is related to it
 */

var NewMap = (function() {

    var _self;
    var Helper = (function() {
        return {
            removeSubstateToggle: function() {
                                            while (_self.controls[google.maps.ControlPosition.TOP_LEFT].length > 0) {
                                                _self.controls[google.maps.ControlPosition.TOP_LEFT].pop();
                                            }
                                        },
            createSubstateToggle : function() {
                                        //substate toggle logic is at UI because
                                        // it exists in different view and is not just a Map thing
                                        // creates a substate toggle only if a state is picked
                                            if (State.get()) {
                                                var toggleDiv = UI.create('Substate Toggle');
                                                _self.controls[google.maps.ControlPosition.TOP_LEFT].push(toggleDiv);
                                            }
                                        },
            createControlDiv    :  function(st) {
                                            if (st == 'TL' && DataSource.isNot('E')) {
                                                return;
                                            }

                                            var controlName = State.Helper.findFullName(st);
                                            if (controlName === '') {
                                                controlName = 'U.S. Mainland';
                                            }

                                            var controlDiv = document.createElement('div');
                                            // Set CSS styles for the DIV containing the control
                                            // Setting padding to 5 px will offset the control
                                            // from the edge of the map
                                            controlDiv.style.padding = '0px 0px 1px';

                                            // Set CSS for the control border
                                            var controlUI = document.createElement('div');
                                            controlUI.style.width = '100px';
                                            controlUI.style.backgroundColor = 'white';
                                            controlUI.style.borderStyle = 'solid';
                                            controlUI.style.borderWidth = '1px';
                                            controlUI.style.borderColor = '#777';
                                            controlUI.style.cursor = 'pointer';
                                            controlUI.style.textAlign = 'center';
                                            controlUI.title = 'Click to set the map to ' + controlName;
                                            controlDiv.appendChild(controlUI);

                                            // Set CSS for the control interior
                                            var controlText = document.createElement('div');
                                            controlText.style.fontFamily = 'Arial,sans-serif';
                                            controlText.style.fontSize = '11px';
                                            controlText.style.color = '#555';
                                            if (State.get() == st ) {
                                                controlText.style.backgroundColor = '#DDDDDD';
                                            }
                                            controlText.style.paddingLeft = '4px';
                                            controlText.style.paddingRight = '4px';
                                            controlText.innerHTML = controlName;
                                            controlUI.appendChild(controlText);

                                            google.maps.event.addDomListener(controlDiv, 'click', function() {
                                                if (DataSource.isOneOf(['E', 'L', 'I'])) {
                                                    State.set(st);
                                                    generateURL('');
                                                } else {
                                                    Map.draw();
                                                }
                                            });
                                            map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(controlDiv);
            },
            createAjaxLoader  : function() {
                                            // Create a div to hold the control.
                                            ajaxLoaderControlDiv = document.createElement('div');
                                            // Set CSS for the control border
                                            var controlUI = document.createElement('div');
                                            ajaxLoaderControlDiv.appendChild(controlUI);
                                            var controlText = document.createElement('div');
                                            controlText.innerHTML = '<img src="img/loadingBig.gif">';
                                            controlUI.appendChild(controlText);
                                            map.controls[google.maps.ControlPosition.TOP_CENTER].push(ajaxLoaderControlDiv);
            }
    }

    }());


    return {
        wipe   : function(thing) {
            if (thing == 'Substate Toggle') {
                Helper.removeSubstateToggle();
            }
        },
        create : function() {
                _self  = new google.maps.Map(document.getElementById('canvas-map'), {
                    center: new google.maps.LatLng(39, -97),
                    zoom: 4,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                });
                _self.polygons = [];
                _self.listeners = [];
                //TODO remove this global
                map = _self;
       },
       clearPolygons : function() {
           //TODO change map global into _self local
           for (var i = 0, polygon; polygon = map.polygons[i]; i++) {
               polygon.setMap(null);
           }
       },
        clear: function() {
            markerCluster.clearMarkers();
            for (var i = 0, polygon; polygon = map.polygons[i]; i++) {
                polygon.setMap(null);
            }
            while (map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].length > 0) {
                map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].pop();
            }
            for (var i = 0, listener; listener = map.listeners[i]; i++) {
                google.maps.event.removeListener(listener);
            }
            Helper.removeSubstateToggle();
        },
       fit : function(data) {
           var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(data.sw.lat, data.sw.lng), new google.maps.LatLng(data.ne.lat, data.ne.lng));
           //TODO replace global with local
           map.fitBounds(bounds);
       },
       draw : function() {
           //TODO remove this global
           num = State.getCode();
               var zoom = 4;
               var location = new google.maps.LatLng(39, -97);
               if (map == undefined) {
                   map = new google.maps.Map(document.getElementById('canvas-map'), {
                       center: location,
                       zoom: zoom,
                       mapTypeId: google.maps.MapTypeId.ROADMAP
                   });
                   map.polygons = [];
                   map.listeners = [];
                   _self = map;
               } else {
                   markerCluster.clearMarkers();

                   //clear out polygons (not sure what this means yet)
                   for (var i = 0, polygon; polygon = map.polygons[i]; i++) {
                       polygon.setMap(null);
                       polygon = null;
                   }
                   map.polygons = [];
                   //clear out controls
                   while (map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].length > 0) {
                       map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].pop();
                   }
                   //clear out listeners (not sure what this means yet)
                   for (var i = 0, listener; listener = map.listeners[i]; i++) {
                       google.maps.event.removeListener(listener);
                   }
                   Helper.removeSubstateToggle();
               }
               if (State.get() == "TL") {
                   map.panTo(location);
                   map.setZoom(4);
               }
               else if (State.get() != "") {
                   if(State.get() == 'MA'){
                       showPopupState(State.get());
                   }
                   if (County.get() != "" && MSA.get() == "") {
                       Backend.Bounds.loadForCounty();
                   }
                   else if (MSA.get() != "" && County.get() == "") {
                       Backend.Bounds.loadForMSA();
                   }
                   else {
                       Backend.Bounds.loadForState();
                   }
               } else {
                   map.panTo(location);
                   map.setZoom(4);
               }
               var basinCode = $('#basin').val();
               if (DataSource.isOneOf(['O', 'E']) && isOnshoreOnly()) {
                   jQuery.getJSON("service/basinsGeo/", function(data) {
                       jQuery.each(data, function() {
                           loadBasin(this, basinCode);
                       });
                   });
               }

               if ((DataSource.is('O') || isOnshoreOnly()) && basinCode != undefined && basinCode != null && basinCode != '') {
                   var url = "service/getBasinBounds/"+basinCode;
                   jQuery.getJSON(url, function(data) {
                       var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(data.sw.lat, data.sw.lng), new google.maps.LatLng(data.ne.lat, data.ne.lng));
                       map.fitBounds(bounds);
                   });
               }
               Helper.createAjaxLoader();
                //those states have Map controls for quick shortcuts to them as they are not in US mainland
               var controlStates = ['US','AK','HI','TL','AS','MP','GU','PR','VI'];
               controlStates.forEach(Helper.createControlDiv);
               Helper.createSubstateToggle();
               getFacilityMapData(FacOrLoc.get(),State.get(),County.get(), MSA.get(), LowE.get(),HighE.get(), reportingStatus, stateLevel, $("#emissionsType").val(), TribalLand.get());
           }
    }
}());

function plot(array,response,row,minValue,maxValue){
    var arr = [];
    var coords = [];
    var tmp;
    var num1;
    var num2;
    var poly;
    var color;
    arr = array;
    var stateNum;
    var fOpacity;

    var emission = 0;
    var label;
    if(response.getDataTable().getColumnLabel(2) == 'name'){
        stateNum = State.Helper.isState(response.getDataTable().getValue(row,2));
        label = response.getDataTable().getValue(row,2);
        jQuery.each(dbData, function(i, item) {
            if (dbData[i].id == stateNum) {
                emission = dbData[i].emission;
            }
        });
        if (mapSelector == 0) {
            color = '#FFFFFF'; // Facility
        } else {
            color = pickColor('Orange',minValue,maxValue,emission); // Heat
        }
    } else {
        stateNum = response.getDataTable().getValue(row,2);
        label = response.getDataTable().getValue(row,3);
        jQuery.each(dbData, function(i, item) {
            if (dbData[i].id == stateNum) {
                emission = dbData[i].emission;
            }
        });
        if (mapSelector == 0) {
            color = '#FFFFFF';
        } else {
            color = pickColor('Orange',minValue,maxValue,emission);
        }
    }
    //Now for every pair of values fill a data variable with the polygon coordinates
    coords = [];
    for(var j = 0; j< (arr.length/2); j++){
        num1 = arr[(j*2)];
        num2 = arr[(j*2)+1];
        tmp = new google.maps.LatLng(num2,num1);
        coords.push(tmp);
    }
    if (mapSelector == 0) {
        fOpacity = .1 // Facility - make the background show
    } else {
        fOpacity = .7 // Heat - make the heat map more opaque
    }
    poly = new google.maps.Polygon({
        paths: coords,
        strokeColor: '#999',
        strokeOpacity: .8,
        strokeWeight: 1,
        fillColor: color,
        fillOpacity: fOpacity,
        map: map,
        zIndex: 100
    });
    poly.set("countyNum", response.getDataTable().getValue(row,2));
    poly.set("label", label);
    google.maps.event.addListener(poly, "click", openInfoWindow);
    google.maps.event.addListener(poly, 'mouseover', function(e) {
        if (map.getZoom()<=8) {
            this.setOptions({fillOpacity: .7});
        } else {
            this.setOptions({fillOpacity: .3});
        }

    });
    google.maps.event.addListener(poly, 'mousemove', function(e) {
        if (poly.marker == undefined) {
            poly.marker = new MarkerWithLabel({
                position: e.latLng,
                draggable: false,
                raiseOnDrag: false,
                map: map,
                labelContent: poly.get("label"),
                labelAnchor: new google.maps.Point(-5,25),
                labelClass: "labels",
                labelStyle: {opacity: 1.0},
                icon: "http://placehold.it/1x1",
                visible: true,
                zIndex: 100000
            });
        }
        poly.marker.setPosition(e.latLng);
        poly.marker.setVisible(true);
    });
    google.maps.event.addListener(poly, 'mouseout', function() {
        this.setOptions({fillOpacity: .1});
        poly.marker.setVisible(false);
    });
    map.polygons.push(poly);
}

function drawMap(facOrLoc,stateIn,fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId) {
    //jQuery("#canvas").html('');
    //jQuery("#canvas").attr('style', 'background-color:#fff; height: 500px;');
    //var zoom,lat,lng;
    var zoom = 4;
    num = State.getCode();
    //if(num != 0){
    //	var obj = setStateInfo(stateIn);
    //	lat = obj[0];
    //	lng = obj[1];
    //	zoom = obj[2];
    //}
    //if(lat != undefined && lng != undefined){
    //	var location = new google.maps.LatLng(lat, lng);
    //	zoom = zoom;
    //} else {
    var location = new google.maps.LatLng(39, -97);
    //	zoom = 4;
    //}
    if (map == undefined) {
        map = new google.maps.Map(document.getElementById('canvas-map'), {
            center: location,
            zoom: zoom,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        });
        map.polygons = [];
        map.listeners = [];
    } else {
        markerCluster.clearMarkers();
        for (var i = 0, polygon; polygon = map.polygons[i]; i++) {
            polygon.setMap(null);
        }
        while (map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].length > 0) {
            map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].pop();
        }
        for (var i = 0, listener; listener = map.listeners[i]; i++) {
            google.maps.event.removeListener(listener);
        }
        while (map.controls[google.maps.ControlPosition.TOP_LEFT].length > 0) {
            map.controls[google.maps.ControlPosition.TOP_LEFT].pop();
        }
    }
    if (stateIn == "Tribal Land") {
        map.panTo(location);
        map.setZoom(4);
    }
    else if (stateIn != "") {
        var stateCode = State.getAbbr();
        if(stateCode == 'MA'){
            showPopupState(stateCode);
        }
        var url = "service/getStateBounds/"+stateCode;
        if (fipsCode != "" && msaCode == "") {
            url = "service/getCountyBounds/"+fipsCode;
        }
        if (msaCode != "" && fipsCode == "") {
            url = "service/getMsaBounds/"+msaCode;
        }
        jQuery.getJSON(url, function(data) {
            var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(data.sw.lat, data.sw.lng), new google.maps.LatLng(data.ne.lat, data.ne.lng));
            map.fitBounds(bounds);
        });
    } else {
        map.panTo(location);
        map.setZoom(4);
    }

    var basinCode = $('#basin').val();

    if (dataSource == 'O' || (dataSource == 'E' && isOnshoreOnly())) {
        jQuery.getJSON("service/basinsGeo/", function(data) {
            jQuery.each(data, function() {
                loadBasin(this, basinCode);
            });
        });
    }
    /*
     if (dataSource == 'I' || (dataSource == 'E' && isOnshoreOnly())) {
     var url = "service/getStateMsaShapes/"+stateCode;
     jQuery.getJSON(url, function(data) {
     jQuery.each(data, function() {
     loadMsa(this, msaCode);
     });
     });
     }*/

    if ((dataSource == 'O' || isOnshoreOnly()) && basinCode != undefined && basinCode != null && basinCode != '') {
        var url = "service/getBasinBounds/"+basinCode;
        jQuery.getJSON(url, function(data) {
            var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(data.sw.lat, data.sw.lng), new google.maps.LatLng(data.ne.lat, data.ne.lng));
            map.fitBounds(bounds);
        });
    }

    // Create a div to hold the control.
    ajaxLoaderControlDiv = document.createElement('div');

    // Set CSS for the control border
    var controlUI = document.createElement('div');
    ajaxLoaderControlDiv.appendChild(controlUI);

    var controlText = document.createElement('div');
    controlText.innerHTML = '<img src="img/loadingBig.gif">';
    controlUI.appendChild(controlText);

    map.controls[google.maps.ControlPosition.TOP_CENTER].push(ajaxLoaderControlDiv);

    // Create a div to hold the control.
    var usaControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    usaControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var alaskaControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    alaskaControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var hawaiiControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    hawaiiControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
        var tribalControlDiv = document.createElement('div');

        // Set CSS styles for the DIV containing the control
        // Setting padding to 5 px will offset the control
        // from the edge of the map
        tribalControlDiv.style.padding = '0px 0px 1px';

        // Set CSS for the control border
        var controlUI = document.createElement('div');
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
        var controlText = document.createElement('div');
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
    var samoaControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    samoaControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var marianaControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    marianaControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var guamControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    guamControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var prControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    prControlDiv.style.padding = '0px 0px 1px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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
    var viControlDiv = document.createElement('div');

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    //viControlDiv.style.padding = '0px';

    // Set CSS for the control border
    var controlUI = document.createElement('div');
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
    var controlText = document.createElement('div');
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

    var stateLevelSelector = document.createElement('div');

    var countyBtn = document.createElement('div');
    countyBtn.setAttribute('class','softBtnLeftMap');
    countyBtn.setAttribute('id', 'countyBtnMap');
    countyBtn.style.borderRightStyle = 'solid';
    countyBtn.style.borderRightWidth = '1px';
    countyBtn.style.borderRightColor = '#CCC';
    countyBtn.innerHTML = '<a href="javascript:setStateLevel(ryear, stateAbbr, 0, 1)">County</a>';

    var msaBtn = document.createElement('div');
    msaBtn.setAttribute('class', 'softBtnRightMap');
    msaBtn.setAttribute('id', 'msaBtnMap');
    msaBtn.style.marginRight = '15px';

    msaBtn.innerHTML = '<a href="javascript:setStateLevel(ryear, stateAbbr, 1, 1)">Metro Area</a>';
    if (stateLevel == 0) {
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
    stateLevelSelector.setAttribute('id','substateMapToggle');

    stateLevelSelector.style.padding = '4px';
    stateLevelSelector.style.paddingLeft = '80px';
    //hide at first
    stateLevelSelector.style.display = 'none';

    map.controls[google.maps.ControlPosition.TOP_LEFT].push(stateLevelSelector);
/*    if ((dataSource == 'E' || dataSource == 'I') && stateIn != "" && stateIn != "Tribal Land") {
    }*/

    google.maps.event.addDomListener(usaControlDiv, 'click', function() {
        State.set('');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    google.maps.event.addDomListener(alaskaControlDiv, 'click', function() {
        State.set('AK');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('AK');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE,reportingStatus), tribalLandId;
        }
    });

    google.maps.event.addDomListener(hawaiiControlDiv, 'click', function() {
        State.set('HI');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('HI');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    if (dataSource == 'E') {
        google.maps.event.addDomListener(tribalControlDiv, 'click', function() {
            State.set('TL');
            if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
                $("#parentState").val('TL');
                generateURL('');
            } else {
                drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
            }
        });
    }

    google.maps.event.addDomListener(samoaControlDiv, 'click', function() {
        State.set('AS');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('AS');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    google.maps.event.addDomListener(marianaControlDiv, 'click', function() {
        State.set('MP');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('MP');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    google.maps.event.addDomListener(guamControlDiv, 'click', function() {
        State.set('GU');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('GU');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    google.maps.event.addDomListener(prControlDiv, 'click', function() {
        State.set('PR');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('PR');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    google.maps.event.addDomListener(viControlDiv, 'click', function() {
        State.set('VI');
        if (dataSource == 'E' || dataSource == 'L' || dataSource == 'I') {
            $("#parentState").val('VI');
            generateURL('');
        } else {
            drawMap(facOrLoc,State.getFullName(), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
        }
    });

    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(viControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(prControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(guamControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(marianaControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(samoaControlDiv);
    if (dataSource == 'E') {
        map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(tribalControlDiv);
    }
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(hawaiiControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(alaskaControlDiv);
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(usaControlDiv);

    if (mapSelector == 0) {
        getFacilityMapData(facOrLoc,stateIn,fipsCode, msaCode, lowE,highE, reportingStatus, stateLevel, $("#emissionsType").val(), tribalLandId);
    } else  {
        getHeatMapData(facOrLoc,stateIn,fipsCode,lowE,highE, reportingStatus);
    }
}
function loadMsa(data, msaCode) {
    var color = data.color;
    var shapes = data.shapes;
    jQuery.each(shapes, function() {
        var shell = this.shell;
        var holes = this.holes;
        var shellCoords = [];
        var paths = [];
        jQuery.each(shell, function() {
            var latLng = new google.maps.LatLng(this.lat, this.lng);
            shellCoords.push(latLng);
        });
        jQuery.each(holes, function() {
            hole = this;
            var holeCoords = [];
            jQuery.each(hole, function() {
                var latLng = new google.maps.LatLng(this.lat, this.lng);
                holeCoords.push(latLng);
            });
            paths.push(holeCoords);
        });
        paths.push(shellCoords);
        var poly;
        if (data.id == msaCode) {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#FF0000',
                strokeOpacity: 1,
                strokeWeight: 1,
                fillColor: '#FFFFFF',
                fillOpacity: .5,
                map: map
            });
        } else {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#FF0000',
                strokeOpacity: .6,
                strokeWeight: 1,
                fillColor: '#FFFFFF',
                fillOpacity: .4,
                map: map
            });
        }
        poly.set("msaCode", data.id);
        poly.set("msaName", data.name);
        map.polygons.push(poly);
        google.maps.event.addListener(poly, "click", selectMsa);
        google.maps.event.addListener(poly, 'mouseover', function(e) {
            this.setOptions({fillOpacity: .8});
        });
        google.maps.event.addListener(poly, 'mousemove', function(e) {
            if (poly.marker == undefined) {
                poly.marker = new MarkerWithLabel({
                    position: e.latLng,
                    draggable: false,
                    raiseOnDrag: false,
                    map: map,
                    labelContent: poly.get("msaName"),
                    labelAnchor: new google.maps.Point(-5,25),
                    labelClass: "labels",
                    labelStyle: {opacity: 1.0},
                    icon: "http://placehold.it/1x1",
                    visible: true,
                    zIndex: 100000
                });
            }
            poly.marker.setPosition(e.latLng);
            poly.marker.setVisible(true);
        });
        google.maps.event.addListener(poly, 'mouseout', function() {
            this.setOptions({fillOpacity: .4});
            poly.marker.setVisible(false);
        });
    });
}
function loadCounty(data, fipsCode) {
    var color = data.color;
    var shapes = data.shapes;
    jQuery.each(shapes, function() {
        var shell = this.shell;
        var holes = this.holes;
        var shellCoords = [];
        var paths = [];
        jQuery.each(shell, function() {
            var latLng = new google.maps.LatLng(this.lat, this.lng);
            shellCoords.push(latLng);
        });
        jQuery.each(holes, function() {
            hole = this;
            var holeCoords = [];
            jQuery.each(hole, function() {
                var latLng = new google.maps.LatLng(this.lat, this.lng);
                holeCoords.push(latLng);
            });
            paths.push(holeCoords);
        });
        paths.push(shellCoords);
        var poly;
        if (data.id == fipsCode) {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#FF0000',
                strokeOpacity: 1,
                strokeWeight: 1,
                fillColor: '#FFFFFF',
                fillOpacity: .5,
                map: map
            });
        } else {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#FF0000',
                strokeOpacity: .6,
                strokeWeight: 1,
                fillColor: '#FFFFFF',
                fillOpacity: .4,
                map: map
            });
        }
        poly.set("fipsCode", data.id);
        poly.set("countyName", data.name);
        map.polygons.push(poly);
        google.maps.event.addListener(poly, "click", selectCounty);
        google.maps.event.addListener(poly, 'mouseover', function(e) {
            this.setOptions({fillOpacity: .8});
        });
        google.maps.event.addListener(poly, 'mousemove', function(e) {
            if (poly.marker == undefined) {
                poly.marker = new MarkerWithLabel({
                    position: e.latLng,
                    draggable: false,
                    raiseOnDrag: false,
                    map: map,
                    labelContent: poly.get("countyName"),
                    labelAnchor: new google.maps.Point(-5,25),
                    labelClass: "labels",
                    labelStyle: {opacity: 1.0},
                    icon: "http://placehold.it/1x1",
                    visible: true,
                    zIndex: 100000
                });
            }
            poly.marker.setPosition(e.latLng);
            poly.marker.setVisible(true);
        });
        google.maps.event.addListener(poly, 'mouseout', function() {
            this.setOptions({fillOpacity: .4});
            poly.marker.setVisible(false);
        });
    });
}
function loadBasin(data, basinCode) {
    var color = data.color;
    var shapes = data.shapes;
    jQuery.each(shapes, function() {
        var shell = this.shell;
        var holes = this.holes;
        var shellCoords = [];
        var paths = [];
        jQuery.each(shell, function() {
            var latLng = new google.maps.LatLng(this.lat, this.lng);
            shellCoords.push(latLng);
        });
        jQuery.each(holes, function() {
            hole = this;
            var holeCoords = [];
            jQuery.each(hole, function() {
                var latLng = new google.maps.LatLng(this.lat, this.lng);
                holeCoords.push(latLng);
            });
            paths.push(holeCoords);
        });
        paths.push(shellCoords);
        var poly;
        if (data.id == basinCode) {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#000',
                strokeOpacity: .8,
                strokeWeight: 3,
                fillColor: color,
                fillOpacity: .4,
                map: map
            });
        } else {
            poly = new google.maps.Polygon({
                paths: paths,
                strokeColor: '#555',
                strokeOpacity: .8,
                strokeWeight: 1,
                fillColor: color,
                fillOpacity: .4,
                map: map
            });
        }
        poly.set("basinCode", data.id);
        poly.set("basinName", data.name);
        map.polygons.push(poly);
        google.maps.event.addListener(poly, "click", selectBasin);
        google.maps.event.addListener(poly, 'mouseover', function(e) {
            this.setOptions({fillOpacity: .7});
        });
        google.maps.event.addListener(poly, 'mousemove', function(e) {
            if (poly.marker == undefined) {
                poly.marker = new MarkerWithLabel({
                    position: e.latLng,
                    draggable: false,
                    raiseOnDrag: false,
                    map: map,
                    labelContent: poly.get("basinName"),
                    labelAnchor: new google.maps.Point(-5,25),
                    labelClass: "labels",
                    labelStyle: {opacity: 1.0},
                    icon: "http://placehold.it/1x1",
                    visible: true,
                    zIndex: 100000
                });
            }
            poly.marker.setPosition(e.latLng);
            poly.marker.setVisible(true);
        });
        google.maps.event.addListener(poly, 'mouseout', function() {
            this.setOptions({fillOpacity: .5});
            poly.marker.setVisible(false);
        });
    });
}
function loadFacilities(facilities) {
    var marker;
    var latLng;

    var stylesArr = resolveMarkerClusterStyle(reportingStatus);
    markerCluster = new MarkerClusterer(map, [], {gridSize: 50, styles: stylesArr});
    jQuery.each(facilities, function() {
        var facilityId = this.id;
        if (this.sa == null) {
            var latLng = new google.maps.LatLng(this.lt, this.ln);
            var industry = 'img/factory.png';
            var color = "black";
            if (this.reportingStatus != null) {
                if (this.reportingStatus == 'POTENTIAL_DATA_QUALITY_ISSUE')  		{	color = "orange"}
                if (this.reportingStatus == 'STOPPED_REPORTING_UNKNOWN_REASON') 	{	color = "red"	}
                if (this.reportingStatus == 'STOPPED_REPORTING_VALID_REASON')	  	{	color = "gray"	}
                industry = 'img/factory-' + color + '.png';
            }
            marker = new google.maps.Marker({
                animation: google.maps.Animation.DROP,
                position: latLng,
                icon: industry
            });
            google.maps.event.addListener(marker, 'click', function() {
                displayFacilityDetail(facilityId);
            });
            google.maps.event.addListener(marker, 'mouseover', function() {
                if (facilityId != facilityFocus || ryear != facilityFocusYear) {
                    jQuery.ajax({
                        type: 'GET',
                        url: 'service/facilityHover/'+ryear,
                        data: "id="+facilityId+"&ds="+dataSource+"&et="+$("#emissionsType").val(),
                        success: function(response) {
                            facilityFocus = facilityId;
                            facilityFocusYear = ryear;
                            facilityContent = response;
                            facilityHoverTip.setContent(response);
                            facilityHoverTip.setPosition(latLng);
                            facilityHoverTip.setOptions({pixelOffset: new google.maps.Size(0,-25)});
                            facilityHoverTip.open(map);
                        }
                    });
                } else {
                    facilityHoverTip.open(map);
                }
                google.maps.event.addListener(facilityHoverTip, 'domready', function() {
                    document.getElementById('hoverTip').parentNode.style.overflow=''; // Workaround for PUB-119
                });
            });
            google.maps.event.addListener(marker, 'mouseout', function() {
                facilityHoverTip.close();
            });
            markerCluster.addMarker(marker, false);
        } else {
            var id = this.sa.id;
            var color = this.sa.color;
            var shapes = this.sa.shapes;
            var centroidLatLng = new google.maps.LatLng(this.sa.lt, this.sa.ln);
            jQuery.each(shapes, function() {
                var shell = this.shell;
                var holes = this.holes;
                var shellCoords = [];
                var paths = [];
                jQuery.each(shell, function() {
                    var latLng = new google.maps.LatLng(this.lat, this.lng);
                    shellCoords.push(latLng);
                });
                jQuery.each(holes, function() {
                    hole = this;
                    var holeCoords = [];
                    jQuery.each(hole, function() {
                        var latLng = new google.maps.LatLng(this.lat, this.lng);
                        holeCoords.push(latLng);
                    });
                    paths.push(holeCoords);
                });
                paths.push(shellCoords);
                /*					#* var encodedPaths = [];
                 jQuery.each(paths, function() {
                 var encodeString = google.maps.geometry.encoding.encodePath(this);
                 encodedPaths.push(encodeString);
                 });
                 var decodedPaths = [];
                 jQuery.each(encodedPaths, function() {
                 var decodedPath = google.maps.geometry.encoding.decodePath(this);
                 decodedPaths.push(decodedPath);
                 }); *#*/
                var poly;
                poly = new google.maps.Polygon({
                    //## paths: decodedPaths,
                    paths: paths,
                    strokeColor: '#CCC55',
                    strokeOpacity: .8,
                    strokeWeight: 1,
                    fillColor: color,
                    fillOpacity: .5,
                    map: map,
                    zIndex: 200
                });
                map.polygons.push(poly);
                google.maps.event.addListener(poly, 'click', function() {
                    displayFacilityDetail(facilityId);
                });
                google.maps.event.addListener(poly, 'mouseover', function(e) {
                    this.setOptions({fillOpacity: .9});
                    if (facilityId != facilityFocus || ryear != facilityFocusYear) {
                        jQuery.ajax({
                            type: 'GET',
                            url: 'service/facilityHover/'+ryear,
                            data: "id="+facilityId+"&ds="+dataSource+"&et="+$("#emissionsType").val(),
                            success: function(response) {
                                facilityFocus = facilityId;
                                facilityFocusYear = ryear;
                                facilityContent = response;
                                facilityHoverTip.setContent(response);
                                facilityHoverTip.setPosition(e.latLng);
                                facilityHoverTip.setOptions({pixelOffset: new google.maps.Size(0,0)});
                                facilityHoverTip.open(map);
                            }
                        });
                    } else {
                        facilityHoverTip.setPosition(e.latLng);
                        facilityHoverTip.open(map);
                    }
                    google.maps.event.addListener(facilityHoverTip, 'domready', function() {
                        document.getElementById('hoverTip').parentNode.style.overflow=''; // Workaround for PUB-119
                    });
                });
                google.maps.event.addListener(poly, 'mouseout', function() {
                    this.setOptions({fillOpacity: .5});
                    facilityHoverTip.close();
                });
            });
        }
    });
    map.controls[google.maps.ControlPosition.TOP_CENTER].pop();
}
function getPolygonFacilityMapData(response) {

    var numRows = response.getDataTable().getNumberOfRows();
    for(var i = 0; i < numRows; i++) {
        str = response.getDataTable().getValue(i,0);
        parseToArray(str,response,i,0,0);
    }
    loadFacilities(dbData);
}


function drawStateOverlays(data){
    dbData = data;
    if(State.getCode() != 0){ //COUNTIES
        /********** THESE TABLES ARE NO LONGER PUBLIC.
         var queryText = encodeURIComponent("SELECT 'geometry', 'value', 'FIPS formula', 'County Name','State Abbr.' FROM 210217 WHERE 'STATE num' = '"+num+"'");
         var query = new google.visualization.Query('http://www.google.com/fusiontables/gvizdata?tq='  + queryText);
         if (mapSelector == 0) {
			query.send(getPolygonFacilityMapData);
		} else {
			query.send(getPolygonHeatMapData);
		}  ***********/
    } else { //STATES


        //load states from local JSON file instead from webservice
        jQuery.ajax({
            type: 'GET',
            url: 'service/getStateBounds/local',
            success: function(response) {

                response.getDataTable = function() {
                    return response;
                };

                response.getNumberOfRows = function() {
                    return response.length;
                };

                response.getValue = function(x,y) {
                    return response[x].c[y].v;
                };

                response.getColumnLabel = function(x) {


                    if (x == 0) {
                        return "geometry";
                    }

                    if (x==1) {
                        return "value";
                    }
                    if (x==2) {
                        return "name";
                    }
                    else {
                        return "Error: should be in the range of [0...2]";
                    }



                };


                getPolygonFacilityMapData(response);

            }
        });


        /*	var queryText = encodeURIComponent("SELECT 'geometry', 'value', 'name' FROM 926036");
         var query = new google.visualization.Query('http://www.google.com/fusiontables/gvizdata?tq='  + queryText);
         if (mapSelector == 0) {
         query.send(getPolygonFacilityMapData);
         } else {
         query.send(getPolygonHeatMapData);
         }*/
    }
}
