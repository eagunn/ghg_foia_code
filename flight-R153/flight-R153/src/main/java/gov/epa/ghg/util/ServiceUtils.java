package gov.epa.ghg.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.web.util.HtmlUtils;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.PubLdcFacility;
import gov.epa.ghg.domain.PubSf6Territory;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.ServiceArea;
import gov.epa.ghg.enums.ReportingStatus;

public class ServiceUtils {
	
	public static BigDecimal addBigDecimalNullSafe(BigDecimal... numbers) {
		BigDecimal retv = BigDecimal.ZERO;
		for (BigDecimal bd : numbers) {
			if (bd != null) {
				retv = retv.add(bd);
			}
		}
		return retv;
	}
	
	public static final BigDecimal kmtFactor = new BigDecimal(1000L);
	public static final BigDecimal mmtFactor = new BigDecimal(1000000L);
	public static final String[] colorArr = new String[]{
			"#FFBD59", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB", "#AA88AA",
			"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#FFFF7F", "#88BB88", "#8888BB", "#AA88AA",
			"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB", "#AA88AA",
			"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB", "#AA88AA",
			"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB", "#AA88AA",
			"#DDBB77", "#E6C758", "#CC7766", "#DD9977", "#DDDD88", "#7799CC", "#88BB88", "#8888BB"
	};
	
	public static final String PowerPlants = "Power Plants";
	public static final String PetroleumAndNaturalGasSystems = "Petroleum and Natural Gas Systems";
	public static final String Refineries = "Refineries";
	public static final String Chemicals = "Chemicals";
	public static final String Other = "Other";
	public static final String Waste = "Waste";
	public static final String Metals = "Metals";
	public static final String Minerals = "Minerals";
	public static final String PulpAndPaper = "Pulp and Paper";
	public static final String TotalReportedEmissions = "Total Reported Emissions";
	
	private ServiceUtils() {
		
	}
	
	public static int isState(String str) {
		if (str.compareTo("Alabama") == 0) {
			return 1;
		} else if (str.compareTo("Alaska") == 0) {
			return 2;
		} else if (str.compareTo("Arizona") == 0) {
			return 4;
		} else if (str.compareTo("Arkansas") == 0) {
			return 5;
		} else if (str.compareTo("California") == 0) {
			return 6;
		} else if (str.compareTo("Colorado") == 0) {
			return 8;
		} else if (str.compareTo("Connecticut") == 0) {
			return 9;
		} else if (str.compareTo("Delaware") == 0) {
			return 10;
		} else if (str.compareTo("District Of Columbia") == 0) {
			return 11;
		} else if (str.compareTo("Florida") == 0) {
			return 12;
		} else if (str.compareTo("Georgia") == 0) {
			return 13;
		} else if (str.compareTo("Hawaii") == 0) {
			return 15;
		} else if (str.compareTo("Idaho") == 0) {
			return 16;
		} else if (str.compareTo("Illinois") == 0) {
			return 17;
		} else if (str.compareTo("Indiana") == 0) {
			return 18;
		} else if (str.compareTo("Iowa") == 0) {
			return 19;
		} else if (str.compareTo("Kansas") == 0) {
			return 20;
		} else if (str.compareTo("Kentucky") == 0) {
			return 21;
		} else if (str.compareTo("Louisiana") == 0) {
			return 22;
		} else if (str.compareTo("Maine") == 0) {
			return 23;
		} else if (str.compareTo("Maryland") == 0) {
			return 24;
		} else if (str.compareTo("Massachusetts") == 0) {
			return 25;
		} else if (str.compareTo("Michigan") == 0) {
			return 26;
		} else if (str.compareTo("Minnesota") == 0) {
			return 27;
		} else if (str.compareTo("Mississippi") == 0) {
			return 28;
		} else if (str.compareTo("Missouri") == 0) {
			return 29;
		} else if (str.compareTo("Montana") == 0) {
			return 30;
		} else if (str.compareTo("Nebraska") == 0) {
			return 31;
		} else if (str.compareTo("Nevada") == 0) {
			return 32;
		} else if (str.compareTo("New Hampshire") == 0) {
			return 33;
		} else if (str.compareTo("New Jersey") == 0) {
			return 34;
		} else if (str.compareTo("New Mexico") == 0) {
			return 35;
		} else if (str.compareTo("New York") == 0) {
			return 36;
		} else if (str.compareTo("North Carolina") == 0) {
			return 37;
		} else if (str.compareTo("North Dakota") == 0) {
			return 38;
		} else if (str.compareTo("Ohio") == 0) {
			return 39;
		} else if (str.compareTo("Oklahoma") == 0) {
			return 40;
		} else if (str.compareTo("Oregon") == 0) {
			return 41;
		} else if (str.compareTo("Pennsylvania") == 0) {
			return 42;
		} else if (str.compareTo("Rhode Island") == 0) {
			return 44;
		} else if (str.compareTo("South Carolina") == 0) {
			return 45;
		} else if (str.compareTo("South Dakota") == 0) {
			return 46;
		} else if (str.compareTo("Tennessee") == 0) {
			return 47;
		} else if (str.compareTo("Texas") == 0) {
			return 48;
		} else if (str.compareTo("Utah") == 0) {
			return 49;
		} else if (str.compareTo("Vermont") == 0) {
			return 50;
		} else if (str.compareTo("Virginia") == 0) {
			return 51;
		} else if (str.compareTo("Washington") == 0) {
			return 53;
		} else if (str.compareTo("West Virginia") == 0) {
			return 54;
		} else if (str.compareTo("Wisconsin") == 0) {
			return 55;
		} else if (str.compareTo("Wyoming") == 0) {
			return 56;
		} else if (str.compareTo("American Samoa") == 0) {
			return 60;
		} else if (str.compareTo("Northern Mariana Islands") == 0) {
			return 69;
		} else if (str.compareTo("Guam") == 0) {
			return 66;
		} else if (str.compareTo("Puerto Rico") == 0) {
			return 72;
		} else if (str.compareTo("Virgin Islands") == 0) {
			return 78;
		}
		return 0;
	}
	
