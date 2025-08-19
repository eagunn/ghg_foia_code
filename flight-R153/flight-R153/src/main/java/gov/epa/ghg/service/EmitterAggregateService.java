package gov.epa.ghg.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.FacilityViewDAO;
import gov.epa.ghg.dao.SectorAggregateDao;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.view.FacilityList;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.view.transformer.SectorDashboardTransformer;
import gov.epa.ghg.util.AppConstants;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.daofilter.ReportingStatusQueryFilter;
import net.sf.json.JSONObject;

@Service
@Transactional
public class EmitterAggregateService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	FacilityViewDAO facilityViewDao;

	@Inject
	private FacilityViewService facilityViewService;

	@Inject
	private SectorAggregateDao sectorAggregateDao;

	@Inject
	private SectorDashboardTransformer dashboardTransformer;

	public Object aggregateSector(BigDecimal value, String unit) {
		if (AppConstants.MT.equals(unit) || ServiceUtils.convert(value, unit) >= 10) {
			return ServiceUtils.convert(value, unit);
		}
		return value.divide(ServiceUtils.mmtFactor).setScale(1, RoundingMode.HALF_UP);		
	}


	/**
	 *
	 * this takes the request object, loads DimFacilities (inside the sectorAggregateDao.find method)
	 * and wraps them in SectorAggregate object, then the result is transformed into a JSON object to
	 * be consumed by the front end
	 *
	 * @param request
	 * @return
	 */
	public JSONObject getAggregates(FlightRequest request) {

		SectorFilter sectors = request.sectors();
		SectorAggregate sa = sectorAggregateDao.find(request);
		int numFacilities = facilityViewService.getTotalCountMinusStoppedReporting(request);		
		/*if(request.isWholePetroNg()) {
			int pipeCnt = facilityViewService.getPipePanelList(request).getFacilities().size();
			numFacilities = numFacilities + pipeCnt;
		}*/
		return dashboardTransformer.transform(sa, sectors, numFacilities);

	}


	/**
	 * the legacy version of the method above, kept for reference
	 * @return
	 */
	@Deprecated
	public JSONObject getAggregates(String q, int year, String stateCode,String fipsCode, String msaCode, String basin, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo, String sectorType, String rs, String emissionsType, Long tribalLandId,
			String ds, int pageNumber, int sc, int sortOrder, int is) {
		
		SectorAggregate sa = null; 
		
		//PUB-488 : those reporting Statuses do not have any emissions data so this method simply does the same thing without filtering on emission range, because doing that will return 0 facilities. 
		if ("GRAY".equals(rs) || "RED".equals(rs)) {  
			sa = facilityViewDao.getEmitterSectorAggregateForStoppedReportingFacilities(q, year, stateCode, fipsCode, msaCode, basin, gases, sectors, qo, sectorType, rs, emissionsType, tribalLandId);
		}
		
		else if ("ALL".equals(rs)) {
			sa = facilityViewDao.getEmitterSectorAggregate(q, year, stateCode, fipsCode, msaCode, basin, lowE, highE, gases, sectors, qo, sectorType, "STILL_REPORTING", emissionsType, tribalLandId); 
		}
		
		else {
			sa = facilityViewDao.getEmitterSectorAggregate(q, year, stateCode, fipsCode, msaCode, basin, lowE, highE,
				gases, sectors, qo, sectorType, rs, emissionsType, tribalLandId);
		}
				
		FacilityList fList = facilityViewService.getFacilityViewList(pageNumber,q,year,stateCode,
				fipsCode!=null?String.valueOf(fipsCode):"",msaCode!=null?String.valueOf(msaCode):"",basin!=null?String.valueOf(basin):"",lowE!=null?String.valueOf(lowE):"",highE!=null?String.valueOf(highE):"",
				gases, sectors, qo,
				ds, sc, sortOrder, is, rs, emissionsType, tribalLandId);		
		int numFacilities = fList.getTotalCount();
		if ("ALL".equals(rs) && ReportingStatusQueryFilter.isReportingStatusEnabled(year)) {
			int numFacilitiesStoppedReporting = facilityViewService.getStoppedReportingFacilitiesCount(pageNumber,q,year,stateCode,
					fipsCode!=null?String.valueOf(fipsCode):"",msaCode!=null?String.valueOf(msaCode):"",basin!=null?String.valueOf(basin):"", gases, sectors, qo, ds, sc, sortOrder, is, emissionsType, tribalLandId);
			numFacilities = numFacilities - numFacilitiesStoppedReporting; 
		}
		
		List<BigDecimal> sectorEmissions = new ArrayList<BigDecimal>();
		ServiceUtils.addToUnitList(sectorEmissions, sa.getPowerplantEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getLandfillEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getMetalEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getMineralEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getRefineryEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getPulpAndPaperEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getChemicalEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getOtherEmission());
		ServiceUtils.addToUnitList(sectorEmissions, sa.getPetroleumAndNaturalGasEmission());
		
		String unit = ServiceUtils.getUnit(sectorEmissions);

		JSONObject jsonParent = new JSONObject();
		jsonParent.put("unit", unit);
		
		if(sectors.isPowerPlants()) {
			jsonParent.accumulate("values", aggregateSector(sa.getPowerplantEmission(), unit)); // 0
			jsonParent.accumulate("values", sa.getPowerplantCount().intValue()); // 1
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isWaste()) {
			jsonParent.accumulate("values", aggregateSector(sa.getLandfillEmission(), unit)); // 2
			jsonParent.accumulate("values", sa.getLandfillCount().intValue()); // 3
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isMetals()) {
			jsonParent.accumulate("values", aggregateSector(sa.getMetalEmission(), unit)); // 4
			jsonParent.accumulate("values", sa.getMetalCount().intValue()); // 5
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isMinerals()) {
			jsonParent.accumulate("values", aggregateSector(sa.getMineralEmission(), unit)); // 6
			jsonParent.accumulate("values", sa.getMineralCount().intValue()); // 7
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isRefineries()) {
			jsonParent.accumulate("values", aggregateSector(sa.getRefineryEmission(), unit)); // 8
			jsonParent.accumulate("values", sa.getRefineryCount().intValue()); // 9
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isPulpAndPaper()) {
			jsonParent.accumulate("values", aggregateSector(sa.getPulpAndPaperEmission(), unit)); // 10
			jsonParent.accumulate("values", sa.getPulpAndPaperCount().intValue()); // 11
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isChemicals()) {
			jsonParent.accumulate("values", aggregateSector(sa.getChemicalEmission(), unit)); // 12
			jsonParent.accumulate("values", sa.getChemicalCount().intValue()); // 13
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isOther()) {
			jsonParent.accumulate("values", aggregateSector(sa.getOtherEmission(), unit)); // 14
			jsonParent.accumulate("values", sa.getOtherCount().intValue()); // 15
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		if(sectors.isPetroleumAndNaturalGas()) {
			jsonParent.accumulate("values", aggregateSector(sa.getPetroleumAndNaturalGasEmission(), unit)); // 16
			jsonParent.accumulate("values", sa.getPetroleumAndNaturalGasCount().intValue()); // 17
		} else {
			jsonParent.accumulate("values", 0);
			jsonParent.accumulate("values", 0);
		}
		jsonParent.accumulate("values", 0); // 18
		jsonParent.accumulate("values", numFacilities); // 19
		return jsonParent;
	}
}
