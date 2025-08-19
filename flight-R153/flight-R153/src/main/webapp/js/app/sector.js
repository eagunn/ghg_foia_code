/**
 * Created by alabdullahwi on 5/26/2015.
 */

function sectorOnOff (x,y,y2) {
    // x=name/id of checkbox
    // y=sector

    if (document.getElementById(x).checked!=true) {
        // alert ('Checked = true');
        z=y-1;
        document.getElementById('sector'+y+'row1').setAttribute("class", "sectorOff");
        document.getElementById('sector'+y+'row1').setAttribute("className", "sectorOff");
        document.getElementById('sector'+y2+'row2').bgColor=colorLight[9];
        document.getElementById('sector'+y2+'row2').setAttribute("class", "MED_GRAY");
        document.getElementById('sector'+y2+'row2').setAttribute("className", "MED_GRAY");
        document.getElementById('s'+y2+'Total').bgColor=colorLight[9];
        document.getElementById('s'+y2+'Total').setAttribute("class", "bigEmissions MED_GRAY");
        document.getElementById('s'+y2+'Total').setAttribute("className", "bigEmissions MED_GRAY");
        document.getElementById('s'+y2+'Facility').bgColor=colorLight[9];
        document.getElementById('s'+y2+'Facility').setAttribute("class", "facilitiesNum MED_GRAY");
        document.getElementById('s'+y2+'Facility').setAttribute("className", "facilitiesNum MED_GRAY");
    }

    if (document.getElementById(x).checked==true){
        //alert ('Checked = true');
        z='sector'+y2+' sectorTop';
        a=y2-1;
        document.getElementById('sector'+y+'row1').setAttribute("class", z);
        document.getElementById('sector'+y+'row1').setAttribute("className", z);
        document.getElementById('sector'+y2+'row2').bgColor=colorDark[a];
        document.getElementById('sector'+y2+'row2').setAttribute("class", "");
        document.getElementById('sector'+y2+'row2').setAttribute("className", "");
        document.getElementById('s'+y2+'Total').bgColor=colorDark[a];	// MI
        document.getElementById('s'+y2+'Total').setAttribute("class", "bigEmissions");
        document.getElementById('s'+y2+'Total').setAttribute("className", "bigEmissions");
        document.getElementById('s'+y2+'Facility').bgColor=colorDark[a];	// MI
        document.getElementById('s'+y2+'Facility').setAttribute("class", "facilitiesNum");
        document.getElementById('s'+y2+'Facility').setAttribute("className", "facilitiesNum");
    }
}

function enableDisableSectors(dataSource) {
	if(dataSource == 'P') {
		$(".check").attr('disabled', false);
		$("#petroleum2").attr('disabled', true);
		$("#petroleum5").attr('disabled', true);
		$("#petroleum10").attr('disabled', true);
		$("#petroleum11").attr('disabled', true);
		$("#other7").attr('disabled', true);
	}
	else if(dataSource == 'O') {
		$(".check").attr('disabled', true);
		$("#sector9checkbox").attr('disabled', false);
		$("#petroleum2").attr('disabled', false);
	}
	else if(dataSource == 'B') {
		$(".check").attr('disabled', true);
		$("#sector9checkbox").attr('disabled', false);
		$("#petroleum10").attr('disabled', false);
	}
	else if(dataSource == 'L') {
		$(".check").attr('disabled', true);
		$("#sector9checkbox").attr('disabled', false);
		$("#petroleum5").attr('disabled', false);
	}
	else if(dataSource == 'T') {
		$(".check").attr('disabled', true);
		$("#sector9checkbox").attr('disabled', false);
		$("#petroleum11").attr('disabled', false);
	}
	else if(dataSource == 'F') {
		$(".check").attr('disabled', true);
		$("#sector8checkbox").attr('disabled', false);
		$("#other7").attr('disabled', false);
	}
	else {
		$(".check").attr('disabled', false);
	}
}

