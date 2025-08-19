/**
 * Created by alabdullahwi on 5/26/2015.
 */
var State = new Parameter({ ui : '#parentState', uiContainer: '#stateSelectAndSearch', url: 'st'});
State.registerListeners = function() {
    var toggle = 'Substate Toggle';
    $(State._ui).change(function () {
        //clear out tribal dropdown value if state changes
        TribalLand.set("");
        var $this = $(this);
        //Tribal Land picked in State dropdown
        if ($this.val() === 'TL') {
            TribalLand.show();
            County.remove();
            MSA.remove();
            UI.hide('Substate Toggle');
        }
        else {
            TribalLand.hide();
            if ($this.val() in ['','AS', 'MP','GU' ]){
                County.remove();
                MSA.remove();
            }
            else {
                County.disable();
                //this populates County with the new state values
                var callback = function() {
                    County.enable();
                    County.set('');
                    County.show();
                }
                Backend.Dropdowns.loadCountyNames(callback);
            }
        }
    });

}
State.show = function() {
    var _this = State;
    $(_this._ui).attr('style', 'float:left; display:inline');
    $(_this._uiContainer).show();
};
State.disable = function() {
 $(State._ui).attr('disabled',true);
};

State.Helper = (function() {

    var stateMap =
        [
            [1,  "AL", "Alabama"]    ,
            [2,  "AK", "Alaska"]    ,
            [4,  "AZ", "Arizona"]    ,
            [5,  "AR", "Arkansas"]    ,
            [6,  "CA", "California"]    ,
            [8,  "CO", "Colorado"]    ,
            [9,  "CT", "Connecticut"]    ,
            [10, "DE", "Delaware"]    ,
            [11, "DC", "District of Columbia"]    ,
            [12, "FL", "Florida"]    ,
            [13, "GA", "Georgia"]    ,
            [15, "HI", "Hawaii"]    ,
            [16, "ID", "Idaho"]    ,
            [17, "IL", "Illinois"]    ,
            [18, "IN", "Indiana"]    ,
            [19, "IA", "Iowa"]    ,
            [20, "KS", "Kansas"]    ,
            [21, "KY", "Kentucky"]    ,
            [22, "LA", "Louisiana"]    ,
            [23, "ME", "Maine"]    ,
            [24, "MD", "Maryland"]    ,
            [25, "MA", "Massachusetts"]    ,
            [26, "MI", "Michigan"]    ,
            [27, "MN", "Minnesota"]    ,
            [28, "MS", "Mississippi"]    ,
            [29, "MO", "Missouri"]    ,
            [30, "MT", "Montana"]    ,
            [31, "NE", "New England"]    ,
            [32, "NV", "Nevada"]    ,
            [33, "NH", "New Hampshire"]    ,
            [34, "NJ", "New Jersey"]    ,
            [35, "NM", "New Mexico"]    ,
            [36, "NY", "New York"]    ,
            [37, "NC", "North Carolina"]    ,
            [38, "ND", "North Dakota"]    ,
            [39, "OH", "Ohio"]    ,
            [40, "OK", "Oklahoma"]    ,
            [41, "OR", "Oregon"]    ,
            [42, "PA", "Pennsylvania"]    ,
            [44, "RI", "Rhode Island"]    ,
            [45, "SC", "South Carolina"]    ,
            [46, "SD", "South Dakota"]    ,
            [47, "TN", "Tennessee"]    ,
            [48, "TX", "Texas"]    ,
            [49, "UT", "Utah"]    ,
            [50, "VT", "Vermont"]    ,
            [51, "VA", "Virginia"]    ,
            [53, "WA", "Washington"]    ,
            [54, "WV", "West Virginia"]    ,
            [55, "WI", "Wisconsin"]    ,
            [56, "WY", "Wyoming"]    ,
            [60, "AS", "American Samoa"]    ,
            [66, "GU", "Guam"]    ,
            [69, "MP", "Northern Mariana Islands"]    ,
            [72, "PR", "Puerto Rico"]         ,
            [78, "VI", "Virgin Islands"],
            [80, "TL", "Tribal Land"]
        ];

    var _paramChecker = (function() {
        return {
            isCode : function(arg) {
                return (typeof(arg) === 'number' && arg > 0 && arg < 81 );
            },
            isAbbr : function(arg) {
                return (typeof(arg) === 'string' && arg.length == 2);
            },
            isFullName: function(arg) {
               return (typeof(arg) === 'string' && arg.length > 2);
            }
        }
    }());

    return {

         findCode: function(state) {
             //which tuple element to look at (0: code, 1:abbr, 2: full name)
             var index = -1;
             if (state == '' || state == 'US' || state == 'U.S. Mainland'){
                return 0;
             }
             if (_paramChecker.isCode(state)) {
                 return state;
             }
             else if (_paramChecker.isFullName(state)) {
                 index = 2;
             }
             else if (_paramChecker.isAbbr(state)) {
                 index = 1;
             }
             for (var i = 0 ; i < stateMap.length ; i++ ){
                 var tuple = stateMap[i];
                if (tuple[index] === state ) {
                   return tuple[0];
                }
             }
             return 0;
         },
         findFullName: function(state) {
             var index = -1;
             if (state == '' || state == 'US') {
                return '';
             }
             if (_paramChecker.isFullName(state)) {
                 return state;
             }
             else if (_paramChecker.isCode(state)) {
                 index = 0;
             }
             else if (_paramChecker.isAbbr(state)) {
                 index = 1;
             }
             for (var i = 0 ; i < stateMap.length ; i++) {
                 var tuple = stateMap[i];
                 if (tuple[index] === state) {
                     return tuple[2];
                 }
             }
             return "";
         },
         findAbbreviation : function(state) {
             var index = -1;
             if (state == '') {
                return 'US';
             }
             if (_paramChecker.isAbbr(state)) {
                 return state;
             }
             else if (_paramChecker.isCode(state)) {
                 index = 0;
             }
             else if (_paramChecker.isFullName(state)) {
                 index = 2;
             }
             for (var i = 0; i < stateMap.length ; i++ ) {
                var tuple = stateMap[i];
                if (tuple[index] === state) {
                     return tuple[1];
                 }

             }
             //default fallback
             return "US";

         },

         isState: function(str) {
             for (var i = 0 ; i < stateMap.length ; i++) {
                var tuple = stateMap[i];
                if (tuple[2] === str) {
                   return tuple[0];
                }

             }
             return 0;
          },
         stateToAbbreviation: function(state) {
             for (var i = 0; i < stateMap.length; i++) {
                 var tuple = stateMap[i];
                 if (tuple[2] === state) {
                     return tuple[1];
                    }
                 }
             return '';
                },
         abbreviationToState: function(abv) {
             for (var i = 0; i < stateMap.length; i++) {
                 var tuple = stateMap[i];
                        if ( tuple[1] === abv )  {
                            return tuple[2] ;
                        }
                }
                return "";
           }
    }

}());

