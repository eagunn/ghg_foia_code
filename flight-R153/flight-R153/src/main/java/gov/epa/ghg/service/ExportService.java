package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.view.FacilityExport;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.enums.ReportingStatusQuery;
import gov.epa.ghg.presentation.request.FlightRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class ExportService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	FacilityViewService facilityViewService;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	DimMsaDao msaDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Resource(name = "reportingYears")
	private Map<String, String> reportingYears;
	
	@Resource(name = "dataDate")
	private String dataDate;
	
	@Resource(name = "startYear")
	Long startYear;
	
	@Resource(name = "endYear")
	Long endYear;
	
	public List<String> createHeaders(FacilityType type, String msaCode) {
		List<String> headers = new ArrayList<>();
		headers.add("REPORTING YEAR");
		headers.add("FACILITY NAME");
		headers.add("GHGRP ID");
		if (type == FacilityType.ONSHORE) {
			headers.add("BASIN NAME/NUMBER");
		}
		headers.add("REPORTED ADDRESS");
		headers.add("LATITUDE");
		headers.add("LONGITUDE");
		headers.add("CITY NAME");
		if (StringUtils.hasLength(msaCode)) {
			headers.add("METRO AREA NAME");
		} else {
			headers.add("COUNTY NAME");
		}
		headers.add("STATE");
		headers.add("ZIP CODE");
		headers.add("PARENT COMPANIES");
		headers.add("GHG QUANTITY (METRIC TONS CO2e)");
		headers.add("SUBPARTS");
		return headers;
	}
	
	public String createSearchParameters(FlightRequest request, boolean listChanges) {
		String retv = "Search Parameters: ";
		if (request.getQuery() != null && !StringUtils.isEmpty(request.getQuery())) {
			retv += "keyword=" + request.getQuery() + "; ";
		}
		if (listChanges) {
			retv += "year=" + String.valueOf(startYear) + "-" + String.valueOf(endYear) + "; ";
		} else {
			retv += "year=" + request.getReportingYear() + "; ";
		}
		if (request.getState() != null && !StringUtils.isEmpty(request.getState())) {
			retv += "state=" + request.getState() + "; ";
		}
		String ghgString = "";
		GasFilter ghgs = request.gases();
		if (ghgs.areAllGasesSelected()) {
			ghgString = "ALL";
		} else {
			ghgString = ghgs.getSelectedGasesAsString();
		}
		retv += "GHGs=" + ghgString + "; ";
		if (request.getCountyFips() != null && !StringUtils.isEmpty(request.getCountyFips())) {
			retv += "county=" + countyDao.getCountyByFips(request.countyFips()).getCountyName() + "; ";
		}
		if (request.getMsaCode() != null && !StringUtils.isEmpty(request.getMsaCode())) {
			retv += "metro area=" + msaDao.getMsaByCode(request.msaCode()).getCbsa_title() + "; ";
		}
		if (request.getTribalLandId() != null && !StringUtils.isEmpty(request.getTribalLandId())) {
			retv += "tribal land=" + tribalLandsDao.findById(request.getTribalLandId()).getTribalLandName() + "; ";
		}
		if (request.lowE() != null && !StringUtils.isEmpty(request.lowE()) && !"-20000".equals(request.lowE()) && request.highE() != null && !StringUtils.isEmpty(request.highE()) && !"23000000".equals(request.highE())) {
			retv += "lowE=" + request.lowE() + "; ";
			retv += "highE=" + request.highE() + "; ";
		}
		retv += "data type=" + FacilityType.getFullName(request.getDataSource()) + "; ";
		if (request.getEmissionsType() != null && !StringUtils.isEmpty(request.getEmissionsType())) {
			retv += "emissionsType=" + request.getEmissionsType() + "; ";
		}
		if (request.getReportingStatus() != null && !StringUtils.isEmpty(request.getReportingStatus()) && !"ALL".equals(request.getReportingStatus())) {
			// convert color symbol name to its descriptive meaning
			ReportingStatusQuery q = ReportingStatusQuery.fromString(request.getReportingStatus());
			String reportingStatusText = "";
			if (q != null) {
				if (q == ReportingStatusQuery.BLACK) {
					reportingStatusText = "Met requirements";
				} else {
					reportingStatusText = q.getReportingStatus().getLabel();
				}
			}
			retv += "Reporting Status=" + reportingStatusText;
		}
		return retv;
	}
	
	/** 
	 * creates the pre-header text in the Export XLS report, those are a few lines of text that occupies the first rows above the columns with the actual data
	 *
	 * @param ws            : the worksheet object containing the entire export report
	 * @param rowIndex      : current rowIndex the sheet is at, should usually be just zero at this point
	 * @param searchParams: Search parameters representing the current query used to generate the report at FLIGHT, should be passed into this method and created by a separate method
	 */
	public Integer createPreHeader(Sheet ws, Integer rowIndex, String searchParams, boolean allReportingYears) {
		Row row = ws.createRow(rowIndex++);
		row.createCell(0).setCellValue("Data Extracted from EPA's FLIGHT Tool (http://ghgdata.epa.gov/ghgp)");
		row = ws.createRow(rowIndex++);
		row.createCell(0).setCellValue("The data was reported to EPA by facilities as of " + dataDate);
		row = ws.createRow(rowIndex++);
		row.createCell(0).setCellValue("All emissions data is presented in units of metric tons of carbon dioxide equivalent using GWP's from IPCC's AR4");
		// all reporting years
		if (allReportingYears) {
			row = ws.createRow(rowIndex++);
			row.createCell(0).setCellValue("GHG data for some source categories are not directly comparable between 2010 and subsequent years. 12 new source categories began reporting for 2011.");
		}
		row = ws.createRow(rowIndex++);
		row.createCell(0).setCellValue(searchParams);
		return rowIndex;
	}
	
	private Sheet createWorksheet(Workbook wb, String sheetTitle, FlightRequest request, List<FacilityExport> feList, boolean allReportingYears) {
		FacilityType type = FacilityType.fromDataSource(request.getDataSource());
		Sheet ws = wb.createSheet(sheetTitle);
		Integer rowIndex = 0;
		rowIndex = this.createPreHeader(ws, rowIndex, createSearchParameters(request, false), allReportingYears);
		// do rowIndex++ twice because we need to leave a blank row between headers and pre-headers
		rowIndex++;
		Row row = ws.createRow(rowIndex++);
		// create header
		int colIndex = 0;
		List<String> headers = createHeaders(type, request.msaCode());
		for (String header : headers) {
			row.createCell(colIndex++).setCellValue(header);
		}
		for (FacilityExport fe : feList) {
			colIndex = 0;
			if (type != FacilityType.SUPPLIERS) {
				//fe.getReportingYear() null indicates no emissions for the year
				if (fe.getReportingYear() != null) {
					row = ws.createRow(rowIndex++);
					row.createCell(colIndex++).setCellValue(fe.getReportingYear());
					row.createCell(colIndex++).setCellValue(fe.getFacilityName());
					row.createCell(colIndex++).setCellValue(fe.getFacilityId());
					if (type == FacilityType.ONSHORE) {
						row.createCell(colIndex++).setCellValue(fe.getBasinDetails());
					}
					row.createCell(colIndex++).setCellValue(fe.getAddress1());
					if (fe.getLatitude() != null) {
						row.createCell(colIndex++).setCellValue(fe.getLatitude());
					} else {
						row.createCell(colIndex++).setCellValue("");
					}
					if (fe.getLongitude() != null) {
						row.createCell(colIndex++).setCellValue(fe.getLongitude());
					} else {
						row.createCell(colIndex++).setCellValue("");
					}
					row.createCell(colIndex++).setCellValue(fe.getCity());
					if (StringUtils.hasLength(request.msaCode())) {
						row.createCell(colIndex++).setCellValue(msaDao.getMsaByCode(request.msaCode()).getCbsa_title());
					} else {
						row.createCell(colIndex++).setCellValue(fe.getCounty());
					}
					row.createCell(colIndex++).setCellValue(fe.getState());
					if (fe.getZip() != null) {
						row.createCell(colIndex++).setCellValue(Double.valueOf(fe.getZip()));
					} else {
						row.createCell(colIndex++).setCellValue(fe.getZip());
					}
					row.createCell(colIndex++).setCellValue(fe.getParentCompanies());
					row.createCell(colIndex++).setCellValue(fe.getTotalCo2e().setScale(0, RoundingMode.HALF_UP).doubleValue());
					row.createCell(colIndex++).setCellValue(fe.getSubParts());
				}
			} else if (type == FacilityType.SUPPLIERS
					&& (request.getSupplierSector() == 33 /* NGC = 33 */
					&& fe.getReportingStatus() != ReportingStatus.STOPPED_REPORTING_UNKNOWN_REASON
					&& fe.getReportingStatus() != ReportingStatus.STOPPED_REPORTING_VALID_REASON)
					|| (request.getSupplierSector() != 33)) {
				if (fe.getReportingYear() != null) {
					row = ws.createRow(rowIndex++);
					row.createCell(colIndex++).setCellValue(fe.getReportingYear());
					row.createCell(colIndex++).setCellValue(fe.getFacilityName());
					row.createCell(colIndex++).setCellValue(fe.getFacilityId());
					row.createCell(colIndex++).setCellValue(fe.getAddress1());
					if (fe.getLatitude() != null) {
						row.createCell(colIndex++).setCellValue(fe.getLatitude());
					} else {
						row.createCell(colIndex++).setCellValue("");
					}
					if (fe.getLongitude() != null) {
						row.createCell(colIndex++).setCellValue(fe.getLongitude());
					} else {
						row.createCell(colIndex++).setCellValue("");
					}
					row.createCell(colIndex++).setCellValue(fe.getCity());
					if (StringUtils.hasLength(request.msaCode())) {
						row.createCell(colIndex++).setCellValue(msaDao.getMsaByCode(request.msaCode()).getCbsa_title());
					} else {
						row.createCell(colIndex++).setCellValue(fe.getCounty());
					}
					row.createCell(colIndex++).setCellValue(fe.getState());
					if (fe.getZip() != null) {
						row.createCell(colIndex++).setCellValue(Double.valueOf(fe.getZip()));
					} else {
						row.createCell(colIndex++).setCellValue(fe.getZip());
					}
					row.createCell(colIndex++).setCellValue(fe.getParentCompanies());
					// PUB-593: replace 0 with confidential for suppliers with NULL for emissions
					if (fe.getTotalCo2e() == null) {
						row.createCell(colIndex++).setCellValue("CONFIDENTIAL");
					} else {
						row.createCell(colIndex++).setCellValue(fe.getTotalCo2e().setScale(0, RoundingMode.HALF_UP).doubleValue());
					}
					row.createCell(colIndex++).setCellValue(fe.getSubParts());
				}
			}
		}
		return ws;
	}
	
	/**
	 * this is the entry point to the service
	 * <p>
	 * it takes the HTTP flight request object and returns an Excel Workbook object
	 */
	
	public Workbook exportToExcel(FlightRequest request, boolean allReportingYears) throws Exception {
		try (Workbook wb = new HSSFWorkbook()) {
			List<FacilityExport> dataResults = null;
			// only current reporting year
			if (!allReportingYears) {
				dataResults = facilityViewService.getExportData(request);
				this.createWorksheet(wb, "FLIGHT Facilities and GHG Quantities", request, dataResults, allReportingYears);
			} else {
				// all reporting years
				for (Map.Entry<String, String> reportingYear : reportingYears.entrySet()) {
					request.setReportingYear(Integer.valueOf(reportingYear.getValue()));
					dataResults = facilityViewService.getExportData(request);
					this.createWorksheet(wb, reportingYear.getValue(), request, dataResults, allReportingYears);
				}
			}
			return wb;
		}
	}
}
