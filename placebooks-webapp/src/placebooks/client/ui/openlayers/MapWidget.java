package placebooks.client.ui.openlayers;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookItemWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapWidget extends SimplePanel
{
	private final static Projection latLonProjection = Projection.create("ESPG:4326");

	private final static String POINT_PREFIX = "POINT (";

	// private final PlaceBookCanvas canvas;

	private final String id;

	private final Label interactionLabel = new Label();

	private EventHandler loadHandler;

	private Map map;

	private MarkerLayer markerLayer;

	private PlaceBookItemWidget positionItem = null;

	private RouteLayer routeLayer;

	private String url;

	private boolean visible = true;

	public MapWidget(final String id)// , final PlaceBookCanvas canvas)
	{
		getElement().setId("mapPanel" + id);
		this.id = id;
		// this.canvas = canvas;
		interactionLabel.setStyleName(Resources.INSTANCE.style().mapLabel());
		add(interactionLabel);
		interactionLabel.setVisible(false);
	}

	public void addLoadHandler(final EventHandler eventHandler)
	{
		this.loadHandler = eventHandler;
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
				if (item.getItem().hasMetadata("mapItemID") && item.getItem().getMetadata("mapItemID").equals(id))
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
									.transform(latLonProjection, map.getProjection()));
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

	public void setURL(final String url, final boolean visible)
	{
		this.url = url;
		this.visible = visible;
		if (map != null)
		{
			createRoute();
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		map = Map.create("mapPanel" + id);
		final ClickControl control = ClickControl.create(new EventHandler()
		{
			@Override
			protected void handleEvent(final Event event)
			{
				final LonLat lonLat = map.getLonLatFromPixel(event.getXY()).transform(map.getProjection(),
																						latLonProjection);
				GWT.log("Clicked at " + lonLat.getLat() + "N, " + lonLat.getLon() + "E");
				if (positionItem != null)
				{
					positionItem.getItem().setGeometry(POINT_PREFIX + lonLat.getLat() + " " + lonLat.getLon() + ")");
					// editor.markChanged();
					// refreshMarkers();
				}
			}
		}.getFunction());
		map.addControl(control);
		control.activate();
		map.addLayer(OSMLayer.create("Osmarender"));
		try
		{
			map.setCenter(LonLat.create(-1.18f, 52.95f), 13);
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}

		if (url != null)
		{
			createRoute();
		}
	}

	private void createRoute()
	{
		if (routeLayer != null)
		{
			map.removeLayer(routeLayer);
		}

		if (markerLayer != null)
		{
			map.removeLayer(markerLayer);
		}

		routeLayer = RouteLayer.create(id, url);
		try
		{
			if (loadHandler != null)
			{
				routeLayer.getEvents().register("loadend", routeLayer, loadHandler.getFunction());
			}
			routeLayer.setVisible(visible);
			map.addLayer(routeLayer);				
		}
		catch(Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
		
		markerLayer = MarkerLayer.create("markerLayer");

		try
		{
			map.addLayer(markerLayer);
		}
		catch(Exception e)
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