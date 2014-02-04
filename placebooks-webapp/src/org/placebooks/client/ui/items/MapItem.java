package org.placebooks.client.ui.items;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.util.JSObject;
import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.controllers.ServerInfoController;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Page;
import org.placebooks.client.model.ServerInfo;
import org.placebooks.client.ui.items.maps.GPX;
import org.wornchaos.logger.Log;
import org.wornchaos.views.View;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapItem extends PlaceBookItemView
{
	public final static String POINT_PREFIX = "POINT (";

	public static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
	public static final Projection TARGET_PROJECTION = new Projection("EPSG:900913");
	
	public static LonLat createLonLat(final String geometry)
	{
		if (geometry.startsWith(POINT_PREFIX))
		{
			final String lonlatString = geometry.substring(POINT_PREFIX.length(), geometry.length() - 1);
			try
			{
				final String[] items = lonlatString.split(" ");
				final float lat = Float.parseFloat(items[0]);
				final float lon = Float.parseFloat(items[1]);

				return new LonLat(lon, lat);
			}
			catch (final Exception e)
			{
				Log.error(e);
			}
		}
		return null;
	}

	public static VectorFeature createMarker(final Map map, final String geometry, final ImageResource image)
	{
		if(image == null) { return null; }
		final LonLat lonlat = createLonLat(geometry);
		if (lonlat == null) { return null; }

		if(map == null) { return null; }	
		lonlat.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());

		final Style style = new Style();
		style.setGraphicSize(image.getWidth(), image.getHeight());
		style.setGraphicOffset(-(image.getWidth()/2),-image.getHeight());
		style.setExternalGraphic(image.getSafeUri().asString());
		style.setGraphicOpacity(1);
		final Point point = new Point(lonlat.lon(), lonlat.lat());
		final VectorFeature marker = new VectorFeature(point, style);

		return marker;
	}

	private final EventHandler moveHandler = new EventHandler()
	{
		@Override
		public void onHandle(final EventObject eventObject)
		{
			Log.info(eventObject.getJSObject().getPropertyNames());
			Log.info(eventObject.getJSObject().getPropertyValues());

			final JSObject[] xy = eventObject.getJSObject().getPropertyAsArray("xy");
			final int x = xy[0].getPropertyAsInt("x");
			final int y = xy[0].getPropertyAsInt("y");

			final LonLat lonLat = map.getLonLatFromPixel(new Pixel(x, y));
			// transform lonlat to more readable format
			lonLat.transform(map.getProjection(), DEFAULT_PROJECTION.getProjectionCode());

			Log.info("LonLat = (" + lonLat.lon() + " ; " + lonLat.lat() + ")");

			if (changeHandler != null && positionItem != null)
			{
				positionItem.getItem().setGeom(POINT_PREFIX + lonLat.lat() + " " + lonLat.lon() + ")");
				positionItem.markChanged();
				Log.info("Item GEOM: " + positionItem.getItem().getGeom());

				fireChanged();
				fireFocusChanged(true);
				refreshMarkers();
				changeHandler.onChange(null);
				createGeometry();
			}
		}
	};
	private Map map = null;

	private SimplePanel panel = new SimplePanel();

	private ItemController positionItem = null;

	private String url;
	
	private ChangeHandler changeHandler = null;

	private final PopupPanel popup = new PopupPanel();

	private final Label popupLabel = new Label();
	private Vector routeLayer;

	private Vector markerLayer;

	public MapItem(final ItemController controller)
	{
		super(controller);
		initWidget(panel);
		panel.setWidth("100%");
		panel.setHeight("100%");

		if (!controller.getItem().getParameters().containsKey("height"))
		{
			controller.getItem().getParameters().put("height", 5000);
		}

		popup.add(popupLabel);
		popup.getElement().getStyle().setZIndex(1600);
		popup.getElement().getStyle().setBackgroundColor("#FFA");
		popup.getElement().getStyle().setProperty("padding", "2px 4px");

		createMap();
	}

	public void moveMarker(final ItemController item, final ChangeHandler changeHandler)
	{
		this.changeHandler = changeHandler;
		this.positionItem = item;
		refreshMarkers();
	}

	@Override
	public void refresh()
	{
		final String newURL = ItemController.getURL(getItem(), null);
		if (url == null || !url.equals(newURL) || routeLayer == null)
		{
			url = newURL;
			createRoute();
		}
		refreshMarkers();
		recenter();
	}

	public void refreshMarkers()
	{
		if (markerLayer == null) { return; }
		markerLayer.removeAllFeatures();

		final int mapPage = getMapPage();
		// Log.info("Map Page: " + mapPage);

		for (final Page page : getController().getPlaceBook().getPages())
		{
			for (final Item item : page.getItems())
			{
				final String geometry = item.getGeom();
				if (getItem().getId() != null && getItem().getId().equals(item.getMetadata().get("mapItemID")))
				{
					item.getMetadata().remove("mapItemID");
					item.getParameters().put("mapPage", mapPage);
				}

				if (geometry != null && item.getParameter("mapPage", -1) == mapPage)
				{
					final VectorFeature marker = createMarker(map, item.getGeom(), ItemController.getMarkerImage(item));
					if (marker != null)
					{
//						marker.getEvents().register("click", marker, new EventHandler()
//						{
//							@Override
//							public void onHandle(final EventObject eventObject)
//							{
//								getController().gotoPage(page);
//							}
//						});
//						marker.getEvents().register("mouseover", marker, new EventHandler()
//						{
//							@Override
//							public void onHandle(final EventObject eventObject)
//							{
//								popupLabel.setText(item.getMetadata().get("title"));
//								//popup.setPopupPosition(marker.getIcon().getImageDiv().getAbsoluteLeft(), marker
//								//		.getIcon().getImageDiv().getAbsoluteTop() - 20);
//								popup.show();
//							}
//						});
//						marker.getEvents().register("mouseout", marker, new EventHandler()
//						{
//							@Override
//							public void onHandle(final EventObject eventObject)
//							{
//								popup.hide();
//							}
//						});
//						if (positionItem != null && !item.equals(positionItem))
//						{
//							marker.getStyle().setGraphicOpacity(0.5);
//						}
						markerLayer.addFeature(marker);
					}
				}
			}
		}
		recenter();
	}

	@Override
	public String resize()
	{
		final String size = super.resize();
		recenter();
		return size;
	}

	private void createGeometry()
	{
		if (getItem().getHash() != null) { return; }
		final Bounds bounds = getLayerBounds();
		if (bounds != null)
		{
			final Bounds clone = new Bounds(bounds.getLowerLeftX(), bounds.getLowerLeftY(), bounds.getUpperRightX(),
					bounds.getUpperRightY());
			clone.transform(new Projection(map.getProjection()), DEFAULT_PROJECTION);
			Log.info("Bounds: " + clone.toBBox(null));
			if (clone.getWidth() == 0 && clone.getHeight() == 0)
			{
				getItem().setGeom("POINT (" + clone.getUpperRightY() + " " + clone.getLowerLeftX() + ")");
			}
			else
			{
				getItem().setGeom(	"LINEARRING (" + clone.getUpperRightY() + " " + clone.getLowerLeftX() + ", "
											+ clone.getUpperRightY() + " " + clone.getUpperRightX() + ","
											+ clone.getLowerLeftY() + " " + clone.getUpperRightX() + ","
											+ clone.getLowerLeftY() + " " + clone.getLowerLeftX() + ","
											+ clone.getUpperRightY() + " " + clone.getLowerLeftX() + ")");
			}
		}
		else
		{
			final LonLat center = map.getCenter();
			center.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
			getItem().setGeom("POINT (" + center.lat() + " " + center.lon() + ")");
		}
	}

	private void createMap()
	{
		if (map == null)
		{
			ServerInfoController.getController().add(new View<ServerInfo>()
			{

				@Override
				public void itemChanged(final ServerInfo value)
				{
					createMap(value);
				}
			});
			ServerInfoController.getController().load();
		}
	}

	private void createMap(final ServerInfo serverInfo)
	{
		if (map != null)
		{
			map.destroy();
		}
		
		// create some MapOptions
		final MapOptions defaultMapOptions = new MapOptions();
		defaultMapOptions.setNumZoomLevels(16);

		// Create a MapWidget and add 2 OSM layers
		final MapWidget mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
		final OSM openStreetMap = OSM.Mapnik("Base Map");
		openStreetMap.setIsBaseLayer(true);
		map = mapWidget.getMap();
		map.addLayer(openStreetMap);

		map.addControl(new ScaleLine()); // Display the scaleline

		// Center and zoom to a location
		final LonLat lonLat = new LonLat(6.95, 50.94);
		// transform lonlat to OSM coordinate system
		lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
		map.setCenter(lonLat, 12);

		// The actual listening for the click, and showing the popup
		map.getEvents().register("click", map, moveHandler);
		
		markerLayer = new Vector("Markers");
		map.addLayer(markerLayer);		

		final VectorOptions options = new VectorOptions();
		
		routeLayer = new Vector("Route", options);
		map.addLayer(routeLayer);
		map.raiseLayer(markerLayer, 10);
		
		panel.setWidget(mapWidget);		
		
		createRoute();
		refreshMarkers();		
	}

	private void createRoute()
	{
		if (map == null) { return; }

		try
		{
			routeLayer.removeAllFeatures();
		}
		catch(Exception e)
		{
			
		}

		if (getItem().getText() != null)
		{
			try
			{
				final GPX gpx = new GPX();				
				final VectorFeature[] features = gpx.read(getItem().getText());
				Log.info("" + features);
				Log.info(map.getProjection());
				if(features != null && features.length > 0)
				{					
					for(VectorFeature feature: features)
					{
						feature.getGeometry().transform(DEFAULT_PROJECTION, TARGET_PROJECTION);
					}
					routeLayer.addFeatures(features);
					
					recenter();
				}
			}
			catch (final Exception e)
			{
				routeLayer = null;
				Log.error(e);
			}
		}
	}

	private Bounds getLayerBounds()
	{
		Bounds bounds = markerLayer.getDataExtent();
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
		return bounds;
	}

	private int getMapPage()
	{
		int index = 0;
		for (final Page page : getController().getPlaceBook().getPages())
		{
			for (final Item item : page.getItems())
			{
				if (item.getId() != null && item.getId().equals(getItem().getId())) { return index; }
			}
			index++;
		}
		return -1;
	}

	private void recenter()
	{
		if (map == null) { return; }
		if (!isVisible()) { return; }

		try
		{
			final LonLat center = new LonLat(-1.10f, 52.58f);
			center.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
			map.setCenter(center, 12);
			final Bounds bounds = getLayerBounds();
			if (bounds != null)
			{
				if (map.getMaxExtent().containsBounds(bounds, false, true))
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
			Log.error(e);
		}
	}
}