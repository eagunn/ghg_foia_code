var labelType, useGradients, nativeTextSupport, animate;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);
})();

function allNumbers(str){
	var count = 0;
	for(var i = 0; i < str.length; i++){
		if(str.charAt(i) >= '0' && str.charAt(i) <= '9'){
			count++;
		}
	}
	if(count == str.length){
		return true;
	} else {
		return false;
	}
}
function pullTitle(str){
	return str.substring(0,str.indexOf("<br>"));
}


function initTreeMap(data){
  var json = data;
  var view = json['view'];
  var tm = new $jit.TM.Squarified({
    injectInto: 'canvas',
    titleHeight: 30,
    animate: animate,
    offset: 2,
	levelsToShow: 1,
    //Attach left and right click events
    Events: {
      enable: true,
	  onMouseMove: function(node, eventInfo, e) {
		  //This part works in IE
//		    if(allNumbers(e.srcElement.innerText) == false){
//				var clickedItem = "<b>"+e.srcElement.innerText+"<br></b>";
//			}
		    //This part works in Firefox
		  // Caesar - Comment everything below
			//if(allNumbers(e.target.textContent) == false){
			//	var clickedItem = "<b>"+e.target.innerHTML+"<br></b>";
			//} else {
			//	var clickedItem = "<b>"+e.rangeParent.parentNode.parentNode.innerHTML+"<br></b>";
			//}
			//var clickedItem = pullTitle(clickedItem);
			//var id = "#dialog";
			//$(id).css('top',  340+eventInfo.getPos().y);
			//$(id).css('left', 900+eventInfo.getPos().x);

			//document.getElementById("dialog").innerHTML = clickedItem;
			
			//if((eventInfo.getPos().x < 390 && eventInfo.getPos().x > -390) && (eventInfo.getPos().y < 290 && eventInfo.getPos().y > -255)){
			//	$(id).fadeIn(100); 
			//} else {
			//	$(id).hide();
			//}
	  },
	  onMouseLeave: function(node, eventInfo, e){
		//var id = "#dialog";
		//$(id).hide();
	  },
	  onClick: function(node, eventInfo, e){
		 if (view == "SECTOR1") {
			 resetFilters(node);
			 generateURL('treeSectorL2');
		 } else if (view == "SECTOR2") {
			 jQuery('#subsectorName').val(node.name);
			 generateURL('treeSectorL3');
		 } else if (view == "SECTOR3" || view == "BASIN2") {
			 jQuery("#_tooltip").css('display','none');
			 displayFacilityDetail(node.data.$info);
		 } else if(view == "SECTOR4"){
			 jQuery('#subsectorName').val(node.data.$subsector);			 
			 jQuery('#parentState').val(stateToAbbreviation(node.name));
			 generateURL('treeSectorL3');
		 } else if (view == "STATE1") {
			 jQuery('#parentState').val(stateToAbbreviation(node.name));
			 generateURL('treeStateL2');
		 } else if (view == "STATE2") {
			 jQuery("#_tooltip").css('display','none');
			 displayFacilityDetail(node.data.$info);
		 } else if (view == "STATE3") {
			 jQuery('#countyState').val(node.data.$info);
			 generateURL('treeStateL2');
		 } else if (view == "SUPPLIER"){
			 jQuery("#_tooltip").css('display','none');
			 displayFacilityDetail(node.data.$info);
		 } else if (view == 'BASIN1') {
			jQuery('#basin').val(node.data.$info);
			generateURL('treeSector');
		 }
	  }
    },
    duration: 1000,
	Tips: {
	  	enable: true,
	  	onShow: function(tip, node, isLeaf, elem) {
	  		tip.innerHTML = "<b>"+node.name+"</b>: "+addCommas(node.data.$area);
	  }
	},

    //Add the name of the node in the correponding label
    //This method is called once, on label creation.
    onCreateLabel: function(domElement, node){
        domElement.innerHTML = node.name;
        var style = domElement.style;
        style.display = '';
        style.border = '3px solid transparent';
        domElement.onmouseover = function() {
          style.border = '3px solid #9FD4FF';
        };
        domElement.onmouseout = function() {
          style.border = '3px solid transparent';
        };
    }
  });
  tm.loadJSON(json);
  tm.refresh();
}
