package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookLibrary;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookLibraryActivity extends AbstractActivity
{
	private final PlaceController placeController;
	private final Shelf shelf;
	
	public PlaceBookLibraryActivity(PlaceController placeController, Shelf shelf)
	{
		this.shelf = shelf;
		this.placeController = placeController;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		PlaceBookLibrary library = new PlaceBookLibrary(placeController, shelf);
		panel.setWidget(library);
	}
}
