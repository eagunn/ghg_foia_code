/**
 * Created by alabdullahwi on 5/26/2015.
 */


var UI = {
    currentView:''
    ,Popups : Popups

};



function setSupplierSector() {
    if (supplierSector == 0) {
        $("#sectorLabel").html('Choose Sector');
    } else {
        if (supplierSector == 11) {
            $("#sectorLabel").html('Coal to Liquids Importers');
        } else if (supplierSector == 12) {
            $("#sectorLabel").html('Coal to Liquids Exporters');
        } else if (supplierSector == 13) {
            $("#sectorLabel").html('Producers of Coal-based Liquid Fuels');
        } else if (supplierSector == 21) {
            $("#sectorLabel").html('Petroleum Product Importers');
        } else if (supplierSector == 22) {
            $("#sectorLabel").html('Petroleum Product Exporters');
        } else if (supplierSector == 23) {
            $("#sectorLabel").html('Refineries');
        } else if (supplierSector == 31) {
            $("#sectorLabel").html('Natural Gas Suppliers (All)');
        } else if (supplierSector == 32) {
            $("#sectorLabel").html('Local Distribution Companies');
        } else if (supplierSector == 33) {
            $("#sectorLabel").html('Natural Gas Liquids Fractionators');
        } else if (supplierSector == 41) {
            $("#sectorLabel").html('Industrial Gas Importers');
        } else if (supplierSector == 42) {
            $("#sectorLabel").html('Industrial Gas Exporters');
        } else if (supplierSector == 43) {
            $("#sectorLabel").html('Industrial Gas Producers');
        } else if (supplierSector == 51) {
            $("#sectorLabel").html('CO<sub>2</sub> Importers');
        } else if (supplierSector == 52) {
            $("#sectorLabel").html('CO<sub>2</sub> Exporters');
        } else if (supplierSector == 53) {
            $("#sectorLabel").html('CO<sub>2</sub> Capture');
        } else if (supplierSector == 54) {
            $("#sectorLabel").html('CO<sub>2</sub> Production Wells');
        } else if (supplierSector == 61) {
            $("#sectorLabel").html('Importers of Equipment Containing Fluorinated GHGs');
        } else if (supplierSector == 62) {
            $("#sectorLabel").html('Exporters of Equipment Containing Fluorinated GHGs');
        }
    }
}

