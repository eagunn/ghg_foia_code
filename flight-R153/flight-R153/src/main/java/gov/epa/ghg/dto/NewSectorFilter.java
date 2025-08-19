package gov.epa.ghg.dto;



public class NewSectorFilter extends SectorFilter{
		
		private Boolean[] powerPlants = new Boolean[1]; //1
		private Boolean[] waste = new Boolean[5]; 		//2
		private Boolean[] metals = new Boolean[8]; 		//3
		private Boolean[] minerals = new Boolean[6]; 	//4
		private Boolean[] refineries = new Boolean[1]; 	//5
		private Boolean[] pulpAndPaper = new Boolean[3]; //6
		private Boolean[] chemicals = new Boolean[12]; 	//7
		private Boolean[] other = new Boolean[11]; 		//8
		private Boolean[] petroleumAndNaturalGas = new Boolean[12]; //9
		//first item is null to make it a 1-index (starts from 1 not 0) array 
		private Boolean[][] matrix = {null, powerPlants, waste, metals, minerals, refineries, pulpAndPaper, chemicals, other, petroleumAndNaturalGas }; 
		
		
		public NewSectorFilter() {}

		
		public void map(Boolean[] toFill, Boolean[] val) {
			assert(toFill.length == val.length); 
			for (int i=0; i<toFill.length; i++) {
				toFill[i] = val[i]; 
			}
		}
		
		
		public void map(Boolean[] arr, Byte[] val) {
			
			for (int i=0; i<arr.length; i++) {
				arr[i] = assign(val[i]); 
			}
			
		}
		
		private void mapEager(Boolean[] arr, Byte val) {
			 for (int i=0; i<arr.length; i++) {
				 arr[i] = assign(val); 
			 }
		}
		
		
		public NewSectorFilter(Byte[] mainVals){ 
			
			for(int i=1; i<matrix.length;i++) {
				mapEager(matrix[i], mainVals[i]); 
			}
		}
		
		
		public NewSectorFilter(Boolean[][]vals) {
			for (int i=1; i < matrix.length; i++) {
				map(matrix[i], vals[i-1]); 
			}
			
			
			
		}
		
		public NewSectorFilter(Byte[][] vals) {
			
			for (int i = 1; i < matrix.length ; i++) {
				map(matrix[i], vals[i]);
			}
			
			
			
		}
		
		
	    private boolean assign(Byte p){
	    	return (p != null && p > 0); 
	    }
	    
	    

		public boolean isPowerPlants() {
			return powerPlants[0]; 
		}

		public void setPowerPlants(boolean powerPlants) {
			this.powerPlants[0] = powerPlants;
		}

		public boolean isWaste() {
			return waste[0];
		}

		public void setWaste(boolean waste) {
			this.waste[0] = waste;
		}

		public boolean isS201() {
			return waste[1];
		}

		public void setS201(boolean s201) {
			this.waste[1] = s201;
		}

		public boolean isS202() {
			return waste[2];
		}

		public void setS202(boolean waste2) {
			this.waste[2] = waste2;
		}

		public boolean isS203() {
			return waste[3];
		}

		public void setS203(boolean s203) {
			this.waste[3] = s203;
		}

		public boolean isS204() {
			return waste[4];
		}

		public void setS204(boolean s204) {
			this.waste[4] = s204;
		}

		public boolean isMetals() {
			return metals[0];
		}

		public void setMetals(boolean metals) {
			this.metals[0] = metals;
		}

		public boolean isS301() {
			return metals[1];
		}

		public void setS301(boolean s301) {
			this.metals[1] = s301;
		}

		public boolean isS302() {
			return metals[2];
		}

		public void setS302(boolean s302) {
			this.metals[2] = s302;
		}

		public boolean isS303() {
			return metals[3];
		}

		public void setS303(boolean s303) {
			this.metals[3] = s303;
		}

		public boolean isS304() {
			return metals[4];
		}

		public void setS304(boolean s304) {
			this.metals[4] = s304;
		}

		public boolean isS305() {
			return metals[5];
		}

