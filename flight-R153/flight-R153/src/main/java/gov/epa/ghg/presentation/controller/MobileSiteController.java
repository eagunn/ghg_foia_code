package gov.epa.ghg.presentation.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.dto.State;
import gov.epa.ghg.service.FacilityDetailInterface;
import gov.epa.ghg.service.FacilityViewInterface;
import gov.epa.ghg.service.MobileFacilityInterface;
import gov.epa.ghg.util.SpatialUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class MobileSiteController {
	
	@Inject
	private FacilityViewInterface facilityViewService;
	
	@Inject
	private FacilityDetailInterface facilityDetailService;
	
	@Inject
	private MobileFacilityInterface mobileFacilityService;
	
	@RequestMapping(value = "/states", method = RequestMethod.GET)
	public @ResponseBody List<State> getStates() {
		List<DimState> states = facilityViewService.getStates();
		List<State> l = new ArrayList<State>();
		for (DimState ds : states) {
			l.add(new State(ds.getState(), ds.getStateName()));
		}
		return l;
	}
	
	@RequestMapping(value = "/facilityList/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Facility> getFacilityList(
			@PathVariable int year,
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "rs", required = false) String rs,
			@RequestParam(value = "st", required = false) String stateCode,
			@RequestParam(value = "fc", required = false) Integer fipsCode,
			@RequestParam(value = "pg", required = false) int pageNumber,
			@RequestParam(value = "lowE", required = false) Long lowE,
			@RequestParam(value = "highE", required = false) Long highE,
			@RequestParam(value = "g1", required = false) Byte g1,
			@RequestParam(value = "g2", required = false) Byte g2,
			@RequestParam(value = "g3", required = false) Byte g3,
			@RequestParam(value = "g4", required = false) Byte g4,
			@RequestParam(value = "g5", required = false) Byte g5,
			@RequestParam(value = "g6", required = false) Byte g6,
			@RequestParam(value = "g7", required = false) Byte g7,
			@RequestParam(value = "g8", required = false) Byte g8,
			@RequestParam(value = "g9", required = false) Byte g9,
			@RequestParam(value = "g10", required = false) Byte g10,
			@RequestParam(value = "g11", required = false) Byte g11,
			@RequestParam(value = "g12", required = false) Byte g12,
			@RequestParam(value = "s1", required = false) Byte s1,
			@RequestParam(value = "s2", required = false) Byte s2,
			@RequestParam(value = "s3", required = false) Byte s3,
			@RequestParam(value = "s4", required = false) Byte s4,
			@RequestParam(value = "s5", required = false) Byte s5,
			@RequestParam(value = "s6", required = false) Byte s6,
			@RequestParam(value = "s7", required = false) Byte s7,
			@RequestParam(value = "s8", required = false) Byte s8,
			@RequestParam(value = "s9", required = false) Byte s9,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "sc", required = false) int sc,
			@RequestParam(value = "so", required = false) int sortOrder) {
		GasFilter gases = new GasFilter(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12);
		SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9);
		QueryOptions qo = new QueryOptions(null);
		if (stateCode.equalsIgnoreCase("US")) {
			stateCode = "";
		}
		return mobileFacilityService.getFacilityList(pageNumber, q, year, stateCode,
				fipsCode != null ? String.valueOf(fipsCode) : "", lowE != null ? String.valueOf(lowE) : "", highE != null ? String.valueOf(highE) : "",
				gases, sectors, qo,
				ds, sc, sortOrder, rs);
	}
	
	@RequestMapping(value = "/facilitiesAround/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Facility> getFacilitiesAround(
			@PathVariable int year,
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "rs", required = false) String rs,
			@RequestParam(value = "st", required = false) String stateCode,
			@RequestParam(value = "fc", required = false) Integer fipsCode,
			@RequestParam(value = "lowE", required = false) Long lowE,
			@RequestParam(value = "highE", required = false) Long highE,
			@RequestParam(value = "g1", required = false) Byte g1,
			@RequestParam(value = "g2", required = false) Byte g2,
			@RequestParam(value = "g3", required = false) Byte g3,
			@RequestParam(value = "g4", required = false) Byte g4,
			@RequestParam(value = "g5", required = false) Byte g5,
			@RequestParam(value = "g6", required = false) Byte g6,
			@RequestParam(value = "g7", required = false) Byte g7,
			@RequestParam(value = "g8", required = false) Byte g8,
			@RequestParam(value = "g9", required = false) Byte g9,
			@RequestParam(value = "g10", required = false) Byte g10,
			@RequestParam(value = "g11", required = false) Byte g11,
			@RequestParam(value = "g12", required = false) Byte g12,
			@RequestParam(value = "s1", required = false) Byte s1,
			@RequestParam(value = "s2", required = false) Byte s2,
			@RequestParam(value = "s3", required = false) Byte s3,
			@RequestParam(value = "s4", required = false) Byte s4,
			@RequestParam(value = "s5", required = false) Byte s5,
			@RequestParam(value = "s6", required = false) Byte s6,
			@RequestParam(value = "s7", required = false) Byte s7,
			@RequestParam(value = "s8", required = false) Byte s8,
			@RequestParam(value = "s9", required = false) Byte s9,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "sc", required = false) int sc,
			@RequestParam(value = "lt", required = true) double lt,
			@RequestParam(value = "ln", required = true) double ln) {
		GasFilter gases = new GasFilter(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12);
		SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9);
		QueryOptions qo = new QueryOptions(null);
		LatLng center = new LatLng(lt, ln);
		return mobileFacilityService.getFacilityListAround(q, year, stateCode,
				fipsCode != null ? String.valueOf(fipsCode) : "", lowE != null ? String.valueOf(lowE) : "", highE != null ? String.valueOf(highE) : "",
				gases, sectors, qo,
				ds, sc, center, 10.0d, rs);
	}
	
	@RequestMapping(value = "/facilitiesWithin/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Facility> getFacilitiesWithin(
			@PathVariable int year,
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "rs", required = false) String rs,
			@RequestParam(value = "st", required = false) String stateCode,
			@RequestParam(value = "fc", required = false) Integer fipsCode,
			@RequestParam(value = "lowE", required = false) Long lowE,
			@RequestParam(value = "highE", required = false) Long highE,
			@RequestParam(value = "g1", required = false) Byte g1,
			@RequestParam(value = "g2", required = false) Byte g2,
			@RequestParam(value = "g3", required = false) Byte g3,
			@RequestParam(value = "g4", required = false) Byte g4,
			@RequestParam(value = "g5", required = false) Byte g5,
			@RequestParam(value = "g6", required = false) Byte g6,
			@RequestParam(value = "g7", required = false) Byte g7,
			@RequestParam(value = "g8", required = false) Byte g8,
			@RequestParam(value = "g9", required = false) Byte g9,
			@RequestParam(value = "g10", required = false) Byte g10,
			@RequestParam(value = "g11", required = false) Byte g11,
			@RequestParam(value = "g12", required = false) Byte g12,
			@RequestParam(value = "s1", required = false) Byte s1,
			@RequestParam(value = "s2", required = false) Byte s2,
			@RequestParam(value = "s3", required = false) Byte s3,
			@RequestParam(value = "s4", required = false) Byte s4,
			@RequestParam(value = "s5", required = false) Byte s5,
			@RequestParam(value = "s6", required = false) Byte s6,
			@RequestParam(value = "s7", required = false) Byte s7,
			@RequestParam(value = "s8", required = false) Byte s8,
			@RequestParam(value = "s9", required = false) Byte s9,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "sc", required = false) int sc,
			@RequestParam(value = "lt", required = true) double lt,
			@RequestParam(value = "ln", required = true) double ln,
			@RequestParam(value = "swlt", required = true) double swlt,
			@RequestParam(value = "swln", required = true) double swln,
			@RequestParam(value = "nelt", required = true) double nelt,
			@RequestParam(value = "neln", required = true) double neln) {
		
		GasFilter gases = new GasFilter(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12);
		SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9);
		QueryOptions qo = new QueryOptions(null);
		
		LatLng center = new LatLng(lt, ln);
		Coordinate sw = new Coordinate(swln, swlt);
		Coordinate ne = new Coordinate(neln, nelt);
		Geometry bounds = SpatialUtil.createLatLngBounds(sw, ne);
		if (stateCode.equalsIgnoreCase("US")) {
			stateCode = "";
		}
		return mobileFacilityService.getFacilityListWithin(q, year, stateCode,
				fipsCode != null ? String.valueOf(fipsCode) : "", lowE != null ? String.valueOf(lowE) : "", highE != null ? String.valueOf(highE) : "",
				gases, sectors, qo,
				ds, sc, center, bounds, rs);
	}
	
	@RequestMapping(value = "/facilitiesInfo/{year}", method = RequestMethod.GET)
	public @ResponseBody FacilityDetail getFacilityInfo(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "et", required = false) String emissionsType) {
		
		return facilityDetailService.getFacilityDetails(facilityId, year, ds, emissionsType);
	}
	
	@RequestMapping(value = "/filterFacilities/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Facility> postFilterFacilities(
			@PathVariable int year,
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "st", required = false) String stateCode,
			@RequestParam(value = "fc", required = false) Integer fipsCode,
			@RequestParam(value = "lowE", required = false) Long lowE,
			@RequestParam(value = "highE", required = false) Long highE,
			@RequestParam(value = "g1", required = false) Byte g1,
			@RequestParam(value = "g2", required = false) Byte g2,
			@RequestParam(value = "g3", required = false) Byte g3,
			@RequestParam(value = "g4", required = false) Byte g4,
			@RequestParam(value = "g5", required = false) Byte g5,
			@RequestParam(value = "g6", required = false) Byte g6,
			@RequestParam(value = "g7", required = false) Byte g7,
			@RequestParam(value = "g8", required = false) Byte g8,
			@RequestParam(value = "g9", required = false) Byte g9,
			@RequestParam(value = "g10", required = false) Byte g10,
			@RequestParam(value = "g11", required = false) Byte g11,
			@RequestParam(value = "g12", required = false) Byte g12,
			@RequestParam(value = "s1", required = false) Byte s1,
			@RequestParam(value = "s2", required = false) Byte s2,
			@RequestParam(value = "s3", required = false) Byte s3,
			@RequestParam(value = "s4", required = false) Byte s4,
			@RequestParam(value = "s5", required = false) Byte s5,
			@RequestParam(value = "s6", required = false) Byte s6,
			@RequestParam(value = "s7", required = false) Byte s7,
			@RequestParam(value = "s8", required = false) Byte s8,
			@RequestParam(value = "s9", required = false) Byte s9,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "sc", required = false) int sc,
			@RequestParam(value = "so", required = false) int sortOrder) {
		return null;
	}
    /*
    @RequestMapping(value = "/sendUserFeedback", method = RequestMethod.GET)
    public @ResponseBody String postUserFeedback(
            @RequestParam(value="name", required=false) String name,
            @RequestParam(value="message", required=false) String message ) {
        
        SimpleEmail email = new SimpleEmail();
        email.setHostName("mail.saic.com");
        try {
            email.addTo("ismaelm@saic.com", "Moustafa Ismael");
            email.setFrom("feedback@apache.org", name);
            email.setSubject("User Feedback");
            email.setMsg(message);
            email.send();
            return "success";
        } catch (EmailException e) {
            e.printStackTrace();
            return "fail";
        }
    }
    */
}
