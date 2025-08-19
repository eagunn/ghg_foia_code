package gov.epa.ghg.service.webservice;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alabdullahwi on 4/24/2015.
 */
@Service
public class UrlShortenerWebServiceClient {

    //those are injected from ghg.properties by Spring
    @Value("${url.shortener.username}")
    private String username;
    @Value("${url.shortener.apikey}")
    private String apiKey;


    /**
     *
     * Sanitized a longurl as the API expects it to be encoded a certain way
     *
     *  the pound sign is encoded because if left as is it will be treated as an anchor and everything after it will be ignored by the GoUSA api
     *  spaces are okay, leave as it
     *  & signs must be encoded or they will be igonred
     *  no need for any library
     * @param url
     * @return
     */
    private String munge(String url) {
        url = url.replace("#", "%23");
        url = url.replace("&amp;", "%26");
        return url;
    }

    /**
     *
     * Makes a call to the USA Go governmental URL shortener web service
     *
     * @param longUrl
     * @return a short url representation of the long url
     */
    public JSONObject getShortUrl(String longUrl) {
        JSONObject retv = null;
        longUrl = munge(longUrl);
            String goUSAApiRequest = "https://go.usa.gov/api/shorten.jsonp?login="+username+"&apiKey="+apiKey+"&longUrl="+longUrl;
            URL obj = null;
            try {
                obj =  new URL(goUSAApiRequest);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String jsonResponse = response.toString();
                retv = (JSONObject) JSONObject.fromObject(jsonResponse).get("response");
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            return retv;
        }

}
