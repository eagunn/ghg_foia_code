package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.util.SpatialUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class MobileFacilityDaoTest extends BaseDaoTest {
	
	@Inject
	MobileFacilityDaoInterface mobileFacilityDao;
	
	@Test
	public void testGetEmitterList() {
		
		GasFilter gases = new GasFilter(b, b, b, b, b, b, b, b, b, b, b, b);
		SectorFilter sectors = new SectorFilter(
				b, b, b, b, b, b, b, b, b,
				b, b, b, b,
				b, b, b, b, b, b, b,
				b, b, b, b,
				b, b,
				b, b, b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b, b);
		QueryOptions qo = new QueryOptions("1000000");
		
		List<Facility> l = mobileFacilityDao.getEmitterList(searchTerm, 2010,
				state, countyFips, lowE, highE, 0, gases, sectors, qo, 0, null);
		
		assertTrue(l.size() > 0);
	}
	
	@Test
	public void testGetEmitterListAround() {
		
		GasFilter gases = new GasFilter(b, b, b, b, b, b, b, b, b, b, b, b);
		SectorFilter sectors = new SectorFilter(
				b, b, b, b, b, b, b, b, b,
				b, b, b, b,
				b, b, b, b, b, b, b,
				b, b, b, b,
				b, b,
				b, b, b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b, b);
		QueryOptions qo = new QueryOptions("1000000");
		
		LatLng center = new LatLng(38.9342, -77.1778);
		List<Facility> l = mobileFacilityDao.getEmitterListAround(searchTerm, 2010,
				state, countyFips, lowE, highE, gases, sectors, qo, center, 10.0, null);
		for (Facility f : l) {
			System.out.println("Fid: " + f.getId() + " H Distance: " + f.getDistance());
		}
		
		assertTrue(l.size() > 0);
	}
	
	@Test
	public void testGetEmitterListWithin() {
		
		GasFilter gases = new GasFilter(b, b, b, b, b, b, b, b, b, b, b, b);
		SectorFilter sectors = new SectorFilter(
				b, b, b, b, b, b, b, b, b,
				b, b, b, b,
				b, b, b, b, b, b, b,
				b, b, b, b,
				b, b,
				b, b, b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b,
				b, b, b, b, b, b, b, b, b, b, b);
		QueryOptions qo = new QueryOptions("1000000");
		
		LatLng center = new LatLng(38.9342, -77.1778);
		Coordinate sw = new Coordinate(-77.709045, 38.737732);
		Coordinate ne = new Coordinate(-76.670837, 39.130841);
		Geometry bounds = SpatialUtil.createLatLngBounds(sw, ne);
		// Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate())
		List<Facility> l = mobileFacilityDao.getEmitterListWithin(searchTerm, 2010,
				state, countyFips, lowE, highE, gases, sectors, qo, center, bounds, null);
		for (Facility f : l) {
			System.out.println("Fid: " + f.getId() + " H Distance: " + f.getDistance());
		}
		
		assertTrue(l.size() > 0);
	}
	
	@Test
	public void testGetEmitterListWithinSectorsOnly() {
		
		GasFilter gases = new GasFilter(b, b, b, b, b, b, b, b, b, b, b, b);
		SectorFilter sectors = new SectorFilter(b, b, b, b, b, b, b, b, b);
		QueryOptions qo = new QueryOptions("1000000");
		
		LatLng center = new LatLng(38.9342, -77.1778);
		Coordinate sw = new Coordinate(-77.709045, 38.737732);
		Coordinate ne = new Coordinate(-76.670837, 39.130841);
		Geometry bounds = SpatialUtil.createLatLngBounds(sw, ne);
		// Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate())
		List<Facility> l = mobileFacilityDao.getEmitterListWithin(searchTerm, 2010,
				state, countyFips, lowE, highE, gases, sectors, qo, center, bounds, null);
		for (Facility f : l) {
			System.out.println("Fid: " + f.getId() + " H Distance: " + f.getDistance());
		}
		
		assertTrue(l.size() > 0);
	}
}
