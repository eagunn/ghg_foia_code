function initEmitterListView(data,title){
	
	var dataGoogle = new google.visualization.DataTable();
	dataGoogle.addColumn('string', title);
	
	//Set Table Headers
	
    for(var i in data.label){
    	//if (data.label[i] == 'CO<sub>2</sub>') {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + ")");
    	//} else {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + " CO<sub>2</sub>e)");
    	//}
    	dataGoogle.addColumn('number', data.label[i]);
    }
    
    var numRow = 0;
    var numCols = 0;
    
    for(var i in data.values){
    	if(isNaN(i)){
    		numRow = 1;
    		break;
    	}
    	numRow += 1;
    }
    
    for(var i in data.label){
    	numCols += 1;
    }
    dataGoogle.addRows(numRow);

	for(var i=0; i < numRow; i++){
		var obj = data.values[i];
		//dataGoogle.setCell(i, 0, obj.label);
		dataGoogle.setCell(i, 0, obj.label, obj.label, {style: 'font-size: 0.9em; font-weight: bold; background-color: LIGHTGRAY;'});
		for(var j=0; j<numCols; j++){
			//dataGoogle.setCell(i, j+1, obj.values[j]);
			dataGoogle.setCell(i, j+1, obj.emissions[j], obj.emissions[j], {style: 'font-size: 11px'});
		}
    }

	var table = new google.visualization.Table(document.getElementById('canvas-vis'));
    
	var formatter = new google.visualization.NumberFormat({groupingSymbol: ',', fractionDigits: 0});
	for(var j=1; j<=numCols; j++){
		formatter.format(dataGoogle, j);
	}

    table.draw(dataGoogle, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
    
    google.visualization.events.addListener(table, 'select', 
       function() {
       var selection = table.getSelection();
       for (var i = 0; i < selection.length; i++) {
          var item = selection[i];
          if (item.row != null) {
        	  if (dataGoogle.getColumnLabel(0) == 'State') {
        		  $('#parentState').val(stateToAbbreviation(dataGoogle.getValue(item.row, 0)));
        		  generateURL('');
        	  }
          }
       }
    });
}

function arrowFormatter(row, cell, value, m, item) {
	var x = parseInt(value.replace(/\,/g,''));
	if (isNaN(x) || x == 0) {
		return "<span class='list-diff'>"+value+"</span>";
	} else if (x < 0) {
		return "<span class='list-diff-down'>"+value+"</span>";
	} else {
		return "<span class='list-diff-up'>"+value+"</span>";
	}
}

function inlineChart(cell, row, item, m) {
	var values = [];
	if (item["total2010"]!=undefined) {
		var v2010 = parseInt(item["total2010"].replace(/\,/g,''));
		if (!isNaN(v2010)) {
			values.push(v2010);
		}
	}
	if (item["total2011"]!=undefined) {
		var v2011 = parseInt(item["total2011"].replace(/\,/g,''));
		if (!isNaN(v2011)) {
			values.push(v2011);
		}
	}
	if (item["total2012"]!=undefined) {
		var v2012 = parseInt(item["total2012"].replace(/\,/g,''));
		if (!isNaN(v2012)) {
			values.push(v2012);
		}
	}
	$(cell).empty().sparkline(values, {width: "100%"});
}

function initEmitterFacilityListView(data,title){
	//var list = new google.visualization.DataTable(data.data);
	//var table = new google.visualization.Table(document.getElementById('canvas-vis'));
	//table.draw(list, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
	/*if ("trend" == data.trend) {
		data.data.cols.push({
			id: "trend",
			name: "Trend",
			field: "trend",
			sortable: false,
			width: 40,
			rerenderOnResize: true,
			asyncPostRender: inlineChart
		});
	}*/
	var linkFormatter = function (row, cell, value, columnDef, dataContext) {
		var vVal = value.replace(/{(.*)}/, ''); //for datatype T or Onshore Gas Transmission Pipelines in viewMakerFactory.createPipeInstance
		var vFacid = vVal.slice(vVal.indexOf("[") + 1, -1);
		var vFacname = vVal.slice(0, vVal.indexOf("["));
		var vDs = getUrlVar()["ds"];    
	    var vParams = ryear + "?id=" + vFacid + "&ds=" + vDs + "&et=" + getUrlVar()["et"] + "&popup=true";
	    var vUrl = "service/facilityDetail/" + vParams;
	    if(getUrlVar()["ds"] == "T") {
	    	vUrl = "service/pipeDetail/" + vParams;
	    }
		return "<a class=facName href=javascript:void(); title='" + vVal + "' onClick=window.open('" + vUrl + "','_blank');>" + vFacname + "</a>";
	}
	var iconsFormatter = function (row, cell, value, columnDef, dataContext) {
		if( value != '') 
			value = value.replace("$!{dataDate}", dataDate).replace("$!{year}", cyear);
		return value;
	}
	jQuery.each(data.data.cols, function() {
		/*if (this.id.substring(0,4) == "diff") {
			this.formatter = arrowFormatter;
		}*/
		if (this.field == "facility") {
			this.formatter = linkFormatter;
		}
		if (this.field == "icons") {
			this.formatter = iconsFormatter;
		}
	});
	var dataView = new Slick.Data.DataView();
	dataView.setItems(data.data.rows);
	
	var grid = new Slick.Grid('#canvas-vis',
		dataView,
		data.data.cols,
		{
			forceSyncScrolling: true,
			syncColumnCellResize: true,
			enableColumnReorder: false,
			multiColumnSort: true,
			forceFitColumns: true
			//leaveSpaceForNewRows: true
		}
	);
	
	grid.registerPlugin(new Slick.AutoTooltips({enableForHeaderCells: true}));
	grid.render();
	
	grid.onSort.subscribe(function (e, args) {
		var sorter = function StringSorter(a, b) {
			if (a[args.sortCols[0].sortCol.field]!=undefined && b[args.sortCols[0].sortCol.field]!=undefined) {
				var x = a[args.sortCols[0].sortCol.field].toUpperCase(), y = b[args.sortCols[0].sortCol.field].toUpperCase();
				return (x > y?1:-1);
			}
			return 0;
		};
		if (args.sortCols[0].sortCol.type == 'number') {
			sorter = function NumericSorter(a, b) {
				var x = parseInt(a[args.sortCols[0].sortCol.field].replace(/\,/g,'')), y = parseInt(b[args.sortCols[0].sortCol.field].replace(/\,/g,''));
				if (isNaN(x))
					return -1;
				if (isNaN(y))
					return 1;
				return (x > y?1:-1);
			};
		} 
		dataView.sort(sorter, args.sortCols[0].sortAsc);
		grid.invalidate();
	});
	
	//set fixed width for specific columns for non-geography list view
	if ($('#isGeoList').val() == 0) {
		var cols = grid.getColumns();
	    cols[0].width = 40;  //icons
	    cols[1].width = 400; //facility name
	    cols[3].width = 80;  //state
	    grid.setColumns(cols);
		$('.slick-header-columns').children().eq(1).trigger('click'); //make sure to sort by facility first
	}
	
	Events.subscribe('resize', function() {
		grid.resizeCanvas();
	});
}

function deprecatedInitEmitterFacilityListView(data,title){
	
	var dataGoogle = new google.visualization.DataTable();
	dataGoogle.addColumn('string', title);
	dataGoogle.addColumn('string', 'City');
	dataGoogle.addColumn('string', 'State');
	
	//Set Table Headers
	
    for(var i in data.label){
    	//if (data.label[i] == 'CO<sub>2</sub>') {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + ")");
    	//} else {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + " CO<sub>2</sub>e)");
    	//}
    	dataGoogle.addColumn('number', data.label[i]);
    }
    dataGoogle.addColumn('string', 'Sectors');
    
    var numRow = 0;
    var numCols = 0;
    
    for(var i in data.values){
    	if(isNaN(i)){
    		numRow = 1;
    		break;
    	}
    	numRow += 1;
    }
    
    for(var i in data.label){
    	numCols += 1;
    }
    dataGoogle.addRows(numRow);

    var formatter = new google.visualization.NumberFormat({groupingSymbol: ',', fractionDigits: 0});
    
	for(var i=0; i < numRow; i++){
		var obj = data.values[i];
		//dataGoogle.setCell(i, 0, obj.label);
		dataGoogle.setCell(i, 0, obj.label, obj.label, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 1, obj.city, obj.city, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 2, obj.state, obj.state, {style: 'font-size: 11px'});
		if (obj.emissions == -1) {
			dataGoogle.setCell(i, 3, obj.emissions, '---', {style: 'font-size: 11px'});
		} else {
			dataGoogle.setCell(i, 3, obj.emissions, formatter.formatValue(obj.emissions), {style: 'font-size: 11px'});			
		}
		dataGoogle.setCell(i, 4, obj.sectors, obj.sectors, {style: 'font-size: 11px'});
    }

	var table = new google.visualization.Table(document.getElementById('canvas-vis'));

    table.draw(dataGoogle, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
}

function initEmitterFacilityTrendListView(data,title){
	var list = new google.visualization.DataTable(data.data);
	var table = new google.visualization.Table(document.getElementById('canvas-vis'));
	table.draw(list, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
}

function deprecatedInitEmitterFacilityTrendListView(data,title){
	
	var dataGoogle = new google.visualization.DataTable();
	dataGoogle.addColumn('string', title);
	dataGoogle.addColumn('string', 'City');
	dataGoogle.addColumn('string', 'State');
	
	//Set Table Headers
	
    //for(var i in data.label){
    	//if (data.label[i] == 'CO<sub>2</sub>') {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + ")");
    	//} else {
    	//	dataGoogle.addColumn('number', data.label[i] + " (" + data.unit + " CO<sub>2</sub>e)");
    	//}
    	//dataGoogle.addColumn('number', data.label[i]);
    //}
	var year2 = data.year;
	var year1 = year2 - 1;
	dataGoogle.addColumn('number','Total Reported Emissions, ' + year1);
	dataGoogle.addColumn('number','Total Reported Emissions, ' + year2);
	dataGoogle.addColumn('number','Change in Emissions (' + year1 + ' to ' + year2 + ')');
    dataGoogle.addColumn('string', 'Sectors');
    
    var numRow = 0;
    var numCols = 0;
    
    for(var i in data.values){
    	if(isNaN(i)){
    		numRow = 1;
    		break;
    	}
    	numRow += 1;
    }
    
    for(var i in data.label){
    	numCols += 1;
    }
    dataGoogle.addRows(numRow);

	for(var i=0; i < numRow; i++){
		var obj = data.values[i];
		//dataGoogle.setCell(i, 0, obj.label);
		dataGoogle.setCell(i, 0, obj.label, obj.label, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 1, obj.city, obj.city, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 2, obj.state, obj.state, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 3, obj.year1, obj.year1, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 4, obj.year2, obj.year2, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 5, obj.difference, obj.difference, {style: 'font-size: 11px'});
		dataGoogle.setCell(i, 6, obj.sectors, obj.sectors, {style: 'font-size: 11px'});
    }

	var table = new google.visualization.Table(document.getElementById('canvas-vis'));
    
	var formatter = new google.visualization.NumberFormat({groupingSymbol: ',', fractionDigits: 0});
	var arrowFormatter = new google.visualization.TableArrowFormat();
	formatter.format(dataGoogle, 3);
	formatter.format(dataGoogle, 4);
	formatter.format(dataGoogle, 5);
	arrowFormatter.format(dataGoogle, 5);

    table.draw(dataGoogle, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
}

function initSupplierListView(data){
	
	var dataGoogle = new google.visualization.DataTable();
	dataGoogle.addColumn('string', 'Facility');
	
	//Set Table Headers
	
	dataGoogle.addColumn('string', data.label[0]);
	dataGoogle.addColumn('string', data.label[1]);
	//dataGoogle.addColumn('string', data.label[2]);
	dataGoogle.addColumn('number', data.label[2]);
	//dataGoogle.addColumn('number', data.label[4]);
	//dataGoogle.addColumn('number', data.label[5]);
	
	var numCols = 3;
	var numRows = data.rows;
	
	dataGoogle.addRows(numRows);
	
	var formatter = new google.visualization.NumberFormat({groupingSymbol: ',', fractionDigits: 0});
	
	for(var i=0; i < numRows; i++){
		var obj = data.values[i];
		dataGoogle.setCell(i, 0, obj.label, obj.label, {style: 'font-size: 11px'});
		for(var j=0; j<numCols; j++){
			if (j == 2) {
				if (obj.values[j] == -1) {
					dataGoogle.setCell(i, j+1, obj.values[j], '---', {style: 'font-size: 11px; text-align: right'});
				} else {
					dataGoogle.setCell(i, j+1, obj.values[j], formatter.formatValue(obj.values[j]), {style: 'font-size: 11px; text-align: right'});
				}
			} else {
				dataGoogle.setCell(i, j+1, obj.values[j], obj.values[j], {style: 'font-size: 11px'});
			}
		}
    }

    var table = new google.visualization.Table(document.getElementById('canvas-vis'));
	
	//var formatter = new google.visualization.NumberFormat({groupingSymbol: ',', fractionDigits: 0});
	//formatter.format(dataGoogle, 3);
	//formatter.format(dataGoogle, 5);
	//formatter.format(dataGoogle, 6);

	table.draw(dataGoogle, {allowHtml:true, cssClassNames: {headerRow: 'listHeader'}});
}

function getFacilityInfo() {
	var facId = $("#facilityId").val();
	Events.publish('canvas-switch',['detail']);
	var vDs = dataSource;
	var vUrl = "service/facilityDetail/";
    if(dataSource == "T") {
    	vUrl = "service/pipeDetail/";
    }
	jQuery.ajax({
		type: 'GET',
		url: vUrl + ryear,
		data: "id="+facId+"&ds="+vDs+"&et="+$("#emissionsType").val(),
		success: function(response) {
			$("#canvas-detail").html(response);
			$("#canvas-detail").removeAttr('class');
			$("#canvas-detail").attr('style', 'background-color:#fff; height: 498px; overflow-x: auto; overflow-y: auto;');
		},
		complete: function() {
			drawFacilityInfo();
		}
	});
}
