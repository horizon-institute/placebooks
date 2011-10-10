package placebooks.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

import placebooks.model.PlaceBook;

public class PlaceBookEntry extends ShelfEntry
{
	@JsonProperty
	private String description;

	@JsonProperty
	private int numItems;

	@JsonProperty
	private String packagePath;

	@JsonProperty
	private String state;

	@JsonProperty
	private String previewImage;

	public PlaceBookEntry()
	{
		super();
	}

	public PlaceBookEntry(final PlaceBook placebook)
	{
		super();
		setKey(placebook.getKey());
		this.description = placebook.getMetadataValue("description");
		setTitle(placebook.getMetadataValue("title"));
		setPreviewImage(placebook.getMetadataValue("placebookImage"));
		this.numItems = placebook.getItems().size();
		setOwner(placebook.getOwner().getKey());
		setTimestamp(placebook.getTimestamp());
		this.packagePath = placebook.getPackagePath();
		this.state = placebook.getState().toString();
	}

	public String getDescription()
	{
		return description;
	}

	public int getNumItems()
	{
		return numItems;
	}

	public String getPackagePath()
	{
		return packagePath;
	}

	public String getPreviewImage()
	{
		return previewImage;
	}

	public String getState()
	{
		return state;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setNumItems(final int numItems)
	{
		this.numItems = numItems;
	}

	public void setPackagePath(final String packagePath)
	{
		this.packagePath = packagePath;
	}

	public void setPreviewImage(final String imageID)
	{
		this.previewImage = imageID;
	}

}
