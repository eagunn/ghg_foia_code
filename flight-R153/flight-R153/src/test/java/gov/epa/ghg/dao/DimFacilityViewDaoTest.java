package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class DimFacilityViewDaoTest extends BaseDaoTest {
	
	@Inject
	FacilityViewDaoInterface facilityViewDao;
	
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
		
		FacilityList fl = facilityViewDao.getEmitterList(searchTerm, 2010,
				state, countyFips, null, lowE, highE, 0, gases, sectors, qo, 0, null, null, "", null);
		
		assertTrue(fl.getTotalCount() > 0);
	}
	
	@Test
	public void testGetEmitterList5b9574() {
		
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
		
		FacilityList fl = facilityViewDao.getEmitterList("Cape", 2010, "FL",
				countyFips, null, lowE, highE, 0, gases, sectors, qo, 0, null, null, "", null);
		
		assertTrue(fl.getTotalCount() > 0);
	}
	
	@Test
	public void testGetEmitterListFacId() {
		
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
		
		FacilityList fl = facilityViewDao.getEmitterList("facID=52b063", 2010,
				state, countyFips, null, lowE, highE, 0, gases, sectors, qo, 0, null, null, "", null);
		
		assertTrue(fl.getTotalCount() > 0);
	}
	
	@Test
	public void testGetEmitterGeo() {
		
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
		
	/*	List<FacilityViewSub> fvl = facilityViewDao.getEmitterGeo(searchTerm,
				2010, state, countyFips, null, lowE, highE, gases, sectors, qo, "E",null, "", null);*//*

		assertFalse(fvl.isEmpty());*/
	}
	
	@Test
	public void testGetSupplierGeo() {

		/*QueryOptions qo = new QueryOptions("1000000");
		List<FacilityViewSub> fvl = facilityViewDao.getSupplierGeo(searchTerm, qo,
				2010, 32,null);

		assertFalse(fvl.isEmpty());*/
	}
	
	@Test
	public void testGetEmitterSectorAggregate() {
		
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
		
		SectorAggregate sa = facilityViewDao.getEmitterSectorAggregate(
				searchTerm, 2010, state, countyFips, null, StringUtils.EMPTY, lowE, highE, gases,
				sectors, qo, null, null, "", null);
		
		assertNotNull(sa.getPowerplantEmission());
	}
	
	@Test
	public void testGetFacilityEmitterSectorAggregate() {
		
		List<DimCounty> ldc = facilityViewDao.getFacilityCounties("AL", 2010);
		
		assertFalse(ldc.isEmpty());
	}
	
}
