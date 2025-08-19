package gov.epa.ghg.service.view.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimFacilityDao;
import gov.epa.ghg.dao.DimStateGeoDao;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimFacility;
import gov.epa.ghg.domain.DimFacilityPipe;
import gov.epa.ghg.domain.DimStateGeo;
import gov.epa.ghg.domain.FacilityViewSub;
import gov.epa.ghg.domain.PubBasinFacility;
import gov.epa.ghg.domain.PubLdcFacility;
import gov.epa.ghg.domain.PubSf6Territory;
import gov.epa.ghg.dto.SectorFilter;
import gov.epa.ghg.enums.ReportingStatus;
import gov.epa.ghg.util.ServiceUtils;
import gov.epa.ghg.util.SpatialUtil;

/**
 * Created by alabdullahwi on 5/22/2015.
 */
@Service
public class FacilityViewSubTransformer {
	
	protected final String[] colorArr = new String[]{"#E20048", "#AA2A53", "#93002F", "#F13C76", "#F16C97", "#A600A6", "#7C1F7C", "#6C006C", "#D235D2", "#D25FD2", "#540EAD", "#502982", "#340570", "#8443D6", "#9A6AD6", "#1D1AB2", "#323086", "#0B0974", "#514ED9", "#7573D9", "#0B61A4", "#25567B", "#033E6B", "#3F92D2", "#66A3D2", "#00AE68", "#21825B", "#007143", "#36D695", "#60D6A7", "#62E200", "#62AA2A", "#409300", "#8BF13C", "#A6F16C", "#C9F600", "#9FB82E", "#83A000", "#D8FA3F", "#E1FA71", "#FFE900", "#BFB330", "#A69800", "#FFEF40", "#FFF373", "#FFBF00", "#BF9B30", "#A67C00", "#FFCF40", "#FFDC73", "#FF9400", "#BF8330", "#A66000", "#FFAE40", "#FFC473", "#FF4900", "#BF5930", "#A62F00", "#FF7640", "#FF9B73", "#E40045", "#AB2B52", "#94002D", "#F13C73", "#F16D95"};
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	DimStateGeoDao dimStateGeoDao;
	
	@Inject
	DimFacilityDao dimFacilityDao;
	
	public List<FacilityViewSub> transformBubbleMap(List<DimFacility> data, Map<Long, BigDecimal> emissions) {
		
		List<FacilityViewSub> retv = new ArrayList<FacilityViewSub>();
		
		for (int i = 0; i < data.size(); i++) {
			
			DimFacility dimFacility = data.get(i);
			Long facilityId = dimFacility.getId().getFacilityId();
			FacilityViewSub fvs = new FacilityViewSub();
			fvs.setId(facilityId);
			fvs.setLt(dimFacility.getLatitude());
			fvs.setLn(dimFacility.getLongitude());
			fvs.setEmissions(emissions.get(facilityId));
			
			retv.add(fvs);
		}
		return retv;
	}
	
