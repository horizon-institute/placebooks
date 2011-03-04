package placebooks.model;

import java.net.URL;
import java.io.*;

import javax.jdo.annotations.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.apache.log4j.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
public class GPSTraceItem extends PlaceBookItem
{
	private static final Logger log = 
		Logger.getLogger(GPSTraceItem.class.getName());

	private Document trace;

	public GPSTraceItem() { }

	public GPSTraceItem(int owner, Geometry geom, URL sourceURL, Document trace)
	{
		super(owner, geom, sourceURL);
		this.trace = trace;
	}

	@Persistent
	@Column(sqlType="TEXT")
	public String getTrace()
	{
		if (trace == null)
			return null;

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

	@Persistent
	public void setTrace(String trace)
	{
		try 
		{
			StringReader reader = new StringReader(trace);
			InputSource source = new InputSource(reader);
			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
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

}
