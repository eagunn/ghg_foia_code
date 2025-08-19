function setOtherEquipmentTypeDisplay() {
	var equipTypeDiv;
	var equipType;
	var equipTypeOther;
	equipType = document.getElementById("equipmentTypeName");
	equipTypeOther = document.getElementById("equipmentTypeOtherDesc");
	equipTypeDiv = document.getElementById("othertype");
	if (equipType.value == "Other") {	// Display other standard text box.
		equipTypeDiv.style.display = "inline";
	}
	else {		// Hide other standard text box.
		equipTypeDiv.style.display = "none";
	}
}

function checkOtherEquipmentType() {
	var equipType = document.getElementById("equipmentTypeName");
	var equipTypeOther = document.getElementById("equipmentTypeOtherDesc");
	if (equipType.value != "Other") equipTypeOther.value = "";
}

function getTextAreaLength(fieldName) {
	var field = document.getElementById(fieldName);
	var value = field.value;
	return value.length;
}

function setSame(chkboxName, targetName, mapName, size) {
	var chkbox = document.getElementById(chkboxName);
	if (chkbox.checked) {
		var selectedIndex = document.getElementById(mapName + "[1]." + targetName).selectedIndex;
		for (i=2; i <= size; i++) {
			var target = document.getElementById(mapName + "[" + i + "]." + targetName);
			target.selectedIndex = selectedIndex;
			target.disabled = true;
		}
	}
	else {
		for (i=2; i <= size; i++) {
			var target = document.getElementById(mapName + "[" + i + "]." + targetName);
			target.disabled = false;
		}
	}
}

function makeSame(chkboxName, targetName, mapName, size) {
	var chkbox = document.getElementById(chkboxName);
	if (chkbox.checked) {
		var selectedIndex = document.getElementById(mapName + "[1]." + targetName).selectedIndex;
		for (i=2; i <= size; i++) {
			var target = document.getElementById(mapName + "[" + i + "]." + targetName);
			target.selectedIndex = selectedIndex;
		}
	}
}

function enableOnSubmit(chkboxName, targetName, mapName, size) {
	var chkbox = document.getElementById(chkboxName);
	if (chkbox.checked) {
		var selectedIndex = document.getElementById(mapName + "[1]." + targetName).selectedIndex;
		for (i=2; i <= size; i++) {
			var target = document.getElementById(mapName + "[" + i + "]." + targetName);
			target.disabled = false;
		}
	}
}
