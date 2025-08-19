package gov.epa.ghg.service.view.list;

import java.io.Serializable;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateDao;
import gov.epa.ghg.dao.LuTribalLandsDao;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.presentation.request.FlightRequest;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
public class ModeDomainResolver implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimStateDao stateDao;
	
	@Inject
	DimCountyDao countyDao;
	
	@Inject
	BasinLayerDAO basinDao;
	
	@Inject
	LuTribalLandsDao tribalLandsDao;
	
	@Inject
	DimMsaDao msaDao;
	
	public enum Mode {
		
		FACILITY("Facility"),
		BASIN("Basin"),
		MSA("Metro Area"),
		STATE("State"),
		TRIBAL_LAND("Tribal Land"),
		COUNTY("County");
		
		Mode(String name) {
			this.viewName = name;
		}
		
		private String viewName;
		
		public String getViewName() {
			return viewName;
		}
	}
	
	/**
	 * This method determines the 'mode' of the list view's main table
	 * <p>
	 * By mode, we mean what the emissions are categorized into per row
	 * <p>
	 * could be per STATE, COUNTY, BASIN, MSA, TRIBAL_LAND or FACILITY
	 *
	 * @param request : The FlightRequest object, we will focus only on the location params (state/county/msa/tribe/basin)
	 */
	public Mode resolveMode(FlightRequest request) {
		
		String basinCode = request.getBasin();
		String state = request.getState();
		String countyFips = request.countyFips();
		String msaCode = request.msaCode();
		Long _tribalLandId = request.getTribalLandId();
		String tribalLandId = "";
		if (_tribalLandId != null) {
			tribalLandId = String.valueOf(_tribalLandId);
		}
		
		// if user has zoomed in on a basin, show emissions per facilities in that basin, otherwise show emissions per basin
		if ("O".equals(request.getDataSource()) || request.isBoosting()) {
			return (!StringUtils.hasLength(basinCode)) ? Mode.BASIN : Mode.FACILITY;
		}
		
		// no state picked, show emissions per state
		if (!StringUtils.hasLength(state)) {
			return Mode.STATE;
		}
		
		// at this point, there is a state.
		
		// If state is TRIBAL_LAND and user hasn't specified which
		// show per tribal land, but if there's a specific tribal land, show per facilities within it
		if ("TL".equals(state)) {
			return (StringUtils.hasLength(tribalLandId)) ? Mode.TRIBAL_LAND : Mode.FACILITY;
		}
		
		// there's a state, now check for county/msa. if there is no msa code. Show PER COUNTY if no county code exists
		if (!StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode)) {
			return Mode.COUNTY;
		}
		// by this point all we need to check if an msa exists or not, if county exists or msa code,
		if (!StringUtils.hasLength(msaCode)) {
			return Mode.MSA;
		}
		
		// by this point -- either msa/county codes exist and state is not tribal. All require show per facility
		return Mode.FACILITY;
	}
	
	public String resolveDomain(FlightRequest request) {
		
		String state = request.getState();
		String countyFips = request.countyFips();
		String msaCode = request.msaCode();
		Long tribalLandId = request.getTribalLandId();
		String basinCode = request.getBasin();
		
		String domain = EMPTY;
		
		if (!StringUtils.hasLength(state) && !StringUtils.hasLength(basinCode)) {
			return "U.S. - ";
		}
		
		if (StringUtils.hasLength(state) && !StringUtils.hasLength(countyFips) && !StringUtils.hasLength(msaCode)) {
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				return st.getStateName() + " - ";
			}
		}
		
		if (StringUtils.hasLength(countyFips)) {
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName();
			}
			DimCounty dc = countyDao.findById(countyFips);
			if (dc != null) {
				return domain + " - " + dc.getCountyName() + " County - ";
			}
		}
		
		if (StringUtils.hasLength(msaCode)) {
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName();
			}
			DimMsa dm = msaDao.getMsaByCode(msaCode);
			if (st != null && dm != null) {
				return domain + " - " + dm.getCbsa_title().split(",")[0] + " Metro Area - ";
			}
		}
		
		if (tribalLandId != null) {
			DimState st = stateDao.getStateByStateAbbr(state);
			if (st != null) {
				domain = st.getStateName();
			}
			LuTribalLands tribalLand = tribalLandsDao.findById(tribalLandId);
			if (tribalLand != null) {
				return domain + " - " + tribalLand.getTribalLandName() + " - ";
			}
			
		}
		
		if (StringUtils.hasLength(basinCode)) {
			BasinLayer basin = basinDao.getBasinByCode(basinCode);
			if (basin != null) {
				return basin.getBasin() + " - ";
			}
		}
		
		return EMPTY;
		
	}
	
}
