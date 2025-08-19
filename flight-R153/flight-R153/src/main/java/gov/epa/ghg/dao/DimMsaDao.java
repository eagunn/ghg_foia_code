package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimMsa;

@Repository
@Transactional
public class DimMsaDao implements Serializable, BaseDao<DimMsa, String> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Override
	public void save(DimMsa instance) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void delete(DimMsa instance) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DimMsa merge(DimMsa instance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DimMsa> find(int start, int max, Map<String, Serializable> propertyNameValuePair) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DimMsa> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public DimMsa getMsaByCode(String msaCode) {
		final String hQuery = "from DimMsa where cbsafp = :msaCode";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("msaCode", msaCode);
		query.setCacheable(true);
		List<DimMsa> msa = query.list();
		if (msa.size() > 0) {
			return msa.get(0);
		} else {
			return null;
		}
	}
	
}
