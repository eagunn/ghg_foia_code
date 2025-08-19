package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.util.DaoUtils;

@Repository
@Transactional
public class PubFactsAggregatedEmissionDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "startYear")
	private int startYear;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public boolean supplierHasTrend(int sc) {
		final String hquery = "from PubFactsAggregatedEmission e "+
		"join e.sector s "+
		"join e.subSector ss "+ 
		"where s.sectorType = 'S' "+
		DaoUtils.supplierAggregatedWhereClause(sc);
		
		Query query = sessionFactory.getCurrentSession().createQuery(hquery);
		query.setCacheable(true);
		List results = query.list();
		
		if (results.size() > 0)
			return true;
		else
			return false;
	}
	
	public List getSuppliersSectorYearlyTrend(int sc) {
		
		final String hQuery = "select s.sectorName, e.id.year, e.co2eEmission, e.notes "+
		"from PubFactsAggregatedEmission e "+
		"join e.sector s "+
		"join e.subSector ss " +
		"where ss.subSectorId in (select distinct ss.subSectorId " +
		"from PubFactsAggregatedEmission e "+
		"join e.sector s "+
		"join e.subSector ss "+
		"where s.sectorType = 'S' "+
		"and e.id.year >= :startYear "+
		DaoUtils.supplierAggregatedWhereClause(sc)+ ") "+
		DaoUtils.supplierAggregatedWhereClause(sc)+
		"and s.sectorType = 'S'";	
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("startYear", (long) startYear);
		query.setCacheable(true);
		return query.list();
	}
	
	public boolean co2InjectionHasTrend(int sc) {
		final String hquery = "from PubFactsAggregatedEmission e "+
	    "join e.sector s " +
		"join e.subSector ss "+
	    "where s.sectorType = 'I' "+
		DaoUtils.co2InjectionAggregateWhereClause(sc);
		
		Query query = sessionFactory.getCurrentSession().createQuery(hquery);
		query.setCacheable(true);
		List results = query.list();
		
		if (results.size() > 0)
			return true;
		else
			return false;
	}
	
	public List getCO2InjectionSectorYearlyTrend(int sc) {
		
		final String hQuery = "select s.sectorName, e.id.year, sum(e.co2eEmission), e.notes "+
		"from PubFactsAggregatedEmission e "+
		"join e.sector s "+
		"join e.subSector ss "+
		"where ss.subSectorId in (select distinct ss.subSectorId " +
		"from PubFactsAggregatedEmission e "+
		"join e.sector s "+
		"join e.subSector ss "+
		"where s.sectorType = 'I' "+
		"and e.id.year >= :startYear "+
		DaoUtils.co2InjectionAggregateWhereClause(sc)+ ")" +
		DaoUtils.co2InjectionAggregateWhereClause(sc)+
		"and s.sectorType = 'I' group by s.sectorName, e.id.year";	
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("startYear", (long) startYear);
		query.setCacheable(true);
		List<Object[]> results = query.list();
		
		return results;
	}
	
}
