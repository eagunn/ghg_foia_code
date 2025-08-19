package gov.epa.ghg.dto;

import java.util.ArrayList;
import java.util.List;

public class Shape {
	
	private List<LatLng> shell;
	private List<List<LatLng>> holes;
	
	public Shape() {
		shell = new ArrayList<LatLng>();
		holes = new ArrayList<List<LatLng>>();
	}
	
	public List<LatLng> getShell() {
		return shell;
	}
	
	public void setShell(List<LatLng> shell) {
		this.shell = shell;
	}
	
	public List<List<LatLng>> getHoles() {
		return holes;
	}
	
	public void setHoles(List<List<LatLng>> holes) {
		this.holes = holes;
	}
}