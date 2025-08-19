/**
 * Created by alabdullahwi on 5/26/2015.
 */


function generateFlightRequestObject(_queryString, _stateAbbr, _fipsCode, _msaCode, _stateLevel, _facId, _lowE, _highE, _cyear, _trendSelection, _reportingStatus, _emissionsType, _tribalLandId, pageNumber, _overlaySelector, _visType) {


    //HACK: if local vars were not passed, get their vals from global variables
    if (!_overlaySelector) {
       _overlaySelector = overlayLevel;
    }
    if(!_reportingStatus) {
       _reportingStatus = reportingStatus;
    }
    if (!_stateLevel) {
        _stateLevel = stateLevel;
    }
    if (!_trendSelection) {
        _trendSelection = trendSelection;
    }
    if(!_cyear) {
        _cyear = cyear;
    }

    var _dataSource= $("#dataType").val();
    var _sortOrder;

    if ("S" === _dataSource) {
        _sortOrder = supplierSort;
    }
    else {
       _sortOrder = emitterSort;
    }


    var _stateAbbr = $("#parentState").val();

    if(_stateAbbr == "US") {
        _stateAbbr = "";
    }

    var _facOrLoc = $("#facOrLocInput").val();
    if(_facOrLoc == "Find a Facility or Location"){
        _facOrLoc = "";
    }
    var _fipsCode = addLeadingZeros($("#countyState").val());
    var _msaCode = "";

    if(_stateLevel == 1) {
        _msaCode = _fipsCode;
        _fipsCode = "";
    }

    var _tribalLandId = "";
    if(_stateAbbr == "TL"  ) {
        _fipsCode = "";
        _msaCode = "";
        _tribalLandId = $("#tribe").find(":selected").val();
    } 
    if (!_visType) { 
       _visType = visType;
    }

    var retVal = {
        "trend" : _trendSelection
        ,"dataSource": _dataSource
        ,"reportingYear" :$("#reportingYear").val()
        ,"currentYear" : _cyear
        ,"query": decodeURIComponent(_facOrLoc)
        ,"lowE": $("#lowEmissionRange").val()
        ,"highE": $("#highEmissionRange").val()
        ,"state" : _stateAbbr
        ,"countyFips" : _fipsCode
        ,"msaCode" : _msaCode
        ,"stateLevel" : _stateLevel
        ,"basin" : generateBasinFilter(null, true)
        ,"gases"  : generateGasFilter()
        ,"sectors" : generateSectorFilter()
        ,"sortOrder": _sortOrder
        //always global
        ,"supplierSector" : supplierSector
        ,"reportingStatus" : _reportingStatus
        ,"searchOptions" : generateSearchQueryJson()
        //this is always from global var!
        ,"injectionSelection" : injectionSelection
        ,"emissionsType" : $("#emissionsType").val()
        ,"tribalLandId" : _tribalLandId
        ,"pageNumber" : pageNumber
        ,"overlayLevel" : _overlaySelector
        ,"visType" : _visType

    };
    return JSON.stringify(retVal) ;
}


function generateUrlParameters(_reportingStatus, _stateLevel,  _trendSelection, _cyear, _pageNumber, _overlayLevel) {

    //HACK: if local vars were not passed, get their vals from global variables
    if(!_reportingStatus) {
        _reportingStatus = reportingStatus;
    }
    if (!_stateLevel) {
        _stateLevel = stateLevel;
    }
    if (!_overlayLevel) {
        _overlayLevel = stateLevel;
    }
    if (!_trendSelection) {
        _trendSelection = trendSelection;
    }
    if(!_cyear) {
        _cyear = cyear;
    }

    var _stateAbbr = $("#parentState").val();

    if(_stateAbbr == "US") {
        _stateAbbr = "";
    }

    var _facOrLoc = $("#facOrLocInput").val();
    if(_facOrLoc == "Find a Facility or Location"){
        _facOrLoc = "";
    }
    var _fipsCode = addLeadingZeros($("#countyState").val());
    var _msaCode = "";

    if(_stateLevel == 1) {
        _msaCode = _fipsCode;
        _fipsCode = "";
    }

    var _tribalLandId = "";

    if(_stateAbbr == "TL" ) {
        _fipsCode = "";
        _msaCode = "";
        _tribalLandId = $("#tribe").find(":selected").val();
    }
    
    var retv = "";
    retv += 'q=' + decodeURIComponent(_facOrLoc);
    retv += '&tr=' + _trendSelection ;
    retv += '&ds=' + $("#dataType").val();
    retv += '&ryr=' + $("#reportingYear").val();
    retv += '&cyr=' + _cyear;
    retv += '&lowE=' + $("#lowEmissionRange").val();
    retv += '&highE=' +$("#highEmissionRange").val();
    retv += '&st='+ _stateAbbr;
    retv += '&fc=' + _fipsCode;
    retv += '&mc=' + _msaCode;
    retv += '&rs=' + _reportingStatus;
    retv += '&sc=' + supplierSector;
    retv += '&is=' + injectionSelection;
    retv += '&et=' + $("#emissionsType").val();
    retv += '&tl=' + _tribalLandId;
    retv += '&pn=' + _pageNumber;
    retv += '&ol=' + _overlayLevel;
    retv += '&sl=' + _stateLevel;

    retv += generateBasinFilter(null, false);
    retv += generateGasFilterQueryString();
    retv += generateSectorFilterQueryString();
    retv += generateSearchQuery();

    return retv;

}



