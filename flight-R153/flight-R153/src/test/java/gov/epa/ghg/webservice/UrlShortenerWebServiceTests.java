package gov.epa.ghg.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import gov.epa.ghg.service.webservice.UrlShortenerWebServiceClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by alabdullahwi on 4/24/2015.
 */
public class UrlShortenerWebServiceTests {
	
	String testUrl = "https://ghgdata.epa.gov/ghgp/main.do#/facilityDetail/?q=Find%20a%20Facility%20or%20Location&st=&bs=&et=undefined&fid=1005091&sf=11001000&lowE=0&highE=23000000&g1=1&g2=1&g3=1&g4=1&g5=1&g6=0&g7=1&g8=1&g9=1&g10=1&s1=1&s2=1&s3=1&s4=1&s5=1&s6=1&s7=1&s8=1&s9=1&s201=1&s202=1&s203=1&s204=1&s301=1&s302=1&s303=1&s304=1&s305=1&s306=1&s307=1&s401=1&s402=1&s403=1&s404=1&s405=1&s601=1&s602=1&s701=1&s702=1&s703=1&s704=1&s705=1&s706=1&s707=1&s708=1&s709=1&s710=1&s711=1&s801=1&s802=1&s803=1&s804=1&s805=1&s806=1&s807=1&s808=1&s809=1&s810=1&s901=1&s902=1&s903=1&s904=1&s905=1&s906=1&s907=1&s908=1&s909=1&si=&ss=&so=0&ds=E&yr=2013&tr=current&cyr=2013&rs=ALL";
	
	@Test
	public void itShouldBeAbleToEncodeAURLComponentCorrectly() throws Exception {
		String encoded = URLEncoder.encode(testUrl, "UTF-8");
		System.out.println(encoded);
		String decoded = URLDecoder.decode(encoded, "UTF-8");
		System.out.println(decoded);
		assert testUrl.equals(decoded);
	}
	
	@Test
	public void itShouldBeAbleToSendARequestUsingTheApiKey() throws IOException {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ghg.properties");
		Properties ghgProperties = new Properties();
		ghgProperties.load(in);
		UrlShortenerWebServiceClient urlShortenerWebService = new UrlShortenerWebServiceClient();
		ReflectionTestUtils.setField(urlShortenerWebService, "username", ghgProperties.getProperty("url.shortener.username"));
		ReflectionTestUtils.setField(urlShortenerWebService, "apiKey", ghgProperties.getProperty("url.shortener.apikey"));
		JSONObject response = urlShortenerWebService.getShortUrl(testUrl);
		String statusCode = (String) ((JSONObject) ((JSONArray) ((JSONObject) response.get("response")).get("0")).get(0)).get("status_code");
		assert "200".equals(statusCode);
		System.out.println(response.toString());
		
	}
	
}
