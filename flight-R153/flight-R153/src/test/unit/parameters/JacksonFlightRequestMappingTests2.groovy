package parameters

import gov.epa.ghg.presentation.request.FlightRequest
import org.codehaus.jackson.JsonGenerationException
import org.codehaus.jackson.JsonParseException
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import spock.lang.Specification

/**
 * Created by alabdullahwi on 8/20/2015.
 */
class JacksonFlightRequestMappingTests2 extends Specification {

    /**
     * this is culled from an old Java test, it just prints out the result, no real testing is made
     */
     def " it should convert a json string to a FlightRequest object "() {

        given:
        ObjectMapper jackson = new ObjectMapper();
        String fromApp = "{\"query\":\"\",\"lowE\":\"0\",\"highE\":\"23000000\",\"pageNumber\":\"7\",\"state\":\"\",\"countyFips\":\"\",\"basin\":\"&bs=\",\"gases\":[true,true,true,true,true,false,true,true,true,true],\"sectors\":[[true],[true,false,false,false,false],[true,false,false,false,false,false,false,false],[true,false,false,false,false,false],[true],[true,false,false],[true,false,false,false,false,false,false,false,false,false,false,false],[true,false,false,false,false,false,false,false,false,false,false],[true,false,false,false,false,false,false,false,false,false]],\"supplierSector\":0,\"reportingStatus\":\"ALL\",\"fs\":\"11001000\"}";
        FlightRequest request = new FlightRequest();
        request.setBasin("123");
        Boolean[][] sectors = new Boolean[9][];

        Boolean[] arr = new Boolean[2];
        arr[0] = true;
        arr[1] = false;
        sectors[0] = arr;
        request.setSectors(sectors);
        String result="";

        when:
        try {
            result = jackson.writeValueAsString(request);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        then:
        System.out.println(result);

        when:
        try {
            request = jackson.readValue(fromApp, FlightRequest.class);
            result = jackson.writeValueAsString(request);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        then:
        System.out.println(result);
    }
}
