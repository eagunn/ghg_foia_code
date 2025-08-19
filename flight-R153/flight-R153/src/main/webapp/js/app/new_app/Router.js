/**
 *
 * Created by alabdullahwi on 6/1/2015.
 */

/**
 * basic routing functionality for FLIGHT
 */

var Router = (function() {

   var resolveParams = {
      MapView : function() { return []; },
      ListView: function() { return [UI.selector, DataSource.get()]; },
      LineView: function() { return [ReportingStatus.get(), DataSource.get()];},
      BarView: function()  { return [ReportingStatus.get(),DataSource.get(), UI.selector];},
      PieView: function()  { return [ReportingStatus.get(),DataSource.get(), UI.selector];},
      TreeView: function() { return [DataSource.get(), UI.selector]; }
   }

    var Routes = {

        //if heatmap is back, need to account for mapSelector (0 for map view return 'facility', otherwise return 'intensity'
        MapView : 'facility',
        ListView: {
            selectors : {
                Geography : {
                    dataSource : {
                        SF6             : 'listFacility',
                        CO2             : 'listFacility',
                        Onshore         : 'listFacilityForBasinGeo',
                        OTHERWISE       : 'listSector'
                    }
                },
                //I think this is the setSelector('1') but I don't see how it's used anywhere
                Gas:  'listGas',
                Facility : {
                    dataSource : {
                        Emitter         : 'listFacility',
                        LDC             : 'listFacility',
                        SF6             : 'listFacility',
                        Point           : 'listFacility',
                        CO2             : 'listFacility',

                        Onshore         : 'listFacilityForBasin',

                        OTHERWISE       : 'listSector'
                    }
                }
            }
        },
        LineView : {
            ReportingStatus : {
                All: {
                    dataSource : {

                        Emitter : 'trend',
                        Onshore : 'trend',
                        LDC     : 'trend',
                        SF6     : 'trend',
                        Point   : 'trend',

                        Supplier: 'hasTrend',
                        CO2     : 'hasTrend'
                    }
                },
                OTHERWISE: 'switchToMapViewAndRouteFacility'
            }
        },
        BarView : {
            ReportingStatus: {

                Red: 'switchToMapViewAndRouteFacility',
                Gray: 'switchToMapViewAndRouteFacility',

                OTHERWISE: {
                    dataSource: {
                        Supplier: 'barSupplier',
                        CO2: 'switchToMapViewAndRouteFacility',
                        OTHERWISE: {
                            selector: {
                                0: 'barSector',
                                1: 'barGas',
                                2: 'barState'
                            }//selector
                        }//other Emitter Types
                    }//dataSource
                }//other ReportingStatuses
            }
        },
        PieView : {
            ReportingStatus : {
                Red : 'switchToMapViewAndRouteFacility',
                Gray: 'switchToMapViewAndRouteFacility',

                OTHERWISE: {
                    dataSource: {
                        Supplier: 'pieSupplier',
                        CO2: 'switchToMapViewAndRouteFacility',
                        OTHERWISE :{
                            selector : {
                                0: 'pieSector',
                                1: 'pieGas',
                                2: 'pieState'
                            }
                        }
                    }
                }
            }
        },
        TreeView : {
            dataSource: {
                Supplier: 'treeSupplier',

                OTHERWISE: {
                    selector : {
                        0: 'treeSector',
                        1: 'treeGas',
                        2: 'treeState'
                    }
                }
            }
        }

    };

   return {

      route : function(currentView) {
         var _params = resolveParams[currentView];
         var route = Routes[currentView];
         for (var i=0 ; i<_params.length; i++) {
           route = Routes[_params[i]];
           if (route === undefined) {
              route = route['OTHERWISE'];
           }
           else if (route instanceof object ){
              break;
           }
         }
         return route;
        }
   }


}());



