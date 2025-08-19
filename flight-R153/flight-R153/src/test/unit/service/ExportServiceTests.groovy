package service

import gov.epa.ghg.dto.GasFilter
import gov.epa.ghg.presentation.request.FlightRequest
import gov.epa.ghg.service.ExportService
import infrastructure.factories.TestObjectsFactory
import spock.lang.Specification

/**
 * Created by alabdullahwi on 3/10/2016.
 */
class ExportServiceParameterTests extends Specification {

    ExportService exportService = new ExportService()


    def "Reporting Status should not be included if query is ALL"() {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(reportingStatus: 'ALL')

        when:
        String paramString = exportService.createSearchParameters(request)

        then:
        !paramString.contains("Reporting Status")

    }

    def "Reporting Status should be included if it's anything else"(String query, String expected ) {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest(reportingStatus: query)

        when:
        String paramString = exportService.createSearchParameters(request)

        then:
        paramString.contains("Reporting Status="+expected)

        where:
        query     |         expected
        'RED'     | 'Stopped Reporting - Unknown Reason'
        'GRAY'    | 'Stopped Reporting - Valid Reason'
        'ORANGE'  | 'Potential Data Quality Issue'

    }

    def "it should  say GHGs=ALL if all GHGs are in the query" () {

        given:
        FlightRequest request = TestObjectsFactory.makeFlightRequest()

        when:
        String paramString = exportService.createSearchParameters(request)

        then:
        paramString.contains("GHGs=ALL")

    }

    def "it should specify the GHGs included in the query if not all are selected"() {

        given:
        GasFilter gasQuery = new GasFilter()
        gasQuery.ch4 = true
        gasQuery.co2 = true
        gasQuery.otherFlourinated = true
        FlightRequest request = TestObjectsFactory.makeFlightRequest(gases: gasQuery.toBooleanArray())

        when:
        String paramString = exportService.createSearchParameters(request)

        then:
        paramString.contains("GHGs=CO2,CH4,OTHER FLOURINATED")
    }

    def "it should not include any GHGs if none are included"() {

        given:
        GasFilter gasQuery = new GasFilter()
        gasQuery.co2 = false
        gasQuery.ch4 = false
        gasQuery.n2o = false
        gasQuery.sf6 = false
        gasQuery.otherFlourinated = false
        gasQuery.other = false
        gasQuery.veryShortCompounds = false
        gasQuery.hfc = false
        gasQuery.hfe = false
        gasQuery.nf3 = false
        gasQuery.pfc = false
        FlightRequest request = TestObjectsFactory.makeFlightRequest(gases: gasQuery.toBooleanArray())

        when:
        String paramString = exportService.createSearchParameters(request)

        then:
        paramString.contains("GHGs=NONE")
    }

}
