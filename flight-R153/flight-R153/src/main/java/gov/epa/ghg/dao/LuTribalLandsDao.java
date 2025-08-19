package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.LuTribalLands;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
@Transactional
public class LuTribalLandsDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public LuTribalLands findById(Long tribalLandId) {
		try {
			LuTribalLands tribalLand = (LuTribalLands) sessionFactory.getCurrentSession().get("gov.epa.ghg.domain.LuTribalLands", tribalLandId);
			return tribalLand;
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public int getIdByName(String tribalLand) {
		try {
			// Some tribal land's names get cut off for some reason, so used the like clause instead of =
			final String hQuery = "select tribalLandId from LuTribalLands where tribalLandName like '%" + ":tribalLand" + "%'";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("tribalLand", tribalLand);
			List<Long> results = query.list();
			int id = 0;
			if (results != null) {
				Long tribalLandId = results.get(0);
				id = tribalLandId.intValue();
			}
			return id;
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public List<LuTribalLands> getTribalLands() {
		try {
			log.debug("finding all tribal lands");
			final String hQuery = "from LuTribalLands order by tribalLandName";
			Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
			return query.list();
		} catch (RuntimeException re) {
			log.error("finding all tribal lands failed", re);
			throw re;
		}
	}
}
