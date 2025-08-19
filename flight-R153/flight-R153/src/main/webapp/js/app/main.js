/**
 * Created by alabdullahwi on 5/26/2015.
 *
 */

var map;
var initLoaded = false;
var loaded = false;
var markerCluster;
var lastFlightRequest;
var dataSource = 'E';
var visType = 'map';
var mapSelector = 0; // 0 = Facility, 1 = Intensity
var listSelector = 2; // 0 = Sector, 1 = GHG, 2 = Facility
var barSelector = 0; // 0 = Sector, 1 = GHG, 2 = Geography
var pieSelector = 0; // 0 = Sector, 1 = GHG, 2 = Geography
var treeSelector = 0; // 0 = Sector, 1 = GHG, 2 = Geography
var trendSelection = "current"; // current = Current Year, trend = Trend Report
var currentJsonObject;
var welcomeScreenAlreadyShown = false;
var welcomeSupplierScreenAlreadyShown = false;
var supplierSector = 0;
var injectionSelection = 11;
var sortOrder = 0; // value of emitterSort or supplierSort based on context
var emitterSort = 0; // 0 = Facility ASC, 1 = Facility DSC, 2 = Emission ASC, 3 = Emission DSC
var supplierSort = 0; // 0 = Facility ASC, 1 = Facility DSC, 2 = Emission ASC, 3 = Emission DSC
//var dataDate is resolved higher at main.htm since it is coming from velocity
//var ryear is resolved higher at main.htm since it is coming from velocity
var cyear = ryear;
var zoom = 4;
var state = 'US';
var stateAbbr = state;
var yearNoteShown = false;
var reportingStatus = "ALL";
var stateLevel = 0; // 0 = County, 1 = MSA
var overlayLevel = 0; // 0 = Facility, 1 = Bubble
google.load('visualization', '1', {packages: ['table']});
var colorLight = new Array();
colorLight[0] = '#FFEBCE';
colorLight[1] = '#FDF3D2';
colorLight[2] = '#FFFFD9';
colorLight[3] = '#E1F3FC';
colorLight[4] = '#ECF8F8';
colorLight[5] = '#F7FBFD';
colorLight[6] = '#E1E1EB';
colorLight[7] = '#EBEBF1';
colorLight[8] = '#F3F3F7';
colorLight[9] = '#EFEFEF';
var colorDark = new Array();
colorDark[0] = '#FFBD59';
colorDark[1] = '#F7D869';
colorDark[2] = '#FFFF7F';
colorDark[3] = '#99D8F5';
colorDark[4] = '#C0E6E6';
colorDark[5] = '#DDEDF4';
colorDark[6] = '#9B9EBB';
colorDark[7] = '#B9B9CF';
colorDark[8] = '#D7D7E4';
isSliderDown = false;
$(document).on('mousedown', '.ui-slider-handle', function () {
	isSliderDown = true;
});
$(document).one('keydown', function (e) {
	if (e.keyCode == 27) {
		clickX();
	}
});
$(".xClose").live('keyup', function (e) {
	if (e.keyCode == 13) {
		$(e.target).trigger('click').clickX();
	}
});
$(document).ready(function () {
	//508 Compliant: set missing attributes for "share" social media iframes
	$("iframe[id*='twitter']").attr("name", "Twitter Tweet");
	$("iframe[id*='oauth2relay']").attr("title", "Google");
	$(".ACS_").attr("name", "ForeSee Storage");
	$("#rufous-sandbox").attr("name", "Twitter analytics");
	var IEVer = document.documentMode;
	if (IEVer == 8) {
		$("#welcomeWindowEmitters").height(580);
		$("#browserWarningWindow").show();
	}
	var ua = navigator.userAgent.toLowerCase();
	var isIE = (ua.indexOf("msie") != -1);
	if (IEVer < 9 || (isIE && IEVer == undefined)) {
		document.getElementById('browserWarning').style.display = 'block'
	}
	$(document).mouseup(function () {
		if (isSliderDown) {
			bounceApplyButtons();
			isSliderDown = false;
		}
	});
	$("#flourinatedgasescheck").change(
		function () {
			if ($(this).is(':checked')) {
				$(".flourinated").attr("disabled", false);
				$(".flourinated").attr("checked", true);
			} else {
				$(".flourinated").attr("checked", false);
			}
		}
	);
	$(".applyButtons").click(function () {
		var applyBtns = document.querySelectorAll('.applyButtons');
		for (i = 0, len = applyBtns.length; i < len; i++) {
			$target = $(applyBtns[i]);
			$target.removeClass('btn-success');
			$target.addClass('btn-primary');
		}
	});
	$("#stateSelectAndSearch > div > select").change(function () {
		bounceApplyButtons();
	});
	$("#countySelectAndSearch > div > select").change(function () {
		bounceApplyButtons();
	});
	$(".applyTriggerButton").click(function () {
		bounceApplyButtons();
	});
	$(".applyTrigger").change(function () {
		bounceApplyButtons();
	});
	$("#searchOptionsPopover > ul > li > label > input").click(function () {
		bounceApplyButtons();
	});
	$("#emitterDiv2 > div > ul > li > label > input").click(function () {
		bounceApplyButtons();
	});
	$("#sectorData > tbody > tr > td  input:checkbox").click(function () {
		bounceApplyButtons();
	});
	// Add the sectors and subsector checkboxes to the main list
	sectorCheckboxes = [];
	sectorCheckboxes.push($("#sector1checkbox:checkbox"));
	sectorCheckboxes.push($("#sector2checkbox:checkbox"));
	sectorCheckboxes.push($("#sector3checkbox:checkbox"));
	sectorCheckboxes.push($("#sector4checkbox:checkbox"));
	sectorCheckboxes.push($("#sector5checkbox:checkbox"));
	sectorCheckboxes.push($("#sector6checkbox:checkbox"));
	sectorCheckboxes.push($("#sector7checkbox:checkbox"));
	sectorCheckboxes.push($("#sector8checkbox:checkbox"));
	sectorCheckboxes.push($("#sector9checkbox:checkbox"));
	sectorCheckboxes.push($("#sector10checkbox:checkbox"));
	sectorCheckboxes.push($("#chem1:checkbox"));
	sectorCheckboxes.push($("#chem2:checkbox"));
	sectorCheckboxes.push($("#chem3:checkbox"));
	sectorCheckboxes.push($("#chem4:checkbox"));
	sectorCheckboxes.push($("#chem5:checkbox"));
	sectorCheckboxes.push($("#chem6:checkbox"));
	sectorCheckboxes.push($("#chem7:checkbox"));
	sectorCheckboxes.push($("#chem8:checkbox"));
	sectorCheckboxes.push($("#chem9:checkbox"));
	sectorCheckboxes.push($("#chem10:checkbox"));
	sectorCheckboxes.push($("#chem11:checkbox"));
	sectorCheckboxes.push($("#chem12:checkbox"));
	sectorCheckboxes.push($("#other1:checkbox"));
	sectorCheckboxes.push($("#other2:checkbox"));
	sectorCheckboxes.push($("#other3:checkbox"));
	sectorCheckboxes.push($("#other4:checkbox"));
	sectorCheckboxes.push($("#other5:checkbox"));
	sectorCheckboxes.push($("#other6:checkbox"));
	sectorCheckboxes.push($("#other7:checkbox"));
	sectorCheckboxes.push($("#other8:checkbox"));
	sectorCheckboxes.push($("#other9:checkbox"));
	sectorCheckboxes.push($("#other10:checkbox"));
	sectorCheckboxes.push($("#waste1:checkbox"));
	sectorCheckboxes.push($("#waste2:checkbox"));
	sectorCheckboxes.push($("#waste3:checkbox"));
	sectorCheckboxes.push($("#waste4:checkbox"));
	sectorCheckboxes.push($("#metal1:checkbox"));
	sectorCheckboxes.push($("#metal2:checkbox"));
	sectorCheckboxes.push($("#metal3:checkbox"));
	sectorCheckboxes.push($("#metal4:checkbox"));
	sectorCheckboxes.push($("#metal5:checkbox"));
	sectorCheckboxes.push($("#metal6:checkbox"));
	sectorCheckboxes.push($("#metal7:checkbox"));
	sectorCheckboxes.push($("#mineral1:checkbox"));
	sectorCheckboxes.push($("#mineral2:checkbox"));
	sectorCheckboxes.push($("#mineral3:checkbox"));
	sectorCheckboxes.push($("#mineral4:checkbox"));
	sectorCheckboxes.push($("#pulp1:checkbox"));
	sectorCheckboxes.push($("#pulp2:checkbox"));
	sectorCheckboxes.push($("#petroleum1:checkbox"));
	sectorCheckboxes.push($("#petroleum2:checkbox"));
	sectorCheckboxes.push($("#petroleum3:checkbox"));
	sectorCheckboxes.push($("#petroleum4:checkbox"));
	sectorCheckboxes.push($("#petroleum5:checkbox"));
	sectorCheckboxes.push($("#petroleum6:checkbox"));
	sectorCheckboxes.push($("#petroleum7:checkbox"));
	sectorCheckboxes.push($("#petroleum8:checkbox"));
	sectorCheckboxes.push($("#petroleum9:checkbox"));
	sectorCheckboxes.push($("#petroleum10:checkbox"));
	sectorCheckboxes.push($("#petroleum11:checkbox"));
	// Add a listener to each checkbox
	sectorCheckboxes.forEach(function (entity) {
		entity.change(checkInfoLinkLogic);
	});
	$('#dataType').change(checkInfoLinkLogic);
	$("#popout-container").hover(popoutHoverIn, popoutHoverOut);
	// Add gases to list
	ghgCheckboxes = [];
	ghgCheckboxes.push($("#gas1check:checkbox"));
	ghgCheckboxes.push($("#gas2check:checkbox"));
	ghgCheckboxes.push($("#gas3check:checkbox"));
	ghgCheckboxes.push($("#gas4check:checkbox"));
	ghgCheckboxes.push($("#gas5check:checkbox"));
	ghgCheckboxes.push($("#gas6check:checkbox"));
	ghgCheckboxes.push($("#gas7check:checkbox"));
	ghgCheckboxes.push($("#gas8check:checkbox"));
	ghgCheckboxes.push($("#gas9check:checkbox"));
	ghgCheckboxes.push($("#gas10check:checkbox"));
	ghgCheckboxes.push($("#gas11check:checkbox"));
	ghgCheckboxes.push($("#gas12check:checkbox"));
	// Add a listener to each box
	ghgCheckboxes.forEach(function (entity) {
		entity.change(checkGhgInfoLinkLogic);
	});
	$("#popout-container-ghg").hover(popoutGhgHoverIn, popoutGhgHoverOut);
	$("#browserWarning").hide();
	$("#whatThisTextReportingStatus").html(resolveReportingStatusWhatsThisText(reportingStatus, cyear, dataDate));
	$('a#excel').click(function (event) {
		event.preventDefault();
	});
	$('a.link').live("click", function (event) {
		jQuery.ajax({
			type: 'POST',
			url: '#springUrl("/service/")' + this.id
		});
	});
	Events.subscribe('canvas-switch', function (type) {
		if (type === 'map') {
			$(".ui-layout-center").css('overflow', '');
			$('#canvas-vis-wrapper').hide();
			$('#canvas-vis').hide();
			$('#canvas-detail').hide();
			$('#canvas-map').show();
			if (layout.state.west.isClosed) { //open westPanel on map
				layout.open("west");
			}
		} else if (type === 'list') {
			$(".ui-layout-center").css('overflow', '');
			$('#canvas-vis-wrapper').show();
			$('#canvas-vis').hide();
			$('#canvas-detail').hide();
			$('#canvas-map').hide();
			if (!layout.state.west.isClosed) {
				layout.close("west");
			}
			ajaxLoading();
		} else if (type === 'detail') {
			$(".ui-layout-center").css('overflow', '');
			$('#canvas-vis-wrapper').show();
			$('#canvas-map').hide();
			$('#canvas-vis').hide();
			if (layout.state.west.isClosed) {
				layout.open("west");
			}
			ajaxLoading();
			$('#canvas-detail').show();
		} else {
			$(".ui-layout-center").css('overflow', '');
			$('#canvas-vis-wrapper').show();
			$('#canvas-map').hide();
			$('#canvas-detail').hide();
			if (layout.state.west.isClosed) {
				layout.open("west");
			}
			ajaxLoading();
			$('#canvas-vis').show();
		}
		// Make sure the current visualization is apparent in the Data View buttons
		$("#dataView a.btn").removeClass("active");
		if (visType == "map") {
			$("#vMap").addClass("active");
		}
		if (visType == "list") {
			$("#vList").addClass("active");
		}
		if (visType == "line") {
			$("#vLine").addClass("active");
		}
		if (visType == "bar") {
			$("#vBar").addClass("active");
		}
		if (visType == "pie") {
			$("#vPie").addClass("active");
		}
	});
	Events.subscribe('resize', function () {
		var chart = $('#canvas-vis').highcharts();
		if (chart != undefined) {
			chart.setSize($('#canvas-vis').width(), $('#canvas-vis').height(), doAnimation = false);
		}
	});
	Highcharts.setOptions({
		lang: {
			numericSymbols: null
		}
	});
	var fipsCode = addLeadingZeros($("#countyState").val());
	var msaCode = "";
	var tribalLandId = "";
	if (stateAbbr == "TL") {
		fipsCode = "";
		msaCode = "";
		tribalLandId = $("#tribe").find(":selected").val();
	}
	if (stateLevel == 1) {
		msaCode = fipsCode;
		fipsCode = "";
	}
	lowE = $("#lowEmissionRange").val();
	highE = $("#highEmissionRange").val();
	stateAbbr = $("#parentState").val();
	$("#facOrLocInput").autocomplete({
		source: function (request, response) {
			$('#autocompleteLoading').attr('style', 'display:block;float:right');
			jQuery.getJSON(
				"service/autocomplete/" + ryear + "?term=" + request.term +
				"&lowE=" + lowE + "&highE=" + highE + "&fc=" + fipsCode + "&mc=" + msaCode + "&st=" + stateAbbr +
				"&tl=" + tribalLandId + "&et=" + $("#emissionsType").val() +
				generateGasFilterQueryString() + generateSectorFilterQueryString() + generateSearchQuery() +
				"&ds=" + dataSource + "&sc=" + supplierSector + "&so=" + sortOrder,
				function (data) {
					response(jQuery.map(data, function (item) {
						return {label: item, value: item}
					}));
					$('#autocompleteLoading').attr('style', 'display:none');
				});
		}
	});
	$("#lastFacOrLoc").val(escape('Find a Facility or Location')); //PUB-162
	$('#sectorAll').click(function () {
		$("#sector5Options").hide();
		$("#sector9Options").hide();
		$("#sector2Options").hide();
		$("#sector3Options").hide();
		$("#sector4Options").hide();
		$("#sector6Options").hide();
		$("#sector8Options").hide();
		resetSectorsSelection();
		updateSectorFilterButton();
		bounceApplyButtons();
		checkDataTypeMatch();
	});
	$('#sectorNone').click(function () {
		$("#sector5Options").hide();
		$("#sector9Options").hide();
		$("#sector2Options").hide();
		$("#sector3Options").hide();
		$("#sector4Options").hide();
		$("#sector6Options").hide();
		$("#sector8Options").hide();
		$("#sector1checkbox").attr('checked', false);
		$("#sector2checkbox").attr('checked', false);
		$("#sector3checkbox").attr('checked', false);
		$("#sector4checkbox").attr('checked', false);
		$("#sector5checkbox").attr('checked', false);
		$("#sector6checkbox").attr('checked', false);
		$("#sector7checkbox").attr('checked', false);
		$("#sector8checkbox").attr('checked', false);
		$("#sector9checkbox").attr('checked', false);
		$('.check_petro').prop('checked', false);
		$('.check_chem').prop('checked', false);
		$('.check_other').prop('checked', false);
		$('.check_mineral').prop('checked', false);
		$('.check_waste').prop('checked', false);
		$('.check_metal').prop('checked', false);
		$('.check_pulp').prop('checked', false);
		determineTotalCheckbox10Select();
		updateSectorFilterButton();
		bounceApplyButtons();
		checkDataTypeMatch();
	});
	$('#vMap').click(function () {
		setVisType('map');
	});
	$('#vList').click(function () {
		setVisType('list');
	});
	$('#vLine').click(function () {
		setVisType('line');
	});
	$('#vBar').click(function () {
		setVisType('bar');
	});
	$('#vPie').click(function () {
		setVisType('pie');
	});
	$('#vTree').click(function () {
		setVisType('tree');
	});
	// Show or hide the GHG Filter popover when the GHG Filter button is pressed
	$('#ghgBtn').click(function () {
		$('#emissionRangePopover').hide()
		$('#searchOptionsPopover').hide();
		$('#whatsThisPopover').hide();
		$('#filterSectorPopover').hide();
		$('#ghgPopover').toggle();
		$('#ghgPopover').focus();
	});
	// Change ghgBtn text to display "filtered" or not
	$("#ghgPopover input").change(function () {
		var defaultFilter = true;
		$("#ghgPopover input[type='checkbox']").each(function () {
			if ($(this).attr("id") != "flourinatedgasescheck" && $(this).attr("id") != "gas6check" && !$(this).is(":checked")) {
				defaultFilter = false;
				return false;
			}
		});
		if (defaultFilter) {
			$('#ghgBtn').html("Greenhouse Gas");
		} else {
			$('#ghgBtn').html("Greenhouse Gas (filtered)");
		}
	});
	// Show or hide the Emission Range Filter popover when the Emission Range button is pressed
	$('#emissionsBtn').click(App.showEmissionsRangePopover);
	// Change emissionsBtn text to display "filtered" or not
	$("#emissionRangePopover input").change(function () {
		if ($("#lowEmissionRange").val() == -20000 && $("#highEmissionRange").val() == 23000000) {
			$('#emissionsBtn').html("Emission Range");
		} else {
			$('#emissionsBtn').html("Emission Range (filtered)");
		}
	});
	$("#slider-range").on("slidechange", function (event, ui) {
		if ($("#slider-range").slider("values", 0) == -20000 && $("#slider-range").slider("values", 1) == 23000000) {
			$('#emissionsBtn').html("Emission Range");
		} else {
			$('#emissionsBtn').html("Emission Range (filtered)");
		}
	});
	// Show or hide the Sector Filter popover when the Sector Filter button is pressed
	$('#sectorBtn').click(function () {
		$('#emissionRangePopover').hide()
		$('#searchOptionsPopover').hide();
		$('#whatsThisPopover').hide();
		$('#ghgPopover').hide();
		$('#filterSectorPopover').toggle();
		$('#filterSectorPopover').focus();
	});
	// Show or hide the Sector Filter popover when a Sector cogwheel is pressed
	$('.filterSector').click(function () {
		$('#emissionRangePopover').hide()
		$('#searchOptionsPopover').hide();
		$('#whatsThisPopover').hide();
		$('#ghgPopover').hide();
		$('#filterSectorPopover').toggle();
		$('#filterSectorPopover').focus();
	});
	// Close the Filter popovers when the close button is clicked
	$('.mPopover > .close').click(function () {
		$('.mPopover').hide();
	});
	$('#whatsThisLinkReportingStatus a').click(function (event) {
		event.preventDefault();
		$('#ghgPopover').hide();
		$('#searchOptionsPopover').hide();
		$('#emissionRangePopover').hide();
		$('#filterSectorPopover').hide();
		$('#whatsThisPopoverReportingStatus').toggle();
	});
	$('#whatsThisLink a').click(function (event) {
		event.preventDefault();
		$('#ghgPopover').hide();
		$('#searchOptionsPopover').hide();
		$('#emissionRangePopover').hide();
		$('#filterSectorPopover').hide();
		$('#whatsThisPopover').toggle();
	});
	$('#whatsThisLinkEmissionsType a').click(function (event) {
		event.preventDefault();
		$('#ghgPopover').hide();
		$('#searchOptionsPopover').hide();
		$('#emissionRangePopover').hide();
		$('#filterSectorPopover').hide();
		$('#whatsThisPopoverEmissionsType').toggle();
	});
	$('#whatsTotal a').click(function (event) {
		event.preventDefault();
		$('#ghgPopover').hide();
		$('#searchOptionsPopover').hide();
		$('#emissionRangePopover').hide();
		$('#filterSectorPopover').hide();
		$('#whatsTotalDiv').toggle();
	});
	// Hide the What's This popup when the apply button is clicked
	$('#whatsThisPopover  > .btn').click(function () {
		$('#whatsThisPopover').hide();
	});
	$('#searchOptionsLink a').click(function (event) {
		event.preventDefault();
		$('#ghgPopover').hide();
		$('#whatsThisPopover').hide();
		$('#emissionRangePopover').hide();
		$('#filterSectorPopover').hide();
		$('#searchOptionsPopover').toggle();
	});
	// Hide the What's This popup when the apply button is clicked
	$('#searchOptionsPopover  > .btn').click(function () {
		$('#searchOptionsPopover').hide();
	});
	var layout = $("#container").layout({
		west: {
			initClosed: false, //open westPanel on start
			size: 290,
			fxSettings_open: {
				easing: "easeOutBounce"
			},
			fxSpeed_open: 750,
			fxSpeed_close: 500,
			onresize: function () {
				Events.publish('resize');
			},
			onopen: function () {
				Events.publish('resize');
			},
			onclose: function () {
				Events.publish('resize');
			}
		}
	});
	//reset county upon state change
	$("#parentState").change(function () {
		$("#countyState").val('');
		var $state = $(this);
		if ($state.val() == 'TL') {
			$('#tribeSelectAndSearch').show();
			$('#countySelectAndSearch').hide();
		} else {
			$('#tribeSelectAndSearch').hide();
		}
	});
	//hide bar chart, pie chart icons when CO2Injection is selected (PUB-498)
	$("#dataType").change(function () {
		var val = $(this).val();
		var urlChange = false;
		if (val == 'I') {
			hasTrend(injectionSelection, urlChange);
			$("a#vPie").hide();
			$("a#vBar").hide();
			$("#pieLabel").hide();
			$("#barLabel").hide();
		} else if (val == 'S') {
			hasTrend(supplierSector, urlChange);
			$("a#vPie").show();
			$("a#vBar").show();
			$("#pieLabel").show();
			$("#barLabel").show();
			$("#trendsBtn").show();
		} else {
			$("a#vPie").show();
			$("a#vBar").show();
			$("a#vLine").show();
			$("#pieLabel").show();
			$("#barLabel").show();
			$("#lineLabel").show();
		}
		if (val == 'E' || val == 'P') {
			if ($("#countyState").attr('disabled') != 'disabled' && $("#countyState").css('display') != 'none') {
				$("#countySelectAndSearch").show();
			}
			$("#emissionsTypeSelection").show();
			$("#emissionsType").val('');
		} else {
			$("#countySelectAndSearch").hide();
			$("#emissionsTypeSelection").hide();
			$("#emissionsType").val('');
		}
		if (val == 'F') {
			$("#parentState").attr('disabled', true);
		} else {
			$("#parentState").attr('disabled', false);
		}
	});
	$("#reportingYear").change(function () {
		//caching jQuery references
		var ry = Number($(this).val());
		if (ry > 2010 && (reportingStatus == 'ALL' || reportingStatus == '') && (dataSource != 'S' && dataSource != 'I' && dataSource != 'A')) {
			$("#vLine").show();
			$("#lineLabel").show();
		}
		//hide trend tool for reporing year 2010 and if in trend view switching to map view
		if (ry == 2010 && visType == "line") {
			$("#vLine").hide();
			$("#lineLabel").hide();
			setVisType('map');
		}
		//also hide Reporting Status filter pre 2013 (PUB-440)
		if (ry > 2012) {
			$("#reportingStatusSelection").show();
			reportingStatus = $("#reportingStatus input.dd-selected-value").val();
		} else {
			$("#reportingStatusSelection").hide();
			reportingStatus = "ALL";
		}
	});
	$("#emissionsType").change(function () {
		bounceApplyButtons();
	});
	$("#reportingStatus").ddslick({
		onSelected: function (selectedData) {
			if (loaded) {
				$("#whatsThisLinkReportingStatus").removeClass('animated flash');
				bounceApplyButtons();
				$("#whatsThisLinkReportingStatus").addClass('animated flash');
			}
			reportingStatus = selectedData.selectedData.value;
			cyear = $("#reportingYear").val();
			var whatsThisContent = resolveReportingStatusWhatsThisText(reportingStatus, cyear, dataDate);
			$("#whatsThisTextReportingStatus").html(whatsThisContent);
			if (reportingStatus != 'ALL') {
				$("a#vLine").hide();
				$("#lineLabel").hide();
				$("#trendsBtn > a").css("color", "#CCC");
			} else {
				if (dataSource != 'S' && dataSource != 'I') {
					$("a#vLine").show();
					$("#lineLabel").show();
					$("#trendsBtn > a").css("color", "#484");
				} else {
					if (dataSource == 'S') {
						hasTrend(supplierSector, false);
					} else {
						hasTrend(injectionSelection, false);
					}
				}
			}
			if (reportingStatus == 'RED' || reportingStatus == 'GRAY') {
				$("a#vPie").hide();
				$("a#vBar").hide();
				$("#pieLabel").hide();
				$("#barLabel").hide();
			} else {
				if (dataSource != 'I' && dataSource != 'A') {
					$("a#vPie").show();
					$("a#vBar").show();
					$("#pieLabel").show();
					$("#barLabel").show();
				}
			}
		}
	});
	if (initLoaded == false) {
		initialize();
		initLoaded = true;
	}
});
$(function () {
	$("#slider-range").slider({
		range: true,
		min: -20000,
		max: 23000000,
		values: [-20000, 23000000],
		slide: function (event, ui) {
			$("#lowEmissionRange").val(ui.values[0]);
			$("#highEmissionRange").val(ui.values[1]);
		}
	});
	$("#lowEmissionRange").blur(function () {
		$("#slider-range").slider("values", 0, parseInt($(this).val()));
	});
	$("#highEmissionRange").blur(function () {
		$("#slider-range").slider("values", 1, parseInt($(this).val()));
	});
	$("#sector9checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_petro').prop('checked', true);
		} else {
			$('.check_petro').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_petro").click(function () {
		updateSectorIcon(".check_petro", "#sector5Options");
		if ($(this).is(':checked')) {
			$('#sector9checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector7checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_chem').prop('checked', true);
		} else {
			$('.check_chem').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_chem").click(function () {
		updateSectorIcon(".check_chem", "#sector9Options");
		if ($(this).is(':checked')) {
			$('#sector7checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector8checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_other').prop('checked', true);
		} else {
			$('.check_other').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_other").click(function () {
		updateSectorIcon(".check_other", "#sector2Options");
		if ($(this).is(':checked')) {
			$('#sector8checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector4checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_mineral').prop('checked', true);
		} else {
			$('.check_mineral').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_mineral").click(function () {
		updateSectorIcon(".check_mineral", "#sector3Options");
		if ($(this).is(':checked')) {
			$('#sector4checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector2checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_waste').prop('checked', true);
		} else {
			$('.check_waste').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_waste").click(function () {
		updateSectorIcon(".check_waste", "#sector4Options");
		if ($(this).is(':checked')) {
			$('#sector2checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector3checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_metal').prop('checked', true);
		} else {
			$('.check_metal').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_metal").click(function () {
		updateSectorIcon(".check_metal", "#sector6Options");
		if ($(this).is(':checked')) {
			$('#sector3checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	$("#sector6checkbox").click(function () {
		if ($(this).is(':checked')) {
			$('.check_pulp').prop('checked', true);
		} else {
			$('.check_pulp').prop('checked', false);
		}
		determineTotalCheckbox10Select();
	});
	$(".check_pulp").click(function () {
		updateSectorIcon(".check_pulp", "#sector8Options");
		if ($(this).is(':checked')) {
			$('#sector6checkbox').prop('checked', true);
		}
		determineTotalCheckbox10Select();
	});
	// Change sectorBtn text to display "filtered" or not
	$("#filterSectorPopover input").change(function () {
		updateSectorFilterButton();
	});
	// this must come after above checkbox changes
	$(".check").click(function () {
		bounceApplyButtons();
		checkDataTypeMatch();
	});
});

function checkDataTypeMatch() {
	if (isPointSourcesOnly()) {
		$("#dataType").val("P");
		setDataType("P");
	} else if (isOnshoreOnly()) {
		$("#dataType").val("O");
		setDataType("O");
	} else if (isBoostingOnly()) {
		$("#dataType").val("B");
		setDataType("B");
	} else if (isLDCOnly()) {
		$("#dataType").val("L");
		setDataType("L");
	} else if (isPipeOnly()) {
		$("#dataType").val("T");
		setDataType("T");
	} else if (isSF6Only()) {
		$("#dataType").val("F");
		setDataType("F");
	} else {
		$("#dataType").val("E");
		setDataTypeValueOnly("E");
	}
}

function initialize() {
	setCookies();
	OurMap.init();
	onloadDrawMap();
}

function updatedParams(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId) {
	if ($("#lastState2").val() != stateAbbr) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastFacOrLoc").val() != facOrLoc) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastFipsCode").val() != fipsCode) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastMsaCode").val() != msaCode) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastBasin").val() != $("#basin").val()) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastLowE").val() != lowE) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastHighE").val() != highE) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg1").val() + "" != $("#gas1check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg2").val() + "" != $("#gas2check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg3").val() + "" != $("#gas3check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg4").val() + "" != $("#gas4check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg5").val() + "" != $("#gas5check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg7").val() + "" != $("#gas7check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg8").val() + "" != $("#gas8check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg9").val() + "" != $("#gas9check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastg10").val() + "" != $("#gas10check").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts1").val() + "" != $("#sector1checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts2").val() + "" != $("#sector2checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts3").val() + "" != $("#sector3checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts4").val() + "" != $("#sector4checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts5").val() + "" != $("#sector5checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts6").val() + "" != $("#sector6checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts7").val() + "" != $("#sector7checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts8").val() + "" != $("#sector8checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts9").val() + "" != $("#sector9checkbox").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts201").val() + "" != $("#waste1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts202").val() + "" != $("#waste2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts203").val() + "" != $("#waste3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts204").val() + "" != $("#waste4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts301").val() + "" != $("#metal1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts302").val() + "" != $("#metal2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts303").val() + "" != $("#metal3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts304").val() + "" != $("#metal4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts305").val() + "" != $("#metal5").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts306").val() + "" != $("#metal6").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts307").val() + "" != $("#metal7").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts401").val() + "" != $("#mineral1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts402").val() + "" != $("#mineral2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts403").val() + "" != $("#mineral3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts404").val() + "" != $("#mineral4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts405").val() + "" != $("#mineral5").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts601").val() + "" != $("#pulp1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts602").val() + "" != $("#pulp2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts701").val() + "" != $("#chem1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts702").val() + "" != $("#chem2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts703").val() + "" != $("#chem3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts704").val() + "" != $("#chem4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts705").val() + "" != $("#chem5").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts706").val() + "" != $("#chem6").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts707").val() + "" != $("#chem7").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts708").val() + "" != $("#chem8").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts709").val() + "" != $("#chem9").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts710").val() + "" != $("#chem10").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts711").val() + "" != $("#chem11").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts801").val() + "" != $("#other1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts802").val() + "" != $("#other2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts803").val() + "" != $("#other3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts804").val() + "" != $("#other4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts805").val() + "" != $("#other5").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts806").val() + "" != $("#other6").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts807").val() + "" != $("#other7").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts808").val() + "" != $("#other8").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts809").val() + "" != $("#other9").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts810").val() + "" != $("#other10").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts901").val() + "" != $("#petroleum1").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts902").val() + "" != $("#petroleum2").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts903").val() + "" != $("#petroleum3").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts904").val() + "" != $("#petroleum4").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts905").val() + "" != $("#petroleum5").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts906").val() + "" != $("#petroleum6").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts907").val() + "" != $("#petroleum7").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts908").val() + "" != $("#petroleum8").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts909").val() + "" != $("#petroleum9").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts910").val() + "" != $("#petroleum10").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lasts911").val() + "" != $("#petroleum11").attr('checked') + "") {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastds").val() != dataSource || $("#lastso").val() != sortOrder) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastst").val() != supplierSector) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastis").val() != injectionSelection) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastrs").val() != reportingStatus) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastyr").val() != ryear) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastEmissionsType").val() != emissionsType) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else if ($("#lastTribalLandId").val() != tribalLandId) {
		recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId);
		return true;
	} else {
		return false;
	}
}

