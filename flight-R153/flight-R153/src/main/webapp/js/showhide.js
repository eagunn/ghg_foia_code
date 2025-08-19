function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}

function alertpref(){
	if (aandi=='c'){showhide(1);}
}

function nextON () {
document.form.next.disabled=false;
}

function route (xyz) {
	if (document.aandi==undefined){document.aandi='o';};
	MM_goToURL('self', xyz+'?aandi='+document.aandi);
}

function route2 (xyz) {
		MM_goToURL('self', xyz);
}
function showhideDRagent(x) {
	document.form.next.disabled=false;
	if (x==1) {
	document.getElementById('agent1').style.display='inline';
	}
	if (x==2) {
	document.getElementById('agent1').style.display='none';
	document.getElementById('agent2').style.display='none';
	document.getElementById('DRagent1first').value='';
	document.getElementById('DRagent1last').value='';
	document.getElementById('DRagent1phone').value='';
	document.getElementById('DRagent1email').value='';
	}
}

function showhide4f(x) {
	document.getElementById('dr2FN').value='';
	document.getElementById('dr2LN').value='';
	document.getElementById('dr2P').value='';
	document.getElementById('dr2E').value='';
	nextON();
	if (x==1){
	document.getElementById('dr1').style.display='inline';
	document.getElementById('dr2').style.display='none';
	//document.getElementById('toggleHIDE').style.display='none';
	//document.getElementById('toggleSHOW').style.display='inline';
	//document.aandi='c';
	}
	else if (x==2){
	document.getElementById('dr1').style.display='none';
	document.getElementById('dr2').style.display='inline';
	//document.getElementById('toggleHIDE').style.display='inline';
	//document.getElementById('toggleSHOW').style.display='none';
	//document.aandi='o';
	}
	else {
	document.getElementById('dr1').style.display='none';
	document.getElementById('dr2').style.display='none';
	//document.getElementById('toggleHIDE').style.display='inline';
	//document.getElementById('toggleSHOW').style.display='none';
	//document.aandi='o';
	}
}

function showhide4g(x) {
	nextON();
	document.getElementById('adr2FN').value='';
	document.getElementById('adr2LN').value='';
	document.getElementById('adr2P').value='';
	document.getElementById('adr2E').value='';
	if (x==1){
	document.getElementById('adr1').style.display='inline';
	document.getElementById('adr2').style.display='none';
	//document.getElementById('toggleHIDE').style.display='none';
	//document.getElementById('toggleSHOW').style.display='inline';
	//document.aandi='c';
	}
	else if (x==2){
	document.getElementById('adr1').style.display='none';
	document.getElementById('adr2').style.display='inline';
	//document.getElementById('toggleHIDE').style.display='inline';
	//document.getElementById('toggleSHOW').style.display='none';
	//document.aandi='o';
	}
	else {
	document.getElementById('adr1').style.display='none';
	document.getElementById('adr2').style.display='none';
	//document.getElementById('toggleHIDE').style.display='inline';
	//document.getElementById('toggleSHOW').style.display='none';
	//document.aandi='o';
	}
}

function showhideDRaccept(x) {
	if (x==1){
		route2('4_invite_dr02.shtml');
	}
	else if (x==2){
		route2('4_invite_dr_reject.shtml');
	}
}