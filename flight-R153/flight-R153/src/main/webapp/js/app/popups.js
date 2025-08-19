/**
 * Created by alabdullahwi on 5/26/2015.
 */

var Popups = (function() {
    
    return {
        
        showStatePopup: function(id) {
            var randNum =   Math.floor( Math.random()*100000000 );
            // send request to show popup to backend
            jQuery.ajax({
                type: 'GET',
                url: 'service/statePopupPermission?rand='+randNum,
                data: "id="+id,
                success: function(response) {
                    // if permitted show the popup
                    if( response == "show" ) {
                        // show the popup box
                        $("#popout-container-state").stop(true, true)
                            .animate({ bottom: '0px' }, 'slow', animationComplete )
                            .delay(5000)
                            .animate({ bottom: '-110px' }, 'slow', animationComplete);
                        // Remove any text previously displayed on the box
                        $("#popout-content-state div").stop(true, true).hide();
                        // Show the corresponding help link text
                        $( '#' + id + '_infolink').stop(true, true).fadeIn('slow');
                    }
                }

            });
        }
    }
    
    
})() 

function showPopup(id) {
    var randNum =   Math.floor( Math.random()*100000000 );
    // send request to show popup to backend
    jQuery.ajax({
        type: 'GET',
        url: 'service/sectorPopupPermission?rand='+randNum,
        data: "id="+id,
        success: function(response) {
            // if permitted show the popup
            if( response == "show" ) {
                // show the popup box
                $("#popout-container").stop(true, true)
                    .animate({ bottom: '0px' }, 'slow', animationComplete )
                    .delay(5000)
                    .animate({ bottom: '-160px' }, 'slow', animationComplete);
                // Remove any text previously displayed on the box
                $("#popout-content div").stop(true, true).hide();
                // Show the corresponding help link text
                $( '#' + id + '_infolink').stop(true, true).fadeIn('slow');
            }
        }

    });
}

function popoutHoverIn() {
    $("#popout-container").stop(true, true);
}

function popoutHoverOut() {
    $("#popout-container").stop(true, true).delay(3000).animate({ bottom: '-160px' }, 'slow', animationComplete );
}

//register popup close button
$('#popout-container .close').click( function() {
    $("#popout-container").stop(true, true).animate({ bottom: '-160px' }, 'slow', animationComplete );
});

function showPopupState(id) {
    var randNum =   Math.floor( Math.random()*100000000 );
    // send request to show popup to backend
    jQuery.ajax({
        type: 'GET',
        url: 'service/statePopupPermission?rand='+randNum,
        data: "id="+id,
        success: function(response) {
            // if permitted show the popup
            if( response == "show" ) {
                // show the popup box
                $("#popout-container-state").stop(true, true)
                    .animate({ bottom: '0px' }, 'slow', animationComplete )
                    .delay(5000)
                    .animate({ bottom: '-110px' }, 'slow', animationComplete);
                // Remove any text previously displayed on the box
                $("#popout-content-state div").stop(true, true).hide();
                // Show the corresponding help link text
                $( '#' + id + '_infolink').stop(true, true).fadeIn('slow');
            }
        }

    });
}

$(".state-infobox").live("click", function(event) {
    var stateCode = $(this).val();
    if(stateCode == "MA"
       || stateCode == "WA"){
        showPopupState(stateCode);
    }
});

function showGhgPopup(id) {
    var randNum =   Math.floor( Math.random()*100000000 );
    // send request to show popup to backend
    jQuery.ajax({
        type: 'GET',
        url: 'service/ghgPopupPermission?rand='+randNum,
        data: "id="+id,
        success: function(response) {
            // if permitted show the popup
            if( response == "show" ) {
                // show the popup box
                $("#popout-container-ghg").stop()
                    .animate({ bottom: '0px' }, 'slow', animationComplete )
                    .delay(5000)
                    .animate({ bottom: '-110px' }, 'slow', animationComplete);
                // Remove any text previously displayed on the box
                $("#popout-content-ghg div").stop(true, true).hide();
                // Show the corresponding help link text
                $( '#' + id + '_infolink').stop(true, true).fadeIn('slow');
            }
        }

    });
}

function popoutGhgHoverIn() {
    $("#popout-container-ghg").stop(true, true);
}

