package org.placebooks.client.ui.pages.views;

import java.util.Comparator;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.images.markers.Markers;
import org.placebooks.client.ui.items.MapItem;
import org.placebooks.client.ui.pages.SearchPage;
import org.placebooks.client.ui.views.PlaceBookShelf;
import org.placebooks.client.ui.views.PlaceBookToolbar;
import org.wornchaos.client.ui.ViewCallback;

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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchView extends PageView
{
	interface PlaceBookMapSearchUiBinder extends UiBinder<Widget, SearchView>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookMapSearchUiBinder uiBinder = GWT.create(PlaceBookMapSearchUiBinder.class);


	@UiField
	PlaceBookToolbar toolbar;	

	@UiField
	PlaceBookShelf shelf;

	@UiField
	TextBox searchBox;

	@UiField
	Anchor nearbyLink;

	private String searchString;

	public SearchView(final String search)
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

		searchBox.setText(searchString);

		shelf.setComparator(new Comparator<Entry>()
		{
			@Override
			public int compare(final Entry o1, final Entry o2)
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
		PlaceBooks.goTo(new SearchPage("location:current"));
	}

	private void search()
	{
		searchString = searchBox.getText();
		shelf.showProgress(uiMessages.searching());
	
		if (searchString.equals("location:current"))
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

					PlaceBooks.getServer().searchLocation(geometry, new ViewCallback<Shelf>(shelf));
				}
			});
		}
		else
		{
			PlaceBooks.getServer().search(searchString, new ViewCallback<Shelf>(shelf));
		}
	}
}