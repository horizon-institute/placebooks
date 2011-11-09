package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.Resources;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.images.markers.Markers;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.openlayers.Bounds;
import placebooks.client.ui.openlayers.LonLat;
import placebooks.client.ui.openlayers.Map;
import placebooks.client.ui.openlayers.Marker;
import placebooks.client.ui.openlayers.MarkerLayer;
import placebooks.client.ui.openlayers.OSLayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookShelf extends Composite
{
	interface PlaceBookShelfUiBinder extends UiBinder<Widget, PlaceBookShelf>
	{
	}

	private static PlaceBookShelfUiBinder uiBinder = GWT.create(PlaceBookShelfUiBinder.class);

	@UiField
	Panel placebooks;

	@UiField
	Panel progress;

	@UiField
	Label progressLabel;
	
	@UiField
	Panel mapPanel;

	private Map map;

	private final Collection<PlaceBookEntryWidget> widgets = new ArrayList<PlaceBookEntryWidget>();

	private MarkerLayer markerLayer;
	
	@UiField
	Label mapToggleText;
	
	@UiField
	Image mapToggleImage;
	
	@UiField
	Panel mapToggle;
	
	boolean mapVisible = false;

	public PlaceBookShelf()
	{
		initWidget(uiBinder.createAndBindUi(this));

		map = Map.create(mapPanel.getElement(), true);
		map.addLayer(OSLayer.create("glayer"));
		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);
		mapPanel.setVisible(false);
		mapToggle.setVisible(false);

		addDomHandler(new LoadHandler()
		{

			@Override
			public void onLoad(LoadEvent event)
			{
				recenter();
			}
		}, LoadEvent.getType());
	}

	public ImageResource getMarker(int index)
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

	public void setShelf(final PlaceBookPlace place, final Shelf shelf, final boolean includeZero)
	{
		if(shelf == null)
		{
			return;
		}
		progress.setVisible(false);
		mapPanel.setVisible(true);
		mapToggle.setVisible(true);
		placebooks.clear();
		widgets.clear();
		if (shelf != null)
		{
			final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
			for (final PlaceBookEntry entry : shelf.getEntries())
			{
				if (entry.getScore() > 0 || includeZero)
				{
					entries.add(entry);
				}
			}

			Collections.sort(entries, new Comparator<PlaceBookEntry>()
			{
				@Override
				public int compare(final PlaceBookEntry o1, final PlaceBookEntry o2)
				{
					if (o2.getScore() != o1.getScore())
					{
						return o2.getScore() - o1.getScore();
					}
					else
					{
						return o1.getTitle().compareTo(o2.getTitle());
					}
				}
			});

			int index = 0;
			int markerIndex = 1;
			for (final PlaceBookEntry entry : entries)
			{
				final PlaceBookEntryWidget widget = new PlaceBookEntryWidget(place, entry);
				if (index % 5 == 0)
				{
					widget.getElement().getStyle().setProperty("clear", "left");
				}
				index++;

				if (entry.getCenter() != null)
				{
					String geometry = entry.getCenter();
					if (geometry.startsWith(MapItem.POINT_PREFIX))
					{
						final LonLat lonlat = LonLat
								.createFromPoint(	geometry.substring(	MapItem.POINT_PREFIX.length(),
																		geometry.length() - 1)).cloneLonLat()
								.transform(map.getDisplayProjection(), map.getProjection());
						final ImageResource markerImage = getMarker(markerIndex);
						markerIndex++;
						final Marker marker = Marker.create(markerImage, lonlat);
						widget.setMarker(marker, markerImage);
						markerLayer.addMarker(marker);
					}
				}

				widget.addMouseOverHandler(new MouseOverHandler()
				{
					@Override
					public void onMouseOver(MouseOverEvent event)
					{
						highlight(entry);
					}
				});
				widget.addMouseOutHandler(new MouseOutHandler()
				{
					@Override
					public void onMouseOut(MouseOutEvent event)
					{
						highlight(null);
					}
				});

				widgets.add(widget);
				placebooks.add(widget);
			}
		}

		setMapVisible(mapVisible);
		recenter();
	}

	private void highlight(PlaceBookEntry highlight)
	{
		for (PlaceBookEntryWidget widget : widgets)
		{
			if (highlight == null || widget.getEntry().getKey().equals(highlight.getKey()))
			{
				if (widget.getMarker() != null)
				{
					widget.getMarker().getIcon().getImageDiv().getStyle().setOpacity(1);
				}
			}
			else
			{
				if (widget.getMarker() != null)
				{
					widget.getMarker().getIcon().getImageDiv().getStyle().setOpacity(0.5);
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
			map.setCenter(LonLat.create(-1.10f, 52.58f).transform(map.getDisplayProjection(), map.getProjection()), 12);
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
	
	public void setMapVisible(boolean visible)
	{
		mapVisible = visible;
		mapPanel.setVisible(mapVisible);
		for (PlaceBookEntryWidget widget : widgets)
		{
			widget.setMarkerVisible(mapVisible);
		}
		
		if(mapVisible)
		{
			mapToggleText.setText("Hide Map");
			mapToggleImage.setResource(Resources.IMAGES.arrow_right());
			placebooks.getElement().getStyle().clearRight();
			placebooks.getElement().getStyle().clearTop();
			placebooks.getElement().getStyle().clearPaddingLeft();
			placebooks.getElement().getStyle().clearPaddingRight();					
			placebooks.setWidth("190px");
			
			recenter();
		}
		else
		{
			mapToggleText.setText("Show Map");
			mapToggleImage.setResource(Resources.IMAGES.arrow_left());
			placebooks.getElement().getStyle().setRight(0, Unit.PX);
			placebooks.getElement().getStyle().setTop(20, Unit.PX);
			placebooks.getElement().getStyle().setPaddingLeft(40, Unit.PX);
			placebooks.getElement().getStyle().setPaddingRight(40, Unit.PX);			
			placebooks.getElement().getStyle().clearWidth();
		}		
	}

	@UiHandler("mapToggle")
	void toggleMapVisible(ClickEvent event)
	{
		setMapVisible(!mapVisible);
	}

	public void showProgress(String string)
	{
		placebooks.clear();
		widgets.clear();
		markerLayer.clearMarkers();

		progress.setVisible(true);
		progressLabel.setText(string);
		mapPanel.setVisible(false);
		mapToggle.setVisible(false);
		
	}
}