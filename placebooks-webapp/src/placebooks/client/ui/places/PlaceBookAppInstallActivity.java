package placebooks.client.ui.places;

import placebooks.client.ui.PlaceBookAppInstall;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookAppInstallActivity extends PlaceBookActivity
{
	private final PlaceController placeController;

	public PlaceBookAppInstallActivity(final PlaceController placeController, final PlaceBookAppInstallPlace place)
	{
		super(place.getShelf());
		this.placeController = placeController;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookAppInstall browse = new PlaceBookAppInstall(placeController, shelf);
		panel.setWidget(browse);
	}
}
