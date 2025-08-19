/**
 *
 * Created by alabdullahwi on 6/2/2015.
 */

// var MSA = new Parameter({ui:'#msaState',uiContainer:'#msaSelectAndSearch', url: 'mc'});
//temporary
var MSA = new Parameter( {uiContainer: "#countySelectAndSearch" , ui : "#countyState", url: 'mc' } )
MSA.wipe = function() {
   $(MSA._ui).val('');
}
MSA.set = function(_val) {
    if (_val === undefined || _val === null) {return;}
    $(MSA._ui).val(_val);
    County.wipe();
}
MSA.disable = function() {
    $(MSA._ui).attr('disabled','disabled');
};
MSA.enable = function() {
    $(MSA._ui).attr('disabled', false);
}
MSA.show = function() {
    var _this = MSA;
    $(_this._ui).attr('style', 'float:left; display:inline');
    $(_this._uiContainer).show();
    if (DataSource.get() === 'L' ){
        MSA.disable();
    }
    UI.hideSelectors([County,TribalLand]);
};
MSA.populateUI = function(msas) {
    var htmlStr = "";
    htmlStr += "<option value =''>Choose Metro Area</option>";
    for(var i = 0; i < msas.length; i++){
        htmlStr += "<option value ='"+msas[i].id+"'>"+msas[i].name+"</option>";
    }
    $(MSA._ui).html(htmlStr);
    MSA.set('');
};
