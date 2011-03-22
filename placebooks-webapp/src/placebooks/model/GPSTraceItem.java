package placebooks.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class GPSTraceItem extends PlaceBookItem
{
	private Document trace;

	public GPSTraceItem(int owner, Geometry geom, URL sourceURL, Document trace)
	{
		super(owner, geom, sourceURL);
		this.trace = trace;
	}

	public String getEntityName()
	{
		return GPSTraceItem.class.getName();
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		Element traceElem = 
			(Element)(trace.getElementsByTagName("gpx").item(0));
		Node traceNode = config.importNode(traceElem, true);
		item.appendChild(traceNode);
		root.appendChild(item);
	}

	@Persistent
	@Column(jdbcType="CLOB")
	public String getTrace()
	{
		try 
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(trace);

			StringWriter out = new StringWriter();
			StreamResult result =  new StreamResult(out);
			t.transform(source, result);
            return out.getBuffer().toString();

        } 
		catch (TransformerConfigurationException e) 
		{
            log.error(e.toString());
        }
		catch (TransformerException e) 
		{
            log.error(e.toString());
        }

        return null;
	}
	public void setTrace(String trace)
	{
		if (trace == null)
			return;
		try 
		{
			StringReader reader = new StringReader(trace);
			InputSource source = new InputSource(reader);
			DocumentBuilder builder = 
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.trace = builder.parse(source);
			reader.close();
		} 
		catch (ParserConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (SAXException e)
		{
			log.error(e.toString());
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
	}

}