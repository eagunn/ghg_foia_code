/**
 *
 *
 * Created by alabdullahwi on 5/31/2015.
 */



var Parameters = (function() {

    // var params = [State,LowE,HighE,FacOrLoc,EmissionsType,TribalLand, County, MSA ];

    //in progress
    return {
        //unused
//         generateFlightRequest: function() {
//                 var retv = {
//                     "trend" : Trend.get(),
//                     "dataSource": dataSource.get(),
//                     "reportingYear" : ReportingYear.get(),
//                     "currentYear" : App.currentYear,
//                     "query": FacOrLoc.get(),
//                     "lowE": LowE.get(),
//                     "highE": HighE.get(),
//                     "state" : State.get(),
//                     "countyFips" : County.get(),
//                     "msaCode" : MSA.get(),
//                     "stateLevel" : App.countyOrMSA(),
// /*                    "basin" : Basin.getFilter(),
//                     "gases"  : Gases.getFilter(),
//                     "sectors" : Sectors.getFilter(),
//                     "supplierSector" : SupplierSector.get(),
//                     "reportingStatus" : ReportingStatus.get(),
//                     "searchOptions" : SearchOptions.get(),
//                     "injectionSelection" : InjectionSelection.get(),*/
//                     "emissionsType" : EmissionsType.get(),
//                     "tribalLandId" : TribalLand.get()
//                 }
//                 return JSON.stringify(retv);
//         },
        buildUrl: function(params) {
            var retv = "?";
            for (var i = 0 ; i < params.length ; i++) {
                retv += "&"+ params[i].toUrl();
            }
            return retv;
        },
        unpackUrl: function(event) {
            for (var i = 0 ; i < params.length; i++) {
              var _param = params[i];
               _param.set(event.parameters[_param._url]);
            }
        },
        sync : function(obj) {
            if (obj.dirtyCheck()) {
                obj.resync();
                return true;
            }
            return false;
        }
        // ,getTribalLand : function () { return TribalLand.get(); }
        // ,setTribalLand: function(_tribalLand) { TribalLand.set(_tribalLand); }

        //curretly used in the app
        ,isAnyEmitterTypeSelected : function(dataSource) {
            var emitterDatatypes = ['E', 'L', 'F', 'P', 'B', 'T'];
            return emitterDatatypes.indexOf(dataSource) != -1;
        }
    }

}());
