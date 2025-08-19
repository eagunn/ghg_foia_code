/**
 *
 * Created by alabdullahwi on 6/1/2015.
 */


var County = new Parameter( {uiContainer: "#countySelectAndSearch" , ui : "#countyState" } )
County._name = '';

County.getName = function()  {
   return County._name;
}
County.setName = function(name) {
   County._name = name;
}
County.wipe = function() {
    $(County._ui).val('');
}


//@Override
County.get = function() {
  var retv = $(County._ui).val();
  return addLeadingZeros(retv);
}

//@Override
County.set = function(_val) {
     if (_val === undefined || _val === null) {
         return;
     }
    MSA.wipe();
    $(County._ui).val(_val);
    $(County._name).val($(County._ui + " option:selected").text());
};

//@Override
County.show = function() {
    var _this = County;
    $(_this._ui).attr('style', 'float:left; display:inline');
    $(_this._uiContainer).show();
    if (DataSource.get() === 'L' ){
      County.disable();
    }
    UI.hideSelectors([State,TribalLand]);
};
County.populateUI = function(counties) {
    County.disable();
    var htmlStr = "";
    htmlStr += "<option value =''>Choose County</option>";
    for(var i = 0; i < counties.length; i++){
        htmlStr += "<option value ='"+counties[i].id+"'>"+counties[i].name+"</option>";
    }
    $(County._ui).html(htmlStr);
    County.set('');
    County.enable();
    County.show();
};



