package gov.epa.ghg.presentation.controller.view;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.epa.ghg.domain.FacilityViewSub;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.service.FacilityViewService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class MapViewController {
	
	@Inject
	private FacilityViewService facilityViewService;
	
	/**
	 * This returns the facility markers to be placed on the map view
	 * it simply delegates to the backing service which determines which dao method to call
	 *
	 * @param request : FlightRequest (json)
	 *
	 * @return : a list of facility map markers
	 */
	@RequestMapping(value = "/mapFacilities", method = RequestMethod.GET)
	public @ResponseBody List<FacilityViewSub> getFacilities
	(
			@RequestParam(value = "ds") String sectorType, @RequestParam(value = "ryr") int year, @RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "st", required = false) String state, @RequestParam(value = "fc", required = false) Integer countyFips,
			@RequestParam(value = "mc", required = false) Integer msaCode, @RequestParam(value = "tl", required = false) Long tribalLandId,
			@RequestParam(value = "sf", required = false) String searchOptions, @RequestParam(value = "lowE", required = false) Long lowE,
			@RequestParam(value = "highE", required = false) Long highE, @RequestParam(value = "et", required = false) String emissionsType,
			@RequestParam(value = "tr", required = false) String trend, @RequestParam(value = "cyr", required = false) Integer currentYear,
			@RequestParam(value = "g1", required = false) Byte g1, @RequestParam(value = "g2", required = false) Byte g2,
			@RequestParam(value = "g3", required = false) Byte g3, @RequestParam(value = "g4", required = false) Byte g4,
			@RequestParam(value = "g5", required = false) Byte g5, @RequestParam(value = "g6", required = false) Byte g6,
			@RequestParam(value = "g7", required = false) Byte g7, @RequestParam(value = "g8", required = false) Byte g8,
			@RequestParam(value = "g9", required = false) Byte g9, @RequestParam(value = "g10", required = false) Byte g10,
			@RequestParam(value = "g11", required = false) Byte g11, @RequestParam(value = "g12", required = false) Byte g12,
			@RequestParam(value = "s1", required = false) Byte s1, @RequestParam(value = "s2", required = false) Byte s2,
			@RequestParam(value = "s3", required = false) Byte s3, @RequestParam(value = "s4", required = false) Byte s4,
			@RequestParam(value = "s5", required = false) Byte s5, @RequestParam(value = "s6", required = false) Byte s6,
			@RequestParam(value = "s7", required = false) Byte s7, @RequestParam(value = "s8", required = false) Byte s8,
			@RequestParam(value = "s9", required = false) Byte s9, @RequestParam(value = "s201", required = false) Byte s201,
			@RequestParam(value = "s202", required = false) Byte s202, @RequestParam(value = "s203", required = false) Byte s203,
			@RequestParam(value = "s204", required = false) Byte s204, @RequestParam(value = "s301", required = false) Byte s301,
			@RequestParam(value = "s302", required = false) Byte s302, @RequestParam(value = "s303", required = false) Byte s303,
			@RequestParam(value = "s304", required = false) Byte s304, @RequestParam(value = "s305", required = false) Byte s305,
			@RequestParam(value = "s306", required = false) Byte s306, @RequestParam(value = "s307", required = false) Byte s307,
			@RequestParam(value = "s401", required = false) Byte s401, @RequestParam(value = "s402", required = false) Byte s402,
			@RequestParam(value = "s403", required = false) Byte s403, @RequestParam(value = "s404", required = false) Byte s404,
			@RequestParam(value = "s405", required = false) Byte s405, @RequestParam(value = "s601", required = false) Byte s601,
			@RequestParam(value = "s602", required = false) Byte s602, @RequestParam(value = "s701", required = false) Byte s701,
			@RequestParam(value = "s702", required = false) Byte s702, @RequestParam(value = "s703", required = false) Byte s703,
			@RequestParam(value = "s704", required = false) Byte s704, @RequestParam(value = "s705", required = false) Byte s705,
			@RequestParam(value = "s706", required = false) Byte s706, @RequestParam(value = "s707", required = false) Byte s707,
			@RequestParam(value = "s708", required = false) Byte s708, @RequestParam(value = "s709", required = false) Byte s709,
			@RequestParam(value = "s710", required = false) Byte s710, @RequestParam(value = "s711", required = false) Byte s711,
			@RequestParam(value = "s801", required = false) Byte s801, @RequestParam(value = "s802", required = false) Byte s802,
			@RequestParam(value = "s803", required = false) Byte s803, @RequestParam(value = "s804", required = false) Byte s804,
			@RequestParam(value = "s805", required = false) Byte s805, @RequestParam(value = "s806", required = false) Byte s806,
			@RequestParam(value = "s807", required = false) Byte s807, @RequestParam(value = "s808", required = false) Byte s808,
			@RequestParam(value = "s809", required = false) Byte s809, @RequestParam(value = "s810", required = false) Byte s810,
			@RequestParam(value = "s901", required = false) Byte s901, @RequestParam(value = "s902", required = false) Byte s902,
			@RequestParam(value = "s903", required = false) Byte s903, @RequestParam(value = "s904", required = false) Byte s904,
			@RequestParam(value = "s905", required = false) Byte s905, @RequestParam(value = "s906", required = false) Byte s906,
			@RequestParam(value = "s907", required = false) Byte s907, @RequestParam(value = "s908", required = false) Byte s908,
			@RequestParam(value = "s909", required = false) Byte s909, @RequestParam(value = "s910", required = false) Byte s910,
			@RequestParam(value = "s911", required = false) Byte s911, @RequestParam(value = "sc", required = false) Integer sc,
			@RequestParam(value = "is", required = false) Integer is, @RequestParam(value = "rs", required = false) String rs,
			@RequestParam(value = "bs", required = false) String basin
	) {
		Byte[] gases = {g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12};
		Byte[] sectors =
				{
						s1, s2, s201, s202, s203, s204, s3, s301, s302, s303, s304, s305, s306, s307, s4, s401, s402, s403, s404,
						s405, s5, s6, s601, s602, s7, s701, s702, s703, s704, s705, s706, s707, s708, s709, s710, s711, s8, s801,
						s802, s803, s804, s805, s806, s807, s808, s809, s810, s9, s901, s902, s903, s904, s905, s906, s907, s908,
						s909, s910, s911
				};
		FlightRequest flightRequest =
				new FlightRequest(
						sectorType, year, currentYear, q, state, countyFips, msaCode, tribalLandId, searchOptions, lowE, highE,
						emissionsType, trend, sc, is, rs, basin, gases, sectors, 0, 0
				);
		
		log.info("Facility map started...");
		StopWatch sw = new StopWatch();
		sw.start();
		List<FacilityViewSub> fList = facilityViewService.getFacilityGeoData(flightRequest);
		sw.stop();
		log.debug("MapFacilities: " + sw.getTotalTimeSeconds() + "s");
		log.info("Facility map completed. Size: " + fList.size());
		return fList;
	}
	
	/**
	 * returns a FacilityViewSub list populated by facilities and their emissions
	 * <p>
	 * this works for any overlay over the map (bubble for example, the legacy heat-map method was deleted because it's dao method is archaic)
	 */
	@RequestMapping(value = "/mapOverlay", method = RequestMethod.POST)
	public @ResponseBody List<FacilityViewSub> getFacilityEmissionBubbleMap(@RequestBody FlightRequest request) {
		log.info("Facility map started...");
		List<FacilityViewSub> fList;
		// bubble
		if (request.getOverlayLevel() == 1) {
			if (request.isPipe()) {
				fList = facilityViewService.getPipeFacilityEmissions(request);
			} else {
				fList = facilityViewService.getFacilityEmissions(request);
			}
		}
		// default (facilities)
		else {
			fList = facilityViewService.getFacilityGeoData(request);
		}
		
		log.info("Facility map completed. Size: " + fList.size());
		return fList;
	}
}
