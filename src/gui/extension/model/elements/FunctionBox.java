package extension.model.elements;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.model.BehaviorProgram;
import extension.model.FunctionTemplate;
import extension.model.GlobalConfig;
import extension.model.Panel.Location;

public class FunctionBox extends Box
{
	static private int nextId = 0;
	
	private int x0, y0, x1, y1;
	private FunctionTemplate template;
	
	public FunctionBox(FunctionTemplate template, BehaviorProgram program, Location location)
	{
		super("function."+nextId,program,location);
		
		nextId++;
		
		this.x0 = template.x0;
		this.y0 = template.y0;
		this.x1 = template.x1;
		this.y1 = template.y1;
		
		this.template = template;
	}
	
	public FunctionBox(Element domSpec,BehaviorProgram program)
	{
		super(domSpec,program);
	}

	public FunctionTemplate getTemplate()				{ return template; }
	public void setTemplate(FunctionTemplate template)	{ this.template = template; }
	
	public int getX0(){ return x0; }
	public int getY0(){ return y0; }
	public int getX1(){ return x1; }
	public int getY1(){ return y1; }
	
	public void setX0( int valor ){ x0 = valor; }
	public void setY0( int valor ){ y0 = valor; }
	public void setX1( int valor ){ x1 = valor; }
	public void setY1( int valor ){ y1 = valor; }
	
	@Override public String getType()					{ return "function"; }
	@Override public boolean showsHighlight()			{ return false; }
	@Override public Icon getImage()					{ return getTemplate().getIcon(); }
	@Override protected boolean canConnectFrom( Box b )	{ return getTemplate().acceptInputs(); }
	
	@Override
	public Element serialize(Document document)
	{
		Element functionElement = document.createElement("caja");
		functionElement.setAttribute("id", getId());
		functionElement.setAttribute("templateId", template.getId());
		functionElement.setAttribute("location", serializeLocation());
		
		Element schemeElement = document.createElement("esquema");
		schemeElement.setAttribute("id", "energia");
		functionElement.appendChild( schemeElement );
		
		// TODO para evitar boludeces los podriamos llamar punto1 y punto2,
		// pero hay que cambiarlo en el core tambien. Es una boludez.
		
		Element pointElement_0 = document.createElement("punto");
		pointElement_0.setAttribute("x", String.valueOf(this.x0));
		pointElement_0.setAttribute("y", String.valueOf(this.y0));
		functionElement.appendChild( pointElement_0 );
		
		Element pointElement_1 = document.createElement("punto");
		pointElement_1.setAttribute("x", String.valueOf(this.x1));
		pointElement_1.setAttribute("y", String.valueOf(this.y1));
		functionElement.appendChild( pointElement_1 );
		
		// serialize entries
		// TODO estas conecciones deberian estar definidas afuera
		// y el core deberia meterlas despues en sus cajas si quiere optimizar
		
		Element entriesElement = document.createElement("entradas");
		functionElement.appendChild( entriesElement );
		
		for ( Box input : program.getConnectionsTo(this) )
		{
			Element entryElement = document.createElement("entrada");
			entriesElement.appendChild( entryElement );
			
			entryElement.setAttribute("id", input.getId());
		}
		
		return functionElement;
	}

	@Override
	public void deserialize(Element domSpec)
	{
		super.deserialize(domSpec);
		
		Element domPunto1 = ((Element)domSpec.getElementsByTagName("punto").item(0));
		Element domPunto2 = ((Element)domSpec.getElementsByTagName("punto").item(1));
		
		this.x0 = Integer.parseInt(domPunto1.getAttribute("x"));
		this.y0 = Integer.parseInt(domPunto1.getAttribute("y"));
		this.x1 = Integer.parseInt(domPunto2.getAttribute("x"));
		this.y1 = Integer.parseInt(domPunto2.getAttribute("y"));
		
		String templateId = domSpec.getAttribute("templateId");
		this.template = GlobalConfig.getCurrentConfig().getFunctionTemplateById(templateId);
	}
}
