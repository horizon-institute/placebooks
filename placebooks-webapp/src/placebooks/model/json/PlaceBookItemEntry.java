package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

import placebooks.model.PlaceBookItem;

public class PlaceBookItemEntry extends ShelfEntry
{
	@JsonProperty
	private String pbKey;
	
	public PlaceBookItemEntry()
	{
		super();
	}

	public PlaceBookItemEntry(final PlaceBookItem p)
	{
		super();
		setKey(p.getKey());
		this.pbKey = p.getPlaceBook().getKey();
		setTitle(p.getMetadataValue("title"));
		setOwner(p.getOwner().getKey());
		setTimestamp(p.getTimestamp());
	}
	
	public String getPBKey()
	{
		return pbKey;
	}

	public void setPBKey(final String pbKey)
	{
		this.pbKey = pbKey;
	}

}
