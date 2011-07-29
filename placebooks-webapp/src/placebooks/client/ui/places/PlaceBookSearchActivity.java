package placebooks.client.ui.places;

import placebooks.client.ui.PlaceBookSearch;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookSearchActivity extends PlaceBookActivity
{
	private final PlaceController placeController;
	private final String search;

	public PlaceBookSearchActivity(final PlaceController placeController, final PlaceBookSearchPlace place)
	{
		super(place.getShelf());
		this.placeController = placeController;
		this.search = place.getSearch();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookSearch browse = new PlaceBookSearch(search, placeController, shelf);
		panel.setWidget(browse);
	}
}
