package placebooks.client.ui.items;

import placebooks.client.model.PlaceBook;
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
import placebooks.client.ui.openlayers.OSLayer;
import placebooks.client.ui.openlayers.RouteLayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemWidget
{
	private static final String MARKER_URL = GWT.getHostPageBaseURL() + "images/Marker_025.png";

	private final static String POINT_PREFIX = "POINT (";

	private final Label interactionLabel = new Label();

	private final EventHandler loadHandler = new EventHandler()
	{
		@Override
		protected void handleEvent(final Event event)
		{
			recenter();
			recenter();
		}
	};

	private Map map = null;

	private MarkerLayer markerLayer;

	private SimplePanel panel = new SimplePanel();

	private PlaceBook placebook;

	private PlaceBookItem positionItem = null;

	private RouteLayer routeLayer;

	private String url;

	MapItem(final PlaceBookItem item)
	{
		super(item);
		initWidget(panel);
		interactionLabel.setStyleName(Resources.INSTANCE.style().mapLabel());
		panel.add(interactionLabel);
		panel.setWidth("100%");
		panel.setHeight("100%");
//		panel.setHeight("300px");

		if (!item.hasParameter("height"))
		{
			item.setParameter("height", 5000);
		}
	}

	@Override
	public void refresh()
	{
		final String newURL = getItem().getURL();
		if (url == null || !url.equals(newURL) || routeLayer == null)
		{
			this.url = newURL;
			createRoute();
		}
		recenter();
	}

	public void refreshMarkers()
	{
		GWT.log("Refresh Markers " + placebook);
		markerLayer.clearMarkers();		
		if (placebook == null) { return; }

		positionItem = null;
		interactionLabel.setVisible(false);
		for (final PlaceBookItem item : placebook.getItems())
		{		
			if (item.hasMetadata("mapItemID"))// && item.getMetadata("mapItemID").equals(getItem().getKey()))
			{
				GWT.log("Has mapItemID");				
				if (item.getGeometry() != null)
				{
					final String geometry = item.getGeometry();
					if (geometry.startsWith(POINT_PREFIX))
					{
						final LonLat lonlat = LonLat.createFromPoint(geometry.substring(	POINT_PREFIX.length(),
																				geometry.length() - 1));
						final Marker marker = Marker.create(MARKER_URL,
															lonlat.cloneLonLat().transform(map.getDisplayProjection(),
																							map.getProjection()), 25,
															25);
						marker.getIcon().getImageDiv().setTitle(item.getMetadata("title", item.getKey()));
						markerLayer.addMarker(marker);
						GWT.log("Added marker for " + item.getKey() + " at " + lonlat);
					}
				}
				else
				{
					GWT.log("No geometry for " + item.getKey());
					positionItem = item;
					interactionLabel.setText("Set position of " + item.getMetadata("title", item.getKey()));
					interactionLabel.setVisible(true);
				}
			}
		}
		recenter();
	}

	@Override
	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
		if (map == null)
		{
			createMap();
		}

		refreshMarkers();
	}

	@Override
	protected void onAttach()
	{
		super.onAttach();
		createMap();
	}

	private void createMap()
	{
		if (map == null)
		{
			map = Map.create(panel.getElement());
			// map = OSMap.create(panel.getElement());
			final ClickControl control = ClickControl.create(new EventHandler()
			{
				@Override
				protected void handleEvent(final Event event)
				{
					final LonLat lonLat = map.getLonLatFromPixel(event.getXY()).transform(map.getProjection(),
																							map.getDisplayProjection());
					if (positionItem != null)
					{
						GWT.log("Clicked at " + lonLat);
						positionItem.setGeometry(POINT_PREFIX + lonLat.getLat() + " " + lonLat.getLon() + ")");

						fireChanged();
						fireFocusChanged(true);
						refreshMarkers();
					}
				}
			}.getFunction());
			map.addControl(control);
			control.activate();

			// map.addLayer(GoogleLayer.create("glayer", map.getMaxExtent()));
			map.addLayer(OSLayer.create("glayer"));
			// map.addLayer(OSMLayer.create("Osmarender"));

			markerLayer = MarkerLayer.create("markerLayer");
			map.addLayer(markerLayer);
		}

		createRoute();
	}

	private void createRoute()
	{
		if (map == null) { return; }

		if (routeLayer != null)
		{
			map.removeLayer(routeLayer);
		}

		GWT.log("Map URL: " + getItem().getURL());
		routeLayer = RouteLayer.create("Route", getItem().getURL(), map.getDisplayProjection());
		try
		{
			if (loadHandler != null)
			{
				routeLayer.getEvents().register("loadend", routeLayer, loadHandler.getFunction());
			}
			// routeLayer.setVisible(Boolean.parseBoolean(item.getMetadata("", "")));
			map.addLayer(routeLayer);
		}
		catch (final Exception e)
		{
			routeLayer = null;
			GWT.log(e.getMessage(), e);
		}
	}

	private Bounds getLayerBounds()
	{
		Bounds bounds = markerLayer.getDataExtent();
		if (routeLayer != null)
		{
			final Bounds routeBounds = routeLayer.getDataExtent();
			if (routeBounds != null)
			{
				if (bounds == null)
				{
					bounds = routeBounds;
				}
				else
				{
					bounds.extend(routeBounds);
				}
			}
		}
		return bounds;
	}

	private void recenter()
	{
		if (map == null) { return; }

		try
		{
			final Bounds bounds = getLayerBounds();
			if (bounds != null)
			{
				map.zoomToExtent(bounds);
			}
			else
			{
				map.setCenter(	LonLat.create(-1.10f, 52.58f)
										.transform(map.getDisplayProjection(), map.getProjection()),
								12);
			}
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
}