package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookLibrary;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookLibraryActivity extends PlaceBookActivity
{
	private final PlaceController placeController;

	public PlaceBookLibraryActivity(final PlaceController placeController, final Shelf shelf)
	{
		super(shelf);
		this.placeController = placeController;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookLibrary browse = new PlaceBookLibrary(placeController, shelf);
		panel.setWidget(browse);
	}
}
