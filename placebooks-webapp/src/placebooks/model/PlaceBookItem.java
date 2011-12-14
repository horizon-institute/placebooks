package placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.TIMESTAMP;

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

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.EMFSingleton;
import placebooks.controller.ItemFactory;
import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS)
public abstract class PlaceBookItem implements IUpdateableExternal
{
	protected static final Logger log = Logger.getLogger(PlaceBookItem.class.getName());

	@JsonSerialize(using = placebooks.model.json.GeometryJSONSerializer.class)
	@JsonDeserialize(using = placebooks.model.json.GeometryJSONDeserializer.class)
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
	@ElementCollection	@Column(columnDefinition="LONGTEXT")
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

	PlaceBookItem()
	{
		index.setPlaceBookItem(this);
	}

	public PlaceBookItem(final PlaceBookItem p)
	{
		this.owner = p.getOwner();
		if (p.getGeometry() != null)
		{
			this.geom = (Geometry) p.getGeometry().clone();
		}
		else
		{
			this.geom = null;
		}

		if (p.getSourceURL() != null)
		{
			this.sourceURL = new String(p.getSourceURL().toString());
		}
		else
		{
			this.sourceURL = null;
		}

		this.parameters = new HashMap<String, Integer>(p.getParameters());
		this.metadata = new HashMap<String, String>(p.getMetadata());

		index.setPlaceBookItem(this);
		this.index.addAll(p.getSearchIndex().getIndex());

		if (p.getTimestamp() != null)
		{
			this.timestamp = (Date) p.getTimestamp().clone();
			log.info("Copied PlaceBookItem, concrete name: " + getEntityName() + ", timestamp="
					+ this.timestamp.toString());
		}
		else
		{
			this.timestamp = null;
			log.info("Copied PlaceBookItem, concrete name: " + getEntityName());
		}
	}

	// Make a new PlaceBookItem
	public PlaceBookItem(final User owner, final Geometry geom, final URL sourceURL)
	{
		this.owner = owner;
		this.geom = geom;
		this.timestamp = new Date();
		if (sourceURL != null)
			this.sourceURL = sourceURL.toExternalForm();
		else
			this.sourceURL = null;

		index.setPlaceBookItem(this);
		log.info("Created new PlaceBookItem, concrete name: " + getEntityName() + ", timestamp="
				+ this.timestamp.toString());
	}

	private final static int VALUE_LENGTH_LIMIT = 511;

	public void addMetadataEntry(String key, String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			if (key.length() > VALUE_LENGTH_LIMIT)
			{
				log.warn("Metadata Key entry too long, truncating: " + key);
				key = key.substring(0, VALUE_LENGTH_LIMIT);
			}
			if (value.length() > 511)
			{
				log.warn("Metadata Value entry too long, truncating: " + value);
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

	/**
	 * Each class must append relevant configuration data
	 * 
	 * @param config
	 * @param root
	 */
	public abstract void appendConfiguration(Document config, Element root);

	public abstract PlaceBookItem deepCopy();

	/**
	 * Each class must provide a method for deleting any data sitting on disk
	 */
	public abstract void deleteItemData();

	/**
	 * Header common to all items
	 * 
	 * @param config
	 * @return
	 */
	protected Element getConfigurationHeader(final Document config)
	{
		log.info(getEntityName() + ": getConfigurationHeader");
		final Element item = config.createElement(getEntityName());
		item.setAttribute("key", getKey());
		item.setAttribute("owner", getOwner().getKey());

		// Timestamp is ok to be null
		if (getTimestamp() != null)
		{
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(config.createTextNode(getTimestamp().toString()));
			item.appendChild(timestamp);
		}

		// Geometry and sourceURL are acceptable as null

		if (getGeometry() != null)
		{
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(getGeometry().toText()));
			item.appendChild(geometry);
		}

		if (getSourceURL() != null)
		{
			final Element url = config.createElement("url");
			url.appendChild(config.createTextNode(getSourceURL().toExternalForm()));
			item.appendChild(url);
		}

		// Write metadata and parameters
		if (this.hasMetadata())
		{
			item.appendChild(setToConfig(config, this.getMetadata(), "metadata"));
		}
		if (this.hasParameters())
		{
			item.appendChild(setToConfig(config, this.getParameters(), "parameters"));
		}

		return item;
	}

	public abstract String getEntityName();

	@Override
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
		catch (Exception e)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#SaveUpdatedItem(placebooks.model.PlaceBookItem)
	 */
	@Override
	public IUpdateableExternal saveUpdatedItem()
	{
		IUpdateableExternal returnItem = this;
		final EntityManager em = EMFSingleton.getEntityManager();
		IUpdateableExternal item;
		try
		{
			em.getTransaction().begin();
			item = ItemFactory.GetExistingItem(this, em);
			if (item != null)
			{

				log.debug("Existing item found so updating");
				item.update(this);
				returnItem = item;
				em.flush();
			}
			else
			{
				log.debug("No existing item found so creating new");
				em.persist(this);
			}
			em.getTransaction().commit();
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				log.error("Rolling current saveUpdatedItem transaction back");
			}
			em.close();
		}
		return returnItem;
	}

	public void setExternalID(final String id)
	{
		this.externalID = id;
	}

	public void setGeometry(final Geometry geom)
	{
		this.geom = geom;
	}

	public void setMedataData(final Map<String, String> new_data)
	{
		this.metadata.putAll(new_data);
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setParameters(final Map<String, Integer> new_data)
	{
		this.parameters.putAll(new_data);
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
	}

	public void setSourceURL(final URL sourceURL)
	{
		if (sourceURL != null)
			this.sourceURL = sourceURL.toExternalForm();
		else
			this.sourceURL = null;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	private Element setToConfig(final Document config, final Map<String, ?> m, final String name)
	{
		if (!m.isEmpty())
		{
			final Element sElem = config.createElement(name);
			for (final Map.Entry<String, ?> e : m.entrySet())
			{
				final Element elem = config.createElement(e.getKey());
				elem.appendChild(config.createTextNode(e.getValue().toString()));
				sElem.appendChild(elem);
			}

			return sElem;
		}

		return null;
	}

	@Override
	public final void update(IUpdateableExternal item)
	{
		if (this == item) { return; }

		if (item == null) { throw new NullPointerException(); }

		updateItem(item);
	}

	/**
	 * Implementation of 'update' for placebook item superclass to update all base fields. This
	 * should be called from descendant classes in their implementation of 'update'
	 * 
	 * @param item
	 */
	protected void updateItem(IUpdateableExternal updateItem)
	{
		PlaceBookItem item = (PlaceBookItem) updateItem;
		parameters.clear();
		for (Entry<String, Integer> entry : item.getParameters().entrySet())
		{
			addParameterEntry(entry.getKey(), entry.getValue());
		}

		index.clear();
		metadata.clear();
		for (Entry<String, String> entry : item.getMetadata().entrySet())
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
