package placebooks.client.ui.places;

import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookHome;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookHomeActivity extends AbstractActivity
{
	private final PlaceController controller;
	private final Shelf shelf;

	public PlaceBookHomeActivity(final PlaceController controller, final Shelf shelf)
	{
		super();
		this.controller = controller;
		this.shelf = shelf;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookHome home = new PlaceBookHome(controller, shelf);
		panel.setWidget(home);
	}
}
