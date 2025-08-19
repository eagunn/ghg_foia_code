package gov.epa.ghg.dto;

public class SectorFilter {

	private boolean powerPlants; //s1
	private boolean waste; //s2
	private boolean s201;
	private boolean s202;
	private boolean s203;
	private boolean s204;
	private boolean metals; //s3
	private boolean s301;
	private boolean s302;
	private boolean s303;
	private boolean s304;
	private boolean s305;
	private boolean s306;
	private boolean s307;
	private boolean minerals; //s4
	private boolean s401;
	private boolean s402;
	private boolean s403;
	private boolean s404;
	private boolean s405;
	private boolean refineries; //s5
	private boolean pulpAndPaper; //s6
	private boolean s601;
	private boolean s602;
	private boolean chemicals; //s7
	private boolean s701;
	private boolean s702;
	private boolean s703;
	private boolean s704;
	private boolean s705;
	private boolean s706;
	private boolean s707;
	private boolean s708;
	private boolean s709;
	private boolean s710;
	private boolean s711;
	private boolean other; //s8
	private boolean s801;
	private boolean s802;
	private boolean s803;
	private boolean s804;
	private boolean s805;
	private boolean s806;
	private boolean s807;
	private boolean s808;
	private boolean s809;
	private boolean s810;
	private boolean petroleumAndNaturalGas; //s9
	private boolean s901;
	private boolean s902;
	private boolean s903;
	private boolean s904;
	private boolean s905;
	private boolean s906;
	private boolean s907;
	private boolean s908;
	private boolean s909;
	private boolean s910;
	private boolean s911;
	
	public SectorFilter() {
	}

    public SectorFilter(Byte s1, Byte s2, Byte s3, Byte s4, Byte s5, Byte s6, Byte s7, Byte s8, Byte s9) {
        super();
        if (s1 != null && s1 > 0) this.powerPlants = true;
        
        if (s2 != null && s2 > 0) this.waste = true;
        this.s201 = true;
        this.s202 = true;
        this.s203 = true;
        this.s204 = true;
        
        if (s3 != null && s3 > 0) this.metals = true;
        this.s301 = true;
        this.s302 = true;
        this.s303 = true;
        this.s304 = true;
        this.s305 = true;
        this.s306 = true;
        this.s307 = true;
        
        if (s4 != null && s4 > 0) this.minerals = true;
        this.s401 = true;
        this.s402 = true;
        this.s403 = true;
        this.s404 = true;
        this.s405 = true;
        
        if (s5 != null && s5 > 0) this.refineries = true;
        
        if (s6 != null && s6 > 0) this.pulpAndPaper = true;
        
        this.s601 = true;
        this.s602 = true;
        
        if (s7 != null && s7 > 0) this.chemicals = true;
        this.s701 = true;
        this.s702 = true;
        this.s703 = true;
        this.s704 = true;
        this.s705 = true;
        this.s706 = true;
        this.s707 = true;
        this.s708 = true;
        this.s709 = true;
        this.s710 = true;
        this.s711 = true;
        
        if (s8 != null && s8 > 0) this.other = true;
        this.s801 = true;
        this.s802 = true;
        this.s803 = true;
        this.s804 = true;
        this.s805 = true;
        this.s806 = true;
        this.s807 = true;
        this.s808 = true;
        this.s809 = true;
        this.s810 = true;
        
        if (s9 != null && s9 > 0) this.petroleumAndNaturalGas = true;
        this.s901 = true;
        this.s902 = true;
        this.s903 = true;
        this.s904 = true;
        this.s905 = true;
        this.s906 = true;
        this.s907 = true;
        this.s908 = true;
        this.s909 = true;
        this.s910 = true;
        this.s911 = true;
    }
    
