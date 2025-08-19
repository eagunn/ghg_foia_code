package infrastructure.factories

import gov.epa.ghg.dto.GasFilter
import gov.epa.ghg.dto.NewSectorFilter
import gov.epa.ghg.dto.QueryOptions
import gov.epa.ghg.dto.SectorFilter
import gov.epa.ghg.presentation.request.FlightRequest

/**
 * Created by alabdullahwi on 8/18/2015.
 */
class TestObjectsFactory {

        /**
         * default behavior for SectorFilter make methods is all true
         * @return
     */
    static SectorFilter makeSectorFilter(Map params) {
        SectorFilter retv
        Byte yes = 1;
        if (params.onshore) {
            retv = new SectorFilter()
            retv.powerPlants = false
            retv.waste = false
            retv.metals = false
            retv.minerals = false
            retv.refineries = false
            retv.pulpAndPaper = false
            retv.chemicals = false
            retv.other = false
            retv.s901 = false
            retv.s902 = true
            retv.s903 = false
            retv.s904 = false
            retv.s905 = false
            retv.s906 = false
            retv.s907 = false
            retv.s908 = false
            retv.s909 = false
        }
        else {
           retv = new SectorFilter(yes,yes,yes,yes,yes,yes,yes,yes,yes)
        }

        retv

    }


    static NewSectorFilter makeNewSectorFilter(Map params) {

        NewSectorFilter retv

        if (params.onshore) {

            retv =new NewSectorFilter()
            retv.powerPlants = false
            retv.waste = false
            retv.metals = false
            retv.minerals = false
            retv.refineries = false
            retv.pulpAndPaper = false
            retv.chemicals = false
            retv.other = false
            retv.s901 = false
            retv.s902 = true
            retv.s903 =  false
            retv.s904 = false
            retv.s905 = false
            retv.s906 = false
            retv.s907 = false
            retv.s908 = false
            retv.s909 = false

        }

        else {

            def sectors = new Boolean[9][];
            sectors[0] = [ true ] as Boolean[]
            sectors[1] = [ true,true,true,true,true ] as Boolean[]
            sectors[2] = [ true,true,true,true,true,true, true, true ] as Boolean[]
            sectors[3] = [ true,true,true,true,true,true ] as Boolean[]
            sectors[4] = [ true ] as Boolean[]
            sectors[5] = [ true,true,true ] as Boolean[]
            sectors[6] = [ true,true,true,true,true,true,true,true,true,true,true,true ] as Boolean[]
            sectors[7] = [ true,true,true,true,true,true,true,true,true,true,true ] as Boolean[]
            sectors[8] = [ true,true,true,true,true,true,true,true,true,true ] as Boolean[]

            retv = new NewSectorFilter(sectors)
        }
        retv





    }

    static FlightRequest makeFlightRequest(Map... params) {

        FlightRequest retv = new FlightRequest()

        retv.query = ''
        retv.reportingYear = 2013
        retv.state = ''
        retv.countyFips = null
        retv.msaCode = null
        retv.lowE = 0
        retv.highE = 23000000
        retv.pageNumber = 0

        def gases = new GasFilter()
        gases.co2 = true
        gases.ch4 = true
        gases.n2o = true
        gases.sf6 = true
        gases.nf3 = true
        gases.hfc23 = false
        gases.hfc = true
        gases.pfc = true
        gases.hfe = true
        gases.other = true
        gases.veryShortCompounds = true
        gases.otherFlourinated = true

        retv.gases = gases.toBooleanArray();

        if (params.size() != 0 && params[0].onshore) {

            def sectors = new Boolean[9][];
            sectors[0] = [false] as Boolean[]
            sectors[1] = [false, false, false, false, false] as Boolean[]
            sectors[2] = [false, false, false, false, false, false, false, false] as Boolean[]
            sectors[3] = [false, false, false, false , false, false] as Boolean[]
            sectors[4] = [false] as Boolean[]
            sectors[5] = [false, false, false] as Boolean[]
            sectors[6] = [false, false, false, false, false, false, false, false, false, false, false, false] as Boolean[]
            sectors[7] = [false, false, false, false, false, false, false, false, false, false, false] as Boolean[]
            sectors[8] = [false, true, false, false, false, false, false, false, false, false] as Boolean[]

            retv.sectors = sectors

        }
        else {

            def sectors = new Boolean[9][];
            sectors[0] = [true] as Boolean[]
            sectors[1] = [true, true, true, true, true] as Boolean[]
            sectors[2] = [true, true, true, true, true, true, true, true] as Boolean[]
            sectors[3] = [true, true, true, true, true, true] as Boolean[]
            sectors[4] = [true] as Boolean[]
            sectors[5] = [true, true, true] as Boolean[]
            sectors[6] = [true, true, true, true, true, true, true, true, true, true, true, true] as Boolean[]
            sectors[7] = [true, true, true, true, true, true, true, true, true, true, true] as Boolean[]
            sectors[8] = [true, true, true, true, true, true, true, true, true, true] as Boolean[]

            retv.sectors = sectors
        }


        def qo = new QueryOptions()
        qo.nameSelected = true
        qo.citySelected = true
        qo.countySelected = false
        qo.stateSelected = false
        qo.zipSelected = true
        qo.idSelected = false
        qo.naicsSelected = false
        qo.parentSelected = false

        retv.queryOptions = qo;
        retv.sortOrder = 0
        retv.dataSource = 'E'
        retv.reportingStatus = 'ALL'
        retv.emissionsType = ''
        retv.tribalLandId = null
        retv.supplierSector = 0
        retv.injectionSelection = 11
        retv.basin = null

        unloadMap(retv, params)
        retv
    }

    protected static unloadMap(target, Map... params){

        //if empty varargs, nothing to do
        if (params.length == 0 ) {
            return
        }
        //else get the first element (which is the params map)
        params[0].each {
            target."${it.key}" = it.value
        }
    }
}
