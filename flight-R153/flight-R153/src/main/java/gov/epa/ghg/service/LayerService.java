package gov.epa.ghg.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateGeoDao;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.dto.ServiceArea;
import gov.epa.ghg.util.SpatialUtil;

@Service
@Transactional
public class LayerService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	DimStateGeoDao dimStateGeoDao;

	@Inject
	DimCountyDao dimCountyDao;

	@Inject
	DimMsaDao msaDao;

	@Inject
	FacilityViewInterface facilityViewService;

	public List<ServiceArea> getCountyShapes(String stateAbbr) {
		List<ServiceArea> lsa = new ArrayList<ServiceArea>();
		List<DimCounty> counties = dimCountyDao.getCountiesByState(stateAbbr);

		for (DimCounty county : counties) {
			ServiceArea sa = new ServiceArea();
			sa.setId(county.getCountyFips());
			sa.setName(county.getCountyName());
			if (county.getGeometry() instanceof MultiPolygon) {
				MultiPolygon mPoly = (MultiPolygon)county.getGeometry();
				int n = mPoly.getNumGeometries();
				for (int i=0; i<n; i++) {
					Geometry g = mPoly.getGeometryN(i);
					if (g instanceof Polygon) {
						sa.getShapes().add(SpatialUtil.createShape((Polygon)g));
					}
				}
			} else if (county.getGeometry() instanceof Polygon) {
				sa.getShapes().add(SpatialUtil.createShape((Polygon)county.getGeometry()));
			}

			sa.setColor("#AA2A53");
			lsa.add(sa);
		}


		return lsa;
	}

	public List<ServiceArea> getMsaShapes(String stateAbbr) {
		List<ServiceArea> lsa = new ArrayList<ServiceArea>();
		List<DimMsa> metros = facilityViewService.getMsas(stateAbbr);

		for (DimMsa msa : metros) {
			ServiceArea sa = new ServiceArea();
			sa.setId(msa.getCbsafp());
			sa.setName(msa.getCbsa_title());
			if (msa.getGeometry() instanceof MultiPolygon) {
				MultiPolygon mPoly = (MultiPolygon)msa.getGeometry();
				int n = mPoly.getNumGeometries();
				for (int i=0; i<n; i++) {
					Geometry g = mPoly.getGeometryN(i);
					if (g instanceof Polygon) {
						sa.getShapes().add(SpatialUtil.createShape((Polygon)g));
					}
				}
			} else if (msa.getGeometry() instanceof Polygon) {
				sa.getShapes().add(SpatialUtil.createShape((Polygon)msa.getGeometry()));
			}

			sa.setColor("#AA2A53");
			lsa.add(sa);
		}
		return lsa;
	}


	public List<ServiceArea> getBasinShapes() {

		//String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
		List<ServiceArea> lsa = new ArrayList<ServiceArea>();
		List<BasinLayer> b = basinLayerDao.getBasinLayers();
		//Map<String, String> basinColorMap = new HashMap<String, String>();
		//int colorIndex = 0;
		for (BasinLayer bl : b) {
			ServiceArea sa = new ServiceArea();
			sa.setId(bl.getBasinCode());
			sa.setName(bl.getBasin());
			if (bl.getGeometry() instanceof MultiPolygon) {
				MultiPolygon mPoly = (MultiPolygon)bl.getGeometry();
				int n = mPoly.getNumGeometries();
				for (int i=0; i<n; i++) {
					Geometry g = mPoly.getGeometryN(i);
					if (g instanceof Polygon) {
						sa.getShapes().add(SpatialUtil.createShape((Polygon)g));
					}
				}
			} else if (bl.getGeometry() instanceof Polygon) {
				sa.getShapes().add(SpatialUtil.createShape((Polygon)bl.getGeometry()));
			}
			/*String color;
			if (StringUtils.hasLength(bl.getBasinCode())) {
				color = basinColorMap.get(bl.getBasinCode());
				if (color == null) {
					color = colorArr[colorIndex];
					basinColorMap.put(bl.getBasinCode(), color);
					if (colorIndex == colorArr.length-1) {
						colorIndex = 0;	
					} else {
						colorIndex++;
					}
				}
			} else {
				color = "#000";
			}
			sa.setColor(color);*/
			sa.setColor("#AA2A53");
			lsa.add(sa);
		}
		return lsa;
	}
		

}
