function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}

function nextON () {
	document.form.next.disabled=false;
}

function goback () {
	window.history.back();
}

function goforward () {
	window.history.forward();
}

function ALERTbasic(x,y){
	if (x==undefined) x="clicked";
	if (y==undefined) y="";
	alert ('Demo Prototype Message:\n\nThe '+x+' selected is not available.\n\n'+y);
}
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}

function route2 (xyz) {
		MM_goToURL('self', xyz);
}

function fauxsubmit() {
	if(document.form.onsubmit())
	{//this check triggers the validations
	document.form.submit();
 	}
}

function deleteunit (x){
if(confirm('DELETE this Unit or Group?\n\nBy deleting '+x+' you will lose\nany of its emissions calculation data already entered.'))
alert('If I werent just a demo -- Id be deleting right now!');
else alert('That was close -- we almost  deleted your unit!')
}

function deletefuel (x){
if(confirm('DELETE this Fuel?\n\nBy deleting '+x+' you will lose\nany of its emissions calculation data already entered.'))
alert('If I werent just a demo -- Id be deleting right now!');
else alert('That was close -- we almost  deleted your fuel!')
}