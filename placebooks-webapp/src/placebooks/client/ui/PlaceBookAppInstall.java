package placebooks.client.ui;

import placebooks.client.model.Shelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookAppInstall extends Composite
{
	interface PlaceBookAppInstallUiBinder extends UiBinder<Widget, PlaceBookAppInstall>
	{
	}

	private static PlaceBookAppInstallUiBinder uiBinder = GWT.create(PlaceBookAppInstallUiBinder.class);

	@UiField
	PlaceBookToolbar toolbar;

	public PlaceBookAppInstall(final PlaceController placeController, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		toolbar.setPlaceController(placeController);
		toolbar.setShelf(shelf);

		Window.setTitle("PlaceBooks App Install Instructions");
		RootPanel.get().getElement().getStyle().clearOverflow();
	}
}