/**
 *
 *
 * Created by alabdullahwi on 6/2/2015.
 */


var FacOrLoc = new Parameter({ ui:'#facOrLocInput' , uiContainer: '#facSearchFunction', url: 'q' } );
FacOrLoc.set = function(_val) {
   if (_val === undefined) { return; }
   $(FacOrLoc._ui).val(decodeURIComponent(_val));
}
FacOrLoc.get = function() {
    var _val = $(FacOrLoc._ui).val();
    if (_val === 'Find a Facility or Location' ) {
       return "";
    }
    return encodeURIComponent(_val);
}