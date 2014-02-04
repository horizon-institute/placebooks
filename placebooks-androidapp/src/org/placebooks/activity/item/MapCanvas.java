package org.placebooks.activity.item;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.placebooks.R;
import org.placebooks.client.model.Item;
import org.wornchaos.logger.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.ImageView;

public class MapCanvas extends ImageView implements LocationListener
{
	private Location location;

	private final Paint trailPen;

	private final List<Item> mapItems = new ArrayList<Item>();
	private double minLat;
	private double maxLat;
	private double minLong;
	private double maxLong;
	private Bitmap bullet;
	private final List<List<PointF>> trails = new ArrayList<List<PointF>>();

	public MapCanvas(final Context c)
	{
		super(c);

		trailPen = new Paint(Paint.ANTI_ALIAS_FLAG);
		trailPen.setStyle(Paint.Style.STROKE);
		trailPen.setStrokeWidth(6);
		trailPen.setColor(Color.BLUE); // color.RED 0xffff0000

		bullet = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_blue);
	}

	public void addMapItem(final Item mapItem)
	{
		mapItems.add(mapItem);
		invalidate();
	}

	private void drawBitmap(final Canvas canvas, final Bitmap bitmap, final double latitude, final double longitude)
	{
		final float y = getOffset(latitude, maxLat, minLat, getHeight()) - (bitmap.getHeight());
		final float x = getOffset(longitude, minLong, maxLong, getWidth()) - (bitmap.getWidth() / 2);

		Log.info("Marker  at " + latitude + ", " + longitude);
		Log.info("Range " + minLat + "-" + maxLat + ", " + minLong + "-" + maxLong);
		Log.info("Drawing at " + x + ", " + y);

		canvas.drawBitmap(bitmap, x, y, null);
	}

	private void drawBitmap(final Canvas canvas, final Bitmap bitmap, final String geometry)
	{
		try
		{
			final int startBracket = geometry.lastIndexOf('(') + 1;
			final int endBracket = geometry.indexOf(')');
			final String coordString = geometry.substring(startBracket, endBracket);
			final String[] coords = coordString.split(" ");
			final double latitude = Double.parseDouble(coords[0]);
			final double longitude = Double.parseDouble(coords[1]);

			drawBitmap(canvas, bitmap, latitude, longitude);
		}
		catch (final Exception e)
		{
			Log.error(e);
		}
	}

	private Bitmap getBitmap(final Item item)
	{
		if (item.getParameters().containsKey("marker"))
		{
			return BitmapFactory.decodeResource(getResources(),
												getResources().getIdentifier(	"marker"
																						+ (char) item.getParameters()
																								.get("marker")
																								.intValue(),
																				"drawable", "org.placebooks"));
		}
		else
		{
			return BitmapFactory.decodeResource(getResources(), R.drawable.marker);
		}
	}

	private float getOffset(final double value, final double min, final double max, final int width)
	{
		return (float) ((value - min) / (max - min) * width);
	}

	@Override
	protected void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);

		if(trails != null)
		{
			for(List<PointF> trail: trails)
			{
				PointF lastPoint = null;
				for(PointF point: trail)
				{ 
					if(lastPoint != null)
					{
						canvas.drawLine(lastPoint.x * getWidth(), lastPoint.y * getHeight(), point.x * getWidth(), point.y * getHeight(), trailPen);		
					}
					
					lastPoint = point;
				}
			}
		}

		for (final Item item : mapItems)
		{
			final Bitmap marker = getBitmap(item);
			if (marker != null)
			{
				drawBitmap(canvas, marker, item.getGeom());
			}
		}

		if (location != null)
		{
			drawBitmap(canvas, bullet, location.getLatitude(), location.getLongitude());
		}
	}

	@Override
	public void onLocationChanged(final Location location)
	{
		this.location = location;
	}

	@Override
	public void onProviderDisabled(final String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(final String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(final String provider, final int status, final Bundle extras)
	{
		// TODO Auto-generated method stub

	}

	public void setGeometry(final String geometry)
	{
		minLat = Double.POSITIVE_INFINITY;
		maxLat = Double.NEGATIVE_INFINITY;
		minLong = Double.POSITIVE_INFINITY;
		maxLong = Double.NEGATIVE_INFINITY;
		final int startBracket = geometry.lastIndexOf('(') + 1;
		final int endBracket = geometry.indexOf(')');
		final String pointlist = geometry.substring(startBracket, endBracket);
		final String[] points = pointlist.split(",");
		for (final String point : points)
		{
			try
			{
				final String[] coords = point.trim().split(" ");
				final double lat = Double.parseDouble(coords[0]);
				final double lng = Double.parseDouble(coords[1]);
				minLat = Math.min(minLat, lat);
				maxLat = Math.max(maxLat, lat);
				minLong = Math.min(minLong, lng);
				maxLong = Math.max(maxLong, lng);
			}
			catch (final Exception e)
			{
				Log.error(e);
			}
		}
	}

	public void setLocation(final Location location)
	{
		this.location = location;
		invalidate();
	}

	public void setTrace(final String trace)
	{
		try
		{			
			trails.clear();
			if(trace == null)
			{
				return;
			}
			final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			final XmlPullParser parser = factory.newPullParser();	
			parser.setInput(new StringReader(trace));
			int eventType = parser.getEventType();
			List<PointF> trail = null;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					if(parser.getName().equals("trkseg"))
					{
						trail = new ArrayList<PointF>();
						trails.add(trail);
					}
					else if(parser.getName().equals("trkpt"))
					{
						if(trail != null)
						{
							final PointF point = new PointF();
							try
							{
								final double longitude = Double.parseDouble(parser.getAttributeValue(null, "lon"));
								point.x = getOffset(longitude, minLong, maxLong, 1);
							}
							catch(Exception e)
							{
								Log.error(e);
							}
							try
							{
								final double latitude = Double.parseDouble(parser.getAttributeValue(null, "lat"));
								point.y = getOffset(latitude, maxLat, minLat, 1);
							}
							catch(Exception e)
							{
								Log.error(e);
							}
							if(point.x != 0 && point.y != 0)
							{
								Log.info("Point "+ point.x + ", " + point.y);
								trail.add(point);
							}
						}
					}
				}
				eventType = parser.next();
			}
		}
		catch(Exception e)
		{
			Log.warn(e);
		}
	}
}
	