	public SectorFilter(Byte s1, Byte s2, Byte s3, Byte s4, Byte s5, Byte s6, Byte s7, Byte s8, Byte s9,
			Byte s201, Byte s202, Byte s203, Byte s204,
			Byte s301, Byte s302, Byte s303, Byte s304, Byte s305, Byte s306, Byte s307,
			Byte s401, Byte s402, Byte s403, Byte s404, Byte s405,
			Byte s601, Byte s602,
			Byte s701, Byte s702, Byte s703, Byte s704, Byte s705, Byte s706, Byte s707, Byte s708, Byte s709, Byte s710, Byte s711,
			Byte s801, Byte s802, Byte s803, Byte s804, Byte s805, Byte s806, Byte s807, Byte s808, Byte s809, Byte s810,
			Byte s901, Byte s902, Byte s903, Byte s904, Byte s905, Byte s906, Byte s907, Byte s908, Byte s909, Byte s910, Byte s911) {
		super();
		if (s1 != null && s1 > 0) this.powerPlants = true;
		
		if (s2 != null && s2 > 0) this.waste = true;
		if (s201 != null && s201 > 0) this.s201 = true;
		if (s202 != null && s202 > 0) this.s202 = true;
		if (s203 != null && s203 > 0) this.s203 = true;
		if (s204 != null && s204 > 0) this.s204 = true;
		
		if (s3 != null && s3 > 0) this.metals = true;
		if (s301 != null && s301 > 0) this.s301 = true;
		if (s302 != null && s302 > 0) this.s302 = true;
		if (s303 != null && s303 > 0) this.s303 = true;
		if (s304 != null && s304 > 0) this.s304 = true;
		if (s305 != null && s305 > 0) this.s305 = true;
		if (s306 != null && s306 > 0) this.s306 = true;
		if (s307 != null && s307 > 0) this.s307 = true;
		
		if (s4 != null && s4 > 0) this.minerals = true;
		if (s401 != null && s401 > 0) this.s401 = true;
		if (s402 != null && s402 > 0) this.s402 = true;
		if (s403 != null && s403 > 0) this.s403 = true;
		if (s404 != null && s404 > 0) this.s404 = true;
		if (s405 != null && s405 > 0) this.s405 = true;
		
		if (s5 != null && s5 > 0) this.refineries = true;
		
		if (s6 != null && s6 > 0) this.pulpAndPaper = true;
		
		if (s601 != null && s601 > 0) this.s601 = true;
		if (s602 != null && s602 > 0) this.s602 = true;
		
		if (s7 != null && s7 > 0) this.chemicals = true;
		if (s701 != null && s701 > 0) this.s701 = true;
		if (s702 != null && s702 > 0) this.s702 = true;
		if (s703 != null && s703 > 0) this.s703 = true;
		if (s704 != null && s704 > 0) this.s704 = true;
		if (s705 != null && s705 > 0) this.s705 = true;
		if (s706 != null && s706 > 0) this.s706 = true;
		if (s707 != null && s707 > 0) this.s707 = true;
		if (s708 != null && s708 > 0) this.s708 = true;
		if (s709 != null && s709 > 0) this.s709 = true;
		if (s710 != null && s710 > 0) this.s710 = true;
		if (s711 != null && s711 > 0) this.s711 = true;
		
		if (s8 != null && s8 > 0) this.other = true;
		if (s801 != null && s801 > 0) this.s801 = true;
		if (s802 != null && s802 > 0) this.s802 = true;
		if (s803 != null && s803 > 0) this.s803 = true;
		if (s804 != null && s804 > 0) this.s804 = true;
		if (s805 != null && s805 > 0) this.s805 = true;
		if (s806 != null && s806 > 0) this.s806 = true;
		if (s807 != null && s807 > 0) this.s807 = true;
		if (s808 != null && s808 > 0) this.s808 = true;
		if (s809 != null && s809 > 0) this.s809 = true;
		if (s810 != null && s810 > 0) this.s810 = true;
		
		if (s9 != null && s9 > 0) this.petroleumAndNaturalGas = true;
		if (s901 != null && s901 > 0) this.s901 = true;
		if (s902 != null && s902 > 0) this.s902 = true;
		if (s903 != null && s903 > 0) this.s903 = true;
		if (s904 != null && s904 > 0) this.s904 = true;
		if (s905 != null && s905 > 0) this.s905 = true;
		if (s906 != null && s906 > 0) this.s906 = true;
		if (s907 != null && s907 > 0) this.s907 = true;
		if (s908 != null && s908 > 0) this.s908 = true;
		if (s909 != null && s909 > 0) this.s909 = true;
		if (s910 != null && s910 > 0) this.s910 = true;
		if (s911 != null && s911 > 0) this.s911 = true;
	}

	public boolean isPowerPlants() {
		return powerPlants;
	}

