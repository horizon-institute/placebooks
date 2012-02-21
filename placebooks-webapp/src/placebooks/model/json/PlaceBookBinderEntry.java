package placebooks.model.json;

import placebooks.model.PlaceBookBinder;

public class PlaceBookBinderEntry extends ShelfEntry
{
	private final String description;

	private final int numPlaceBooks;

	private final String packagePath;

	private final String state;

	// private String geometry;

	private String center;

	private final String previewImage;

	private final String permissions;

	public PlaceBookBinderEntry(final PlaceBookBinder placebookBinder)
	{
		super();
		setKey(placebookBinder.getKey());
		setTitle(placebookBinder.getMetadataValue("title"));
		setOwner(placebookBinder.getOwner().getKey());
		setTimestamp(placebookBinder.getTimestamp());
		this.description = placebookBinder.getMetadataValue("description");
		this.previewImage = placebookBinder.getMetadataValue("placebookImage");
		this.numPlaceBooks = placebookBinder.getPlaceBooks().size();
		this.packagePath = placebookBinder.getPackagePath();
		this.state = placebookBinder.getState().toString();
		this.permissions = placebookBinder.getPermissionsAsString();

		if (placebookBinder.getGeometry() != null)
		{
			// this.geometry = placebookBinder.getGeometry().toString();
			this.center = placebookBinder.getGeometry().getCentroid().toString();
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

	public int getNumPlaceBooks()
	{
		return numPlaceBooks;
	}

	public String getPackagePath()
	{
		return packagePath;
	}

	public String getPermissions()
	{
		return permissions;
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
