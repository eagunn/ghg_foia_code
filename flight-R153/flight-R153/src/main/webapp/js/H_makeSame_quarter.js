//quarterSelect is triggered by onChange for Q1-Q4 pick lists that have a dynamic "specify other" requirement.
function quarterSelect(x,y,r){
	// function to show/hide the 'specify other' label and field r for quarter x depending upon option selected y	
	// x = number representing month (ex. 2=Q2)
	// y = selectedIndex of menu triggering this function
	// r = which menu set

	//Setting the ind variable tells the function which option in the pick list
	//should trigger displaying a text field.  Most commonly, it's the 'Other' option
	//but does vary depending upon requirements. 
	//'Select' is 0 in option array
	//make ind=999 if there is no requirement to display a "Specify Other" text field
	if ((r=='caoFractionMethod')||(r=='mgoFractionMethod')){var ind=2;}
//  if (r=='clinkerCarbContent'){var ind=999;}

	if (y==ind){
		document.getElementById(r+'OtherLabel'+x).style.display='inline';
		document.getElementById(r+'OtherText'+x).style.display='inline';					
	}
	if ((y!=ind)&&(ind!=99)){
		document.getElementById(r+'OtherLabel'+x).style.display='none';
		document.getElementById(r+'OtherText'+x).style.display='none';
		document.getElementById(r+'OtherText'+x).value='';
	}
}

// FUNCTION for making pick list selection for Q2-Q4 match Q1
function matchQ1Selection(r){
// r = passed to function by link--vaue represents which data element set

	//Setting the ind variable tells the function which option in the pick list
	//should trigger displaying a text field.  Most commonly, it's the 'Other' option
	//but does vary depending upon requirements. 
	//'Select' is 0 in option array
	//make ind=999 if there is no requirement to display a "Specify Other" text field
	if ((r=='caoFractionMethod')||(r=='mgoFractionMethod')){var ind=2;}
//  if (r=='clinkerCarbContent'){var ind=999;}

	
	// gets which option is selected for the relevant Q1 pick list
	x=document.getElementById(r+'1').selectedIndex;	
	
	// gests the value of the Q1 "Specify Other" field, if it exists
	if (ind!=99){f=document.getElementById(r+'OtherText1').value;}

	// makes the Q2-Q4 match the Q1 picklist
	for (m = 2; m <5; m++){
		picklist=r+m
		document.getElementById(picklist).selectedIndex=x;		
	}	

	//if the Q1 Option selected is the option that requires displaying an additional 'Specify Other' text field
	//this will display them for Q2-Q4
	if (x==ind){	
		for (q = 2; q <5; q++){
			labelID=r+'OtherLabel'+q
			fieldID=r+'OtherText'+q
			document.getElementById(labelID).style.display='inline';
			document.getElementById(fieldID).style.display='inline';
			document.getElementById(fieldID).value=f
		}			
	}
	
	//if the Q1 Option selected is NOT the option that requires displaying an additional 'Specify Other' text field
	//this will hide them for Q2-Q4
	if ((x!=ind)&&(ind!=99)){		
		for (q = 2; q <5; q++){
			labelID=r+'OtherLabel'+q
			fieldID=r+'OtherText'+q
			document.getElementById(labelID).style.display='none';
			document.getElementById(fieldID).style.display='none';
			document.getElementById(fieldID).value=''
		}			
	}	
}

// FUNCTION for making text field for Q2-Q4 match Q1 -- WHEN 'MAKE SAME' function is associated with just a text field -- not a picklist/textfield combo
function matchQ1Text(r){
// r = passed to function by link--vaue represents which data element set

		f=document.getElementById(r+'1').value;
		for (q = 2; q <5; q++){
			textID=r+q
			document.form[textID].value=f;
		}		
}