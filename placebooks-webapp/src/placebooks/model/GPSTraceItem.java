package placebooks.model;

import placebooks.model.jaxb.GPX10.Gpx;

import placebooks.model.jaxb.GPX11.GpxType;
import placebooks.model.jaxb.GPX11.TrksegType;
import placebooks.model.jaxb.GPX11.WptType;
import placebooks.model.jaxb.GPX11.TrkType;

import placebooks.controller.EMFSingleton;
import placebooks.controller.EverytrailHelper;

import java.util.List;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@JsonIgnore
	@Lob
	private String trace;

	public GPSTraceItem(final User owner, final URL sourceURL, final String trace)
	{
		// Geometry is set from calculating the GPX boundaries
		super(owner, null, sourceURL);
		setTrace(trace);
	}

	GPSTraceItem()
	{
	}

	public GPSTraceItem(final GPSTraceItem g)
	{
		super(g);
		setTrace(new String(g.getTrace()));
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
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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

	@SuppressWarnings("unchecked")
	private void setTrace(final String trace)
	{
		if (trace == null)
		{
			this.trace = null;
			return;
		}

		this.trace = trace;

		Geometry bounds = null;
		float minLat = Float.POSITIVE_INFINITY;
		float maxLat = Float.NEGATIVE_INFINITY;
		float minLon = Float.POSITIVE_INFINITY;
		float maxLon = Float.NEGATIVE_INFINITY;

		final WKTReader wktReader = new WKTReader();

		try 
		{
			// GPX 1.1 spec

			GpxType gpx = null;
			Unmarshaller u = JAXBContext.newInstance("placebooks.model.jaxb.GPX11")
										.createUnmarshaller();
			JAXBElement<GpxType> root = 
				(JAXBElement<GpxType>)u.unmarshal(new StreamSource(
												  new StringReader(this.trace))
				);
			gpx = root.getValue();
			List<TrkType> tracks = gpx.getTrk();
			for (TrkType track : tracks) 
			{
				for (TrksegType seg : track.getTrkseg())
				{
					for (WptType wpt : seg.getTrkpt())
					{
						try
						{
							Geometry g = 
								wktReader.read(
									"POINT (" + wpt.getLat().floatValue() 
									+ " "
									+ wpt.getLon().floatValue() + ")");
							if (g != null)
							{
								if (bounds == null)
									bounds = g;
								else
									bounds = g.union(bounds);
							}
						}
						catch (final Throwable e)
						{
							log.error(e.toString());
						}
					}
				}
			}

		} 
		catch (final Throwable e) 
		{
			// GPX 1.0 spec
			log.error(e.toString());

			try 
			{
				Unmarshaller u = 
					JAXBContext.newInstance("placebooks.model.jaxb.GPX10")
							   .createUnmarshaller();
				// GPX 1.0 is anonymous
				Object root =
					u.unmarshal(new StreamSource(new StringReader(this.trace)));
				log.info(root.getClass());
				Gpx gpx = (Gpx)root;

				// Trk
				for (Gpx.Trk track : gpx.getTrk())
				{
					for (Gpx.Trk.Trkseg seg : track.getTrkseg())
					{
						for (Gpx.Trk.Trkseg.Trkpt pt : seg.getTrkpt())
						{
							minLat = Math.min(minLat, pt.getLat().floatValue());
							maxLat = Math.max(maxLat, pt.getLat().floatValue());
							minLon = Math.min(minLon, pt.getLon().floatValue());
							maxLon = Math.max(maxLon, pt.getLon().floatValue());
						}
					}
				}
				// Wpt
				for (Gpx.Wpt wpt : gpx.getWpt())
				{
					minLat = Math.min(minLat, wpt.getLat().floatValue());
					maxLat = Math.max(maxLat, wpt.getLat().floatValue());
					minLon = Math.min(minLon, wpt.getLon().floatValue());
					maxLon = Math.max(maxLon, wpt.getLon().floatValue());
				}
				// Rte
				for (Gpx.Rte rte : gpx.getRte())
				{
					for (Gpx.Rte.Rtept rpt : rte.getRtept())
					{
						minLat = Math.min(minLat, rpt.getLat().floatValue());
						maxLat = Math.max(maxLat, rpt.getLat().floatValue());
						minLon = Math.min(minLon, rpt.getLon().floatValue());
						maxLon = Math.max(maxLon, rpt.getLon().floatValue());
					}
				}

			} 
			catch (final Exception e_) 
			{
				log.error(e_.toString());
			}

			try
			{
				bounds = wktReader.read("POLYGON ((" + minLat + " " + minLon + ", "
										+ minLat + " " + maxLon + ", "
										+ maxLat + " " + maxLon + ", "
										+ maxLat + " " + minLon + ", "
										+ minLat + " " + minLon + "))");
			}
			catch (final Throwable e_)
			{
				log.error(e_.toString());
			}


		}
		setGeometry(bounds.getBoundary());
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
			GPSTraceItem gpsitem = (GPSTraceItem)item; 
			if(gpsitem.getTrace() != null && !gpsitem.getTrace().trim().equals(""))
			{
				setTrace(((GPSTraceItem) item).getTrace());
			}
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
