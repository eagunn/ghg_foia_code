/**
 *
 * Created by alabdullahwi on 6/1/2015.
 */

var DataSource = new Parameter({uiContainer:'#dataTypeSelection', ui: '#dataType', url: 'ds'});
DataSource.registerListeners = function() {

    $(DataSource._ui).change(function(){
        if (DataSource.is('S')) {
            UI.hideSelectors([State,County,TribalLand,MSA]);
        }
        if (DataSource.is('E')) {
           UI.showSelectors([State])
        }


    });

}




