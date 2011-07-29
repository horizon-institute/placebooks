package placebooks.client.ui;

import placebooks.client.model.Shelf;
import placebooks.client.ui.places.PlaceBookSearchPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
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

	private final PlaceController placeController;

	public PlaceBookHome(final PlaceController controller)
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("PlaceBooks");
		this.placeController = controller;
		
		RootPanel.get().getElement().getStyle().clearOverflow();		
	}

	public PlaceBookHome(final PlaceController controller, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));
		toolbar.setPlaceController(controller);
		toolbar.setShelf(shelf);
		this.placeController = controller;

		Window.setTitle("PlaceBooks");
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
		placeController.goTo(new PlaceBookSearchPlace(search.getText(), toolbar.getShelf()));
	}
}