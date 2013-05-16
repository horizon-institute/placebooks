package placebooks.client.ui;

import java.util.Comparator;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.logger.Log;

import placebooks.client.PlaceBooks;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookShelf;
import placebooks.client.ui.elements.PlaceBookToolbar;
import placebooks.client.ui.images.markers.Markers;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.places.Search;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookSearch extends PlaceBookPage
{
	interface PlaceBookMapSearchUiBinder extends UiBinder<Widget, PlaceBookSearch>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookMapSearchUiBinder uiBinder = GWT.create(PlaceBookMapSearchUiBinder.class);

	private final AbstractReadOnlyController<Shelf> controller = new AbstractReadOnlyController<Shelf>()
	{
		@Override
		protected void load(final String id, final AsyncCallback<Shelf> callback)
		{
			Log.info("Search: " + id);
			if (id.equals("location:current"))
			{
				final Geolocation geolocation = Geolocation.getIfSupported();
				geolocation.getCurrentPosition(new Callback<Position, PositionError>()
				{
					@Override
					public void onFailure(final PositionError reason)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(final Position result)
					{
						final String geometry = MapItem.POINT_PREFIX + result.getCoordinates().getLatitude() + " "
								+ result.getCoordinates().getLongitude() + ")";

						PlaceBooks.getServer().searchLocation(geometry, callback);
					}
				});
			}
			else
			{
				PlaceBooks.getServer().search(id, callback);
			}
		}
	};
	
	@UiField
	PlaceBookToolbar toolbar;	

	@UiField
	PlaceBookShelf shelf;

	@UiField
	TextBox searchBox;

	@UiField
	Anchor nearbyLink;

	private String searchString;

	public PlaceBookSearch(final String search)
	{
		searchString = search;
	}

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		if (searchString.trim().isEmpty())
		{
			Window.setTitle(uiMessages.placebooksSearch());
		}
		else
		{
			Window.setTitle(uiMessages.placebooksSearch() + " - " + searchString);
		}

		controller.add(shelf);
		controller.load(searchString);

		searchBox.setText(searchString);

		shelf.setComparator(new Comparator<PlaceBookEntry>()
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

		search();
		return widget;
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

	public String getSearch()
	{
		return searchString;
	}

	@UiHandler("searchButton")
	void handleSearch(final ClickEvent event)
	{
		search();
	}

	@UiHandler("searchBox")
	void handleSearchEnter(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			search();
		}
	}

	@UiHandler("nearbyLink")
	void handleSearchNearby(final ClickEvent event)
	{
		PlaceBooks.goTo(new Search("location:current"));
	}

	private void search()
	{
		searchString = searchBox.getText();
		shelf.showProgress(uiMessages.searching());

		// TODO
		// @Override
		// public boolean include(final PlaceBookEntry entry)
		// {
		// if (searchString.equals("")) { return true; }
		// return entry.getScore() > 0;
		// }

		controller.load(searchString);
	}
}