package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimStateGeo;

/**
 * A data access object (DAO) providing persistence and search support for
 * PubDimState entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 *
 * @author MyEclipse Persistence Tools
 * @see gov.epa.ghg.domain.DimState
 */

@Repository
@Transactional
public class DimStateGeoDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public List<DimStateGeo> getStates() {
		final String hQuery = "from DimStateGeo s where s.sortOrder < 60 order by s.sortOrder";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		return query.list();
	}
	
	public DimStateGeo getStateByStateAbbr(String stateAbbr) {
		final String hQuery = "from DimStateGeo s where s.state = :stateAbbr";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("stateAbbr", stateAbbr);
		query.setCacheable(true);
		List<DimStateGeo> ls = query.list();
		if (ls.size() > 0) {
			return ls.get(0);
		} else {
			return null;
		}
	}
}
