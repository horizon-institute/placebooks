package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

import placebooks.model.PlaceBook;

public class PlaceBookEntry
{
	@JsonProperty	
	private String description;

	@JsonProperty
	private String key;

	@JsonProperty
	private int numItems;

	@JsonProperty
	private String owner;

	@JsonProperty	
	private String packagePath;
	
	@JsonProperty	
	private String state;

	@JsonProperty
	private String title;
	
	@JsonProperty	
	private Date timestamp;

	public PlaceBookEntry()
	{
	}

	public PlaceBookEntry(final PlaceBook placebook)
	{
		this.key = placebook.getKey();
		this.description = placebook.getMetadataValue("description");
		this.title = placebook.getMetadataValue("title");
		this.numItems = placebook.getItems().size();
		this.owner = placebook.getOwner().getKey();
		this.timestamp = placebook.getTimestamp();
		this.packagePath = placebook.getPackagePath();
		this.state = placebook.getState().toString();
	}
	
	public String getDescription()
	{
		return description;
	}

	public String getKey()
	{
		return key;
	}
	
	public int getNumItems()
	{
		return numItems;
	}

	public String getOwner()
	{
		return owner;
	}
	
	public String getPackagePath()
	{
		return packagePath;
	}
	
	public String getState()
	{
		return state;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setKey(final String key)
	{
		this.key = key;
	}

	public void setNumItems(final int numItems)
	{
		this.numItems = numItems;
	}

	public void setOwner(final String owner)
	{
		this.owner = owner;
	}

	public void setPackagePath(final String packagePath)
	{
		this.packagePath = packagePath;
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
