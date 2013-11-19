package org.placebooks.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.placebooks.controller.CommunicationHelper;
import org.placebooks.model.jaxb.GPX10.Gpx;
import org.placebooks.model.jaxb.GPX11.GpxType;
import org.placebooks.model.jaxb.GPX11.RteType;
import org.placebooks.model.jaxb.GPX11.TrkType;
import org.placebooks.model.jaxb.GPX11.TrksegType;
import org.placebooks.model.jaxb.GPX11.WptType;
import org.placebooks.model.json.JsonIgnore;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@JsonIgnore
	@Lob
	private String trace;

	private String hash;

	public GPSTraceItem(final GPSTraceItem g)
	{
		super(g);
		if (g.getTrace() != null)
		{
			setTrace(new String(g.getTrace()));
		}
		else
		{
			setTrace(null);
		}
	}

	public GPSTraceItem(final User owner)
	{
		// Geometry is set from calculating the GPX boundaries
		super();
		setOwner(owner);
	}

	public GPSTraceItem(final User owner, final URL sourceURL, final String trace)
	{
		// Geometry is set from calculating the GPX boundaries
		super(owner, null, sourceURL);
		setTrace(trace);
	}

	GPSTraceItem()
	{
	}

	@Override
	public void copyDataToPackage()
	{
	}

	@Override
	public GPSTraceItem deepCopy()
	{
		return new GPSTraceItem(this);
	}

	@Override
	public boolean deleteItemData()
	{
		return true;
	}

	@Override
	public String getEntityName()
	{
		return GPSTraceItem.class.getName();
	}

	public String getHash()
	{
		return hash;
	}

	// @Persistent
	// @Column(jdbcType = "CLOB")
	public String getTrace()
	{
		if (trace == null && getSourceURL() != null)
		{
			log.warn("Attempting to redownload GPSTraceItem content for " + getKey() + " from URL " + getSourceURL());
			try
			{
				readTrace(CommunicationHelper.getConnection(getSourceURL()).getInputStream());
			}
			catch (final Throwable e)
			{
				log.error(e.toString());
			}
		}

		return trace;
	}

	public void readTrace(final InputStream is) throws Exception
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		final StringWriter writer = new StringWriter();
		int data;
		while ((data = reader.read()) != -1)
		{
			writer.write(data);
		}
		reader.close();
		writer.close();

		setTrace(writer.toString());
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#udpate(PlaceBookItem)
	 */
	public void updateItem(final PlaceBookItem updateItem)
	{
		final GPSTraceItem item = (GPSTraceItem) updateItem;
		super.updateItem(item);
		if (item instanceof GPSTraceItem)
		{
			final GPSTraceItem gpsitem = item;
			if (gpsitem.getTrace() != null && !gpsitem.getTrace().trim().equals(""))
			{
				setTrace((item).getTrace());
			}
		}
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
			final Unmarshaller u = JAXBContext.newInstance("org.placebooks.model.jaxb.GPX11").createUnmarshaller();
			final JAXBElement<GpxType> root = (JAXBElement<GpxType>) u.unmarshal(new StreamSource(new StringReader(
					this.trace)));
			gpx = root.getValue();
			for (final TrkType track : gpx.getTrk())
			{
				for (final TrksegType seg : track.getTrkseg())
				{
					for (final WptType wpt : seg.getTrkpt())
					{
						minLat = Math.min(minLat, wpt.getLat().floatValue());
						maxLat = Math.max(maxLat, wpt.getLat().floatValue());
						minLon = Math.min(minLon, wpt.getLon().floatValue());
						maxLon = Math.max(maxLon, wpt.getLon().floatValue());
					}
				}
			}
			// Wpt
			for (final WptType wpt : gpx.getWpt())
			{
				minLat = Math.min(minLat, wpt.getLat().floatValue());
				maxLat = Math.max(maxLat, wpt.getLat().floatValue());
				minLon = Math.min(minLon, wpt.getLon().floatValue());
				maxLon = Math.max(maxLon, wpt.getLon().floatValue());
			}
			// Rte
			for (final RteType rte : gpx.getRte())
			{
				for (final WptType wpt : rte.getRtept())
				{
					minLat = Math.min(minLat, wpt.getLat().floatValue());
					maxLat = Math.max(maxLat, wpt.getLat().floatValue());
					minLon = Math.min(minLon, wpt.getLon().floatValue());
					maxLon = Math.max(maxLon, wpt.getLon().floatValue());
				}
			}
			log.info("Read track as GPX 1.1");
		}
		catch (final Throwable e)
		{
			// GPX 1.0 spec
			log.info("Failed to read GPX as GPX1.1, trying 1.0");
			try
			{
				final Unmarshaller u = JAXBContext.newInstance("placebooks.model.jaxb.GPX10").createUnmarshaller();
				// GPX 1.0 is anonymous
				final Object root = u.unmarshal(new StreamSource(new StringReader(this.trace)));
				log.info(root.getClass());
				final Gpx gpx = (Gpx) root;

				// Trk
				for (final Gpx.Trk track : gpx.getTrk())
				{
					for (final Gpx.Trk.Trkseg seg : track.getTrkseg())
					{
						for (final Gpx.Trk.Trkseg.Trkpt pt : seg.getTrkpt())
						{
							minLat = Math.min(minLat, pt.getLat().floatValue());
							maxLat = Math.max(maxLat, pt.getLat().floatValue());
							minLon = Math.min(minLon, pt.getLon().floatValue());
							maxLon = Math.max(maxLon, pt.getLon().floatValue());
						}
					}
				}
				// Wpt
				for (final Gpx.Wpt wpt : gpx.getWpt())
				{
					minLat = Math.min(minLat, wpt.getLat().floatValue());
					maxLat = Math.max(maxLat, wpt.getLat().floatValue());
					minLon = Math.min(minLon, wpt.getLon().floatValue());
					maxLon = Math.max(maxLon, wpt.getLon().floatValue());
				}
				// Rte
				for (final Gpx.Rte rte : gpx.getRte())
				{
					for (final Gpx.Rte.Rtept rpt : rte.getRtept())
					{
						minLat = Math.min(minLat, rpt.getLat().floatValue());
						maxLat = Math.max(maxLat, rpt.getLat().floatValue());
						minLon = Math.min(minLon, rpt.getLon().floatValue());
						maxLon = Math.max(maxLon, rpt.getLon().floatValue());
					}
				}
				log.info("Read track as GPX 1.0");
			}
			catch (final Exception e_)
			{
				log.error("Fatal error in reading GPX file");
				log.error(e_.toString(), e_);
				return;
			}
		}
		try
		{

			if (minLat == Float.POSITIVE_INFINITY || maxLat == Float.NEGATIVE_INFINITY
					|| minLon == Float.POSITIVE_INFINITY || maxLon == Float.NEGATIVE_INFINITY)
			{
				log.error("Warning: calculated bounds were not valid, ignoring");
				setGeometry(null);
			}
			else
			{

				log.info("Creating bounds: " + minLat + ", " + minLon + ",  " + maxLat + ",  " + maxLon);
				bounds = wktReader.read("POLYGON ((" + minLat + " " + minLon + ", " + minLat + " " + maxLon + ", "
						+ maxLat + " " + maxLon + ", " + maxLat + " " + minLon + ", " + minLat + " " + minLon + "))");
			}
		}
		catch (final Throwable e_)
		{
			log.error("Fatal error in calculating bounds for GPX");
			log.error(e_.toString(), e_);
			return;
		}
		if (bounds != null)
		{
			setGeometry(bounds.getBoundary());
		}
		else
		{
			log.error("Bounds was null");
		}

		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(trace.getBytes());

			hash = String.format("%032x", new BigInteger(1, md.digest()));
		}
		catch (final Exception e)
		{
			log.error("Failed to hash gpx", e);
		}
	}

}