function recordChanges(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId) {
	$("#lastState2").val(stateAbbr);
	$("#lastFacOrLoc").val(facOrLoc);
	$("#lastFipsCode").val(fipsCode);
	$("#lastMsaCode").val(msaCode);
	$("#lastBasin").val($("#basin").val());
	$("#lastLowE").val(lowE);
	$("#lastHighE").val(highE);
	$("#lastg1").val($("#gas1check").attr('checked') + "");
	$("#lastg2").val($("#gas2check").attr('checked') + "");
	$("#lastg3").val($("#gas3check").attr('checked') + "");
	$("#lastg4").val($("#gas4check").attr('checked') + "");
	$("#lastg5").val($("#gas5check").attr('checked') + "");
	$("#lastg6").val($("#gas6check").attr('checked') + "");
	$("#lastg7").val($("#gas7check").attr('checked') + "");
	$("#lastg8").val($("#gas8check").attr('checked') + "");
	$("#lastg9").val($("#gas9check").attr('checked') + "");
	$("#lastg10").val($("#gas10check").attr('checked') + "");
	$("#lasts1").val($("#sector1checkbox").attr('checked') + "");
	$("#lasts2").val($("#sector2checkbox").attr('checked') + "");
	$("#lasts3").val($("#sector3checkbox").attr('checked') + "");
	$("#lasts4").val($("#sector4checkbox").attr('checked') + "");
	$("#lasts5").val($("#sector5checkbox").attr('checked') + "");
	$("#lasts6").val($("#sector6checkbox").attr('checked') + "");
	$("#lasts7").val($("#sector7checkbox").attr('checked') + "");
	$("#lasts8").val($("#sector8checkbox").attr('checked') + "");
	$("#lasts9").val($("#sector9checkbox").attr('checked') + "");
	$("#lasts201").val($("#waste1").attr('checked') + "");
	$("#lasts202").val($("#waste2").attr('checked') + "");
	$("#lasts203").val($("#waste3").attr('checked') + "");
	$("#lasts204").val($("#waste4").attr('checked') + "");
	$("#lasts301").val($("#metal1").attr('checked') + "");
	$("#lasts302").val($("#metal2").attr('checked') + "");
	$("#lasts303").val($("#metal3").attr('checked') + "");
	$("#lasts304").val($("#metal4").attr('checked') + "");
	$("#lasts305").val($("#metal5").attr('checked') + "");
	$("#lasts306").val($("#metal6").attr('checked') + "");
	$("#lasts307").val($("#metal7").attr('checked') + "");
	$("#lasts401").val($("#mineral1").attr('checked') + "");
	$("#lasts402").val($("#mineral2").attr('checked') + "");
	$("#lasts403").val($("#mineral3").attr('checked') + "");
	$("#lasts404").val($("#mineral4").attr('checked') + "");
	$("#lasts405").val($("#mineral5").attr('checked') + "");
	$("#lasts601").val($("#pulp1").attr('checked') + "");
	$("#lasts602").val($("#pulp2").attr('checked') + "");
	$("#lasts701").val($("#chem1").attr('checked') + "");
	$("#lasts702").val($("#chem2").attr('checked') + "");
	$("#lasts703").val($("#chem3").attr('checked') + "");
	$("#lasts704").val($("#chem4").attr('checked') + "");
	$("#lasts705").val($("#chem5").attr('checked') + "");
	$("#lasts706").val($("#chem6").attr('checked') + "");
	$("#lasts707").val($("#chem7").attr('checked') + "");
	$("#lasts708").val($("#chem8").attr('checked') + "");
	$("#lasts709").val($("#chem9").attr('checked') + "");
	$("#lasts710").val($("#chem10").attr('checked') + "");
	$("#lasts711").val($("#chem11").attr('checked') + "");
	$("#lasts712").val($("#chem12").attr('checked') + "");
	$("#lasts801").val($("#other1").attr('checked') + "");
	$("#lasts802").val($("#other2").attr('checked') + "");
	$("#lasts803").val($("#other3").attr('checked') + "");
	$("#lasts804").val($("#other4").attr('checked') + "");
	$("#lasts805").val($("#other5").attr('checked') + "");
	$("#lasts806").val($("#other6").attr('checked') + "");
	$("#lasts807").val($("#other7").attr('checked') + "");
	$("#lasts808").val($("#other8").attr('checked') + "");
	$("#lasts809").val($("#other9").attr('checked') + "");
	$("#lasts810").val($("#other10").attr('checked') + "");
	$("#lasts901").val($("#petroleum1").attr('checked') + "");
	$("#lasts902").val($("#petroleum2").attr('checked') + "");
	$("#lasts903").val($("#petroleum3").attr('checked') + "");
	$("#lasts904").val($("#petroleum4").attr('checked') + "");
	$("#lasts905").val($("#petroleum5").attr('checked') + "");
	$("#lasts906").val($("#petroleum6").attr('checked') + "");
	$("#lasts908").val($("#petroleum8").attr('checked') + "");
	$("#lasts909").val($("#petroleum9").attr('checked') + "");
	$("#lasts910").val($("#petroleum10").attr('checked') + "");
	$("#lasts911").val($("#petroleum11").attr('checked') + "");
	$("#lastds").val(dataSource);
	$("#lastst").val(supplierSector);
	$("#lastis").val(injectionSelection);
	$("#lastol").val(overlayLevel);
	$("#lastso").val(sortOrder);
	$("#lastyr").val(ryear);
	$("#lastrs").val(reportingStatus);
	$("#lastEmissionsType").val(emissionsType);
	$("#lastTribalLandId").val(tribalLandId);
}

