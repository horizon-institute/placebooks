package placebooks.client.ui;

import java.util.Iterator;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookEntryPreview;
import placebooks.client.ui.elements.PlaceBookToolbar;
import placebooks.client.ui.places.Search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookHome extends PlaceBookPage
{
	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookHome>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	TextBox search;

	@UiField
	SimplePanel preview1;

	@UiField
	PlaceBookToolbar toolbar;
	
	@UiField
	SimplePanel preview2;

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooks());

		PlaceBooks.getServer().getRandomPlaceBooks(2, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final Shelf shelf = PlaceBooks.getServer().parse(Shelf.class, response.getText());
				final Iterator<PlaceBookEntry> entries = shelf.getEntries().iterator();
				if (entries.hasNext())
				{
					preview1.setWidget(new PlaceBookEntryPreview(entries.next()));
				}
				else
				{
					preview1.setVisible(false);
				}
				if (entries.hasNext())
				{
					preview2.setWidget(new PlaceBookEntryPreview(entries.next()));
				}
				else
				{
					preview2.setVisible(false);
				}

			}
		});

		return widget;
	}

	@UiHandler("search")
	void handleBlur(final BlurEvent event)
	{
		if (search.getText().equals(""))
		{
			search.setText(uiMessages.searchPlaceBooks());
			search.getElement().getStyle().clearColor();
		}
	}

	@UiHandler("search")
	void handleFocus(final FocusEvent event)
	{
		if (search.getText().equals(uiMessages.searchPlaceBooks()))
		{
			search.setText("");
			search.getElement().getStyle().setColor("#000");
		}
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
		if (search.getText().equals(uiMessages.searchPlaceBooks()))
		{
			PlaceBooks.goTo(new Search());
		}
		else
		{
			PlaceBooks.goTo(new Search(search.getText()));
		}
	}
}