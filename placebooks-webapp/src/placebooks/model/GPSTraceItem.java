package placebooks.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import placebooks.controller.EMFSingleton;
import placebooks.controller.EverytrailHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@JsonIgnore
	@Lob
	private String trace;

	public GPSTraceItem(final User owner, final Geometry geom, 
						final URL sourceURL, final String trace)
	{
		super(owner, geom, sourceURL);
		this.trace = trace;
	}

	GPSTraceItem()
	{
	}

	public GPSTraceItem(final GPSTraceItem g)
	{
		super(g);
		this.trace = new String(g.getTrace());
	}
	
	@Override
	public GPSTraceItem deepCopy()
	{
		return new GPSTraceItem(this);
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		try
		{
			final StringReader reader = new StringReader(trace);
			final InputSource source = new InputSource(reader);
			final DocumentBuilder builder = 
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(source);
			reader.close();

			final Element item = getConfigurationHeader(config);
			final Element data = config.createElement("data");
			final Node traceNode = 
				config.importNode(document.getDocumentElement(), true);
			data.appendChild(traceNode);
			item.appendChild(data);
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

	public void readTrace(final InputStream is) throws Exception
	{
		final InputStreamReader reader = new InputStreamReader(is);
		final StringWriter writer = new StringWriter();
		int data;
		while((data = reader.read()) != -1)
		{
			writer.write(data);
		}
		reader.close();
		writer.close();
		
		log.info("Got gps from url " + getSourceURL());
		
		setTrace(writer.toString());
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

	@Override
	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#udpate(PlaceBookItem)
	 */
	public void updateItem(PlaceBookItem item)
	{
		super.updateItem(item);
		if(item instanceof GPSTraceItem)
		{
			setTrace(((GPSTraceItem) item).getTrace());
		}
	}


	
	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
	 */
	@Override
	public PlaceBookItem saveUpdatedItem()
	{
		PlaceBookItem returnItem = this;
		final EntityManager pm = EMFSingleton.getEntityManager();
		GPSTraceItem item;
		try
		{
			pm.getTransaction().begin();
			item = (GPSTraceItem) EverytrailHelper.GetExistingItem(this, pm);
			if(item != null)
			{
				
				log.debug("Existing item found so updating");
				item.update(this);
				returnItem = item;
				pm.flush();
			}
			else
			{
				log.debug("No existing item found so creating new");
				pm.persist(this);
			}
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current delete all transaction back");
			}
		}
		return returnItem;
	}
}
