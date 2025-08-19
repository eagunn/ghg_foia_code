package gov.epa.ghg.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacility;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/gov/epa/ghg/dao/applicationContext.xml"})

public class DaoTest extends BaseDaoTest {

	@Inject
	private SessionFactory sessionFactory;
	
	@Test
	@Transactional
	public void testFindFacilityIdAndReportingYearWithResult() {
		
		final String hQuery = "from DimFacility f where f.tribalLand is not null";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery);
		query.setCacheable(true);
		List<DimFacility> fl = (List<DimFacility>)query.list();
		
		assertTrue(fl.size()>0);
	}
}
