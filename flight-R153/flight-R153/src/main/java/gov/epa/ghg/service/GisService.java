package gov.epa.ghg.service;

import java.io.Serializable;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.epa.ghg.dao.BasinLayerDAO;
import gov.epa.ghg.dao.DimCountyDao;
import gov.epa.ghg.dao.DimMsaDao;
import gov.epa.ghg.dao.DimStateGeoDao;
import gov.epa.ghg.domain.BasinLayer;
import gov.epa.ghg.domain.DimCounty;
import gov.epa.ghg.domain.DimMsa;
import gov.epa.ghg.domain.DimStateGeo;
import gov.epa.ghg.dto.LatLng;
import gov.epa.ghg.dto.LatLngBounds;

@Service
@Transactional
public class GisService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	DimStateGeoDao dimStateGeoDao;

	@Inject
	DimCountyDao dimCountyDao;
	
	@Inject
	BasinLayerDAO basinLayerDao;
	
	@Inject
	DimMsaDao dimMsaDao;
	
	//private static String[] colorArr = new String[] {"#E20048","#AA2A53","#93002F","#F13C76","#F16C97","#A600A6","#7C1F7C","#6C006C","#D235D2","#D25FD2","#540EAD","#502982","#340570","#8443D6","#9A6AD6","#1D1AB2","#323086","#0B0974","#514ED9","#7573D9","#0B61A4","#25567B","#033E6B","#3F92D2","#66A3D2","#00AE68","#21825B","#007143","#36D695","#60D6A7","#62E200","#62AA2A","#409300","#8BF13C","#A6F16C","#C9F600","#9FB82E","#83A000","#D8FA3F","#E1FA71","#FFE900","#BFB330","#A69800","#FFEF40","#FFF373","#FFBF00","#BF9B30","#A67C00","#FFCF40","#FFDC73","#FF9400","#BF8330","#A66000","#FFAE40","#FFC473","#FF4900","#BF5930","#A62F00","#FF7640","#FF9B73","#E40045","#AB2B52","#94002D","#F13C73","#F16D95"};
	
	public LatLngBounds getStateBounds(String state) {
		DimStateGeo s = dimStateGeoDao.getStateByStateAbbr(state);
		
		if (s!=null && s.getGeometry() != null && s.getGeometry().getEnvelope() != null) {
			if (state.equals("AK")) {
				//Shows Alaska and the ones in California
				LatLngBounds akb = new LatLngBounds(new LatLng(51.1750920000966, -179.231085999719), new LatLng(71.185865, -143.041992));
				//Shows just Alaska - LatLngBounds akb = new LatLngBounds(new LatLng(58.946986, -160.883789), new LatLng(69.035045, -144.975586));
				return akb;
			}
			else {
				LatLngBounds llb = new LatLngBounds(new LatLng(s.getGeometry().getEnvelope().getCoordinates()[0].y, s.getGeometry().getEnvelope().getCoordinates()[0].x),
					new LatLng(s.getGeometry().getEnvelope().getCoordinates()[2].y, s.getGeometry().getEnvelope().getCoordinates()[2].x)); 
				return llb;
			}
		} else {
			return null;
		}
	}

	public LatLngBounds getCountyBounds(String fipsCode) {

		DimCounty c = dimCountyDao.getCountyByFips(fipsCode);
		if (c!=null && c.getGeometry() != null && c.getGeometry().getEnvelope() != null) {
			LatLngBounds llb = new LatLngBounds(new LatLng(c.getGeometry().getEnvelope().getCoordinates()[0].y, c.getGeometry().getEnvelope().getCoordinates()[0].x),
				new LatLng(c.getGeometry().getEnvelope().getCoordinates()[2].y, c.getGeometry().getEnvelope().getCoordinates()[2].x)); 
			return llb; 
		} else {
			return null;
		}
	}

	public LatLngBounds getBasinBounds(String basinCode) {

		BasinLayer b = basinLayerDao.getBasinByCode(basinCode);
		if (b!=null && b.getGeometry() != null && b.getGeometry().getEnvelope() != null) {
			LatLngBounds llb = new LatLngBounds(new LatLng(b.getGeometry().getEnvelope().getCoordinates()[0].y, b.getGeometry().getEnvelope().getCoordinates()[0].x),
				new LatLng(b.getGeometry().getEnvelope().getCoordinates()[2].y, b.getGeometry().getEnvelope().getCoordinates()[2].x));
			return llb;
		} else {
			return null;
		}
	}
	
	public LatLngBounds getMsaBounds(String msaCode) {

		DimMsa msa = dimMsaDao.getMsaByCode(msaCode);
		if (msa!=null && msa.getGeometry() != null && msa.getGeometry().getEnvelope() != null) {
			LatLngBounds llb = new LatLngBounds(new LatLng(msa.getGeometry().getEnvelope().getCoordinates()[0].y, msa.getGeometry().getEnvelope().getCoordinates()[0].x),
				new LatLng(msa.getGeometry().getEnvelope().getCoordinates()[2].y, msa.getGeometry().getEnvelope().getCoordinates()[2].x));
			return llb;
		} else {
			return null;
		}
	}
}
