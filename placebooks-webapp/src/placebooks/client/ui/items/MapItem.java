package placebooks.client.ui.items;

import placebooks.client.JSONResponse;
import placebooks.client.model.DataStore;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.model.ServerInfo;
import placebooks.client.model.ServerInfoDataStore;
import placebooks.client.ui.elements.PlaceBookController;
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
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemWidget
{
	public final static String POINT_PREFIX = "POINT (";

	private final EventHandler loadHandler = new EventHandler()
	{
		@Override
		protected void handleEvent(final Event event)
		{
			recenter();
		}
	};

	private Map map = null;

	private MarkerLayer markerLayer;

	private SimplePanel panel = new SimplePanel();

	private PlaceBookItem positionItem = null;

	private RouteLayer routeLayer;

	private String url;
	
	private final PopupPanel popup = new PopupPanel();
	
	private final Label popupLabel = new Label();

	private final DataStore<ServerInfo> infoStore = new ServerInfoDataStore();

	@Override
	public String resize()
	{
		String size = super.resize();
		recenter();
		return size;
	}

	public MapItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);
		initWidget(panel);
		panel.setWidth("100%");
		panel.setHeight("100%");

		if (!item.hasParameter("height"))
		{
			item.setParameter("height", 5000);
		}

		popup.add(popupLabel);
		popup.getElement().getStyle().setZIndex(1600);
		popup.getElement().getStyle().setBackgroundColor("#FFA");	
		popup.getElement().getStyle().setProperty("padding", "2px 4px");
		
		createMap();
	}

	public void moveMarker(final PlaceBookItem item, final ChangeHandler changeHandler)
	{
		positionItem = item;
		refreshMarkers();

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
					controller.markChanged();
					refreshMarkers();
					changeHandler.onChange(null);
				}
			}
		}.getFunction());
		map.addControl(control);
		control.activate();
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
		refreshMarkers();
		recenter();
	}

	private int getMapPage()
	{
		int index = 0;
		for(final PlaceBook page: controller.getPages().getPlaceBook().getPages())
		{
			for(final PlaceBookItem item: page.getItems())
			{
				if(item.equals(getItem()))
				{
					return index;
				}
			}
			index++;
		}
		return -1;
	}
	
	public void refreshMarkers()
	{
		if (markerLayer == null) { return; }
		markerLayer.clearMarkers();

		final int mapPage = getMapPage();
		
		for (final PlaceBook page : controller.getPages().getPlaceBook().getPages())
		{
			for (final PlaceBookItem item : page.getItems())
			{
				final String geometry = item.getGeometry();				
				if (getItem().getKey() != null && getItem().getKey().equals(item.getMetadata("mapItemID")))
				{
					item.removeMetadata("mapItemID");
					item.setParameter("mapPage", mapPage);
				}
							
				if(geometry != null && item.getParameter("mapPage", -1) == mapPage)
				{
					if (geometry.startsWith(POINT_PREFIX))
					{
						final LonLat lonlat = LonLat
								.createFromPoint(geometry.substring(POINT_PREFIX.length(), geometry.length() - 1))
								.cloneLonLat().transform(map.getDisplayProjection(), map.getProjection());
						final Marker marker = Marker.create(getMarker(item), lonlat);
						marker.getEvents().register("click", marker, EventHandler.createHandler(new EventHandler()
						{
							@Override
							protected void handleEvent(Event event)
							{
								controller.goToPage(page);
							}
						}));
						marker.getEvents().register("mouseover", marker, EventHandler.createHandler(new EventHandler()
						{
							@Override
							protected void handleEvent(Event event)
							{
								popupLabel.setText(item.getMetadata("title"));
								popup.setPopupPosition(marker.getIcon().getImageDiv().getAbsoluteLeft(), marker.getIcon().getImageDiv().getAbsoluteTop() - 20);
								popup.show();
							}
						}));		
						marker.getEvents().register("mouseout", marker, EventHandler.createHandler(new EventHandler()
						{
							@Override
							protected void handleEvent(Event event)
							{
								popup.hide();
							}
						}));							
						if (positionItem != null && !item.equals(positionItem))
						{
							marker.getIcon().getImageDiv().getStyle().setOpacity(0.5);
						}
						markerLayer.addMarker(marker);
					}
				}
				

			}
		}
		recenter();
	}

	private void createMap()
	{
		if (map == null)
		{
			infoStore.get(null, new JSONResponse<ServerInfo>()
			{
				@Override
				public void handleResponse(final ServerInfo object)
				{
					createMap(object);
				}
			});
		}
	}

	private void createMap(final ServerInfo serverInfo)
	{
		if (map != null)
		{
			map.destroy();
		}
		map = Map.create(panel.getElement(), controller.canEdit());

		// map.addLayer(GoogleLayer.create("glayer", map.getMaxExtent()));
		map.addLayer(OSLayer.create("oslayer", serverInfo));
		// map.addLayer(OSMLayer.create("Osmarender"));

		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);

		createRoute();
		refreshMarkers();
	}

	private void createRoute()
	{
		if (map == null) { return; }

		if (routeLayer != null)
		{
			map.removeLayer(routeLayer);
		}

		// GWT.log("Map URL: " + getItem().getURL());
		if (getItem().getHash() != null)
		{
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
	}

	private Bounds getLayerBounds()
	{
		Bounds bounds = markerLayer.getDataExtent();
		if (routeLayer != null)
		{
			final Bounds routeBounds = routeLayer.getDataExtent();
			if (routeBounds != null)
			{
				// GWT.log(routeBounds.toString());
				// Bounds transformed = routeBounds.transform(map.getProjection(),
				// map.getDisplayProjection());
				// GWT.log(transformed.toString());
				// GWT.log(routeBounds.toString());

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

	private ImageResource getMarker(final PlaceBookItem item)
	{
		if (item.is(ItemType.AUDIO))
		{
			return Markers.IMAGES.marker_audio();
		}
		else if (item.is(ItemType.VIDEO))
		{
			return Markers.IMAGES.marker_video();
		}
		else if (item.is(ItemType.WEB))
		{
			return Markers.IMAGES.marker_web();
		}
		else if (item.is(ItemType.TEXT))
		{
			return Markers.IMAGES.marker_text();
		}
		else if (item.is(ItemType.IMAGE))
		{
			return Markers.IMAGES.marker_image();
		}
		else
		{
			return Markers.IMAGES.marker();
		}
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
				if (map.getMaxExtent().contains(bounds))
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