jQuery.address.change(function (event) {
	$(".spinner").hide();
	if (welcomeScreenAlreadyShown == false) {
		hideGHGfilter();
		hideEmissionsfilter();
		$('#searchOptionsPopover').hide(0);
		$('#whatsThisPopover').hide(0);
		$('#ghgPopover').hide(0);
		$('#emissionRangePopover').hide(50);
		$('#filterSectorPopover').hide();
		$("#mask").css('width', '100%');
		$("#mask").css('height', '100%');
		//##jQuery("#mask").fadeIn(1000);
		$("#mask").fadeTo("slow", 0.5);
		if (event.parameters['ds'] == null) {
			$("#welcomeWindowSuppliers").attr('style', 'display:none')
			$("#welcomeCloseEmitter").attr('style', 'display:none');
			$("#welcomeCloseSupplier").attr('style', 'display:none');
			$("#welcomeCloseOnshore").attr('style', 'display:none');
			$("#welcomeCloseLDC").attr('style', 'display:none');
			$("#welcomeCloseCO2Injection").attr('style', 'display:none');
			$("#welcomeCloseSF6").attr('style', 'display:none');
			$("#welcomeClosePointSource").attr('style', 'display:none');
			$("#welcomeWindow").css('top', getBrowserWindowHeight() / 2 - 150);
			$("#welcomeWindow").css('left', getBrowserWindowWidth() / 2 - 427);
			$("#welcomeWindow").fadeIn(1000);
			welcomeScreenAlreadyShown = true;
		} else {
			if (event.parameters['ds'] == 'E') {
				$("#dataType").val("E");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:block;padding-bottom:30px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'S' && event.parameters['sc'] != null && event.parameters['sc'] != 0) {
				supplierSector = event.parameters['sc'];
				$("#dataType").val("S");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:block;padding-bottom:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'S' && event.parameters['sc'] != null && event.parameters['sc'] == 0) {
				$("#dataType").val("S");
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:block;padding-left:40px;padding-bottom:90px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('top', getBrowserWindowHeight() / 2 - 150);
			} else if (event.parameters['ds'] == 'O') {
				$("#dataType").val("O");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'B') {
				$("#dataType").val("B");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'L') {
				$("#dataType").val("L");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'T') {
				$("#dataType").val("T");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'I') {
				injectionSelection = event.parameters['is'];
				$("#dataType").val("I");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'F') {
				$("#dataType").val("F");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeClosePointSource").attr('style', 'display:none');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'P') {
				$("#dataType").val("P");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			} else if (event.parameters['ds'] == 'A') {
				$("#dataType").val("A");
				$("#welcomeWindowEmitters").attr('style', 'display:none');
				$("#welcomeWindowSuppliers").attr('style', 'display:none');
				$("#welcomeCloseEmitter").attr('style', 'display:none');
				$("#welcomeCloseSupplier").attr('style', 'display:none');
				$("#welcomeCloseOnshore").attr('style', 'display:none');
				$("#welcomeCloseLDC").attr('style', 'display:none');
				$("#welcomeCloseCO2Injection").attr('style', 'display:none');
				$("#welcomeCloseSF6").attr('style', 'display:none');
				$("#welcomeClosePointSource").attr('style', 'display:block;padding-left:40px;background-image:url(img/welcomeBG2.gif);background-color:#FFF;background-position:center;background-repeat:no-repeat;border:4px solid #999');
				$("#welcomeWindow").css('height', 100);
				$("#welcomeWindow").css('top', 200);
			}
			$("#welcomeWindow").css('left', getBrowserWindowWidth() / 2 - 427);
			$("#welcomeWindow").fadeIn(1000);
			welcomeScreenAlreadyShown = true;
		}
	}
	//This is the section where given all of the information in event, the page has to be reloaded
	var stateAbbr = "";
	var facOrLoc = "";
	var fipsCode = "";
	var basinCode = "";
	var lowE = $("#lowEmissionRange").val();
	var highE = $("#highEmissionRange").val();
	var ss = "";
	var si = "";
	var facId;
	var msaCode = "";
	var emissionsType = "";
	var tribalLandId = "";
	var overlayLevel = 0;
	$("#parentState").val("");
	$("#facOrLocInput").val("Find a Facility or Location");
	$("#countyState").val("");
	$("#countyName").val("");
	if (event.parameters['et'] != null) {
		emissionsType = event.parameters['et'];
		$("#emissionsType").val(emissionsType);
	}
	if (event.parameters['q'] != null) {
		facOrLoc = event.parameters['q'];
		$("#facOrLocInput").val(unescape(facOrLoc));
	}
	if (event.parameters['st'] != null) {
		stateAbbr = event.parameters['st'];
		if (state == "") {
			stateAbbr = "US";
		}
		$("#parentState").val(stateAbbr);
		if (dataSourceChange(dataSource)) {
			if (dataSource == "L") {
				$("#countyState").attr('disabled', 'disabled');
			} else {
				$("#countyState").attr('disabled', false);
			}
			if (dataSource == "F") {
				$("#parentState").attr('disabled', 'disabled');
				$("#parentState").val("");
				stateAbbr = "US";
			} else {
				$("#parentState").attr('disabled', false);
			}
		}
		if (stateChange($("#parentState").val()) || stateLevelChange(stateLevel)) {
			if (isState(abbreviationToState(stateAbbr))) {
				//Gets the list of counties or MSAs
				setStateLevel(ryear, stateAbbr, stateLevel, 0);
			}
		}
		if (event.parameters['fc'] != null) {
			fipsCode = addLeadingZeros(event.parameters['fc']);
			$("#countyState").val(fipsCode);
			$("#countyName").val($("#countyState option:selected").text());
			if (stateLevel == 1) {
				msaCode = fipsCode;
				fipsCode = "";
			}
		} else {
			if (isState(abbreviationToState(stateAbbr))) {
				var $tribe = $('#tribeSelectAndSearch');
				if (stateAbbr == "TL") {
					$tribe.show();
					if (event.parameters['tl'] != null) {
						tribalLandId = event.parameters['tl'];
						$("#tribe").val(tribalLandId);
					}
					$("#countySelectAndSearch").hide();
				} else {
					$tribe.hide();
					if (event.parameters['ds'] == "L") {
						$("#countyState").attr('style', 'float:left; display:inline').attr('disabled', 'disabled');
					} else {
						$("#countyState").attr('style', 'float:left; display:inline');
					}
					if (stateLevel == 0) {
						$("#countyLabel").html('Pick a County');
					} else if (stateLevel == 1) {
						$("#countyLabel").html('Pick a Metro Area');
					}
				}
				if (event.parameters['ds'] != "T") {
					if ($("#countyState").attr('disabled') != 'disabled' && $("#countyState").css('display') != 'none') {
						$('#countySelectAndSearch').show();
					}
				}
			} else {
				$("#countyState").attr('style', 'float:left; display:none');
				$('#tribeSelectAndSearch').hide();
				$("#parentState").attr('style', 'float:right; display:inline');
				$("#selectionLabel").html('Browse to a State');
				$("#countyLabel").html('');
				$('#countySelectAndSearch').hide();
			}
		}
	} else {
		stateAbbr = "US";
		$("#countyState").attr('style', 'float:left; display:none');
		$("#parentState").attr('style', 'float:right; display:inline');
		$("#selectionLabel").html('Browse to a State');
		$("#countyLabel").html('');
		$('#countySelectAndSearch').hide();
	}
	if (event.parameters['cn'] != null) {
		$("#countyName").val(unescape(event.parameters['cn']));
	}
	if (event.parameters['rs'] != null) {
		reportingStatus = event.parameters['rs'];
		var i = 0; //DEFAULT VALUE (ALL)
		if (reportingStatus == 'ORANGE') {
			i = 1;
		} else if (reportingStatus == 'RED') {
			i = 2;
		} else if (reportingStatus == 'GRAY') {
			i = 3;
		} else if (reportingStatus == 'BLACK') {
			i = 4;
		}
		$('#reportingStatus').ddslick('select', {index: i});
	}
	if (event.parameters['lowE'] != null) {
		lowE = event.parameters['lowE'];
		$("#lowEmissionRange").val(lowE);
	}
	if (event.parameters['highE'] != null) {
		highE = event.parameters['highE'];
		$("#highEmissionRange").val(highE);
	}
	if (event.parameters['si'] != null) {
		si = event.parameters['si'];
		$("#sectorId").val(si);
	}
	if (event.parameters['ss'] != null) {
		ss = event.parameters['ss'];
		$("#subsectorName").val(ss);
	}
	if (event.parameters['g1'] != null) {
		if (event.parameters['g1'] == "1") {
			$("#gas1check").attr('checked', true);
		} else {
			$("#gas1check").attr('checked', false);
		}
	}
	if (event.parameters['g2'] != null) {
		if (event.parameters['g2'] == "1") {
			$("#gas2check").attr('checked', true);
		} else {
			$("#gas2check").attr('checked', false);
		}
	}
	if (event.parameters['g3'] != null) {
		if (event.parameters['g3'] == "1") {
			$("#gas3check").attr('checked', true);
		} else {
			$("#gas3check").attr('checked', false);
		}
	}
	if (event.parameters['g4'] != null) {
		if (event.parameters['g4'] == "1") {
			$("#gas4check").attr('checked', true);
		} else {
			$("#gas4check").attr('checked', false);
		}
	}
	if (event.parameters['g5'] != null) {
		if (event.parameters['g5'] == "1") {
			$("#gas5check").attr('checked', true);
		} else {
			$("#gas5check").attr('checked', false);
		}
	}
	if (event.parameters['g6'] != null) {
		if (event.parameters['g6'] == "1") {
			$("#gas6check").attr('checked', true);
		} else {
			$("#gas6check").attr('checked', false);
		}
	}
	if (event.parameters['g7'] != null) {
		if (event.parameters['g7'] == "1") {
			$("#gas7check").attr('checked', true);
		} else {
			$("#gas7check").attr('checked', false);
		}
	}
	if (event.parameters['g8'] != null) {
		if (event.parameters['g8'] == "1") {
			$("#gas8check").attr('checked', true);
		} else {
			$("#gas8check").attr('checked', false);
		}
	}
	if (event.parameters['g9'] != null) {
		if (event.parameters['g9'] == "1") {
			$("#gas9check").attr('checked', true);
		} else {
			$("#gas9check").attr('checked', false);
		}
	}
	if (event.parameters['g10'] != null) {
		if (event.parameters['g10'] == "1") {
			$("#gas10check").attr('checked', true);
		} else {
			$("#gas10check").attr('checked', false);
		}
	}
	if (event.parameters['s1'] != null) {
		if (event.parameters['s1'] == "1") {
			$("#sector1checkbox").attr('checked', true);
		} else {
			$("#sector1checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s2'] != null) {
		if (event.parameters['s2'] == "1") {
			$("#sector2checkbox").attr('checked', true);
		} else {
			$("#sector2checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s3'] != null) {
		if (event.parameters['s3'] == "1") {
			$("#sector3checkbox").attr('checked', true);
		} else {
			$("#sector3checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s4'] != null) {
		if (event.parameters['s4'] == "1") {
			$("#sector4checkbox").attr('checked', true);
		} else {
			$("#sector4checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s5'] != null) {
		if (event.parameters['s5'] == "1") {
			$("#sector5checkbox").attr('checked', true);
		} else {
			$("#sector5checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s6'] != null) {
		if (event.parameters['s6'] == "1") {
			$("#sector6checkbox").attr('checked', true);
		} else {
			$("#sector6checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s7'] != null) {
		if (event.parameters['s7'] == "1") {
			$("#sector7checkbox").attr('checked', true);
		} else {
			$("#sector7checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s8'] != null) {
		if (event.parameters['s8'] == "1") {
			$("#sector8checkbox").attr('checked', true);
		} else {
			$("#sector8checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s9'] != null) {
		if (event.parameters['s9'] == "1") {
			$("#sector9checkbox").attr('checked', true);
		} else {
			$("#sector9checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s10'] != null) {
		if (event.parameters['s10'] == "1") {
			$("#sector10checkbox").attr('checked', true);
		} else {
			$("#sector10checkbox").attr('checked', false);
		}
	}
	if (event.parameters['s201'] != null) {
		if (event.parameters['s201'] == "1") {
			$("#waste1").attr('checked', true);
		} else {
			$("#waste1").attr('checked', false);
		}
	}
	if (event.parameters['s202'] != null) {
		if (event.parameters['s202'] == "1") {
			$("#waste2").attr('checked', true);
		} else {
			$("#waste2").attr('checked', false);
		}
	}
	if (event.parameters['s203'] != null) {
		if (event.parameters['s203'] == "1") {
			$("#waste3").attr('checked', true);
		} else {
			$("#waste3").attr('checked', false);
		}
	}
	if (event.parameters['s204'] != null) {
		if (event.parameters['s204'] == "1") {
			$("#waste4").attr('checked', true);
		} else {
			$("#waste4").attr('checked', false);
		}
	}
	if (event.parameters['s301'] != null) {
		if (event.parameters['s301'] == "1") {
			$("#metal1").attr('checked', true);
		} else {
			$("#metal1").attr('checked', false);
		}
	}
	if (event.parameters['s302'] != null) {
		if (event.parameters['s302'] == "1") {
			$("#metal2").attr('checked', true);
		} else {
			$("#metal2").attr('checked', false);
		}
	}
	if (event.parameters['s303'] != null) {
		if (event.parameters['s303'] == "1") {
			$("#metal3").attr('checked', true);
		} else {
			$("#metal3").attr('checked', false);
		}
	}
	if (event.parameters['s304'] != null) {
		if (event.parameters['s304'] == "1") {
			$("#metal4").attr('checked', true);
		} else {
			$("#metal4").attr('checked', false);
		}
	}
	if (event.parameters['s305'] != null) {
		if (event.parameters['s305'] == "1") {
			$("#metal5").attr('checked', true);
		} else {
			$("#metal5").attr('checked', false);
		}
	}
	if (event.parameters['s306'] != null) {
		if (event.parameters['s306'] == "1") {
			$("#metal6").attr('checked', true);
		} else {
			$("#metal6").attr('checked', false);
		}
	}
	if (event.parameters['s307'] != null) {
		if (event.parameters['s307'] == "1") {
			$("#metal7").attr('checked', true);
		} else {
			$("#metal7").attr('checked', false);
		}
	}
	if (event.parameters['s401'] != null) {
		if (event.parameters['s401'] == "1") {
			$("#mineral1").attr('checked', true);
		} else {
			$("#mineral1").attr('checked', false);
		}
	}
	if (event.parameters['s402'] != null) {
		if (event.parameters['s402'] == "1") {
			$("#mineral2").attr('checked', true);
		} else {
			$("#mineral2").attr('checked', false);
		}
	}
	if (event.parameters['s403'] != null) {
		if (event.parameters['s403'] == "1") {
			$("#mineral3").attr('checked', true);
		} else {
			$("#mineral3").attr('checked', false);
		}
	}
	if (event.parameters['s404'] != null) {
		if (event.parameters['s404'] == "1") {
			$("#mineral4").attr('checked', true);
		} else {
			$("#mineral4").attr('checked', false);
		}
	}
	if (event.parameters['s405'] != null) {
		if (event.parameters['s405'] == "1") {
			$("#mineral5").attr('checked', true);
		} else {
			$("#mineral5").attr('checked', false);
		}
	}
	if (event.parameters['s601'] != null) {
		if (event.parameters['s601'] == "1") {
			$("#pulp1").attr('checked', true);
		} else {
			$("#pulp1").attr('checked', false);
		}
	}
	if (event.parameters['s602'] != null) {
		if (event.parameters['s602'] == "1") {
			$("#pulp2").attr('checked', true);
		} else {
			$("#pulp2").attr('checked', false);
		}
	}
	if (event.parameters['s701'] != null) {
		if (event.parameters['s701'] == "1") {
			$("#chem1").attr('checked', true);
		} else {
			$("#chem1").attr('checked', false);
		}
	}
	if (event.parameters['s702'] != null) {
		if (event.parameters['s702'] == "1") {
			$("#chem2").attr('checked', true);
		} else {
			$("#chem2").attr('checked', false);
		}
	}
	if (event.parameters['s703'] != null) {
		if (event.parameters['s703'] == "1") {
			$("#chem3").attr('checked', true);
		} else {
			$("#chem3").attr('checked', false);
		}
	}
	if (event.parameters['s704'] != null) {
		if (event.parameters['s704'] == "1") {
			$("#chem4").attr('checked', true);
		} else {
			$("#chem4").attr('checked', false);
		}
	}
	if (event.parameters['s705'] != null) {
		if (event.parameters['s705'] == "1") {
			$("#chem5").attr('checked', true);
		} else {
			$("#chem5").attr('checked', false);
		}
	}
	if (event.parameters['s706'] != null) {
		if (event.parameters['s706'] == "1") {
			$("#chem6").attr('checked', true);
		} else {
			$("#chem6").attr('checked', false);
		}
	}
	if (event.parameters['s707'] != null) {
		if (event.parameters['s707'] == "1") {
			$("#chem7").attr('checked', true);
		} else {
			$("#chem7").attr('checked', false);
		}
	}
	if (event.parameters['s708'] != null) {
		if (event.parameters['s708'] == "1") {
			$("#chem8").attr('checked', true);
		} else {
			$("#chem8").attr('checked', false);
		}
	}
	if (event.parameters['s709'] != null) {
		if (event.parameters['s709'] == "1") {
			$("#chem9").attr('checked', true);
		} else {
			$("#chem9").attr('checked', false);
		}
	}
	if (event.parameters['s710'] != null) {
		if (event.parameters['s710'] == "1") {
			$("#chem10").attr('checked', true);
		} else {
			$("#chem10").attr('checked', false);
		}
	}
	if (event.parameters['s711'] != null) {
		if (event.parameters['s711'] == "1") {
			$("#chem11").attr('checked', true);
		} else {
			$("#chem11").attr('checked', false);
		}
	}
	if (event.parameters['s801'] != null) {
		if (event.parameters['s801'] == "1") {
			$("#other1").attr('checked', true);
		} else {
			$("#other1").attr('checked', false);
		}
	}
	if (event.parameters['s802'] != null) {
		if (event.parameters['s802'] == "1") {
			$("#other2").attr('checked', true);
		} else {
			$("#other2").attr('checked', false);
		}
	}
	if (event.parameters['s803'] != null) {
		if (event.parameters['s803'] == "1") {
			$("#other3").attr('checked', true);
		} else {
			$("#other3").attr('checked', false);
		}
	}
	if (event.parameters['s804'] != null) {
		if (event.parameters['s804'] == "1") {
			$("#other4").attr('checked', true);
		} else {
			$("#other4").attr('checked', false);
		}
	}
	if (event.parameters['s805'] != null) {
		if (event.parameters['s805'] == "1") {
			$("#other5").attr('checked', true);
		} else {
			$("#other5").attr('checked', false);
		}
	}
	if (event.parameters['s806'] != null) {
		if (event.parameters['s806'] == "1") {
			$("#other6").attr('checked', true);
		} else {
			$("#other6").attr('checked', false);
		}
	}
	if (event.parameters['s807'] != null) {
		if (event.parameters['s807'] == "1") {
			$("#other7").attr('checked', true);
		} else {
			$("#other7").attr('checked', false);
		}
	}
	if (event.parameters['s808'] != null) {
		if (event.parameters['s808'] == "1") {
			$("#other8").attr('checked', true);
		} else {
			$("#other8").attr('checked', false);
		}
	}
	if (event.parameters['s809'] != null) {
		if (event.parameters['s809'] == "1") {
			$("#other9").attr('checked', true);
		} else {
			$("#other9").attr('checked', false);
		}
	}
	if (event.parameters['s810'] != null) {
		if (event.parameters['s810'] == "1") {
			$("#other10").attr('checked', true);
		} else {
			$("#other10").attr('checked', false);
		}
	}
	if (event.parameters['s901'] != null) {
		if (event.parameters['s901'] == "1") {
			$("#petroleum1").attr('checked', true);
		} else {
			$("#petroleum1").attr('checked', false);
		}
	}
	if (event.parameters['s902'] != null) {
		if (event.parameters['s902'] == "1") {
			$("#petroleum2").attr('checked', true);
		} else {
			$("#petroleum2").attr('checked', false);
		}
	}
	if (event.parameters['s903'] != null) {
		if (event.parameters['s903'] == "1") {
			$("#petroleum3").attr('checked', true);
		} else {
			$("#petroleum3").attr('checked', false);
		}
	}
	if (event.parameters['s904'] != null) {
		if (event.parameters['s904'] == "1") {
			$("#petroleum4").attr('checked', true);
		} else {
			$("#petroleum4").attr('checked', false);
		}
	}
	if (event.parameters['s905'] != null) {
		if (event.parameters['s905'] == "1") {
			$("#petroleum5").attr('checked', true);
		} else {
			$("#petroleum5").attr('checked', false);
		}
	}
	if (event.parameters['s906'] != null) {
		if (event.parameters['s906'] == "1") {
			$("#petroleum6").attr('checked', true);
		} else {
			$("#petroleum6").attr('checked', false);
		}
	}
	if (event.parameters['s907'] != null) {
		if (event.parameters['s907'] == "1") {
			$("#petroleum7").attr('checked', true);
		} else {
			$("#petroleum7").attr('checked', false);
		}
	}
	if (event.parameters['s908'] != null) {
		if (event.parameters['s908'] == "1") {
			$("#petroleum8").attr('checked', true);
		} else {
			$("#petroleum8").attr('checked', false);
		}
	}
	if (event.parameters['s909'] != null) {
		if (event.parameters['s909'] == "1") {
			$("#petroleum9").attr('checked', true);
		} else {
			$("#petroleum9").attr('checked', false);
		}
	}
	if (event.parameters['s910'] != null) {
		if (event.parameters['s910'] == "1") {
			$("#petroleum10").attr('checked', true);
		} else {
			$("#petroleum10").attr('checked', false);
		}
	}
	if (event.parameters['s911'] != null) {
		if (event.parameters['s911'] == "1") {
			$("#petroleum11").attr('checked', true);
		} else {
			$("#petroleum11").attr('checked', false);
		}
	}
	if (event.parameters['sf'] != null) {
		if (event.parameters['sf'].substring(0, 1) == "1") {
			$("#advSearch_name").attr('checked', true);
		} else {
			$("#advSearch_name").attr('checked', false);
		}
		if (event.parameters['sf'].substring(1, 2) == "1") {
			$("#advSearch_city").attr('checked', true);
		} else {
			$("#advSearch_city").attr('checked', false);
		}
		if (event.parameters['sf'].substring(2, 3) == "1") {
			$("#advSearch_county").attr('checked', true);
		} else {
			$("#advSearch_county").attr('checked', false);
		}
		if (event.parameters['sf'].substring(3, 4) == "1") {
			$("#advSearch_state").attr('checked', true);
		} else {
			$("#advSearch_state").attr('checked', false);
		}
		if (event.parameters['sf'].substring(4, 5) == "1") {
			$("#advSearch_zipcode").attr('checked', true);
		} else {
			$("#advSearch_zipcode").attr('checked', false);
		}
		if (event.parameters['sf'].substring(5, 6) == "1") {
			$("#advSearch_facId").attr('checked', true);
		} else {
			$("#advSearch_facId").attr('checked', false);
		}
		if (event.parameters['sf'].substring(6, 7) == "1") {
			$("#advSearch_naicsCode").attr('checked', true);
		} else {
			$("#advSearch_naicsCode").attr('checked', false);
		}
		if (event.parameters['sf'].substring(7, 8) == "1") {
			$("#advSearch_corpParent").attr('checked', true);
		} else {
			$("#advSearch_corpParent").attr('checked', false);
		}
	}
	if (event.parameters['fid'] != null) {
		facId = event.parameters['fid'];
		$("#facilityId").val(facId);
	}
	if (event.parameters['bs'] != null) {
		if (event.parameters['ds'] == 'O' || event.parameters['ds'] == 'B') {
			basinCode = event.parameters['bs'];
			$("#basin").val(unescape(event.parameters['bs']));
		} else {
			$("#basin").val("");
		}
	} else {
		$("#basin").val("");
	}
	if (event.parameters['tr'] != null) {
		if (event.parameters['tr'] == "trend" && (event.parameters['ds'] == 'E' || event.parameters['ds'] == 'P')) {
			trendSelection = 'trend';
		}
	}
	if (event.parameters['ol'] != null) {
		overlayLevel = event.parameters['ol'];
		$("#selectedOverlay").val(overlayLevel);
		if (overlayLevel == 1) {
			App.showEmissionsRangePopover('offset');
			if (visType == "map") {
				//OurMap.Overlay.clear();
				//getMapOverlayData('bubble');
			}
		}
	}
	if (event.parameters['sl'] != null) {
		stateLevel = event.parameters['sl'];
		$("#selectedLevel").val(stateLevel);
	}
	var whatIcon = '<br/><br/>'
		+ '<p><img align="middle" src="img/icon_marker/factory_black.png"> 		All Direct Emitters and Point Sources</p>'
		+ '<p><img align="middle" src="img/icon_marker/derrick_black.png"> 		Onshore Petroleum Natural Gas Production and Gathering & Boosting</p>'
		+ '<p><img align="middle" src="img/icon_marker/pipeline_black.png"> 	Local Distribution Companies and Onshore Gas Transmission Pipelines</p>'
		+ '<p><img align="middle" src="img/icon_marker/electricity_black.png"> 	SF6 from Electrical Transmission and Distribution Equipment Use</p>'
		+ '<p><img align="middle" src="img/icon_marker/fuel_black.png"> 		Suppliers</p>'
		+ '<p><img align="middle" src="img/icon_marker/co2_black.png"> 			CO2 Injection (UU)</p>'
		+ '<p><img align="middle" src="img/icon_marker/sequestration_black.png"> Geologic Sequestration of CO2 (RR)</p>'
	;
	var whatData = "The data was reported to EPA by facilities as of " + dataDate + ".  EPA continues to quality assure data and plans to release updated data periodically.<br/><br/>" +
		"<a href='" + dsUrl + "' target='_blank'>Learn more</a> about what is included in this data set and view related EPA GHG data sources.<br/><br/>" +
		"Visit our <a href='" + helplinkurlbase + "' target='_blank'>help section</a> to learn about the features of FLIGHT and the data collected by the GHGRP."
		+ whatIcon;
	$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions from Large Facilities');
	if (event.parameters['ds'] != null) {
		if (event.parameters['ds'] == 'E' || event.parameters['ds'] == 'P') {
			$("#emissionsTypeSelectAndSearch").css('display', 'inline');
		} else {
			$("#emissionsTypeSelectAndSearch").css('display', 'none');
			$("#emissionsType").val('');
		}
		if (event.parameters['ds'] == 'S') {
			$("#ry2010").show();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Product Suppliers');
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'none');
			$("#emitterDiv2").attr('style', 'display:none');
			$("#rollupsDiv").attr('style', 'display:none');
			$("#sectorSelection").attr('style', 'margin-left: 5px; display:inline');
			$("#injectionSelection").attr('style', 'display:none');
			$("#countySelectAndSearch").hide();
			$("#basin").attr('style', 'float:right;display:none');
			$("#bar1").attr('background', 'img/blue_bar_bg.gif');
			$("#bar2").attr('background', 'img/blue_bar_bg.gif');
			$("#share").removeClass('green_button').addClass('blue_button');
			greenButtonOff('share');
			setSupplierSector();
			$("#whatsThisText").html("Suppliers are facilities or entities that supply certain products (e.g., fossil fuels or certain industrial gases) into " +
				"the economy that, when combusted, released, or oxidized, result in GHG emisisons.  The emissions do not " +
				"take place at the suppliers' reporting location.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'O') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions from Onshore Petroleum and Natural Gas Production');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			$("#countyState").attr('style', 'float:left; display:none');
			$("#parentState").attr('style', 'float:right; display:none');
			$("#basin").attr('style', 'float:right; display:inline');
			$("#selectionLabel").html('Browse to a Basin');
			jQuery.getJSON("service/basins", function (data) {
				htmlStr = "";
				htmlStr += "<option value =''>Choose Basin</option>";
				for (var i = 0; i < data.length; i++) {
					htmlStr += "<option value ='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$("#basin").html(htmlStr);
				if (event.parameters['bs'] != null) {
					$("#basin").val(unescape(event.parameters['bs']));
				}
			});
			$("#whatsThisText").html("Owners or operators engaged in Onshore Oil and Gas Production report aggregated GHG emissions in each basin where they operate and emissions exceed the reporting threshold.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'B') {
			whichDataYear(0);
			$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions from Onshore Oil & Gas Gathering & Boosting');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			$("#countyState").attr('style', 'float:left; display:none');
			$("#parentState").attr('style', 'float:right; display:none');
			$("#basin").attr('style', 'float:right; display:inline');
			$("#selectionLabel").html('Browse to a Basin');
			jQuery.getJSON("service/basins", function (data) {
				htmlStr = "";
				htmlStr += "<option value =''>Choose Basin</option>";
				for (var i = 0; i < data.length; i++) {
					htmlStr += "<option value ='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$("#basin").html(htmlStr);
				if (event.parameters['bs'] != null) {
					$("#basin").val(unescape(event.parameters['bs']));
				}
			});
			$("#whatsThisText").html("Owners or operators engaged in Onshore Oil & Gas Gathering & Boosting report aggregated GHG emissions in each basin where they operate and emissions exceed the reporting threshold.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'L') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions Natural Gas Local Distribution Companies');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			//$("#countySelectAndSearch").hide();
			$("#basin").attr('style', 'float:right; display:none');
			$("#whatsThisText").html("Natural gas distribution companies that exceed the reporting threshold report aggregated GHG emissions in " +
				"each state they operate in.  These emissions are from fugitive leaks.  GHG data associated with natural gas " +
				"supplied by local distribution companies to end users is available in the suppliers portion of this tool.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'T') {
			whichDataYear(0);
			$("#dashboardType").html(cyear + ' Onshore Gas Transmission Pipelines');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			$("#basin").attr('style', 'float:right; display:none');
			$("#whatsThisText").html("Owners or operators of Onshore Gas Transmission Pipelines report aggregated pipeline blowdown GHG emissions nationally (and for each state) if emissions exceed the reporting threshold.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'F') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions from Use of Electrical Equipment');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			$("#countySelectAndSearch").hide();
			$("#vis").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
			if (reportingStatus != 'RED' && reportingStatus != 'GRAY') {
				$("#vBar").show();
				$("#vPie").show();
				$("#barLabel").show();
				$("#pieLabel").show();
			}
			if (reportingStatus == 'ALL') {
				$("#vLine").show();
				$("#lineLabel").show();
			}
			$("#basin").attr('style', 'float:right; display:none');
			$("#whatsThisText").html("Owners or operators of electric power system facilities that exceed the reporting threshold report agregated GHG emission across the power system.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'P') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Greenhouse Gas Emissions from Point Sources');
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#selectionPanel").hide();
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			if (reportingStatus != 'RED' && reportingStatus != 'GRAY') {
				$("#vBar").show();
				$("#vPie").show();
				$("#barLabel").show();
				$("#pieLabel").show();
			}
			if (reportingStatus == 'ALL') {
				$("#vLine").show();
				$("#lineLabel").show();
			}
			$("#basin").attr('style', 'float:right; display:none');
			$("#whatsThisText").html('Facilities that emit 25,000 metric tons or more per year of GHGs are required to annually report their GHG emissions to EPA.  ' +
				'Roughly half of total U.S. GHG emissions are reportetd by direct emitters.  The facilities displayed in this view are a subset of all direct emitters that report,' +
				'those reporting emissions from a traditional facility or "point source".  Other emitters not shown report emissions over broader spatial areas.<br/><br/>' +
				whatData);
		} else if (event.parameters['ds'] == 'I') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' CO2 Injection (UU)');
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'none');
			$("#emitterDiv2").attr('style', 'display:none');
			$("#rollupsDiv").attr('style', 'display:none');
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'margin-left: 5px; display:inline');
			$("#basin").attr('style', 'float:right;display:none');
			setInjectionSelection();
			$("#whatsThisText").html("Facilities injecting CO2 underground that exceed the reporting threshold report the total quantity of CO2 received for injection.  The quantity of " +
				"CO2 received for injection by each reporter is confidential except for facilities with an EPA-approved R&amp;D project exemption for greenhouse gas reporting of geologic sequestration of carbon dioxide.<br/><br/>" +
				whatData);
		} else if (event.parameters['ds'] == 'A') {
			$("#ry2010").hide();
			whichDataYear(1);
			$("#dashboardType").html(cyear + ' Geologic Sequestration of CO2 (RR)');
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'none');
			$("#emitterDiv2").attr('style', 'display:none');
			$("#rollupsDiv").attr('style', 'display:none');
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			$("#basin").attr('style', 'float:right;display:none');
		} else {
			$("#ry2010").show();
			whichDataYear(1);
			$("#emitterDiv2").attr('style', 'display:block');
			$("#rollupsDiv").attr('style', 'display:block');
			$("#sectorSelection").attr('style', 'display:none');
			$("#injectionSelection").attr('style', 'display:none');
			if (reportingStatus == 'ALL') {
				$("#vLine").show();
				$("#lineLabel").show();
			}
			if (reportingStatus != 'RED' && reportingStatus != 'GRAY') {
				$("#vBar").show();
				$("#vPie").show();
				$("#barLabel").show();
				$("#pieLabel").show();
			}
			if ((isOnshoreOnly() || event.parameters['ss'] == 'Onshore Petroleum ') && (ryear == 2011 || ryear == 2012) && (event.parameters['yr'] == null || event.parameters['yr'] == '2011' || event.parameters['yr'] == '2012')) {
				$("#countyState").attr('style', 'float:left; display:none');
				$("#parentState").attr('style', 'float:right; display:none');
				$("#basin").attr('style', 'float:right; display:inline');
				$("#selectionLabel").html('Browse to a Basin');
				jQuery.getJSON("service/basins", function (data) {
					htmlStr = "";
					htmlStr += "<option value =''>Choose Basin</option>";
					for (var i = 0; i < data.length; i++) {
						htmlStr += "<option value ='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#basin").html(htmlStr);
					if (event.parameters['bs'] != null) {
						$("#basin").val(unescape(event.parameters['bs']));
					} else {
						$("#basin").val("");
					}
				});
			} else {
				$("#selectionPanel").hide();
				$("#basin").attr('style', 'float:right; display:none');
			}
			$("#whatsThisText").html("Facilities that emit 25,000 metric tons or more per year of GHGs are required to annually report their GHG emissions to EPA.  " +
				"Roughly half of total U.S. GHG emissions are reported by direct emitters.<br/><br/>" +
				whatData);
			$("#bar1").attr('background', 'img/green_bar_bg.gif');
			$("#bar2").attr('background', 'img/green_bar_bg.gif');
			$("#share").removeClass('blue_button').addClass('green_button');
			greenButtonOff('share');
		}
		dataSource = event.parameters['ds'];
		$('#home').attr('href', 'javascript:refreshView(dataSource)');
		$("#dataType").val(dataSource);
	} else {
		$("#whatsThisText").html("Facilities that emit 25,000 metric tons or more per year of GHGs are required to annually report their GHG emissions to EPA.  " +
			"Roughly half of total U.S. GHG emissions are reported by direct emitters.<br/><br/>" +
			whatData);
	}
	if (event.parameters['yr'] != null) {
		ryear = parseInt(event.parameters['yr']);
		$("#emissionYear").html(ryear);
		$("#reportingYear").val(ryear);
	}
	if (ryear == 2010) {
		$("#vLine").hide(); //hide trend tool for reporing year 2010
		$("#lineLabel").hide();
		$("#lichem3").hide();
		$("#liother1").hide();
		$("#liother2").hide();
		$("#liother7").hide();
		$("#liother8").hide();
		$("#liother9").hide();
		$("#limetal5").hide();
		$("#liwaste2").hide();
		$("#liwaste3").hide();
		$("#opt_onshore").hide();
		$("#opt_ldc").hide();
		$("#opt_co2injection").hide();
		$("#opt_rr").hide();
		$("#fghg1").hide();
		$("#fghg_import1").hide();
		$("#fghg_export1").hide();
		$("#fghg2").hide();
		$("#fghg_import2").hide();
		$("#fghg_export2").hide();
		if (dataSource == "S") {
			$("#yearChangeText").hide();
		}
		$(".li_petro").hide();
		if (dataSource == "E") {
			$("#petroleum9").attr('checked', true);
			if (!yearNoteShown) {
				$("#yearChangeText").show();
				yearNoteShown = true;
			}
		}
		$(".blueflag").show();
	} else {
		$("#lichem3").show();
		$("#liother1").show();
		$("#liother2").show();
		$("#liother7").show();
		$("#liother8").show();
		$("#liother9").show();
		$("#limetal5").show();
		$("#liwaste2").show();
		$("#liwaste3").show();
		$("#opt_onshore").show();
		$("#opt_boosting").show();
		$("#opt_ldc").show();
		$("#opt_transmission").show();
		$("#opt_co2injection").show();
		$("#opt_rr").show();
		$("#fghg1").show();
		$("#fghg_import1").show();
		$("#fghg_export1").show();
		$("#fghg2").show();
		$("#fghg_import2").show();
		$("#fghg_export2").show();
		$(".li_petro").show();
		$(".blueflag").hide();
		$("#yearChangeText").hide();
		$("#wSub9").removeAttr('style');
		$("#wSub10").removeAttr('style');
		if (ryear < 2016) { // hiding 2 new W9 and W10 subsectors
			$("#opt_boosting").hide();
			$("#opt_transmission").hide();
			$("#wSub9").hide();
			$("#wSub10").hide();
		}
	}
	determineTotalCheckbox10Select();
	if (event.parameters['so'] != null) {
		sortOrder = event.parameters['so'];
		if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'I' || dataSource == 'A' || dataSource == 'B' || dataSource == 'T') {
			emitterSort = sortOrder;
		} else {
			supplierSort = sortOrder;
		}
	}
	if (unescape(facOrLoc) == "Find a Facility or Location") {
		facOrLoc = "";
	}
	//Check to see if params have changed, if so, run queryTable(0)
	if (updatedParams(stateAbbr, facOrLoc, fipsCode, msaCode, lowE, highE, sortOrder, reportingStatus, emissionsType, tribalLandId)) {
		queryTable(0, facOrLoc, fipsCode, stateAbbr, msaCode, basinCode, tribalLandId, lowE, highE, sortOrder, reportingStatus, emissionsType);
	}
	var vOrangeflag = '<img src="img/orangeflag.png" height="24" width="24" align="absmiddle" alt="orange flag image">';
	var flagTxtState  = vOrangeflag + 'Emissions totals displayed at the state level exclude Onshore Oil and Gas Production, Onshore Oil and Gas Gathering and Boosting and Use of Electrical Equipment';
	var flagTxtCounty = vOrangeflag + 'Emissions totals displayed at the county level exclude Onshore Oil and Gas Production, Onshore Oil and Gas Gathering and Boosting, Onshore Gas Transmission Pipelines, Natural Gas Local Distribution Companies and Use of Electrical Equipment';
	var flagTxtMetro  = vOrangeflag + 'Emissions totals displayed at the metro area level exclude Onshore Oil and Gas Production, Onshore Oil and Gas Gathering and Boosting, Onshore Gas Transmission Pipelines, Natural Gas Local Distribution Companies and Use of Electrical Equipment';
	if (fipsCode != "" || msaCode != "") {
		$("#note_state").show();
		if (fipsCode != "") {
			$("#note_state").html(flagTxtCounty);
		}
		if (msaCode != "") {
			$("#note_state").html(flagTxtMetro);
		}
	} else if (isState(abbreviationToState(stateAbbr)) != 0) {
		$("#note_state").show();
		if (visType == "list") {
			if (listSelector == 0 && dataSource == 'E') {
				if (fipsCode != "") {
					$("#note_state").html(flagTxtCounty);
				}
				if (msaCode != "") {
					$("#note_state").html(flagTxtMetro);
				}
			} else {
				$("#note_state").html(flagTxtState);
			}
		} else if (visType == "bar") {
			if (barSelector == 2 && dataSource == 'E') {
				if (fipsCode != "") {
					$("#note_state").html(flagTxtCounty);
				}
				if (msaCode != "") {
					$("#note_state").html(flagTxtMetro);
				}
			} else {
				$("#note_state").html(flagTxtState);
			}
		} else {
			$("#note_state").html(flagTxtState);
		}
	} else {
		$("#note_state").hide();
	}
	$("#table-label").hide();
	if (event.pathNames == "" || event.pathNames == "facility" || event.parameters['ds'] == null) {  //NEED TO HAVE IT ZOOM TO STATE LEVEL
		$("#usa").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:inline')
		$("#vis").attr('style', 'display:none')
		$("#facility").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#intensity").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#selectorWidgets").css('display', 'none');
		$("#titleDiv").attr('style', 'display:none');
		visType = 'map';
		Events.publish('canvas-switch', ['map']);
		mapSelector = 0;
		if (!loaded) {
			loadScript();
			loaded = true;
		} else {
			drawMap(facOrLoc, abbreviationToState(stateAbbr), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
	} else if (event.pathNames == "intensity") { //DONE
		$("#usa").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:inline')
		$("#vis").attr('style', 'display:none')
		$("#facility").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#intensity").removeClass('softBtnRight').addClass('softBtnRightOn');
		visType = 'map';
		mapSelector = 1;
		if (!loaded) {
			loadScript();
			loaded = true;
		} else {
			drawMap(facOrLoc, abbreviationToState(stateAbbr), fipsCode, msaCode, lowE, highE, reportingStatus, tribalLandId);
		}
		// List
	} else if (event.pathNames == "listSector") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").show();
		$("#geoSec").html('Geography').attr("title", 'Geography');
		$("#geoFac").html('Facility').attr("title", 'Facility');
		$("#trendSelector").attr('style', 'display:inline');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#titleDiv").attr('style', 'padding: 5px; font-weight: bold');
		$("#sector").attr('style', 'margin-left: 5px; display:inline');
		$("#trendsBtn").attr('style', 'display:inline; margin-right:15px');
		$("#table-label").empty();
		$("#table-label").attr('style', 'display:inline');
		if (dataSource != 'I' && dataSource != 'A') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		if (dataSource == 'S') {
			$("#vis").attr('style', 'display:none');
			if (reportingStatus != "RED" && reportingStatus != "GREY") {
				$("a#vPie").show();
				$("a#vBar").show();
				$("#pieLabel").show();
				$("#barLabel").show();
				hasTrend(supplierSector, false);
			}
			//$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
			//$("#sector").attr('style','display:none');
			//$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		}
		if (dataSource == 'E' || dataSource == 'P') {
			$("#selectionPanel").css('display', 'inline');
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:inline');
			$("#trendSelector").attr('style', 'display:none');
			$("#map").attr('style', 'display:none');
			if (stateAbbr == "" || stateAbbr == 'TL') {
				$("#stateLevelSelector").attr('style', 'display:none');
			} else {
				$("#stateLevelSelector").attr('style', 'display:inline');
				$("#msaWarning").attr('style', 'display:inline; color: darkgray; padding-left:5px; height:29px; line-height:29px;');
				if (stateLevel == 0) {
					$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
					$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				} else {
					$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
					$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				}
			}
		}
		if (event.parameters['tr'] != null) {
			if (event.parameters['tr'] == "trend" && (dataSource == 'E' || dataSource == 'P')) {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
				trendSelection = 'current';
			} else if (event.parameters['tr'] == "trend" && dataSource == 'L' && listSelector == 0) {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
				trendSelection = 'current';
			} else if (event.parameters['tr'] == "trend") {
				$("#currentYearBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
				$("#trendsBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				trendSelection = 'trend';
			} else {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
			}
		}
		if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T') {
			$("#trendSelector").attr('style', 'display:none');
		}
		visType = 'list';
		listSelector = 0;
		//stateAbbr cannot = "" or else it will give a 404 error when @RequestMapping tries to retrieve its value
		if (stateAbbr == "") {
			stateAbbr = "US";
		}
		listSector(facOrLoc, stateAbbr, fipsCode, msaCode, stateLevel, facId, lowE, highE, cyear, reportingStatus, trendSelection, event.parameters['ds'], event.parameters['et'], tribalLandId);
	} else if (event.pathNames == "listGas") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Geography').attr("title", 'Geography');
		$("#geoFac").html('Facility').attr("title", 'Facility');
		$("#trendSelector").attr('style', 'display:inline');
		$("#trendsBtn").attr('style', 'display:inline; margin-right:15px');
		$("#table-label").empty();
		$("#table-label").attr('style', 'display:inline');
		if (dataSource != 'I' && dataSource != 'A') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		visType = 'list';
		listSelector = 1;
		listGas(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
	} else if (event.pathNames == "listFacility") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#trendSelector").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Geography').attr("title", 'Geography');
		$("#geoFac").html('Facility').attr("title", 'Facility');
		$("#sector").attr('style', 'margin-left: 5px; display:inline');
		$("#trendsBtn").attr('style', 'display:inline; margin-right:15px');
		$("#table-label").empty();
		$("#table-label").attr('style', 'display:inline');
		if (dataSource != 'I' && dataSource != 'A') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		if (dataSource == 'I' && stateAbbr != "") {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:none');
			$("#trendSelector").attr('style', 'display:none');
		}
		if ($("#parentState").val() == "TL") {
			$("#trendSelector").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
		}
		if (stateAbbr != "" && stateAbbr != 'TL' && dataSource != 'L' && dataSource != 'T') {
			$("#stateLevelSelector").attr('style', 'display:inline');
			$("#msaWarning").attr('style', 'display:none');
		} else {
			$("#stateLevelSelector").attr('style', 'display:none');
		}
		if (dataSource == 'F') {
			$("#vis").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
			$("#parentState").attr('disabled', 'disabled');
		}
		visType = 'list';
		listSelector = 2;
		if (event.parameters['tr'] != null) {
			if (event.parameters['tr'] == "trend") {
				$("#currentYearBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
				$("#trendsBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				trendSelection = 'trend';
			} else {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
			}
		}
		if (stateLevel == 0) {
			$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
			$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		} else {
			$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
			$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
		}
		//stateAbbr cannot = "" or else it will give a 404 error when @RequestMapping tries to retrieve its value
		if (stateAbbr == "") {
			stateAbbr = "US";
		}
		listFacility(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, trendSelection, cyear, reportingStatus, event.parameters['ds'], emissionsType, tribalLandId);
	} else if (event.pathNames == "listFacilityForBasin") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Geography').attr("title", 'Geography');
		$("#geoFac").html('Facility').attr("title", 'Facility');
		$("#sector").attr('style', 'margin-left: 5px; display:inline');
		$("#trendSelector").attr('style', 'display:inline');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#trendsBtn").attr('style', 'display:inline; margin-right:15px');
		if (dataSource == 'S') {
			$("#vis").attr('style', 'display:none');
		}
		$("#table-label").empty();
		$("#table-label").attr('style', 'display:inline');
		if (dataSource != 'I' && dataSource != 'A') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		if ($("#parentState").val() == "TL") {
			$("#trendSelector").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
		}
		visType = 'list';
		listSelector = 2;
		if (event.parameters['tr'] != null) {
			if (event.parameters['tr'] == "trend") {
				$("#currentYearBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
				$("#trendsBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				trendSelection = 'trend';
			} else {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
			}
		}
		listFacilityForBasin(facOrLoc, basinCode, facId, lowE, highE, cyear, trendSelection, reportingStatus);
	} else if (event.pathNames == "listFacilityForBasinGeo") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Geography').attr("title", 'Geography');
		$("#geoFac").html('Facility').attr("title", 'Facility');
		$("#sector").attr('style', 'margin-left: 5px; display:inline');
		$("#trendSelector").attr('style', 'display:inline');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#trendsBtn").attr('style', 'display:inline; margin-right:15px');
		if (dataSource == 'S') {
			$("#vis").attr('style', 'display:none');
		}
		$("#table-label").empty();
		$("#table-label").attr('style', 'display:inline');
		if (dataSource != 'I' && dataSource != 'A') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		if ($("#parentState").val() == "TL") {
			$("#trendSelector").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
		}
		visType = 'list';
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#sector").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#trendSelector").attr('style', 'display:none');
		listFacilityForBasinGeo(facOrLoc, basinCode, facId, lowE, highE, cyear, trendSelection, reportingStatus);
		// Trend
	} else if (event.pathNames == "trend") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#table-label").attr('style', 'display:none');
		visType = 'line';
		if ((dataSource == 'E' || dataSource == 'P') && stateAbbr != "") {
			if (stateAbbr == "TL") {
				$("#selectionPanel").css('display', 'none');
			} else {
				$("#selectionPanel").css('display', 'inline');
			}
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:none');
			$("#trendSelector").attr('style', 'display:none');
			$("#map").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:inline');
			if (stateLevel == 0) {
				$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
				$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
			} else {
				$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
				$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
			}
		}
		if (dataSource == 'F') {
			$("#vis").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
			$("#parentState").attr('disabled', 'disabled');
		}
		//stateAbbr cannot = "" or else it will give a 404 error when @RequestMapping tries to retrieve its value
		if (stateAbbr == "") {
			stateAbbr = "US";
		}
		var sc = 0;
		if (dataSource == 'S') {
			sc = supplierSector;
		} else if (dataSource == 'I') {
			sc = injectionSelection;
		}
		chartTrend(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, sc, tribalLandId);
		// Bar
	} else if (event.pathNames == "barSector" || event.pathNames == "barSectorL2") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#trendSelector").attr('style', 'display:inline');
		$("#sector").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#titleDiv").attr('style', 'padding: 5px; font-weight: bold');
		$("#trendSelector").attr('style', 'display:none');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E' || dataSource == 'P') {
			$("#selectionPanel").css('display', 'inline');
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:inline');
			$("#trendSelector").attr('style', 'display:none');
			$("#map").attr('style', 'display:none');
			if (stateAbbr == "" || stateAbbr == "TL") {
				$("#stateLevelSelector").attr('style', 'display:none');
			} else {
				$("#stateLevelSelector").attr('style', 'display:inline');
				$("#msaWarning").attr('style', 'display:none');
				if (stateLevel == 0) {
					$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
					$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				} else {
					$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
					$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				}
			}
		}
		if (dataSource == 'F') {
			$("#vis").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
			$("#parentState").attr('disabled', 'disabled');
		}
		visType = 'bar';
		barSelector = 0;
		if (event.pathNames == "barSector") {
			if (event.parameters['tr'] != null) {
				$("#currentYearBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				$("#trendsBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
				trendSelection = 'current';
			}
			//stateAbbr cannot = "" or else it will give a 404 error when @RequestMapping tries to retrieve its value
			if (stateAbbr == "") {
				stateAbbr = "US";
			}
			barSector(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, trendSelection, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "barSectorL2") {
			barSectorL2(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		}
	} else if (event.pathNames == "barGas") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
			$("#stateLevelSelector").attr('style', 'display:inline');
		}
		visType = 'bar';
		barSelector = 1;
		barGas(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
	} else if (event.pathNames == "barState" || event.pathNames == "barStateL2" || event.pathNames == "barStateL22" || event.pathNames == "barStateL3") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC; display:none');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#trendSelector").attr('style', 'display:none');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E' || dataSource == 'P') {
			$("#selectionPanel").css('display', 'inline');
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:inline');
			$("#trendSelector").attr('style', 'display:none');
			$("#map").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:inline');
			if (stateAbbr == "" || stateAbbr == "TL") {
				$("#stateLevelSelector").attr('style', 'display:none');
			} else {
				$("#stateLevelSelector").attr('style', 'display:inline');
				$("#msaWarning").attr('style', 'display:inline; color: darkgray; padding-left:5px; height:29px; line-height:29px;');
				if (stateLevel == 0) {
					$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
					$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
				} else {
					$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
					$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
				}
			}
		}
		visType = 'bar';
		barSelector = 2;
		if (event.pathNames == "barState") {
			barState(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "barStateL2") {
			barStateL2(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "barStateL22") {
			barStateL2(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "barStateL3") {
			barStateL3(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, ss, reportingStatus, emissionsType, tribalLandId);
		}
	} else if (event.pathNames == "barSupplier") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#stateLevelSelector").attr('style', 'display:none');
		$("#table-label").attr('style', 'display:none');
		hasTrend(supplierSector, false);
		barSupplier(facOrLoc);
		// Pie
	} else if (event.pathNames == "pieSector" || event.pathNames == "pieSectorL2" || event.pathNames == "pieSectorL3" || event.pathNames == "pieSectorL4") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#titleDiv").attr('style', 'padding: 5px; font-weight: bold');
		$("#table-label").attr('style', 'display:none');
		$("#stateLevelSelector").attr('style', 'display:none');
		if ((dataSource == 'E' || dataSource == 'P') && stateAbbr != "") {
			if (stateAbbr == "TL") {
				$("#selectionPanel").css('display', 'none');
			} else {
				$("#selectionPanel").css('display', 'inline');
			}
			$("#selectorWidgets").css('display', 'inline');
			$("#vis").attr('style', 'display:none');
			$("#trendSelector").attr('style', 'display:none');
			$("#map").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:inline');
			if (stateLevel == 0) {
				$("#msaBtn").removeClass('softBtnRightOn').addClass('softBtnRight');
				$("#countyBtn").removeClass('softBtnLeft').addClass('softBtnLeftOn');
			} else {
				$("#countyBtn").removeClass('softBtnLeftOn').addClass('softBtnLeft');
				$("#msaBtn").removeClass('softBtnRight').addClass('softBtnRightOn');
			}
		}
		if (dataSource == 'F') {
			$("#vis").attr('style', 'display:none');
			$("#stateLevelSelector").attr('style', 'display:none');
			$("#parentState").attr('disabled', 'disabled');
		}
		visType = 'pie';
		pieSelector = 0;
		if (event.pathNames == "pieSector") {
			//stateAbbr cannot = "" or else it will give a 404 error when @RequestMapping tries to retrieve its value
			if (stateAbbr == "") {
				stateAbbr = "US";
			}
			pieSector(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "pieSectorL2") {
			pieSectorL2(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "pieSectorL3") {
			pieSectorL3(ss, facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		} else if (event.pathNames == "pieSectorL4") {
			pieSectorL4(ss, facOrLoc, stateAbbr, fipsCode, msaCodefacId, lowE, highE, reportingStatus, emissionsType, tribalLandId);
		}
	} else if (event.pathNames == "pieGas") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		visType = 'pie';
		pieSelector = 1;
		pieGas(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
	} else if (event.pathNames == "pieState" || event.pathNames == "pieStateL2" || event.pathNames == "pieStateL3") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		visType = 'pie';
		pieSelector = 2;
		if (event.pathNames == "pieState") {
			pieState(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "pieStateL2") {
			pieStateL2(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "pieStateL3") {
			pieStateL3(ss, facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		}
	} else if (event.pathNames == "pieSupplier") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#table-label").attr('style', 'display:none');
		$("#stateLevelSelector").attr('style', 'display:none');
		pieSupplier(facOrLoc);
		// Tree
	} else if (event.pathNames == "treeSector" || event.pathNames == "treeSectorL2" || event.pathNames == "treeSectorL3" || event.pathNames == "treeSectorL4") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeft').addClass('softBtnLeftOn');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#titleDiv").attr('style', 'padding: 5px; font-weight: bold');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").hide();
			$("#selectorWidgets").css('display', 'none');
		}
		visType = 'tree';
		treeSelector = 0;
		if (event.pathNames == "treeSector") {
			treeSector(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "treeSectorL2") {
			treeSectorL2(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "treeSectorL3") {
			treeSectorL3(ss, facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "treeSectorL4") {
			treeSectorL4(ss, facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
		}
	} else if (event.pathNames == "treeGas") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenter').addClass('softBtnCenterOn');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRightOn').addClass('softBtnRight');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		visType = 'tree';
		treeSelector = 1;
		treeGas(facOrLoc, stateAbbr, fipsCode, msaCode, facId, lowE, highE, reportingStatus);
	} else if (event.pathNames == "treeState" || event.pathNames == "treeStateL2" || event.pathNames == "treeStateL3") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#map").attr('style', 'display:none');
		$("#vis").attr('style', 'display:inline');
		$("#sector").removeClass('softBtnLeftOn').addClass('softBtnLeft');
		$("#gas").removeClass('softBtnRight').removeClass('softBtnRightOn').removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#gas").attr('style', 'border-right-style:solid; border-right-width:1px; border-right-color:#CCC;');
		$("#geography").attr('style', 'margin-right:15px; display:inline');
		$("#geography").removeClass('softBtnRight').addClass('softBtnRightOn');
		$("#geoSec").html('Sector').attr("title", 'Sector');
		$("#geoFac").html('Geography').attr("title", 'Geography');
		$("#table-label").attr('style', 'display:none');
		if (dataSource == 'E') {
			$("#selectionPanel").show();
			$("#selectorWidgets").css('display', 'inline');
		}
		visType = 'tree';
		treeSelector = 2;
		if (event.pathNames == "treeState") {
			treeState(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "treeStateL2") {
			treeStateL2(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		} else if (event.pathNames == "treeStateL3") {
			treeStateL3(facOrLoc, stateAbbr, fipsCode, facId, lowE, highE, reportingStatus);
		}
	} else if (event.pathNames == "treeSupplier") {
		$("#usa").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#list").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#line").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#bar").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#pie").removeClass('softBtnCenterOn').addClass('softBtnCenter');
		$("#table-label").attr('style', 'display:none');
		treeSupplier(facOrLoc);
	} else if (event.pathNames == "facilityDetail") {
		if (dataSource == "S") {
			$("#selectionPanel").show();
			$("#sectorSelection").attr('style', 'margin-left: 5px; display:inline');
		} else if (dataSource == "I") {
			$("#selectionPanel").show();
			$("#injectionSelection").attr('style', 'margin-left: 5px; display:inline');
		} else {
			$("#selectionPanel").hide();
		}
		$("#selectorWidgets").css('display', 'none');
		$("#titleDiv").attr('style', 'display:none');
		$("#table-label").attr('style', 'display:none');
		mapSelector = 0;
		if (!loaded) {
			loadScript();
			loaded = true;
		} else {
			getFacilityInfo(facId);
		}
	}
});

function generateURL(pageName, allReportingYears) {
	$('.mPopover').hide(500);
	if (pageName == '') { //Parameter updated, same view
		var urlString = jQuery.address.path() + "?";
	} else {
		var urlString = "/" + pageName + "/?";
	}
	//ON STATE CHANGE ONLY
	//Load user entered filtering params
	urlString += "q=" + $("#facOrLocInput").val() + "&";
	if (dataSource != 'O' && !isOnshoreOnly() && dataSource != 'B' && !isBoostingOnly()) {
		urlString += "st=" + $("#parentState").val() + "&";
		if (isState(abbreviationToState($("#parentState").val()))) {
			if ($("#countyState").val() != "" && dataSource != 'L' && $("#parentState").val() != "TL") {
				urlString += "fc=" + addLeadingZeros($("#countyState").val()) + "&";
			}
			if ($("#parentState").val() == "TL") {
				urlString += "tl=" + $("#tribe").val() + "&";
			}
		}
		urlString += "bs=" + $("#basin").val() + "&";
	}
	if (dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) {
		urlString += "bs=" + $("#basin").val() + "&";
	}
	if (dataSource == 'E' || dataSource == 'P') {
		urlString += "et=" + $("#emissionsType").val() + "&";
	}
	var facID = $("#facilityId").val();
	var vDs = dataSource;
	urlString += "fid=" + facID;
	$("a.facName").attr('style', 'font-weight: bold; color: rgb(67, 159, 62)');
	if (facID.length > 0) {
		$("#" + facID).attr('style', 'font-weight: 800; color: #465f45');
	}
	urlString += generateSearchQuery() + "&";
	if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T') {
		urlString += "lowE=" + $("#lowEmissionRange").val() + "&";
		urlString += "highE=" + $("#highEmissionRange").val();
		urlString += generateGasFilterQueryString() + generateSectorFilterQueryString() + "&";
		urlString += "si=" + $("#sectorId").val() + "&";
		urlString += "ss=" + $("#subsectorName").val() + "&";
		urlString += "so=" + emitterSort + "&";
	} else if (dataSource == 'S') {
		urlString += "sc=" + supplierSector + "&";
		urlString += "so=" + supplierSort + "&";
	} else if (dataSource == 'I') {
		urlString += "is=" + injectionSelection + "&";
		urlString += "so=" + emitterSort + "&";
	}
	urlString += "ds=" + vDs + "&";
	urlString += "yr=" + $("#reportingYear").val() + "&";
	if ((dataSource == "I") || reportingStatus != 'ALL') {
		trendSelection = 'current';
	}
	urlString += "tr=" + trendSelection + "&";
	urlString += "cyr=" + cyear + "&";
	urlString += "ol=" + overlayLevel + "&";
	urlString += "sl=" + stateLevel + "&";
	urlString += "rs=" + reportingStatus;
	//if (visType != 'map') {
		if (reportingStatus === 'GRAY') {
			$("#sectorNumberOfFacilities").html('# of Non-Reporting Facilities');
		} else if (reportingStatus === 'RED') {
			$("#sectorNumberOfFacilities").html('# of Late or Non-Reporting Facilities');
		} else {
			$("#sectorNumberOfFacilities").html('# of Reporting Facilities');
		}
	//}
	if (pageName == 'excel') {
		downloadExcel(allReportingYears);
	} else {
		//## Modify the url address
		var newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '#' + urlString.replace(/\s/g, "%20");
		//open facDetail in new window
		if (pageName == 'facilityDetail') {
			vDetailService = "/service/facilityDetail/";
			if (dataSource == "T") {
				vDetailService = "/service/pipeDetail/";
			}
			newUrl = window.location.protocol + "//" + window.location.host + ROOT + vDetailService + ryear + "?id=" + facID + "&ds=" + vDs + "&et=" + $("#emissionsType").val() + "&popup=true";
			window.open(newUrl)
		} else {
			window.location = newUrl;
		}
	}
}

function queryTable(pageNumber, facOrLoc, fipsCode, state, msaCode, basinCode, tribalLandId, lowE, highE, sortOrder, reportingStatus, emissionsType) {
	var visType = $("#visType").val();
	var countyName = $("#countyName").val();
	if (pageNumber >= 0) {
		var flightRequest = generateFlightRequestObject(facOrLoc, state, fipsCode, msaCode, null, null, lowE, highE, cyear, null, reportingStatus, emissionsType, tribalLandId, pageNumber, visType);
		$('#facloading').attr('style', 'display:block;float:right');
		jQuery.ajax({
			type: 'POST',
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			},
			url: 'service/populateFacilitySummaryPanel/',
			data: flightRequest,
			success: function (response) {
				$("#leftNav").html(response);
				$('#facloading').attr('style', 'display:none');
				setSort();
				var facID = $("#facilityId").val();
				if (facID.length > 0) {
					$("#" + facID).attr('style', 'font-weight: 800; color: #465f45');
				}
			}
		});
		if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T') {
			jQuery.ajax({
					type: 'POST',
					headers: {
						'Accept': 'application/json',
						'Content-Type': 'application/json'
					},
					url: 'service/populateSectorDashboard/',
					data: flightRequest,
					success: function (data) {
						$("#rollupUnit").html('(' + data.unit + ' CO<sub>2</sub>e)');
						$("#s1Total").html('0');
						$("#s1Facility").html('0');
						$("#s2Total").html('0');
						$("#s2Facility").html('0');
						$("#s3Total").html('0');
						$("#s3Facility").html('0');
						$("#s4Total").html('0');
						$("#s4Facility").html('0');
						$("#s5Total").html('0');
						$("#s5Facility").html('0');
						$("#s6Total").html('0');
						$("#s6Facility").html('0');
						$("#s7Total").html('0');
						$("#s7Facility").html('0');
						$("#s8Total").html('0');
						$("#s8Facility").html('0');
						$("#s9Total").html('0');
						$("#s9Facility").html('0');
						$("#s10Total").html('0');
						$("#s10Facility").html('0');
						//power plants
						if ($("#sector1checkbox").attr('checked')) {
							sectorOnOff('sector1checkbox', 1, 1);
							$("#s1Total").html(addCommas(data.values[0]));
							$("#s1Facility").html(addCommas(data.values[1]));
						} else {
							sectorOnOff('sector1checkbox', 1, 1);
							$("#s1Total").html('---');
							$("#s1Facility").html('---');
						}
						//gas
						if ($("#sector9checkbox").attr('checked')) {
							sectorOnOff('sector9checkbox', 5, 2);
							$("#s2Total").html(addCommas(data.values[16]));
							//PUB-704: Special case for Onshore Non-Reporting Facilities per selected basin
							if (data.values[17] != 0) {
								$("#s2Facility").html(addCommas(data.values[17]));
							} /*else if (data.values[17] == 0 && data.values[19] != 0) {
								$("#s2Facility").html(addCommas(data.values[19]));
							}*/
						} else {
							sectorOnOff('sector9checkbox', 5, 2);
							$("#s2Total").html('---');
							$("#s2Facility").html('---');
						}
						//refineries
						if ($("#sector5checkbox").attr('checked')) {
							sectorOnOff('sector5checkbox', 7, 3);
							$("#s3Total").html(addCommas(data.values[8]));
							$("#s3Facility").html(addCommas(data.values[9]));
						} else {
							sectorOnOff('sector5checkbox', 7, 3);
							$("#s3Total").html('---');
							$("#s3Facility").html('---');
						}
						//chemicals
						if ($("#sector7checkbox").attr('checked')) {
							sectorOnOff('sector7checkbox', 9, 4);
							$("#s4Total").html(addCommas(data.values[12]));
							$("#s4Facility").html(addCommas(data.values[13]));
						} else {
							sectorOnOff('sector7checkbox', 9, 4);
							$("#s4Total").html('---');
							$("#s4Facility").html('---');
						}
						//other
						if ($("#sector8checkbox").attr('checked')) {
							sectorOnOff('sector8checkbox', 2, 5);
							$("#s5Total").html(addCommas(data.values[14]));
							$("#s5Facility").html(addCommas(data.values[15]));
						} else {
							sectorOnOff('sector8checkbox', 2, 5);
							$("#s5Total").html('---');
							$("#s5Facility").html('---');
						}
						//minerals
						if ($("#sector4checkbox").attr('checked')) {
							sectorOnOff('sector4checkbox', 3, 6);
							$("#s6Total").html(addCommas(data.values[6]));
							$("#s6Facility").html(addCommas(data.values[7]));
						} else {
							sectorOnOff('sector4checkbox', 3, 6);
							$("#s6Total").html('---');
							$("#s6Facility").html('---');
						}
						//waste
						if ($("#sector2checkbox").attr('checked')) {
							sectorOnOff('sector2checkbox', 4, 7);
							$("#s7Total").html(addCommas(data.values[2]));
							$("#s7Facility").html(addCommas(data.values[3]));
						} else {
							sectorOnOff('sector2checkbox', 4, 7);
							$("#s7Total").html('---');
							$("#s7Facility").html('---');
						}
						//metals
						if ($("#sector3checkbox").attr('checked')) {
							sectorOnOff('sector3checkbox', 6, 8);
							$("#s8Total").html(addCommas(data.values[4]));
							$("#s8Facility").html(addCommas(data.values[5]));
						} else {
							sectorOnOff('sector3checkbox', 6, 8);
							$("#s8Total").html('---');
							$("#s8Facility").html('---');
						}
						//pulp paper
						if ($("#sector6checkbox").attr('checked')) {
							sectorOnOff('sector6checkbox', 8, 9);
							$("#s9Total").html(addCommas(data.values[10]));
							$("#s9Facility").html(addCommas(data.values[11]));
						} else {
							sectorOnOff('sector6checkbox', 8, 9);
							$("#s9Total").html('---');
							$("#s9Facility").html('---');
						}
						if ($("#sector10checkbox").attr('checked')) {
							sectorOnOff('sector10checkbox', 10, 10);
							$("#s10Total").html(addCommas(data.values[20])).css('background-color', '#efefef');
							$("#s10Facility").html(addCommas(data.values[19])).css('background-color', '#efefef');
						} else {
							sectorOnOff('sector10checkbox', 10, 10);
							$("#s10Total").html('---').css('background-color', '#efefef');
							$("#s10Facility").html('---').css('background-color', '#efefef');
						}
						$("#sector10row2").css('background-color', '#efefef');
						enableDisableSectors(dataSource);						
						if (data.unit.toLocaleLowerCase().indexOf("million")!=-1) {
							$(".yellowflag").show();
						} else {
							$(".yellowflag").hide();
						}
					}
				}
			);
		}
	}
}

function setURLPath() {
	if (visType == 'map') {
		if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
			if (mapSelector == 0) {
				generateURL('facility');
			} else {
				generateURL('intensity');
			}
		} else if (dataSource == 'S') {
			generateURL('facility');
		}
	} else if (visType == 'list') {
		if (listSelector == 0) {
			if (dataSource == 'F' || dataSource == 'I') {
				generateURL('listFacility');
			} else if (dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) {
				generateURL('listFacilityForBasinGeo');
			} else {
				generateURL('listSector');
			}
		} else if (listSelector == 1) {
			generateURL('listGas');
		} else if (listSelector == 2) {
			if (dataSource == 'E' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
				generateURL('listFacility');
			} else if (dataSource == 'O' || isOnshoreOnly() || dataSource == 'B' || isBoostingOnly()) {
				generateURL('listFacilityForBasin');
			} else {
				generateURL('listSector');
			}
		}
	} else if (visType == 'line') {
		if ((dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'A') && reportingStatus == 'ALL') {
			generateURL('trend');
			btTrendPopup();
		} else if ((dataSource == 'S' || dataSource == 'I') && reportingStatus == 'ALL') {
			if (dataSource == 'S') {
				hasTrend(supplierSector, true);
				refineriesPopup();
			} else {
				hasTrend(injectionSelection, true);
			}
		} else {
			visType = 'map';
			generateURL('facility');
		}
	} else if (visType == 'bar') {
		if (reportingStatus == 'RED' || reportingStatus == 'GRAY') {
			visType = 'map';
			generateURL('facility');
		} else if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'A') {
			if (barSelector == 0 || dataSource == 'L' || dataSource == 'F') {
				generateURL('barSector');
			} else if (barSelector == 1) {
				generateURL('barGas');
			} else {
				$('#subsectorName').val("");
				generateURL('barState');
			}
		} else if (dataSource == 'S') {
			generateURL('barSupplier');
		} else if (dataSource == 'I') {
			visType == 'map';
			generateURL('facility');
		}
	} else if (visType == 'pie') {
		if (reportingStatus == 'RED' || reportingStatus == 'GRAY') {
			visType = 'map'
			generateURL('facility');
		} else if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
			if (pieSelector == 0) {
				generateURL('pieSector');
			} else if (pieSelector == 1) {
				generateURL('pieGas');
			} else {
				generateURL('pieState');
			}
		} else if (dataSource == 'S') {
			generateURL('pieSupplier');
		} else if (dataSource == 'I') {
			visType == 'map';
			generateURL('facility');
		}
	} else if (visType == 'tree') {
		if (dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P' || dataSource == 'B' || dataSource == 'T' || dataSource == 'I' || dataSource == 'A') {
			if (treeSelector == 0) {
				generateURL('treeSector');
			} else if (treeSelector == 1) {
				generateURL('treeGas');
			} else {
				generateURL('treeState');
			}
		} else if (dataSource == 'S') {
			generateURL('treeSupplier');
		}
	}
	//PUB-425 Caveat on SAR AR4 Transition
	if ((dataSource == 'E' || dataSource == 'O' || dataSource == 'L' || dataSource == 'F' || dataSource == 'P') && ryear < 2013) {
		showPopup('gwp_4th');
	}
}

function loadScript() {
	var sources = [
		"js/app/leaflet/leaflet.js",
		"js/app/leaflet/esri-leaflet.js",
		"js/app/leaflet/leaflet.ghgp.js",
		"js/app/leaflet/leaflet.control.topcenter.js",
		"js/app/leaflet/leaflet.control.fullscreen.js",
		"js/app/leaflet/leaflet.control.zoomlabel.js",
		"js/app/leaflet/leaflet.markercluster.js"
	];
	sources.forEach(src => {
		var script = document.createElement('script');
		script.src = src;
		script.async = false;
		document.body.appendChild(script);
	});
}

function getUrlVar() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
		vars[key] = value;
	});
	return vars;
}

var whichDataYear = function (vWhich) {
	if (vWhich === 1) { //show year 2011+
		$("[id^=ry]").not("#ry2010").show();
	} else { //only show year 2016+
		for (var idx = 2010; idx < 2016; idx++) {
			$("#ry" + idx).hide();
		}
	}
};
var btTrendPopup = function () {
	$("#btDashboardTrend").hide();
	$('.dPopover > .close').click(function () {
		$('.dPopover').hide();
	});
	if (($("#sector9checkbox:checkbox").is(':checked') &&
		($('#petroleum10:checkbox').is(':checked') || $('#petroleum11:checkbox').is(':checked')))
		|| ((trendSelection != null && trendSelection == 'current') &&
			(getUrlVar()["ds"] == "B" || getUrlVar()["ds"] == "T"))
	) {
		$("#btDashboardTrend").show();
	}
};
var refineriesPopup = function () {
	$('.dPopover > .close').click(function () {
		$('.dPopover').hide();
	});
	$("#refineriesTrend").show();
};
