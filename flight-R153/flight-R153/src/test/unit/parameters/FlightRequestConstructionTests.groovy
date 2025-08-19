package parameters

import gov.epa.ghg.presentation.request.FlightRequest
import spock.lang.Specification

/**
 * Created by alabdullahwi on 8/20/2015.
 */
class FlightRequestConstructionTests extends Specification {


    def "it should be able to convert a request-param style byte representations of gases into a FlightRequest Boolean array   " () {

        given:
        Byte[] gases = [1,1,1,1,1,0,0,1,0,1] as Byte[]

        when:
        FlightRequest flightRequest = new FlightRequest('', 0, 0, '', '',0,0,1, '', 0, 0,'' ,'' ,-1,-1,'','', gases,null);

        then:
        Boolean[] bools = flightRequest.getGases()
        bools[0] == true
        bools[1] == true
        bools[2] == true
        bools[3] == true
        bools[4] == true
        bools[5] == false
        bools[6] == false
        bools[7] == true
        bools[8] == false
        bools[9] == true

    }

    def "it should be able to convert a request-param style byte representations of sectors into a FlightRequest Boolean array   " () {

        given:
        Byte[] _sectors =  [ 1,  0,1,1,0,1,   1,0,0,0,1,1,0,1, 0,0,0,1,1,1,  1,  1,0,1
                             ,1,0,1,0,0,1,0,0,1,1,1,1,    0, 1, 0,1,0,1,0,0,1,1,0, 0, 1,1,1,1,0,0,0,0,1] as Byte[];

        when:
        FlightRequest flightRequest = new FlightRequest('', 0, 0, '', '',0,0,1, '', 0, 0,'' ,'' ,-1,-1,'','',null,_sectors);

        then:
        Boolean[][] sectors = flightRequest.getSectors()
        sectors[0].length == 1
        sectors[0][0] == true

        sectors[1].length  ==  5
        sectors[1][0] == false
        sectors[1][1] == true
        sectors[1][2] == true
        sectors[1][3] == false
        sectors[1][4] == true

        sectors[2].length == 8
        sectors[2][0] == true
        sectors[2][1] == false
        sectors[2][2] == false
        sectors[2][3] == false
        sectors[2][4] == true
        sectors[2][5] == true
        sectors[2][6] == false
        sectors[2][7] == true

        sectors[3].length == 6
        sectors[3][0] == false
        sectors[3][1] == false
        sectors[3][2] == false
        sectors[3][3] == true
        sectors[3][4] == true
        sectors[3][5] == true

        sectors[4].length == 1
        sectors[4][0] == true


        sectors[5].length == 3
        sectors[5][0] == true
        sectors[5][1] == false
        sectors[5][2] == true

        sectors[6].length == 12
        sectors[6][0] == true
        sectors[6][1] == false
        sectors[6][2] == true
        sectors[6][3] == false
        sectors[6][4] == false
        sectors[6][5] == true
        sectors[6][6] == false
        sectors[6][7] == false
        sectors[6][8] == true
        sectors[6][9] == true
        sectors[6][10] == true
        sectors[6][11] == true

        sectors[7].length == 11
        sectors[7][0] == false
        sectors[7][1] == true
        sectors[7][2] == false
        sectors[7][3] == true
        sectors[7][4] == false
        sectors[7][5] == true
        sectors[7][6] == false
        sectors[7][7] == false
        sectors[7][8] == true
        sectors[7][9] == true
        sectors[7][10] == false

        sectors[8].length == 10
        sectors[8][0] == false
        sectors[8][1] == true
        sectors[8][2] == true
        sectors[8][3] == true
        sectors[8][4] == true
        sectors[8][5] == false
        sectors[8][6] == false
        sectors[8][7] == false
        sectors[8][8] == false
        sectors[8][9] == true


    }




}
