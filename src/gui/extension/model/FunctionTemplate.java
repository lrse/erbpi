package extension.model;

import java.net.URL;

import javax.swing.Icon;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import extension.ExtensionApp;
import extension.utils.IconBank;

public class FunctionTemplate
{
	public Icon icon;
	public String id;
	public int x0,y0,x1,y1;
	public boolean acceptInputs;
	
	public FunctionTemplate(String id, Icon icon, int x0, int y0, int x1, int y1)
	{
		this.id = id;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.icon = icon;
		this.acceptInputs = true;
	}
	
	public FunctionTemplate(Element elem) {
		this.icon = null;
		this.x0 = this.y0 = this.x1 = this.y1 = -1;

		this.id = elem.getAttribute("id");
		if( elem.hasAttribute("aceptaentradas") )
			this.acceptInputs = elem.getAttribute("aceptaentradas").toLowerCase().equals("true");
		else
			this.acceptInputs = true;
		
		NodeList nl2 = elem.getChildNodes();
		for( int j=0; j<nl2.getLength(); j++ ) {
			if( nl2.item(j).getNodeType() != Node.ELEMENT_NODE )
				continue;
			
			Element elem2 = (Element)(nl2.item(j));
			if( elem2.getTagName().equals("imagen") ) {
				URL imageUrl  = ExtensionApp.class.getResource(elem2.getAttribute("href"));
				this.icon = IconBank.getByUrl(imageUrl, 40, 40);
			} else if( elem2.getTagName().equals("puntos") ) {
				NodeList nl3 = elem2.getChildNodes();
				for( int k=0; k<nl3.getLength(); k++ ) {
					if( nl3.item(k).getNodeType() != Node.ELEMENT_NODE )
						continue;
					Element elem3 = (Element)(nl3.item(k));
					if( this.x0 == -1) {
						this.x0 = Integer.parseInt(elem3.getAttribute("x"));
						this.y0 = Integer.parseInt(elem3.getAttribute("y"));
					} else {
						this.x1 = Integer.parseInt(elem3.getAttribute("x"));						
						this.y1 = Integer.parseInt(elem3.getAttribute("y"));
					}
				}	
			}
		}		
	}
	
	public Icon getIcon()			{ return icon; }
	public String getId()			{ return id; }
	public boolean acceptInputs()	{ return acceptInputs; }
}
