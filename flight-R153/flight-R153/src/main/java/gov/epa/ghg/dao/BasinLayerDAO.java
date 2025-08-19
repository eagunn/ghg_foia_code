package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.dto.Basin;

@Repository
@Transactional
public class BasinLayerDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public List<BasinLayer> getBasinLayers() {
		// String s = "from BasinLayer b where basinCode = '140' or basinCode = '150' or basinCode = '130' or basinCode = '120'";
		// String s = "from BasinLayer b where basinCode = '140'";
		String s = "from BasinLayer b where b.basinCode in (select distinct bf.id.basinCode from PubBasinFacility bf)";
		// String s = "from LdcLayer l where id = '20149'";
		// String s = "from LdcLayer l where id = '28551'";
		// String s = "from LdcLayer l where id = '13692'";
		final String hQuery = s;
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		return query.list();
	}
	
	public List<Basin> getBasins() {
		String s = "from BasinLayer b where b.basinCode in (select distinct bf.id.basinCode from PubBasinFacility bf) order by basin asc";
		// String s = "from LdcLayer l where id = '20149'";
		// String s = "from LdcLayer l where id = '28551'";
		// String s = "from LdcLayer l where id = '13692'";
		final String hQuery = s;
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		List<BasinLayer> bl = query.list();
		List<Basin> lb = new ArrayList<Basin>();
		for (BasinLayer b : bl) {
			lb.add(new Basin(b.getBasinCode(), b.getBasin()));
		}
		return lb;
	}
	
	public BasinLayer getBasinByCode(String basinCode) {
		final String hQuery = "from BasinLayer b where b.basinCode = :basinCode";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("basinCode", basinCode);
		query.setCacheable(true);
		List<BasinLayer> lb = query.list();
		if (lb.size() > 0) {
			return lb.get(0);
		} else {
			return null;
		}
	}
}