function generateBasinFilter(dataSource, isJson) {
    basin = $("#basin").val();
    if (isJson) {
        return basin;
    }

    //if ((dataSource == 'O' || isOnshoreOnly()) && basin != undefined && basin != null) {
    return "&bs="+basin;
    //} else {
    //	return "";
    //}
}


function generateSearchQueryJson() {

    var facName = 0;
    var facCity = 0;
    var facCounty = 0;
    var facState = 0;
    var facZip = 0;
    var facId = 0;
    var naicsCode = 0;
    var parentName = 0;

    if( $("#advSearch_name").is(":checked") )
        facName = 1;
    if( $("#advSearch_city").is(":checked") )
        facCity= 1;
    if( $("#advSearch_county").is(":checked") )
        facCounty= 1;
    if( $("#advSearch_state").is(":checked") )
        facState= 1;
    if( $("#advSearch_zipcode").is(":checked") )
        facZip = 1;
    if( $("#advSearch_facId").is(":checked") )
        facId = 1;
    if( $("#advSearch_naicsCode").is(":checked") )
        naicsCode = 1;
    if( $("#advSearch_corpParent").is(":checked") )
        parentName = 1;

    // Query String for Search Facility flags in the following order:
    // Facility Name, County, State, Zipcode, Facility ID, Program ID, NAICS Code, and Corporate Parent name
    return "" + facName + facCity + facCounty + facState + facZip + facId + naicsCode + parentName;




}


function generateSearchQuery() {
    var facName = 0;
    var facCity = 0;
    var facCounty = 0;
    var facState = 0;
    var facZip = 0;
    var facId = 0;
    var naicsCode = 0;
    var parentName = 0;

    if( $("#advSearch_name").is(":checked") )
        facName = 1;
    if( $("#advSearch_city").is(":checked") )
        facCity= 1;
    if( $("#advSearch_county").is(":checked") )
        facCounty= 1;
    if( $("#advSearch_state").is(":checked") )
        facState= 1;
    if( $("#advSearch_zipcode").is(":checked") )
        facZip = 1;
    if( $("#advSearch_facId").is(":checked") )
        facId = 1;
    if( $("#advSearch_naicsCode").is(":checked") )
        naicsCode = 1;
    if( $("#advSearch_corpParent").is(":checked") )
        parentName = 1;

    // Query String for Search Facility flags in the following order:
    // Facility Name, County, State, Zipcode, Facility ID, Program ID, NAICS Code, and Corporate Parent name

    var queryStr = "&sf=" + facName + facCity + facCounty + facState + facZip + facId + naicsCode + parentName;
    return queryStr;
}



function generateGasFilter() {

    var gases = [];
    for (var i=1; i<13; i++) {
        gases.push($("#gas"+i+"check").attr('checked') == 'checked');
    }
    return gases;

}

