package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.PubBasinFacility;

/**
 * Created by lee@saic October 2016.
 * Miscellaneous DAO for exporting List by Changes
 */

@Repository
@Transactional
public class TrendListDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public Map<Long, DimFacility> getFacilityByYear(int year) {
		final String hQuery = "from DimFacility f where f.id.year = :year";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		List<DimFacility> resultList = query.list();
		Map<Long, DimFacility> dfMap = new HashMap<Long, DimFacility>();
		for (DimFacility df : resultList) {
			dfMap.put(df.getId().getFacilityId(), df);
		}
		
		return dfMap;
	}
	
	public Map<Long, PubBasinFacility> getBasinFacilityByYear(int year) {
		final String hQuery = "from PubBasinFacility bf where bf.id.year = :year";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year);
		query.setCacheable(true);
		List<PubBasinFacility> resultList = query.list();
		Map<Long, PubBasinFacility> basinMap = new HashMap<Long, PubBasinFacility>();
		for (PubBasinFacility bf : resultList) {
			basinMap.put(bf.getId().getFacilityId(), bf);
		}
		
		return basinMap;
	}
	
}
