package parameters

import gov.epa.ghg.dto.NewSectorFilter
import gov.epa.ghg.dto.SectorFilter
import infrastructure.factories.TestObjectsFactory
import spock.lang.Specification

/**
 * Created by alabdullahwi on 8/19/2015.
 */
class NewSectorFilterTests extends Specification {


    def "it should produce identical results to old SectorFilter when callig isOnshoreOnly  "() {

        when:
        SectorFilter legacy = TestObjectsFactory.makeSectorFilter([:])
        NewSectorFilter modern = TestObjectsFactory.makeNewSectorFilter([:])

        then:
        legacy.isOnshorePetroleumSectorOnly() == modern.isOnshorePetroleumSectorOnly()
        legacy.isLDCSectorOnly() == modern.isLDCSectorOnly()

    }

    def "modern/legacy test: trigger onshore "() {

        when:
        SectorFilter legacy = TestObjectsFactory.makeSectorFilter(onshore: true)
        NewSectorFilter modern = TestObjectsFactory.makeNewSectorFilter(onshore: true)

        then:
        legacy.isOnshorePetroleumSectorOnly() == modern.isOnshorePetroleumSectorOnly()
        legacy.isOnshorePetroleumSectorOnly() == true

        when:
        legacy.s905 = true
        legacy.s902 = false
        modern.s905 = true
        modern.s902 = false

        then:
        legacy.isOnshorePetroleumSectorOnly() == modern.isOnshorePetroleumSectorOnly()
        legacy.isLDCSectorOnly() == true

    }

}
