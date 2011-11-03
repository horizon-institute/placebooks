package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookEntryWidget;
import placebooks.client.ui.openlayers.Map;
import placebooks.client.ui.openlayers.MarkerLayer;
import placebooks.client.ui.openlayers.OSLayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookMapSearch extends PlaceBookPlace
{
	@Prefix("mapsearch")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookMapSearch>
	{
		@Override
		public PlaceBookMapSearch getPlace(final String token)
		{
			return new PlaceBookMapSearch(token);
		}

		@Override
		public String getToken(final PlaceBookMapSearch place)
		{
			return place.getSearch();
		}
	}

	interface PlaceBookMapSearchUiBinder extends UiBinder<Widget, PlaceBookMapSearch>
	{
	}

	private static PlaceBookMapSearchUiBinder uiBinder = GWT.create(PlaceBookMapSearchUiBinder.class);

	@UiField
	Panel placebooks;

	@UiField
	Panel indicator;

	@UiField
	TextBox searchBox;
	
	@UiField
	Panel mapPanel;
	
	private Map map;

	private final String searchString;

	private MarkerLayer markerLayer;

	public PlaceBookMapSearch(final String search)
	{
		super(null);
		this.searchString = search;
	}

	public PlaceBookMapSearch(final String search, final Shelf shelf)
	{
		super(shelf);
		this.searchString = search;
	}

	public String getSearch()
	{
		return searchString;
	}

	public void setSearchShelf(final Shelf shelf)
	{
		indicator.setVisible(false);
		placebooks.clear();
		if (shelf != null)
		{
			final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
			for (final PlaceBookEntry entry : shelf.getEntries())
			{
				if (entry.getScore() > 0 || searchString.equals(""))
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
			for (final PlaceBookEntry entry : entries)
			{
				final PlaceBookEntryWidget widget = new PlaceBookEntryWidget(this, entry);
				if (index % 5 == 0)
				{
					widget.getElement().getStyle().setProperty("clear", "left");
				}
				index++;
				placebooks.add(widget);
			}
		}

	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		map = Map.create(mapPanel.getElement(), true);
		map.addLayer(OSLayer.create("glayer"));
		markerLayer = MarkerLayer.create("markerLayer");
		map.addLayer(markerLayer);

		
		
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

		RootPanel.get().getElement().getStyle().clearOverflow();
		panel.setWidget(widget);
	}

	@UiHandler("searchButton")
	void handleSearch(final ClickEvent event)
	{
		search();
	}
	
	void hideMap()
	{
		mapPanel.setVisible(false);
		placebooks.setWidth("800px");
	}

	void showMap()
	{
		mapPanel.setVisible(true);
		placebooks.setWidth("200px");
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
		getPlaceController().goTo(new PlaceBookMapSearch(searchBox.getText(), getShelf()));
	}
}