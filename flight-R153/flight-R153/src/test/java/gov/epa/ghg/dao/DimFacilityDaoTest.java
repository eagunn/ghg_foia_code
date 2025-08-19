package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.spatial.dialect.oracle.criterion.OracleSpatialRestrictions;
import org.hibernate.spatial.dialect.oracle.criterion.SDOParameterMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.SpatialUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})

public class DimFacilityDaoTest extends BaseDaoTest {
	
	@Inject
	DimFacilityDaoInterface facilityDao;
	
	@Inject
	private SessionFactory sessionFactory;
	
	private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
	
	@Test
	public void testFindFacilityIdAndReportingYearWithResult() {
		DimFacility f = facilityDao.findByFacilityIdAndReportingYear(1001430L, 2011);
		assertTrue(f.getId().getFacilityId() > 0);
	}
	
	@Test
	@Transactional
	public void testFindFacilitiesAroundCoordinate() {
		try {
			Point p = gf.createPoint(new Coordinate(-122.391018, 37.931303));
			// Point p = gf.createPoint(new Coordinate(-77.1778, 38.9342));
			Criteria c = sessionFactory.getCurrentSession().createCriteria(DimFacility.class);
			SDOParameterMap pm = new SDOParameterMap();
			pm.setUnit("mile");
			c.add(Restrictions.eq("id.year", 2010L));
			c.add(OracleSpatialRestrictions.SDOWithinDistance("location", p, 10.0d, pm));
			List<DimFacility> l = (List<DimFacility>) c.list();
			System.out.println("Num facilities: " + l.size());
			for (DimFacility f : l) {
				System.out.println("Fid: " + f.getId().getFacilityId() + " H Distance: " + SpatialUtil.distanceInMiles(p, f.getLocation()));
			}
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	@Test
	@Transactional
	public void testFindFacilitiesAroundCoordinateHQL() {
		try {
			// Point p = gf.createPoint(new Coordinate(-122.391018, 37.931303));
			Point p = gf.createPoint(new Coordinate(-77.1778, 38.9342));
			// final String q = "from DimFacility f where distance(f.location,?) > 10";
			final String q = "from DimFacility f where sdo_within_distance(f.location,?,'unit=mile distance=10.0') = 'TRUE'";
			Query query = sessionFactory.getCurrentSession().createQuery(q);
			query.setParameter(0, p);
			List<DimFacility> l = (List<DimFacility>) query.list();
			System.out.println("Num facilities: " + l.size());
			for (DimFacility f : l) {
				System.out.println("Fid: " + f.getId().getFacilityId() + " H Distance: " + SpatialUtil.distanceInMiles(p, f.getLocation()));
			}
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	@Test
	public void testFindFacilityIdAndReportingYearWithNoResult() {
		DimFacility f = facilityDao.findByFacilityIdAndReportingYear(0L, 2011);
		assertNotNull(f);
		assertNull(f.getId());
	}
	
	@Test
	public void testGetFacilityHoverTip() {
		ReportingStatus rs = ReportingStatus.POTENTIAL_DATA_QUALITY_ISSUE;
		FacilityHoverTip ht = facilityDao.getFacilityHoverTip(1000112L, 2010, "E", "", rs);
		assertNotNull(ht.getFacility());
		ht = facilityDao.getFacilityHoverTip(519493L, 2011, "E", "", rs);
		assertNotNull(ht.getFacility());
	}
	
}
