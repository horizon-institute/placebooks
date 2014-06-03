package org.placebooks.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.wornchaos.logger.Log;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

@Entity
public class GPSTraceItem extends PlaceBookItem
{
	@Lob
	private String text;

	GPSTraceItem()
	{
	}

	public GPSTraceItem(final GPSTraceItem g)
	{
		super(g);
		if (g.getText() != null)
		{
			setText(new String(g.getText()));
		}
		else
		{
			setText(null);
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
		setText(trace);
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

	public String getText()
	{
		return text;
	}

	public void readText(final InputStream is) throws Exception
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

		setText(writer.toString());
	}

	private void setText(final String trace)
	{
		if (trace == null)
		{
			this.text = null;
			return;
		}
		
		this.text = trace;

		Geometry bounds = null;
		float minLat = Float.POSITIVE_INFINITY;
		float maxLat = Float.NEGATIVE_INFINITY;
		float minLon = Float.POSITIVE_INFINITY;
		float maxLon = Float.NEGATIVE_INFINITY;

		final XMLInputFactory factory = XMLInputFactory.newInstance();
		try
		{
			final XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(trace));

			while (reader.hasNext())
			{
				final int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT)
				{
					if (reader.getName().toString().endsWith("pt"))
					{
						try
						{
							final float latitude = Float.parseFloat(reader.getAttributeValue(null, "lat"));
							minLat = Math.min(minLat, latitude);
							maxLat = Math.max(maxLat, latitude);
						}
						catch (final Exception e)
						{

						}
						try
						{
							final float longitude = Float.parseFloat(reader.getAttributeValue(null, "lon"));
							minLon = Math.min(minLon, longitude);
							maxLon = Math.max(maxLon, longitude);
						}
						catch (final Exception e)
						{

						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			Log.error(e);
		}

		final WKTReader wktReader = new WKTReader();
		try
		{

			if (minLat == Float.POSITIVE_INFINITY || maxLat == Float.NEGATIVE_INFINITY
					|| minLon == Float.POSITIVE_INFINITY || maxLon == Float.NEGATIVE_INFINITY)
			{
				Log.error("Warning: calculated bounds were not valid, ignoring");
				setGeometry(null);
			}
			else
			{

				Log.info("Creating bounds: " + minLat + ", " + minLon + ",  " + maxLat + ",  " + maxLon);
				bounds = wktReader.read("POLYGON ((" + minLat + " " + minLon + ", " + minLat + " " + maxLon + ", "
						+ maxLat + " " + maxLon + ", " + maxLat + " " + minLon + ", " + minLat + " " + minLon + "))");
			}
		}
		catch (final Throwable e_)
		{
			Log.error("Fatal error in calculating bounds for GPX");
			Log.error(e_.toString(), e_);
			return;
		}
		if (bounds != null)
		{
			setGeometry(bounds.getBoundary());
		}
		else
		{
			Log.error("Bounds was null");
		}
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.model.PlaceBookItem#udpate(PlaceBookItem)
	 */
	public void updateItem(final PlaceBookItem updateItem)
	{
		final GPSTraceItem item = (GPSTraceItem) updateItem;
		super.updateItem(item);
		if (item instanceof GPSTraceItem)
		{
			final GPSTraceItem gpsitem = item;
			if (gpsitem.getText() != null && !gpsitem.getText().trim().equals(""))
			{
				setText((item).getText());
			}
		}
	}
}