	public static void addToUnitList(List<BigDecimal> bdList, BigDecimal bd) {
		if (bd.compareTo(BigDecimal.ZERO) != 0) {
			bdList.add(bd);
		}
	}
	
	public static String getUnit(List<BigDecimal> bdList) {
		long mtCount = 0;
		// long kmtCount = 0;
		long mmtCount = 0;
		for (BigDecimal bd : bdList) {
			BigDecimal value = bd.setScale(0, RoundingMode.HALF_UP);
			if (value != BigDecimal.ZERO) {
				/*if (value.longValue() < 1000L) {
					mtCount++;
				} else if (value.longValue() >= 1000L && value.longValue() < 1000000L) {
					kmtCount++;
				} else if (value.longValue() >= 1000000L) {
					mmtCount++;
				}*/
				if (value.longValue() < 1000000L) {
					mtCount++;
				} else if (value.longValue() >= 1000000L) {
					mmtCount++;
				}
			}
		}
		if (mmtCount > mtCount) {
			return AppConstants.MMT;
		}
		/*if (kmtCount > mtCount) {
			return "kMT";
		}*/
		return AppConstants.MT;
	}
	
	public static String formatNullEmissionForDisplay(DimFacility facility) {
		String retVal = "---";
		ReportingStatus rs = facility.getReportingStatus();
		if (ReportingStatus.STOPPED_REPORTING_UNKNOWN_REASON.equals(rs) || ReportingStatus.STOPPED_REPORTING_VALID_REASON.equals(rs)) {
			retVal = "N/A";
		}
		return retVal;
	}
	
	public static String formatPipeNullEmissionForDisplay(DimFacilityPipe facility) {
		String retVal = "---";
		ReportingStatus rs = facility.getReportingStatus();
		if (ReportingStatus.STOPPED_REPORTING_UNKNOWN_REASON.equals(rs) || ReportingStatus.STOPPED_REPORTING_VALID_REASON.equals(rs)) {
			retVal = "N/A";
		}
		return retVal;
	}
	
	public static long convert(BigDecimal bd, String unit) {
		if ("kMT".equals(unit)) {
			return bd.divide(kmtFactor).setScale(0, RoundingMode.HALF_UP).longValue();
		} else if (AppConstants.MMT.equals(unit)) {
			return bd.divide(mmtFactor).setScale(0, RoundingMode.HALF_UP).longValue();
		}
		return bd.setScale(0, RoundingMode.HALF_UP).longValue();
	}
	
