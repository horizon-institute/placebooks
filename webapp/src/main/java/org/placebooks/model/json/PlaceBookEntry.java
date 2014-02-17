package org.placebooks.model.json;

import org.placebooks.model.PlaceBook;


public class PlaceBookEntry extends ShelfEntry
{
	private final String description;

	private final int numItems;

	// private String geometry;

	private String center;

	public PlaceBookEntry(final PlaceBook placebook)
	{
		super();
		setKey(placebook.getKey());
		setTitle(placebook.getMetadataValue("title"));
		setOwner(placebook.getOwner().getKey());
		setTimestamp(placebook.getTimestamp());
		description = placebook.getMetadataValue("description");
		numItems = placebook.getItems().size();

		if (placebook.getGeometry() != null)
		{
			// this.geometry = placebook.getGeometry().toString();
			center = placebook.getGeometry().getCentroid().toString();
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

}