	public void setPowerPlants(boolean powerPlants) {
		this.powerPlants = powerPlants;
	}

	public boolean isWaste() {
		return waste;
	}

	public void setWaste(boolean waste) {
		this.waste = waste;
	}

	public boolean isS201() {
		return s201;
	}

	public void setS201(boolean s201) {
		this.s201 = s201;
	}

	public boolean isS202() {
		return s202;
	}

	public void setS202(boolean s202) {
		this.s202 = s202;
	}

	public boolean isS203() {
		return s203;
	}

	public void setS203(boolean s203) {
		this.s203 = s203;
	}

	public boolean isS204() {
		return s204;
	}

	public void setS204(boolean s204) {
		this.s204 = s204;
	}

	public boolean isMetals() {
		return metals;
	}

	public void setMetals(boolean metals) {
		this.metals = metals;
	}

	public boolean isS301() {
		return s301;
	}

	public void setS301(boolean s301) {
		this.s301 = s301;
	}

	public boolean isS302() {
		return s302;
	}

	public void setS302(boolean s302) {
		this.s302 = s302;
	}

	public boolean isS303() {
		return s303;
	}

	public void setS303(boolean s303) {
		this.s303 = s303;
	}

	public boolean isS304() {
		return s304;
	}

	public void setS304(boolean s304) {
		this.s304 = s304;
	}

	public boolean isS305() {
		return s305;
	}

	public void setS305(boolean s305) {
		this.s305 = s305;
	}

	public boolean isS306() {
		return s306;
	}

	public void setS306(boolean s306) {
		this.s306 = s306;
	}

	public boolean isS307() {
		return s307;
	}

	public void setS307(boolean s307) {
		this.s307 = s307;
	}

	public boolean isMinerals() {
		return minerals;
	}

	public void setMinerals(boolean minerals) {
		this.minerals = minerals;
	}

	public boolean isS401() {
		return s401;
	}

	public void setS401(boolean s401) {
		this.s401 = s401;
	}

	public boolean isS402() {
		return s402;
	}

	public void setS402(boolean s402) {
		this.s402 = s402;
	}

	public boolean isS403() {
		return s403;
	}

	public void setS403(boolean s403) {
		this.s403 = s403;
	}

	public boolean isS404() {
		return s404;
	}

	public void setS404(boolean s404) {
		this.s404 = s404;
	}

	public boolean isS405() {
		return s405;
	}

	public void setS405(boolean s405) {
		this.s405 = s405;
	}
	
	public boolean isRefineries() {
		return refineries;
	}

	public void setRefineries(boolean refineries) {
		this.refineries = refineries;
	}

	public boolean isPulpAndPaper() {
		return pulpAndPaper;
	}

	public void setPulpAndPaper(boolean pulpAndPaper) {
		this.pulpAndPaper = pulpAndPaper;
	}

	public boolean isS601() {
		return s601;
	}

	public void setS601(boolean s601) {
		this.s601 = s601;
	}

	public boolean isS602() {
		return s602;
	}

	public void setS602(boolean s602) {
		this.s602 = s602;
	}

	public boolean isChemicals() {
		return chemicals;
	}

	public void setChemicals(boolean chemicals) {
		this.chemicals = chemicals;
	}

	public boolean isS701() {
		return s701;
	}

	public void setS701(boolean s701) {
		this.s701 = s701;
	}

	public boolean isS702() {
		return s702;
	}

	public void setS702(boolean s702) {
		this.s702 = s702;
	}

	public boolean isS703() {
		return s703;
	}

	public void setS703(boolean s703) {
		this.s703 = s703;
	}

	public boolean isS704() {
		return s704;
	}

	public void setS704(boolean s704) {
		this.s704 = s704;
	}

	public boolean isS705() {
		return s705;
	}

	public void setS705(boolean s705) {
		this.s705 = s705;
	}

	public boolean isS706() {
		return s706;
	}

	public void setS706(boolean s706) {
		this.s706 = s706;
	}

	public boolean isS707() {
		return s707;
	}

	public void setS707(boolean s707) {
		this.s707 = s707;
	}

	public boolean isS708() {
		return s708;
	}

	public void setS708(boolean s708) {
		this.s708 = s708;
	}

	public boolean isS709() {
		return s709;
	}

