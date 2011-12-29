package extension.model.persist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.Icon;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import extension.ExtensionApp;
import extension.model.ActuatorType;
import extension.model.FunctionTemplate;
import extension.model.GlobalConfig;
import extension.model.SensorType;
import extension.utils.IconBank;
import extension.utils.XmlUtils;

public class GlobalConfigXml {

	public static GlobalConfig load(InputStream is) throws SAXException, ParserConfigurationException, IOException {
        return load(XmlUtils.getDocumentFromStream(is));
	}
	public static GlobalConfig load(Document doc) throws SAXException {
		GlobalConfig config = new GlobalConfig();
		Element configElement = doc.getDocumentElement();
		for( Element elem: XmlUtils.getChildrenByTagName(configElement, "*") ) {
        	String tag = elem.getTagName();
        	if( tag.equals("herramientas") ) 
        		loadTools(elem, config);
        	else if( tag.equals("tipoSensores") ) 
        		loadSensorTypes(elem, config);
        	else if( tag.equals("tipoActuadores") ) 
        		loadActuatorTypes(elem, config);
		}
		return config;
	}
	private static void loadTools(Element toolsElement, GlobalConfig config) {
		for( Element elem: XmlUtils.getChildrenByTagName(toolsElement, "esquemacaja") ) {
				FunctionTemplate template = new FunctionTemplate(elem);
				config.addFunctionTemplate(template);
		}		
	}
	private static void loadSensorTypes(Element elem, GlobalConfig config) {
		for( Element sensorTypeElement: XmlUtils.getChildrenByTagName(elem, "tipoSensor") ) {
			String id = sensorTypeElement.getAttribute("id");
			Icon image = null;
			Element imageElement = XmlUtils.getFirstChildByTagName(sensorTypeElement, "imagen");
			URL imageUrl  = ExtensionApp.class.getResource(imageElement.getAttribute("href"));
			image = IconBank.getByUrl(imageUrl, 40, 40);		
			
			SensorType sensorType = new SensorType(id, id, image);
			config.addSensorType(sensorType);
		}
	}	
	private static void loadActuatorTypes(Element elem, GlobalConfig config) {
		for( Element sensorTypeElement: XmlUtils.getChildrenByTagName(elem, "tipoActuador") ) {
			String id = sensorTypeElement.getAttribute("id");
			Icon image = null;
			Element imageElement = XmlUtils.getFirstChildByTagName(sensorTypeElement, "imagen");
			URL imageUrl  = ExtensionApp.class.getResource(imageElement.getAttribute("href"));
			image = IconBank.getByUrl(imageUrl, 40, 40);		
			
			ActuatorType actuatorType = new ActuatorType(id, id, image);
			config.addActuatorType(actuatorType);
		}
	}	
	
	
}