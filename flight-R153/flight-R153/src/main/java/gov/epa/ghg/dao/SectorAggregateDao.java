package gov.epa.ghg.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dto.SectorAggregate;
import gov.epa.ghg.presentation.request.FlightRequest;
import gov.epa.ghg.util.DaoUtils;

/**
 * Created by alabdullahwi on 9/14/2015.
 */
@Service
@Transactional
public class SectorAggregateDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public SectorAggregate find(FlightRequest request) {
		
		String originalStatus = request.getReportingStatus();
		if ("ALL".equals(request.getReportingStatus())) {
			request.setReportingStatus("STILL_REPORTING");
		}
		
		String mainQuery = "";
		if (request.isPipe()) {
			mainQuery = request.generatePipeDashboardQuery();
		} else {
			mainQuery = request.generateSectorDashboardQuery();
		}
		Query query = sessionFactory.getCurrentSession().createQuery(mainQuery);
		query.setCacheable(true);
		List<Object[]> results = query.list();
        
        /*if (request.isWholePetroNg()) {
        	String pipeQuery = request.generatePipeDashboardQuery();
        	Query qryPipe = sessionFactory.getCurrentSession().createQuery(pipeQuery);
        	qryPipe.setCacheable(true);
        	List<Object[]> pipeResults = qryPipe.list();
        	results = this.joinDashboardResult(results, pipeResults);
        }*/
		
		request.setReportingStatus(originalStatus);
		return createSectorAggregate(results);
	}
	
	private SectorAggregate createSectorAggregate(List<Object[]> results) {
		
		SectorAggregate sectorAggregate = new SectorAggregate();
		
		for (Object[] result : results) {
			if (result[0].equals(DaoUtils.POWERPLANTS)) {
				sectorAggregate.setPowerplantEmission((BigDecimal) result[1]);
				sectorAggregate.setPowerplantCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.WASTE)) {
				sectorAggregate.setLandfillEmission((BigDecimal) result[1]);
				sectorAggregate.setLandfillCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.METALS)) {
				sectorAggregate.setMetalEmission((BigDecimal) result[1]);
				sectorAggregate.setMetalCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.MINERALS)) {
				sectorAggregate.setMineralEmission((BigDecimal) result[1]);
				sectorAggregate.setMineralCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.REFINERIES)) {
				sectorAggregate.setRefineryEmission((BigDecimal) result[1]);
				sectorAggregate.setRefineryCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.PULPANDPAPER)) {
				sectorAggregate.setPulpAndPaperEmission((BigDecimal) result[1]);
				sectorAggregate.setPulpAndPaperCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.CHEMICALS)) {
				sectorAggregate.setChemicalEmission((BigDecimal) result[1]);
				sectorAggregate.setChemicalCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.OTHER)) {
				sectorAggregate.setOtherEmission((BigDecimal) result[1]);
				sectorAggregate.setOtherCount((Long) result[2]);
			} else if (result[0].equals(DaoUtils.PETRO_NG)) {
				sectorAggregate.setPetroleumAndNaturalGasEmission((BigDecimal) result[1]);
				sectorAggregate.setPetroleumAndNaturalGasCount((Long) result[2]);
			}
		}
		return sectorAggregate;
	}
	
	public List<Object[]> joinDashboardResult(List<Object[]> results1, List<Object[]> results2) {
		
		if (results1.size() > 0 && results2.size() > 0) {
			for (Object[] res1 : results1) {
				String sector1 = (String) res1[0];
				for (Object[] res2 : results2) {
					String sector2 = (String) res2[0];
					if (sector1.equals(sector2)) {
						res1[0] = (String) res1[0];
						BigDecimal em1 = (BigDecimal) res1[1];
						BigDecimal em2 = (BigDecimal) res2[1];
						BigDecimal totalEmission = em1.add(em2);
						res1[1] = totalEmission;
						
						res1[2] = (Long) res1[2] + (Long) res2[2];
					}
				}
			}
		}
		
		return results1;
	}
	
}
