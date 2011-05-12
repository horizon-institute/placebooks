package placebooks.model;

import javax.persistence.Entity;

@Entity
public class PlaceBookItemSearchIndex extends SearchIndex
{
	private PlaceBookItem item;

	public PlaceBookItemSearchIndex()
	{
		super();
	}

	public PlaceBookItem getPlaceBookItem()
	{
		return item;
	}

	public void setPlaceBookItem(final PlaceBookItem item)
	{
		this.item = item;
	}
}