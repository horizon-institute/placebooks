package org.placebooks.model.json;

import org.placebooks.model.PlaceBookBinder;

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

	private final String activity;

	public PlaceBookBinderEntry(final PlaceBookBinder placebookBinder)
	{
		super();
		setKey(placebookBinder.getKey());
		setTitle(placebookBinder.getMetadataValue("title"));
		setOwner(placebookBinder.getOwner().getKey());
		setTimestamp(placebookBinder.getTimestamp());
		description = placebookBinder.getMetadataValue("description");
		activity = placebookBinder.getMetadataValue("activity");
		previewImage = placebookBinder.getMetadataValue("placebookImage");
		numPlaceBooks = placebookBinder.getPlaceBooks().size();
		packagePath = placebookBinder.getPackagePath();
		state = placebookBinder.getState().toString();
		permissions = placebookBinder.getPermissionsAsString();

		if (placebookBinder.getGeometry() != null)
		{
			// this.geometry = placebookBinder.getGeometry().toString();
			center = placebookBinder.getGeometry().getCentroid().toString();
		}
	}

	public String getActivity()
	{
		return activity;
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
