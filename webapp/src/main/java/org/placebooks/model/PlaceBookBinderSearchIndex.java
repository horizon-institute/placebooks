package org.placebooks.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class PlaceBookBinderSearchIndex extends SearchIndex
{
	@OneToOne
	private PlaceBookBinder placebookBinder;

	public PlaceBookBinderSearchIndex()
	{
		super();
	}

	public PlaceBookBinder getPlaceBookBinder()
	{
		return placebookBinder;
	}

	public void setPlaceBookBinder(final PlaceBookBinder placebookBinder)
	{
		this.placebookBinder = placebookBinder;
	}
}
