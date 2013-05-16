package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wornchaos.client.ui.CompositeView;
import org.wornchaos.client.ui.View;

import placebooks.client.Resources;
import placebooks.client.controllers.ServerInfoController;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.ServerInfo;
import placebooks.client.model.Shelf;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.images.markers.Markers;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.openlayers.Bounds;
import placebooks.client.ui.openlayers.LonLat;
import placebooks.client.ui.openlayers.Map;
import placebooks.client.ui.openlayers.Marker;
import placebooks.client.ui.openlayers.MarkerLayer;
import placebooks.client.ui.openlayers.OSLayer;
import placebooks.client.ui.places.PlaceBook;
import placebooks.client.ui.places.PlaceBook.Type;

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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookShelf extends CompositeView<Shelf>
{
	// public static abstract class ShelfControl implements Comparator<PlaceBookEntry>
	// {
	// public ShelfControl()
	// {
	// }
	//
	// public abstract void getShelf(final JSONResponse<Shelf> callback);
	//
	// public abstract boolean include(PlaceBookEntry entry);
	// }

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

	private AsyncCallback<PlaceBookEntry> deleteCallback = null;

	private Map map;

	private Comparator<PlaceBookEntry> comparator = new Comparator<PlaceBookEntry>()
	{
		@Override
		public int compare(final PlaceBookEntry o1, final PlaceBookEntry o2)
		{
			final String state1 = getState(o1);
			final String state2 = getState(o2);

			if (!state1.equals(state2)) { return state2.compareTo(state1); }
			return 0;
		}

		private String getState(final PlaceBookEntry entry)
		{
			String result = entry.getState();
			if (result == null)
			{
				result = "0";
			}
			return result;
		}
	};
	
	private PlaceBook.Type type = null;

	private MarkerLayer markerLayer;

	private final Collection<PlaceBookEntryWidget> widgets = new ArrayList<PlaceBookEntryWidget>();

	public PlaceBookShelf()
	{
		initWidget(uiBinder.createAndBindUi(this));

		addDomHandler(new LoadHandler()
		{
			@Override
			public void onLoad(final LoadEvent event)
			{
				recenter();
			}
		}, LoadEvent.getType());
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

		final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
		for (final PlaceBookEntry entry : shelf.getEntries())
		{
			// if (shelfControl.include(entry))
			// {
			entries.add(entry);
			// }
		}

		Collections.sort(entries, comparator);

		int markerIndex = 1;
		for (final PlaceBookEntry entry : entries)
		{
			final PlaceBookEntryWidget widget = new PlaceBookEntryWidget(entry, deleteCallback, type);

			if (entry.getCenter() != null)
			{
				final String geometry = entry.getCenter();
				if (geometry.startsWith(MapItem.POINT_PREFIX) && map != null)
				{
					try
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
					catch (final Exception e)
					{
						GWT.log(e.getMessage(), e);
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

		ServerInfoController.getController().add(new View<ServerInfo>()
		{
			@Override
			public void itemChanged(final ServerInfo value)
			{
				createMap(value);
			}
		});

		setMapVisible(mapVisible);
		recenter();
	}

	public void setComparator(final Comparator<PlaceBookEntry> comparator)
	{
		this.comparator = comparator;
	}

	public void setDeleteCallback(final AsyncCallback<PlaceBookEntry> callback)
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
			markerLayer.clearMarkers();
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

		ServerInfoController.getController().load();
	}

	public void setType(final Type type)
	{
		this.type = type;
	}
	
	private void createMap(final ServerInfo serverInfo)
	{
		map = Map.create(mapPanel.getElement(), true);
		map.addLayer(OSLayer.create("glayer", serverInfo));
		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);
		mapPanel.setVisible(false);
		mapToggle.setVisible(false);
	}

	private void highlight(final PlaceBookEntry highlight)
	{
		for (final PlaceBookEntryWidget widget : widgets)
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
					widget.getMarker().getIcon().getImageDiv().getStyle().setOpacity(0.3);
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
}