function sortSelected(sortColumn) {
    // facility
    if (sortColumn == 0) {
        if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
            if (emitterSort == 0 || emitterSort == 2 || emitterSort == 3)
                emitterSort = 1;
            else
                emitterSort = 0;
        }
        else {
            if (supplierSort == 0 || supplierSort == 2 || supplierSort == 3)
                supplierSort = 1;
            else
                supplierSort = 0;
        }
        // emission/quantities
    }
    else {
        if (supplierSort == 2 || supplierSort == 0 || supplierSort == 1)
            supplierSort = 3;
        else
            supplierSort = 2;
    }
    generateURL('');
}
function setSort() {
    if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
        if (emitterSort == 0) {
            $('#facSort').attr('src','img/icon-downarrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        }
        else if (emitterSort == 1) {
            $('#facSort').attr('src','img/icon-uparrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        }
        else if (emitterSort == 2) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-downarrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        }
        else if (emitterSort == 3) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-uparrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        }
    }
    else {
        if (supplierSort == 0) {
            $('#facSort').attr('src','img/icon-downarrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        }
        else if (supplierSort == 1) {
            $('#facSort').attr('src','img/icon-uparrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        }
        else if (supplierSort == 2) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-downarrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        }
        else if (supplierSort == 3) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-uparrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        }
    }
}

function setDataType(type) {
    if (type == 'E') {
        $("#sector1checkbox").attr('checked',true);
        $("#sector2checkbox").attr('checked',true);
        $("#sector3checkbox").attr('checked',true);
        $("#sector4checkbox").attr('checked',true);
        $("#sector5checkbox").attr('checked',true);
        $("#sector6checkbox").attr('checked',true);
        $("#sector7checkbox").attr('checked',true);
        $("#sector8checkbox").attr('checked',true);
        $("#sector9checkbox").attr('checked',true);
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
    }
    else if (type == 'O') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',true);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
        $("#petroleum1").attr('checked',false);
        $("#petroleum2").attr('checked',true);
        $("#petroleum3").attr('checked',false);
        $("#petroleum4").attr('checked',false);
        $("#petroleum5").attr('checked',false);
        $("#petroleum6").attr('checked',false);
        $("#petroleum7").attr('checked',false);
        $("#petroleum8").attr('checked',false);
        $("#petroleum9").attr('checked',false);
        $("#petroleum10").attr('checked',false);
        $("#petroleum11").attr('checked',false);
    }
    else if (type == 'B') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',true);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
        $("#petroleum1").attr('checked',false);
        $("#petroleum2").attr('checked',false);
        $("#petroleum3").attr('checked',false);
        $("#petroleum4").attr('checked',false);
        $("#petroleum5").attr('checked',false);
        $("#petroleum6").attr('checked',false);
        $("#petroleum7").attr('checked',false);
        $("#petroleum8").attr('checked',false);
        $("#petroleum9").attr('checked',false);
        $("#petroleum10").attr('checked',true);
        $("#petroleum11").attr('checked',false);
    }
    else if (type == 'L') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',true);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
        $("#petroleum1").attr('checked',false);
        $("#petroleum2").attr('checked',false);
        $("#petroleum3").attr('checked',false);
        $("#petroleum4").attr('checked',false);
        $("#petroleum5").attr('checked',true);
        $("#petroleum6").attr('checked',false);
        $("#petroleum7").attr('checked',false);
        $("#petroleum8").attr('checked',false);
        $("#petroleum9").attr('checked',false);
        $("#petroleum10").attr('checked',false);
        $("#petroleum11").attr('checked',false);
    }
    else if (type == 'T') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',true);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
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
        $("#petroleum11").attr('checked',true);
    }
    else if (type == 'I' || type == 'A' || type == 'S') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',false);
        $("#sector9checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
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
        $("#petroleum11").attr('checked',false);
    }
    else if (type == 'F') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
        $("#sector8checkbox").attr('checked',true);
        $("#sector9checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
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
        $("#chem11").attr('checked',false);
        $("#other1").attr('checked',false);
        $("#other2").attr('checked',false);
        $("#other3").attr('checked',false);
        $("#other4").attr('checked',false);
        $("#other5").attr('checked',false);
        $("#other6").attr('checked',false);
        $("#other7").attr('checked',true);
        $("#other8").attr('checked',false);
        $("#other9").attr('checked',false);
        $("#other10").attr('checked',false);
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
        $("#petroleum11").attr('checked',false);
    }
    else if (type == 'P') {
        $("#sector1checkbox").attr('checked',true);
        $("#sector2checkbox").attr('checked',true);
        $("#sector3checkbox").attr('checked',true);
        $("#sector4checkbox").attr('checked',true);
        $("#sector5checkbox").attr('checked',true);
        $("#sector6checkbox").attr('checked',true);
        $("#sector7checkbox").attr('checked',true);
        $("#sector8checkbox").attr('checked',true);
        $("#sector9checkbox").attr('checked',true);
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
        $("#other1").attr('checked',true);
        $("#other2").attr('checked',true);
        $("#other3").attr('checked',true);
        $("#other4").attr('checked',true);
        $("#other5").attr('checked',true);
        $("#other6").attr('checked',true);
        $("#other7").attr('checked',false);
        $("#other8").attr('checked',true);
        $("#other9").attr('checked',true);
        $("#other10").attr('checked',true);
        $("#petroleum1").attr('checked',true);
        $("#petroleum2").attr('checked',false);
        $("#petroleum3").attr('checked',true);
        $("#petroleum4").attr('checked',true);
        $("#petroleum5").attr('checked',false);
        $("#petroleum6").attr('checked',true);
        $("#petroleum7").attr('checked',true);
        $("#petroleum8").attr('checked',true);
        $("#petroleum9").attr('checked',true);
        $("#petroleum10").attr('checked',false);
        $("#petroleum11").attr('checked',false);
    }
    determineTotalCheckbox10Select();
    dataSource = type;
    updateSectorFilterButton();
    enableDisableSectors(dataSource);
}

