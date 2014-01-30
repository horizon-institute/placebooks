package org.placebooks.activity.item;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.R;
import org.placebooks.client.model.Item;
import org.wornchaos.logger.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.widget.ImageView;

public class MapCanvas extends ImageView
{
	private Location location;

	private final Paint trailPen;

	private List<Item> mapItems = new ArrayList<Item>();
	private double minLat;
	private double maxLat;
	private double minLong;
	private double maxLong;
	
	
	public MapCanvas(final Context c)
	{
		super(c);

		trailPen = new Paint(Paint.ANTI_ALIAS_FLAG);
		trailPen.setStyle(Paint.Style.STROKE);
		trailPen.setStrokeWidth(6);
		trailPen.setColor(Color.BLUE); // color.RED 0xffff0000
	}

	public void addMapItem(final Item mapItem)
	{
		mapItems.add(mapItem);
		invalidate();
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

	@Override
	protected void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);

		// TODO
		// draw out the gps trail
		// for (int i = 1; i < gpsLatPx.size(); i++)
		// {
		// canvas.drawLine(gpsLonPx.get(i), gpsLatPx.get(i), gpsLonPx.get(i - 1), gpsLatPx.get(i -
		// 1), trailPen);
		// }

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
			//TODO drawBitmap(canvas, bitmap, location.getLatitude(), location.getLongitude());
		}
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
			return BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("marker" + (char)item.getParameters().get("marker").intValue(), "drawable", "org.placebooks"));
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
}