package org.placebooks.client.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.placebooks.client.Resources;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.images.markers.Markers;
import org.placebooks.client.ui.items.MapItem;
import org.placebooks.client.ui.pages.PlaceBookPage;
import org.placebooks.client.ui.pages.PlaceBookPage.Type;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.ui.CompositeView;
import org.wornchaos.logger.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookShelf extends CompositeView<Shelf>
{
	interface PlaceBookShelfUiBinder extends UiBinder<Widget, PlaceBookShelf>
	{
	}

	private static final PlaceBookShelfUiBinder uiBinder = GWT.create(PlaceBookShelfUiBinder.class);

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	@UiField
	Panel mapPanel;

	@UiField
	Panel mapToggle;

	@UiField
	Image mapToggleImage;

	@UiField
	Label mapToggleText;

	boolean mapVisible = false;

	@UiField
	Panel placebooks;

	@UiField
	Panel progress;

	@UiField
	Label progressLabel;

	private AsyncCallback<Entry> deleteCallback = null;

	private Map map;

	private Comparator<Entry> comparator = new Comparator<Entry>()
	{
		@Override
		public int compare(final Entry o1, final Entry o2)
		{
			final String state1 = getState(o1);
			final String state2 = getState(o2);

			if (!state1.equals(state2)) { return state2.compareTo(state1); }
			return 0;
		}

		private String getState(final Entry entry)
		{
			String result = entry.getState();
			if (result == null)
			{
				result = "0";
			}
			return result;
		}
	};
	
	private PlaceBookPage.Type type = null;

	private Vector markerLayer = null;

	private final Collection<PlaceBookEntryWidget> widgets = new ArrayList<PlaceBookEntryWidget>();

	private MapWidget mapWidget;
	
	public PlaceBookShelf()
	{
		initWidget(uiBinder.createAndBindUi(this));

		createMap();
	}

	public ImageResource getMarker(final int index)
	{
		switch (index)
		{
			case 1:
				return Markers.IMAGES.markera();
			case 2:
				return Markers.IMAGES.markerb();
			case 3:
				return Markers.IMAGES.markerc();
			case 4:
				return Markers.IMAGES.markerd();
			case 5:
				return Markers.IMAGES.markere();
			case 6:
				return Markers.IMAGES.markerf();
			case 7:
				return Markers.IMAGES.markerg();
			case 8:
				return Markers.IMAGES.markerh();
			case 9:
				return Markers.IMAGES.markeri();
			case 10:
				return Markers.IMAGES.markerj();
			case 11:
				return Markers.IMAGES.markerk();
			case 12:
				return Markers.IMAGES.markerl();
			case 13:
				return Markers.IMAGES.markerm();
			case 14:
				return Markers.IMAGES.markern();
			case 15:
				return Markers.IMAGES.markero();
			case 16:
				return Markers.IMAGES.markerp();
			case 17:
				return Markers.IMAGES.markerq();
			case 18:
				return Markers.IMAGES.markerr();
			case 19:
				return Markers.IMAGES.markers();
			case 20:
				return Markers.IMAGES.markert();
			case 21:
				return Markers.IMAGES.markeru();
			case 22:
				return Markers.IMAGES.markerv();
			case 23:
				return Markers.IMAGES.markerw();
			case 24:
				return Markers.IMAGES.markerx();
			case 25:
				return Markers.IMAGES.markery();
			case 26:
				return Markers.IMAGES.markerz();
			default:
				return Markers.IMAGES.marker();
		}
	}

	@Override
	public void itemChanged(final Shelf shelf)
	{
		if (shelf == null) { return; }
		progress.setVisible(false);
		mapToggle.setVisible(true);
		placebooks.clear();
		widgets.clear();
		
		final List<Entry> entries = new ArrayList<Entry>();
		for (final Entry entry : shelf.getEntries())
		{
			entries.add(entry);
		}

		Collections.sort(entries, comparator);
		
		int markerIndex = 1;
		for (final Entry entry : entries)
		{
			final PlaceBookEntryWidget widget = new PlaceBookEntryWidget(entry, deleteCallback, type);

			if (entry.getCenter() != null)
			{
				final String geometry = entry.getCenter();
				if (map != null)
				{
					try
					{
						final ImageResource markerImage = getMarker(markerIndex);					
						final VectorFeature marker = MapItem.createMarker(map, geometry, markerImage);
						if(marker != null)
						{
							markerIndex++;
							widget.setMarker(marker, markerImage);
							markerLayer.addFeature(marker);							
						}
					}
					catch (final Exception e)
					{
						Log.error(e.getMessage(), e);
					}
				}
			}

			widget.addMouseOverHandler(new MouseOverHandler()
			{
				@Override
				public void onMouseOver(final MouseOverEvent event)
				{
					highlight(entry);
				}
			});
			widget.addMouseOutHandler(new MouseOutHandler()
			{
				@Override
				public void onMouseOut(final MouseOutEvent event)
				{
					highlight(null);
				}
			});

			widgets.add(widget);
			placebooks.add(widget);
		}

		setMapVisible(mapVisible);
		recenter();
	}

	public void setComparator(final Comparator<Entry> comparator)
	{
		this.comparator = comparator;
	}

	public void setDeleteCallback(final AsyncCallback<Entry> callback)
	{
		deleteCallback = callback;
	}

	public void setMapVisible(final boolean visible)
	{
		mapVisible = visible;
		mapPanel.setVisible(mapVisible);
		for (final PlaceBookEntryWidget widget : widgets)
		{
			widget.setMarkerVisible(mapVisible);
		}

		if (mapVisible)
		{
			mapToggleText.setText(uiMessages.mapHide());
			mapToggleImage.setResource(Resources.IMAGES.arrow_right());
			placebooks.getElement().getStyle().clearRight();
			placebooks.getElement().getStyle().clearPaddingLeft();
			placebooks.getElement().getStyle().clearPaddingRight();
			placebooks.setWidth("190px");

			recenter();
		}
		else
		{
			mapToggleText.setText(uiMessages.mapShow());
			mapToggleImage.setResource(Resources.IMAGES.arrow_left());
			placebooks.getElement().getStyle().setRight(0, Unit.PX);
			placebooks.getElement().getStyle().setPaddingLeft(40, Unit.PX);
			placebooks.getElement().getStyle().setPaddingRight(40, Unit.PX);
			placebooks.getElement().getStyle().clearWidth();
		}
	}

	public void showProgress(final String string)
	{
		placebooks.clear();
		widgets.clear();
		if (markerLayer != null)
		{
			try
			{
				markerLayer.eraseFeatures();
			}
			catch(Exception e)
			{
				Log.error(e);
			}
		}

		progress.setVisible(true);
		progressLabel.setText(string.toUpperCase());
		mapPanel.setVisible(false);
		mapToggle.setVisible(false);

	}

	@UiHandler("mapToggle")
	void toggleMapVisible(final ClickEvent event)
	{
		setMapVisible(!mapVisible);
		event.preventDefault();
	}

	@Override
	protected void onAttach()
	{
		super.onAttach();

		setMapVisible(false);
	}

	public void setType(final Type type)
	{
		this.type = type;
	}
	
	private void createMap()
	{
		if(map == null)
		{
			// create some MapOptions
			final MapOptions defaultMapOptions = new MapOptions();
			defaultMapOptions.setNumZoomLevels(16);
	
			mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
			final OSM osm = new OSM();
			osm.setIsBaseLayer(true);
			map = mapWidget.getMap();
			map.addLayer(osm);
			
			//map.addControl(new ScaleLine()); // Display the scaleline
	
			markerLayer = new Vector("Markers");
			map.addLayer(markerLayer);
			mapPanel.add(mapWidget);
			mapPanel.setVisible(false);
			mapToggle.setVisible(true);
		}
	}

	private void highlight(final Entry highlight)
	{
		for (final PlaceBookEntryWidget widget : widgets)
		{
			if (highlight == null || widget.getEntry().getKey().equals(highlight.getKey()))
			{
				if (widget.getMarker() != null)
				{
					widget.getMarker().getStyle().setGraphicOpacity(1);
				}
			}
			else
			{
				if (widget.getMarker() != null)
				{
					widget.getMarker().getStyle().setGraphicOpacity(0.5);
				}
			}
		}
	}

	private void recenter()
	{
		if (map == null) { return; }
		if (!mapPanel.isVisible()) { return; }

		try
		{
			LonLat center = new LonLat(-1.10f, 52.58f);
			center.transform(MapItem.DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
			map.setCenter(center, 12);
			final Bounds bounds = markerLayer.getDataExtent();
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