function setDataTypeValueOnly(type) {
	dataSource = type;
	updateSectorFilterButton();
	enableDisableSectors(dataSource);
}

function switchDataSource() {
    closeWelcomeWindow();
    if (dataSource == 'E') {
        dataSource = 'S';
        if (welcomeSupplierScreenAlreadyShown == false) {
            $("#mask").css('width','100%');
            $("#mask").css('height','100%');
            $("#mask").fadeTo("slow",0.5);
            $("#welcomeCloseEmitter").attr('style','display:none');
            $("#welcomeCloseSupplier").attr('style','display:none');
            $("#welcomeWindowEmitters").attr('style', 'display:none');
            $("#welcomeWindowSuppliers").attr('style', 'display:block');
            $("#welcomeWindow").css('top', getBrowserWindowHeight()/2 - 150);
            $("#welcomeWindow").css('left', getBrowserWindowWidth()/2 - 427);
            $("#welcomeWindow").fadeIn(1000);
            welcomeSupplierScreenAlreadyShown = true;
        }
    }
    else {
        dataSource = 'E';
    }
    setURLPath();
}
function ajaxLoading() {
    $('#canvas-vis').html('<div class="spinner" style="text-align:center"><img src="img/loadingBigTransparent.gif" alt="loading image"></div>');
}
function setInjectionSelection() {
    if (injectionSelection == 11) {
        $("#injectionLabel").html('All CO<sub>2</sub> Injectors');
    }
    else if (injectionSelection) {
        $("#injectionLabel").html('CO<sub>2</sub> Injectors with an R&amp;D Exemption');
    }
}
function injectionSelected(option) {
    injectionSelection = option;
    hasTrend(injectionSelection, false);
}
function supplierSectorSelected(sector) {
    if (sector != 0) {
        closeWelcomeWindow();
        supplierSector = sector;
        hasTrend(supplierSector, false);
    }
}