State.getCode = function() {
   return State.Helper.findCode(State.get());
};
State.getFullName = function() {
    return State.Helper.findFullName(State.get());
};
State.getAbbr = function() {
    return State.get();
};
State.isNationalLevel = function() {
    return State.get() == '' || State.get() == 'US' ;
}
State.isStateLevel = function() {
    return !State.isNationalLevel();
}




//does not appear to be used anywhere
/*function setStateInfo(str){
    var obj = [];
    if (str == "Alabama"){ //OK
        obj.push(32.7990);
        obj.push(-86.8073);
        obj.push(6);
    } else if(str == "Alaska"){ //OK
        obj.push(64.4728);
        obj.push(-152.2683);
        obj.push(3);
    } else if(str == "Arizona"){ // OK
        obj.push(34.2527);
        obj.push(-111.3877);
        obj.push(6);
    } else if(str == "Arkansas"){ //OK
        obj.push(34.7958);
        obj.push(-92.3809);
        obj.push(7);
    } else if(str == "California"){ //OK
        obj.push(37.1700);
        obj.push(-119.7462);
        obj.push(5);
    } else if(str == "Colorado"){ //OK
        obj.push(39.0646);
        obj.push(-105.3272);
        obj.push(6);
    } else if(str == "Connecticut"){ //OK
        obj.push(41.5250);
        obj.push(-72.7622);
        obj.push(8);
    } else if(str == "Delaware"){ //OK
        obj.push(39.1441);
        obj.push(-75.5148);
        obj.push(8);
    } else if(str == "District Of Columbia"){ //OK
        obj.push(38.8964);
        obj.push(-77.0262);
        obj.push(11);
    } else if(str == "Florida"){ //OK
        obj.push(27.8333);
        obj.push(-83.4521);
        obj.push(6);
    } else if(str == "Georgia"){ //OK
        obj.push(32.9866);
        obj.push(-83.6487);
        obj.push(6);
    } else if(str == "Hawaii"){ //OK
        obj.push(22.1098);
        obj.push(-167.5311);
        obj.push(5);
    } else if(str == "Idaho"){ //OK
        obj.push(45.2394);
        obj.push(-114.5103);
        obj.push(5);
    } else if(str == "Illinois"){ //OK
        obj.push(39.9771);
        obj.push(-89.0022);
        obj.push(6);
    } else if(str == "Indiana"){ //OK
        obj.push(39.8647);
        obj.push(-86.2604);
        obj.push(6);
    } else if(str == "Iowa"){ //OK
        obj.push(42.0046);
        obj.push(-93.7140);
        obj.push(7);
    } else if(str == "Kansas"){ //OK
        obj.push(38.5111);
        obj.push(-98.6005);
        obj.push(7);
    } else if(str == "Kentucky"){ //OK
        obj.push(37.6690);
        obj.push(-86.1514);
        obj.push(7);
    } else if(str == "Louisiana"){ //OK
        obj.push(31.1801);
        obj.push(-91.8749);
        obj.push(6);
    } else if(str == "Maine"){ //OK
        obj.push(45.2074);
        obj.push(-69.3977);
        obj.push(6);
    } else if(str == "Maryland"){ //OK
        obj.push(38.7724);
        obj.push(-76.9902);
        obj.push(7);
    } else if(str == "Massachusetts"){ //OK
        obj.push(42.0373);
        obj.push(-71.5314);
        obj.push(7);
    } else if(str == "Michigan"){
        obj.push(45.0504);
        obj.push(-84.5603);
        obj.push(6);
    } else if(str == "Minnesota"){ //OK
        obj.push(46.4326);
        obj.push(-93.9196);
        obj.push(6);
    } else if(str == "Mississippi"){
        obj.push(40.7673);
        obj.push(-89.6812);
        obj.push(6);
    } else if(str == "Missouri"){ //OK
        obj.push(38.4623);
        obj.push(-92.8020);
        obj.push(6);
    } else if(str == "Montana"){ //OK
        obj.push(46.7326);
        obj.push(-110.5196);
        obj.push(6);
    } else if(str == "Nebraska"){ //OK
        obj.push(41.4289);
        obj.push(-100.3883);
        obj.push(6);
    } else if(str == "Nevada"){ //OK
        obj.push(38.6199);
        obj.push(-117.1219);
        obj.push(6);
    } else if(str == "New Hampshire"){ //OK
        obj.push(43.9108);
        obj.push(-71.5653);
        obj.push(7);
    } else if(str == "New Jersey"){ //OK
        obj.push(40.1140);
        obj.push(-74.5089);
        obj.push(7);
    } else if(str == "New Mexico"){ //OK
        obj.push(34.4375);
        obj.push(-106.2371);
        obj.push(6);
    } else if(str == "New York"){ //OK
        obj.push(42.9497);
        obj.push(-75.9384);
        obj.push(6);
    } else if(str == "North Carolina"){ //OK
        obj.push(35.4411);
        obj.push(-79.8431);
        obj.push(6);
    } else if(str == "North Dakota"){ //OK
        obj.push(47.5362);
        obj.push(-100.2930);
        obj.push(6);
    } else if(str == "Ohio"){ //OK
        obj.push(40.3736);
        obj.push(-82.7755);
        obj.push(6);
    } else if(str == "Oklahoma"){ //OK
        obj.push(35.5376);
        obj.push(-97.9247);
        obj.push(6);
    } else if(str == "Oregon"){ //OK
        obj.push(43.8978);
        obj.push(-120.6519);
        obj.push(6);
    } else if(str == "Pennsylvania"){ //OK
        obj.push(40.9773);
        obj.push(-77.9640);
        obj.push(7);
    } else if(str == "Rhode Island"){ //OK
        obj.push(41.6772);
        obj.push(-71.5101);
        obj.push(8);
    } else if(str == "South Carolina"){ //OK
        obj.push(33.6191);
        obj.push(-80.9066);
        obj.push(7);
    } else if(str == "South Dakota"){ //OK
        obj.push(44.2853);
        obj.push(-100.4632);
        obj.push(6);
    } else if(str == "Tennessee"){ //OK
        obj.push(35.7449);
        obj.push(-86.7489);
        obj.push(6);
    } else if(str == "Texas"){ //OK
        obj.push(31.1060);
        obj.push(-99.6475);
        obj.push(5);
    } else if(str == "Utah"){ //OK
        obj.push(39.5135);
        obj.push(-111.8535);
        obj.push(6);
    } else if(str == "Vermont"){ //OK
        obj.push(43.8407);
        obj.push(-72.7093);
        obj.push(7);
    } else if(str == "Virginia"){ //OK
        obj.push(37.9680);
        obj.push(-79.5057);
        obj.push(7);
    } else if(str == "Washington"){ //OK
        obj.push(47.3537);
        obj.push(-120.4101);
        obj.push(6);
    } else if(str == "West Virginia"){ //OK
        obj.push(38.8680);
        obj.push(-80.9696);
        obj.push(7);
    } else if(str == "Wisconsin"){ //OK
        obj.push(44.7563);
        obj.push(-89.6385);
        obj.push(6);
    } else if(str == "Wyoming"){ //OK
        obj.push(42.9475);
        obj.push(-107.2085);
        obj.push(6);
    } else if(str == "American Samoa"){
        obj.push(-14.2417);
        obj.push(-170.0397);
        obj.push(8);
    } else if(str == "Northern Mariana Islands"){
        obj.push(17.08058);
        obj.push(145.5505);
        obj.push(6);
    } else if(str == "Guam"){
        obj.push(13.4658);
        obj.push(144.7505);
        obj.push(10);
    } else if(str == "Puerto Rico"){
        obj.push(18.2766);
        obj.push(-66.3350);
        obj.push(8);
    } else if(str == "Virgin Islands"){
        obj.push(18.0001);
        obj.push(-64.8199);
        obj.push(9);
    } else if(str == "Tribal Land"){
        obj.push(39);
        obj.push(-97);
        obj.push(4);
    }
    return obj;
}*/
