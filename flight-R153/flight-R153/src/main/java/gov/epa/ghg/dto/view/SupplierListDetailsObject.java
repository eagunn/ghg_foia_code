package gov.epa.ghg.dto.view;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.enums.ReportingStatus;

import java.math.BigDecimal;

/**
 * Created by alabdullahwi on 2/9/2016.
 */
public interface SupplierListDetailsObject {

   void populate(int year, BigDecimal suppliedQuantity, DimFacility facility, ReportingStatus rs);
   DimFacility getFacility();
   ReportingStatus getReportingStatus();
}
