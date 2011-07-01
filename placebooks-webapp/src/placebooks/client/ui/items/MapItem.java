package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.openlayers.Bounds;
import placebooks.client.ui.openlayers.ClickControl;
import placebooks.client.ui.openlayers.Event;
import placebooks.client.ui.openlayers.EventHandler;
import placebooks.client.ui.openlayers.LonLat;
import placebooks.client.ui.openlayers.Map;
import placebooks.client.ui.openlayers.Marker;
import placebooks.client.ui.openlayers.MarkerLayer;
import placebooks.client.ui.openlayers.OSMLayer;
import placebooks.client.ui.openlayers.Projection;
import placebooks.client.ui.openlayers.RouteLayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemWidget
{
	private final static Projection LATLON_PROJECTION = Projection.create("ESPG:4326");

	private final static String POINT_PREFIX = "POINT (";

	private final Label interactionLabel = new Label();

	private EventHandler loadHandler;

	private final Map map;

	private MarkerLayer markerLayer;

	private SimplePanel panel = new SimplePanel();

	private PlaceBookItemWidget positionItem = null;

	private RouteLayer routeLayer;

	private String url;

	MapItem(final PlaceBookItem item)
	{
		super(item);
		initWidget(panel);
		interactionLabel.setStyleName(Resources.INSTANCE.style().mapLabel());
		panel.add(interactionLabel);
		interactionLabel.setVisible(false);
		map = Map.create(getElement());

		final ClickControl control = ClickControl.create(new EventHandler()
		{
			@Override
			protected void handleEvent(final Event event)
			{
				final LonLat lonLat = map.getLonLatFromPixel(event.getXY()).transform(map.getProjection(),
																						LATLON_PROJECTION);
				GWT.log("Clicked at " + lonLat.getLat() + "N, " + lonLat.getLon() + "E");
				if (positionItem != null)
				{
					positionItem.getItem().setGeometry(POINT_PREFIX + lonLat.getLat() + " " + lonLat.getLon() + ")");
				}
			}
		}.getFunction());
		map.addControl(control);				
		control.activate();

		map.addLayer(OSMLayer.create("Osmarender"));
//		try
//		{
//			map.setCenter(LonLat.create(-1.18f, 52.95f), 13);
//		}
//		catch (final Exception e)
//		{
//			GWT.log(e.getMessage(), e);
//		}
	}

	@Override
	public void refresh()
	{
		final String newURL = getItem().getURL();
		if (url == null || !url.equals(newURL) || routeLayer == null)
		{
			this.url = newURL;
			createDataLayers();
		}
		recenter();
	}

	public void refreshMarkers(final Iterable<PlaceBookItemWidget> items)
	{
		if (markerLayer != null)
		{
			markerLayer.clearMarkers();
			positionItem = null;
			interactionLabel.setVisible(false);
			for (final PlaceBookItemWidget item : items)
			{
				if (item.getItem().hasMetadata("mapItemID") && item.getItem().getMetadata("mapItemID").equals(getItem().getKey()))
				{
					if (item.getItem().getGeometry() != null)
					{
						final String geometry = item.getItem().getGeometry();
						if (geometry.startsWith(POINT_PREFIX))
						{
							final String latLong = geometry.substring(POINT_PREFIX.length(), geometry.length() - 1);
							final int comma = latLong.indexOf(" ");
							final float lat = Float.parseFloat(latLong.substring(0, comma));
							final float lon = Float.parseFloat(latLong.substring(comma + 1));
							final Marker marker = Marker.create(LonLat.create(lon, lat)
									.transform(LATLON_PROJECTION, map.getProjection()));
							// marker.getEvents().register("click", marker, new EventHandler()
							// {
							// @Override
							// void handleEvent(Event event)
							// {
							// GWT.log("Clickedx at " + event.getX() + "," + event.getY());
							// }
							// }.getFunction());
							markerLayer.addMarker(marker);
							GWT.log("Added marker for " + item.getItem().getKey() + " at " + lat + ", " + lon);
						}
					}
					else
					{
						GWT.log("No geometry for " + item.getItem().getKey());
						positionItem = item;
						interactionLabel.setText("Set position of item " + item.getItem().getKey());
						interactionLabel.setVisible(true);
					}
				}
			}
		}
		recenter();
	}

	private void createDataLayers()
	{
		if (routeLayer != null)
		{
			map.removeLayer(routeLayer);
		}

		if (markerLayer != null)
		{
			map.removeLayer(markerLayer);
		}

		routeLayer = RouteLayer.create("Route", getItem().getURL(), LATLON_PROJECTION);
		try
		{
			if (loadHandler != null)
			{
				routeLayer.getEvents().register("loadend", routeLayer, loadHandler.getFunction());
			}
			routeLayer.setVisible(Boolean.parseBoolean(item.getMetadata("", "")));
			map.addLayer(routeLayer);
		}
		catch (final Exception e)
		{
			routeLayer = null;
			GWT.log(e.getMessage(), e);
		}

		markerLayer = MarkerLayer.create("markerLayer");

		try
		{
			map.addLayer(markerLayer);
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	private void recenter()
	{
		try
		{
			Bounds bounds = null;
			if (markerLayer != null)
			{
				bounds = markerLayer.getDataExtent();
			}
			if (routeLayer != null)
			{
				if (bounds == null)
				{
					bounds = routeLayer.getDataExtent();
				}
				else
				{
					bounds.extend(routeLayer.getDataExtent());
				}
			}
			if (bounds != null)
			{
				map.zoomToExtent(bounds);
			}
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
}