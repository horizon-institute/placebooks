package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.place.shared.Place;

public abstract class PlaceBookPlace extends Place
{
	private final Shelf shelf;

	protected PlaceBookPlace(final Shelf shelf)
	{
		this.shelf = shelf;
	}

	public Shelf getShelf()
	{
		return shelf;
	}
}
