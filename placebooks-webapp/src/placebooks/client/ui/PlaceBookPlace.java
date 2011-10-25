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
	
	protected Shelf shelf;
	protected PlaceController placeController;

	protected PlaceBookPlace(final Shelf shelf)
	{
		this.shelf = shelf;
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
	
	public User getCurrentUser()
	{
		if(shelf != null)
		{
			return shelf.getUser();
		}
		return null;
	}

	@Override	
	public void onStop()
	{
	}

	public Shelf getShelf()
	{
		return shelf;
	}
	
	public PlaceController getPlaceController()
	{
		return placeController;
	}
	
	public void setShelf(final Shelf shelf)
	{
		this.shelf = shelf;
		if(toolbar != null)
		{
			toolbar.refresh();
		}
	}
	
	public void setPlaceController(PlaceController controller)
	{
		this.placeController = controller;		
	}
}
