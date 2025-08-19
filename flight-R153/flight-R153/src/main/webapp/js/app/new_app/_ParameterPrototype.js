/**
 * Created by alabdullahwi on 6/2/2015.
 */
//base object for all params
function Parameter(obj) {

    var _this = this;
    this._url = obj.url;
    this._uiContainer = obj.uiContainer;
    this._ui= obj.ui;
    this._lastValue = "";
    this.get = function () { return $(_this._ui).val(); };
    this.dirtyCheck = function() { return _this.get() !== _this._lastValue;};
    this.resync = function() {_this._lastValue = _this.get();  };
    this.set= function (_val) { if (_val === undefined || _val === null) {return;}  $(_this._ui).val(_val); };
    this.show = function() { return $(_this._uiContainer).show(); };
    this.hide = function() { return $(_this._uiContainer).hide(); };
    this.is = function(something) {
       return _this.get() === something;
    };
    this.isNot = function(something) {
       return !_this.is(something);
    };
    this.isOneOf = function(stuff){
        return (stuff.indexOf(_this.get()) > -1);
    };
    this.isNotOneOf = function(stuff) {
       return !(stuff.indexOf(_this.get()) > -1) ;
    };
    this.enable = function() {
        $(_this._ui).removeAttr('disabled');
    };
    this.disable = function() {
        $(_this._ui).attr('disabled','disabled');
    };
    this.remove = function() { _this.hide(); _this.set('');    };
    this.toUrl = function() { return _this._url + "=" + _this.get(); };
    this.registerListeners = function() {
        console.log("ERROR: You have not overridden me!");
    }
}

function InvisibleParameter(obj) {

    var retv = new Parameter({ui: null, uiContainer: null, url: obj.url});
    retv._val = '';
    retv.get = function() { return retv._val; }
    retv.set = function(val) { retv._val = val; }
    retv.show = undefined;
    retv.hide = undefined;
    retv.enable = undefined;
    retv.disable = undefined;
    retv.remove = undefined;
    return retv;
}

