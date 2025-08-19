package gov.epa.ghg.dao;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.service.BarChartService;
import net.sf.json.JSONObject;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})

public class SectorGhgEmissionDaoTest extends BaseDaoTest {

	@Inject
	PubFactsSectorGhgEmissionDao facilityEmissionDao;

	@Inject
	BarChartService barChartService;
	
	@Test
	public void testGetSectorAggregatePieTreeL2() {
		
		String searchTerm = "";
		String lowE = "2500";
		String highE = "250000000";
		String state = "";
		String countyFips = "";
		String facId = "";
		String ss = "Petrochemical Production";
		
		GasFilter gases = new GasFilter(b, b, b, b, b, b, b, b, b, b,b,b);
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
		
		//facilityEmissionDao.getSectorAggregatePieTreeL2(searchTerm, lowE, highE, state, countyFips, facId, gas1, gas2, gas3, gas4, gas5, gas6, gas7, s1, s2, s3, s4, s5, s6, s7, s8);
		//facilityEmissionDao.getSectorAggregatePieTreeL3(ss, searchTerm, lowE, highE, state, countyFips, facId, gas1, gas2, gas3, gas4, gas5, gas6, gas7, s1, s2, s3, s4, s5, s6, s7, s8);
		//List l = facilityEmissionDao.getListSectorAggregate(searchTerm, lowE, highE, state, countyFips, facId, gas1, gas2, gas3, gas4, gas5, gas6, gas7, s1, s2, s3, s4, s5, s6, s7, s8);
		JSONObject l = barChartService.barChartSector(searchTerm, 2010, lowE, highE, state, countyFips, null, gases, sectors, qo, null, null, "", null);
	}
}
