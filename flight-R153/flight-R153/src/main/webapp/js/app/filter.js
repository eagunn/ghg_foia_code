/**
 * Created by alabdullahwi on 5/26/2015.
 */

function showGHGfilter(){
    $("#divGHGfilter").show(500);
    $("#aGasControl").attr('href','javascript:hideGHGfilter()');
    $("#gasControl").attr('src','img/control-down.png');
}

function hideGHGfilter(){
    $("#divGHGfilter").hide(500);
    $("#aGasControl").attr('href','javascript:showGHGfilter()');
    $("#gasControl").attr('src','img/control.png');
}

function updateGHGfilter(){
    var htmlStr = "";
    if ($("#gas1check").attr('checked') &&
        $("#gas2check").attr('checked') &&
        $("#gas3check").attr('checked') &&
        $("#gas4check").attr('checked') &&
        $("#gas5check").attr('checked') &&
        $("#gas6check").attr('checked') &&
        $("#gas7check").attr('checked') &&
        $("#gas8check").attr('checked') &&
        $("#gas9check").attr('checked') &&
        $("#gas10check").attr('checked')) {
        htmlStr += "<nobr>All</nobr>";
    } else	if (!$("#gas1check").attr('checked') &&
        !$("#gas2check").attr('checked') &&
        !$("#gas3check").attr('checked') &&
        !$("#gas4check").attr('checked') &&
        !$("#gas5check").attr('checked') &&
        !$("#gas6check").attr('checked') &&
        !$("#gas7check").attr('checked') &&
        !$("#gas8check").attr('checked') &&
        !$("#gas9check").attr('checked') &&
        !$("#gas10check").attr('checked')) {
        htmlStr += "<nobr>None</nobr>";
    } else {
        htmlStr += "<nobr>";
        if($("#gas1check").attr('checked'))
            htmlStr += " CO<sub>2</sub>,";
        if($("#gas2check").attr('checked'))
            htmlStr += " CH<sub>4</sub>,";
        if($("#gas3check").attr('checked'))
            htmlStr += " N<sub>2</sub>O,";
        if($("#gas4check").attr('checked'))
            htmlStr += " SF<sub>6</sub>,";
        if($("#gas5check").attr('checked'))
            htmlStr += " NF<sub>3</sub>,";
        if($("#gas6check").attr('checked'))
            htmlStr += " HFC-23,";
        if($("#gas7check").attr('checked'))
            htmlStr += " HFCs,";
        if($("#gas8check").attr('checked'))
            htmlStr += " PFCs,";
        if($("#gas9check").attr('checked'))
            htmlStr += " HFEs,";
        if($("#gas10check").attr('checked'))
            htmlStr += " Other,";
        htmlStr = htmlStr.substr(0,htmlStr.length-1);
        htmlStr += "</nobr>";
    }
    $("#GHGGases").html(htmlStr);
}

function showEmissionsfilter(){
    $("#divRangefilter").show(500);
    $("#aRangeControl").attr('href','javascript:hideEmissionsfilter()');
    $("#rangeControl").attr('src','img/control-down.png');
}

function hideEmissionsfilter(){
    //jQuery("#divRangefilter").hide(500);
    $("#aRangeControl").attr('href','javascript:showEmissionsfilter()');
    $("#rangeControl").attr('src','img/control.png');
}

function updateEmissionsfilter(){
    var htmlStr = "<nobr>";
    htmlStr += addCommas($("#lowEmissionRange").val());
    htmlStr += " - ";
    htmlStr += addCommas($("#highEmissionRange").val());
    htmlStr += " metric tons CO<font class='fakeSub'>2</font>e</nobr>";
    $("#emissionRange").html(htmlStr);
}

