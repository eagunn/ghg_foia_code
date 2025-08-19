package gov.epa.ghg.presentation.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.epa.ghg.domain.DimState;
import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.service.FacilityDetailInterface;
import gov.epa.ghg.service.FacilityViewInterface;
import gov.epa.ghg.service.webservice.UrlShortenerWebServiceClient;
import net.sf.json.JSONObject;

/**
 * Created by alabdullahwi on 9/15/2015.
 * <p>
 * contains the handler for main.do as well as small methods that fit nowhere else
 */
@Controller
public class MainController {
	
	@Inject
	private UrlShortenerWebServiceClient urlShortener;
	
	@Inject
	@Qualifier("switchDataSourceUrl")
	private String switchDataSourceUrl;
	
	@Resource(name = "reportingYears")
	private Map<String, String> reportingYears;
	
	@Inject
	private FacilityViewInterface facilityViewService;
	
	@Inject
	private FacilityDetailInterface facilityDetailService;
	
	@Resource(name = "startYear")
	private Long startYear;
	
	@Resource(name = "endYear")
	private Long endYear;
	
	@Resource(name = "dataDate")
	private String dataDate;
	
	@Resource(name = "dataCredit")
	private String dataCredit;
	
	@Resource(name = "helplinkurlbase")
	private String helplinkurlbase;
	
	@Inject
	private SessionFactory sessionFactory;
	
	/**
	 * called when main page is first loaded ,
	 */
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String getMain(
			SitePreference sitePreference,
			HttpSession session, ModelMap model) {
		model.addAttribute("startYear", startYear);
		model.addAttribute("endYear", endYear);
		model.addAttribute("dataDate", dataDate);
		model.addAttribute("dataCredit", dataCredit);
		model.addAttribute("helplinkurlbase", helplinkurlbase);
		
		if (sitePreference == SitePreference.MOBILE) {
			return "redirect:home-screen.htm";
		} else {
			model.addAttribute("states", facilityViewService.getStates());
			model.addAttribute("tribes", facilityViewService.getTribalLands());
			model.addAttribute("dsUrl", switchDataSourceUrl);
			model.addAttribute("reportingYears", reportingYears);
			return "main";
		}
	}
	
	@RequestMapping(value = "/urlShortener", method = RequestMethod.GET)
	public @ResponseBody
	JSONObject getShortUrl(
			@RequestParam(value = "longUrl", required = true) String longUrl) {
		JSONObject retv = urlShortener.getShortUrl(longUrl);
		return retv;
	}
	
	/**
	 * probably legacy cruft
	 */
	@RequestMapping(value = "/suppliers.do", method = RequestMethod.GET)
	public String getMainS(HttpSession session, ModelMap model) {
		return "suppliers";
	}
	
	@RequestMapping(value = "/ldc.do", method = RequestMethod.GET)
	public String getLdc(HttpSession session, ModelMap model) {
		List<DimState> states = facilityViewService.getStates();
		model.addAttribute("states", states);
		model.addAttribute("dsUrl", switchDataSourceUrl);
		model.addAttribute("reportingYears", reportingYears);
		return "ldc";
	}
	
	@RequestMapping(value = "/switch.do", method = RequestMethod.GET)
	public String switchDS(HttpSession session, ModelMap model) {
		return "switch";
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public @ResponseBody
	String postDownload() {
		return StringUtils.EMPTY;
	}
	
	@RequestMapping(value = "/help", method = RequestMethod.POST)
	public @ResponseBody String postHelp() {
		return StringUtils.EMPTY;
	}
	
	@RequestMapping(value = "/html/{yr}", method = RequestMethod.GET)
	public void getHTML(
			@PathVariable String yr,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "et", required = false) String emissionsType,
			HttpServletResponse response) {
		
		int year;
		// The data source doesn't matter in this case
		String ds = "E";
		FacilityDetail fd = null;
		
		if (yr.equals("latest")) {
			fd = facilityDetailService.getLatestFacilityDetails(facilityId, ds, emissionsType);
			
			if (fd == null) {
				
				fd = facilityDetailService.getLatestFacilityDetails2(facilityId, ds, emissionsType);
				
			}
		} else {
			year = Integer.parseInt(yr);
			fd = facilityDetailService.getFacilityDetails(facilityId, year, ds, emissionsType);
		}
		
		long rYear = fd.getFacility().getId().getYear();
		year = (int) rYear;
		
		fd = facilityDetailService.getFacilityDetails(facilityId, year, ds, emissionsType);
		
		if (fd.getFacility().getId() != null) {
			try {
				response.setContentType("text/html");
				response.getOutputStream().write(fd.getFacility().getHtml().getBytes());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = "/xml/{yr}", method = RequestMethod.GET)
	public void getXml(
			@PathVariable String yr,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "et", required = false) String emissionsType,
			HttpServletResponse response) {
		
		int year;
		// The data source doesn't matter in this case
		String ds = "E";
		FacilityDetail fd = null;
		
		if (yr.equals("latest")) {
			fd = facilityDetailService.getLatestFacilityDetails(facilityId, ds, emissionsType);
			
			if (fd == null) {
				
				fd = facilityDetailService.getLatestFacilityDetails2(facilityId, ds, emissionsType);
				
			}
		} else {
			year = Integer.parseInt(yr);
			fd = facilityDetailService.getFacilityDetails(facilityId, year, ds, emissionsType);
		}
		
		long rYear = fd.getFacility().getId().getYear();
		year = (int) rYear;
		
		fd = facilityDetailService.getFacilityDetails(facilityId, year, ds, emissionsType);
		
		if (fd.getFacility().getId() != null) {
			try {
				response.setContentType("text/xml");
				response.getOutputStream().write(fd.getFacility().getPublicXml().getBytes());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = "/clear-cache.do", method = RequestMethod.GET)
	public String clearCache() {
		Map<String, ClassMetadata> classesMetadata = sessionFactory.getAllClassMetadata();
		for (String entityName : classesMetadata.keySet()) {
			try {
				sessionFactory.getCache().evictEntityData(entityName);
			} catch (Exception e) {
				
			}
		}
		return "redirect:main.do";
	}
}
