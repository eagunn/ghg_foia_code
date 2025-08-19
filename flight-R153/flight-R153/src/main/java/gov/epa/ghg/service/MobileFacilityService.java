package gov.epa.ghg.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.MobileFacilityDaoInterface;
import gov.epa.ghg.dto.Facility;
import gov.epa.ghg.dto.GasFilter;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.QueryOptions;
import gov.epa.ghg.dto.SectorFilter;

@Service
@Transactional
public class MobileFacilityService implements Serializable, MobileFacilityInterface{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	MobileFacilityDaoInterface mobileFacilityDao;
	
	public List<Facility> getFacilityList(int page, String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, int sortOrder, String rs) {

		return mobileFacilityDao.getEmitterList(q, year, stateCode, fipsCode, lowE, highE, page,
			gases, sectors, qo,
			sortOrder,rs);
	}

	public List<Facility> getFacilityListAround(String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, LatLng center, double radius, String rs) {

		return mobileFacilityDao.getEmitterListAround(q, year, stateCode, fipsCode, lowE, highE,
			gases, sectors, qo, center, radius,rs);
	}
	
	public List<Facility> getFacilityListWithin(String q, int year, String stateCode, String fipsCode, String lowE, String highE,
			GasFilter gases, SectorFilter sectors, QueryOptions qo,
			String sectorType, int sc, LatLng location, Geometry bounds, String rs) {

		return mobileFacilityDao.getEmitterListWithin(q, year, stateCode, fipsCode, lowE, highE,
			gases, sectors, qo, location, bounds,rs);
	}
}
