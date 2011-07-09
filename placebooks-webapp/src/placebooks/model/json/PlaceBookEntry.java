package placebooks.model.json;

import java.util.Date;

import placebooks.model.PlaceBook;

public class PlaceBookEntry
{
	private String description;

	private String key;

	private int numItems;

	private String owner;

	private String packagePath;
	
	private String state;

	private String title;
	
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
	
	public String getState()
	{
		return state;
	}

	public int getNumItems()
	{
		return numItems;
	}
	
	public Date getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getOwner()
	{
		return owner;
	}

	public String getPackagePath()
	{
		return packagePath;
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

	public void setTitle(final String title)
	{
		this.title = title;
	}

}
