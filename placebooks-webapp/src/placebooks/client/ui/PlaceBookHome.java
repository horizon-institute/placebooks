package placebooks.client.ui;

import placebooks.client.model.Shelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookHome extends Composite
{
	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookHome>
	{
	}

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	PlaceBookToolbar toolbar;

	public PlaceBookHome()
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("PlaceBooks");
	}

	public PlaceBookHome(final PlaceController controller, Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));
		toolbar.setPlaceController(controller);
		toolbar.setShelf(shelf);
	}
}