	public static BigDecimal convert(BigDecimal bd, String unit, int scale) {
		if ("kMT".equals(unit)) {
			return bd.divide(kmtFactor).setScale(scale, RoundingMode.HALF_UP);
		} else if (AppConstants.MMT.equals(unit)) {
			return bd.divide(mmtFactor).setScale(scale, RoundingMode.HALF_UP);
		}
		return bd.setScale(scale, RoundingMode.HALF_UP);
	}
	
	public static long aggregate(Map<String, BigDecimal> bdMap, String unit) {
		long total = 0L;
		for (BigDecimal value : bdMap.values()) {
			total += ServiceUtils.convert(value, unit);
		}
		return total;
	}
	
	public static String getSector(SectorFilter sectors) {
		if (sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Power Plants";
		} else if (sectors.isWaste() && !sectors.isPowerPlants() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Waste";
		} else if (sectors.isMetals() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Metals";
		} else if (sectors.isMinerals() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Minerals";
		} else if (sectors.isRefineries() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Refineries";
		} else if (sectors.isPulpAndPaper() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isChemicals() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Pulp and Paper";
		} else if (sectors.isChemicals() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isOther() && !sectors.isPetroleumAndNaturalGas()) {
			return "Chemicals";
		} else if (sectors.isOther() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isPetroleumAndNaturalGas()) {
			return "Other";
		} else if (sectors.isPetroleumAndNaturalGas() && !sectors.isPowerPlants() && !sectors.isWaste() && !sectors.isMetals() && !sectors.isMinerals() && !sectors.isRefineries() && !sectors.isPulpAndPaper() && !sectors.isChemicals() && !sectors.isOther()) {
			return "Petroleum and Natural Gas Systems";
		}
		return "";
	}
	
	public static String getSupplierType(int sc) {
		if (sc == 11) {
			return "Coal-based Liquid Fuel Importers";
		} else if (sc == 12) {
			return "Coal-based Liquid Fuel Exporters";
		} else if (sc == 13) {
			return "Coal-based Liquid Fuel Producers";
		} else if (sc == 21) {
			return "Petroleum Product Importers";
		} else if (sc == 22) {
			return "Petroleum Product Exporters";
		} else if (sc == 23) {
			return "Petroleum Product Refineries";
		} else if (sc == 31) {
			return "Natural Gas Suppliers - All";
		} else if (sc == 32) {
			return "Natural Gas - Local Distribution Companies";
		} else if (sc == 33) {
			return "Natural Gas Liquids Fractionators";
		} else if (sc == 41) {
			return "Industrial Gas Importers";
		} else if (sc == 42) {
			return "Industrial Gas Exporters";
		} else if (sc == 43) {
			return "Industrial Gas Producers";
		} else if (sc == 51) {
			return "CO<sub>2</sub> Importers";
		} else if (sc == 52) {
			return "CO<sub>2</sub> Exporters";
		} else if (sc == 53) {
			return "CO<sub>2</sub> Capture";
		} else if (sc == 54) {
			return "CO<sub>2</sub> Production Wells";
		} else if (sc == 61) {
			return "Importers of Equipment Containing Fluorinated GHGs";
		} else if (sc == 62) {
			return "Exporters of Equipment Containing Fluorinated GHGs";
		}
		return "";
	}
	
	public static String getCO2InjectionType(int sc) {
		if (sc == 12) {
			return "CO<sub>2</sub> Injectors with an R&D Exemption";
		} else if (sc == 11) {
			return "All CO<sub>2</sub> Injectors";
		} else {
			return "";
		}
	}
	
