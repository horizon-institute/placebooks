package placebooks.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@JsonIgnore
	private Document trace;

	public GPSTraceItem(final User owner, final Geometry geom, final URL sourceURL, final Document trace)
	{
		super(owner, geom, sourceURL);
		this.trace = trace;
	}

	GPSTraceItem()
	{
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		getTrace(); // TODO: why does this need to be done??

		final Element item = getConfigurationHeader(config);
		final Element traceElem = (Element) (trace.getElementsByTagName("gpx").item(0));
		final Node traceNode = config.importNode(traceElem, true);
		item.appendChild(traceNode);
		root.appendChild(item);
	}

	@Override
	public void deleteItemData()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getEntityName()
	{
		return GPSTraceItem.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
	}

	// @Persistent
	// @Column(jdbcType = "CLOB")
	public String getTrace()
	{
		try
		{
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer t = tf.newTransformer();
			final DOMSource source = new DOMSource(trace);

			final StringWriter out = new StringWriter();
			final StreamResult result = new StreamResult(out);
			t.transform(source, result);
			return out.getBuffer().toString();

		}
		catch (final TransformerConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (final TransformerException e)
		{
			log.error(e.toString());
		}

		return null;
	}

	public void setTrace(final String trace)
	{
		if (trace == null) { return; }
		try
		{
			final StringReader reader = new StringReader(trace);
			final InputSource source = new InputSource(reader);
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.trace = builder.parse(source);
			reader.close();
		}
		catch (final ParserConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (final SAXException e)
		{
			log.error(e.toString());
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
	}

}
