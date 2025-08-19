/**
 * Created by alabdullahwi on 5/26/2015.
 */

var UI = (function() {

  var _view = "";
  var SubstateToggle = (function () {

       var MapView = (function() {
           var _id = "#substateMapToggle";
           var CountyToggle = new Parameter({ui:'#countyBtnMap', uiContainer: '#substateMapToggle' });

           CountyToggle.turnOn = function() {
              $(CountyToggle._ui).removeClass('softBtnLeft').addClass('softBtnLeftOn');
           };
           CountyToggle.turnOff = function() {
               $(CountyToggle._ui).removeClass('softBtnLeftOn').addClass('softBtnLeft');
           };

           var MSAToggle = new Parameter({ui:'#msaBtnMap', uiContainer: '#substateMapToggle' });
           MSAToggle.turnOn = function() {
               $(MSAToggle._ui).removeClass('softBtnRight').addClass('softBtnRightOn');
           };
           MSAToggle.turnOff = function() {
               $(MSAToggle._ui).removeClass('softBtnRightOn').addClass('softBtnRight');
           };

           return {
               wipe : function() {
                   Map.wipe('Substate Toggle');
               },
               create : function() {

                   //if substate toggle is already there, don't create anything
                   if ($(_id).length > 0) {
                       return;
                   }

                   //this logic needs to be here and not in Map because the MSAToggle and CountyToggles are privates
                   var stateLevelSelector = document.createElement('div');
                   var countyBtn = document.createElement('div');
                   countyBtn.setAttribute('class','softBtnLeftMap');
                   countyBtn.setAttribute('id', 'countyBtnMap');
                   countyBtn.style.borderRightStyle = 'solid';
                   countyBtn.style.borderRightWidth = '1px';
                   countyBtn.style.borderRightColor = '#CCC';
                   countyBtn.innerHTML = '<a href="javascript:void(0)">County</a>';
                   var msaBtn = document.createElement('div');
                   msaBtn.setAttribute('class', 'softBtnRightMap');
                   msaBtn.setAttribute('id', 'msaBtnMap');
                   msaBtn.style.marginRight = '15px';
                   msaBtn.innerHTML = '<a href="javascript:void(0)">Metro Area</a>';
                   stateLevelSelector.appendChild(countyBtn);
                   stateLevelSelector.appendChild(msaBtn);
                   stateLevelSelector.setAttribute('id','substateMapToggle');
                   stateLevelSelector.style.padding = '4px';
                   stateLevelSelector.style.paddingLeft = '80px';
                   if (MSA.get()) {
                       MSAToggle.turnOn();
                       CountyToggle.turnOff();
                   } else {
                       CountyToggle.turnOn();
                       MSAToggle.turnOff();
                   }
                   return stateLevelSelector;
               },
               getId : function() { return _id ; },
               registerListeners: function() {
                   //this delegate business is instead of the deprecated live per Stack Overflow
                   //can't use 'on' because we're still using jQuery 1.4 here
                  $('body').delegate(CountyToggle._ui, 'click', function() {
                     CountyToggle.turnOn();
                     MSAToggle.turnOff();
                     MSA.hide();
                     Backend.Dropdowns.loadCountyNames();
                     // Map.clearPolygons();
                     Backend.Shapes.loadForCounty();
                     County.show();
                  });
                  $('body').delegate(MSAToggle._ui, 'click', function(){
                      County.hide();
                      MSAToggle.turnOn();
                      CountyToggle.turnOff();
                      MSA.disable();
                      // Map.clearPolygons();
                      Backend.Dropdowns.loadMSANames();
                      Backend.Shapes.loadForMSA();
                      MSA.enable();
                      MSA.show();
                  });
               }
           }
       }());
       var ListView = function() {
       };

      var viewDic = { 'MapView' : MapView,
                  'ListView': ListView
      };

      return {
         registerListeners : function() {
             MapView.registerListeners();
         },
         show : function() {
             var currentView = viewDic[_view];
             $(currentView.getId()).show();
            },
         hide : function() {
             var currentView = viewDic[_view];
             $(currentView.getId()).hide();
            },
         create : function() {
             var currentView = viewDic[_view];
             var obj = currentView.create();
             return obj;
            },
          wipe : function() {
              var currentView = viewDic[_view];
              currentView.wipe();
          }
         }

      }());

   var componentDic = { "Substate Toggle" : SubstateToggle};

  return {
      registerListeners: function() {
          SubstateToggle.registerListeners();

      },
      hideSelectors: function(selectors) {
          for (var i = 0 ; i < selectors.length ; i++) {
             selectors[i].set('');
              selectors[i].hide();
          }
      },
      showSelectors : function (selectors) {
          for (var i = 0 ; i < selectors.length ; i++) {
              selectors[i].show();
          }
      },
      getView : function() { return _view; },
      setView : function(view) { if (view === 'map') { _view =  'MapView'; } },
      hide    : function(thing) {
          componentDic[thing].hide();
      },
      show    : function(thing) {
          componentDic[thing].show();
      },
      create : function(thing) {
          return componentDic[thing].create();
      },
      wipe : function(thing) {
          componentDic[thing].wipe();
      },
      Mask : (function() {

          var _mask = function (){ return  $('#mask'); }

          return {

              maximize : function() {
                    var $this = _mask();
                    $this.css('height', '100');
                    $this.css('width', '100');
              },
              appear : function(degree){
                    var $this = _mask();
                    $this.fadeTo('slow', degree);
              }


          }

      } ())

  }




} () );

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
        if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'I') {
            if (emitterSort == 0 || emitterSort == 2 || emitterSort == 3)
                emitterSort = 1;
            else
                emitterSort = 0;
        } else {
            if (supplierSort == 0 || supplierSort == 2 || supplierSort == 3)
                supplierSort = 1;
            else
                supplierSort = 0;
        }
        // emission/quantities
    } else {
        if (supplierSort == 2 || supplierSort == 0 || supplierSort == 1)
            supplierSort = 3;
        else
            supplierSort = 2;
    }
    generateURL('');
}
function setSort() {
    if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B'|| dataSource == 'T' || dataSource == 'I') {
        if (emitterSort == 0) {
            $('#facSort').attr('src','img/icon-downarrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        } else if (emitterSort == 1) {
            $('#facSort').attr('src','img/icon-uparrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        } else if (emitterSort == 2) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-downarrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        } else if (emitterSort == 3) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-uparrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        }
    } else {
        if (supplierSort == 0) {
            $('#facSort').attr('src','img/icon-downarrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        } else if (supplierSort == 1) {
            $('#facSort').attr('src','img/icon-uparrow.png');
            $('#facSort').attr('style','margin-left: 5px; display:inline');
            $('#ghgSort').attr('style','display:none');
        } else if (supplierSort == 2) {
            $('#facSort').attr('style','display:none');
            $('#ghgSort').attr('src','img/icon-downarrow.png');
            $('#ghgSort').attr('style','margin-left: 5px; display:inline');
        } else if (supplierSort == 3) {
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
        $("#waste1").attr('checked',true);
        $("#waste2").attr('checked',true);
        $("#waste3").attr('checked',true);
        $("#waste4").attr('checked',true);
        $("#sector3checkbox").attr('checked',true);
        $("#metal1").attr('checked',true);
        $("#metal2").attr('checked',true);
        $("#metal3").attr('checked',true);
        $("#metal4").attr('checked',true);
        $("#metal5").attr('checked',true);
        $("#metal6").attr('checked',true);
        $("#metal7").attr('checked',true);
        $("#sector4checkbox").attr('checked',true);
        $("#mineral1").attr('checked',true);
        $("#mineral2").attr('checked',true);
        $("#mineral3").attr('checked',true);
        $("#mineral4").attr('checked',true);
        $("#mineral5").attr('checked',true);
        $("#sector5checkbox").attr('checked',true);
        $("#sector6checkbox").attr('checked',true);
        $("#pulp1").attr('checked',true);
        $("#pulp2").attr('checked',true);
        $("#sector7checkbox").attr('checked',true);
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
        $("#sector8checkbox").attr('checked',true);
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
        $("#sector9checkbox").attr('checked',true);
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
    } else if (type == 'O') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
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
        $("#sector8checkbox").attr('checked',false);
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
        $("#sector9checkbox").attr('checked',true);
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
    } else if (type == 'L') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
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
        $("#sector8checkbox").attr('checked',false);
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
        $("#sector9checkbox").attr('checked',true);
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
    } else if (type == 'I') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
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
        $("#sector8checkbox").attr('checked',false);
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
        $("#sector9checkbox").attr('checked',false);
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
    } else if (type == 'F') {
        $("#sector1checkbox").attr('checked',false);
        $("#sector2checkbox").attr('checked',false);
        $("#waste1").attr('checked',false);
        $("#waste2").attr('checked',false);
        $("#waste3").attr('checked',false);
        $("#waste4").attr('checked',false);
        $("#sector3checkbox").attr('checked',false);
        $("#metal1").attr('checked',false);
        $("#metal2").attr('checked',false);
        $("#metal3").attr('checked',false);
        $("#metal4").attr('checked',false);
        $("#metal5").attr('checked',false);
        $("#metal6").attr('checked',false);
        $("#metal7").attr('checked',false);
        $("#sector4checkbox").attr('checked',false);
        $("#mineral1").attr('checked',false);
        $("#mineral2").attr('checked',false);
        $("#mineral3").attr('checked',false);
        $("#mineral4").attr('checked',false);
        $("#mineral5").attr('checked',false);
        $("#sector5checkbox").attr('checked',false);
        $("#sector6checkbox").attr('checked',false);
        $("#pulp1").attr('checked',false);
        $("#pulp2").attr('checked',false);
        $("#sector7checkbox").attr('checked',false);
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
        $("#sector8checkbox").attr('checked',true);
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
        $("#sector9checkbox").attr('checked',false);
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
    } else if (type == 'P') {
        $("#sector1checkbox").attr('checked',true);
        $("#sector2checkbox").attr('checked',true);
        $("#waste1").attr('checked',true);
        $("#waste2").attr('checked',true);
        $("#waste3").attr('checked',true);
        $("#waste4").attr('checked',true);
        $("#sector3checkbox").attr('checked',true);
        $("#metal1").attr('checked',true);
        $("#metal2").attr('checked',true);
        $("#metal3").attr('checked',true);
        $("#metal4").attr('checked',true);
        $("#metal5").attr('checked',true);
        $("#metal6").attr('checked',true);
        $("#metal7").attr('checked',true);
        $("#sector4checkbox").attr('checked',true);
        $("#mineral1").attr('checked',true);
        $("#mineral2").attr('checked',true);
        $("#mineral3").attr('checked',true);
        $("#mineral4").attr('checked',true);
        $("#mineral5").attr('checked',true);
        $("#sector5checkbox").attr('checked',true);
        $("#sector6checkbox").attr('checked',true);
        $("#pulp1").attr('checked',true);
        $("#pulp2").attr('checked',true);
        $("#sector7checkbox").attr('checked',true);
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
        $("#sector8checkbox").attr('checked',true);
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
        $("#sector9checkbox").attr('checked',true);
        $("#petroleum1").attr('checked',true);
        $("#petroleum2").attr('checked',false);
        $("#petroleum3").attr('checked',true);
        $("#petroleum4").attr('checked',true);
        $("#petroleum5").attr('checked',false);
        $("#petroleum6").attr('checked',true);
        $("#petroleum7").attr('checked',true);
        $("#petroleum8").attr('checked',true);
        $("#petroleum9").attr('checked',true);
        $("#petroleum10").attr('checked',true);
        $("#petroleum11").attr('checked',true);
    }
    dataSource = type;
}
function switchDataSource() {
    closeWelcomeWindow();
    if (dataSource == 'E') {
        dataSource = 'S';
        if (welcomeSupplierScreenShow == false) {
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
            welcomeSupplierScreenShow = true;
        }
    } else {
        dataSource = 'E';
    }
    setURLPath();
}
function ajaxLoading() {
    $('#canvas-vis').html('<div style="text-align:center"><img src="img/loadingBig.gif"></div>');
}
function setInjectionSelection() {
    if (injectionSelection == 11) {
        $("#injectionLabel").html('All CO<sub>2</sub> Injectors');
    } else if (injectionSelection) {
        $("#injectionLabel").html('CO<sub>2</sub> Injectors with an R&amp;D Exemption');
    }
}
function injectionSelected(option) {
    injectionSelection = option;
    hasTrend(injectionSelection, true);
    generateURL('');
}
function supplierSectorSelected(sector) {
    if (sector != 0) {
        closeWelcomeWindow();
        supplierSector = sector;
        hasTrend(supplierSector, true);
        generateURL('');
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
    if($("#lastState2").val() == stateAbbr){
        return false;
    } else {
        return true;
    }
}

function stateLevelChange(stateLevel) {
    if($("#lastStateLevel").val() == stateLevel) {
        return false;
    } else {
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

function setStateLevel(ryear, stateAbbr, level, draw) {

    var facOrLoc = FacOrLoc.get();
    var lowE = LowE.get();
    var highE = HighE.get();
    var stateAbbr = State.get();

    if((level == 0 && stateChange(stateAbbr)) || (level == 0 && stateLevelChange(level))) {
        jQuery.getJSON("service/getCountiesFromState/"+ryear+"?st="+stateAbbr, function(data) {
            htmlStr = "";
            htmlStr += "<option value =''>Choose County</option>";
            for(var i = 0; i < data.length; i++){
                htmlStr += "<option value ='"+data[i].id+"'>"+data[i].name+"</option>";
            }
            $("#countyState").html(htmlStr);
            if (stateAbbr == "TL") {
                $("#countySelection").attr('style', 'float:left; display:none');
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
            Map.draw();
            //drawMap(facOrLoc,State.Helper.abbreviationToState(stateAbbr),"","", lowE, highE, reportingStatus,"")
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
            Map.draw();
            //drawMap(facOrLoc,State.getFullName(),"","", lowE, highE, reportingStatus,"")
        }
    }
}


function openInfoWindow(event) {
    if(State.Helper.isState(this.get("countyNum"))){
        $("#parentState").val(State.Helper.stateToAbbreviation(this.get("countyNum")));
        generateURL('');
    } else {
        $("#countyState").val(addLeadingZeros(this.get("countyNum")));
        var fipsCode = addLeadingZeros(this.get("countyNum"));
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

    if (reportingStatus=='ALL') {

        returnVal= '<p>Facilities are color coded to designate their verification and reporting status as of ' + dataDate + ' for reporting year '+ cyear +'.'
        +'<br/><br/><span class="orangeText">Orange</span> indicates the facility has not met EPA&apos;s verification requirements by ' + dataDate + '. '
        +'<a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139281" target="_blank">Click here </a> to learn more about verification.'
        +'</p><br/><p><span class="redText">Red</span> indicates a facility has discontinued reporting without a valid reason as of ' + dataDate + '.'
        +'<br/><br/><span class="greyText">Grey</span> indicates a facility has discontinued reporting with a <a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139271" target="_blank"> valid reason.</a>'
        +'<br/><br/><span class="blackText">Black</span> indicates a facility has submitted a ' + cyear + ' report and has met EPA&apos;s verification requirements.'
        ;

    }

    else if (reportingStatus=='ORANGE') {
        returnVal =
            '<p>Facility has not met EPA&apos;s verification requirements by ' + dataDate + '. '
            + '<a href="http://www.ccdsupport.com/confluence/pages/viewpage.action?pageId=243139281" target="_blank">'
            + 'Click here to learn more'
            + '</a></p>';
    }

    else if (reportingStatus == 'RED') {
        returnVal =
            '<p>Facility discontinued reporting without a valid reason as of ' + dataDate + '.</p>'
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
    $("#mask").hide();
    $("#welcomeWindow").hide();
    $("#parentState").val($("#welcomeState").val());
    generateURL('facility');
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
function isOnshoreOnly() {
    if ($("#sector1checkbox").attr('checked') ||
        $("#sector2checkbox").attr('checked') ||
        $("#sector3checkbox").attr('checked') ||
        $("#sector4checkbox").attr('checked') ||
        $("#sector5checkbox").attr('checked') ||
        $("#sector6checkbox").attr('checked') ||
        $("#sector7checkbox").attr('checked') ||
        $("#sector8checkbox").attr('checked') ||
        $("#petroleum1").attr('checked') ||
        $("#petroleum3").attr('checked') ||
        $("#petroleum4").attr('checked') ||
        $("#petroleum5").attr('checked') ||
        $("#petroleum6").attr('checked') ||
        $("#petroleum7").attr('checked') ||
        $("#petroleum8").attr('checked') ||
        $("#petroleum9").attr('checked') ||
        $("#petroleum10").attr('checked') ||
        $("#petroleum11").attr('checked')) {
        return false;
    } else if ($("#petroleum2").attr('checked')) {
        return true;
    } else {
        return false;
    }
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
