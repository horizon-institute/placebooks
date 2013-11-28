package org.placebooks.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.log4j.Logger;
import org.placebooks.model.json.JsonIgnore;

@Entity
public class PlaceBookGroup
{
	protected static final Logger log = Logger.getLogger(PlaceBookGroup.class.getName());

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	private String title;

	private ImageItem image;

	private String description;

	@JsonIgnore
	private Collection<PlaceBookBinder> placebooks = new ArrayList<PlaceBookBinder>();

	@ManyToOne
	@JsonIgnore	
	private User owner;

	public String getDescription()
	{
		return description;
	}

	public String getId()
	{
		return id;
	}

	public ImageItem getImage()
	{
		return image;
	}

	public User getOwner()
	{
		return owner;
	}

	public Iterable<PlaceBookBinder> getPlaceBooks()
	{
		return placebooks;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setImage(final ImageItem image)
	{
		this.image = image;
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}
	
	public void setPlaceBooks(final Collection<PlaceBookBinder> placebooks)
	{
		this.placebooks = placebooks;
	}

	public void update(PlaceBookGroup group)
	{
		this.title = group.getTitle();
		this.description = group.getDescription();
		this.image = group.getImage();
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof PlaceBookGroup)
		{
			return ((PlaceBookGroup)obj).getId().equals(id);
		}
		return false;
	}

	public void add(PlaceBookBinder binder)
	{
		placebooks.add(binder);		
	}

	public void remove(PlaceBookBinder binder)
	{
		placebooks.remove(binder);		
	}
}