package gov.epa.ghg.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.Shape;

/**
 * Geometric utilities. Decimal to sexagesimal conversions. Contains only static
 * methods.
 *
 * @author olivier@lediouris.net
 */
public class SpatialUtil {
	
	private static final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
	static final DecimalFormat df = new DecimalFormat("#.##");
	
	public static GeometryFactory getGeometryFactory() {
		return gf;
	}
	
	/**
	 * Converts Sexagesimal to decimal. Return a double, in degrees.
	 *
	 * @param degrees the value of the degrees. It's an int
	 * @param minutes the value of the minutes. Can be decimal, like 45.67
	 */
	public static double sexToDec(String degrees, String minutes)
			throws RuntimeException {
		double deg = 0.0;
		double min = 0.0;
		
		double ret = 0.0;
		
		try {
			deg = Double.parseDouble(degrees);
			min = Double.parseDouble(minutes);
			min *= (10.0 / 6.0);
			ret = (deg + (min / 100.0));
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Bad number");
		}
		return ret;
	}
	
	public final static int HTML = 0;
	public final static int SHELL = 1;
	public final static int SWING = 2;
	
	public final static int NONE = 0;
	public final static int NS = 1;
	public final static int EW = 2;
	
	/**
	 * Converts decimal to sexagesimmal. Return an ASCII string
	 *
	 * @param v the double value to convertEmitter, in degrees.
	 */
	public static String decToSex(double v) {
		return decToSex(v, SHELL);
	}
	
	public static String decToSex(double v, int output) {
		return decToSex(v, output, NONE);
	}
	
	/**
	 * Formats an angle in degrees into DMS.
	 *
	 * @param v           the value to convertEmitter, in degrees
	 * @param output      HTML, SHELL or SWING, for the degree symbol.
	 * @param displayType EW, NS, or NONE.
	 *
	 * @return the formated string
	 */
	public static String decToSex(double v, int output, int displayType) {
		String s = "";
		double absVal = Math.abs(v);
		double intValue = Math.floor(absVal);
		double dec = absVal - intValue;
		
		int i = (int) intValue;
		
		dec *= (100 * (6.0 / 10.0));
		
		DecimalFormat df = new DecimalFormat("00.00");
		if (output == HTML) {
			s = Integer.toString(i) + "&deg; " + df.format(dec) + "'";
		} else if (output == SWING) {
			s = Integer.toString(i) + '�' + df.format(dec) + "'";
		} else {
			s = Integer.toString(i) + (char) 248 + df.format(dec) + "'";
		}
		
		if (v < 0.0) {
			switch (displayType) {
				case NONE:
					s = "-" + s;
					break;
				case NS:
					s += " S";
					break;
				case EW:
					s += " W";
					break;
			}
		} else {
			switch (displayType) {
				case NONE:
					s = " " + s;
					break;
				case NS:
					s += " N";
					break;
				case EW:
					s += " E";
					break;
			}
		}
		
		return s;
	}
	
	/**
	 * Converts an Hour Angle into an Hour. Noon for Meridian Passage.
	 *
	 * @param angle The Hour Angle
	 *
	 * @return the Hour in DEGREES
	 */
	public static double ha2hour(double angle) {
		double deg = angle;
		// Noon : 0
		// Midnight: 180
		// 6am: : 270
		// 6pm : 90
		deg += 180;
		while (deg < 0.0) {
			deg += 360.0;
		}
		while (deg > 360.0) {
			deg -= 360.0;
		}
		
		return deg;
	}
	
	/**
	 * Used to convertEmitter LHA to real solar time
	 */
	public static String angle2Hour(double angle) {
		String hValue = "";
		DecimalFormat nf = new DecimalFormat("00");
		
		double deg = angle;
		// Noon : 0
		// Midnight: 180
		// 6am: : 270
		// 6pm : 90
		deg += 180;
		while (deg < 0.0) {
			deg += 360.0;
		}
		while (deg > 360.0) {
			deg -= 360.0;
		}
		
		double nbMinArc = deg * 60.0;
		
		double nbH = Math.floor(nbMinArc / (60 * 15));
		nbMinArc -= (nbH * (60 * 15));
		double dnbM = 4.0 * nbMinArc / 60.0;
		double nbM = Math.floor(dnbM);
		double nbS = (dnbM - (double) nbM) * 60.0;
		hValue = nf.format(nbH) + ":" + nf.format(nbM) + ":" + nf.format(nbS);
		
		return hValue;
	}
	
	public static double ha2ra(double ha) {
		return (360.0 - ha);
	}
	
	public static double ra2ha(double ra) {
		return (360.0 - ra);
	}
	
