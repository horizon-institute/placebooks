package placebooks.client.ui;

import placebooks.client.model.User;
import placebooks.client.ui.elements.PlaceBookToolbar;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiField;

public abstract class PlaceBookPlace extends Place implements Activity
{
	@UiField
	PlaceBookToolbar toolbar;

	private User user;
	private PlaceController placeController;

	protected PlaceBookPlace(final User user)
	{
		this.user = user;
	}

	public PlaceController getPlaceController()
	{
		return placeController;
	}

	public User getUser()
	{
		return user;
	}

	@Override
	public String mayStop()
	{
		return null;
	}

	@Override
	public void onCancel()
	{
	}

	@Override
	public void onStop()
	{
	}

	public void setPlaceController(final PlaceController controller)
	{
		this.placeController = controller;
	}

	public void setUser(final User user)
	{
		this.user = user;
		toolbar.setUser(user);
	}
}