		public void setS305(boolean s305) {
			this.metals[5] = s305;
		}

		public boolean isS306() {
			return metals[6];
		}

		public void setS306(boolean s306) {
			this.metals[6] = s306;
		}

		public boolean isS307() {
			return metals[7];
		}

		public void setS307(boolean s307) {
			this.metals[7] = s307;
		}

		public boolean isMinerals() {
			return minerals[0]; 
		}

		public void setMinerals(boolean minerals) {
			this.minerals[0] = minerals;
		}

		public boolean isS401() {
			return minerals[1];
		}

		public void setS401(boolean s401) {
			this.minerals[1] = s401;
		}

		public boolean isS402() {
			return minerals[2];
		}

		public void setS402(boolean s402) {
			this.minerals[2] = s402;
		}

		public boolean isS403() {
			return minerals[3];
		}

		public void setS403(boolean s403) {
			this.minerals[3] = s403;
		}

		public boolean isS404() {
			return minerals[4];
		}

		public void setS404(boolean s404) {
			this.minerals[4] = s404;
		}

		public boolean isS405() {
			return minerals[5];
		}

		public void setS405(boolean s405) {
			this.minerals[5] = s405;
		}
		
		public boolean isRefineries() {
			return refineries[0];
		}

		public void setRefineries(boolean refineries) {
			this.refineries[0] = refineries;
		}

		public boolean isPulpAndPaper() {
			return pulpAndPaper[0];
		}

		public void setPulpAndPaper(boolean pulpAndPaper) {
			this.pulpAndPaper[0] = pulpAndPaper;
		}

		public boolean isS601() {
			return pulpAndPaper[1];
		}

		public void setS601(boolean s601) {
			this.pulpAndPaper[1] = s601;
		}

		public boolean isS602() {
			return pulpAndPaper[2]; 
		}

		public void setS602(boolean s602) {
			this.pulpAndPaper[2] = s602;
		}

		public boolean isChemicals() {
			return chemicals[0];
		}

		public void setChemicals(boolean chemicals) {
			this.chemicals[0] = chemicals;
		}

		public boolean isS701() {
			return chemicals[1];
		}

		public void setS701(boolean s701) {
			this.chemicals[1] = s701;
		}

		public boolean isS702() {
			return chemicals[2];
		}

		public void setS702(boolean s702) {
			this.chemicals[2] = s702;
		}

		public boolean isS703() {
			return chemicals[3];
		}

		public void setS703(boolean s703) {
			this.chemicals[3] = s703;
		}

		public boolean isS704() {
			return chemicals[4];
		}

		public void setS704(boolean s704) {
			this.chemicals[4] = s704;
		}

		public boolean isS705() {
			return chemicals[5];
		}

		public void setS705(boolean s705) {
			this.chemicals[5] = s705;
		}

		public boolean isS706() {
			return chemicals[6];
		}

		public void setS706(boolean s706) {
			this.chemicals[6] = s706;
		}

		public boolean isS707() {
			return chemicals[7];
		}

		public void setS707(boolean s707) {
			this.chemicals[7] = s707;
		}

		public boolean isS708() {
			return chemicals[8];
		}

		public void setS708(boolean s708) {
			this.chemicals[8] = s708;
		}

		public boolean isS709() {
			return chemicals[9];
		}

		public void setS709(boolean s709) {
			this.chemicals[9] = s709;
		}

		public boolean isS710() {
			return chemicals[10];
		}

		public void setS710(boolean s710) {
			this.chemicals[10] = s710;
		}

		public boolean isS711() {
			return chemicals[11];
		}

		public void setS711(boolean s711) {
			this.chemicals[11] = s711;
		}

		public boolean isOther() {
			return other[0];
		}

		public void setOther(boolean other) {
			this.other[0] = other;
		}

		public boolean isS801() {
			return other[1];
		}

		public void setS801(boolean s801) {
			this.other[1] = s801;
		}

		public boolean isS802() {
			return other[2];
		}

		public void setS802(boolean s802) {
			this.other[2] = s802;
		}

