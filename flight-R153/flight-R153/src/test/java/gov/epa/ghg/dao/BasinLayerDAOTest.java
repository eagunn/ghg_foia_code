package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.BasinLayer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class BasinLayerDAOTest {
	
	@Inject
	private BasinLayerDAO basinLayerDao;
	
	@Inject
	private FacilityViewDaoInterface facilityViewDao;
	
	@Test
	@Transactional
	public void testFindAll() {
		List<BasinLayer> b = basinLayerDao.getBasinLayers();
		for (BasinLayer bl : b) {
			if (bl.getGeometry() instanceof MultiPolygon) {
				MultiPolygon mPoly = (MultiPolygon)bl.getGeometry();
				int n = mPoly.getNumGeometries();
				for (int i=0; i<n; i++) {
					Geometry g = mPoly.getGeometryN(i);
					if (g instanceof Polygon) {
						//System.out.println(g.getClass().getName());
						if (g.getNumGeometries()>1) {
							System.out.println("Num geo (Polygon): "+g.getNumGeometries());
						}
					} else {
						System.out.println("Caesar :"+g.getClass().getName());
					}
				}
			} else if (bl.getGeometry() instanceof Polygon) {
				Polygon poly = (Polygon)bl.getGeometry();
				if (poly.getNumGeometries()>1) {
					System.out.println("Num geo (Polygon): "+poly.getNumGeometries());
				}
				if (poly.getNumInteriorRing()>0) {
					System.out.println("ID: "+bl.getBasinCode()+" Holes: "+poly.getNumInteriorRing());
				}
			}
		}
		assertTrue(b.size()>0);
	}
}
