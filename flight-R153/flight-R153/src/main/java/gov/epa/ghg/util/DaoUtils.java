package gov.epa.ghg.util;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DaoUtils {
	
	public static final String forceExclude = "XX";
	private static DecimalFormat df = new DecimalFormat("00000");
	
	public static final String POWERPLANTS = "POWERPLANTS";
	public static final String WASTE = "WASTE";
	public static final String METALS = "METALS";
	public static final String MINERALS = "MINERALS";
	public static final String REFINERIES = "REFINERIES";
	public static final String PULPANDPAPER = "PULPANDPAPER";
	public static final String CHEMICALS = "CHEMICALS";
	public static final String OTHER = "OTHER";
	public static final String PETRO_NG = "PETRO_NG";
	
	public static final String EMISSIONS_MIN = "-20000";
	
	private DaoUtils() {
	}
	
	public static String shouldFacilitiesWithNullEmissionsBeIncluded(String lowE) {
		if (EMISSIONS_MIN.equals(lowE)) {
			return "sum(e.co2eEmission) is null or ";
		} else {
			return "";
		}
	}
	
	public static String shouldFacilitiesWithNullEmissionsBeIncludedSql(String lowE) {
		if (EMISSIONS_MIN.equals(lowE)) {
			return " sum(emissions8_.CO2E_EMISSION) is null or ";
		} else {
			return "";
		}
	}
	
	public static String shouldTrendOrderByClauseBeIncluded(boolean isTrend, FacilityType type) {
		if (!isTrend) {
			return "";
		}
		switch (type) {
			case EMITTERS:
			case ONSHORE:
				return " order by f.facilityName, e.id.year ";
			case SUPPLIERS:
				return " order by f.id.facilityId, e.id.year";
		}
		return "";
	}
	
	public static String trendFilter(boolean isTrend, int year, int startYear) {
		if (!isTrend) {
			return "and f.id.year = " + year;
		}
		return "and e.id.year >= " + startYear;
	}
	
	public static String shouldTrendBeIncluded(boolean isTrend) {
		if (!isTrend) {
			return "";
		}
		return ", e.id.year ";
	}
	
	public static String getEmitterGeoSelectionQueryFromRequest(FlightRequest request) {
		String state = request.getState();
		String emissionsType = request.getEmissionsType();
		SectorFilter sectors = request.sectors();
		String countyFips = request.countyFips();
		String q = request.getQuery();
		int year = request.getReportingYear();
		GasFilter gases = request.gases();
		QueryOptions qo = request.queryOptions();
		String lowE = request.lowE();
		String highE = request.highE();
		Long tribalLandId = request.getTribalLandId();
		String reportingStatus = request.getReportingStatus();
		return "from DimFacility f join f.facStatus fs "
				+ DaoUtils.emissionsTypeFilter(emissionsType)
				+ "left join e.sector s "
				+ "left join e.gas g "
				+ "left join f.tribalLand "
				+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
				+ "where f.id in (select distinct f.id "
				+ "from DimFacility f "
				+ DaoUtils.emissionsTypeFilter(emissionsType)
				+ "left join e.sector s "
				+ "left join e.gas g "
				+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
				+ "where (s.sectorType is null or s.sectorType = 'E') "
				+ DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo)
				+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
				+ "group by f.id having "
				+ shouldFacilitiesWithNullEmissionsBeIncluded(lowE)
				+ "(sum(e.co2eEmission) >= " + lowE + " and sum(e.co2eEmission) <= " + highE + ")) "
				+ DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo)
				+ DaoUtils.tribalLandWhereClause(state, tribalLandId)
				+ "and ("
				+ DaoUtils.gasFilter(gases)
				+ "g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = " + year + " and fs.facilityType = 'E' "
				+ ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
	}
	
	public static String emitterSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		String hQuery = "and ((0=1)";
		if (sectors.isPowerPlants()) {
			hQuery += " or s.sectorCode = '" + DaoUtils.POWERPLANTS + "'";
		}
		if (sectors.isWaste()) {
			hQuery += emitterWasteSectorFilter(sectors);
		}
		if (sectors.isMetals()) {
			hQuery += emitterMetalsSectorFilter(sectors);
		}
		if (sectors.isMinerals()) {
			hQuery += emitterMineralsSectorFilter(sectors);
		}
		if (sectors.isRefineries()) {
			hQuery += " or s.sectorCode = '" + DaoUtils.REFINERIES + "'";
		}
		if (sectors.isPulpAndPaper()) {
			hQuery += emitterPulpSectorFilter(sectors);
		}
		if (sectors.isChemicals()) {
			hQuery += emitterChemicalsSectorFilter(sectors);
		}
		if (sectors.isOther()) {
			hQuery += emitterOtherSectorFilter(sectors, state, fipsCode);
		}
		if (sectors.isPetroleumAndNaturalGas()) {
			hQuery += emitterPetroleumSectorFilter(sectors, state, fipsCode);
		}
		hQuery += ") ";
		return hQuery;
	}
	
	public static String emitterSectorFilterSql(SectorFilter sectors, String state, String fipsCode, String tableAliasAndColumnName, String subSectorAlias) {
		// String hQuery = "and ((0=1) or s.sectorCode is null";
		//String hQuery = "and ((0=1)";
		StringBuilder sectorQuery = new StringBuilder();
		sectorQuery.append(" and ((0=1)");
		if (sectors.isPowerPlants()) {
			sectorQuery.append(" OR " + tableAliasAndColumnName + " = 'POWERPLANTS'");
			//hQuery += " or s.sectorCode = '" + DaoUtils.POWERPLANTS + "'";
		}
		if (sectors.isWaste()) {
			sectorQuery.append(emitterWasteSectorFilterSql(sectors, tableAliasAndColumnName, subSectorAlias));
			//hQuery += emitterWasteSectorFilter(sectors);
		}
		if (sectors.isMetals()) {
			//hQuery += emitterMetalsSectorFilter(sectors);
			sectorQuery.append(emitterMetalsSectorFilterSql(sectors, tableAliasAndColumnName, subSectorAlias));
		}
		if (sectors.isMinerals()) {
			//hQuery += emitterMineralsSectorFilter(sectors);
			sectorQuery.append(emitterMineralsSectorFilterSql(sectors, tableAliasAndColumnName, subSectorAlias));
			
		}
		if (sectors.isRefineries()) {
			sectorQuery.append(" OR " + tableAliasAndColumnName + " = 'REFINERIES'");
			//hQuery += " or s.sectorCode = '" + DaoUtils.REFINERIES + "'";
		}
		if (sectors.isPulpAndPaper()) {
			sectorQuery.append(emitterPulpSectorFilterSql(sectors, tableAliasAndColumnName, subSectorAlias));
			//hQuery += emitterPulpSectorFilter(sectors);
		}
		if (sectors.isChemicals()) {
			//hQuery += emitterChemicalsSectorFilter(sectors);
			sectorQuery.append(emitterChemicalsSectorFilterSql(sectors, tableAliasAndColumnName, subSectorAlias));
		}
		if (sectors.isOther()) {
			//hQuery += emitterOtherSectorFilter(sectors, state, fipsCode);
			sectorQuery.append(emitterOtherSectorFilterSql(sectors, state, fipsCode, tableAliasAndColumnName, subSectorAlias));
		}
		if (sectors.isPetroleumAndNaturalGas()) {
			sectorQuery.append(emitterPetroleumSectorFilterSql(sectors, state, fipsCode, tableAliasAndColumnName, subSectorAlias));
			//hQuery += emitterPetroleumSectorFilter(sectors, state, fipsCode);
		}
		//hQuery += ") ";
		sectorQuery.append(") ");
		return sectorQuery.toString();
	}
	
	public static String emitterAllSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		// String hQuery = "and ((0=1) or s.sectorCode is null";
		String hQuery = "and ((0=1)";
		if (sectors.isPowerPlants()) {
			hQuery += " or s.sectorCode = '" + DaoUtils.POWERPLANTS + "'";
		}
		if (sectors.isWaste()) {
			hQuery += emitterWasteSectorFilter(sectors);
		}
		if (sectors.isMetals()) {
			hQuery += emitterMetalsSectorFilter(sectors);
		}
		if (sectors.isMinerals()) {
			hQuery += emitterMineralsSectorFilter(sectors);
		}
		if (sectors.isRefineries()) {
			hQuery += " or s.sectorCode = '" + DaoUtils.REFINERIES + "'";
		}
		if (sectors.isPulpAndPaper()) {
			hQuery += emitterPulpSectorFilter(sectors);
		}
		if (sectors.isChemicals()) {
			hQuery += emitterChemicalsSectorFilter(sectors);
		}
		if (sectors.isOther()) {
			hQuery += emitterOtherSectorFilter(sectors, state, fipsCode);
		}
		if (sectors.isPetroleumAndNaturalGas()) {
			hQuery += emitterAllPetroleumSectorFilter(sectors, state, fipsCode);
		}
		hQuery += ") ";
		return hQuery;
	}
	
	public static String emitterSubSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		if ((!sectors.isWaste() || (sectors.isWaste() && sectors.isS201() && sectors.isS202() && sectors.isS203() && sectors.isS204()))
				&& (!sectors.isMetals() || (sectors.isMetals() && sectors.isS301() && sectors.isS302() && sectors.isS303() && sectors.isS304() && sectors.isS305() && sectors.isS306() && sectors.isS307()))
				&& (!sectors.isMinerals() || (sectors.isMinerals() && sectors.isS401() && sectors.isS402() && sectors.isS403() && sectors.isS404() && sectors.isS405()))
				&& (!sectors.isPulpAndPaper() || (sectors.isPulpAndPaper() && sectors.isS601() && sectors.isS602()))
				&& (!sectors.isChemicals() || (sectors.isChemicals() && sectors.isS701() && sectors.isS702() && sectors.isS703() && sectors.isS704()
						&& sectors.isS705() && sectors.isS706() && sectors.isS707() && sectors.isS708() && sectors.isS709() && sectors.isS710() && sectors.isS711())) && (!sectors.isOther()
								|| (sectors.isOther() && sectors.isS801() && sectors.isS802() && sectors.isS803() && sectors.isS804()
						&& sectors.isS805() && sectors.isS806() && (sectors.isS807()
								&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode))
						&& sectors.isS808() && sectors.isS809() && sectors.isS810()))
				&& (!sectors.isPetroleumAndNaturalGas() || (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && (sectors.isS902()
						&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) && sectors.isS903() && sectors.isS904() && sectors.isS905()
						&& sectors.isS906() && sectors.isS907() && sectors.isS908() && (sectors.isS910() && !org.springframework.util.StringUtils.hasLength(state)
								&& !org.springframework.util.StringUtils.hasLength(fipsCode)) && (sectors.isS911() && !org.springframework.util.StringUtils.hasLength(state)
										&& !org.springframework.util.StringUtils.hasLength(fipsCode))))) {
			return StringUtils.EMPTY;
		} else {
			return "join e.subSector ss ";
		}
	}
	
	public static String emitterSubSectorFilterSql(SectorFilter sectors, String state, String fipsCode, String tableAndColAlias) {
		if ((!sectors.isWaste() || (sectors.isWaste() && sectors.isS201() && sectors.isS202() && sectors.isS203() && sectors.isS204()))
				&& (!sectors.isMetals() || (sectors.isMetals() && sectors.isS301() && sectors.isS302() && sectors.isS303() && sectors.isS304() && sectors.isS305() && sectors.isS306() && sectors.isS307()))
				&& (!sectors.isMinerals() || (sectors.isMinerals() && sectors.isS401() && sectors.isS402() && sectors.isS403() && sectors.isS404() && sectors.isS405()))
				&& (!sectors.isPulpAndPaper() || (sectors.isPulpAndPaper() && sectors.isS601() && sectors.isS602()))
				&& (!sectors.isChemicals() || (sectors.isChemicals() && sectors.isS701() && sectors.isS702() && sectors.isS703() && sectors.isS704()
						&& sectors.isS705() && sectors.isS706() && sectors.isS707() && sectors.isS708() && sectors.isS709() && sectors.isS710() && sectors.isS711())) && (!sectors.isOther()
								|| (sectors.isOther() && sectors.isS801() && sectors.isS802() && sectors.isS803() && sectors.isS804()
						&& sectors.isS805() && sectors.isS806() && (sectors.isS807()
								&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode))
						&& sectors.isS808() && sectors.isS809() && sectors.isS810()))
				&& (!sectors.isPetroleumAndNaturalGas() || (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && (sectors.isS902()
						&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) && sectors.isS903() && sectors.isS904() && sectors.isS905()
						&& sectors.isS906() && sectors.isS907() && sectors.isS908() && (sectors.isS910() && !org.springframework.util.StringUtils.hasLength(state)
								&& !org.springframework.util.StringUtils.hasLength(fipsCode)) && (sectors.isS911() && !org.springframework.util.StringUtils.hasLength(state)
										&& !org.springframework.util.StringUtils.hasLength(fipsCode))))) {
			return StringUtils.EMPTY;
		} else {
			return " INNER JOIN PUB_DIM_SUBSECTOR " + tableAndColAlias;
		}
	}
	
	public static String ldcSubSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		if ((!sectors.isWaste() || (sectors.isWaste() && sectors.isS201() && sectors.isS202() && sectors.isS203() && sectors.isS204()))
				&& (!sectors.isMetals() || (sectors.isMetals() && sectors.isS301() && sectors.isS302() && sectors.isS303() && sectors.isS304() && sectors.isS305() && sectors.isS306() && sectors.isS307()))
				&& (!sectors.isMinerals() || (sectors.isMinerals() && sectors.isS401() && sectors.isS402() && sectors.isS403() && sectors.isS404() && sectors.isS405()))
				&& (!sectors.isPulpAndPaper() || (sectors.isPulpAndPaper() && sectors.isS601() && sectors.isS602()))
				&& (!sectors.isChemicals() || (sectors.isChemicals() && sectors.isS701() && sectors.isS702() && sectors.isS703() && sectors.isS704()
						&& sectors.isS705() && sectors.isS706() && sectors.isS707() && sectors.isS708() && sectors.isS709() && sectors.isS710() && sectors.isS711())) && (!sectors.isOther()
								|| (sectors.isOther() && sectors.isS801() && sectors.isS802() && sectors.isS803() && sectors.isS804()
						&& sectors.isS805() && sectors.isS806() && (sectors.isS807()
								&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode))
						&& sectors.isS808() && sectors.isS809() && sectors.isS810()))
				&& (!sectors.isPetroleumAndNaturalGas() || (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && (sectors.isS902()
						&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) && sectors.isS903() && sectors.isS904() && sectors.isS905()
						&& sectors.isS906() && sectors.isS907() && sectors.isS908() && (sectors.isS910() && !org.springframework.util.StringUtils.hasLength(state)
								&& !org.springframework.util.StringUtils.hasLength(fipsCode)) && (sectors.isS911() && !org.springframework.util.StringUtils.hasLength(state)
										&& !org.springframework.util.StringUtils.hasLength(fipsCode))))) {
			return StringUtils.EMPTY;
		} else {
			return "join pub_dim_subsector ss on e.subsector_id = ss.subsector_id ";
		}
	}
	
	public static String emitterWasteSectorFilter(SectorFilter sectors) {
		if (!sectors.isWaste()) {
			return StringUtils.EMPTY;
		} else if (sectors.isWaste() && sectors.isS201() && sectors.isS202() && sectors.isS203() && sectors.isS204()) {
			return " or s.sectorCode = '" + DaoUtils.WASTE + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS201()) {
				hQuery += " or ss.subSectorName = 'HH'";
			}
			if (sectors.isS202()) {
				hQuery += " or ss.subSectorName = 'TT'";
			}
			if (sectors.isS203()) {
				hQuery += " or ss.subSectorName = 'II'";
			}
			if (sectors.isS204()) {
				hQuery += " or ss.subSectorName = 'C_COMB'";
			}
			return hQuery;
		}
	}
	
	public static String emitterWasteSectorFilterSql(SectorFilter sectors, String tableAliasAndColumnName, String subSectorAlias) {
		if (!sectors.isWaste()) {
			return StringUtils.EMPTY;
		} else if (sectors.isWaste() && sectors.isS201() && sectors.isS202() && sectors.isS203() && sectors.isS204()) {
			//return " or s.sectorCode = '" + DaoUtils.WASTE + "'";
			return " OR " + tableAliasAndColumnName + " = 'WASTE' ";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS201() || sectors.isS202() || sectors.isS203() ||  sectors.isS204()) {
				if (sectors.isS201()) {
					hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'HH'";
				}
				if (sectors.isS202()) {
					hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'TT'";
				}
				if (sectors.isS203()) {
					hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'II'";
				}
				if (sectors.isS204()) {
					hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'C_COMB'";
				}
			}
			return hQuery;
		}
	}
	
	public static String emitterMetalsSectorFilter(SectorFilter sectors) {
		if (!sectors.isMetals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isMetals() && sectors.isS301() && sectors.isS302() && sectors.isS303() && sectors.isS304() && sectors.isS305() && sectors.isS306() && sectors.isS307()) {
			return " or s.sectorCode = '" + DaoUtils.METALS + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS301()) {
				hQuery += " or ss.subSectorName = 'F'";
			}
			if (sectors.isS302()) {
				hQuery += " or ss.subSectorName = 'K'";
			}
			if (sectors.isS303()) {
				hQuery += " or ss.subSectorName = 'Q'";
			}
			if (sectors.isS304()) {
				hQuery += " or ss.subSectorName = 'R'";
			}
			if (sectors.isS305()) {
				hQuery += " or ss.subSectorName = 'T'";
			}
			if (sectors.isS306()) {
				hQuery += " or ss.subSectorName = 'GG'";
			}
			if (sectors.isS307()) {
				hQuery += " or ss.subSectorName = 'C_METAL'";
			}
			return hQuery;
		}
	}
	
	public static String emitterMetalsSectorFilterSql(SectorFilter sectors, String tableAliasAndColumnName, String subSectorAlias) {
		if (!sectors.isMetals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isMetals() && sectors.isS301() && sectors.isS302() && sectors.isS303() && sectors.isS304() && sectors.isS305() && sectors.isS306() && sectors.isS307()) {
			//return " or s.sectorCode = '" + DaoUtils.METALS + "'";
			return " OR " + tableAliasAndColumnName + " = 'METALS' ";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS301()) {
				//hQuery += " or ss.subSectorName = 'F'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'F'";
			}
			if (sectors.isS302()) {
				//hQuery += " or ss.subSectorName = 'K'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'K'";
			}
			if (sectors.isS303()) {
				//hQuery += " or ss.subSectorName = 'Q'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'Q'";
			}
			if (sectors.isS304()) {
				//hQuery += " or ss.subSectorName = 'R'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'K'";
			}
			if (sectors.isS305()) {
				//hQuery += " or ss.subSectorName = 'T'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'R'";
			}
			if (sectors.isS306()) {
				//hQuery += " or ss.subSectorName = 'GG'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'GG'";
			}
			if (sectors.isS307()) {
				//hQuery += " or ss.subSectorName = 'C_METAL'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'C_METAL'";
			}
			return hQuery;
		}
	}
	
	public static String emitterMineralsSectorFilter(SectorFilter sectors) {
		if (!sectors.isMinerals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isMinerals() && sectors.isS401() && sectors.isS402() && sectors.isS403() && sectors.isS404() && sectors.isS405()) {
			return " or s.sectorCode = '" + DaoUtils.MINERALS + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS401()) {
				hQuery += " or ss.subSectorName = 'H'";
			}
			if (sectors.isS402()) {
				hQuery += " or ss.subSectorName = 'N'";
			}
			if (sectors.isS403()) {
				hQuery += " or ss.subSectorName = 'S'";
			}
			if (sectors.isS404()) {
				hQuery += " or ss.subSectorName = 'CC'";
			}
			if (sectors.isS405()) {
				hQuery += " or ss.subSectorName = 'C_MINERAL'";
			}
			return hQuery;
		}
	}
	
	public static String emitterMineralsSectorFilterSql(SectorFilter sectors, String tableAliasAndColumnName, String subSectorAlias) {
		if (!sectors.isMinerals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isMinerals() && sectors.isS401() && sectors.isS402() && sectors.isS403() && sectors.isS404() && sectors.isS405()) {
			//return " or s.sectorCode = '" + DaoUtils.MINERALS + "'";
			return " OR " + tableAliasAndColumnName + " = 'MINERALS'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS401()) {
				//hQuery += " or ss.subSectorName = 'H'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'H'";
			}
			if (sectors.isS402()) {
				//hQuery += " or ss.subSectorName = 'N'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'N'";
			}
			if (sectors.isS403()) {
				//hQuery += " or ss.subSectorName = 'S'";
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'S'";
			}
			if (sectors.isS404()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'CC'";
				//hQuery += " or ss.subSectorName = 'CC'";
			}
			if (sectors.isS405()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'C_MINERAL'";
				//hQuery += " or ss.subSectorName = 'C_MINERAL'";
			}
			return hQuery;
		}
	}
	
	public static String emitterPulpSectorFilter(SectorFilter sectors) {
		if (!sectors.isPulpAndPaper()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPulpAndPaper() && sectors.isS601() && sectors.isS602()) {
			return " or s.sectorCode = '" + DaoUtils.PULPANDPAPER + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS601()) {
				hQuery += " or ss.subSectorName = 'AA'";
			}
			if (sectors.isS602()) {
				hQuery += " or ss.subSectorName = 'C_PAPER'";
			}
			return hQuery;
		}
	}
	
	public static String emitterPulpSectorFilterSql(SectorFilter sectors, String tableAliasAndColumnName, String subSectorAlias) {
		if (!sectors.isPulpAndPaper()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPulpAndPaper() && sectors.isS601() && sectors.isS602()) {
			//return " or s.sectorCode = '" + DaoUtils.PULPANDPAPER + "'";
			return " OR " + tableAliasAndColumnName + " = 'PULPANDPAPER'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS601()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'AA'";
			}
			if (sectors.isS602()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'C_PAPER'";
				
			}
			return hQuery;
		}
	}
	
	public static String emitterChemicalsSectorFilter(SectorFilter sectors) {
		if (!sectors.isChemicals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isChemicals() && sectors.isS701() && sectors.isS702() && sectors.isS703() && sectors.isS704() && sectors.isS705() && sectors.isS706() && sectors.isS707()
				&& sectors.isS708() && sectors.isS709() && sectors.isS710() && sectors.isS711()) {
			return " or s.sectorCode = '" + DaoUtils.CHEMICALS + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS701()) {
				hQuery += " or ss.subSectorName = 'E'";
			}
			if (sectors.isS702()) {
				hQuery += " or ss.subSectorName = 'G'";
			}
			if (sectors.isS703()) {
				hQuery += " or ss.subSectorName = 'L'";
			}
			if (sectors.isS704()) {
				hQuery += " or ss.subSectorName = 'O'";
			}
			if (sectors.isS705()) {
				hQuery += " or ss.subSectorName = 'P'";
			}
			if (sectors.isS706()) {
				hQuery += " or ss.subSectorName = 'V'";
			}
			if (sectors.isS707()) {
				hQuery += " or ss.subSectorName = 'X'";
			}
			if (sectors.isS708()) {
				hQuery += " or ss.subSectorName = 'Z'";
			}
			if (sectors.isS709()) {
				hQuery += " or ss.subSectorName = 'BB'";
			}
			if (sectors.isS710()) {
				hQuery += " or ss.subSectorName = 'EE'";
			}
			if (sectors.isS711()) {
				hQuery += " or ss.subSectorName = 'C_CHEM'";
			}
			return hQuery;
		}
	}
	
	public static String emitterChemicalsSectorFilterSql(SectorFilter sectors, String tableAliasAndColumnName, String subSectorAlias) {
		if (!sectors.isChemicals()) {
			return StringUtils.EMPTY;
		} else if (sectors.isChemicals() && sectors.isS701() && sectors.isS702() && sectors.isS703() && sectors.isS704() && sectors.isS705() && sectors.isS706() && sectors.isS707()
				&& sectors.isS708() && sectors.isS709() && sectors.isS710() && sectors.isS711()) {
			//return " or s.sectorCode = '" + DaoUtils.CHEMICALS + "'";
			return " OR " + tableAliasAndColumnName + " = 'CHEMICALS'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS701()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'E'";
			}
			if (sectors.isS702()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'G'";
			}
			if (sectors.isS703()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'L'";
			}
			if (sectors.isS704()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'O'";
			}
			if (sectors.isS705()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'P'";
			}
			if (sectors.isS706()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'V'";
			}
			if (sectors.isS707()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'X'";
			}
			if (sectors.isS708()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'Z'";
			}
			if (sectors.isS709()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'BB'";
			}
			if (sectors.isS710()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'EE'";
			}
			if (sectors.isS711()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'C_CHEM'";
			}
			return hQuery;
		}
	}
	
	public static String emitterOtherSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		// For Subpart DD, do not include aggregates at the state, county level since emissions may span across states.
		if (!sectors.isOther()) {
			return StringUtils.EMPTY;
		} else if (sectors.isOther() && sectors.isS801() && sectors.isS802() && sectors.isS803() && sectors.isS804() && sectors.isS805() && sectors.isS806() && sectors.isS807() && sectors.isS808() && sectors.isS809() && sectors.isS810()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			return " or s.sectorCode = '" + DaoUtils.OTHER + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS801()) {
				hQuery += " or ss.subSectorName = 'FF'";
			}
			if (sectors.isS802()) {
				hQuery += " or ss.subSectorName = 'C_FOOD'";
			}
			if (sectors.isS803()) {
				hQuery += " or ss.subSectorName = 'C_ETHANOL'";
			}
			if (sectors.isS804()) {
				hQuery += " or ss.subSectorName = 'C_UNIV'";
			}
			if (sectors.isS805()) {
				hQuery += " or ss.subSectorName = 'C_MANUF'";
			}
			if (sectors.isS806()) {
				hQuery += " or ss.subSectorName = 'C_MIL'";
			}
			if (sectors.isS807() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or ss.subSectorName = 'DD'";
			}
			if (sectors.isS808()) {
				hQuery += " or ss.subSectorName = 'I'";
			}
			if (sectors.isS809()) {
				hQuery += " or ss.subSectorName = 'SS'";
			}
			if (sectors.isS810()) {
				hQuery += " or ss.subSectorName = 'C_OTHER'";
			}
			return hQuery;
		}
	}
	
	public static String emitterOtherSectorFilterSql(SectorFilter sectors, String state, String fipsCode, String tableAliasAndColumnName, String subsectorAlias) {
		// For Subpart DD, do not include aggregates at the state, county level since emissions may span across states.
		if (!sectors.isOther()) {
			return StringUtils.EMPTY;
		} else if (sectors.isOther() && sectors.isS801() && sectors.isS802() && sectors.isS803() && sectors.isS804() && sectors.isS805() && sectors.isS806() && sectors.isS807() && sectors.isS808() && sectors.isS809() && sectors.isS810()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			//return " or s.sectorCode = '" + DaoUtils.OTHER + "'";
			return " OR " + tableAliasAndColumnName + " = 'OTHER'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS801()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'FF'";
			}
			if (sectors.isS802()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_FOOD'";
			}
			if (sectors.isS803()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_ETHANOL'";
			}
			if (sectors.isS804()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_UNIV'";
			}
			if (sectors.isS805()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_MANUF'";
			}
			if (sectors.isS806()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_MIL'";
			}
			if (sectors.isS807() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'DD'";
			}
			if (sectors.isS808()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'I'";
			}
			if (sectors.isS809()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'SS'";
			}
			if (sectors.isS810()) {
				hQuery += " or " + subsectorAlias + ".SUBSECTOR_NAME = 'C_OTHER'";
			}
			return hQuery;
		}
	}
	
	public static String emitterPetroleumSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		// For on-shore production (W2), do not include aggregates at the state, county level since emissions may span across states.
		if (!sectors.isPetroleumAndNaturalGas()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && sectors.isS902() && sectors.isS903() && sectors.isS904() && sectors.isS905() && sectors.isS906()
				&& sectors.isS907() && sectors.isS908() && sectors.isS909() && sectors.isS910() && sectors.isS911()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			return " or s.sectorCode = '" + DaoUtils.PETRO_NG + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS901()) {
				hQuery += " or ss.subSectorName = 'W1'";
			}
			if (sectors.isS902() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or ss.subSectorName = 'W2'";
			}
			if (sectors.isS903()) {
				hQuery += " or ss.subSectorName = 'W3'";
			}
			if (sectors.isS904()) {
				hQuery += " or ss.subSectorName = 'W4'";
			}
			if (sectors.isS905()) {
				hQuery += " or ss.subSectorName = 'W8'";
			}
			if (sectors.isS906()) {
				hQuery += " or ss.subSectorName = 'W5'";
			}
			if (sectors.isS907()) {
				hQuery += " or ss.subSectorName = 'W6'";
			}
			if (sectors.isS908()) {
				hQuery += " or ss.subSectorName = 'W7'";
			}
			if (sectors.isS909()) {
				hQuery += " or ss.subSectorName = 'W_Other'";
			}
			if (sectors.isS910() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or ss.subSectorName = 'W9'";
			}
			if (sectors.isS911() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or ss.subSectorName = 'W10'";
			}
			return hQuery;
		}
	}
	
	public static String emitterPetroleumSectorFilterSql(SectorFilter sectors, String state, String fipsCode, String tableAliasAndColumnName, String subSectorAlias) {
		// For on-shore production (W2), do not include aggregates at the state, county level since emissions may span across states.
		if (!sectors.isPetroleumAndNaturalGas()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && sectors.isS902() && sectors.isS903() && sectors.isS904() && sectors.isS905() && sectors.isS906()
				&& sectors.isS907() && sectors.isS908() && sectors.isS909() && sectors.isS910() && sectors.isS911()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			//return " or s.sectorCode = '" + DaoUtils.PETRO_NG + "'";
			return " OR " + tableAliasAndColumnName + " = 'PETRO_NG'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS901()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W1'";
			}
			if (sectors.isS902() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W2'";
			}
			if (sectors.isS903()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W3'";
			}
			if (sectors.isS904()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W4'";
			}
			if (sectors.isS905()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W8'";
			}
			if (sectors.isS906()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W5'";
			}
			if (sectors.isS907()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W6'";
			}
			if (sectors.isS908()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W7'";
			}
			if (sectors.isS909()) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W_Other'";
			}
			if (sectors.isS910() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W9'";
			}
			if (sectors.isS911() && !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
				hQuery += " or " + subSectorAlias + ".SUBSECTOR_NAME = 'W10'";
			}
			return hQuery;
		}
	}
	
	public static String emitterAllPetroleumSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		if (!sectors.isPetroleumAndNaturalGas()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && sectors.isS902() && sectors.isS903() && sectors.isS904() && sectors.isS905() && sectors.isS906()
				&& sectors.isS907() && sectors.isS908() && sectors.isS909() && sectors.isS910() && sectors.isS911()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			return " or s.sectorCode = '" + DaoUtils.PETRO_NG + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS901()) {
				hQuery += " or ss.subSectorName = 'W1'";
			}
			if (sectors.isS902()) {
				hQuery += " or ss.subSectorName = 'W2'";
			}
			if (sectors.isS903()) {
				hQuery += " or ss.subSectorName = 'W3'";
			}
			if (sectors.isS904()) {
				hQuery += " or ss.subSectorName = 'W4'";
			}
			if (sectors.isS905()) {
				hQuery += " or ss.subSectorName = 'W8'";
			}
			if (sectors.isS906()) {
				hQuery += " or ss.subSectorName = 'W5'";
			}
			if (sectors.isS907()) {
				hQuery += " or ss.subSectorName = 'W6'";
			}
			if (sectors.isS908()) {
				hQuery += " or ss.subSectorName = 'W7'";
			}
			if (sectors.isS909()) {
				hQuery += " or ss.subSectorName = 'W_Other'";
			}
			if (sectors.isS910()) {
				hQuery += " or ss.subSectorName = 'W9'";
			}
			if (sectors.isS911()) {
				hQuery += " or ss.subSectorName = 'W10'";
			}
			return hQuery;
		}
	}
	
	public static String ldcPetroleumSectorFilter(SectorFilter sectors, String state, String fipsCode) {
		// For on-shore production (W2), do not include aggregates at the state, county level since emissions may span across states.
		if (!sectors.isPetroleumAndNaturalGas()) {
			return StringUtils.EMPTY;
		} else if (sectors.isPetroleumAndNaturalGas() && sectors.isS901() && sectors.isS902() && sectors.isS903() && sectors.isS904() && sectors.isS905() && sectors.isS906()
				&& sectors.isS907() && sectors.isS908() && sectors.isS909() && sectors.isS910() && sectors.isS911()
				&& !org.springframework.util.StringUtils.hasLength(state) && !org.springframework.util.StringUtils.hasLength(fipsCode)) {
			return " or s.sector_code = '" + DaoUtils.PETRO_NG + "'";
		} else {
			String hQuery = StringUtils.EMPTY;
			if (sectors.isS905()) {
				hQuery += "and ss.subsector_name = 'W8' ";
			}
			return hQuery;
		}
	}
	
	public static String emitterWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "and ((0=1) or g.gasCode is null";
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gasCode = 'Other_L'";
		//}
		if (gases.isCo2()) {
			hQuery += " or g.gasCode = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gasCode = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gasCode = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gasCode = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gasCode = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gasCode = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gasCode = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gasCode = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gasCode = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gasCode = 'Other'";
			hQuery += " or g.gasCode = 'Other_L'";
		}
		if (gases.isVeryShortCompounds()) {
			hQuery += " or g.gasCode = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			hQuery += " or g.gasCode = 'Other_Full'";
		}
		hQuery += ") " + emitterSectorFilter(sectors, state, countyFips);
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String emitterWhereClauseSql(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo, String tableAliasAndColName, String sectorTblAndCol, String subSectorAlias, String topTable) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		//String sQuery = "and ((0=1) or g.gasCode is null";
		StringBuilder sQuery = new StringBuilder();
		sQuery.append("and ((0=1) or " + tableAliasAndColName + " IS NULL");
		
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gasCode = 'Other_L'";
		//}
		if (gases.isCo2()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'CO2'");
			//sQuery += " or g.gasCode = 'CO2'";
		}
		if (gases.isCh4()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'CH4'");
			//sQuery += " or g.gasCode = 'CH4'";
		}
		if (gases.isN2o()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'N2O'");
			//sQuery += " or g.gasCode = 'N2O'";
		}
		if (gases.isSf6()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'SF6'");
			//sQuery += " or g.gasCode = 'SF6'";
		}
		if (gases.isNf3()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'NF3'");
			//sQuery += " or g.gasCode = 'NF3'";
		}
		if (gases.isHfc23()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'CHF3'");
			//sQuery += " or g.gasCode = 'CHF3'";
		}
		if (gases.isHfc()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'HFC'");
			//sQuery += " or g.gasCode = 'HFC'";
		}
		if (gases.isPfc()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'PFC'");
			//sQuery += " or g.gasCode = 'PFC'";
		}
		if (gases.isHfe()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'HFE'");
			//sQuery += " or g.gasCode = 'HFE'";
		}
		if (gases.isOther()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'Other'");
			sQuery.append(" OR " + tableAliasAndColName + " = 'Other_L'");
			//sQuery += " or g.gasCode = 'Other'";
			//sQuery += " or g.gasCode = 'Other_L'";
		}
		if (gases.isVeryShortCompounds()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'Very_Short'");
			//sQuery += " or g.gasCode = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			sQuery.append(" OR " + tableAliasAndColName + " = 'Other_Full'");
			//sQuery += " or g.gasCode = 'Other_Full'";
		}
		sQuery.append(" ) ");
		sQuery.append(emitterSectorFilterSql(sectors, state, countyFips, sectorTblAndCol, subSectorAlias));
		
		//sQuery += ") " + emitterSectorFilter(sectors, state, countyFips);
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			sQuery.append(" and " + topTable + ".state ='" + state + "'");
			//sQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			sQuery.append(" and " + topTable + ".TRIBAL_LAND_ID is not null");
			//sQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			sQuery.append(" and " + topTable + ".county_fips= " + fipsCode);
			//sQuery += " and dimfacilit7_.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					sQuery.append(" and " + topTable + ".eggrt_facility_id = " + facilityId + " and " + topTable + ".year = " + year);
					//sQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					//sQuery += " and (";
					sQuery.append(" and (");
				}
				if (qo.isNameSelected()) {
					sQuery.append(" REPLACE(upper(" + topTable + ".facility_name), chr(9), chr(32)) like upper('%");
					sQuery.append(srchStr);
					//sQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					//sQuery += srchStr;
					//sQuery += "%')";
					sQuery.append("%')");
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						sQuery.append(" OR");
						//sQuery += " or ";
					}
					
					sQuery.append(" upper(" + topTable + ".city) like upper('%");
					//sQuery += "upper(f.city) like upper('%";
					sQuery.append(srchStr);
					//sQuery += srchStr;
					sQuery.append("%')");
					//sQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						sQuery.append(" OR");
						//sQuery += " or ";
					}
					sQuery.append(" upper(" + topTable + ".county) like upper('%");
					//sQuery += "upper(f.county) like upper('%";
					sQuery.append(srchStr);
					//sQuery += srchStr;
					//sQuery += "%')";
					sQuery.append("%')");
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						sQuery.append(" OR");
						//sQuery += " or ";
					}
					sQuery.append(" upper(" + topTable + ".state_name) like upper('%");
					//sQuery += "upper(f.stateName) like upper('%";
					sQuery.append(srchStr);
					//sQuery += srchStr;
					sQuery.append("%')");
					//sQuery += "%')";
					sQuery.append(" or upper(" + topTable + ".state) like upper('%");
					//sQuery += " or upper(f.state) like upper('%";
					sQuery.append(srchStr);
					//sQuery += srchStr;
					//sQuery += "%')";
					sQuery.append("%')");
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						sQuery.append(" OR");
						//sQuery += " or ";
					}
					sQuery.append(" upper(" + topTable + ".zip) like upper('%");
					//sQuery += "upper(f.zip) like upper('%";
					sQuery.append(srchStr);
					sQuery.append("%')");
					//sQuery += srchStr;
					//sQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						//sQuery += " or ";
						sQuery.append(" OR");
					}
					sQuery.append(" upper(" + topTable + ".facility_id) like upper('%");
					//sQuery += "upper(f.id.facilityId) like upper('%";
					sQuery.append(srchStr);
					//sQuery += srchStr;
					sQuery.append("%')");
					//sQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						//sQuery += " or ";
						sQuery.append(" OR");
					}
					sQuery.append(" upper(" + topTable + ".naics_code) like upper('%");
					sQuery.append(srchStr);
					//sQuery += srchStr
					sQuery.append("%')");
					//sQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						//sQuery += " or ";
						sQuery.append(" OR");
					}
					sQuery.append(" upper(" + topTable + ".parent_company) like upper('%");
					//sQuery += "upper(f.parentCompany) like upper('%";
					sQuery.append(srchStr);
					sQuery.append("%')");
					//sQuery += srchStr;
					//sQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					sQuery.append(")");
					//sQuery += ")";
				}
			}
		}
		return sQuery.toString();
	}
	
	public static String emitterAllWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "and ((0=1) or g.gasCode is null";
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gasCode = 'Other_L'";
		//}
		if (gases.isCo2()) {
			hQuery += " or g.gasCode = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gasCode = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gasCode = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gasCode = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gasCode = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gasCode = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gasCode = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gasCode = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gasCode = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gasCode = 'Other'";
			hQuery += " or g.gasCode = 'Other_L'";
		}
		if (gases.isVeryShortCompounds()) {
			hQuery += " or g.gasCode = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			hQuery += " or g.gasCode = 'Other_Full'";
		}
		hQuery += ") " + emitterAllSectorFilter(sectors, state, countyFips);
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String ldcWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "and ((0=1) or g.gas_code is null";
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gas_code = 'Other_L'";
		//}
		if (gases.isCo2()) {
			hQuery += " or g.gas_code = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gas_code = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gas_code = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gas_code = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gas_code = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gas_code = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gas_code = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gas_code = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gas_code = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gas_code = 'Other'";
			hQuery += " or g.gas_code = 'Other_L'";
		}
		hQuery += ") " + ldcPetroleumSectorFilter(sectors, state, countyFips);
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribal_land_id is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.county_fips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrt_facility_id = " + facilityId + " and f.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facility_name),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.state_name) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facility_id) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naics_code) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parent_company) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String basinFilter(String basinCode) {
		if (basinCode != null && basinCode.length() > 0) {
			return "join f.basins b ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String basinWhereClause(String basinCode) {
		if (basinCode != null && basinCode.length() > 0) {
			return "and b.id.basinCode = '" + basinCode + "' ";
		} else {
			return StringUtils.EMPTY;
		}
	}

	public static String tribalLandWhereClauseSql(String state, Long tribalLandId) {
		if (state.equals("TL") && tribalLandId != null) {
			return " and lutriballa6_.TRIBAL_LAND_ID = '" + tribalLandId + "' ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String tribalLandWhereClause(String state, Long tribalLandId) {
		if (state.equals("TL") && tribalLandId != null) {
			return " and f.tribalLand.tribalLandId = '" + tribalLandId + "' ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String co2InjectionWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			QueryOptions qo, int is) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = (is == 12 ? "and f.uuRandDExempt = 'Y' " : "")
				+ "and ((0=1) or g.gasCode is null";
		if (gases.isCo2()) {
			hQuery += " or g.gasCode = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gasCode = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gasCode = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gasCode = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gasCode = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gasCode = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gasCode = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gasCode = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gasCode = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gasCode = 'Other'";
		}
		if (gases.isVeryShortCompounds()) {
			hQuery += " or g.gasCode = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			hQuery += " or g.gasCode = 'Other_Full'";
		}
		hQuery += ") "/*+emitterSectorFilter(sectors, state, countyFips)*/;
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String co2InjectionAggregateWhereClause(int sc) {
		String hQuery = (sc == 12 ? "and e.uuRandDExempt = 'Y' " : "and (e.uuRandDExempt is null or e.uuRandDExempt = 'N') ") + "and ((1=1) ";
		hQuery += "and s.sectorCode = 'INJ'";
		hQuery += ") ";
		return hQuery;
	}
	
	public static String rrCo2WhereClause(String q, int year, String state, String countyFips,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "";
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0 && "US".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String autoCompleteEmitterWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "and ((0=1) or g.gasCode is null";
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gasCode = 'Other_L'";
		//}
		if (gases.isCo2()) {
			hQuery += " or g.gasCode = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gasCode = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gasCode = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gasCode = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gasCode = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gasCode = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gasCode = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gasCode = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gasCode = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gasCode = 'Other'";
			hQuery += " or g.gasCode = 'Other_L'";
		}
		if (gases.isVeryShortCompounds()) {
			hQuery += " or g.gasCode = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			hQuery += " or g.gasCode = 'Other_Full'";
		}
		hQuery += ") " + emitterSectorFilter(sectors, state, countyFips);
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribalLand is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.countyFips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String supplierWhereClause(String q, QueryOptions qo, int year, int sc, String state) {
		String srchStr = q.replaceAll("''*", "''");
		// SUPPLIER TYPE WHERE CLAUSE
		String hQuery = "and ((1=1)";
		if (sc == 11) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 12) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 13) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 21) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 22) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 23) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 31) {
			hQuery += " and s.sectorCode = 'NG_NGL_SUP'";
		} else if (sc == 32) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'LDC')";
		} else if (sc == 33) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'NGL')";
		} else if (sc == 41) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 42) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 43) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 51) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 52) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 53) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'CAPTURE')";
		} else if (sc == 54) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'PROD_WELLS')";
		} else if (sc == 61) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'IMP')";
		} else if (sc == 62) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'EXP')";
		}
		hQuery += ") ";
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += "and f.state = '" + state + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String supplierAggregatedWhereClause(int sc) {
		// SUPPLIER TYPE WHERE CLAUSE FOR PUB_FACTS_AGGREGATED_EMISSION TABLE
		String hQuery = "and ((1=1)";
		if (sc == 11) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 12) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 13) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 21) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 22) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 23) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 31) {
			hQuery += " and s.sectorCode = 'NG_NGL_SUP'";
		} else if (sc == 32) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'LDC')";
		} else if (sc == 33) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'NGL')";
		} else if (sc == 41) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 42) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 43) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'PRO')";
		} else if (sc == 51) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'IMP')";
		} else if (sc == 52) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'EXP')";
		} else if (sc == 53) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'CAPTURE')";
		} else if (sc == 54) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'PROD_WELLS')";
		} else if (sc == 61) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'IMP')";
		} else if (sc == 62) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'EXP')";
		}
		hQuery += ") ";
		return hQuery;
	}
	
	public static String autoCompleteSupplierWhereClause(String q, QueryOptions qo, int year, int st) {
		String srchStr = q.replaceAll("''*", "''");
		// SUPPLIER TYPE WHERE CLAUSE
		String hQuery = "and ((1=1)";
		if (st == 11) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'IMP')";
		} else if (st == 12) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'EXP')";
		} else if (st == 13) {
			hQuery += " and (s.sectorCode = 'COAL_TO_LIQUIDS_SUP' and ss.subSectorName = 'PRO')";
		} else if (st == 21) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'IMP')";
		} else if (st == 22) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'EXP')";
		} else if (st == 23) {
			hQuery += " and (s.sectorCode = 'PETROLEUM_SUP' and ss.subSectorName = 'PRO')";
		} else if (st == 31) {
			hQuery += " and s.sectorCode = 'NG_NGL_SUP'";
		} else if (st == 32) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'LDC')";
		} else if (st == 33) {
			hQuery += " and (s.sectorCode = 'NG_NGL_SUP' and ss.subSectorName = 'NGL')";
		} else if (st == 41) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'IMP')";
		} else if (st == 42) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'EXP')";
		} else if (st == 43) {
			hQuery += " and (s.sectorCode = 'IG_SUP' and ss.subSectorName = 'PRO')";
		} else if (st == 51) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'IMP')";
		} else if (st == 52) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'EXP')";
		} else if (st == 53) {
			hQuery += " and (s.sectorCode = 'CO2_SUP' and ss.subSectorName = 'PRO')";
		} else if (st == 61) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'IMP')";
		} else if (st == 62) {
			hQuery += " and (s.sectorCode = 'QQ' and ss.subSectorName = 'EXP')";
		}
		hQuery += ") ";
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrtFacilityId = " + facilityId + " and f.id.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facilityName),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.stateName) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.id.facilityId) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naicsCode) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parentCompany) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
	
	public static String gasFilter(GasFilter gases) {
		if (gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
			return "g.gasCode is null or ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String gasFilterSql(GasFilter gases) {
		if (gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
			//return "g.gasCode is null or ";
			return " dimghg5_.gas_code is null or ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String abbreviationToState(String str) {
		if (str.equals("AK")) {
			return "Alaska";
		} else if (str.equals("AL")) {
			return "Alabama";
		} else if (str.equals("AR")) {
			return "Arkansas";
		} else if (str.equals("AS")) {
			return "American Samoa";
		} else if (str.equals("AZ")) {
			return "Arizona";
		} else if (str.equals("CA")) {
			return "California";
		} else if (str.equals("CO")) {
			return "Colorado";
		} else if (str.equals("CT")) {
			return "Connecticut";
		} else if (str.equals("DC")) {
			return "District Of Columbia";
		} else if (str.equals("DE")) {
			return "Delaware";
		} else if (str.equals("FL")) {
			return "Florida";
		} else if (str.equals("GA")) {
			return "Georgia";
		} else if (str.equals("GU")) {
			return "Guam";
		} else if (str.equals("HI")) {
			return "Hawaii";
		} else if (str.equals("IA")) {
			return "Iowa";
		} else if (str.equals("ID")) {
			return "Idaho";
		} else if (str.equals("IL")) {
			return "Illinois";
		} else if (str.equals("IN")) {
			return "Indiana";
		} else if (str.equals("KS")) {
			return "Kansas";
		} else if (str.equals("KY")) {
			return "Kentucky";
		} else if (str.equals("LA")) {
			return "Louisiana";
		} else if (str.equals("MA")) {
			return "Massachusetts";
		} else if (str.equals("MD")) {
			return "Maryland";
		} else if (str.equals("ME")) {
			return "Maine";
		} else if (str.equals("MI")) {
			return "Michigan";
		} else if (str.equals("MN")) {
			return "Minnesota";
		} else if (str.equals("MO")) {
			return "Missouri";
		} else if (str.equals("MP")) {
			return "Northern Mariana Islands";
		} else if (str.equals("MS")) {
			return "Mississippi";
		} else if (str.equals("MT")) {
			return "Montana";
		} else if (str.equals("NC")) {
			return "North Carolina";
		} else if (str.equals("ND")) {
			return "North Dakota";
		} else if (str.equals("NE")) {
			return "Nebraska";
		} else if (str.equals("NH")) {
			return "New Hampshire";
		} else if (str.equals("NJ")) {
			return "New Jersey";
		} else if (str.equals("NM")) {
			return "New Mexico";
		} else if (str.equals("NV")) {
			return "Nevada";
		} else if (str.equals("NY")) {
			return "New York";
		} else if (str.equals("OH")) {
			return "Ohio";
		} else if (str.equals("OK")) {
			return "Oklahoma";
		} else if (str.equals("OR")) {
			return "Oregon";
		} else if (str.equals("PA")) {
			return "Pennsylvania";
		} else if (str.equals("PR")) {
			return "Puerto Rico";
		} else if (str.equals("RI")) {
			return "Rhode Island";
		} else if (str.equals("SC")) {
			return "South Carolina";
		} else if (str.equals("SD")) {
			return "South Dakota";
		} else if (str.equals("TL")) {
			return "Tribal Land";
		} else if (str.equals("TN")) {
			return "Tennessee";
		} else if (str.equals("TX")) {
			return "Texas";
		} else if (str.equals("UT")) {
			return "Utah";
		} else if (str.equals("VA")) {
			return "Virginia";
		} else if (str.equals("VI")) {
			return "Virgin Islands";
		} else if (str.equals("VT")) {
			return "Vermont";
		} else if (str.equals("WA")) {
			return "Washington";
		} else if (str.equals("WI")) {
			return "Wisconsin";
		} else if (str.equals("WV")) {
			return "West Virginia";
		} else if (str.equals("WY")) {
			return "Wyoming";
		}
		return "";
	}
	
	public static String emissionsTypeFilter(String emissionsType) {
		if (emissionsType == null || emissionsType.equals("") || emissionsType.equals("undefined")) {
			return "left join f.emissions e ";
		} else if (emissionsType.equals("PE")) {
			return "left join f.peEmissions e ";
		} else if (emissionsType.equals("FC")) {
			return "left join f.fcEmissions e ";
		} else if (emissionsType.equals("FC_CL")) {
			return "left join f.emCoal e ";
		} else if (emissionsType.equals("FC_NG")) {
			return "left join f.emNg e ";
		} else if (emissionsType.equals("FC_PP")) {
			return "left join f.emPet e ";
		} else if (emissionsType.equals("FC_OT")) {
			return "left join f.emOther e ";
		} else if (emissionsType.equals("SORB")) {
			return "left join f.emSorb e ";
		} else {
			return "left join f.ucEmissions e ";
		}
	}
	
	public static String emissionsTypeFilterSql(String emissionsType, String joinCondtionAndTableAlias) {
		if (emissionsType == null || emissionsType.equals("") || emissionsType.equals("undefined")) {
			return " LEFT OUTER JOIN PUB_FACTS_SECTOR_GHG_EMISSION " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("PE")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_PE " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("FC")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_FC " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("FC_CL")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_COAL " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("FC_NG")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_NG " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("FC_PP")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_PET " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("FC_OT")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_OTHER " + joinCondtionAndTableAlias;
		} else if (emissionsType.equals("SORB")) {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_SORB " + joinCondtionAndTableAlias;
		} else {
			return " LEFT OUTER JOIN PUB_SECTOR_GHG_EMISSION_UC " + joinCondtionAndTableAlias;
		}
	}
	
	/**
	 * LDC should be included if :
	 * the sectorType is either LDC (L) or Direct Emitters (E) AND
	 * a state is selected
	 */
	public static boolean shouldLdcBeIncludedInEmitterResults(String sectorType, String state, String msaCode, String countyFips, Long tribalLandId) {
		return (("L".equals(sectorType) || "E".equals(sectorType)) && !state.equals("")
				&& msaCode.equals("") && countyFips.equals("") && tribalLandId == null);
	}
	
	// some subqueries need the state to be blank, this logic encompasses why
	public static boolean shouldStateBeBlank(String msaCode, String sectorType, String state, String countyFips) {
		return
				// if there is an MSA code, we need a blank state
				!msaCode.equals("")
						||
						(
								// if sectorType is emitter or LDC and there is a state while msa/county are both blanks
								("L".equals(sectorType) || "E".equals(sectorType))
										&&
										!state.equals("")
										&& msaCode.equals("")
										&& countyFips.equals("")
						)
				;
		
	}
	
	public static String createOrderByClauseFromSortOrder(int sortOrder) {
		String orderBy = "NLSSORT(UPPER(f.facilityName),'NLS_SORT=BINARY_CI') asc";
		switch (sortOrder) {
			case 1:
				orderBy = "NLSSORT(UPPER(f.facilityName),'NLS_SORT=BINARY_CI') desc";
			case 2:
				break;
			case 3:
				break;
		}
		return orderBy;
	}
	
	public static String emissionsTypeFromClause(String emissionsType) {
		if (emissionsType.equals("PE")) {
			return "from PubSectorGhgEmissionPE e ";
		} else if (emissionsType.equals("FC")) {
			return "from PubSectorGhgEmissionFC e ";
		} else if (emissionsType.equals("FC_CL")) {
			return "from PubSectorGhgEmissionCoal e ";
		} else if (emissionsType.equals("FC_NG")) {
			return "from PubSectorGhgEmissionNg e ";
		} else if (emissionsType.equals("FC_PP")) {
			return "from PubSectorGhgEmissionPet e ";
		} else if (emissionsType.equals("FC_OT")) {
			return "from PubSectorGhgEmissionOther e ";
		} else if (emissionsType.equals("UC")) {
			return "from PubSectorGhgEmissionUC e ";
		} else if (emissionsType.equals("SORB")) {
			return "from PubSectorGhgEmissionSorb e ";
		} else {
			return "from PubFactsSectorGhgEmission e ";
		}
	}
	
	public String generateBaseLdcQuery(String q, int year, String sectorType, String state, String msaCode, String tribalLandState, String countyFips,
			String lowE, String highE, GasFilter gases, SectorFilter sectors,
			QueryOptions qo, String reportingStatus, String emissionsType, Long tribalLandId, int sortOrder, int pageNumber) {
		String orderBy = DaoUtils.createOrderByClauseFromSortOrder(sortOrder);
		String s = "from DimFacility f join f.facStatus fs "
				+ DaoUtils.emissionsTypeFilter(emissionsType)
				+ "left join e.sector s "
				+ "left join e.gas g "
				+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
				+ "where f.id in (select distinct f.id "
				+ "from DimFacility f "
				+ DaoUtils.emissionsTypeFilter(emissionsType)
				+ "left join e.sector s "
				+ "left join e.gas g "
				+ DaoUtils.emitterSubSectorFilter(sectors, state, countyFips)
				+ "where (s.sectorType is null or s.sectorType = 'E') "
				+ DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo)
				+ DaoUtils.tribalLandWhereClause(tribalLandState, tribalLandId)
				+ ReportingStatusQueryFilter.filterEmissionsRange(reportingStatus, lowE, highE)
				+ DaoUtils.emitterWhereClause(q, year, state, countyFips, gases, sectors, qo)
				+ DaoUtils.tribalLandWhereClause(tribalLandState, tribalLandId)
				+ "and ("
				+ DaoUtils.gasFilter(gases)
				+ "g.gasCode <> 'BIOCO2') and (s.sectorType is null or s.sectorType = 'E') and fs.id.year = " + year + " and fs.facilityType = 'E' "
				+ ReportingStatusQueryFilter.filter(reportingStatus, year) + " group by f.id";
		final String baseQuery;
		if (DaoUtils.shouldLdcBeIncludedInEmitterResults(sectorType, state, msaCode, countyFips, tribalLandId)) {
			baseQuery = "from PubLdcFacility l where l.id.state='" + tribalLandState + "' and l.id.facilityId != '0' and l.id.year='" + year + "' and l.facility.id in (select f.id " + s + ")";
		} else if (msaCode.equals("")) {
			baseQuery = "from DimFacility f where f.id in (select f.id " + s + ") order by " + orderBy;
		} else {
			baseQuery = "from DimFacility f where f.id in "
					+ " (select f.id from DimFacility f, DimMsa m where sdo_inside(f.location, m.geometry) = 'TRUE' and m.cbsafp = '"
					+ msaCode + "' and f.id.year = '" + year + "')" + " and f.id in (select f.id " + s + ") order by " + orderBy;
		}
		return baseQuery;
		
	}
	
	public static String sqlBasinFilter(String basinCode) {
		if (basinCode != null && basinCode.length() > 0) {
			return "join pub_basin_facility b on f.facility_id = b.facility_id ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String sqlBasinWhereClause(String basinCode) {
		if (basinCode != null && basinCode.length() > 0) {
			return "and b.basin_code = '" + basinCode + "' ";
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static String sqlEmitterWhereClause(String q, int year, String state, String countyFips,
			GasFilter gases,
			SectorFilter sectors,
			QueryOptions qo) {
		String srchStr = q.replaceAll("''*", "''");
		// GAS WHERE CLAUSE
		String hQuery = "and ((0=1) or g.gas_code is null";
		// if(gases.isCo2() && gases.isCh4() && gases.isN2o() && gases.isSf6() && gases.isNf3() && gases.isHfc() && gases.isPfc() && gases.isHfe() && gases.isOther()) {
		//	hQuery += " or g.gas_code = 'Other_L'";
		//}
		if (gases.isCo2()) {
			hQuery += " or g.gas_code = 'CO2'";
		}
		if (gases.isCh4()) {
			hQuery += " or g.gas_code = 'CH4'";
		}
		if (gases.isN2o()) {
			hQuery += " or g.gas_code = 'N2O'";
		}
		if (gases.isSf6()) {
			hQuery += " or g.gas_code = 'SF6'";
		}
		if (gases.isNf3()) {
			hQuery += " or g.gas_code = 'NF3'";
		}
		if (gases.isHfc23()) {
			hQuery += " or g.gas_code = 'CHF3'";
		}
		if (gases.isHfc()) {
			hQuery += " or g.gas_code = 'HFC'";
		}
		if (gases.isPfc()) {
			hQuery += " or g.gas_code = 'PFC'";
		}
		if (gases.isHfe()) {
			hQuery += " or g.gas_code = 'HFE'";
		}
		if (gases.isOther()) {
			hQuery += " or g.gas_code = 'Other'";
			hQuery += " or g.gas_code = 'Other_L'";
		}
		if (gases.isVeryShortCompounds()) {
			hQuery += " or g.gas_code = 'Very_Short'";
		}
		if (gases.isOtherFlourinated()) {
			hQuery += " or g.gas_code = 'Other_Full'";
		}
		hQuery += ") ";
		if (state != null && DaoUtils.forceExclude.compareTo(state) != 0 && "".compareTo(state) != 0 && "TL".compareTo(state) != 0) {
			hQuery += " and f.state = '" + state + "'";
		}
		if (state != null && "TL".compareTo(state) == 0) {
			hQuery += " and f.tribal_land is not null ";
		}
		if (countyFips != null && DaoUtils.forceExclude.compareTo(countyFips) != 0 && countyFips.compareTo("") != 0) {
			String fipsCode = countyFips;
			try {
				fipsCode = df.format(Integer.valueOf(countyFips));
			} catch (Exception e) {
				log.debug("Formatting FIPS code failed: " + countyFips);
			}
			hQuery += " and f.county_fips = '" + fipsCode + "'";
		}
		if (q != null && !q.isEmpty()) {
			if (q.startsWith("facID=")) {
				if (q.split("=").length > 1) {
					String facilityId = q.split("=")[1];
					hQuery += "and f.eggrt_facility_id = " + facilityId + " and f.year = " + year + " ";
				}
			} else {
				int numFilters = 0;
				if (qo.isNameSelected() || qo.isCitySelected() || qo.isCountySelected() || qo.isStateSelected() || qo.isZipSelected() || qo.isIdSelected() || qo.isIdSelected()
						|| qo.isNaicsSelected() || qo.isParentSelected()) {
					hQuery += " and (";
				}
				if (qo.isNameSelected()) {
					hQuery += "REPLACE(upper(f.facility_name),chr(9), chr(32)) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCitySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.city) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isCountySelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.county) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isStateSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.state_name) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					hQuery += " or upper(f.state) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isZipSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.zip) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isIdSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.facility_id) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isNaicsSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.naics_code) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (qo.isParentSelected()) {
					if (numFilters > 0) {
						hQuery += " or ";
					}
					hQuery += "upper(f.parent_company) like upper('%";
					hQuery += srchStr;
					hQuery += "%')";
					numFilters++;
				}
				if (numFilters > 0) {
					hQuery += ")";
				}
			}
		}
		return hQuery;
	}
}