		public boolean isS803() {
			return other[3];
		}

		public void setS803(boolean s803) {
			this.other[3] = s803;
		}

		public boolean isS804() {
			return other[4];
		}

		public void setS804(boolean s804) {
			this.other[4] = s804;
		}

		public boolean isS805() {
			return other[5];
		}

		public void setS805(boolean s805) {
			this.other[5] = s805;
		}

		public boolean isS806() {
			return other[6];
		}

		public void setS806(boolean s806) {
			this.other[6] = s806;
		}

		public boolean isS807() {
			return other[7];
		}

		public void setS807(boolean s807) {
			this.other[7] = s807;
		}

		public boolean isS808() {
			return other[8];
		}

		public void setS808(boolean s808) {
			this.other[8] = s808;
		}

		public boolean isS809() {
			return other[9];
		}

		public void setS809(boolean s809) {
			this.other[9] = s809;
		}

		public boolean isS810() {
			return other[10];
		}

		public void setS810(boolean s810) {
			this.other[10] = s810;
		}

		public boolean isPetroleumAndNaturalGas() {
			return petroleumAndNaturalGas[0];
		}

		public void setPetroleumAndNaturalGas(boolean petroleumAndNaturalGas) {
			this.petroleumAndNaturalGas[0] = petroleumAndNaturalGas;
		}

		public boolean isS901() {
			return petroleumAndNaturalGas[1];
		}

		public void setS901(boolean s901) {
			this.petroleumAndNaturalGas[1] = s901;
		}

		public boolean isS902() {
			return petroleumAndNaturalGas[2];
		}

		public void setS902(boolean s902) {
			this.petroleumAndNaturalGas[2] = s902;
		}

		public boolean isS903() {
			return petroleumAndNaturalGas[3];
		}

		public void setS903(boolean s903) {
			this.petroleumAndNaturalGas[3] = s903;
		}

		public boolean isS904() {
			return petroleumAndNaturalGas[4];
		}

		public void setS904(boolean s904) {
			this.petroleumAndNaturalGas[4] = s904;
		}

		public boolean isS905() {
			return petroleumAndNaturalGas[5];
		}

		public void setS905(boolean s905) {
			this.petroleumAndNaturalGas[5] = s905;
		}

		public boolean isS906() {
			return petroleumAndNaturalGas[6];
		}

		public void setS906(boolean s906) {
			this.petroleumAndNaturalGas[6] = s906;
		}

		public boolean isS907() {
			return petroleumAndNaturalGas[7];
		}

		public void setS907(boolean s907) {
			this.petroleumAndNaturalGas[7] = s907;
		}

		public boolean isS908() {
			return petroleumAndNaturalGas[8];
		}

		public void setS908(boolean s908) {
			this.petroleumAndNaturalGas[8] = s908;
		}

		public boolean isS909() {
			return petroleumAndNaturalGas[9];
		}

		public void setS909(boolean s909) {
			this.petroleumAndNaturalGas[9] = s909;
		}

		public boolean isS910() {
			return petroleumAndNaturalGas[10];
		}

		public void setS910(boolean s910) {
			this.petroleumAndNaturalGas[10] = s910;
		}

		public boolean isS911() {
			return petroleumAndNaturalGas[11];
		}

		public void setS911(boolean s911) {
			this.petroleumAndNaturalGas[11] = s911;
		}		
		
		public boolean isOnly(int flipIndex) {
			boolean retVal = false; 
			
			for ( int i = 1 ; i< matrix.length-1 ; i++) {
				retVal |= matrix[i][0]; 
			}
			
			for (int i = 1 ; i < petroleumAndNaturalGas.length ; i++) {
				if (i==flipIndex) {
					retVal |= !this.petroleumAndNaturalGas[i]; 
				}
				else {
					retVal |= this.petroleumAndNaturalGas[i]; 
				}
			}
			
			return !retVal; 
		}
		
		public boolean isLDCSectorOnly() {
			return isOnly(5);
		}

		public boolean isOnshorePetroleumSectorOnly() {
			return isOnly(2);
		}
	}

	
	


