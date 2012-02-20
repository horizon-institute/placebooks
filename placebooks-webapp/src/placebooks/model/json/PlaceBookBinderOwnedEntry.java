package placebooks.model.json;

import placebooks.model.PlaceBookBinder;

public class PlaceBookBinderOwnedEntry extends PlaceBookBinderEntry
{
	public PlaceBookBinderOwnedEntry(PlaceBookBinder placebookBinder)
	{
		super(placebookBinder);
		this.ownerName = placebookBinder.getOwner().getName();
	}

	private String ownerName;
	
	public String getOwnerName()
	{
		return ownerName;
	}
}
