package gov.epa.ghg.presentation.controller.view;

import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.service.MyClimatePieChartService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by alabdullahwi on 9/13/2015.
 */
@Controller
public class MyClimatePieViewController {

        @Inject
        MyClimatePieChartService mcPieChartService;

        @RequestMapping(value = "/pieSector.jsonp/{ds}/{year}", method = RequestMethod.GET)
        public ResponseEntity<Map<String, Object>> pieSectorJSONP(
                @PathVariable(value="ds") String ds,
                @PathVariable(value="year") int year,
                @RequestParam(value="q", required=false) String q,
                @RequestParam(value="st", required=false) String state,
                @RequestParam(value="fc", required=false) Integer countyFips,
                @RequestParam(value="mc", required=false) Integer msaCode,
                @RequestParam(value="bs", required=false) String basin,
                @RequestParam(value="tl", required=false) Long tribalLandId,
                @RequestParam(value="sf", required=false) String searchOptions,
                @RequestParam(value="lowE", required=false) Long lowE,
                @RequestParam(value="highE", required=false) Long highE,
                @RequestParam(value="et", required=false) String emissionsType,
                @RequestParam(value="g1", required=false) Byte g1,
                @RequestParam(value="g2", required=false) Byte g2,
                @RequestParam(value="g3", required=false) Byte g3,
                @RequestParam(value="g4", required=false) Byte g4,
                @RequestParam(value="g5", required=false) Byte g5,
                @RequestParam(value="g6", required=false) Byte g6,
                @RequestParam(value="g7", required=false) Byte g7,
                @RequestParam(value="g8", required=false) Byte g8,
                @RequestParam(value="g9", required=false) Byte g9,
                @RequestParam(value="g10", required=false) Byte g10,
                @RequestParam(value="g11", required=false) Byte g11,
                @RequestParam(value="g12", required=false) Byte g12,
                @RequestParam(value="s1", required=false) Byte s1,
                @RequestParam(value="s2", required=false) Byte s2,
                @RequestParam(value="s3", required=false) Byte s3,
                @RequestParam(value="s4", required=false) Byte s4,
                @RequestParam(value="s5", required=false) Byte s5,
                @RequestParam(value="s6", required=false) Byte s6,
                @RequestParam(value="s7", required=false) Byte s7,
                @RequestParam(value="s8", required=false) Byte s8,
                @RequestParam(value="s9", required=false) Byte s9,
                @RequestParam(value="s201", required=false) Byte s201,
                @RequestParam(value="s202", required=false) Byte s202,
                @RequestParam(value="s203", required=false) Byte s203,
                @RequestParam(value="s204", required=false) Byte s204,
                @RequestParam(value="s301", required=false) Byte s301,
                @RequestParam(value="s302", required=false) Byte s302,
                @RequestParam(value="s303", required=false) Byte s303,
                @RequestParam(value="s304", required=false) Byte s304,
                @RequestParam(value="s305", required=false) Byte s305,
                @RequestParam(value="s306", required=false) Byte s306,
                @RequestParam(value="s307", required=false) Byte s307,
                @RequestParam(value="s401", required=false) Byte s401,
                @RequestParam(value="s402", required=false) Byte s402,
                @RequestParam(value="s403", required=false) Byte s403,
                @RequestParam(value="s404", required=false) Byte s404,
                @RequestParam(value="s405", required=false) Byte s405,
                @RequestParam(value="s601", required=false) Byte s601,
                @RequestParam(value="s602", required=false) Byte s602,
                @RequestParam(value="s701", required=false) Byte s701,
                @RequestParam(value="s702", required=false) Byte s702,
                @RequestParam(value="s703", required=false) Byte s703,
                @RequestParam(value="s704", required=false) Byte s704,
                @RequestParam(value="s705", required=false) Byte s705,
                @RequestParam(value="s706", required=false) Byte s706,
                @RequestParam(value="s707", required=false) Byte s707,
                @RequestParam(value="s708", required=false) Byte s708,
                @RequestParam(value="s709", required=false) Byte s709,
                @RequestParam(value="s710", required=false) Byte s710,
                @RequestParam(value="s711", required=false) Byte s711,
                @RequestParam(value="s801", required=false) Byte s801,
                @RequestParam(value="s802", required=false) Byte s802,
                @RequestParam(value="s803", required=false) Byte s803,
                @RequestParam(value="s804", required=false) Byte s804,
                @RequestParam(value="s805", required=false) Byte s805,
                @RequestParam(value="s806", required=false) Byte s806,
                @RequestParam(value="s807", required=false) Byte s807,
                @RequestParam(value="s808", required=false) Byte s808,
                @RequestParam(value="s809", required=false) Byte s809,
                @RequestParam(value="s810", required=false) Byte s810,
                @RequestParam(value="s901", required=false) Byte s901,
                @RequestParam(value="s902", required=false) Byte s902,
                @RequestParam(value="s903", required=false) Byte s903,
                @RequestParam(value="s904", required=false) Byte s904,
                @RequestParam(value="s905", required=false) Byte s905,
                @RequestParam(value="s906", required=false) Byte s906,
                @RequestParam(value="s907", required=false) Byte s907,
                @RequestParam(value="s908", required=false) Byte s908,
                @RequestParam(value="s909", required=false) Byte s909,
                @RequestParam(value="s910", required=false) Byte s910,
                @RequestParam(value="s911", required=false) Byte s911,
                @RequestParam(value="rs", required=false) String rs,
                @RequestParam(value="callback", required=false) String callback) {

        		Map<String, Object> json = new HashMap<String, Object>();

                GasFilter gases = new GasFilter(g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12);
                SectorFilter sectors = new SectorFilter(s1, s2, s3, s4, s5, s6, s7, s8, s9,
                        s201, s202, s203, s204,
                        s301, s302, s303, s304, s305, s306, s307,
                        s401, s402, s403, s404, s405,
                        s601, s602,
                        s701, s702, s703, s704, s705, s706,	s707, s708, s709, s710, s711,
                        s801, s802, s803, s804, s805, s806, s807, s808, s809, s810,
                        s901, s902, s903, s904, s905, s906, s907, s908, s909, s910, s911);
                QueryOptions qo = new QueryOptions(searchOptions);
                //changes state back to an empty string so it will retrieve all facilities in the US
                if (state.equals("US")) {
                    state = "";
                }
                json = mcPieChartService.pieChartSector(q,year,lowE!=null?String.valueOf(lowE):"",String.valueOf(highE),state,countyFips!=null?String.valueOf(countyFips):"",
                    msaCode!=null?String.valueOf(msaCode):"",gases, sectors, qo, ds, rs, emissionsType, tribalLandId);
                //String s = callback+"("+json.toString()+")";
                //return s;
                HttpHeaders headers = new HttpHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                return (new ResponseEntity<Map<String, Object>>(json, headers, HttpStatus.OK));
        }
}
