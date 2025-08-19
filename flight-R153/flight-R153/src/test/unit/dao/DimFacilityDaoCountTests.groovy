package dao

import gov.epa.ghg.dao.DimFacilityDao
import gov.epa.ghg.presentation.request.FlightRequest
import infrastructure.config.FlightDaoTest
import infrastructure.factories.TestObjectsFactory

import javax.inject.Inject

/**
 * Created by alabdullahwi on 9/15/2015.
 */
class DimFacilityDaoCountTests extends FlightDaoTest {


    @Inject
    DimFacilityDao dao


    def "it should get the total count for emitters datasource "() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest()

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 8199

    }

    def "it should get the total count for emitters datasource + msa" () {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(msaCode: 17140  )

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 48

    }


    def "it should get the total count for emitter datasource : plc case  "() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(state: 'NY')

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 244

    }

    def "it should get the total count for suppliers + supplierSector " () {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'S', supplierSector: 1)

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 995

    }

    def "it should get the total count for suppliers with no sector"() {
        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'S', supplierSector: 0)

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 0
    }

    def 'it should get the total count for onshore facilities + basinCode'() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'O', basin: '600' )

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 0


    }

    def 'it should get the total count for CO2 Injection'() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'I')

        when:
        Integer count = dao.getTotalCount(request)

        then:
        count != null
        count == 92

    }
}
