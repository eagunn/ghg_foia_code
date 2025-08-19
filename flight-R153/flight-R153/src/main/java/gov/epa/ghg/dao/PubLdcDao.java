package gov.epa.ghg.dao;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.PubLdcFacility;
import gov.epa.ghg.presentation.request.FlightRequest;

/**
 * Created by alabdullahwi on 5/22/2015.
 */
@Repository
@Transactional
public class PubLdcDao extends AbstractFlightDao {
	
	private static final long serialVersionUID = 1L;
	
	public List<PubLdcFacility> get(FlightRequest request, boolean isLimited) {
		
		String queryString = request.generateLdcQuery();
		Query query = createQuery(queryString, isLimited, request.getPageNumber());
		return query.list();
	}
	
	public List<PubLdcFacility> get(FlightRequest request) {
		return get(request, false);
	}
	
}
