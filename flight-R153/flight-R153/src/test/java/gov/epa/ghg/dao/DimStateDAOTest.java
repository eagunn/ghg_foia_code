package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimStateGeo;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class DimStateDAOTest {
	
	@Inject
	private DimStateGeoDao dimStateGeoDao;
	
	@Test
	@Transactional
	public void testGetDimStateForState() {
		DimStateGeo s = dimStateGeoDao.getStateByStateAbbr("VA");
		if (s.getGeometry() instanceof MultiPolygon) {
			MultiPolygon mPoly = (MultiPolygon) s.getGeometry();
			int n = mPoly.getNumGeometries();
			for (int i = 0; i < n; i++) {
				Geometry g = mPoly.getGeometryN(i);
				if (g instanceof Polygon) {
					// System.out.println(g.getClass().getName());
					if (g.getNumGeometries() > 1) {
						System.out.println("Num geo (Polygon): " + g.getNumGeometries());
					}
				} else {
					System.out.println("Caesar :" + g.getClass().getName());
				}
			}
		} else if (s.getGeometry() instanceof Polygon) {
			Polygon poly = (Polygon) s.getGeometry();
			if (poly.getNumGeometries() > 1) {
				System.out.println("Num geo (Polygon): " + poly.getNumGeometries());
			}
			if (poly.getNumInteriorRing() > 0) {
				System.out.println("ID: " + s.getState() + " Holes: " + poly.getNumInteriorRing());
			}
		}
	}
	
	@Test
	@Transactional
	public void testGetEnvelopeForState() {
		List<DimStateGeo> ls = dimStateGeoDao.getStates();
		for (DimStateGeo s : ls) {
			if (s.getGeometry() != null) {
				if (s.getGeometry() instanceof MultiPolygon) {
					MultiPolygon mPoly = (MultiPolygon) s.getGeometry();
					int n = mPoly.getNumGeometries();
					for (int i = 0; i < n; i++) {
						Geometry g = mPoly.getGeometryN(i);
						if (g instanceof Polygon) {
							// System.out.println(g.getClass().getName());
							if (g.getNumGeometries() > 1) {
								// System.out.println("Num geo (Polygon): "+g.getNumGeometries());
							}
						} else {
							// System.out.println("Caesar :"+g.getClass().getName());
						}
					}
				} else if (s.getGeometry() instanceof Polygon) {
					Polygon poly = (Polygon) s.getGeometry();
					if (poly.getNumGeometries() > 1) {
						// System.out.println("Num geo (Polygon): "+poly.getNumGeometries());
					}
					if (poly.getNumInteriorRing() > 0) {
						// System.out.println("ID: "+s.getState()+" Holes: "+poly.getNumInteriorRing());
					}
				}
			}
			if (s.getGeometry() != null) {
				Geometry e = s.getGeometry().getEnvelope();
				Coordinate c0 = e.getCoordinates()[0];
				Coordinate c1 = e.getCoordinates()[1];
				Coordinate c2 = e.getCoordinates()[2];
				Coordinate c3 = e.getCoordinates()[3];
				System.out.println("C0: " + c0.y + ", " + c0.x + "C1: " + c1.y + ", " + c1.x + "C2: " + c2.y + ", " + c2.x + "C3: " + c3.y + ", " + c3.x);
				System.out.println("Caesar");
			}
		}
	}
}
