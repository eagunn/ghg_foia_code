package gov.epa.ghg.dto;


public class GasQuantity {
	
	String type;
	Long quantity;
	String subpartName;
	String state;
	
	public GasQuantity() {
		super();
	}

	public GasQuantity(String type, Long quantity) {
		super();
		this.type = type;
		this.quantity = quantity;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public String getSubpartName() {
		return subpartName;
	}
	public void setSubpartName(String subpartName) {
		this.subpartName = subpartName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