function facTypeSelect(facType) {
	var displayText = "";
	
    if(facType == 'factype0') {
    	displayText = "All Direct Emitters";
    	document.getElementById("dataType").selectedIndex = 0;
    	$("#opt_allEmitters").change();
    } else if(facType == 'factype1') {
    	displayText = "Point Sources";
    	document.getElementById("dataType").selectedIndex = 1;
    	$("#opt_pointSources").change();
    } else if(facType == 'factype2') {
    	displayText = "Onshore Oil & Gas Production";
    	document.getElementById("dataType").selectedIndex = 2;
    	$("#opt_onshore").change();
    } else if(facType == 'factype3') {
    	displayText = "Onshore Oil & Gas Gathering & Boosting";
    	document.getElementById("dataType").selectedIndex = 3;
    	$("#opt_boosting").change();
    } else if(facType == 'factype4') {
    	displayText = "Local Distribution Companies";
    	document.getElementById("dataType").selectedIndex = 4;
    	$("#opt_ldc").change();
    } else if(facType == 'factype5') {
    	displayText = "Onshore Gas Transmission Pipelines";
    	document.getElementById("dataType").selectedIndex = 5;
    	$("#opt_transmission").change();
    } else if(facType == 'factype6') {
    	displayText = "SF6 from Elect. Dist. Systems";
    	document.getElementById("dataType").selectedIndex = 6;
    	$("#opt_sf6").change();
    } else if(facType.includes('factype7')) {
    	document.getElementById("dataType").selectedIndex = 7;
    	$("#opt_suppliers").change();
    	
    	if(facType == 'factype7-1') {
    		displayText = "Coal-based Liquid Fuel Suppliers: Producers";
    		supplierSectorSelected(13);
    	} else if(facType == 'factype7-2') {
    		displayText = "Petroleum Product Suppliers: Refineries";
    		supplierSectorSelected(23);
    	} else if(facType == 'factype7-3') {
    		displayText = "Petroleum Product Suppliers: Importers";
    		supplierSectorSelected(21);
    	} else if(facType == 'factype7-4') {
    		displayText = "Petroleum Product Suppliers: Exporters";
    		supplierSectorSelected(22);
    	} else if(facType == 'factype7-5') {
    		displayText = "Natural Gas and Natural Gas Liquids Suppliers: Local Distribution Companies";
    		supplierSectorSelected(32);
    	} else if(facType == 'factype7-6') {
    		displayText = "Natural Gas and Natural Gas Liquids Suppliers: Natural Gas Liquids Fractionators";
    		supplierSectorSelected(33);
    	} else if(facType == 'factype7-7') {
    		displayText = "Industrial Gas Suppliers: Importers";
    		supplierSectorSelected(41);
    	} else if(facType == 'factype7-8') {
    		displayText = "Industrial Gas Suppliers: Exporters";
    		supplierSectorSelected(42);
    	} else if(facType == 'factype7-9') {
    		displayText = "Industrial Gas Suppliers: Producers";
    		supplierSectorSelected(43);
    	} else if(facType == 'factype7-10') {
    		displayText = "Equipment Containing Fluorinated GHGs: Importers";
    		supplierSectorSelected(61);
    	} else if(facType == 'factype7-11') {
    		displayText = "Equipment Containing Fluorinated GHGs: Exporters";
    		supplierSectorSelected(62);
    	}
    } else if(facType == 'factype8-1' || facType == 'factype8-2' || facType == 'factype8-3' || facType == 'factype8-4') {
    	document.getElementById("dataType").selectedIndex = 7; // this is 7 due to old menu
    	$("#opt_suppliers").change();
    	
    	if(facType == 'factype8-1') {
    		displayText = "Suppliers of CO<sub>2</sub>: Importers (PP)";
    		supplierSectorSelected(51);
    	} else if(facType == 'factype8-2') {
    		displayText = "Suppliers of CO<sub>2</sub>: Exporters (PP)";
    		supplierSectorSelected(52);
    	} else if(facType == 'factype8-3') {
    		displayText = "Suppliers of CO<sub>2</sub>: CO<sub>2</sub> Capture (PP)";
    		supplierSectorSelected(53);
    	} else if(facType == 'factype8-4') {
    		displayText = "Suppliers of CO<sub>2</sub>: CO<sub>2</sub> Production Wells (PP)";
    		supplierSectorSelected(54);
    	}
    } else if(facType == 'factype8-5' || facType == 'factype8-6') {
    	document.getElementById("dataType").selectedIndex = 8;
    	$("#opt_co2injection").change();
    	
    	if(facType == 'factype8-5') {
    		displayText = "All CO<sub>2</sub> Injectors (UU)";
    		injectionSelected(11);
    	} else if(facType == 'factype8-6') {
    		displayText = "CO<sub>2</sub> Injectors with an R&amp;D Exemption (UU)";
    		injectionSelected(12);
    	}
    } else if(facType == 'factype9') {
    	displayText = "Geologic Sequestration of CO<sub>2</sub> (RR)";
    	document.getElementById("dataType").selectedIndex = 9;
    	$("#opt_rr").change();
    }
    
    if(displayText != "") {
    	$("#facilityTypeDropdown .btn:first-child").html(displayText + "<span class=\"caret\" style=\"float:right;\"></span>");
    }
}

