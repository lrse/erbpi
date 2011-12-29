package extension.utils;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import extension.model.persist.ProgramXml;

public class XmlUtils
{
	private static DocumentBuilder docBuilder;;
	
	static
	{
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch( ParserConfigurationException e) {
			System.err.println("docBuilder not created. ParserConfigurationException.");
			docBuilder = null;
		}
	}
	
	public static Document getDocumentFromStream(InputStream is) throws SAXException, IOException
	{
        return docBuilder.parse(is);
	}
	
	public static boolean writeDocumentToStream(Document doc, OutputStream os)
	{
		// Check if DOM Load and Save is supported
		
		if (!( (doc.getFeature("Core", "3.0") != null) && (doc.getFeature("LS", "3.0") != null) ))
			throw new RuntimeException("DOM Load and Save unsupported");
		
		// Grab the available implementation
		
		DOMImplementationLS DOMiLS = (DOMImplementationLS) (doc.getImplementation()).getFeature("LS", "3.0");
		
		// Create LS output destination
		
		LSOutput lso = DOMiLS.createLSOutput();
		lso.setByteStream(os);
		
		// Create a LS serializer
		// and tell it to make the output 'pretty'
		
		LSSerializer serializer = DOMiLS.createLSSerializer();
		serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
		
		// Serialize the xml to your output stream
		
		return serializer.write(doc, lso);
	}
	
	public static Document getNewDocument()
	{
        return docBuilder.newDocument();
	}
	
	public static Element getFirstChildByTagName(Element elem, String name)
	{
		for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling())
			if (child instanceof Element && name.equals(child.getNodeName()))
				return (Element) child;
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
	
	private static JFileChooser getXMLfileChooser()
	{
		JFileChooser fc = new JFileChooser(new File("../comportamientos"));
		
		// set ExtensionFile...
		fc.setMultiSelectionEnabled(false);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter( "XML", "xml" );
		fc.setFileFilter(filter);
		
		return fc;
	}
	
	public static Element openFileDialog(Component parent)
	{
		JFileChooser fc = getXMLfileChooser();
		int returnVal = fc.showOpenDialog(parent);
	
		if ( returnVal == JFileChooser.APPROVE_OPTION )
		{
			File file = fc.getSelectedFile();
			
			try {
				return ProgramXml.load(file);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void saveFileDialog(Component parent, Document document)
	{
		JFileChooser fc = getXMLfileChooser();
		int returnVal = fc.showSaveDialog(parent);
	
		if ( returnVal == JFileChooser.APPROVE_OPTION )
		{
			File file = fc.getSelectedFile();
			
			// chequeo la extension XML...
			String filePath = file.getPath();
			if( !filePath.toLowerCase().endsWith(".xml") ){
				file = new File(filePath + ".xml");
			}
			
			try
			{
				OutputStream out = new FileOutputStream(file);
				
				XmlUtils.writeDocumentToStream(document, out);
				
				// throws IOException
				out.close();
			}
			catch (IOException e)
			{
				System.err.println("IO exception saving file!!!");
				e.printStackTrace();
			}
		}
	}
	
	public static File saveTemporalFile(Document document) throws IOException
	{
		// throws IOException
		File file = File.createTempFile("prg-", ".xml");
		
		OutputStream out = new FileOutputStream(file);
		
		XmlUtils.writeDocumentToStream(document, out);
		
		// throws IOException
		out.close();
		
		return file;
	}
}