function generateGasFilterQueryString(){


    var gas1=0;
    var gas2=0;
    var gas3=0;
    var gas4=0;
    var gas5=0;
    var gas6=0;
    var gas7=0;
    var gas8=0;
    var gas9=0;
    var gas10=0;
    var gas11=0;
    var gas12=0;



    if($("#gas1check").attr('checked'))
        gas1 = 1;
    if($("#gas2check").attr('checked'))
        gas2 = 1;
    if($("#gas3check").attr('checked'))
        gas3 = 1;
    if($("#gas4check").attr('checked'))
        gas4 = 1;
    if($("#gas5check").attr('checked'))
        gas5 = 1;
    if($("#gas6check").attr('checked'))
        gas6 = 1;
    if($("#gas7check").attr('checked'))
        gas7 = 1;
    if($("#gas8check").attr('checked'))
        gas8 = 1;
    if($("#gas9check").attr('checked'))
        gas9 = 1;
    if($("#gas10check").attr('checked'))
        gas10 = 1;
    if($("#gas11check").attr('checked'))
        gas11 = 1;
    if($("#gas12check").attr('checked'))
        gas12 = 1;

    var queryString = "";
    queryString += "&g1="+gas1+"&g2="+gas2+"&g3="+gas3+"&g4="+gas4+"&g5="+gas5+"&g6="+gas6+"&g7="+gas7+"&g8="+gas8+"&g9="+gas9+"&g10="+gas10+"&g11="+gas11+"&g12="+gas12;
    return queryString;
}



function generateSectorFilter() {



    function populateSectorArray(sectorIndex, sectorName, maxIndex) {
        var retVal= [];
        retVal.push($("#sector"+sectorIndex+"checkbox").attr('checked') == 'checked' );
        if (maxIndex != null) {
            for ( var i = 1; i<maxIndex+1; i++) {
                retVal.push($("#"+sectorName+i).attr('checked') == 'checked');
            }
        }
        return retVal;
    }


    var matrix = [];

    //powerplant
    var sectorRef = {
        'powerplant': null,
        'waste': 4,
        'metal':7,
        'mineral': 5,
        'refineries': null,
        'pulp': 2,
        'chem': 11,
        'other': 10,
        'petroleum':11
    };

    var index = 1;
    for ( var key in sectorRef) {
        if (sectorRef.hasOwnProperty(key)) {
            matrix.push(populateSectorArray(index, key, sectorRef[key]));
        }
        index++;
    }

    return matrix;


}


