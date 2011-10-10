package placebooks.client.ui;

import java.util.Iterator;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.places.PlaceBookSearchPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookHome extends Composite
{
	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookHome>
	{
	}

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	TextBox search;

	@UiField
	PlaceBookToolbar toolbar;

	@UiField
	SimplePanel preview1;

	@UiField
	SimplePanel preview2;

	public PlaceBookHome(final PlaceController controller)
	{
		initWidget(uiBinder.createAndBindUi(this));
		toolbar.setPlaceController(controller);
		
		Window.setTitle("PlaceBooks");

		RootPanel.get().getElement().getStyle().clearOverflow();

		PlaceBookService.getRandomPlaceBooks(2, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final Shelf shelf = Shelf.parse(response.getText());
				final Iterator<PlaceBookEntry> entries = shelf.getEntries().iterator();
				preview1.setWidget(new PlaceBookEntryPreview(toolbar, entries.next()));
				preview2.setWidget(new PlaceBookEntryPreview(toolbar, entries.next()));				
			}
		});
	}

	public PlaceBookHome(final PlaceController controller, final Shelf shelf)
	{
		this(controller);
		toolbar.setShelf(shelf);
	}

	@UiHandler("search")
	void handleBlur(final BlurEvent event)
	{
		if (search.getText().equals(""))
		{
			search.setText("Search PlaceBooks");
			search.getElement().getStyle().clearColor();
		}
	}

	@UiHandler("search")
	void handleFocus(final FocusEvent event)
	{
		if (search.getText().equals("Search PlaceBooks"))
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
		if (search.getText().equals("Search PlaceBooks"))
		{
			toolbar.getPlaceController().goTo(new PlaceBookSearchPlace("", toolbar.getShelf()));
		}
		else
		{
			toolbar.getPlaceController().goTo(new PlaceBookSearchPlace(search.getText(), toolbar.getShelf()));
		}
	}
}