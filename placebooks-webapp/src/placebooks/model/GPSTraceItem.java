package placebooks.model;

import java.io.StringReader;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@JsonIgnore
	@Lob
	private String trace;

	public GPSTraceItem(final User owner, final Geometry geom, final URL sourceURL, final String trace)
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
		try
		{
			final StringReader reader = new StringReader(trace);
			final InputSource source = new InputSource(reader);
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(source);
			reader.close();

			final Element item = getConfigurationHeader(config);
			final Element traceElem = (Element) (document.getElementsByTagName("gpx").item(0));
			final Node traceNode = config.importNode(traceElem, true);
			item.appendChild(traceNode);
			root.appendChild(item);
		}
		catch (Exception e)
		{
			log.info(e.getMessage(), e);
		}
	}

	@Override
	public void deleteItemData()
	{
	}

	@Override
	public String getEntityName()
	{
		return GPSTraceItem.class.getName();
	}

	// @Persistent
	// @Column(jdbcType = "CLOB")
	public String getTrace()
	{
		return trace;
	}

	public void setTrace(final String trace)
	{
		this.trace = trace;
	}
}
