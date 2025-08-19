package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacilityStatus;
import gov.epa.ghg.enums.ReportingStatus;

@Repository
@Transactional
public class DimFacilityStatusDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SessionFactory sessionFactory;
	
	public Map<Long, ReportingStatus> findByIdYearType(Long id, Long year, String type) {
		final String hQuery = "from DimFacilityStatus fs where fs.id.facilityId = :id and fs.id.year = :year and fs.facilityType = :type";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("id", id).setParameter("year", (long) year).setParameter("type", type);
		query.setCacheable(true);
		List<DimFacilityStatus> resultList = query.list();
		Map<Long, ReportingStatus> rsMap = new HashMap<Long, ReportingStatus>();
		for (DimFacilityStatus rs : resultList) {
			rsMap.put(rs.getId().getFacilityId(), rs.getReportingStatus());
		}
		return rsMap;
	}
	
	public Map<Long, ReportingStatus> getAllFacilityByYearType(int year, String type) {
		final String hQuery = "from DimFacilityStatus fs where fs.id.year = :year and fs.facilityType = :type";
		Query query = sessionFactory.getCurrentSession().createQuery(hQuery).setParameter("year", (long) year).setParameter("type", type);
		List<DimFacilityStatus> resultList = query.list();
		Map<Long, ReportingStatus> rsMap = new HashMap<Long, ReportingStatus>();
		for (DimFacilityStatus rs : resultList) {
			rsMap.put(rs.getId().getFacilityId(), rs.getReportingStatus());
		}
		return rsMap;
	}
	
}