function resetFilters(sector){
    if (sector == "Power Plants") {
        $("#sector1checkbox").attr('checked',true);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Waste") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',true);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Metals") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',true);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Minerals") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',true);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Refineries") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',true);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Pulp and Paper") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',true);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Chemicals") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',true);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Other") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',true);
        $("#sector9checkbox").attr('checked',false);
    } else if (sector == "Petroleum and Natural Gas Systems") {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',true);
    }
    
    determineTotalCheckbox10Select();
}

function updateSectorCheckbox(si, ss) {
    // Petroleum and Natural Gas Systems
    if (si == "15") {
        if (ss == "Offshore Petroleum & Natural Gas Production") {
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Onshore Petroleum & Natural Gas Production") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Natural Gas Processing") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Natural Gas Transmission/Compression") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Natural Gas Local Distribution Companies") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Underground Natural Gas Storage") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Liquefied Natural Gas Storage") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Liquefied Natural Gas Imp./Exp. Equipment") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Other Petroleum and Natural Gas Systems") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum10").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else if (ss == "Petroleum & Natural Gas Gathering & Boosting") {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum11").attr('checked',false);
        } else {
            $("#petroleum1").attr('checked',false);
            $("#petroleum2").attr('checked',false);
            $("#petroleum3").attr('checked',false);
            $("#petroleum4").attr('checked',false);
            $("#petroleum5").attr('checked',false);
            $("#petroleum6").attr('checked',false);
            $("#petroleum7").attr('checked',false);
            $("#petroleum8").attr('checked',false);
            $("#petroleum9").attr('checked',false);
            $("#petroleum10").attr('checked',false);
        }
    }
    // Chemicals
    else if (si == "5") {
        if (ss == "Adipic Acid Production") {
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Ammonia Manufacturing") {
            $("#chem1").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Fluorinated GHG Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "HCFC-22 Prod./HFC-23 Dest.") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Hydrogen Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Nitric Acid Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Petrochemical Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Phosphoric Acid Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Silicon Carbide Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem10").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else if (ss == "Titanium Dioxide Production") {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem11").attr('checked',false);
        } else {
            $("#chem1").attr('checked',false);
            $("#chem2").attr('checked',false);
            $("#chem3").attr('checked',false);
            $("#chem4").attr('checked',false);
            $("#chem5").attr('checked',false);
            $("#chem6").attr('checked',false);
            $("#chem7").attr('checked',false);
            $("#chem8").attr('checked',false);
            $("#chem9").attr('checked',false);
            $("#chem10").attr('checked',false);
        }
    }
    // Other
    else if (si == "14") {
        if (ss == "Underground Coal Mines") {
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Food Processing") {
            $("#other1").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Ethanol Production") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Universities") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Manufacturing") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Military") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Use of Electrical Equipment") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if (ss == "Electronics Manufacturing") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other9").attr('checked',false);
            $("#other10").attr('checked',false);
        } else if(ss == "Electrical Equipment Manufacturers") {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other10").attr('checked',false);
        } else {
            $("#other1").attr('checked',false);
            $("#other2").attr('checked',false);
            $("#other3").attr('checked',false);
            $("#other4").attr('checked',false);
            $("#other5").attr('checked',false);
            $("#other6").attr('checked',false);
            $("#other7").attr('checked',false);
            $("#other8").attr('checked',false);
            $("#other9").attr('checked',false);
        }
    }
    // Waste
    else if (si == "2") {
        if (ss == "Municipal Landfills") {
            $("#waste2").attr('checked',false);
            $("#waste3").attr('checked',false);
            $("#waste4").attr('checked',false);
        } else if (ss == "Industrial Landfills") {
            $("#waste1").attr('checked',false);
            $("#waste3").attr('checked',false);
            $("#waste4").attr('checked',false);
        } else if (ss == "Wastewater Treatment") {
            $("#waste1").attr('checked',false);
            $("#waste2").attr('checked',false);
            $("#waste4").attr('checked',false);
        } else {
            $("#waste1").attr('checked',false);
            $("#waste2").attr('checked',false);
            $("#waste3").attr('checked',false);
        }
    }
    // Metals
    else if (si == "6") {
        if (ss == "Aluminum Production") {
            $("#metal2").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal6").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else if (ss == "Ferroalloy Production") {
            $("#metal1").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal6").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else if (ss == "Iron and Steel Production") {
            $("#metal1").attr('checked',false);
            $("#metal2").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal6").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else if (ss == "Lead Production") {
            $("#metal1").attr('checked',false);
            $("#metal2").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal6").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else if (ss == "Magnesium") {
            $("#metal1").attr('checked',false);
            $("#metal2").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal6").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else if (ss == "Zinc Production") {
            $("#metal1").attr('checked',false);
            $("#metal2").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal7").attr('checked',false);
        } else {
            $("#metal1").attr('checked',false);
            $("#metal2").attr('checked',false);
            $("#metal3").attr('checked',false);
            $("#metal4").attr('checked',false);
            $("#metal5").attr('checked',false);
            $("#metal6").attr('checked',false);
        }
    }
    // Minerals
    else if (si == "8") {
        if (ss == "Cement Production") {
            $("#mineral2").attr('checked',false);
            $("#mineral3").attr('checked',false);
            $("#mineral4").attr('checked',false);
            $("#mineral5").attr('checked',false);
        } else if (ss == "Glass Production") {
            $("#mineral1").attr('checked',false);
            $("#mineral3").attr('checked',false);
            $("#mineral4").attr('checked',false);
            $("#mineral5").attr('checked',false);
        } else if (ss == "Lime Manufacturing") {
            $("#mineral1").attr('checked',false);
            $("#mineral2").attr('checked',false);
            $("#mineral4").attr('checked',false);
            $("#mineral5").attr('checked',false);
        } else if (ss == "Soda Ash Manufacturing") {
            $("#mineral1").attr('checked',false);
            $("#mineral2").attr('checked',false);
            $("#mineral3").attr('checked',false);
            $("#mineral5").attr('checked',false);
        } else {
            $("#mineral1").attr('checked',false);
            $("#mineral2").attr('checked',false);
            $("#mineral3").attr('checked',false);
            $("#mineral4").attr('checked',false);
        }
    }
    // Pulp and Paper
    else {
        if (ss == "Pulp and Paper") {
            $("#pulp2").attr('checked', false);
        } else {
            $("#pulp1").attr('checked', false);
        }
    }
    
    determineTotalCheckbox10Select();
}


