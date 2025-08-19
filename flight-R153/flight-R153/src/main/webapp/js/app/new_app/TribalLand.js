/**
 *
 *
 *
 * Created by alabdullahwi on 5/29/2015.
 */
var TribalLand = new Parameter({uiContainer: "#tribeSelectAndSearch", ui: "#tribe"});

//@Override
TribalLand.registerListeners = function() {
    //this listener shows the tribe selection dropdown only when "Tribal Land" is picked from the State Dropdown, hiding it otherwise and clearing the value of the global variable
    $(TribalLand._ui).change(function () {
        TribalLand.set($(this).val());
    });
};

TribalLand.show = function() {
    var _this = TribalLand;
    $(_this._ui).attr('style', 'float:left; display:inline');
    $(_this._uiContainer).show();
    UI.hideSelectors([County,State]);
};







