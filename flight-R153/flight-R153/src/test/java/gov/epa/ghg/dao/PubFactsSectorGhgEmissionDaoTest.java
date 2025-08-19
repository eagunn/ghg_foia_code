package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.presentation.request.FlightRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})

public class PubFactsSectorGhgEmissionDaoTest extends BaseDaoTest {
	
	@Inject
	PubFactsSectorGhgEmissionDao facilityEmissionDao;
	
	@Test
	public void testReportingStatusTrendAggregate() {
		
		FlightRequest request = new FlightRequest();
		Boolean[] powerPlants = new Boolean[1]; // 1
		Boolean[] waste = new Boolean[5];        // 2
		Boolean[] metals = new Boolean[8];        // 3
		Boolean[] minerals = new Boolean[6];    // 4
		Boolean[] refineries = new Boolean[1];    // 5
		Boolean[] pulpAndPaper = new Boolean[3]; // 6
		Boolean[] chemicals = new Boolean[12];    // 7
		Boolean[] other = new Boolean[11];        // 8
		Boolean[] petroleumAndNaturalGas = new Boolean[12]; // 9
		// first item is null to make it a 1-index (starts from 1 not 0) array
		Boolean[][] matrix = {powerPlants, waste, metals, minerals, refineries, pulpAndPaper, chemicals, other, petroleumAndNaturalGas};
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = true;
			}
		}
		
		String rs = "RED";
		Boolean[] gasArray = new Boolean[10];
		boolean[] arr = {true, true, true, true, true, false, true, true, true, true};
		for (int i = 0; i < gasArray.length; i++) {
			gasArray[i] = arr[i];
		}
		
		request.setLowE(0L);
		request.setHighE(203333120L);
		request.setReportingStatus(rs);
		request.setGases(gasArray);
		request.setSectors(matrix);
		request.setSearchOptions(searchOptions);
		request.setQuery("");
		request.setReportingYear(2013);
		request.setState("KY");
		request.setCountyFips(null);
		List<Object[]> result = facilityEmissionDao.getListChartEmitterFacilitiesTrend(request);
		for (Object res : result) {
			System.out.println(res);
		}
		assertTrue(result.size() != 3);
	}
	
	@Test
	public void testGetListChartEmitterSectorAggregate() {
		
		// The world
		FlightRequest request = new FlightRequest();
		List l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
		
		l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
		
		// VA
		l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
		
		// VA
		l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
		
		// Fairfax, VA
		l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetListChartEmitterSectorAggregateCape() {
		
		FlightRequest request = new FlightRequest();
		// FL - "Cape"
		List l = facilityEmissionDao.getListChartEmitterSectorAggregate(request);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarSector() {
		
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
		
		List l = facilityEmissionDao.getBarSector(searchTerm, 2010, lowE, highE, state, countyFips, null,
				gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarSectorLevel2() {
		
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
		
		List l = facilityEmissionDao.getBarSectorLevel2(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarSectorLevel3() {
		
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
		
		List l = facilityEmissionDao.getBarSectorLevel3(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Disabled
	@Test
	public void testGetBarPieTreeGas() {
		
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
		
		List l = facilityEmissionDao.getBarPieTreeGas(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarState() {
		
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
		
		List l = facilityEmissionDao.getBarState(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarStateLevel2() {
		
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
		
		List l = facilityEmissionDao.getBarStateLevel2(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetBarStateLevel3() {
		
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
		
		List l = facilityEmissionDao.getBarStateLevel3(null, searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, "", null);
		
		assertNotNull(l);
		
		l = facilityEmissionDao.getBarStateLevel3("Glass Production", searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeSector() {
		
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
		
		List l = facilityEmissionDao.getPieTreeSector(searchTerm, 2010, lowE, highE, state, countyFips, null,
				gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeSectorLevel2() {
		
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
		
		List l = facilityEmissionDao.getPieTreeSectorLevel2(searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeSectorLevel3() {
		
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
		
		List l = facilityEmissionDao.getPieTreeSectorLevel3("Glass Production", searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeSectorLevel4() {
		
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
		
		List l = facilityEmissionDao.getPieTreeSectorLevel4("Glass Production", searchTerm, 2010, lowE, highE, state, countyFips,
				null, gases, sectors, qo, null, null, "", null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeState() {
		
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
		
		List l = facilityEmissionDao.getPieTreeState(searchTerm, 2010, lowE, highE, state, countyFips,
				gases, sectors, qo, null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeStateLevel2() {
		
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
		
		List l = facilityEmissionDao.getPieTreeStateLevel2(searchTerm, 2010, lowE, highE, state, countyFips,
				gases, sectors, qo, null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetPieTreeStateLevel3() {
		
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
		
		List l = facilityEmissionDao.getPieTreeStateLevel3(searchTerm, 2010, lowE, highE, state, countyFips,
				gases, sectors, qo, null);
		
		assertFalse(l.isEmpty());
	}
	
	@Test
	public void testGetFacilityTrend() {
		
		Long id = 1000355L;
		List l = facilityEmissionDao.getFacilityTrend(id, "E", "");
		
		assertFalse(l.isEmpty());
	}
}
