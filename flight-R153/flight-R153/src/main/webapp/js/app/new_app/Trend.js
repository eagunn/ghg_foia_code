/**
 * Created by alabdullahwi on 6/4/2015.
 */

    //in progress
var Trend = new Parameter({ui:null, uiContainer:null});
//default value
Trend.set('current');

//@Override
Trend.get = function() {
    return trendSelection;
}

