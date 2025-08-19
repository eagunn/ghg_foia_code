package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimCounty;
import lombok.extern.log4j.Log4j2;

/**
 * A data access object (DAO) providing persistence and search support for
 * PubDimCounty entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 *
 * @author MyEclipse Persistence Tools
 * @see gov.epa.ghg.domain.DimCounty
 */

@Log4j2
@Repository
@Transactional
public class DimCountyDao implements Serializable, BaseDao<DimCounty, String> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	// property constants
	public static final String COUNTY_NAME = "countyName";
	public static final String STATE = "state";
	public static final String STATE_NAME = "stateName";
	
	public void save(DimCounty transientInstance) {
		log.debug("saving DimCounty instance");
		try {
			sessionFactory.getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	
	public void delete(DimCounty persistentInstance) {
		log.debug("deleting DimCounty instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public DimCounty findById(java.lang.String id) {
		log.debug("getting DimCounty instance with id: " + id);
		try {
			DimCounty instance = (DimCounty) sessionFactory.getCurrentSession().get("gov.epa.ghg.domain.DimCounty", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<DimCounty> findAll() {
		log.debug("finding all DimCounty instances");
		try {
			String queryString = "from DimCounty";
			Query query = sessionFactory.getCurrentSession().createQuery(queryString);
			query.setCacheable(true);
			return query.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public DimCounty merge(DimCounty detachedInstance) {
		log.debug("merging DimCounty instance");
		try {
			DimCounty result = (DimCounty) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}
	
	public void attachDirty(DimCounty instance) {
		log.debug("attaching dirty DimCounty instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public void attachClean(DimCounty instance) {
		log.debug("attaching clean DimCounty instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public List<DimCounty> find(int start, int max, Map<String, Serializable> propertyNameValuePair) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		try {
			Criteria criteria = (Criteria) cb.createQuery(DimCounty.class);
			Criterion criterion = Restrictions.allEq(propertyNameValuePair);
			criteria.add(criterion);
			criteria.setFirstResult(start);
			criteria.setMaxResults(max);
			return criteria.list();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public List<DimCounty> getCounties() {
		final String hQuery = "from DimCounty c";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		return query.list();
	}
	
	public List<DimCounty> getCountiesByState(String state) {
		final String hQuery = "from DimCounty c where c.state= :state";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("state", state);
		query.setCacheable(true);
		return query.list();
	}
	
	public DimCounty getCountyByFips(String fipsCode) {
		final String hQuery = "from DimCounty c where c.countyFips = :fipsCode";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("fipsCode", fipsCode);
		query.setCacheable(true);
		List<DimCounty> lc = query.list();
		if (lc.size() > 0) {
			return lc.get(0);
		} else {
			return null;
		}
	}
	
	public int getFips(String state, String county) {
		final String hQuery = "from DimCounty where state= :state and countyName= :county";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("state", state).setParameter("county", county);
		query.setCacheable(true);
		List<DimCounty> result = query.list();
		if (result.size() > 0) {
			return Integer.parseInt(result.get(0).getCountyFips());
		} else {
			return 0;
		}
	}
}
