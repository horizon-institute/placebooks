package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class ShelfEntry
{
	@JsonProperty	
	private String title;

	@JsonProperty
	private String key;

	@JsonProperty
	private String owner;

	@JsonProperty	
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

	public void setKey(final String key)
	{
		this.key = key;
	}

	public void setOwner(final String owner)
	{
		this.owner = owner;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

}
