package gov.epa.ghg.dto;

import java.util.ArrayList;
import java.util.List;

public class GasFilter {

	private boolean co2;
	private boolean ch4;
	private boolean n2o;
	private boolean sf6;
	private boolean nf3;

	//this could be a gas that no longer exists (legacy)
	private boolean hfc23;

	private boolean hfc;
	private boolean pfc;
	private boolean hfe;
	private boolean other;
	private boolean veryShortCompounds;
	private boolean otherFlourinated;

	public GasFilter() {
	}
	
	public GasFilter(Byte[] g) {
		boolean[] results = new boolean[12];
		for (int i=0; i<g.length; i++) {
			if (g[i] != null && g[i] > 0) {
				results[i] = true; 
			}
		}
		map(results); 
	}
	
	public GasFilter(Boolean[] arr) {
		this.co2= arr[0]; 
		this.ch4 =  arr[1]; 
		this.n2o =  arr[2];
		this.sf6 =  arr[3];
		this.nf3 =  arr[4]; 
		this.hfc23 = arr[5];
		this.hfc = arr[6];
		this.pfc = arr[7];
		this.hfe = arr[8];
		this.other = arr[9];
		this.veryShortCompounds = arr[10];
		this.otherFlourinated = arr[11];
	}


	public String getSelectedGasesAsString() {

		String retv = "";
		List<String> gases = new ArrayList<String>();

		if (this.co2) gases.add("CO2");
		if (this.ch4) gases.add("CH4");
		if (this.n2o) gases.add("N2O");
		if (this.sf6) gases.add("SF6");
		if (this.nf3) gases.add("NF3");
		if (this.hfc) gases.add("HFC");
		if (this.pfc) gases.add("PFC");
		if (this.hfe) gases.add("HFE");
		if (this.other) gases.add("OTHER");
		if (this.veryShortCompounds) gases.add("VERY SHORT COMPOUNDS");
		if (this.otherFlourinated) gases.add("OTHER FULLY FLOURINATED GASES");

		if (gases.size() == 0 ) {
			return "NONE";
		}

		for (String ghg : gases) {
		  retv+= ghg + ",";
		}

		//omit trailing comma
		return retv.substring(0,retv.length()-1);

	}

	public boolean areAllGasesSelected() {

		return
				this.co2
			    && this.ch4
				&& this.n2o
				&& this.sf6
				&& this.nf3
				&& this.hfc
				&& this.pfc
				&& this.hfe
				&& this.other
				&& this.veryShortCompounds
				&& this.otherFlourinated
				;
	}


	public Boolean[] toBooleanArray() {

		Boolean[] retv = new Boolean[12];

		retv[0] = co2;
		retv[1] = ch4;
		retv[2] = n2o;
		retv[3] = sf6;
		retv[4] = nf3;
		retv[5] = hfc23;
		retv[6] = hfc;
		retv[7] = pfc;
		retv[8] = hfe;
		retv[9] = other;
		retv[10] = veryShortCompounds;
		retv[11] = otherFlourinated;

		return retv;

	}
	
	
	private void map(boolean[] results) {
		this.co2= results[0]; 
		this.ch4 =  results[1]; 
		this.n2o =  results[2];
		this.sf6 =  results[3];
		this.nf3 =  results[4]; 
		this.hfc23 = results[5];
		this.hfc = results[6];
		this.pfc = results[7];
		this.hfe = results[8];
		this.other = results[9];
		this.veryShortCompounds = results[10];
		this.otherFlourinated = results[11];
	}
	
	
	public GasFilter(Byte g1, Byte g2, Byte g3, Byte g4,
			Byte g5, Byte g6, Byte g7, Byte g8, Byte g9,
			Byte g10, Byte g11, Byte g12) {
		super();
		if (g1 != null && g1 > 0) this.co2=true;
		if (g2 != null && g2 > 0) this.ch4 = true;
		if (g3 != null && g3 > 0) this.n2o = true;
		if (g4 != null && g4 > 0) this.sf6 = true;
		if (g5 != null && g5 > 0) this.nf3 = true;
		if (g6 != null && g6 > 0) this.hfc23 = true;
		if (g7 != null && g7 > 0) this.hfc = true;
		if (g8 != null && g8 > 0) this.pfc = true;
		if (g9 != null && g9 > 0) this.hfe = true;
		if (g10 != null && g10 > 0) this.other = true;
		if (g11 != null && g11 > 0) this.veryShortCompounds = true;
		if (g12 != null && g12 > 0) this.otherFlourinated = true;
	}

	public boolean isCo2() {
		return co2;
	}

	public void setCo2(boolean co2) {
		this.co2 = co2;
	}

	public boolean isCh4() {
		return ch4;
	}

	public void setCh4(boolean ch4) {
		this.ch4 = ch4;
	}

	public boolean isN2o() {
		return n2o;
	}

	public void setN2o(boolean n2o) {
		this.n2o = n2o;
	}

	public boolean isSf6() {
		return sf6;
	}

	public void setSf6(boolean sf6) {
		this.sf6 = sf6;
	}

	public boolean isNf3() {
		return nf3;
	}

	public void setNf3(boolean nf3) {
		this.nf3 = nf3;
	}

	public boolean isHfc23() {
		return hfc23;
	}

	public void setHfc23(boolean hfc23) {
		this.hfc23 = hfc23;
	}

	public boolean isHfc() {
		return hfc;
	}

	public void setHfc(boolean hfc) {
		this.hfc = hfc;
	}

	public boolean isPfc() {
		return pfc;
	}

	public void setPfc(boolean pfc) {
		this.pfc = pfc;
	}

	public boolean isHfe() {
		return hfe;
	}

	public void setHfe(boolean hfe) {
		this.hfe = hfe;
	}

	public boolean isOther() {
		return other;
	}

	public void setOther(boolean other) {

		this.other = other;
	}

	public boolean isVeryShortCompounds() {
		return veryShortCompounds;
	}

	public void setVeryShortCompounds(boolean veryShortCompounds) {
		this.veryShortCompounds = veryShortCompounds;
	}

	public boolean isOtherFlourinated() {
		return otherFlourinated;
	}

	public void setOtherFlourinated(boolean otherFlourinated) {
		this.otherFlourinated = otherFlourinated;
	}
}
