package jira

import gov.epa.ghg.dao.FacilityViewDAO
import gov.epa.ghg.dto.GasFilter
import gov.epa.ghg.dto.NewSectorFilter
import gov.epa.ghg.dto.QueryOptions
import gov.epa.ghg.dto.SectorFilter
import infrastructure.config.FlightDaoTest
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by alabdullahwi on 8/7/2015.
 *
 * JIRA:            PUB-578
 * HYPERLINK:       10.170.0.249:10080/jira/browse/PUB-578
 * TITLE:           Export Data Isn't Working Properly When a state has been selected
 * DESCRIPTION:     If I filter on a state and then click 'Export Data' button, the spreadsheet that opens doesn't include all the facilities it should
 *                  for example, New York has 225 facilities in 2013 but the export data only shows 9 facilities.
 *
 */
class ExportDataTests extends FlightDaoTest {


    @Autowired
    FacilityViewDAO facilityViewDAO


    def "facilities loaded for the export service should be the same as any other"() {


    }




}
