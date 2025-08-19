package gov.epa.ghg.presentation.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.epa.ghg.dao.DimFacilityStatusDAO;
import gov.epa.ghg.dto.FacilityDetail;
import gov.epa.ghg.dto.FacilityHoverTip;
import gov.epa.ghg.dto.PipeDetail;
import gov.epa.ghg.dto.PipeHoverTip;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.presentation.viewformatter.FacilitySpecificViewFormatter;
import gov.epa.ghg.service.FacilityDetailService;
import gov.epa.ghg.service.PipeDetailService;
import gov.epa.ghg.util.ServiceUtils;
import lombok.extern.log4j.Log4j2;

/**
 * Created by alabdullahwi on 8/20/2015.
 */

@Log4j2
@Controller
public class FacilitySpecificController {
	
	@Inject
	DimFacilityStatusDAO dimFacilityStatusDao;
	
	@Inject
	FacilityDetailService facilityDetailService;
	
	@Inject
	PipeDetailService pipeDetailService;
	
	@Inject
	FacilitySpecificViewFormatter viewFormatter;
	
	@Resource(name = "dataDate")
	private String dataDate;
	
	@Resource(name = "helplinkurlbase")
	private String helplinkurlbase;
	
	@RequestMapping(value = "/facilityName/{year}", method = RequestMethod.GET)
	public @ResponseBody
	String getFacilityName(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			ModelMap model) throws UnsupportedEncodingException {
		
		return facilityDetailService.getFacilityName(facilityId, year);
	}
	