	public static ServiceArea getServiceArea(PubLdcFacility plf) {
		ServiceArea sa = new ServiceArea();
		sa.setId(plf.getId().getCharId());
		if (plf.getGeometry() instanceof MultiPolygon) {
			MultiPolygon mPoly = (MultiPolygon) plf.getGeometry();
			// Coordinate c = mPoly.getCentroid().getCoordinate();
			// sa.setLt(c.y);
			// sa.setLn(c.x);
			int n = mPoly.getNumGeometries();
			for (int i = 0; i < n; i++) {
				Geometry g = mPoly.getGeometryN(i);
				if (g instanceof Polygon) {
					if (sa.getLt() == null || sa.getLn() == null) {
						for (int k = 0; k < 10; k++) {
							Envelope e = mPoly.getEnvelopeInternal();
							Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
							if (p != null && plf.getGeometry().contains(p)) {
								sa.setLt(p.getCoordinate().y);
								sa.setLn(p.getCoordinate().x);
								break;
							}
						}
					}
					sa.getShapes().add(SpatialUtil.createShape((Polygon) g));
				}
			}
		} else if (plf.getGeometry() instanceof Polygon) {
			// Coordinate c = plf.getGeometry().getCentroid().getCoordinate();
			// sa.setLt(c.y);
			// sa.setLn(c.x);
			for (int i = 0; i < 10; i++) {
				Envelope e = plf.getGeometry().getEnvelopeInternal();
				Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
				if (p != null && plf.getGeometry().contains(p)) {
					sa.setLt(p.getCoordinate().y);
					sa.setLn(p.getCoordinate().x);
					break;
				}
			}
			sa.getShapes().add(SpatialUtil.createShape((Polygon) plf.getGeometry()));
		}
		return sa;
	}
	
	public static ServiceArea getSf6ServiceArea(PubSf6Territory psf) {
		ServiceArea sa = new ServiceArea();
		sa.setId(psf.getId().getRecId().toString());
		if (psf.getGeometry() instanceof MultiPolygon) {
			MultiPolygon mPoly = (MultiPolygon) psf.getGeometry();
			int n = mPoly.getNumGeometries();
			for (int i = 0; i < n; i++) {
				Geometry g = mPoly.getGeometryN(i);
				if (g instanceof Polygon) {
					if (sa.getLt() == null || sa.getLn() == null) {
						for (int k = 0; k < 10; k++) {
							Envelope e = mPoly.getEnvelopeInternal();
							Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
							if (p != null && psf.getGeometry().contains(p)) {
								sa.setLt(p.getCoordinate().y);
								sa.setLn(p.getCoordinate().x);
								break;
							}
						}
					}
					sa.getShapes().add(SpatialUtil.createShape((Polygon) g));
				}
			}
		} else if (psf.getGeometry() instanceof Polygon) {
			for (int i = 0; i < 10; i++) {
				Envelope e = psf.getGeometry().getEnvelopeInternal();
				Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
				if (p != null && psf.getGeometry().contains(p)) {
					sa.setLt(p.getCoordinate().y);
					sa.setLn(p.getCoordinate().x);
					break;
				}
			}
			sa.getShapes().add(SpatialUtil.createShape((Polygon) psf.getGeometry()));
		}
		return sa;
	}
	
	public static String getIconText(String iconImgName) {
		String vText = "";
		if ("co2b".equals(iconImgName)) {
			vText = "Some CO2 reported as emissions is collected and transferred to other users or sequestered or otherwise injected underground, as reported in Subpart PP.";
		}
		if ("co2g".equals(iconImgName)) {
			vText = "Some CO2 reported by this facility includes CO2 that is collected and later used on-site to manufacture other products. This CO2 is not emitted to the ambient air by the affected manufacturing unit(s) at this facility.";
		}
		if ("co2o".equals(iconImgName)) {
			vText = "This facility was granted a Research &amp; Development (R&amp;D) project exemption from 40 CFR part 98, subpart RR &quot;Geologic Sequestration of Carbon Dioxide&quot;.";
		}
		return vText;
	}
	
	public static String getIconInfo(String iconImgName) {
		String vIcons = "";
		if ("co2b".equals(iconImgName)) {
			vIcons = "<img src='/ghgp/img/co2b.jpg' title='" + getIconText("co2b") + "' alt='" + getIconText("co2b") + "' width='15' height='15' border='0'>";
		}
		if ("co2g".equals(iconImgName)) {
			vIcons = "<img src='/ghgp/img/co2g.jpg' title='" + getIconText("co2g") + "' alt='" + getIconText("co2g") + "' width='15' height='15' border='0'>";
		}
		if ("co2o".equals(iconImgName)) {
			vIcons = "<img src='/ghgp/img/co2o.jpg' title='" + getIconText("co2o") + "' alt='" + getIconText("co2o") + "' width='15' height='15' border='0'>";
		}
		return vIcons;
	}
	
	public static String nullSafeHtmlEscape(String str) {
		if (str == null) {
			return null;
		}
		return HtmlUtils.htmlEscape(str);
	}
	
	public static String nullSafeHtmlUnescape(String str) {
		if (str == null) {
			return null;
		}
		return HtmlUtils.htmlUnescape(str);
	}
}
