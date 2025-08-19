package domain

import gov.epa.ghg.dao.DimFacilityDao
import gov.epa.ghg.dto.FacilityDetail
import infrastructure.config.FlightDaoTest
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by alabdullahwi on 8/11/2015.
 */
class FacilitySubpartKeyValTests extends FlightDaoTest {


    @Autowired
    DimFacilityDao dao


    def "it should be able to retrieve FacilitySubpartKeyVal object "() {


        //randomly chosen from DEV DB, datasource (E here)   and emissions won't affect the DB query
        when:
        FacilityDetail retv = dao.getFacilityDetails(1004607, 2012 ,"E","PE")

        then:
        retv != null
        retv.subpartDetails != null
        retv.subpartDetails.each {
            it.value.each {
                println it.id
                println it.luKey
                println it.notes
            }
        }

    }

}
