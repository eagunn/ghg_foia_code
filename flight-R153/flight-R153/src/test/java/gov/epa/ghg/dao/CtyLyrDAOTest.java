package gov.epa.ghg.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.CountyLayer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class CtyLyrDAOTest {
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Test
	@Transactional
	public void testFindById(){
		try {
			CountyLayer instance = (CountyLayer) sessionFactory.getCurrentSession().get(
					"gov.epa.ghg.domain.CountyLayer", 1420);
			Polygon p = (Polygon)instance.getSdoGeometry();
			Coordinate[] cArray = p.getCoordinates();
			for (int i=0; i<cArray.length;i++) {
				Coordinate c = cArray[i];
				System.out.println("Lat: "+c.y+" Long: "+c.x);
			}
			assertTrue(instance!=null);
		} catch (RuntimeException re) {
			throw re;
		}
	}
}
