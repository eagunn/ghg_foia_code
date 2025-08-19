package service

import gov.epa.ghg.domain.DimFacility
import gov.epa.ghg.domain.DimFacilityId
import gov.epa.ghg.enums.FacilityType
import gov.epa.ghg.service.view.transformer.FacilityListTransformer
import spock.lang.Specification

/**
 * Created by alabdullahwi on 9/23/2015.
 */
class FacilityListTransformerTests extends Specification {

    FacilityListTransformer transformer = new FacilityListTransformer()


    def "it should be able to sort FacilityView list  in ascending order "() {

        given:
        int sortOrder = 0
        def name1 = '123'
        def name2 = 'Ahmed'
        def name3 = 'Matthew'
        DimFacility one = new DimFacility()
        DimFacilityId id = new DimFacilityId()
        id.facilityId = 999L
        one.facilityName =  name1
        one.id = id
        DimFacility two = new DimFacility()
        DimFacilityId id2 = new DimFacilityId()
        id2.facilityId = 998L
        two.facilityName = name2
        two.id = id2
        DimFacility three = new DimFacility()
        DimFacilityId id3 = new DimFacilityId()
        id3.facilityId = 9989L
        three.facilityName = name3
        three.id = id3

        when:
        def list = [three, one, two]

        then:
        list[0] == three
        list[1] == one
        list[2] == two


        when:
        def sortedList = transformer.transform(list,[:], 2015, FacilityType.EMITTERS , sortOrder)

        then:
        sortedList.facilities[0].facilityName == name1
        sortedList.facilities[1].facilityName == name2
        sortedList.facilities[2].facilityName == name3


    }


    def "it should sort lists in descending order as well " () {

        given:
        int sortOrder = 1
        def name1 = '123'
        def name2 = 'Ahmed'
        def name3 = 'Matthew'
        DimFacility one = new DimFacility()
        DimFacilityId id = new DimFacilityId()
        id.facilityId = 999L
        one.facilityName =  name1
        one.id = id
        DimFacility two = new DimFacility()
        DimFacilityId id2 = new DimFacilityId()
        id2.facilityId = 998L
        two.facilityName = name2
        two.id = id2
        DimFacility three = new DimFacility()
        DimFacilityId id3 = new DimFacilityId()
        id3.facilityId = 9989L
        three.facilityName = name3
        three.id = id3

        when:
        def list = [three, one, two]

        then:
        list[0] == three
        list[1] == one
        list[2] == two


        when:
        def sortedList = transformer.transform(list,[:], 2015, FacilityType.EMITTERS , sortOrder)

        then:
        sortedList.facilities[0].facilityName == name3
        sortedList.facilities[1].facilityName == name2
        sortedList.facilities[2].facilityName == name1


    }
}
