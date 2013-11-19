package org.placebooks.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class PlaceBookItemSearchIndex extends SearchIndex
{
	@OneToOne
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