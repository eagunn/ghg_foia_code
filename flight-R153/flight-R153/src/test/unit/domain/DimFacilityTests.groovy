package domain

import gov.epa.ghg.dao.DimFacilityDao
import gov.epa.ghg.domain.DimFacility
import gov.epa.ghg.enums.FacilityType
import gov.epa.ghg.enums.FacilityViewType
import gov.epa.ghg.presentation.request.FlightRequest
import infrastructure.config.FlightDaoTest
import infrastructure.factories.TestObjectsFactory
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject

/**
 * Created by alabdullahwi on 12/9/2015.
 */
class DimFacilityTests extends FlightDaoTest {

    @Inject
    DimFacilityDao dimFacilityDao;

    @Transactional
    def "each DimFacility should belong to only one basin"() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(reportingYear: 2013, dataSource : FacilityType.ONSHORE.asInitial())

        when:
        List<DimFacility> facilities = dimFacilityDao.loadDimFacilities(request, FacilityViewType.EXPORT)

        then:
        def count = 0
        facilities.each {
            if (it.basins != null && it.basins.size() > 1 )  {
                count++
                println it.id.facilityId + " " + it.facilityName
            }
        }

        println "count is ${count} "

    }

}
