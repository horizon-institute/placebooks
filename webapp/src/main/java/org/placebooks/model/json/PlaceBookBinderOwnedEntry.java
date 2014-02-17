package org.placebooks.model.json;

import org.placebooks.model.PlaceBookBinder;

public class PlaceBookBinderOwnedEntry extends PlaceBookBinderEntry
{
	private String ownerName;

	public PlaceBookBinderOwnedEntry(final PlaceBookBinder placebookBinder)
	{
		super(placebookBinder);
		ownerName = placebookBinder.getOwner().getName();
	}

	public String getOwnerName()
	{
		return ownerName;
	}
}
