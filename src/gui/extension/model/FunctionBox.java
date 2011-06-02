package extension.model;

import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FunctionBox extends Box {
	private int x0, y0, x1, y1;
	private FunctionTemplate template;
	
	public FunctionBox(String id, int x0, int y0, int x1, int y1) {
		super(id);
		this.id = id;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.template = null;
	}
	public FunctionBox(FunctionTemplate template) {
		super(FunctionBox.getRandomId());
		this.template = template;
		this.x0 = template.x0;
		this.y0 = template.y0;
		this.x1 = template.x1;
		this.y1 = template.y1;
	}
	
	public FunctionBox(Element functionElement) {
		super(null);
		this.id = functionElement.getAttribute("id");
		Element pointsElement = (Element)(functionElement.getElementsByTagName("puntos").item(0));
		NodeList nl = pointsElement.getChildNodes();
		x0=x1=y0=y1=-1;
		for( int i=0; i<nl.getLength(); i++ ) {
			if( nl.item(i).getNodeType() != Node.ELEMENT_NODE )
				continue;
			Element elem = (Element)(nl.item(i));
			if( x0 == -1 ) {
				this.x0 = Integer.parseInt(elem.getAttribute("x"));
				this.y0 = Integer.parseInt(elem.getAttribute("y"));
			} else {
				this.x1 = Integer.parseInt(elem.getAttribute("x"));
				this.y1 = Integer.parseInt(elem.getAttribute("y"));
			}
		}		
	}
	
	public FunctionTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(FunctionTemplate template) {
		this.template = template;
	}
		
		
	private static String getRandomId() {
		String id = "caja.";
		Random rnd = new Random();
		for( int i=0; i<5; i++ )
			id = id + Integer.toString(rnd.nextInt(10));
		return id;
	}
	
	public boolean canConnectFrom( Box b ){
		return getTemplate().isAcceptInputs();
	}
	
	public String getType(){
		return "function";
	}
	
	public int getX0(){
		return x0;
	}
	
	public int getY0(){
		return y0;
	}
	
	public int getX1(){
		return x1;
	}
	
	public int getY1(){
		return y1;
	}
	
	public void setX0( int valor ){
		x0 = valor;
	}
	
	public void setY0( int valor ){
		y0 = valor;
	}
	
	public void setX1( int valor ){
		x1 = valor;
	}
	
	public void setY1( int valor ){
		y1 = valor;
	}
}
