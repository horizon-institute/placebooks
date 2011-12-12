package placebooks.client.ui;

import java.util.Iterator;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.ui.elements.PlaceBookEntryPreview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookHome extends PlaceBookPlace
{
	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookHome>
	{
		@Override
		public PlaceBookHome getPlace(final String token)
		{
			return new PlaceBookHome();
		}

		@Override
		public String getToken(final PlaceBookHome place)
		{
			return "";
		}
	}

	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookHome>
	{
	}

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	TextBox search;

	@UiField
	SimplePanel preview1;

	@UiField
	SimplePanel preview2;

	public PlaceBookHome()
	{
		this(null);
	}

	public PlaceBookHome(final User user)
	{
		super(user);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget widget = uiBinder.createAndBindUi(this);
		toolbar.setPlace(this);

		Window.setTitle("PlaceBooks");

		PlaceBookService.getRandomPlaceBooks(2, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final Shelf shelf = PlaceBookService.parse(Shelf.class, response.getText());
				final Iterator<PlaceBookEntry> entries = shelf.getEntries().iterator();
				preview1.setWidget(new PlaceBookEntryPreview(PlaceBookHome.this, entries.next()));
				preview2.setWidget(new PlaceBookEntryPreview(PlaceBookHome.this, entries.next()));
			}
		});
		panel.setWidget(widget);
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
			getPlaceController().goTo(new PlaceBookSearch(getUser(), ""));
		}
		else
		{
			getPlaceController().goTo(new PlaceBookSearch(getUser(), search.getText()));
		}
	}
}