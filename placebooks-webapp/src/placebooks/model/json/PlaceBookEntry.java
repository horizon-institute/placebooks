package placebooks.model.json;

import placebooks.model.PlaceBook;

public class PlaceBookEntry extends ShelfEntry
{
	private final String description;

	private final int numItems;

	private final String packagePath;

	private final String state;

	// private String geometry;

	private String center;

	private final String previewImage;

	public PlaceBookEntry(final PlaceBook placebook)
	{
		super();
		setKey(placebook.getKey());
		setTitle(placebook.getMetadataValue("title"));
		setOwner(placebook.getOwner().getKey());
		setTimestamp(placebook.getTimestamp());		
		this.description = placebook.getMetadataValue("description");
		this.previewImage = placebook.getMetadataValue("placebookImage");
		this.numItems = placebook.getItems().size();
		this.packagePath = placebook.getPackagePath();
		this.state = placebook.getState().toString();

		if (placebook.getGeometry() != null)
		{
			// this.geometry = placebook.getGeometry().toString();
			this.center = placebook.getGeometry().getCentroid().toString();
		}
	}

	public String getCenter()
	{
		return center;
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
}