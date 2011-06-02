package extension.model;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import extension.ExtensionApp;
import extension.model.persist.GlobalConfigXml;

public class GlobalConfig {
	private Vector<FunctionTemplate> functionTemplates;
	private HashMap<String,SensorType> sensorTypes;
	private HashMap<String,ActuatorType> actuatorTypes;
	
	public GlobalConfig() {
		functionTemplates = new Vector<FunctionTemplate>();
		sensorTypes = new HashMap<String,SensorType>();
		actuatorTypes = new HashMap<String,ActuatorType>();
	}
	public Iterable<FunctionTemplate> getFunctionTemplates() {
		return functionTemplates;
	}
	public void addFunctionTemplate(FunctionTemplate template) {
		functionTemplates.add(template);
	}
	public FunctionTemplate getFunctionTemplateById(String id) {
		for( FunctionTemplate template: functionTemplates )
			if( template.getId().equals(id) )
				return template;
		return null;
	}

	public SensorType getSensorType(String type) {
		return sensorTypes.get(type);
	}
	public Iterable<SensorType> getSensorTypes() {
		return sensorTypes.values();
	}
	public void addSensorType(SensorType sensorType) {
		sensorTypes.put(sensorType.getId(), sensorType);
	}
	public ActuatorType getActuatorType(String type) {
		return actuatorTypes.get(type);
	}
	public Iterable<ActuatorType> getActuatorTypes() {
		return actuatorTypes.values();
	}
	public void addActuatorType(ActuatorType actuatorType) {
		actuatorTypes.put(actuatorType.getId(), actuatorType);
	}
	
	private static GlobalConfig currentConfig = null;
	public static GlobalConfig getCurrentConfig() {
		if( currentConfig == null ) {
			URL url = ExtensionApp.class.getResource("config.xml"); 
			try {
				currentConfig = GlobalConfigXml.load(url.openStream());
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return currentConfig;
	}
}