	public static double degrees2hours(double d) {
		return d / 15.0;
	}
	
	public static double hours2degrees(double d) {
		return d * 15.0;
	}
	
	/**
	 * Display an angle given in degrees as hh:mm:ss
	 *
	 * @param deg The value to display, in DEGREES
	 *
	 * @return a formatted string
	 */
	public static String formatInHours(double deg) {
		String hValue = "";
		DecimalFormat nf = new DecimalFormat("00");
		DecimalFormat nf2 = new DecimalFormat("00.0");
		
		double nbMinArc = deg * 60.0;
		double nbH = nbMinArc / (60 * 15);
		nbMinArc -= (Math.floor(nbH) * (60 * 15));
		double dnbM = 4.0 * nbMinArc / 60.0;
		double nbS = (dnbM - Math.floor(dnbM)) * 60.0;
		hValue = nf.format(Math.floor(nbH)) + ":" + nf.format(Math.floor(dnbM))
				+ ":" + nf2.format(nbS);
		
		return hValue;
	}
	
	/**
	 * Just format a decimal hour (hh.hhhhh) to hh:mm:ss
	 *
	 * @param h the hour to format
	 *
	 * @return a formatted string
	 */
	public static String formatHMS(double h) {
		String hValue = "";
		DecimalFormat nf = new DecimalFormat("00");
		DecimalFormat nf2 = new DecimalFormat("00.0");
		
		double min = (h - Math.floor(h)) * 60.0;
		double sec = (min - Math.floor(min)) * 60.0;
		hValue = nf.format(Math.floor(h)) + ":" + nf.format(Math.floor(min))
				+ ":" + nf2.format(sec);
		
		return hValue;
	}
	
	/**
	 *
	 */
	public static class PolyAngle {
		
		private double angleInDegrees;
		
		/**
		 * DEGREES - Will be counted clockwise
		 */
		public final static short DEGREES = 0;
		/**
		 * HOURS - Will be counted counter-clockwise
		 */
		public final static short HOURS = 1;
		
		public PolyAngle() {
		}
		
		/**
		 *
		 */
		public PolyAngle(double d, short type) {
			switch (type) {
				case DEGREES:
					this.angleInDegrees = d;
					break;
				case HOURS:
					this.angleInDegrees = ra2ha(d);
					break;
				default:
					break;
			}
		}
		
		public PolyAngle(String str, short type) {
		
		}
		
		public double getAngleInDegrees() {
			return angleInDegrees;
		}
	}
	
	/**
	 * Haversine formula to compute distance between 2 locations (in miles)
	 */
	public static double distanceInMiles(Point p1, Point p2) {
		double earthRadius = 3958.75; // in miles
		double dLat = Math.toRadians(p2.getCoordinate().y
				- p1.getCoordinate().y);
		double dLng = Math.toRadians(p2.getCoordinate().x
				- p1.getCoordinate().x);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(p1.getCoordinate().y))
				* Math.cos(Math.toRadians(p2.getCoordinate().y));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;
		return Double.valueOf(df.format(dist));
	}
	
	public static Geometry createLatLngBounds(Coordinate sw, Coordinate ne) {
		
		Coordinate[] bounds = new Coordinate[5];
		Coordinate nw = new Coordinate(sw.x, ne.y);
		Coordinate se = new Coordinate(ne.x, sw.y);
		bounds[0] = sw;
		bounds[1] = nw;
		bounds[2] = ne;
		bounds[3] = se;
		bounds[4] = sw;
		return new Polygon(gf.createLinearRing(bounds), null, gf);
	}
	
	public static Shape createShape(Polygon p) {
		Coordinate[] c = p.getExteriorRing().getCoordinates();
		Shape s = new Shape();
		for (int i = 0; i < c.length; i++) {
			LatLng latLng = new LatLng(c[i].y, c[i].x);
			s.getShell().add(latLng);
		}
		for (int j = 0; j < p.getNumInteriorRing(); j++) {
			c = p.getInteriorRingN(j).getCoordinates();
			List<LatLng> hole = new ArrayList<LatLng>();
			for (int k = 0; k < c.length; k++) {
				LatLng latLng = new LatLng(c[k].y, c[k].x);
				hole.add(latLng);
			}
			s.getHoles().add(hole);
		}
		return s;
	}
	
	public static void main(String[] args) {
		DecimalFormat nf3 = new DecimalFormat("000.0000000");
		
		double d = sexToDec("333", "22.07");
		PolyAngle pa = new PolyAngle(d, PolyAngle.DEGREES);
		System.out.println(d + "="
				+ formatHMS(degrees2hours(ha2ra(pa.getAngleInDegrees()))));
	}
}
