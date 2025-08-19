package infrastructure.config

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
/**
 * Created by alabdullahwi on 3/25/2015.
 */

class profiles {


    static class ahmed {

        static final firefoxBinaryPath = "C:\\Users\\alabdullahwi\\Code\\Browsers\\Firefox 28\\firefox.exe"

        static def universal =  [

                                ]
        static def local = [
                            baseUrl : 'https://localhost:8070/ghgp/',
                            browserPosition: new Point(2020, -1020) ,
                            browserSize:  new Dimension(1200,1024)
                           ]
        static def production = [
                            baseUrl :  'https://ghgdata.epa.gov',
                            ]
    }



}
