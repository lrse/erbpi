package extension.model.persist;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import extension.ExtensionApp;
import extension.model.ActuatorType;
import extension.model.GlobalConfig;
import extension.model.ImageMap;
import extension.model.Panel;
import extension.model.Robot;
import extension.model.SensorType;
import extension.model.elements.ActuatorBox;
import extension.model.elements.SensorBox;
import extension.utils.IconBank;
import extension.utils.XmlUtils;

public class RobotXml {
	
	public static Robot load(InputStream is) throws SAXException, ParserConfigurationException, IOException {
        return load(XmlUtils.getDocumentFromStream(is));
	}
	public static Robot load(Document doc) throws SAXException {

		Element robotElement = doc.getDocumentElement();
        String id = robotElement.getAttribute("id");
        String name = robotElement.getAttribute("nombre");
		Robot robot = new Robot(id,name);
		
		for( Element elem: XmlUtils.getChildrenByTagName(robotElement, "*") ) {
        	String tag = elem.getTagName();
        	if( tag.equals("sensores") )
        		loadSensors(elem, robot);
        	else if( tag.equals("actuadores") )
        		loadActuators(elem, robot);
        	else if( tag.equals("imagen") )
        		loadImageMap(elem, 100, 100, robot);
        	else if( tag.equals("ral") )
        		loadRal(elem, robot);
        	//else if( tag.equals("diagramas") )
        	//	loadDiagrams(elem, robot);
        }        	
		return robot;
	}
	private static void loadActuators(Element actuatorsElement, Robot robot) {
		ImageMap imageMap = robot.getImageMap();
		for( Element actuatorElement: XmlUtils.getChildrenByTagName(actuatorsElement, "actuador") ) {
			String id = actuatorElement.getAttribute("id");
			ActuatorType type = GlobalConfig.getCurrentConfig().getActuatorType(actuatorElement.getAttribute("tipo"));			
			
//			String label = actuatorElement.hasAttribute("nombre") ? actuatorElement.getAttribute("nombre") : id;
			
			Element ubicacionElement = XmlUtils.getFirstChildByTagName(actuatorElement, "ubicacion");
			String locationId = ubicacionElement.getAttribute("id");
			Panel.Location location = Panel.Location.NONE;
			
			if (locationId.equals("izquierda"))		location = Panel.Location.LEFT;
			else if (locationId.equals("derecha"))	location = Panel.Location.RIGHT;
			else if (locationId.equals(""))			location = Panel.Location.NONE;
			else
				System.err.println("RobotXml::loadActuators - no existe una locationId '" + locationId + "'!!!");
			
			ActuatorBox actuator = new ActuatorBox(id, type, null, location);
			robot.addActuator(actuator);
			
			Element mapaImagenElement = XmlUtils.getFirstChildByTagName(actuatorElement, "mapaimagen");
			loadImageMapFeature(mapaImagenElement, imageMap, id);
		}	
	}		
	private static void loadImageMapFeature(Element imageMapElement, ImageMap imageMap, String boxId) {
		for( Element elem: XmlUtils.getChildrenByTagName(imageMapElement, "*") ) {
			if( elem.getTagName() == "linea") {
				int x0 = (int)(Float.parseFloat(elem.getAttribute("x0")) * imageMap.icon.getIconWidth());
				int y0 = (int)(Float.parseFloat(elem.getAttribute("y0")) * imageMap.icon.getIconHeight());
				int x1 = (int)(Float.parseFloat(elem.getAttribute("x1")) * imageMap.icon.getIconWidth());
				int y1 = (int)(Float.parseFloat(elem.getAttribute("y1")) * imageMap.icon.getIconHeight());
				int width = Integer.parseInt(elem.getAttribute("width"));
				Color c;
				if( elem.hasAttribute("color") ) {
					String colorStr = elem.getAttribute("color");
					StringTokenizer st = new StringTokenizer(colorStr, ",");
					int[] rgb = new int[3];
					for( int k=0; k<3; k++ )
						rgb[k] = Integer.parseInt(st.nextToken());
					c = new Color(rgb[0], rgb[1], rgb[2]);
				} else 
					c = Color.BLACK;
				imageMap.addLine(boxId, new Point(x0,y0), new Point(x1,y1), width, c);
			}
			else if( elem.getTagName() == "circulo") {
				int x = (int)(Float.parseFloat(elem.getAttribute("x")) * imageMap.icon.getIconWidth());
				int y = (int)(Float.parseFloat(elem.getAttribute("y")) * imageMap.icon.getIconHeight());
				Color c;
				if( elem.hasAttribute("color") ) {
					String colorStr = elem.getAttribute("color");
					StringTokenizer st = new StringTokenizer(colorStr, ",");
					int[] rgb = new int[3];
					for( int k=0; k<3; k++ )
						rgb[k] = Integer.parseInt(st.nextToken());
					c = new Color(rgb[0], rgb[1], rgb[2]);
				} else 
					c = Color.BLACK;
				imageMap.addPoint(boxId, new Point(x,y),c);						
			}
		}
	}
	