function generateSectorFilterQueryString(){
    var s1=0;
    var s2=0;
    var s3=0;
    var s4=0;
    var s5=0;
    var s6=0;
    var s7=0;
    var s8=0;
    var s9=0;
    var s10=0;
    var s201=0;
    var s202=0;
    var s203=0;
    var s204=0;
    var s301=0;
    var s302=0;
    var s303=0;
    var s304=0;
    var s305=0;
    var s306=0;
    var s307=0;
    var s401=0;
    var s402=0;
    var s403=0;
    var s404=0;
    var s405=0;
    var s601=0;
    var s602=0;
    var s701=0;
    var s702=0;
    var s703=0;
    var s704=0;
    var s705=0;
    var s706=0;
    var s707=0;
    var s708=0;
    var s709=0;
    var s710=0;
    var s711=0;
    var s801=0;
    var s802=0;
    var s803=0;
    var s804=0;
    var s805=0;
    var s806=0;
    var s807=0;
    var s808=0;
    var s809=0;
    var s810=0;
    var s901=0;
    var s902=0;
    var s903=0;
    var s904=0;
    var s905=0;
    var s906=0;
    var s907=0;
    var s908=0;
    var s909=0;
    var s910=0;
    var s911=0;

    if($("#sector1checkbox").attr('checked'))
        s1 = 1;
    if($("#sector2checkbox").attr('checked'))
        s2 = 1;
    if($("#sector3checkbox").attr('checked'))
        s3 = 1;
    if($("#sector4checkbox").attr('checked'))
        s4 = 1;
    if($("#sector5checkbox").attr('checked'))
        s5 = 1;
    if($("#sector6checkbox").attr('checked'))
        s6 = 1;
    if($("#sector7checkbox").attr('checked'))
        s7 = 1;
    if($("#sector8checkbox").attr('checked'))
        s8 = 1;
    if($("#sector9checkbox").attr('checked'))
        s9 = 1;
    if($("#sector10checkbox").attr('checked'))
        s10 = 1;
    if($("#waste1").attr('checked'))
        s201 = 1;
    if($("#waste2").attr('checked'))
        s202 = 1;
    if($("#waste3").attr('checked'))
        s203 = 1;
    if($("#waste4").attr('checked'))
        s204 = 1;
    if($("#metal1").attr('checked'))
        s301 = 1;
    if($("#metal2").attr('checked'))
        s302 = 1;
    if($("#metal3").attr('checked'))
        s303 = 1;
    if($("#metal4").attr('checked'))
        s304 = 1;
    if($("#metal5").attr('checked'))
        s305 = 1;
    if($("#metal6").attr('checked'))
        s306 = 1;
    if($("#metal7").attr('checked'))
        s307 = 1;
    if($("#mineral1").attr('checked'))
        s401 = 1;
    if($("#mineral2").attr('checked'))
        s402 = 1;
    if($("#mineral3").attr('checked'))
        s403 = 1;
    if($("#mineral4").attr('checked'))
        s404 = 1;
    if($("#mineral5").attr('checked'))
        s405 = 1;
    if($("#pulp1").attr('checked'))
        s601 = 1;
    if($("#pulp2").attr('checked'))
        s602 = 1;
    if($("#chem1").attr('checked'))
        s701 = 1;
    if($("#chem2").attr('checked'))
        s702 = 1;
    if($("#chem3").attr('checked'))
        s703 = 1;
    if($("#chem4").attr('checked'))
        s704 = 1;
    if($("#chem5").attr('checked'))
        s705 = 1;
    if($("#chem6").attr('checked'))
        s706 = 1;
    if($("#chem7").attr('checked'))
        s707 = 1;
    if($("#chem8").attr('checked'))
        s708 = 1;
    if($("#chem9").attr('checked'))
        s709 = 1;
    if($("#chem10").attr('checked'))
        s710 = 1;
    if($("#chem11").attr('checked'))
        s711 = 1;
    if($("#other1").attr('checked'))
        s801 = 1;
    if($("#other2").attr('checked'))
        s802 = 1;
    if($("#other3").attr('checked'))
        s803 = 1;
    if($("#other4").attr('checked'))
        s804 = 1;
    if($("#other5").attr('checked'))
        s805 = 1;
    if($("#other6").attr('checked'))
        s806 = 1;
    if($("#other7").attr('checked'))
        s807 = 1;
    if($("#other8").attr('checked'))
        s808 = 1;
    if($("#other9").attr('checked'))
        s809 = 1;
    if($("#other10").attr('checked'))
        s810 = 1;
    if($("#petroleum1").attr('checked'))
        s901 = 1;
    if($("#petroleum2").attr('checked'))
        s902 = 1;
    if($("#petroleum3").attr('checked'))
        s903 = 1;
    if($("#petroleum4").attr('checked'))
        s904 = 1;
    if($("#petroleum5").attr('checked'))
        s905 = 1;
    if($("#petroleum6").attr('checked'))
        s906 = 1;
    if($("#petroleum7").attr('checked'))
        s907 = 1;
    if($("#petroleum8").attr('checked'))
        s908 = 1;
    if($("#petroleum9").attr('checked'))
        s909 = 1;
    if($("#petroleum10").attr('checked'))
        s910 = 1;
    if($("#petroleum11").attr('checked'))
        s911 = 1;

    var queryString = "";
    queryString += "&s1="+s1+"&s2="+s2+"&s3="+s3+"&s4="+s4+"&s5="+s5+"&s6="+s6+"&s7="+s7+"&s8="+s8+"&s9="+s9+"&s10="+s10;
    queryString += "&s201="+s201+"&s202="+s202+"&s203="+s203+"&s204="+s204;
    queryString += "&s301="+s301+"&s302="+s302+"&s303="+s303+"&s304="+s304+"&s305="+s305+"&s306="+s306+"&s307="+s307;
    queryString += "&s401="+s401+"&s402="+s402+"&s403="+s403+"&s404="+s404+"&s405="+s405;
    queryString += "&s601="+s601+"&s602="+s602;
    queryString += "&s701="+s701+"&s702="+s702+"&s703="+s703+"&s704="+s704+"&s705="+s705+"&s706="+s706;
    queryString += "&s707="+s707+"&s708="+s708+"&s709="+s709+"&s710="+s710+"&s711="+s711;
    queryString += "&s801="+s801+"&s802="+s802+"&s803="+s803+"&s804="+s804+"&s805="+s805+"&s806="+s806+"&s807="+s807+"&s808="+s808+"&s809="+s809+"&s810="+s810;
    queryString += "&s901="+s901+"&s902="+s902+"&s903="+s903+"&s904="+s904+"&s905="+s905+"&s906="+s906+"&s907="+s907+"&s908="+s908+"&s909="+s909+"&s910="+s910+"&s911="+s911;
    return queryString;
}