package extension.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import extension.ExtensionApp;
import extension.model.elements.ActuatorBox;
import extension.model.elements.Box;
import extension.model.elements.SensorBox;
import extension.model.persist.RobotXml;

public class Robot {
	private Vector<SensorBox> sensors;
	private Vector<ActuatorBox> actuators;
	private ImageMap imageMap;
	private String id;
	private String name;
	private String ral;
	private Vector<Diagram> defaultDiagrams;
	
	public Robot(String id, String name) {
		this.id = id;
		this.name = name;
		sensors = new Vector<SensorBox>();
		actuators = new Vector<ActuatorBox>();		
		defaultDiagrams = new Vector<Diagram>();
	}
	public void addSensor(SensorBox sensor) {
		sensors.add(sensor);
	}
	public void addActuator(ActuatorBox actuator) {
		actuators.add(actuator);
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public ImageMap getImageMap() {
		return imageMap;
	}
	public void setImageMap(ImageMap imageMap) {
		this.imageMap = imageMap;
	}
	public String getRal() {
		return ral;
	}	
	public void setRal(String ral) {
		this.ral = ral;
	}
	public Iterable<SensorBox> getSensors() {
		return sensors;
	}
	public Iterable<ActuatorBox> getActuators() {
		return actuators;
	}
	public Iterable<Diagram> getDefaultDiagrams() {
		return defaultDiagrams;
	}
	public void addDefaultDiagram(Diagram diagram) {
		defaultDiagrams.add(diagram);
	}
	public ActuatorBox getActuatorById(String id) {
		for( ActuatorBox actuator: actuators )
			if( actuator.getId().equals(id) )
				return actuator;
		return null;
	}
	public SensorBox getSensorById(String id) {
		for( SensorBox sensor: sensors )
			if( sensor.getId().equals(id) )
				return sensor;
		return null;
	}
	
	public Box getBoxById(String id) {
		Box box = getSensorById(id);
		if( box == null )
			box = getActuatorById(id);
		return box;
	}
	
	public int getNumberOfSensorTypes() { return sensors.size(); }
	
	private static Vector<Robot> robots = new Vector<Robot>();
	
	public static Iterable<Robot> getRobots() {
		String path =  ExtensionApp.class.getPackage().getName().replace('.', '/') + "/robots"; 
		//String path =  "bin/" + ExtensionApp.class.getPackage().getName().replace('.', '/') + "/robots";
		//String path =  "robots";
		File fdir = new File(path);
		for( File f: fdir.listFiles() ) {
			String s[] = f.getName().split("/|\\\\|\\.");
			if( !s[s.length-1].toLowerCase().equals("xml") )
				continue;
			System.out.println(s[s.length-2]);
			getRobotById(s[s.length-2]);
		}
		return robots;
	}
	
	public static Robot getRobotById(String id) {
		for( Robot robot: robots ) {
			if( robot.getId().equals(id) ) 
				return robot;
		}
		try {
			URL url = ExtensionApp.class.getResource(String.format("robots/%s.xml", id));
			Robot robot = RobotXml.load(url.openStream());
			robots.add(robot);
			return robot;
		
		} catch( ParserConfigurationException e) {
			return null;
		} catch(SAXException e) {
			return null;
		} catch(IOException e) {
			return null;
		}
	}
}
