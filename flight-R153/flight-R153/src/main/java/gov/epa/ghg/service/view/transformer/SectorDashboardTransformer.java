package gov.epa.ghg.service.view.transformer;

import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.AppConstants;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alabdullahwi on 9/13/2015.
 */
@Service
public class SectorDashboardTransformer {


    public JSONObject transform(SectorAggregate sa, SectorFilter sectors, int numFacilities) {

        List<BigDecimal> sectorEmissions = new ArrayList<BigDecimal>();
        ServiceUtils.addToUnitList(sectorEmissions, sa.getPowerplantEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getLandfillEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getMetalEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getMineralEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getRefineryEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getPulpAndPaperEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getChemicalEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getOtherEmission());
        ServiceUtils.addToUnitList(sectorEmissions, sa.getPetroleumAndNaturalGasEmission());

        String unit = ServiceUtils.getUnit(sectorEmissions);

        JSONObject jsonParent = new JSONObject();
        jsonParent.put("unit", unit);

        if(sectors.isPowerPlants()) {
            jsonParent.accumulate("values", aggregateSector(sa.getPowerplantEmission(), unit)); // 0
            jsonParent.accumulate("values", sa.getPowerplantCount().intValue()); // 1
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isWaste()) {
            jsonParent.accumulate("values", aggregateSector(sa.getLandfillEmission(), unit)); // 2
            jsonParent.accumulate("values", sa.getLandfillCount().intValue()); // 3
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isMetals()) {
            jsonParent.accumulate("values", aggregateSector(sa.getMetalEmission(), unit)); // 4
            jsonParent.accumulate("values", sa.getMetalCount().intValue()); // 5
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isMinerals()) {
            jsonParent.accumulate("values", aggregateSector(sa.getMineralEmission(), unit)); // 6
            jsonParent.accumulate("values", sa.getMineralCount().intValue()); // 7
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isRefineries()) {
            jsonParent.accumulate("values", aggregateSector(sa.getRefineryEmission(), unit)); // 8
            jsonParent.accumulate("values", sa.getRefineryCount().intValue()); // 9
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isPulpAndPaper()) {
            jsonParent.accumulate("values", aggregateSector(sa.getPulpAndPaperEmission(), unit)); // 10
            jsonParent.accumulate("values", sa.getPulpAndPaperCount().intValue()); // 11
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isChemicals()) {
            jsonParent.accumulate("values", aggregateSector(sa.getChemicalEmission(), unit)); // 12
            jsonParent.accumulate("values", sa.getChemicalCount().intValue()); // 13
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isOther()) {
            jsonParent.accumulate("values", aggregateSector(sa.getOtherEmission(), unit)); // 14
            jsonParent.accumulate("values", sa.getOtherCount().intValue()); // 15
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        if(sectors.isPetroleumAndNaturalGas()) {
            jsonParent.accumulate("values", aggregateSector(sa.getPetroleumAndNaturalGasEmission(), unit)); // 16
            jsonParent.accumulate("values", sa.getPetroleumAndNaturalGasCount().intValue()); // 17
        } else {
            jsonParent.accumulate("values", 0);
            jsonParent.accumulate("values", 0);
        }
        jsonParent.accumulate("values", 0); // 18
        jsonParent.accumulate("values", numFacilities); // 19
        jsonParent.accumulate("values", aggregateSector(sa.sumAllSectors(), unit));
        return jsonParent;

    }

    private Object aggregateSector(BigDecimal value, String unit) {
        if (AppConstants.MT.equals(unit) || ServiceUtils.convert(value, unit) >= 10) {
            return ServiceUtils.convert(value, unit);
        }
        return value.divide(ServiceUtils.mmtFactor).setScale(1, RoundingMode.HALF_UP);
    }

}
