package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * DimSector entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 *
 * @author MyEclipse Persistence Tools
 * @see gov.epa.ghg.domain.DimSector
 */

@Repository
@Transactional
public class DimSubSectorDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public List<String> getEmitterSubSectors() {
		final String hQuery = "select ss.subSectorDescription from DimSubSector ss where ss.sector.sectorType = 'E' order by ss.subSectorId";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		return query.list();
	}
	
	public List<String> getSubSectorsByType(String type) {
		final String hQuery = "select ss.subSectorDescription from DimSubSector ss where ss.sector.sectorType = :type order by ss.subSectorId";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("type", type);
		query.setCacheable(true);
		return query.list();
	}
}
