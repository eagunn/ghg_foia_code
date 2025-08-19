package dao

import gov.epa.ghg.dao.DimFacilityDao
import gov.epa.ghg.enums.FacilityViewType
import gov.epa.ghg.presentation.request.FlightRequest
import infrastructure.config.FlightDaoTest
import infrastructure.factories.TestObjectsFactory

import javax.inject.Inject

/**
 * Created by edkhalid on 2/15/16.
 */
class Co2InjectionQueryTests extends FlightDaoTest {


    @Inject
    DimFacilityDao dao

    def "it should return >90 facilities when all injectors are queried " () {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(
                dataSource: 'I'
                ,reportingYear: 2014
                ,lowE: -20000
                ,injectionSelection : 11
        )

        when:
        def result = dao.loadDimFacilities(request, FacilityViewType.MAP)

        then:
        result != null
        result.size() > 90


    }

    def "it should return >90 facilities when all injectors are queried: LIST VIEW " () {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(
                dataSource: 'I'
                ,reportingYear: 2014
                ,lowE: -20000
                ,injectionSelection : 11
        )

        when:
        def result = dao.loadDimFacilities(request, FacilityViewType.LIST)

        then:
        result != null
        result.size() > 90


    }

    def "it should return 3 facilities when R&D is present in query"() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'I'
                ,reportingYear: 2014
                ,lowE : -20000
                ,injectionSelection : 12
                )

        when:
        def result = dao.loadDimFacilities(request, FacilityViewType.MAP)

        then:
        result != null
        result.size() == 3

    }

    def "it should return 3 facilities when R&D is present in query : LIST"() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(dataSource: 'I'
                ,reportingYear: 2014
                ,lowE : -20000
                ,injectionSelection : 12
        )

        when:
        def result = dao.loadDimFacilities(request, FacilityViewType.LIST)

        then:
        result != null
        result.size() == 3

    }







}
