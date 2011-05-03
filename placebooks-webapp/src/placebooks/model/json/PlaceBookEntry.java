package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlaceBookEntry
{
	@JsonProperty
	private String key;

	@JsonProperty
	private String owner;

	@JsonProperty
	private String title;

	@JsonProperty
	private int numItems;

	@JsonProperty
	private String description;

	@JsonProperty
	private String packagePath;

	public PlaceBookEntry() {}

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	
	public String getOwner() { return owner; }
	public void setOwner(String owner) { this.owner = owner; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public int getNumItems() { return numItems; }
	public void setNumItems(int numItems) { this.numItems = numItems; }

	public String getDescription() { return description; }
	public void setDescription(String description)
	{ 
		this.description = description; 
	}
	
	public String getPackagePath() { return packagePath; }
	public void setPackagePath(String packagePath) 
	{ 
		this.packagePath = packagePath; 
	}

}
