package org.placebooks.client.model;

public class Entry
{
	private String center;
	private String description;
	private float distance;
	private String key;
	private int numItems;
	private String owner;
	private String ownerName;
	private String previewImage;
	private int score;
	private String state;
	private String title;

	public Entry()
	{
	}

	public String getCenter()
	{
		return center;
	}

	public String getDescription()
	{
		return description;
	}

	public float getDistance()
	{
		return distance;
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

	public String getOwnerName()
	{
		return ownerName;
	}

	public String getPreviewImage()
	{
		return previewImage;
	}

	public int getScore()
	{
		return score;
	}

	public String getState()
	{
		return state;
	}

	public String getTitle()
	{
		return title;
	}

	public void setCenter(final String center)
	{
		this.center = center;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setDistance(final float distance)
	{
		this.distance = distance;
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

	public void setOwnerName(final String ownerName)
	{
		this.ownerName = ownerName;
	}

	public void setPreviewImage(final String previewImage)
	{
		this.previewImage = previewImage;
	}

	public void setScore(final int score)
	{
		this.score = score;
	}

	public void setState(final String state)
	{
		this.state = state;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

}
