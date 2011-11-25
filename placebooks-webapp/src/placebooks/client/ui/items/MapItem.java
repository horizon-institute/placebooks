package placebooks.client.ui.items;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.model.ServerInfo;
import placebooks.client.ui.images.markers.Markers;
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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemWidget
{
	public static ServerInfo serverInfo = null;
	
	public final static String POINT_PREFIX = "POINT (";

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

	private final boolean controls;

	MapItem(final PlaceBookItem item, final boolean controls)
	{
		super(item);
		this.controls = controls;
		initWidget(panel);
		interactionLabel.setStyleName(Resources.STYLES.style().mapLabel());
		panel.add(interactionLabel);
		panel.setWidth("100%");
		panel.setHeight("100%");

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

	private ImageResource getMarker(final PlaceBookItem item)
	{
		if(item.is(ItemType.AUDIO))
		{
			return Markers.IMAGES.marker_audio();
		}
		else if(item.is(ItemType.VIDEO))
		{
			return Markers.IMAGES.marker_video();			
		}
		else if(item.is(ItemType.WEB))
		{
			return Markers.IMAGES.marker_web();			
		}
		else if(item.is(ItemType.TEXT))
		{
			return Markers.IMAGES.marker_text();
		}
		else if(item.is(ItemType.IMAGE))
		{
			return Markers.IMAGES.marker_image();
		}		
		else
		{
			return Markers.IMAGES.marker();
		}
	}
	
	public void refreshMarkers()
	{
		// GWT.log("Refresh Markers " + placebook);
		if(markerLayer == null) { return; }
		markerLayer.clearMarkers();
		if (placebook == null) { return; }

		positionItem = null;
		interactionLabel.setVisible(false);
		for (final PlaceBookItem item : placebook.getItems())
		{
			if (item.hasMetadata("mapItemID"))
			{
				if (item.getGeometry() != null)
				{
					final String geometry = item.getGeometry();
					if (geometry.startsWith(POINT_PREFIX))
					{
						final LonLat lonlat = LonLat
								.createFromPoint(geometry.substring(POINT_PREFIX.length(), geometry.length() - 1))
								.cloneLonLat().transform(map.getDisplayProjection(), map.getProjection());
						final Marker marker = Marker.create(getMarker(item), lonlat);
						// marker.getIcon().getImageDiv().getStyle().setOpacity(0.5);
						markerLayer.addMarker(marker);
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

	private void createMap(ServerInfo serverInfo)
	{
		map = Map.create(panel.getElement(), controls);
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
		map.addLayer(OSLayer.create("oslayer", serverInfo));
		// map.addLayer(OSMLayer.create("Osmarender"));

		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);

		createRoute();
		refreshMarkers();
	}
	
	private void createMap()
	{
		if (map == null)
		{
			if(serverInfo != null)
			{
				createMap(serverInfo);
			}
			else
			{
				PlaceBookService.getServerInfo(new AbstractCallback()
				{	
					@Override
					public void success(Request request, Response response)
					{
						try
						{
							serverInfo = ServerInfo.parse(response.getText());
							if(serverInfo != null)
							{
								createMap(serverInfo);						
							}
						}
						catch(Exception e)
						{
							
						}
					}
				});
			}
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

		// GWT.log("Map URL: " + getItem().getURL());
		routeLayer = RouteLayer.create("Route", getItem().getURL(), map.getDisplayProjection());
		try
		{
			if (loadHandler != null)
			{
				routeLayer.getEvents().register("loadend", routeLayer, loadHandler.getFunction());
			}
			// routeLayer.setVisible(Boolean.parseBoolean(item.getMetadata("", "")));
			map.addLayer(routeLayer);
			map.raiseLayer(markerLayer, 10);
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
//				GWT.log(routeBounds.toString());			
//				Bounds transformed = routeBounds.transform(map.getProjection(), map.getDisplayProjection());
//				GWT.log(transformed.toString());
//				GWT.log(routeBounds.toString());
				
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
		if (!isVisible()) { return; }

		try
		{
			map.setCenter(LonLat.create(-1.10f, 52.58f).transform(map.getDisplayProjection(), map.getProjection()), 12);
			final Bounds bounds = getLayerBounds();
			if (bounds != null)
			{
				GWT.log("" + bounds);
				GWT.log("" + map.getMaxExtent());
				if(map.getMaxExtent().contains(bounds))
				{
					map.zoomToExtent(bounds);
				}
				else
				{
					map.zoomToMaxExtent();
				}
			}
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
}