function getSectorId(sectorName) {
    if (sectorName == "Power Plants") {
        return 3;
    } else if (sectorName == "Petroleum and Natural Gas Systems") {
        return 15;
    } else if (sectorName == "Refineries") {
        return 4;
    } else if (sectorName == "Chemicals") {
        return 5;
    } else if (sectorName == "Other") {
        return 14;
    } else if (sectorName == "Waste") {
        return 2;
    } else if (sectorName == "Metals") {
        return 6;
    } else if (sectorName == "Minerals") {
        return 8;
    } else {
        return 7;
    }
}

function updateSectorFilterButton() {
	var defaultFilter = true;
	
	$("#filterSectorPopover input[type='checkbox']").each( function() {
		if(!$(this).is(":checked")) {
			defaultFilter = false;
			return false;
		}
	});
	
	if(defaultFilter) { $('#sectorBtn').html("Filter Sectors"); }
	else { $('#sectorBtn').html("Filter Sectors (filtered)"); }
}

function updateSectorIcon(sectorClass, iconId) {
	var displayIcon = false;
	
	$(sectorClass).each(function() {
		if(!$(this).is(":checked")) {
			displayIcon = true;
			return false;
		}
	});
	
	if(displayIcon) { $(iconId).show(); }
	else { $(iconId).hide(); }
}

