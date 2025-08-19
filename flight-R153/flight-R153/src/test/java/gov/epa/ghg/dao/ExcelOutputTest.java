package gov.epa.ghg.dao;

import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityExport;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})
public class ExcelOutputTest extends BaseDaoTest {
	
	@Inject
	FacilityViewDaoInterface facilityViewDao;
	
	@Inject
	PubFactsSectorGhgEmissionDao facilityEmissionDao;
	
	/*@Inject
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
	}*/
	
	@Test
	public void testExport() {
		
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
		
		List<FacilityExport> feList = facilityViewDao.getEmitters(searchTerm, 2012,
				state, countyFips, null, null, lowE, highE, gases, sectors, qo, null, null, "", 0, 0, "");
		
		Workbook wb = new HSSFWorkbook();
		Sheet ws = wb.createSheet("FLIGHT Facilities and GHG Quantities");
		Row row = ws.createRow(0);
		row.createCell(0).setCellValue("REPORTING YEAR");
		row.createCell(1).setCellValue("FACILITY NAME");
		row.createCell(2).setCellValue("GHGRP ID");
		row.createCell(3).setCellValue("LOCATION ADDRESS");
		row.createCell(4).setCellValue("CITY NAME");
		row.createCell(5).setCellValue("COUNTY NAME");
		row.createCell(6).setCellValue("STATE");
		row.createCell(7).setCellValue("ZIP CODE");
		row.createCell(8).setCellValue("PARENT COMPANIES");
		row.createCell(9).setCellValue("GHG QUANTITY (METRIC TONS CO2e)");
		row.createCell(10).setCellValue("SUBPARTS");
		// row.createCell(11).setCellValue("SECTOR-SUBSECTOR");
		int i = 1;
		for (FacilityExport fe : feList) {
			row = ws.createRow(i);
			row.createCell(0).setCellValue(2012);
			row.createCell(1).setCellValue(fe.getFacilityName());
			row.createCell(2).setCellValue(fe.getFacilityId());
			row.createCell(3).setCellValue(fe.getAddress1());
			row.createCell(4).setCellValue(fe.getCity());
			row.createCell(5).setCellValue(fe.getCounty());
			row.createCell(6).setCellValue(fe.getState());
			row.createCell(7).setCellValue(fe.getZip());
			row.createCell(8).setCellValue(fe.getParentCompanies());
			row.createCell(9).setCellValue(fe.getTotalCo2e().setScale(0, RoundingMode.HALF_UP).doubleValue());
			row.createCell(10).setCellValue(fe.getSubParts());
			// row.createCell(11).setCellValue("");
			i++;
		}
		try {
			FileOutputStream fos = new FileOutputStream("Z:/GGDS/caesar/EF/flight.xls");
			wb.write(fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
