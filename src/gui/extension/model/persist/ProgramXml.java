package extension.model.persist;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

// TODO
// esta clase creo que no se usa...
public class ProgramXml
{
	
	public static Element load(File f) throws ParserConfigurationException, SAXException, IOException
	{
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element domElement = docBuilder.parse(f).getDocumentElement();
        return domElement;
//        return load(doc);
	}
	
//	private static BehaviorProgram load(Element programElement) {
//		//boxes = new Vector<Box>();
//		//connections = new HashMap<Box,Vector<Box>>();
//		//this.connectionMaker = new ConnectionMaker(this);
//
//		Vector<String[]> connectionIds = new Vector<String[]>();
//		String robotId = XmlUtils.getChildByTagName(programElement, "robot").getAttribute("id");
//		Robot robot = Robot.getRobotById(robotId);
//		BehaviorProgram program = new BehaviorProgram(robot);
//		loadSensors(XmlUtils.getChildByTagName(programElement, "sensores"), program);
//		
//		loadBoxes((Element)(programElement.getElementsByTagName("cajas").item(0)), connectionIds, program);
//		loadActuators((Element)(programElement.getElementsByTagName("actuadores").item(0)), connectionIds, program);
//		connectAfterLoad(connectionIds, program);
//
//		return program;
//	}
//	
//	public static void save(File file, BehaviorProgram program) throws ParserConfigurationException, IOException, TransformerException {
//        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
//        Document doc = docBuilder.newDocument();
//        Element elem = save(doc, program);
//		doc.appendChild(elem);
//		
//		//set up a transformer
//        TransformerFactory transfac = TransformerFactory.newInstance();
//        Transformer trans = transfac.newTransformer();
//        //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        trans.setOutputProperty(OutputKeys.INDENT, "yes");
//
//        //create string from xml tree
//        FileWriter sw = new FileWriter(file);
//        StreamResult result = new StreamResult(sw);
//        DOMSource source = new DOMSource(doc);
//        trans.transform(source, result);
//        sw.close();        
//	}
//	
//	public static Element save(Document doc, BehaviorProgram program) {
//		Element sensorsElement = doc.createElement("sensores");
//		Element actuatorsElement = doc.createElement("actuadores");
//		Element functionsElement = doc.createElement("cajas"); 
//		for( Box box: program.getBoxes() ) {
//			Element boxElement = saveBox(doc, box);
//			Element inputsElement = doc.createElement("entradas");
//			for( Box src: program.getConnectionsTo(box) ) {
//				Element inputElement = doc.createElement("entrada");
//				inputElement.setAttribute("id", src.getId());
//				inputsElement.appendChild(inputElement);
//			}	
//			if( inputsElement.getChildNodes().getLength() > 0 )
//				boxElement.appendChild(inputsElement);
//			
//			if( box instanceof SensorBox ) {
//				sensorsElement.appendChild(boxElement);
//			} else if( box instanceof ActuatorBox ) {
//				actuatorsElement.appendChild(boxElement);
//			} else if( box instanceof FunctionBox ) {
//				functionsElement.appendChild(boxElement);
//			}			
//		}
//		Element robotElement = doc.createElement("robot");
//		robotElement.setAttribute("id",program.getRobot().getId());
//		
//		Element programElement = doc.createElement("ejecucion");
//		programElement.appendChild(sensorsElement);
//		programElement.appendChild(actuatorsElement);
//		programElement.appendChild(functionsElement);
//		programElement.appendChild(robotElement);
//		
//		return programElement;
//	}
//	
//
//	private static void loadSensors(Element sensorsElement, BehaviorProgram program) {
//		for( Element sensorElement: XmlUtils.getChildrenByTagName(sensorsElement, "sensor") ) {			
//			String id = sensorElement.getAttribute("id");
//			SensorType type = GlobalConfig.getCurrentConfig().getSensorType(sensorElement.getAttribute("tipo"));
//			Box box;
//			if( program.getRobot() != null )
//				box = program.getRobot().getSensorById(id);
//			else
//				box = new SensorBox(id, type);
//			program.addBox(box);
//		}
//	}
//	
//	private static void loadActuators(Element actuatorsElement, Vector<String[]> connectionIds, BehaviorProgram program) {
//		for( Element actuatorElement: XmlUtils.getChildrenByTagName(actuatorsElement,"actuador") ) {
//			String id = actuatorElement.getAttribute("id");
//			ActuatorType type = GlobalConfig.getCurrentConfig().getActuatorType(actuatorElement.getAttribute("tipo"));
//			Box box;
//			
//			if( program.getRobot() != null ) {
//				box = program.getRobot().getActuatorById(id);
//			} else {
//				assert false;
//				box = new ActuatorBox(id,type, program, null);
//			}
//			program.addBox(box);
//			Element inputsElement = XmlUtils.getChildByTagName(actuatorElement, "entradas");
//			if( inputsElement != null )
//				loadBoxInputs(inputsElement, id, connectionIds, program);
//		}
//	}
//	
//	private static void loadBoxes(Element boxesElement, Vector<String[]> connectionIds, BehaviorProgram program) {
//		for( Element boxElement: XmlUtils.getChildrenByTagName(boxesElement,"caja") ) {
//			//FunctionBox func = new FunctionBox(boxElement);
//			FunctionBox func = new FunctionBox(null,null,null);
//			for( Element templateElement: XmlUtils.getChildrenByTagName(boxElement, "esquema") ) {
//				String templateId = templateElement.getAttribute("id");
//				FunctionTemplate template = GlobalConfig.getCurrentConfig().getFunctionTemplateById(templateId);
//				func.setTemplate(template);
//			}
//			program.addBox(func);
//			Element inputsElement = XmlUtils.getChildByTagName(boxElement,"entradas");
//			if( inputsElement != null )
//				loadBoxInputs(inputsElement, func.getId(), connectionIds, program);
//		}
//	}
//	
//	private static void loadBoxInputs(Element element, String destId, Vector<String[]> connectionIds, BehaviorProgram program) {
//		for( Element connectionElement: XmlUtils.getChildrenByTagName(element, "entrada") ) {
//			String sourceId = connectionElement.getAttribute("id");
//			connectionIds.add(new String[]{sourceId, destId});
//		}
//	}
//
//	private static void connectAfterLoad(Vector<String[]> connectionIds, BehaviorProgram program) {
//		for( String[] ids: connectionIds ) {
//			String srcId = ids[0];
//			String dstId = ids[1];
//			Box boxSrc = program.getBoxById(srcId);
//			Box boxDst = program.getBoxById(dstId);
//			program.addConnection(boxSrc, boxDst);
//			/*
//			if( !connections.containsKey(boxDst) )
//				connections.put(boxDst,new Vector<Box>());
//			connections.get(boxDst).add(boxSrc);
//			*/
//		}
//	}
//	
//	public static Element saveBox(Document doc, Box box) {
//		Element elem;  
//		if( box instanceof ActuatorBox )
//			elem = doc.createElement("actuador");
//		else if( box instanceof SensorBox )
//			elem = doc.createElement("sensor");
//		else if( box instanceof FunctionBox ) {
//			FunctionBox funcBox = (FunctionBox)box;
//			Element pointsElement, pointElement;
//			
//			elem = doc.createElement("caja");
//			pointsElement = doc.createElement("puntos");
//			
//			pointElement = doc.createElement("punto");
//			pointElement.setAttribute("x", Integer.toString(funcBox.getX0()));
//			pointElement.setAttribute("y", Integer.toString(funcBox.getY0()));
//			pointsElement.appendChild(pointElement);
//			
//			pointElement = doc.createElement("punto");
//			pointElement.setAttribute("x", Integer.toString(funcBox.getX1()));		
//			pointElement.setAttribute("y", Integer.toString(funcBox.getY1()));
//			pointsElement.appendChild(pointElement);
//			
//			if( funcBox.getTemplate() != null ) {
//				Element templateElement = doc.createElement("esquema");
//				templateElement.setAttribute("id", funcBox.getTemplate().getId());
//				elem.appendChild(templateElement);
//			}
//
//			elem.appendChild(pointsElement);
//		}
//		else
//			return null;
//		elem.setAttribute("id", box.getId());
//		return elem;
//	}	
//	
}