function determineTotalCheckbox10Select() {
	var selectedSectorCount = 0;
	
	if($("#sector1checkbox").attr('checked')) { selectedSectorCount += 1; }
	if($("#sector2checkbox").attr('checked') || $('.check_waste').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector3checkbox").attr('checked') || $('.check_metal').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector4checkbox").attr('checked') || $('.check_mineral').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector5checkbox").attr('checked')) { selectedSectorCount += 1; }
	if($("#sector6checkbox").attr('checked') || $('.check_pulp').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector7checkbox").attr('checked') || $('.check_chem').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector8checkbox").attr('checked') || $('.check_other').prop('checked')) { selectedSectorCount += 1; }
	if($("#sector9checkbox").attr('checked') || $('.check_petro').prop('checked')) { selectedSectorCount += 1; }
	
	if(selectedSectorCount > 1) {
		$("#sector10checkbox").attr('checked', true);
	}
	else {
		$("#sector10checkbox").attr('checked', false);
	}
}

function isPointSourcesOnly() {
	if(
		$("#sector1checkbox").attr('checked') && $("#sector2checkbox").attr('checked') && $("#sector3checkbox").attr('checked') &&
		$("#sector4checkbox").attr('checked') && $("#sector5checkbox").attr('checked') && $("#sector6checkbox").attr('checked') &&
		$("#sector7checkbox").attr('checked') && $("#sector8checkbox").attr('checked') && $("#sector9checkbox").attr('checked') &&
		$("#sector10checkbox").attr('checked') && $("#waste1").attr('checked') && $("#waste2").attr('checked') && $("#waste3").attr('checked') &&
		$("#waste4").attr('checked') && $("#metal1").attr('checked') && $("#metal2").attr('checked') && $("#metal3").attr('checked') &&
		$("#metal4").attr('checked') && $("#metal5").attr('checked') && $("#metal6").attr('checked') && $("#metal7").attr('checked') &&
		$("#mineral1").attr('checked') && $("#mineral2").attr('checked') && $("#mineral3").attr('checked') && $("#mineral4").attr('checked') &&
		$("#mineral5").attr('checked') && $("#pulp1").attr('checked') && $("#pulp2").attr('checked') && $("#chem1").attr('checked') &&
		$("#chem2").attr('checked') && $("#chem3").attr('checked') && $("#chem4").attr('checked') && $("#chem5").attr('checked') &&
		$("#chem6").attr('checked') && $("#chem7").attr('checked') && $("#chem8").attr('checked') && $("#chem9").attr('checked') &&
		$("#chem10").attr('checked') && $("#chem11").attr('checked') && $("#other1").attr('checked') && $("#other2").attr('checked') &&
		$("#other3").attr('checked') && $("#other4").attr('checked') && $("#other5").attr('checked') && $("#other6").attr('checked') &&
		!$("#other7").attr('checked') && $("#other8").attr('checked') && $("#other9").attr('checked') && $("#other10").attr('checked') &&
		$("#petroleum1").attr('checked') && !$("#petroleum2").attr('checked') && $("#petroleum3").attr('checked') && $("#petroleum4").attr('checked') &&
		!$("#petroleum5").attr('checked') && $("#petroleum6").attr('checked') && $("#petroleum7").attr('checked') && $("#petroleum8").attr('checked') &&
		$("#petroleum9").attr('checked') && !$("#petroleum10").attr('checked') && !$("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}

function isOnshoreOnly() {
	if(
		!$("#sector1checkbox").attr('checked') && !$("#sector2checkbox").attr('checked') && !$("#sector3checkbox").attr('checked') &&
		!$("#sector4checkbox").attr('checked') && !$("#sector5checkbox").attr('checked') && !$("#sector6checkbox").attr('checked') &&
		!$("#sector7checkbox").attr('checked') && !$("#sector8checkbox").attr('checked') && $("#sector9checkbox").attr('checked') &&
		!$("#sector10checkbox").attr('checked') && !$("#waste1").attr('checked') && !$("#waste2").attr('checked') &&
		!$("#waste3").attr('checked') && !$("#waste4").attr('checked') && !$("#metal1").attr('checked') && !$("#metal2").attr('checked') &&
		!$("#metal3").attr('checked') && !$("#metal4").attr('checked') && !$("#metal5").attr('checked') && !$("#metal6").attr('checked') &&
		!$("#metal7").attr('checked') && !$("#mineral1").attr('checked') && !$("#mineral2").attr('checked') && !$("#mineral3").attr('checked') &&
		!$("#mineral4").attr('checked') && !$("#mineral5").attr('checked') && !$("#pulp1").attr('checked') && !$("#pulp2").attr('checked') &&
		!$("#chem1").attr('checked') && !$("#chem2").attr('checked') && !$("#chem3").attr('checked') && !$("#chem4").attr('checked') &&
		!$("#chem5").attr('checked') && !$("#chem6").attr('checked') && !$("#chem7").attr('checked') && !$("#chem8").attr('checked') &&
		!$("#chem9").attr('checked') && !$("#chem10").attr('checked') && !$("#chem11").attr('checked') && !$("#other1").attr('checked') &&
		!$("#other2").attr('checked') && !$("#other3").attr('checked') && !$("#other4").attr('checked') && !$("#other5").attr('checked') &&
		!$("#other6").attr('checked') && !$("#other7").attr('checked') && !$("#other8").attr('checked') && !$("#other9").attr('checked') &&
		!$("#other10").attr('checked') && !$("#petroleum1").attr('checked') && $("#petroleum2").attr('checked') && !$("#petroleum3").attr('checked') &&
		!$("#petroleum4").attr('checked') && !$("#petroleum5").attr('checked') && !$("#petroleum6").attr('checked') && !$("#petroleum7").attr('checked') &&
		!$("#petroleum8").attr('checked') && !$("#petroleum9").attr('checked') && !$("#petroleum10").attr('checked') && !$("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}

function isBoostingOnly() {
	if(
		!$("#sector1checkbox").attr('checked') && !$("#sector2checkbox").attr('checked') && !$("#sector3checkbox").attr('checked') &&
		!$("#sector4checkbox").attr('checked') && !$("#sector5checkbox").attr('checked') && !$("#sector6checkbox").attr('checked') &&
		!$("#sector7checkbox").attr('checked') && !$("#sector8checkbox").attr('checked') && $("#sector9checkbox").attr('checked') &&
		!$("#sector10checkbox").attr('checked') && !$("#waste1").attr('checked') && !$("#waste2").attr('checked') &&
		!$("#waste3").attr('checked') && !$("#waste4").attr('checked') && !$("#metal1").attr('checked') && !$("#metal2").attr('checked') &&
		!$("#metal3").attr('checked') && !$("#metal4").attr('checked') && !$("#metal5").attr('checked') && !$("#metal6").attr('checked') &&
		!$("#metal7").attr('checked') && !$("#mineral1").attr('checked') && !$("#mineral2").attr('checked') && !$("#mineral3").attr('checked') &&
		!$("#mineral4").attr('checked') && !$("#mineral5").attr('checked') && !$("#pulp1").attr('checked') && !$("#pulp2").attr('checked') &&
		!$("#chem1").attr('checked') && !$("#chem2").attr('checked') && !$("#chem3").attr('checked') && !$("#chem4").attr('checked') &&
		!$("#chem5").attr('checked') && !$("#chem6").attr('checked') && !$("#chem7").attr('checked') && !$("#chem8").attr('checked') &&
		!$("#chem9").attr('checked') && !$("#chem10").attr('checked') && !$("#chem11").attr('checked') && !$("#other1").attr('checked') &&
		!$("#other2").attr('checked') && !$("#other3").attr('checked') && !$("#other4").attr('checked') && !$("#other5").attr('checked') &&
		!$("#other6").attr('checked') && !$("#other7").attr('checked') && !$("#other8").attr('checked') && !$("#other9").attr('checked') &&
		!$("#other10").attr('checked') && !$("#petroleum1").attr('checked') && !$("#petroleum2").attr('checked') && !$("#petroleum3").attr('checked') &&
		!$("#petroleum4").attr('checked') && !$("#petroleum5").attr('checked') && !$("#petroleum6").attr('checked') && !$("#petroleum7").attr('checked') &&
		!$("#petroleum8").attr('checked') && !$("#petroleum9").attr('checked') && $("#petroleum10").attr('checked') && !$("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}

function isLDCOnly() {
	if(
		!$("#sector1checkbox").attr('checked') && !$("#sector2checkbox").attr('checked') && !$("#sector3checkbox").attr('checked') &&
		!$("#sector4checkbox").attr('checked') && !$("#sector5checkbox").attr('checked') && !$("#sector6checkbox").attr('checked') &&
		!$("#sector7checkbox").attr('checked') && !$("#sector8checkbox").attr('checked') && $("#sector9checkbox").attr('checked') &&
		!$("#sector10checkbox").attr('checked') && !$("#waste1").attr('checked') && !$("#waste2").attr('checked') && !$("#waste3").attr('checked') &&
		!$("#waste4").attr('checked') && !$("#metal1").attr('checked') && !$("#metal2").attr('checked') && !$("#metal3").attr('checked') &&
		!$("#metal4").attr('checked') && !$("#metal5").attr('checked') && !$("#metal6").attr('checked') && !$("#metal7").attr('checked') &&
		!$("#mineral1").attr('checked') && !$("#mineral2").attr('checked') && !$("#mineral3").attr('checked') && !$("#mineral4").attr('checked') &&
		!$("#mineral5").attr('checked') && !$("#pulp1").attr('checked') && !$("#pulp2").attr('checked') && !$("#chem1").attr('checked') &&
		!$("#chem2").attr('checked') && !$("#chem3").attr('checked') && !$("#chem4").attr('checked') && !$("#chem5").attr('checked') &&
		!$("#chem6").attr('checked') && !$("#chem7").attr('checked') && !$("#chem8").attr('checked') && !$("#chem9").attr('checked') &&
		!$("#chem10").attr('checked') && !$("#chem11").attr('checked') && !$("#other1").attr('checked') && !$("#other2").attr('checked') &&
		!$("#other3").attr('checked') && !$("#other4").attr('checked') && !$("#other5").attr('checked') && !$("#other6").attr('checked') &&
		!$("#other7").attr('checked') && !$("#other8").attr('checked') && !$("#other9").attr('checked') && !$("#other10").attr('checked') &&
		!$("#petroleum1").attr('checked') && !$("#petroleum2").attr('checked') && !$("#petroleum3").attr('checked') && !$("#petroleum4").attr('checked') &&
		$("#petroleum5").attr('checked') && !$("#petroleum6").attr('checked') && !$("#petroleum7").attr('checked') && !$("#petroleum8").attr('checked') &&
		!$("#petroleum9").attr('checked') && !$("#petroleum10").attr('checked') && !$("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}

function isPipeOnly() {
	if(
		!$("#sector1checkbox").attr('checked') && !$("#sector2checkbox").attr('checked') && !$("#sector3checkbox").attr('checked') &&
		!$("#sector4checkbox").attr('checked') && !$("#sector5checkbox").attr('checked') && !$("#sector6checkbox").attr('checked') &&
		!$("#sector7checkbox").attr('checked') && !$("#sector8checkbox").attr('checked') && $("#sector9checkbox").attr('checked') &&
		!$("#sector10checkbox").attr('checked') && !$("#waste1").attr('checked') && !$("#waste2").attr('checked') && !$("#waste3").attr('checked') &&
		!$("#waste4").attr('checked') && !$("#metal1").attr('checked') && !$("#metal2").attr('checked') && !$("#metal3").attr('checked') &&
		!$("#metal4").attr('checked') && !$("#metal5").attr('checked') && !$("#metal6").attr('checked') && !$("#metal7").attr('checked') &&
		!$("#mineral1").attr('checked') && !$("#mineral2").attr('checked') && !$("#mineral3").attr('checked') && !$("#mineral4").attr('checked') &&
		!$("#mineral5").attr('checked') && !$("#pulp1").attr('checked') && !$("#pulp2").attr('checked') && !$("#chem1").attr('checked') &&
		!$("#chem2").attr('checked') && !$("#chem3").attr('checked') && !$("#chem4").attr('checked') && !$("#chem5").attr('checked') &&
		!$("#chem6").attr('checked') && !$("#chem7").attr('checked') && !$("#chem8").attr('checked') && !$("#chem9").attr('checked') &&
		!$("#chem10").attr('checked') && !$("#chem11").attr('checked') && !$("#other1").attr('checked') && !$("#other2").attr('checked') &&
		!$("#other3").attr('checked') && !$("#other4").attr('checked') && !$("#other5").attr('checked') && !$("#other6").attr('checked') &&
		!$("#other7").attr('checked') && !$("#other8").attr('checked') && !$("#other9").attr('checked') && !$("#other10").attr('checked') &&
		!$("#petroleum1").attr('checked') && !$("#petroleum2").attr('checked') && !$("#petroleum3").attr('checked') && !$("#petroleum4").attr('checked') &&
		!$("#petroleum5").attr('checked') && !$("#petroleum6").attr('checked') && !$("#petroleum7").attr('checked') && !$("#petroleum8").attr('checked') &&
		!$("#petroleum9").attr('checked') && !$("#petroleum10").attr('checked') && $("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}

function isSF6Only() {
	if(
		!$("#sector1checkbox").attr('checked') && !$("#sector2checkbox").attr('checked') && !$("#sector3checkbox").attr('checked') &&
		!$("#sector4checkbox").attr('checked') && !$("#sector5checkbox").attr('checked') && !$("#sector6checkbox").attr('checked') &&
		!$("#sector7checkbox").attr('checked') && $("#sector8checkbox").attr('checked') && !$("#sector9checkbox").attr('checked') &&
		!$("#sector10checkbox").attr('checked') && !$("#waste1").attr('checked') && !$("#waste2").attr('checked') && !$("#waste3").attr('checked') &&
		!$("#waste4").attr('checked') && !$("#metal1").attr('checked') && !$("#metal2").attr('checked') && !$("#metal3").attr('checked') &&
		!$("#metal4").attr('checked') && !$("#metal5").attr('checked') && !$("#metal6").attr('checked') && !$("#metal7").attr('checked') &&
		!$("#mineral1").attr('checked') && !$("#mineral2").attr('checked') && !$("#mineral3").attr('checked') && !$("#mineral4").attr('checked') &&
		!$("#mineral5").attr('checked') && !$("#pulp1").attr('checked') && !$("#pulp2").attr('checked') && !$("#chem1").attr('checked') &&
		!$("#chem2").attr('checked') && !$("#chem3").attr('checked') && !$("#chem4").attr('checked') && !$("#chem5").attr('checked') &&
		!$("#chem6").attr('checked') && !$("#chem7").attr('checked') && !$("#chem8").attr('checked') && !$("#chem9").attr('checked') &&
		!$("#chem10").attr('checked') && !$("#chem11").attr('checked') && !$("#other1").attr('checked') && !$("#other2").attr('checked') &&
		!$("#other3").attr('checked') && !$("#other4").attr('checked') && !$("#other5").attr('checked') && !$("#other6").attr('checked') &&
		$("#other7").attr('checked') && !$("#other8").attr('checked') && !$("#other9").attr('checked') && !$("#other10").attr('checked') &&
		!$("#petroleum1").attr('checked') && !$("#petroleum2").attr('checked') && !$("#petroleum3").attr('checked') && !$("#petroleum4").attr('checked') &&
		!$("#petroleum5").attr('checked') && !$("#petroleum6").attr('checked') && !$("#petroleum7").attr('checked') && !$("#petroleum8").attr('checked') &&
		!$("#petroleum9").attr('checked') && !$("#petroleum10").attr('checked') && !$("#petroleum11").attr('checked')
	)
	{
		return true;
	}
	else {
		return false;
	}
}