function selectMsa(event) {
    $("#countyState").val(this.get("msaCode"));
    generateURL('');
}


function selectCounty(event) {
    $("#countyState").val(this.get("fipsCode"));
    generateURL('');
}


function selectBasin(event) {
    $("#basin").val(this.get("basinCode"));
    generateURL('');
}
function stateChange(stateAbbr){
    if($("#lastState2").val() == stateAbbr)
        return false;
    else
        return true;
}

function stateLevelChange(stateLevel) {
    if($("#lastStateLevel").val() == stateLevel) {
        return false;
    }
    else {
        $("#lastStateLevel").val(stateLevel);
        return true;
    }
}

function dataSourceChange(dataSource){
    if($("#lastds").val() == dataSource){
        $("#lastds").val(dataSource);
        return false;
    } else {
        $("#lastds").val(dataSource);
        return true;
    }
}


function setVisType(type) {
    visType = type;
    setURLPath();
}

function setSelector(type) {
    if (visType == 'map') {
        mapSelector = type;
    } else if (visType == 'list'){
        listSelector = type;
    } else if (visType == 'bar') {
        barSelector = type;
    } else if (visType == 'pie') {
        pieSelector = type;
    } else if (visType == 'tree') {
        treeSelector = type;
    }
    $('#isGeoList').val(0);
    if (listSelector == 0) $('#isGeoList').val(1);
    setURLPath();
}

function setTrend(type) {

    if (reportingStatus != 'ALL') {
        trendSelection = 'current';
    }
    else {
        trendSelection = type;
        setURLPath();
    }
}

function displayFacilityDetail(id){
    $("#facilityId").val(id);
    var lastURL = $("#lastURL").val();
    var currentURL = window.location.href;
    if (currentURL.indexOf("facilityDetail") == -1) {
        $("#lastURL").val(currentURL);
    }
    generateURL('facilityDetail');
}

function setOverlaySelector(level) {
   overlayLevel = level;
}

