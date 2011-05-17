package placebooks.model;

import javax.persistence.Entity;

@Entity
public class PlaceBookSearchIndex extends SearchIndex
{
	private PlaceBook placebook;

	public PlaceBookSearchIndex()
	{
		super();
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
	}
}