	private static void loadSensors(Element sensorsElement, Robot robot) {
		ImageMap imageMap = robot.getImageMap();
		for( Element sensorElement: XmlUtils.getChildrenByTagName(sensorsElement, "sensor") ) {
			String id = sensorElement.getAttribute("id");
			SensorType type = GlobalConfig.getCurrentConfig().getSensorType(sensorElement.getAttribute("tipo"));
			SensorBox sensor = new SensorBox(id, type);
			robot.addSensor(sensor);
			
			Element mapaImagenElement = XmlUtils.getFirstChildByTagName(sensorElement, "mapaimagen");
			loadImageMapFeature(mapaImagenElement, imageMap, id);
		}
	}
	private static ImageIcon loadImage(Element imageElement, int width, int height) {
		URL imageUrl  = ExtensionApp.class.getResource(imageElement.getAttribute("href"));		
		return IconBank.getByUrl(imageUrl, width, height);		
	}
	private static void loadImageMap(Element imageElement, int width, int height, Robot robot) {
		ImageIcon image = loadImage(imageElement, width, height);		
		robot.setImageMap(new ImageMap(image));
	}
	private static void loadRal(Element ralElement, Robot robot) {
		robot.setRal(ralElement.getAttribute("href"));
	}
	/*
    private static void loadDiagrams(Element diagramsElement, Robot robot) throws SAXException {
    	for( Element diagramElement: XmlUtils.getChildrenByTagName(diagramsElement, "diagrama") ) {
			String id = diagramElement.getAttribute("id");
			String label = diagramElement.getAttribute("nombre");
			String strtype = diagramElement.getAttribute("tipo");
			Diagram.Type type;
			if( strtype.toLowerCase().equals("simple") )
				type = Diagram.Type.SINGLE;
			else if( strtype.toLowerCase().equals("doble") )
				type = Diagram.Type.DOUBLE;
			else
				throw new SAXException("Invalid diagram type");

			Diagram diagram = new Diagram(id,label,type);

			for( Element panelElement: XmlUtils.getChildrenByTagName(diagramElement, "panel") ) {
				String panelId = panelElement.getAttribute("id");
				String panelLabel = panelElement.getAttribute("nombre");
				strtype = panelElement.getAttribute("tipo");
				Panel.Type panelType;
				if( strtype.toLowerCase().equals("entrada") )
					panelType = Panel.Type.INPUT;
				else if( strtype.toLowerCase().equals("salida") )
					panelType = Panel.Type.OUTPUT;
				else if( strtype.toLowerCase().equals("cajas") )
					panelType = Panel.Type.BOXES;
				else
					throw new SAXException("Invalid panel type");						
				Panel panel = new Panel(panelId,panelLabel,panelType);
				diagram.addPanel(panel);
				
				String strhandlers = panelElement.getAttribute("manijas");
				if( strhandlers != null ) {
					if( strhandlers.toLowerCase().equals("izquierda") )
						panel.setHandles(Panel.Handles.LEFT);
					else if( strhandlers.toLowerCase().equals("derecha") )
						panel.setHandles(Panel.Handles.RIGHT);
					else if( strhandlers.toLowerCase().equals("ninguna") )
						panel.setHandles(Panel.Handles.NONE);
					else if( strhandlers.toLowerCase().equals("ambas") )
						panel.setHandles(Panel.Handles.BOTH);
				}
				for( Element elem: XmlUtils.getChildrenByTagName(panelElement, "*") ) {
					if( elem.getTagName().equals("ubicacion") ) {
						String locationId = elem.getAttribute("id");
						panel.addLocationId(locationId);
					}
					else if( elem.getTagName().equals("cajas") ) {
						NodeList nl4 = elem.getChildNodes();
						for( int h=0; h<nl4.getLength(); h++ ) {
							if( nl4.item(h).getNodeType() != Node.ELEMENT_NODE )
								continue;
							Element boxElem = (Element)(nl4.item(h));
							String boxId = boxElem.getAttribute("id");
							Box box = robot.getBoxById(boxId);
							panel.addDefaultBox(box);
						}
					}
				}
				robot.addDefaultDiagram(diagram);
			}
    	}
    }
    */
	
}
