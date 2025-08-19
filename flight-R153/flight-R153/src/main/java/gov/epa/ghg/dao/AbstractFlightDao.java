package gov.epa.ghg.dao;

import java.io.Serializable;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityId;
import gov.epa.ghg.domain.LuTribalLands;
import gov.epa.ghg.enums.FacilityViewType;
import static gov.epa.ghg.enums.FacilityViewType.MAP;
import gov.epa.ghg.enums.converter.ReportingStatusConverter;
import gov.epa.ghg.presentation.request.FlightRequest;


/**
 * Created by alabdullahwi on 9/15/2015.
 * <p>
 * this must NOT be taken as an Abstract Flight Dao, despite the name. Looking back I think it has very limited use...particularly against building the Facility Summary Panel (that's what
 * all the "threshold" is about
 */
@Repository
@Transactional
public abstract class AbstractFlightDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	protected SessionFactory sessionFactory;
	
	protected final int THRESHOLD = 100;
	
	protected Query createQuery(String queryString) {
		Query retv = sessionFactory.getCurrentSession().createQuery(queryString);
		retv.setCacheable(true);
		return retv;
	}
	
	@SuppressWarnings("rawtypes")
	protected Query createQuery(String queryString, boolean isLimited, int pageNumber) {
		Query retv = createQuery(queryString);
		if (isLimited) {
			retv.setFirstResult(pageNumber * THRESHOLD);
			retv.setMaxResults(THRESHOLD);
		}
		return retv;
	}
	
	protected List<DimFacility> getAllDimFacilities(String queryStr, FlightRequest request, FacilityViewType viewType) {
		return sessionFactory.getCurrentSession().doReturningWork(new ReturningWork<List<DimFacility>>() {
			@Override
			public List<DimFacility> execute(Connection connection) throws SQLException {
				try (PreparedStatement stm = connection.prepareStatement(queryStr)) {
					stm.setInt(1, request.getReportingYear());
					WKTReader reader = new WKTReader();
					List<DimFacility> dfs = new ArrayList<>();
					ResultSet rs =  stm.executeQuery();
					while (rs.next()) {
						try {
							DimFacility df = new DimFacility();
							DimFacilityId dfi = new DimFacilityId();
							dfi.setFacilityId(rs.getLong("facilityId"));
							dfi.setYear(rs.getLong("year"));
							df.setId(dfi);
							if (rs.getObject("location") != null) {
								java.sql.Clob clob = (Clob) rs.getObject("location");
								String pointStr = clob.getSubString(1, (int) clob.length());
								Geometry geom = reader.read(pointStr);
								df.setLocation(geom.getInteriorPoint());
							}
							df.setAddress1(rs.getString("address1"));
							df.setAddress2(rs.getString("address2"));
							df.setCemsUsed(rs.getString("cemsUsed"));
							df.setCity(rs.getString("city"));
							df.setCo2Captured(rs.getString("co2Captured"));
							df.setCo2EmittedSupplied(rs.getString("emittedCo2Supplied"));
							df.setComments(rs.getString("comments"));
							df.setCounty(rs.getString("county"));
							df.setCountyFips(rs.getString("countyFips"));
							df.setEggrtFacilityId(rs.getLong("eggrtFacilityId"));
							df.setEmissionClassification(rs.getString("emissionClassificationCode"));
							df.setFacilityName(rs.getString("facilityName"));
							df.setFrsId(rs.getString("frsId"));
							if (rs.getDouble("latitude") != 0) {
								df.setLatitude(rs.getDouble("latitude"));
							}
							if (rs.getDouble("longitude") != 0) {
								df.setLongitude(rs.getDouble("longitude"));
							}
							df.setNaicsCode(rs.getString("naicsCode"));
							df.setParentCompany(rs.getString("parentCompany"));
							df.setProcessStationaryCml(rs.getString("processStationaryCml"));
							df.setProgramName(rs.getString("programName"));
							df.setProgramSysId(rs.getString("programSysId"));
							df.setReportedIndustryTypes(rs.getString("reportedIndustryTypes"));
							df.setReportedSubparts(rs.getString("reportedSubparts"));
							df.setRrMonitoringPlan(rs.getBytes("rrMonitoringPlan"));
							df.setRrFilename(rs.getString("rrMonitoringPlanFilename"));
							df.setRrLink(rs.getString("rrMrvPlanUrl"));
							df.setState(rs.getString("state"));
							df.setStateName(rs.getString("stateName"));
							df.setUuRandDExempt(rs.getString("uuRdExempt"));
							df.setZip(rs.getString("zip"));
							LuTribalLands tland = new LuTribalLands();
							tland.setTribalLandId(rs.getLong("tribalLandId"));
							if (viewType == MAP) {
								tland.setTribalLandName(rs.getString("tribalLandName"));
							}
							df.setTribalLand(tland);
							if (rs.getString("reportingStatus") != null) {
								ReportingStatusConverter converter = new ReportingStatusConverter();
								df.setReportingStatus(converter.convertToEntityAttribute(rs.getString("reportingStatus")));
							}
							dfs.add(df);
						} catch (SQLException | ParseException e) {
							e.printStackTrace();
						}
					}
					return dfs;
				}
			}
		});
	}
}