	public void setS709(boolean s709) {
		this.s709 = s709;
	}

	public boolean isS710() {
		return s710;
	}

	public void setS710(boolean s710) {
		this.s710 = s710;
	}

	public boolean isS711() {
		return s711;
	}

	public void setS711(boolean s711) {
		this.s711 = s711;
	}

	public boolean isOther() {
		return other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}

	public boolean isS801() {
		return s801;
	}

	public void setS801(boolean s801) {
		this.s801 = s801;
	}

	public boolean isS802() {
		return s802;
	}

	public void setS802(boolean s802) {
		this.s802 = s802;
	}

	public boolean isS803() {
		return s803;
	}

	public void setS803(boolean s803) {
		this.s803 = s803;
	}

	public boolean isS804() {
		return s804;
	}

	public void setS804(boolean s804) {
		this.s804 = s804;
	}

	public boolean isS805() {
		return s805;
	}

	public void setS805(boolean s805) {
		this.s805 = s805;
	}

	public boolean isS806() {
		return s806;
	}

	public void setS806(boolean s806) {
		this.s806 = s806;
	}

	public boolean isS807() {
		return s807;
	}

	public void setS807(boolean s807) {
		this.s807 = s807;
	}

	public boolean isS808() {
		return s808;
	}

	public void setS808(boolean s808) {
		this.s808 = s808;
	}

	public boolean isS809() {
		return s809;
	}

	public void setS809(boolean s809) {
		this.s809 = s809;
	}

	public boolean isS810() {
		return s810;
	}

	public void setS810(boolean s810) {
		this.s810 = s810;
	}

	public boolean isPetroleumAndNaturalGas() {
		return petroleumAndNaturalGas;
	}

	public void setPetroleumAndNaturalGas(boolean petroleumAndNaturalGas) {
		this.petroleumAndNaturalGas = petroleumAndNaturalGas;
	}

	public boolean isS901() {
		return s901;
	}

	public void setS901(boolean s901) {
		this.s901 = s901;
	}

	public boolean isS902() {
		return s902;
	}

	public void setS902(boolean s902) {
		this.s902 = s902;
	}

	public boolean isS903() {
		return s903;
	}

	public void setS903(boolean s903) {
		this.s903 = s903;
	}

	public boolean isS904() {
		return s904;
	}

	public void setS904(boolean s904) {
		this.s904 = s904;
	}

	public boolean isS905() {
		return s905;
	}

	public void setS905(boolean s905) {
		this.s905 = s905;
	}

	public boolean isS906() {
		return s906;
	}

	public void setS906(boolean s906) {
		this.s906 = s906;
	}

	public boolean isS907() {
		return s907;
	}

	public void setS907(boolean s907) {
		this.s907 = s907;
	}

	public boolean isS908() {
		return s908;
	}

	public void setS908(boolean s908) {
		this.s908 = s908;
	}

	public boolean isS909() {
		return s909;
	}

	public void setS909(boolean s909) {
		this.s909 = s909;
	}
	
	public boolean isS910() {
		return s910;
	}

	public void setS910(boolean s910) {
		this.s910 = s910;
	}

	public boolean isS911() {
		return s911;
	}

	public void setS911(boolean s911) {
		this.s911 = s911;
	}

	public boolean isLDCSectorOnly() {
        if (powerPlants || waste || metals || minerals || refineries || pulpAndPaper || chemicals || other
        		|| s901 || s902 || s903 || s904 || !s905 || s906 || s907 || s908 || s909 || s910 || s911) return false;
        return true;
	}

	public boolean isOnshorePetroleumSectorOnly() {
        if (powerPlants || waste || metals || minerals || refineries || pulpAndPaper || chemicals || other
        		|| s901 || !s902 || s903 || s904 || s905 || s906 || s907 || s908 || s909 || s910 || s911) return false;
        return true;
	}

	public boolean isBoostingSectorOnly() {
        return ( this.isPetroleumAndNaturalGas() && !s901 && !s902 && !s903 && !s904 && !s905 && !s906 && !s907 && !s908 && s910 && !s911 );
	}

	public boolean isPipeSectorOnly() {
        return ( this.isPetroleumAndNaturalGas() && !s901 && !s902 && !s903 && !s904 && !s905 && !s906 && !s907 && !s908 && !s910 && s911 );
	}
}
