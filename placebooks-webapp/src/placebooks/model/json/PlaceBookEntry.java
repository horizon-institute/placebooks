package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

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
	private String title;

	public PlaceBookEntry()
	{
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
