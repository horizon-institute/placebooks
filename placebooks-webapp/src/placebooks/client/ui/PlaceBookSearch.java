package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookShelf;
import placebooks.client.ui.images.markers.Markers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookSearch extends PlaceBookPlace
{
	@Prefix("mapsearch")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookSearch>
	{
		@Override
		public PlaceBookSearch getPlace(final String token)
		{
			return new PlaceBookSearch(token);
		}

		@Override
		public String getToken(final PlaceBookSearch place)
		{
			return place.getSearch();
		}
	}

	interface PlaceBookMapSearchUiBinder extends UiBinder<Widget, PlaceBookSearch>
	{
	}

	private static PlaceBookMapSearchUiBinder uiBinder = GWT.create(PlaceBookMapSearchUiBinder.class);

	@UiField
	PlaceBookShelf shelf;

	@UiField
	TextBox searchBox;
		
	private final String searchString;

	public PlaceBookSearch(final String search)
	{
		super(null);
		this.searchString = search;
	}

	public PlaceBookSearch(final String search, final Shelf shelf)
	{
		super(shelf);
		this.searchString = search;
	}

	public String getSearch()
	{
		return searchString;
	}

	public ImageResource getMarker(int index)
	{
		switch(index)
		{
			case 1: return Markers.IMAGES.markera();
			case 2: return Markers.IMAGES.markerb();
			case 3: return Markers.IMAGES.markerc();
			case 4: return Markers.IMAGES.markerd();
			case 5: return Markers.IMAGES.markere();
			case 6: return Markers.IMAGES.markerf();
			case 7: return Markers.IMAGES.markerg();
			case 8: return Markers.IMAGES.markerh();
			case 9: return Markers.IMAGES.markeri();
			case 10: return Markers.IMAGES.markerj();
			case 11: return Markers.IMAGES.markerk();
			case 12: return Markers.IMAGES.markerl();
			case 13: return Markers.IMAGES.markerm();
			case 14: return Markers.IMAGES.markern();	
			case 15: return Markers.IMAGES.markero();
			case 16: return Markers.IMAGES.markerp();
			case 17: return Markers.IMAGES.markerq();
			case 18: return Markers.IMAGES.markerr();
			case 19: return Markers.IMAGES.markers();
			case 20: return Markers.IMAGES.markert();
			case 21: return Markers.IMAGES.markeru();
			case 22: return Markers.IMAGES.markerv();
			case 23: return Markers.IMAGES.markerw();
			case 24: return Markers.IMAGES.markerx();
			case 25: return Markers.IMAGES.markery();
			case 26: return Markers.IMAGES.markerz();
			default: return Markers.IMAGES.marker();
		}
	}
	
	public void setSearchShelf(final Shelf newShelf)
	{
		shelf.setShelf(this, newShelf, searchString.equals(""));
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget widget = uiBinder.createAndBindUi(this);
		
		Window.setTitle("PlaceBooks Search - " + searchString);

		searchBox.setText(searchString);

		toolbar.setPlace(this);
		GWT.log("Search: " + searchString);
		// setShelf(shelf);

		PlaceBookService.search(searchString, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				setSearchShelf(Shelf.parse(response.getText()));
			}
		});

		panel.setWidget(widget);
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

	private void search()
	{
		getPlaceController().goTo(new PlaceBookSearch(searchBox.getText(), getShelf()));
	}
}