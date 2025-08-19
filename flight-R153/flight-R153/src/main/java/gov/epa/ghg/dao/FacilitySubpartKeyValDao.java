package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.FacilitySubpartKeyVal;
import gov.epa.ghg.domain.LuKey;

/**
 * Created by alabdullahwi on 8/11/2015.
 */
@Repository
@Transactional
public class FacilitySubpartKeyValDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public List<FacilitySubpartKeyVal> getByIdAndYear(Long id, int year) {
		StringBuffer sb = new StringBuffer();
		sb.append("from FacilitySubpartKeyVal as keyVal ");
		sb.append("inner join fetch keyVal.facility ");
		sb.append("inner join fetch keyVal.subpart ");
		sb.append("inner join fetch keyVal.luKey ");
		// sb.append("where keyVal.facility.id.facilityId = "+id+" and keyVal.facility.id.year = "+year+" and keyVal.id.year = "+year+" and keyVal.subpart.subpartType = '"+ds+"' ");
		sb.append("where keyVal.facility.id.facilityId = :id and keyVal.facility.id.year = :year and keyVal.id.year = :year ");
		sb.append("order by keyVal.subpart.subpartName, substr(keyVal.notes,0,1), case when keyVal.notes like '%i' then 1 else 2 end, keyVal.luKey.keyId");
		final String hQuery = sb.toString();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year);
		query.setCacheable(true);
		List<FacilitySubpartKeyVal> results = query.list();
		return results;
	}
	
	public List<FacilitySubpartKeyVal> getById(Long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("from FacilitySubpartKeyVal as keyVal ");
		sb.append("inner join fetch keyVal.facility ");
		sb.append("inner join fetch keyVal.subpart ");
		sb.append("inner join fetch keyVal.luKey ");
		sb.append("where keyVal.facility.id.facilityId = :id ");
		sb.append("order by keyVal.id.year DESC");
		final String hQuery = sb.toString();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id);
		query.setCacheable(true);
		return query.list();
	}
	
	public List<LuKey> getLuKeybyName(Long id, int year, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append("from FacilitySubpartKeyVal as keyVal ");
		sb.append("inner join fetch keyVal.facility ");
		sb.append("inner join fetch keyVal.subpart ");
		sb.append("inner join fetch keyVal.luKey ");
		sb.append("where keyVal.facility.id.facilityId = :id and keyVal.facility.id.year = :year and keyVal.id.year = :year and keyVal.luKey.keyName = :name ");
		sb.append("order by keyVal.subpart.subpartName, substr(keyVal.notes,0,1), case when keyVal.notes like '%i' then 1 else 2 end, keyVal.luKey.keyId");
		final String hQuery = sb.toString();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("name", name);
		query.setCacheable(true);
		List<LuKey> results = query.list();
		return results;
		
	}
	
	public List getKeyNotes(Long id, String note) {
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct keyVal.notes ");
		sb.append("from FacilitySubpartKeyVal as keyVal ");
		sb.append("where keyVal.facility.id.facilityId = :id and keyVal.notes like :note");
		final String hQuery = sb.toString();
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("note", note + "%");
		query.setCacheable(true);
		return query.list();
	}
}
