package org.placebooks.client.model;

public class Group
{
	private String id;
	private String title;
	private String description;
	private Item image;

	public String getDescription()
	{
		return description;
	}

	public String getId()
	{
		return id;
	}

	public Item getImage()
	{
		return image;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setImage(final Item image)
	{
		this.image = image;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}
}
