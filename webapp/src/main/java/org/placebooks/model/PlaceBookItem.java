package org.placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

import org.placebooks.controller.EMFSingleton;
import org.placebooks.controller.ItemFactory;
import org.placebooks.controller.SearchHelper;
import org.placebooks.model.json.JsonIgnore;

import com.vividsolutions.jts.geom.Geometry;
import org.wornchaos.logger.Log;

@Entity
public abstract class PlaceBookItem
{
	private Geometry geom;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	// External id is a string key to identify externally sourced data and consists of the source
	// name
	// e.g (everytrail) and it's id with a hyphen in between.... "everytrail-123456"
	protected String externalID = null;

	@OneToOne(cascade = ALL, mappedBy = "item")
	@JsonIgnore
	protected PlaceBookItemSearchIndex index = new PlaceBookItemSearchIndex();

	// Searchable metadata attributes, e.g., title, description, etc.
	@ElementCollection
	@Column(columnDefinition = "LONGTEXT")
	private Map<String, String> metadata = new HashMap<String, String>();

	@JsonIgnore
	private User owner;

	// The PlaceBookItem's configuration data
	@ElementCollection
	private Map<String, Integer> parameters = new HashMap<String, Integer>();

	@JsonIgnore
	@ManyToOne
	private PlaceBook placebook; // PlaceBook this PlaceBookItem belongs to

	private String sourceURL; // The original internet resource string if it
								// exists

	@Temporal(TIMESTAMP)
	private Date timestamp;

	private final static int VALUE_LENGTH_LIMIT = 511;

	public PlaceBookItem(final PlaceBookItem p)
	{
		owner = p.getOwner();
		if (p.getGeometry() != null)
		{
			geom = (Geometry) p.getGeometry().clone();
		}
		else
		{
			geom = null;
		}

		if (p.getSourceURL() != null)
		{
			sourceURL = new String(p.getSourceURL().toString());
		}
		else
		{
			sourceURL = null;
		}

		parameters = new HashMap<String, Integer>(p.getParameters());
		metadata = new HashMap<String, String>(p.getMetadata());

		index.setPlaceBookItem(this);
		index.addAll(p.getSearchIndex().getIndex());

		if (p.getTimestamp() != null)
		{
			timestamp = (Date) p.getTimestamp().clone();
			Log.info("Copied PlaceBookItem, concrete name: " + getEntityName() + ", timestamp=" + timestamp.toString());
		}
		else
		{
			timestamp = null;
			Log.info("Copied PlaceBookItem, concrete name: " + getEntityName());
		}
	}

	// Make a new PlaceBookItem
	public PlaceBookItem(final User owner, final Geometry geom, final URL sourceURL)
	{
		this.owner = owner;
		this.geom = geom;
		timestamp = new Date();
		if (sourceURL != null)
		{
			this.sourceURL = sourceURL.toExternalForm();
		}
		else
		{
			this.sourceURL = null;
		}

		index.setPlaceBookItem(this);
		Log.info("Created new PlaceBookItem, concrete name: " + getEntityName() + ", timestamp=" + timestamp.toString());
	}

	PlaceBookItem()
	{
		index.setPlaceBookItem(this);
	}

	public void addMetadataEntry(String key, String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			// Strip HTML tags since people seem unable to remove them
			// themselves before calling this method
			value = value.replaceAll("<(.|\n)*?>", "");
			if (key.length() > VALUE_LENGTH_LIMIT)
			{
				Log.warn("Metadata Key entry too long, truncating: " + key);
				key = key.substring(0, VALUE_LENGTH_LIMIT);
			}
			if (value.length() > 511)
			{
				Log.warn("Metadata Value entry too long, truncating: " + value);
				value = value.substring(0, VALUE_LENGTH_LIMIT);
			}
			metadata.put(key, value);
		}
	}

	public void addMetadataEntryIndexed(final String key, final String value)
	{
		addMetadataEntry(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public void addParameterEntry(final String key, final Integer value)
	{
		parameters.put(key, value);
	}

	public abstract void copyDataToPackage() throws IOException;

	public abstract PlaceBookItem deepCopy();

	/**
	 * Each class must provide a method for deleting any data sitting on disk
	 */
	public abstract boolean deleteItemData();

	public abstract String getEntityName();

	public String getExternalID()
	{
		return externalID;
	}

	public Geometry getGeometry()
	{
		return geom;
	}

	public String getKey()
	{
		return id;
	}

	public Map<String, String> getMetadata()
	{
		return Collections.unmodifiableMap(metadata);
	}

	public String getMetadataValue(final String key)
	{
		return metadata.get(key);
	}

	public User getOwner()
	{
		return owner;
	}

	public Map<String, Integer> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public Integer getParameterValue(final String key)
	{
		return parameters.get(key);
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}

	public PlaceBookItemSearchIndex getSearchIndex()
	{
		return index;
	}

	public URL getSourceURL()
	{
		if (sourceURL == null) { return null; }
		try
		{
			return new URL(sourceURL);
		}
		catch (final Exception e)
		{
			return null;
		}
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
	}

	public boolean hasParameters()
	{
		return (!parameters.isEmpty());
	}

	public PlaceBookItem saveUpdatedItem()
	{
		PlaceBookItem returnItem = this;
		final EntityManager em = EMFSingleton.getEntityManager();
		PlaceBookItem item;
		try
		{
			em.getTransaction().begin();
			item = ItemFactory.getExistingItem(this, em);
			if (item != null)
			{

				Log.debug("Existing item found so updating");
				item.update(this);
				returnItem = item;
				em.flush();
			}
			else
			{
				Log.debug("No existing item found so creating new");
				em.persist(this);
			}
			em.getTransaction().commit();
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				Log.error("Rolling current saveUpdatedItem transaction back");
			}
			em.close();
		}
		return returnItem;
	}

	public void setExternalID(final String id)
	{
		externalID = id;
	}

	public void setGeometry(final Geometry geom)
	{
		this.geom = geom;
	}

	public void setMedataData(final Map<String, String> new_data)
	{
		metadata.putAll(new_data);
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setParameters(final Map<String, Integer> new_data)
	{
		parameters.putAll(new_data);
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
	}

	public void setSourceURL(final URL sourceURL)
	{
		if (sourceURL != null)
		{
			this.sourceURL = sourceURL.toExternalForm();
		}
		else
		{
			this.sourceURL = null;
		}
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public final void update(final PlaceBookItem item)
	{
		if (this == item) { return; }

		if (item == null) { throw new NullPointerException(); }

		updateItem(item);
	}

	/**
	 * Implementation of 'update' for placebook item superclass to update all base fields. This
	 * should be called from descendant classes in their implementation of 'update'
	 */
	protected void updateItem(final PlaceBookItem updateItem)
	{
		final PlaceBookItem item = updateItem;
		parameters.clear();
		for (final Entry<String, Integer> entry : item.getParameters().entrySet())
		{
			addParameterEntry(entry.getKey(), entry.getValue());
		}

		index.clear();
		metadata.clear();
		for (final Entry<String, String> entry : item.getMetadata().entrySet())
		{
			addMetadataEntry(entry.getKey(), entry.getValue());
		}

		if (item.getGeometry() != null)
		{
			geom = item.getGeometry();
		}

		if (item.getSourceURL() != null)
		{
			sourceURL = item.getSourceURL().toExternalForm();
		}

		if (item.getTimestamp() == null)
		{
			item.setTimestamp(new Date());
		}
	}
}