/**
 * Created by alabdullahwi on 6/29/2016.
 */
requirejs.config({
    
    baseUrl: 'js/lib'
    ,paths : {
        app: 'js/app'
        , jquery: '//ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min'
        , jqueryUI: '//ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min'
        , jqueryAddress: 'jquery.address-1.4.min'
        , jqueryDrag: 'jquery.event.drag-2.2'
        , googleJsApi: '//www.google.com/jsapi'
        , highcharts: '//code.highcharts.com/highcharts.src'
        , highchartsExporting: '//code.highcharts.com/modules/exporting'
        , ddSlick: 'jquery.ddslick.min'
        , bootstrap: '//netdna.bootstrapcdn.com/bootstrap/2.3.2/js/bootstrap.min.js'
    }
});

requirejs(['app/main2'])
