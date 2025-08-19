package gov.epa.ghg.dto;

public class LatLngBounds {

	private LatLng sw;
	private LatLng ne;

	public LatLngBounds(LatLng sw, LatLng ne) {
		this.sw = sw;
		this.ne = ne;
	}

	public LatLng getSw() {
		return sw;
	}

	public void setSw(LatLng sw) {
		this.sw = sw;
	}

	public LatLng getNe() {
		return ne;
	}

	public void setNe(LatLng ne) {
		this.ne = ne;
	}
}
