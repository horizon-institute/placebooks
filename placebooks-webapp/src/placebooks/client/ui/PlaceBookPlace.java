package placebooks.client.ui;

import placebooks.client.model.Shelf;
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

	private Shelf shelf;
	private PlaceController placeController;

	protected PlaceBookPlace(final Shelf shelf)
	{
		this.shelf = shelf;
	}

	public User getCurrentUser()
	{
		if (shelf != null) { return shelf.getUser(); }
		return null;
	}

	public PlaceController getPlaceController()
	{
		return placeController;
	}

	public Shelf getShelf()
	{
		return shelf;
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

	public final void setShelf(final Shelf shelf)
	{
		if (this.shelf != shelf)
		{
			this.shelf = shelf;
			shelfUpdated();
			if (toolbar != null)
			{
				toolbar.refresh();
			}
		}
	}

	protected void shelfUpdated()
	{
	}
}
