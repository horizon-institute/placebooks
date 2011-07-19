package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookShelf;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookBrowseActivity extends AbstractActivity
{
	private final PlaceController placeController;
	private final Shelf shelf;
	private final String title;

	public PlaceBookBrowseActivity(final String title, final PlaceController placeController, final Shelf shelf)
	{
		this.shelf = shelf;
		this.placeController = placeController;
		this.title = title;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookShelf browse = new PlaceBookShelf(title, placeController, shelf);
		panel.setWidget(browse);
	}
}