function popoutGhgHoverOut() {
    $("#popout-container-ghg").stop(true, true).delay(3000).animate({ bottom: '-110px' }, 'slow', animationComplete );
}

//register popup close button
$('#popout-container-ghg .close').click( function() {
    $("#popout-container-ghg").stop(true, true).animate({ bottom: '-110px' }, 'slow', animationComplete );
});

function checkInfoLinkLogic( event ) {
    // Underground Coal Mine
    if( $("#other1:checkbox").is(':checked') && (
        !$("#other2:checkbox").is(':checked') &&
        !$("#other3:checkbox").is(':checked') &&
        !$("#other4:checkbox").is(':checked') &&
        !$("#other5:checkbox").is(':checked') &&
        !$("#other6:checkbox").is(':checked') &&
        !$("#other7:checkbox").is(':checked') &&
        !$("#other8:checkbox").is(':checked') &&
        !$("#other9:checkbox").is(':checked') &&
        !$("#other10:checkbox").is(':checked') &&
        $('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'underground_coal_mines' );

    // Waste - Municipal Landfill
    else if( $("#waste1:checkbox").is(':checked') && (
        !$("#waste2:checkbox").is(':checked') &&
        !$("#waste3:checkbox").is(':checked') &&
        !$("#waste4:checkbox").is(':checked') &&
        $('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'municipal_landfill' );

    // Petroleum and Natural Gas Systems - Oil and Gas systems
    else if( $("#sector9checkbox:checkbox").is(':checked') && (
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector8checkbox:checkbox').is(':checked') &&
        ($("#petroleum1:checkbox").is(':checked') ||
        $("#petroleum2:checkbox").is(':checked') ||
        $("#petroleum3:checkbox").is(':checked') ||
        $("#petroleum4:checkbox").is(':checked') ||
        $("#petroleum5:checkbox").is(':checked') ||
        $("#petroleum7:checkbox").is(':checked') ||
        $('#petroleum8:checkbox').is(':checked') ||
        $('#petroleum9:checkbox').is(':checked') ||
        $('#petroleum10:checkbox').is(':checked') ||
        $('#petroleum11:checkbox').is(':checked')) ) )
        showPopup( 'oilandgas' );

    // Other - Use of electrical equipment
    else if( $("#other7:checkbox").is(':checked') && (
        !$("#other2:checkbox").is(':checked') &&
        !$("#other3:checkbox").is(':checked') &&
        !$("#other4:checkbox").is(':checked') &&
        !$("#other5:checkbox").is(':checked') &&
        !$("#other6:checkbox").is(':checked') &&
        !$("#other1:checkbox").is(':checked') &&
        !$("#other8:checkbox").is(':checked') &&
        !$("#other9:checkbox").is(':checked') &&
        !$("#other10:checkbox").is(':checked') &&
        $('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'electrical_power' );

    // Other - Electronics Manufacturing
    else if( $("#other8:checkbox").is(':checked') && (
        !$("#other2:checkbox").is(':checked') &&
        !$("#other3:checkbox").is(':checked') &&
        !$("#other4:checkbox").is(':checked') &&
        !$("#other5:checkbox").is(':checked') &&
        !$("#other6:checkbox").is(':checked') &&
        !$("#other1:checkbox").is(':checked') &&
        !$("#other7:checkbox").is(':checked') &&
        !$("#other9:checkbox").is(':checked') &&
        !$("#other10:checkbox").is(':checked') &&
        $('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'electronics_manufacturing' );

    // Metals - Magnesium
    else if( $("#metal5:checkbox").is(':checked') && (
        !$("#metal1:checkbox").is(':checked') &&
        !$("#metal2:checkbox").is(':checked') &&
        !$("#metal3:checkbox").is(':checked') &&
        !$("#metal4:checkbox").is(':checked') &&
        !$("#metal6:checkbox").is(':checked') &&
        !$("#metal7:checkbox").is(':checked') &&
        $('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'magnesium' );

    // Metals - Aluminum
    else if( $("#metal1:checkbox").is(':checked') && (
        !$("#metal5:checkbox").is(':checked') &&
        !$("#metal2:checkbox").is(':checked') &&
        !$("#metal3:checkbox").is(':checked') &&
        !$("#metal4:checkbox").is(':checked') &&
        !$("#metal6:checkbox").is(':checked') &&
        !$("#metal7:checkbox").is(':checked') &&
        $('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector8checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector9checkbox:checkbox').is(':checked') ) )
        showPopup( 'aluminum' );
    
    // Petroleum and Natural Gas subsector - petroleum6 
    else if( $("#petroleum6:checkbox").is(':checked') && (
        !$("#petroleum1:checkbox").is(':checked') &&
        !$("#petroleum2:checkbox").is(':checked') &&
        !$("#petroleum3:checkbox").is(':checked') &&
        !$("#petroleum4:checkbox").is(':checked') &&
        !$("#petroleum5:checkbox").is(':checked') &&
        !$("#petroleum7:checkbox").is(':checked') &&
        !$('#petroleum8:checkbox').is(':checked') &&
        !$('#petroleum9:checkbox').is(':checked') &&
        !$('#petroleum10:checkbox').is(':checked') &&
        !$('#petroleum11:checkbox').is(':checked') &&
        $("#sector9checkbox:checkbox").is(':checked') && 
        !$('#sector1checkbox:checkbox').is(':checked') &&
        !$('#sector2checkbox:checkbox').is(':checked') &&
        !$('#sector3checkbox:checkbox').is(':checked') &&
        !$('#sector4checkbox:checkbox').is(':checked') &&
        !$('#sector5checkbox:checkbox').is(':checked') &&
        !$('#sector6checkbox:checkbox').is(':checked') &&
        !$('#sector7checkbox:checkbox').is(':checked') &&
        !$('#sector8checkbox:checkbox').is(':checked') ) )
        showPopup( 'petroUnderground' );
}

function checkGhgInfoLinkLogic(event) {
    if ($('#gas1check:checkbox').is(':checked') && (
        !$('#gas2check:checkbox').is(':checked') &&
        !$('#gas3check:checkbox').is(':checked') &&
        !$('#gas4check:checkbox').is(':checked') &&
        !$('#gas5check:checkbox').is(':checked') &&
        !$('#gas7check:checkbox').is(':checked') &&
        !$('#gas8check:checkbox').is(':checked') &&
        !$('#gas9check:checkbox').is(':checked') &&
        !$('#gas10check:checkbox').is(':checked'))) {
        showGhgPopup('co2');
    }
    else if ($('#gas2check:checkbox').is(':checked') && (
        !$('#gas1check:checkbox').is(':checked') &&
        !$('#gas3check:checkbox').is(':checked') &&
        !$('#gas4check:checkbox').is(':checked') &&
        !$('#gas5check:checkbox').is(':checked') &&
        !$('#gas7check:checkbox').is(':checked') &&
        !$('#gas8check:checkbox').is(':checked') &&
        !$('#gas9check:checkbox').is(':checked') &&
        !$('#gas10check:checkbox').is(':checked'))) {
        showGhgPopup('methane');
    }
    else if ($('#gas3check:checkbox').is(':checked') && (
        !$('#gas1check:checkbox').is(':checked') &&
        !$('#gas2check:checkbox').is(':checked') &&
        !$('#gas4check:checkbox').is(':checked') &&
        !$('#gas5check:checkbox').is(':checked') &&
        !$('#gas7check:checkbox').is(':checked') &&
        !$('#gas8check:checkbox').is(':checked') &&
        !$('#gas9check:checkbox').is(':checked') &&
        !$('#gas10check:checkbox').is(':checked'))) {
        showGhgPopup('n2o');
    }
    else if ((!$('#gas1check:checkbox').is(':checked') &&
        !$('#gas2check:checkbox').is(':checked') &&
        !$('#gas3check:checkbox').is(':checked') ) && (
        $('#gas4check:checkbox').is(':checked') ||
        $('#gas5check:checkbox').is(':checked') ||
        $('#gas7check:checkbox').is(':checked') ||
        $('#gas8check:checkbox').is(':checked') ||
        $('#gas9check:checkbox').is(':checked') ||
        $('#gas10check:checkbox').is(':checked'))) {
        showGhgPopup('flourinated_gases');
    }
}
