function same(r) {
	// r = which menu set -- caoFraction, mgoFraction
	
	if (r=='clinkerProd'){var ind=3;}
		
	if (document.getElementById('same'+r).checked==true){		
		// below disables all the monthly select menus
		for (i = 2; i <13; i++){
			menuID=r+'Method'+i
			document.form[menuID].disabled=true		
		}
		
		// below disables all otherMethod text fields
		for (j = 2; j <13; j++){
			textID=r+'OtherMethod'+j
			document.form[textID].readOnly=true;
		}
		
		// below makes sure february through december matches january
		januaryMethod(r);
		
		// initialize will not met the next if condition
		if (document.getElementById(r+'Method1').selectedIndex==ind){
			f=document.getElementById(r+'OtherMethod1').value;
			januaryOther(r,f);
		}
		
		document.getElementById('same'+r).focus();		
	}
	
	if (document.getElementById('same'+r).checked==false){		
		//below enables all the monthly select menus
		for (i = 2; i <13; i++){
			menuID=r+'Method'+i
			document.form[menuID].disabled=false		
		}
		//below enables any displayed otherMethod text fields
		for (j = 2; j <13; j++){
			menuID=r+'Method'+j
			textID=r+'OtherMethod'+j
			if (document.getElementById(menuID).selectedIndex==ind){
				document.form[textID].readOnly=false;
			}
		}
	}
}

function setMonthMethod (x,y,r){
	// function to show/hide the other method div and field for a given month depending upon option selected
	
	// x = number representing month (ex. 5=May)
	// y = selectedIndex of menu triggering this function
	// r = which menu set -- massProduced, massSold, etc
	

	if (r=='clinkerProd'){var ind=3;}
	
	a=r+'Other'+x;
	b=r+'OtherMethod'+x;
	if (y==ind){
		document.getElementById(a).style.display='inline';
		document.getElementById(b).readOnly=false;
		document.getElementById(b).focus();
		}
	if (y!=ind){
		document.getElementById(a).style.display='none';
		document.getElementById(b).value='';
	}
}

function januaryMethod (r) {
	// r = which menu set -- massProduced, massSold, etc
	
	if (r=='clinkerProd'){var ind=3;}
	
	q='same'+r;
	x=document.getElementById(r+'Method1').selectedIndex;
	
	if ((x==ind)&&(document.getElementById(q).checked==true)){
		for (i = 1; i <13; i++){
			divID=r+'Other'+i
			document.getElementById(divID).style.display='inline';		
		}
		document.getElementById(r+'OtherMethod1').focus();
		matchJanuary(x,r);
	}
	
	if ((x==ind)&&(document.getElementById(q).checked==false)){
		setMonthMethod(1,ind,r);		
	}
	
	if ((x!=ind)&&(document.getElementById(q).checked==true)){
		for (i = 1; i <13; i++){
			divID=r+'Other'+i
			textID=r+'OtherMethod'+i
			document.getElementById(divID).style.display='none';
			if (i>1){document.getElementById(textID).readOnly=true;}
		}
		for (j = 1; j <13; j++){
			textID=r+'OtherMethod'+j
			document.getElementById(textID).value='';		
		}
		matchJanuary(x,r);
	}
	
	if ((x!=ind)&&(document.getElementById(q).checked==false)){
		setMonthMethod(1,0,r);		
	}
}

function matchJanuary(t,r){
	// t = the selected index in the january menu
	// r = which menu set -- massProduced, massSold, etc
	for (j = 2; j <13; j++){
		method=r+'Method'+j
		document.getElementById(method).selectedIndex=t;		
	}		
}

function januaryOther(r,f){
	// r = which menu set -- massProduced, massSold, etc
	// f = the value/string of the other method field as specified by the user
	if (document.getElementById('same'+r).checked==true){
		for (j = 2; j <13; j++){
			txtarea=r+'OtherMethod'+j
			document.getElementById(txtarea).value=f;		
		}
	}
}


// BELOW IS ENGINE FOR TWO TEXT-ENTERED METHODS WHERE THERE'S NO PICK LIST

function januaryMethText(r,f){
	// r = which data element set -- caoNoncalcined or mgoNoncalcined

	if (document.getElementById('same'+r).checked==true){
		makeYearReadonly(r);
		makeYearMatchJanuary(r,f);
	}
}
function sameMeth(r){
	if (document.getElementById('same'+r).checked==true){
		f=document.getElementById(r+'Method1').value;
		makeYearReadonly(r);
		makeYearMatchJanuary(r,f);
	}
	if (document.getElementById('same'+r).checked==false){
		makeYearWritable(r);
	}
}


function makeYearMatchJanuary(r,f) {
	// r = which data element set -- caoNoncalcined or mgoNoncalcined
	
		// below makes feb-december match January
		// f=document.getElementById(r+'Method1');
		for (j = 2; j <13; j++){
			textID=r+'Method'+j
			document.form[textID].value=f;
		}
}

function makeYearReadonly(r) {
	// r = which data element set -- caoNoncalcined or mgoNoncalcined
	
		// below makes feb-december readOnly
		for (j = 2; j <13; j++){
			textID=r+'Method'+j
			document.form[textID].readOnly=true;
		}		
}

function makeYearWritable(r) {
	// r = which data element set -- caoNoncalcined or mgoNoncalcined
	
		// below makes feb-december readOnly
		for (j = 2; j <13; j++){
			textID=r+'Method'+j
			document.form[textID].readOnly=false;
		}		
}