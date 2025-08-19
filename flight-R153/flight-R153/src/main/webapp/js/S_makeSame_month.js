//monthSelect is triggered by onChange for Jan-Dec pick lists that have a dynamic "specify other" requirement.
function monthSelect(x,y,r){
	// function to show/hide the 'specify other' label and field r for month x depending upon option selected y	
	// x = number representing month (ex. 5=May)
	// y = selectedIndex of menu triggering this function
	// r = which menu set

	//Setting the ind variable tells the function which option in the pick list
	//should trigger displaying a text field.  Most commonly, it's the 'Other' option
	//but does vary depending upon requirements. 
	//'Select' is 0 in option array
	//make ind=999 if there is no requirement to display a "Specify Other" text field
//	if (r=='ccMethod'){var ind=17;}
//	if (r=='quantityMethod'){var ind=999;}
//	if (r=='mwMethod'){var ind=999;}	
	
	if (r=='massProduced'){var ind=5;}
	if (r=='massSold'){var ind=5;}
	if (r=='CaO'){var ind=2;}
	if (r=='MgO'){var ind=2;}

	if (y==ind){
		document.getElementById(r+'OtherLabel'+x).style.display='inline';
		document.getElementById(r+'OtherMethod'+x).style.display='inline';					
	}
	if ((y!=ind)&&(ind!=999)){
		document.getElementById(r+'OtherLabel'+x).style.display='none';
		document.getElementById(r+'OtherMethod'+x).style.display='none';
		document.getElementById(r+'OtherMethod'+x).value='';
	}
}

// FUNCTION for making pick list selection for February-December match January
function matchJanSelection(r){
// r = passed to function by link--vaue represents which data element set

	//Setting the ind variable tells the function which option in the pick list
	//should trigger displaying a text field.  Most commonly, it's the 'Other' option
	//but does vary depending upon requirements. 
	//'Select' is 0 in option array
	//make ind=999 if there is no requirement to display a "Specify Other" text field
//	if (r=='ccMethod'){var ind=17;}
//	if (r=='quantityMethod'){var ind=999;}
//	if (r=='mwMethod'){var ind=999;}
	
	if (r=='massProduced'){var ind=5;}
	if (r=='massSold'){var ind=5;}
	if (r=='CaO'){var ind=2;}
	if (r=='MgO'){var ind=2;}
	
	// gets which option is selected for the relevant January pick list
	x=document.getElementById(r+'Method1').selectedIndex;	
	
	// gests the value of the January "Specify Other" field, if it exists
	if (ind!=999){f=document.getElementById(r+'OtherMethod1').value;}

	// makes the February-December picklists match January
	for (m = 2; m <13; m++){
		picklist=r+"Method"+m;
		document.getElementById(picklist).selectedIndex=x;		
	}	

	//if the January Option selected is the option that requires displaying an additional 'Specify Other' text field
	//this will display them for February-December
	if (x==ind){	
		for (m = 2; m <13; m++){
			labelID=r+'OtherLabel'+m;
			fieldID=r+'OtherMethod'+m;
			document.getElementById(labelID).style.display='inline';
			document.getElementById(fieldID).style.display='inline';
			document.getElementById(fieldID).value=f;
		}			
	}
	
	//if the January Option selected is NOT the option that requires displaying an additional 'Specify Other' text field
	//this will display them for February-December
	if ((x!=ind)&&(ind!=999)){		
		for (m = 2; m <13; m++){
			labelID=r+'OtherLabel'+m;
			fieldID=r+'OtherMethod'+m;
			document.getElementById(labelID).style.display='none';
			document.getElementById(fieldID).style.display='none';
			document.getElementById(fieldID).value='';
		}			
	}	
}

// FUNCTION for making text field (NOT picklist and NOT any dynamic hide/show)
// for February-December match January
function matchJanText(r){
// r = passed to function by link--vaue represents which data element set

		f=document.getElementById(r+'1').value;
		for (m = 2; m <13; m++){
			textID=r+m;
//			document.form[textID].value=f;
			document.getElementById(textID).value=f;
		}		
}

// FUNCTION for making textarea for February-December match January
function matchJanTextArea(r){
// r = passed to function by link--vaue represents which data element set

		f=document.getElementById(r+'1').value;
		for (m = 2; m <13; m++){
			textID=r+m;
//			document.form[textID].value=f;
			document.getElementById(textID).value=f;
		}		
}

// FUNCTION for making radio button selection (NOT picklist and NOT any dynamic hide/show)
// for February-December match January
function matchJanRadio(r,n){
// r = passed to function by link--vaue represents which data element set
// num = passed to function by link--number of options in the array
	janr=r+'1';
	x=999;
	for (i=0;i<n;i++){
		if (document.getElementById(janr)[i].checked==true){
			x=i;
		}
		if (x!=999){
			for (m = 2; m <13; m++){
				buttons=r+m
				document.form.elements[buttons][x].checked=true;
			}
		}
	}
}

function matchJanuaryRadio(){
	// r = passed to function by link--vaue represents which data element set
	// num = passed to function by link--number of options in the array
	if(document.getElementById("volumeTemp160").checked==true){
		for (m = 2; m <13; m++){
			document.getElementById("volumeTemp" + m + "60").checked=true;
		}
	}else if(document.getElementById("volumeTemp168").checked==true){
		for (m = 2; m <13; m++){
			document.getElementById("volumeTemp" + m + "68").checked=true;
		}
	}
}
	
