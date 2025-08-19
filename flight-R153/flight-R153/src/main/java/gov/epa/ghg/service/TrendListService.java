package gov.epa.ghg.service;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.dao.PubFactsSectorGhgEmissionDao;
import gov.epa.ghg.enums.FacilityType;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.view.list.ModeDomainResolver;
import gov.epa.ghg.service.view.list.viewmaker.ListViewMaker;
import gov.epa.ghg.service.view.list.viewmaker.ViewMakerFactory;
import gov.epa.ghg.util.AppConstants;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

@Log4j2
@Service
@Transactional
public class TrendListService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static NumberFormat df = NumberFormat.getInstance();
	
	@Inject
	PubFactsSectorGhgEmissionDao pubFactsDao;
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	@Inject
	ModeDomainResolver modeDomainResolver;
	
	@Inject
	ViewMakerFactory viewMapFactory;
	
	@Resource(name = "startYear")
	Long startYear;
	
	@Resource(name = "endYear")
	Long endYear;
	
	private Set<Long> buildTimeline() {
		return buildTimeline(endYear);
	}
	
	public Set<Long> buildTimeline(long currentYear) {
		Set<Long> years = new TreeSet<Long>();
		for (long _year = startYear; _year <= currentYear; _year++) {
			years.add(_year);
		}
		return years;
	}
	
	public JSONObject generateResponse(FlightRequest request) {
		
		// unroll request
		String dataSource = request.getDataSource();
		FacilityType type = FacilityType.fromDataSource(dataSource);
		int currentYear = request.getCurrentYear();
		String facType = "E";
		if (type == FacilityType.SUPPLIERS) {
			facType = "S";
		} else if (type == FacilityType.CO2_INJECTION) {
			facType = "I";
		} else if (type == FacilityType.RR_CO2) {
			facType = "A";
		}
		// load data
		List<Object[]> results = pubFactsDao.getTrendListChart(request);
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.getAllFacilityByYearType(request.getCurrentYear(), facType);
		// setup view building
		ModeDomainResolver.Mode mode = modeDomainResolver.resolveMode(request);
		String domain = modeDomainResolver.resolveDomain(request);
		
		ListViewMaker listViewMaker = null;
		if (type == FacilityType.SUPPLIERS) {
			// true = isTrend
			listViewMaker = viewMapFactory.createSupplierInstance(results, request.getReportingYear(), true);
		} else {
			listViewMaker = viewMapFactory.createTrendEmitterInstance(results, type, rsMap, request.getReportingYear());
		}
		Set<Long> years = buildTimeline(currentYear);
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("domain", domain);
		args.put("mode", mode);
		args.put("years", years);
		args.put("unit", AppConstants.MT);
		args.put("currentYear", currentYear);
		args.put("startYear", startYear);
		args.put("endYear", endYear);
		args.put("df", df);
		return listViewMaker.createView(args);
		
	}
	
}
