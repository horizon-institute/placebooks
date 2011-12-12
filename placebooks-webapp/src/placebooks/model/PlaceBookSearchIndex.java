package placebooks.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PlaceBookSearchIndex extends SearchIndex
{
	@OneToOne
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
