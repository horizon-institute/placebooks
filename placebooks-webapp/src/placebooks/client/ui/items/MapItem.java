package placebooks.client.ui.items;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.openlayers.Bounds;
import placebooks.client.ui.openlayers.ClickControl;
import placebooks.client.ui.openlayers.Event;
import placebooks.client.ui.openlayers.EventHandler;
import placebooks.client.ui.openlayers.GoogleLayer;
import placebooks.client.ui.openlayers.LonLat;
import placebooks.client.ui.openlayers.Map;
import placebooks.client.ui.openlayers.Marker;
import placebooks.client.ui.openlayers.MarkerLayer;
import placebooks.client.ui.openlayers.Projection;
import placebooks.client.ui.openlayers.RouteLayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemWidget
{
	private final static Projection LATLON_PROJECTION = Projection.create("ESPG:4326");

	private static final String MARKER_URL = GWT.getHostPageBaseURL() + "images/marker.png";

	private final static String POINT_PREFIX = "POINT (";

	private final Label interactionLabel = new Label();

	private final EventHandler loadHandler = new EventHandler()
	{
		@Override
		protected void handleEvent(final Event event)
		{
			recenter();
		}
	};

	private Map map;

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
		interactionLabel.setVisible(true);
		interactionLabel.setText("Blah");
		interactionLabel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				createRoute();
			}
		});

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

	@Override
	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
		if (map == null)
		{
			createMap();
		}

		markerLayer.clearMarkers();
		refreshMarkers();
	}

	private void createMap()
	{
		map = Map.create(panel.getElement());
		final ClickControl control = ClickControl.create(new EventHandler()
		{
			@Override
			protected void handleEvent(final Event event)
			{
				final LonLat lonLat = map.getLonLatFromPixel(event.getXY()).transform(map.getProjection(),
																						LATLON_PROJECTION);
				if (positionItem != null)
				{
					GWT.log("Clicked at " + lonLat);
					positionItem.setGeometry(POINT_PREFIX + lonLat.getLon() + " " + lonLat.getLat() + ")");
				}

				fireChanged();
				fireFocusChanged(true);
				refreshMarkers();
			}
		}.getFunction());
		map.addControl(control);
		control.activate();

		map.addLayer(GoogleLayer.create("glayer"));
		// map.addLayer(OSMLayer.create("Osmarender"));

		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);

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
		routeLayer = RouteLayer.create("Route", getItem().getURL(), LATLON_PROJECTION);
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
		Bounds bounds = markerLayer.getDataExtent().transform(LATLON_PROJECTION, map.getProjection());
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
				map.setCenter(LonLat.create(-1.10f, 52.58f).transform(LATLON_PROJECTION, map.getProjection()), 12);
			}
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	private void refreshMarkers()
	{
		positionItem = null;
		interactionLabel.setVisible(false);
		for (final PlaceBookItem item : placebook.getItems())
		{
			if (item.hasMetadata("mapItemID") && item.getMetadata("mapItemID").equals(getItem().getKey()))
			{
				if (item.getGeometry() != null)
				{
					final String geometry = item.getGeometry();
					if (geometry.startsWith(POINT_PREFIX))
					{
						final LonLat lonlat = LonLat.create(geometry.substring(	POINT_PREFIX.length(),
																				geometry.length() - 1));
						final Marker marker = Marker.create(MARKER_URL,
															lonlat.clone().transform(LATLON_PROJECTION, map.getProjection()),
															32, 32);
						markerLayer.addMarker(marker);
						GWT.log("Added marker for " + item.getKey() + " at " + lonlat);
					}
				}
				else
				{
					GWT.log("No geometry for " + item.getKey());
					positionItem = item;
					interactionLabel.setText("Set position of item " + item.getKey());
					interactionLabel.setVisible(true);
				}
			}
		}
		recenter();
	}
}