	public List<FacilityViewSub> transformSupplier(List<DimFacility> results, int supplierSector, Map<Long, ReportingStatus> rsMap) {
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		
		int colorIndex = 0;
		for (DimFacility df : results) {
			if (df.getLatitude() != null && df.getLongitude() != null) {
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), df.getLatitude(), df.getLongitude(), rs);
				if (supplierSector == 32) { /* LDC = 32 */
					List<PubLdcFacility> results2 = dimFacilityDao.getFacLayers(df.getId().getFacilityId(), df.getId().getYear());
					Iterator i = results2.iterator();
					while (i.hasNext()) {
						PubLdcFacility plf = (PubLdcFacility) i.next();
						if ("NN".equals(plf.getId().getType())) {
							try {
								fvs.setSa(ServiceUtils.getServiceArea(plf));
								fvs.getSa().setColor(colorArr[colorIndex]);
								if (colorIndex == colorArr.length - 1) {
									colorIndex = 0;
								} else {
									colorIndex++;
								}
							} catch (Exception e) {
								// Swallow exception, facility will be rendered as a pin
							}
							break;
						}
					}
				}
				fvsList.add(fvs);
			}
		}
		return fvsList;
		
	}
	
	public List<FacilityViewSub> transformOnshore(List<DimFacility> results, String basin, Map<Long, ReportingStatus> rsMap) {
		
		/**
		 * Updated by lee@saic March 2018
		 * expand getting random lat/lon coordinates from selected basin's geometry to ensure facilities placed within the basin
		 */
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		
		for (DimFacility df : results) {
			if (df.getLatitude() != null && df.getLongitude() != null) {
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), df.getLatitude(), df.getLongitude(), rs);
				BasinLayer bl = new BasinLayer();
				if (org.springframework.util.StringUtils.hasLength(basin)) {
					bl = basinLayerDao.getBasinByCode(basin);
				} else {
					//for sql fit and hibernate query will not break as it is getting the objects
					List<PubBasinFacility> results2 = dimFacilityDao.getFacBasin(df.getId().getFacilityId(), df.getId().getYear());	
					if (!results2.isEmpty()) {
						Iterator iter = results2.iterator();
						while (iter.hasNext()) {
							bl = ((PubBasinFacility) iter.next()).getLayer();
						}
					}
				}
				if (bl != null && bl.getGeometry() != null) {
					Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(df.getLongitude(), df.getLatitude()));
					if (!bl.getGeometry().contains(p)) {
						// p = bl.getGeometry().getInteriorPoint();
						for (int i = 0; i < 20; i++) { //
							Envelope e = bl.getGeometry().getEnvelopeInternal();
							p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
							if (p != null && bl.getGeometry().contains(p)) {
								fvs.setLt(p.getCoordinate().y);
								fvs.setLn(p.getCoordinate().x);
								break;
							}
						}
					}
				}
				fvsList.add(fvs);
			}
		}
		return fvsList;
		
	}
	
	public List<FacilityViewSub> transformCo2Injection(List<DimFacility> results, Map<Long, ReportingStatus> rsMap) {
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		
		for (DimFacility df : results) {
			if (df.getLatitude() != null && df.getLongitude() != null) {
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), df.getLatitude(), df.getLongitude(), rs);
				fvsList.add(fvs);
			}
		}
		return fvsList;
	}
	
	public List<FacilityViewSub> transformEmitter(List<DimFacility> results, String sectorType, SectorFilter sectors, Map<Long, ReportingStatus> rsMap) {
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		int colorIndex = 0;
		for (DimFacility df : results) {
			if (df.getLatitude() != null && df.getLongitude() != null) {
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), df.getLatitude(), df.getLongitude(), rs);
				if ("L".equals(sectorType) || sectors.isLDCSectorOnly()) {
					List<PubLdcFacility> results2 = dimFacilityDao.getFacLayers(df.getId().getFacilityId(), df.getId().getYear());
					Iterator i = results2.iterator();
					while (i.hasNext()) {
						PubLdcFacility plf = (PubLdcFacility) i.next();
						if ("W".equals(plf.getId().getType())) {
							try {
								fvs.setSa(ServiceUtils.getServiceArea(plf));
								fvs.getSa().setColor(colorArr[colorIndex]);
								if (colorIndex == colorArr.length - 1) {
									colorIndex = 0;
								} else {
									colorIndex++;
								}
							} catch (Exception e) {
							}
							break;
						}
					}
				}
				fvsList.add(fvs);
			}
		}
		return fvsList;
		
	}
	
	public List<FacilityViewSub> transformPipeBubbleMap(List<DimFacilityPipe> data, Map<String, Map<String, BigDecimal>> keyMap) {
		
		List<FacilityViewSub> retv = new ArrayList<FacilityViewSub>();
		
		for (int i = 0; i < data.size(); i++) {
			
			DimFacilityPipe dimFacility = data.get(i);
			Long facId = dimFacility.getId().getFacilityId();
			FacilityViewSub fvs = new FacilityViewSub();
			fvs.setId(facId);
			fvs.setLt(dimFacility.getLatitude());
			fvs.setLn(dimFacility.getLongitude());
			BigDecimal emission = null;
			for (String key : keyMap.keySet()) {
				Map<String, BigDecimal> pipeEmMap = keyMap.get(key);
				for (String emissionKey : pipeEmMap.keySet()) {
					if (pipeEmMap.get(emissionKey) != null) {
						if (emission == null) {
							emission = BigDecimal.ZERO;
						}
						emission = emission.add(pipeEmMap.get(emissionKey));
					}
				}
			}
			fvs.setEmissions(emission);
			fvs.setState(dimFacility.getState());
			
			retv.add(fvs);
		}
		return retv;
	}
	
	public List<FacilityViewSub> transformPipeEmitter(List<Object[]> results, String state, Map<Long, ReportingStatus> rsMap) {
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		
		for (Object[] result : results) {
			Long facilityId = (Long) result[2];
			String facState = ServiceUtils.nullSafeHtmlUnescape((String) result[4]);
			ReportingStatus rs = rsMap.get(facilityId);
			FacilityViewSub fvs = new FacilityViewSub(facilityId, rs, facState);
			DimStateGeo sg = new DimStateGeo();
			if (org.springframework.util.StringUtils.hasLength(state)) {
				sg = dimStateGeoDao.getStateByStateAbbr(state);
			} else {
				if (!facState.isEmpty()) {
					sg = dimStateGeoDao.getStateByStateAbbr(facState);
				}
			}
			if (sg != null && sg.getGeometry() != null) {
				Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate());
				if (!sg.getGeometry().contains(p)) {
					for (int i = 0; i < 10; i++) {
						Envelope e = sg.getGeometry().getEnvelopeInternal();
						p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
						if (p != null && sg.getGeometry().contains(p)) {
							fvs.setLt(p.getCoordinate().y);
							fvs.setLn(p.getCoordinate().x);
							break;
						}
					}
				}
			}
			fvsList.add(fvs);
		}
		return fvsList;
		
	}
	
	public List<FacilityViewSub> transformBasin(List<DimFacility> results, String basin, Map<Long, ReportingStatus> rsMap) {
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		
		for (DimFacility df : results) {
			ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
			FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), rs);
			BasinLayer bl = new BasinLayer();
			String facBasin = df.retrieveBasinCode();
			if (org.springframework.util.StringUtils.hasLength(basin)) {
				bl = basinLayerDao.getBasinByCode(basin);
			} else {
				if (!facBasin.isEmpty()) {
					bl = basinLayerDao.getBasinByCode(facBasin);
				}
			}
			if (bl != null && bl.getGeometry() != null) {
				Point p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate());
				if (!bl.getGeometry().contains(p)) {
					for (int i = 0; i < 10; i++) {
						Envelope e = bl.getGeometry().getEnvelopeInternal();
						p = SpatialUtil.getGeometryFactory().createPoint(new Coordinate(e.getMinX() + Math.random() * e.getWidth(), e.getMinY() + Math.random() * e.getHeight()));
						if (p != null && bl.getGeometry().contains(p)) {
							fvs.setLt(p.getCoordinate().y);
							fvs.setLn(p.getCoordinate().x);
							break;
						}
					}
				}
			}
			fvsList.add(fvs);
		}
		return fvsList;
		
	}
	
	public List<FacilityViewSub> transformSf6(List<DimFacility> results, String sectorType, SectorFilter sectors, Map<Long, ReportingStatus> rsMap) {
		
		List<FacilityViewSub> fvsList = new ArrayList<FacilityViewSub>();
		int colorIndex = 0;
		for (DimFacility df : results) {
			if (df.getLatitude() != null && df.getLongitude() != null) {
				ReportingStatus rs = rsMap.get(df.getId().getFacilityId());
				FacilityViewSub fvs = new FacilityViewSub(df.getId().getFacilityId(), df.getLatitude(), df.getLongitude(), rs);
				List<PubSf6Territory> results2 = dimFacilityDao.getFacTerritories(df.getId().getFacilityId(), df.getId().getYear());
				Iterator i = results2.iterator();
				//Iterator i = df.getTerritories().iterator();
				while (i.hasNext()) {
					PubSf6Territory psf = (PubSf6Territory) i.next();
					try {
						fvs.setSa(ServiceUtils.getSf6ServiceArea(psf));
						fvs.getSa().setColor(colorArr[colorIndex]);
						if (colorIndex == colorArr.length - 1) {
							colorIndex = 0;
						} else {
							colorIndex++;
						}
					} catch (Exception e) {
					}
					break;
				}
				fvsList.add(fvs);
			}
		}
		return fvsList;
		
	}
}
