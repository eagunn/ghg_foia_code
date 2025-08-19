/**
 *
 * Created by alabdullahwi on 6/4/2015.
 */

var App = (function() {

    return {
        currentYear: ryear,
        countyOrMSA : function() {
           if (County.get() == '' && MSA.get() != '' ) {
                return 0;
           }
           else if (County.get() != '' && MSA.get() == '' ) {
               return 1;
           }
           //state
           else {
               return -1;
           }
        }
        ,showEmissionsRangePopover : function(offset)  {
            $('#ghgPopover').hide();
            $('#searchOptionsPopover').hide();
            $('#whatsThisPopover').hide();
            $('#emissionRangePopover').toggle();
            $('#emissionRangePopover').focus();
            if (offset == 'offset' || overlayLevel == 1) {
                $('#emissionRangePopover').css('margin-left', '30%');
                $('#emissionRangePopover').css('margin-top', '2.5%');
            }
            else {
                $('#emissionRangePopover').css('margin-left', '0px');
                $('#emissionRangePopover').css('margin-top', '0px');
            }
        }
    }

}());
