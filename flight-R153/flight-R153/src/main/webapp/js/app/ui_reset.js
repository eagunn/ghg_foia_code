/**
 *
 * Created by alabdullahwi on 5/26/2015.
 */

function refreshView(ds) {

    $("#dd-selectedStatusLabel").html('<label class="dd-selected-text">All Facilities</label>');
    $("#reportingStatusSelection").show();
    reportingStatus = "ALL";
    document.getElementById("reportingYear").selectedIndex = "ry" + ryear;
    $("#slider-range").slider("values", 0, -20000);
    $("#slider-range").slider("values", 1, 23000000);

    if (ds != 'S' && ds != 'I') {
        $("a#vLine").show();
        $("#lineLabel").show();
    }
    if (ds != 'I') {
        $("a#vBar").show();
        $("#barLabel").show();
        $("a#vPie").show();
        $("#pieLabel").show();
    }
    if (ds == 'S') {
        welcomeScreenAlreadyShown = false;
        supplierSector = 0;
        var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&fid='
                +'&sf=11001000&sc=0&so=0&ds=S&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (trendSelection == "current") {
                urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&fid='
                    +'&sf=11001000&sc=0&so=0&ds=S&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
            else {
            	urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&fid='
                    +'&sf=11001000&sc=0&so=0&ds=S&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "bar") {
            urlString = 'main.do#/barSupplier/?q=Find%20a%20Facility%20or%20Location&fid='
                +'&sf=11001000&sc=0&so=0&ds=S&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL'
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSupplier/?q=Find%20a%20Facility%20or%20Location&fid='
                +'&sf=11001000&sc=0&so=0&ds=S&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        } 
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'E') {
        if (visType == "map") {
            window.location.href = 'main.do';
        }
        else {
        	var urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            if (visType == "list") {
	            if (listSelector == 2) {
	                if (trendSelection == "current") {
	                	urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
	                } 
	                else {
	                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
	                }
	            }
	        }
	        else if (visType == "line") {
	            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
	        }
	        else if (visType == "bar") {
	            if (barSelector == 0) {
	                urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
	            }
	            else {
	                urlString = 'main.do#/barState/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
	            }
	        }
	        else if (visType == "pie") {
	        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=E&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL'; 
	        }
        	
        	if (window.location.href.indexOf(urlString) > -1) {
                location.reload();
            } else {
                window.location.href = urlString;
            }
        }
    }
    else if (ds == 'O') {
    	var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 0) {
                urlString = 'main.do#/listFacilityForBasinGeo/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
            else {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacilityForBasin/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'main.do#/listFacilityForBasin/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=1&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=O&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'B') {
    	var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 0) {
                urlString = 'main.do#/listFacilityForBasinGeo/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
            else {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacilityForBasin/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'main.do#/listFacilityForBasin/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=1&s911=0&si=&ss=&so=0&ds=B&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'L') {
    	var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 2) {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
            else {
                urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=1&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=L&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'T') {
    	var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 2) {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
            else {
                urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=0&s9=1&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=0&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=1&si=&ss=&so=0&ds=T&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'F') {
    	var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=&ss=&so=0&ds=F&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 2) {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=14&ss=&so=0&ds=F&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=14&ss=&so=0&ds=F&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=14&ss=&so=0&ds=F&yr='+cyear+'&tr=trend&rs=ALL&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            if (barSelector == 0) {
                urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=14&ss=&so=0&ds=F&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "pie") {
        	urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=0&s2=0&s3=0&s4=0&s5=0&s6=0&s7=0&s8=1&s9=0&s201=0&s202=0&s203=0&s204=0&s301=0&s302=0&s303=0&s304=0&s305=0&s306=0&s307=0&s401=0&s402=0&s403=0&s404=0&s405=0&s601=0&s602=0&s701=0&s702=0&s703=0&s704=0&s705=0&s706=0&s707=0&s708=0&s709=0&s710=0&s711=0&s801=0&s802=0&s803=0&s804=0&s805=0&s806=0&s807=1&s808=0&s809=0&s810=0&s901=0&s902=0&s903=0&s904=0&s905=0&s906=0&s907=0&s908=0&s909=0&s910=0&s911=0&si=14&ss=&so=0&ds=F&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else if (ds == 'P') {
        var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            if (listSelector == 2) {
                if (trendSelection == "current") {
                    urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
                }
                else {
                    urlString = 'ghgp/main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=trend&cyr='+cyear+'&rs=ALL';
                }
            }
            else {
                urlString = 'main.do#/listSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "line") {
            urlString = 'main.do#/trend/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        else if (visType == "bar") {
            if (barSelector == 0) {
                urlString = 'main.do#/barSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
            else {
                urlString = 'main.do#/barState/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
            }
        }
        else if (visType == "pie") {
            urlString = 'main.do#/pieSector/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&g11=1&g12=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=0&s808=1&s809=1&s810=1&s901=1&s902=0&s903=1&s904=1&s905=0&s906=1&s907=1&s908=1&s909=1&s910=1&s911=1&si=&ss=&so=0&ds=P&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';

        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
    else {
        injectionSelection = 11;
        var urlString = 'main.do#/facility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&is=11&so=0&ds=I&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        if (visType == "list") {
            var urlString = 'main.do#/listFacility/?q=Find%20a%20Facility%20or%20Location&st=&bs=&fid=&sf=11001000&is=11&so=0&ds=I&yr='+cyear+'&tr=current&cyr='+cyear+'&rs=ALL';
        }
        
        if (window.location.href.indexOf(urlString) > -1) {
            location.reload();
        } else {
            window.location.href = urlString;
        }
    }
}

//hides dropdown button if click happens outside
window.onclick = function(event) {
    if (!event.target.matches('.dropdownButton')) {
        var dropdowns = document.getElementsByClassName('dropdownOptions');
        var i;
        for (i = 0 ; i <dropdowns.length; i ++ ) {
            $(dropdowns[i]).hide();
        }
    }
}
function resetForm() {
	// Reset the Facility search and state and location fields
    dataSource = 'E';
    $("#parentState").val( "" );
    $("#reportingYear").val( ryear );
    $("#facOrLocInput").val("Find a Facility or Location");
    $("#countyState").val("");
    $("#countyName").val("");
    $("#basin").val( "" );
    // Reset the Sector and Gas selections
    resetSectorsSelection();
    resetGasesSelection();
    resetEmissionRange();
    // Refresh the page with the new original selections selected above
    //setURLPath();
    location.href = "main.do";
}

function resetSectorsSelection() {
    $("#sector1checkbox").attr('checked',true);
    $("#sector2checkbox").attr('checked',true);
    $("#sector3checkbox").attr('checked',true);
    $("#sector4checkbox").attr('checked',true);
    $("#sector5checkbox").attr('checked',true);
    $("#sector6checkbox").attr('checked',true);
    $("#sector7checkbox").attr('checked',true);
    $("#sector8checkbox").attr('checked',true);
    $("#sector9checkbox").attr('checked',true);
    $("#chem1").attr('checked',true);
    $("#chem2").attr('checked',true);
    $("#chem3").attr('checked',true);
    $("#chem4").attr('checked',true);
    $("#chem5").attr('checked',true);
    $("#chem6").attr('checked',true);
    $("#chem7").attr('checked',true);
    $("#chem8").attr('checked',true);
    $("#chem9").attr('checked',true);
    $("#chem10").attr('checked',true);
    $("#chem11").attr('checked',true);
    $("#chem12").attr('checked',true);
    $("#other1").attr('checked',true);
    $("#other2").attr('checked',true);
    $("#other3").attr('checked',true);
    $("#other4").attr('checked',true);
    $("#other5").attr('checked',true);
    $("#other6").attr('checked',true);
    $("#other7").attr('checked',true);
    $("#other8").attr('checked',true);
    $("#other9").attr('checked',true);
    $("#other10").attr('checked',true);
    $("#waste1").attr('checked',true);
    $("#waste2").attr('checked',true);
    $("#waste3").attr('checked',true);
    $("#waste4").attr('checked',true);
    $("#metal1").attr('checked',true);
    $("#metal2").attr('checked',true);
    $("#metal3").attr('checked',true);
    $("#metal4").attr('checked',true);
    $("#metal5").attr('checked',true);
    $("#metal6").attr('checked',true);
    $("#metal7").attr('checked',true);
    $("#mineral1").attr('checked',true);
    $("#mineral2").attr('checked',true);
    $("#mineral3").attr('checked',true);
    $("#mineral4").attr('checked',true);
    $("#mineral5").attr('checked',true);
    $("#pulp1").attr('checked',true);
    $("#pulp2").attr('checked',true);
    $("#petroleum1").attr('checked',true);
    $("#petroleum2").attr('checked',true);
    $("#petroleum3").attr('checked',true);
    $("#petroleum4").attr('checked',true);
    $("#petroleum5").attr('checked',true);
    $("#petroleum6").attr('checked',true);
    $("#petroleum7").attr('checked',true);
    $("#petroleum8").attr('checked',true);
    $("#petroleum9").attr('checked',true);
    $("#petroleum10").attr('checked',true);
    $("#petroleum11").attr('checked',true);
    determineTotalCheckbox10Select();
}

function resetGasesSelection() {
    $("#gas1check").attr('checked',true);
    $("#gas2check").attr('checked',true);
    $("#gas3check").attr('checked',true);
    $("#gas4check").attr('checked',true);
    $("#gas5check").attr('checked',true);
    $("#gas6check").attr('checked',true);
    $("#gas7check").attr('checked',true);
    $("#gas8check").attr('checked',true);
    $("#gas9check").attr('checked',true);
    $("#gas10check").attr('checked',true);
    $("#gas11check").attr('checked',true);
    $("#gas12check").attr('checked',true);
}

function resetEmissionRange() {
    // get the original range for high and low emissions
    var range = $("#highEmissionRange").map( function() {
        return [this.min, this.max];
    });
    var min = range.get(0);
    var max = range.get(1);
    // reset the range to their original values
    $("#lowEmissionRange").val( min );
    $("#highEmissionRange").val( max );
    // reset the actual slider and input fields
    $("#slider-range").slider("values", 0, min); // set the first handle (index 0) to the minimum value (0)
    $("#slider-range").slider("values", 1, max); // set the second handle (index 1) to the maximum value (23000000)
}

function resetStateCounty() {
    $("#parentState").val('');
    $("#countyState").val('');
    generateURL('');
}

function resetCounty() {
    $("#countyState").val('');
    generateURL('');
}
