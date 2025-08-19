package gov.epa.ghg.service.xstream;

public interface XStream {
	
	public String getXMLFromObject(Object instance);
	
	public Object getObjectfromXML(String xml);
}
