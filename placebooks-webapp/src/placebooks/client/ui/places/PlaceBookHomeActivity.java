package placebooks.client.ui.places;

import placebooks.client.ui.PlaceBookHome;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookHomeActivity extends AbstractActivity
{
	private final PlaceController controller;
	
	public PlaceBookHomeActivity(PlaceController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookHome home = new PlaceBookHome(controller);		
		panel.setWidget(home);
	}
}
