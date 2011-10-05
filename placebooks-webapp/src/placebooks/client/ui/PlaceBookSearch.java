package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.places.PlaceBookSearchPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookSearch extends Composite
{
	interface PlaceBookSearchUiBinder extends UiBinder<Widget, PlaceBookSearch>
	{
	}

	private static PlaceBookSearchUiBinder uiBinder = GWT.create(PlaceBookSearchUiBinder.class);

	@UiField
	Panel placebooks;

	@UiField
	PlaceBookToolbar toolbar;

	@UiField
	Panel indicator;
	
	@UiField
	TextBox search;

	private Shelf shelf;

	public PlaceBookSearch(final String searchString, final PlaceController placeController, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("PlaceBooks Search - " + searchString);

		search.setText(searchString);

		toolbar.setPlaceController(placeController);
		toolbar.setShelf(shelf);
		GWT.log("Search: " + searchString);
		// setShelf(shelf);

		PlaceBookService.search(searchString, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				setShelf(Shelf.parse(response.getText()));
			}
		});

		RootPanel.get().getElement().getStyle().clearOverflow();
	}

	@UiHandler("searchButton")
	void handleSearch(final ClickEvent event)
	{
		search();
	}

	@UiHandler("search")
	void handleSearchEnter(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			search();
		}
	}

	private void search()
	{		
		toolbar.getPlaceController().goTo(new PlaceBookSearchPlace(search.getText(), toolbar.getShelf()));
	}

	private void setShelf(final Shelf shelf)
	{
		if (this.shelf != shelf)
		{
			this.shelf = shelf;
			indicator.setVisible(false);
			placebooks.clear();
			if (shelf != null)
			{
				final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
				for (final PlaceBookEntry entry : shelf.getEntries())
				{
					if (entry.getScore() > 0 || search.getText().equals(""))
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
					PlaceBookEntryWidget widget = new PlaceBookEntryWidget(toolbar, entry);
					if(index % 5 == 0)
					{
						widget.getElement().getStyle().setProperty("clear", "left");
					}
					index++;
					placebooks.add(widget);
				}
			}
		}
	}
}