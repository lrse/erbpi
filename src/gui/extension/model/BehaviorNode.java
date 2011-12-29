package extension.model;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BehaviorNode
{
	private Program program = null;
	private Point position = null;
	
	public BehaviorNode( Program program, Point position )
	{
		this.program=program;
		this.position=position;
	}
	
	public BehaviorNode( Element domSpec, Robot robot, Program program )
	{
		deserialize(domSpec,robot);
		
		this.program = program;
	}
	
	private void deserialize( Element domSpec, Robot robot )
	{
		this.position = new Point(
			Integer.parseInt(domSpec.getAttribute("pos_x")),
			Integer.parseInt(domSpec.getAttribute("pos_y"))
		);
	}
	
	public Element serialize(Document document)
	{
		Element behaviorElement = this.program.serialize(document);
		serializePosition(behaviorElement);
		
		return behaviorElement;
	}
	
	private void serializePosition(Element behaviorElement)
	{
		behaviorElement.setAttribute("pos_x", String.valueOf(position.x));
		behaviorElement.setAttribute("pos_y", String.valueOf(position.y));
	}
	
	public Program getProgram() { return this.program; }
	public void setProgram( BehaviorProgram programa ) { this.program = programa; }
	public Point getPosition() { return this.position; }
	public String getDescripcion() { return program.getDescription(); }
	public void setDescription(String description) { program.setDescription(description); }
	public String getId() { return program.getId(); }
}