function setStateLevel(ryear, stateAbbr, level, draw) {
    var facOrLoc = $("#facOrLocInput").val();
    if(unescape(facOrLoc) == "Find a Facility or Location"){
        facOrLoc = "";
    }
    var lowE = $("#lowEmissionRange").val();
    var highE = $("#highEmissionRange").val();

    if(stateAbbr == "") {
        stateAbbr = $("#parentState").val();
    }


    if((level == 0 && stateChange(stateAbbr)) || (level == 0 && stateLevelChange(level))) {
        jQuery.getJSON("service/getCountiesFromState/"+ryear+"?st="+stateAbbr, function(data) {
            htmlStr = "";
            htmlStr += "<option value =''>Choose County</option>";
            for(var i = 0; i < data.length; i++){
                htmlStr += "<option value ='"+data[i].id+"'>"+data[i].name+"</option>";
            }
            $("#countyState").html(htmlStr);
            if (stateAbbr == "TL") {
            	$("#countySelectAndSearch").css('display','none');
            } else {
                $("#countyState").attr('title', 'County selection');
                if (dataSource == "L") {
                    $("#countyState").attr('style', 'float:left; display:inline').attr('disabled','disabled');
                } else {
                    $("#countyState").attr('style', 'float:left; display:inline').attr('disabled', false);
                }
                if (visType != "map") {
                    $("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
                    $("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
                } else {
                    $("#msaBtnMap").removeClass('softBtnRightOn').addClass('softBtnRight');
                    $("#countyBtnMap").removeClass('softBtnLeft').addClass('softBtnLeftOn');
                }
                $("#countyLabel").html('Pick a County');
                $("#lastStateLevel").val(0);

            }
        });
        stateLevel = 0;
        if (draw == 1) {
            drawMap(facOrLoc,abbreviationToState(stateAbbr),"","", lowE, highE, reportingStatus,"")
        }
    } else if((level == 1 && stateChange(stateAbbr)) || (level == 1 && stateLevelChange(level))){
        jQuery.getJSON("service/getMSAsFromState/"+ryear+"?st="+stateAbbr, function(data) {
            htmlStr = "";
            htmlStr += "<option value =''>Choose Metro Area</option>";
            for(var i = 0; i < data.length; i++){
                htmlStr += "<option value ='"+data[i].id+"'>"+data[i].name+"</option>";
            }
            $("#countyState").html(htmlStr);
            $("#countyState").attr('title', 'Metro selection');
            if (dataSource == "L") {
                $("#countyState").attr('style', 'float:left; display:inline').attr('disabled','disabled');
            } else {
                $("#countyState").attr('style', 'float:left; display:inline').attr('disabled', false);
            }

            if (visType != "map") {
                $("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
                $("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
            } else {
                $("#countyBtnMap").removeClass('softBtnLeftOn').addClass('softBtnLeft');
                $("#msaBtnMap").removeClass('softBtnRight').addClass('softBtnRightOn');
            }
            $("#countyLabel").html('Pick a Metro Area');
            $("#lastStateLevel").val(1);

        });
        stateLevel = 1;
        if (draw == 1) {
            drawMap(facOrLoc,abbreviationToState(stateAbbr),"","", lowE, highE, reportingStatus,"")
        }
    }
    generateURL('');
}


function openInfoWindow(event) {
    if(isState(this.getObject("countyNum"))){
        $("#parentState").val(stateToAbbreviation(this.getObject("countyNum")));
        generateURL('');
    } else {
        $("#countyState").val(addLeadingZeros(this.getObject("countyNum")));
        var fipsCode = addLeadingZeros(this.getObject("countyNum"));
        jQuery.getJSON("getCountyFromFips?fc="+fipsCode, function(data){
            $("#countyName").val(data);
            setSelector('0');
        });
    }
}

function goToPage(pageNumber) {
    //Get all of the queryable params
    //If this parameter has default value, set to ""
    var county = "";
    var lowE = "";
    var highE = "";
    var facOrLoc = $("#facOrLocInput").val();
    if(facOrLoc == "Find a Facility or Location"){
        facOrLoc = "";
    }
    var state = $("#parentState").val();
    var fipsCode = addLeadingZeros($("#countyState").val());
    var basinCode = $("#basin").val();
    var msaCode = "";
    if(stateLevel == 1) {
        msaCode = fipsCode;
        fipsCode = "";
    }
    var tribalLandId = "";
    if(state == "TL" ) {
        tribalLandId = $("#tribe").find(":selected").val();
        fipsCode = "";
        msaCode = "";
    }
    lowE = $("#lowEmissionRange").val();
    highE = $("#highEmissionRange").val();

    queryTable(pageNumber,facOrLoc,fipsCode,state,msaCode,basinCode,tribalLandId,lowE,highE,sortOrder,reportingStatus);
}


function gasSelectionString() {
    if($("#gas1check").attr('checked') &&
        $("#gas2check").attr('checked') &&
        $("#gas3check").attr('checked') &&
        $("#gas4check").attr('checked') &&
        $("#gas5check").attr('checked') &&
        $("#gas6check").attr('checked') &&
        $("#gas7check").attr('checked') &&
        $("#gas8check").attr('checked') &&
        $("#gas9check").attr('checked') &&
        $("#gas10check").attr('checked')) {
        return " ";
    } else {
        return " of Selected Gases ";
    }
}

function closeState(){
    //Erase everything and sets params to null
    $("resultsDisplay").html("");
    $("#parentState").val('');
    $("#countyState").val('');
    //Display state dropdown
    $("#countyState").attr('style', 'float:right; display:none');
    $("#parentState").attr('style', 'float:right; display:inline');
    generateURL('');
}

function closeCounty(){
    //Erase everyhing but state and set other params to null
    var htmlStr = $("#resultsDisplay").html();
    var index = htmlStr.indexOf("a>");
    index = index+2;
    htmlStr = htmlStr.substring(0,index);
    $("#resultsDisplay").html(htmlStr);
    $("#countyState").val('');
    //Display county dropdown
    $("#countyState").attr('style', 'float:right; display:inline');
    $("#parentState").attr('style', 'float:right; display:none');
    generateURL('');
}
function resolveMarkerClusterStyle(reportingStatus) {

    var retVal = {};
    if (reportingStatus=='ORANGE') {
        retVal.url = "img/m_orange.png";
    }

    else if (reportingStatus == 'RED') {
        retVal.url = "img/m_red.png";
    }
    else if (reportingStatus == 'GRAY') {
        retVal.url = "img/m_gray.png";
    }
    else  {
        retVal.url = "img/m1.png";
    }
    retVal.height= 56;
    retVal.width = 56;


    return [retVal];

}

function resolveReportingStatusWhatsThisText(reportingStatus, cyear, dataDate) {

    var returnVal="";
	var rs19="EPA&apos;s verification requirements have not been met as of " + dataDate;
	if (cyear >= 2019) {
		rs19="Verification of a facility&apos;s report was still in progress as of " + dataDate;
	}
	
    if (reportingStatus=='ALL') {

        returnVal= '<p>Facilities are color coded to designate their verification and reporting status as of ' + dataDate + ' for reporting year '+ cyear +'.'
        +'<br/><br/><span class="orangeText">Orange</span> indicates ' + rs19 + '. '
        +'<a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139281" target="_blank">Click here </a> to learn more about verification. Note (for RY21 and forward) that if a facility certified their report after the annual reporting deadline and verification was still in progress as of ' + dataDate + ', the facility&apos;s icon will display as orange.'
        +'</p><br/><p><span class="redText">Red</span> indicates a facility has discontinued reporting without a valid reason as of ' + dataDate + '  or (for RY21 and forward) the facility certified their report after the reporting deadline.'
        +'<br/><br/><span class="greyText">Grey</span> indicates a facility has discontinued reporting with a <a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139271" target="_blank"> valid reason.</a>'
        +'<br/><br/><span class="blackText">Black</span> indicates a facility has submitted a ' + cyear + ' report and has met EPA&apos;s verification requirements.'
        ;

    }

    else if (reportingStatus=='ORANGE') {
        returnVal =
            '<p>' + rs19 + '. '
            + '<a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139281" target="_blank">'
            + 'Click here to learn more'
            + '</a></p>';
    }

    else if (reportingStatus == 'RED') {
        returnVal =
            '<p>Facility discontinued reporting without a valid reason as of ' + dataDate+ '.</p>'
        ;
    }
    else if (reportingStatus == 'GRAY') {
        returnVal =
            '<p>Facility discontinued reporting for a'
            + '<a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139271" target="_blank"> '
            + 'valid reason.</a></p>'
        ;
    }
    else if (reportingStatus == 'BLACK') {
        returnVal =
            "<p>Facility has met EPA&apos;s reporting and verification requirements for " + cyear
            + ".</p>";

    }

    return returnVal;

}

function printPage() {
    if (window.print) {
        window.print();
    }
    else {
        alert('To print this page, open the File menu and select Print...');
    }
}
function closeWelcomeWindow(){
    $("#mask").hide();
    $("#welcomeWindow").hide();
}

function runStateQuery(){
	var welSt = $("#welcomeState").val();
	if (welSt!=null) {
	    $("#mask").hide();
	    $("#welcomeWindow").hide();
	    $("#parentState").val(welSt);
	    generateURL('facility');
	}
}

function clickX(){
    $("#mask").hide();
    $("#welcomeWindow").hide();
    //if (dataSource == 'S' && welcomeSupplierScreenShow == false) {
    //	jQuery("#mask").css('width',jQuery(document).width());
    //	jQuery("#mask").css('height',jQuery(window).height());
    //	jQuery("#mask").fadeTo("slow",0.5);
    //	jQuery("#welcomeCloseEmitter").attr('style','display:none');
    //	jQuery("#welcomeCloseSupplier").attr('style','display:none');
    //	jQuery("#welcomeWindowEmitters").attr('style', 'display:none');
    //	jQuery("#welcomeWindowSuppliers").attr('style', 'display:block');
    //	jQuery("#welcomeWindow").css('top', getBrowserWindowHeight()/2 - 150);
    //	jQuery("#welcomeWindow").css('left', getBrowserWindowWidth()/2 - 427);
    //	jQuery("#welcomeWindow").fadeIn(1000);
    //}
}

function runCustomQuery(){
    $("#mask").hide();
    $("#welcomeWindow").hide();
    $("#facOrLocInput").val($("#facOrLocWelcome").val());
    generateURL('facility');
}

function updateBreadcrumbs(state,county,zip,facility,fipsCode){
//		var htmlStr = "<li class='first'><a href='#/listSector/?state=Select' tabindex='75'>United States</a></li>";
//		if(state != ""){
//			if(county != ""){
//				htmlStr += "<li><a href='#/listSector/?state="+stateToAbbreviation(state)+"' tabindex='75'>"+state+"</a></li>";
//			} else {
//				htmlStr += "<li class='thepage'>"+state+"</li>";
//			}
//		}
//		if(county != ""){
//			if(zip != ""){
//				htmlStr += "<li><a href='#/listSector/?state="+stateToAbbreviation(state)+"&fipsCode="+fipsCode+"&countyName="+county+"' tabindex='75'>"+county+"</a></li>";
//			} else {
//				htmlStr += "<li class='thepage'>"+county+"</li>";
//			}
//		}
//		if(zip != ""){
//			if(facility != ""){
//				htmlStr += "<li><a href='#' tabindex='75'>"+faciziplity+"</a></li>";
//			} else {
//				htmlStr += "<li class='thepage'>"+zip+"</li>";
//			}
//		}
//		if(facility != ""){
//			htmlStr += "<li class='thepage'>"+facility+"</li>";
//		}
//		jQuery("#breadcrumbs").html(htmlStr);
    if (dataSource == 'E') {
        $('#usBCDiv').removeClass('bc1 bc3').addClass('bc2');
    } else {
        $('#usBCDiv').removeClass('bc1 bc2').addClass('bc3');
    }
    if(state != "") {
        $('#usBCDiv').removeClass('bc2').addClass('bc1');
        $('#stateBC').html(state);
        $('#stateBCDiv').attr('style','display:inline');
        if(county != "" && county != "Choose County") {
            $('#stateBCDiv').removeClass('bc2').addClass('bc1');
            $('#countyBC').html(county);
            $('#countyBCDiv').attr('style','display:inline');
            if (facility != "") {
                $('#facilityBC').html(facility);
                $('#facilityBCDiv').attr('style','display:inline');
            } else {
                $('#countyBCDiv').removeClass('bc1').addClass('bc2');
                $('#facilityBCDiv').attr('style','display:none');
            }
        } else {
            $('#stateBCDiv').removeClass('bc1').addClass('bc2');
            $('#countyBCDiv').attr('style','display:none');
        }
    } else {
        $('#usBCDiv').removeClass('bc1').addClass('bc2');
        $('#stateBCDiv').attr('style','display:none');
        $('#countyBCDiv').attr('style','display:none');
    }
//		if(county != "") {
//			if(zip != "") {
//				htmlStr += "<li><a href='#/listSector/?state="+stateToAbbreviation(state)+"&fipsCode="+fipsCode+"&countyName="+county+"' tabindex='75'>"+county+"</a></li>";
//			} else {
//				htmlStr += "<li class='thepage'>"+county+"</li>";
//			}
//		}
//		if(facility != ""){
//			htmlStr += "<li class='thepage'>"+facility+"</li>";
//		}
}
