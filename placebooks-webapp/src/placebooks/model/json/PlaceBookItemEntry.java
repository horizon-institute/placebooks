package placebooks.model.json;

import placebooks.model.PlaceBookItem;

public class PlaceBookItemEntry extends ShelfEntry
{
	private String pbKey;
	
	public PlaceBookItemEntry()
	{
		super();
	}

	public PlaceBookItemEntry(final PlaceBookItem p)
	{
		super();
		setKey(p.getKey());
		setTitle(p.getMetadataValue("title"));
		setOwner(p.getOwner().getKey());
		setTimestamp(p.getTimestamp());
		
		this.pbKey = p.getPlaceBook().getKey();		
	}
	
	public String getPBKey()
	{
		return pbKey;
	}
}