	@RequestMapping(value = "/facilityHover/{year}", method = RequestMethod.GET)
	public String getFacilityHoverTip(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "et", required = false) String emissionsType,
			@RequestParam(value = "containerType") String containerType,
			ModelMap model) {
		
		String dataSource = ds;
		if ("O".equals(dataSource)
				|| "L".equals(dataSource)
				|| "F".equals(dataSource)
				|| "P".equals(dataSource)
				|| "B".equals(dataSource)) {
			dataSource = "E";
		} else if ("A".equals(dataSource)) {
			dataSource = "I";
		}
		Long vYear = new Long(year);
		String facType = "E";
		if ("S".equals(dataSource)) {
			facType = "S";
		} else if ("I".equals(dataSource)) {
			facType = "I";
		} else if ("A".equals(dataSource)) {
			facType = "A";
		}
		
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(facilityId, vYear, facType);
		ReportingStatus rs = rsMap.get(facilityId);
		FacilityHoverTip fht = facilityDetailService.getFacilityHoverTip(facilityId, year, dataSource, emissionsType, rs);
		model.addAttribute("fht", fht);
		model.addAttribute("ds", ds);
		model.addAttribute("year", year);
		model.addAttribute("dataDate", dataDate);
		// hoverTip for facility markers, bubbleTip for bubbles
		model.addAttribute("idName", containerType + "Tip");
		return "specific_fac_info";
	}
	
	@RequestMapping(value = "/facilityDetail/{year}", method = RequestMethod.GET)
	public String getFacility(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "ds", required = false) String dataSource,
			@RequestParam(value = "et", required = false) String emissionsType,
			@RequestParam(value = "popup", required = false) Boolean showPopup,
			ModelMap viewModel) {
		
		/** I commented this subSectorId part out because it's not used anywhere - Ahmed, August 2015 **/
/*		Long subSectorId = null;
		if(ds.equals("F")) {
			subSectorId = new Long(62);
		} else if (ds.equals("L")) {
			subSectorId = new Long(56);
		} else if (ds.equals("O")) {
			subSectorId = new Long(53);
		}*/
		
		/**Taken out for now while EPA thinks it over***/
		// PUB-136: links to the other data sources' facility pages
		// List<String> otherDataSources = facilityDetailService.checkOtherDataSources(facilityId, dataSource, subSectorId, year, emissionsType);
		// model.addAttribute("otherDataSources", otherDataSources);
		
		// cleanup
		if ("O".equals(dataSource)
				|| "L".equals(dataSource)
				|| "F".equals(dataSource)
				|| "P".equals(dataSource)
				|| "B".equals(dataSource)) {
			dataSource = "E";
		}
		String facType = "E";
		if ("S".equals(dataSource)) {
			facType = "S";
		} else if ("I".equals(dataSource)) {
			facType = "I";
		} else if ("A".equals(dataSource)) {
			facType = "A";
		}
		
		// prepare data
		FacilityDetail fd = facilityDetailService.getFacilityDetails(facilityId, year, dataSource, emissionsType);
		List<String> cmlSubparts = new ArrayList<String>();
		if (fd.getFacility().getProcessStationaryCml() != null && !fd.getFacility().getProcessStationaryCml().equals("")) {
			String processStationaryCml = fd.getFacility().getProcessStationaryCml();
			String[] subparts = processStationaryCml.split(",");
			for (String subpart : subparts) {
				cmlSubparts.add(subpart);
			}
		}
		Map<Long, ReportingStatus> rsMap =
				dimFacilityStatusDao.findByIdYearType(fd.getFacility().getId().getFacilityId(), fd.getFacility().getId().getYear(), facType);
		// format view
		viewFormatter.populateViewModel(viewModel, fd, cmlSubparts, dataSource, year, showPopup, rsMap);
		
		return "facdetail";
	}
	
	@RequestMapping(value = "/pipeDetail/{year}", method = RequestMethod.GET)
	public String getPipe(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "ds", required = false) String dataSource,
			@RequestParam(value = "et", required = false) String emissionsType,
			@RequestParam(value = "popup", required = false) Boolean showPopup,
			ModelMap viewModel) {
		
		String facType = "E";
		
		// prepare data
		PipeDetail fd = pipeDetailService.getPipeDetails(facilityId, year, "E", emissionsType);
		List<String> cmlSubparts = new ArrayList<String>();
		if (fd.getFacility().getProcessStationaryCml() != null && !fd.getFacility().getProcessStationaryCml().equals("")) {
			String processStationaryCml = fd.getFacility().getProcessStationaryCml();
			String[] subparts = processStationaryCml.split(",");
			for (String subpart : subparts) {
				cmlSubparts.add(subpart);
			}
		}
		Map<Long, ReportingStatus> rsMap =
				dimFacilityStatusDao.findByIdYearType(fd.getFacility().getId().getFacilityId(), fd.getFacility().getId().getYear(), facType);
		// format view
		viewFormatter.populateViewPipe(viewModel, fd, cmlSubparts, dataSource, year, showPopup, rsMap);
		
		return "facdetail";
	}
	
	@RequestMapping(value = "/pipeHover/{year}", method = RequestMethod.GET)
	public String getPipeHoverTip(
			@PathVariable int year,
			@RequestParam(value = "id", required = false) Long facilityId,
			@RequestParam(value = "ds", required = false) String ds,
			@RequestParam(value = "et", required = false) String emissionsType,
			@RequestParam(value = "containerType") String containerType,
			@RequestParam(value = "st", required = false) String state,
			ModelMap model) {
		
		String dataSource = ds;
		dataSource = "E";
		
		Long vYear = new Long(year);
		String facType = "E";
		
		Map<Long, ReportingStatus> rsMap = dimFacilityStatusDao.findByIdYearType(facilityId, vYear, facType);
		ReportingStatus rs = rsMap.get(facilityId);
		PipeHoverTip pht = pipeDetailService.getPipeHoverTip(facilityId, year, dataSource, emissionsType, rs, state);
		model.addAttribute("fht", pht);
		model.addAttribute("ds", ds);
		model.addAttribute("year", year);
		model.addAttribute("dataDate", dataDate);
		model.addAttribute("idName", containerType + "Tip");
		model.addAttribute("co2g", ServiceUtils.getIconInfo("co2g"));
		model.addAttribute("co2b", ServiceUtils.getIconInfo("co2b"));
		model.addAttribute("co2o", ServiceUtils.getIconInfo("co2o"));
		
		return "specific_fac_info";
	}
	
	@RequestMapping(value = "/facilityRRFile/{id}", method = RequestMethod.GET)
	public String getFacilityRRFile(HttpServletResponse response,
			@PathVariable Long id,
			@RequestParam(value = "yr", required = false) int year,
			@RequestParam(value = "et", required = false) String emissionsType,
			ModelMap model) throws IOException {
		log.info("get facilityRRFile request...");
		
		ServletOutputStream stream = null;
		try {
			FacilityDetail fd = facilityDetailService.getFacilityDetails(id, year, "E", emissionsType);
			String vFilename = fd.getFacility().getRrFilename();
			String vExtension = StringUtils.substringAfterLast(vFilename, ".");
			
			stream = response.getOutputStream();
			stream.write(fd.getFacility().getRrMonitoringPlan());
			if (vExtension.equalsIgnoreCase("pdf")) {
				response.setContentType("application/pdf");
			} else if (vExtension.equalsIgnoreCase("doc")) {
				response.setContentType("application/msword");
			} else if (vExtension.equalsIgnoreCase("docx")) {
				response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			}
			response.setHeader("Content-Disposition", "inline; filename=" +
					URLEncoder.encode(vFilename, "UTF-8"));
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.setHeader("Pragma", "");
			stream.write(fd.getFacility().getRrMonitoringPlan());
			response.flushBuffer();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (stream != null) {
				stream.close();
				stream = null;
			}
		}
		
		return null;
	}
	
}
