package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookAppInstall;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookAppInstallPlace extends PlaceBookPlace
{
	@Prefix("appinstall")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookAppInstallPlace>
	{
		@Override
		public PlaceBookAppInstallPlace getPlace(final String token)
		{
			return new PlaceBookAppInstallPlace(null);
		}

		@Override
		public String getToken(final PlaceBookAppInstallPlace place)
		{
			return "";
		}
	}

	public PlaceBookAppInstallPlace(final Shelf shelf)
	{
		super(shelf);
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookAppInstall browse = new PlaceBookAppInstall(getPlaceController(), getShelf());
		panel.setWidget(browse);
	}	
}