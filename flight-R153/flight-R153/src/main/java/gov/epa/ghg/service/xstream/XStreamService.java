package gov.epa.ghg.service.xstream;

import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class XStreamService implements gov.epa.ghg.service.xstream.XStream {
	
	@Override
	public Object getObjectfromXML(String xml) {
		XStream xstream = new XStream(new DomDriver());
		xstream.allowTypesByWildcard(new String[]{"gov.epa.ghg.**"});
		Object object = xstream.fromXML(xml);
		return object;
	}
	
	@Override
	public String getXMLFromObject(Object instance) {
		XStream xstream = new XStream();
		xstream.allowTypesByWildcard(new String[]{"gov.epa.ghg.**"});
		String xml = xstream.toXML(instance);
		return xml;
	}
}
