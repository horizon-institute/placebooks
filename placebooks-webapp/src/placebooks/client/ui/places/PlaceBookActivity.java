package placebooks.client.ui.places;

import placebooks.client.model.Shelf;

import com.google.gwt.activity.shared.AbstractActivity;

public abstract class PlaceBookActivity extends AbstractActivity
{
	protected final Shelf shelf;

	protected PlaceBookActivity(final Shelf shelf)
	{
		this.shelf = shelf;
	}

}
