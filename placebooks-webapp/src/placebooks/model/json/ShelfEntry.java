package placebooks.model.json;

import java.util.Date;

public abstract class ShelfEntry
{
	private String title;

	private String key;

	private String owner;

	private Date timestamp;
	
	public ShelfEntry()
	{
	}

	public String getKey()
	{
		return key;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public Date getTimestamp()
	{
		return timestamp;
	}
	
	public String getTitle()
	{
		return title;
	}

	protected void setKey(final String key)
	{
		this.key = key;
	}

	protected void setOwner(final String owner)
	{
		this.owner = owner;
	}

	protected void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	protected void setTitle(final String title)
	{
		this.title = title;
	}
}