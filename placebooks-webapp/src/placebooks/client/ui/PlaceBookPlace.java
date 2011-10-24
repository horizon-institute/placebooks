package placebooks.client.ui;

import placebooks.client.model.Shelf;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public abstract class PlaceBookPlace extends Place implements Activity
{
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
	}
	
	public void setPlaceController(PlaceController controller)
	{
		this.placeController = controller;		
	}
}
