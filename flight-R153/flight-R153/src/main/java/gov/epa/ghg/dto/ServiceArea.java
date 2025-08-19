package gov.epa.ghg.dto;

import java.util.ArrayList;
import java.util.List;

public class ServiceArea {
	
	private String id;
	private String name;
	private String color;
	private Double lt;
	private Double ln;
	private List<Shape> shapes;
	
	public ServiceArea() {
		this.shapes = new ArrayList<Shape>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Double getLt() {
		return lt;
	}

	public void setLt(Double lt) {
		this.lt = lt;
	}

	public Double getLn() {
		return ln;
	}

	public void setLn(Double ln) {
		this.ln = ln;
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}
}