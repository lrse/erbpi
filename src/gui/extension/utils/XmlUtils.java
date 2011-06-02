package extension.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {
	public static Document getDocumentFromStream(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(is);
        return doc;
	}
	public static Element getChildByTagName(Element elem, String tag) {
		NodeList nl;		
		nl = elem.getElementsByTagName(tag);
		if( nl.getLength()>0 ) 
			return (Element)(nl.item(0));
		else
			return null;
	}
	public static Iterable<Element> getChildrenByTagName(Element elem, String tag) {
		Vector<Element> answer = new Vector<Element>();
		NodeList nl;
		nl = elem.getChildNodes();
		for( int i=0; i<nl.getLength(); i++ ) {
			if( nl.item(i).getNodeType() != Node.ELEMENT_NODE )
				continue;
			Element child = ((Element)(nl.item(i)));
			if( tag.equals("*") || child.getTagName().equals(tag) )
				answer.add(child);
		}
		return answer;
	}
}
