package org.placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

import org.placebooks.model.json.JsonIgnore;
import org.wornchaos.logger.Log;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class PlaceBook extends BoundaryGenerator
{
	private Geometry geom; // Pertaining to the PlaceBook

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	// TODO: Cascading deletes: not sure about this
	@OneToMany(mappedBy = "placebook", cascade = ALL)
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// Searchable metadata attributes, e.g., title, description, etc.
	@ElementCollection
	@Column(columnDefinition = "LONGTEXT")
	private Map<String, String> metadata = new HashMap<String, String>();

	@JsonIgnore
	@ManyToOne
	private User owner;

	@Temporal(TIMESTAMP)
	private Date timestamp;

	@JsonIgnore
	@ManyToOne
	private PlaceBookBinder placeBookBinder; // PlaceBookBinder that owns this

	public PlaceBook()
	{
	}

	// Copy constructor
	public PlaceBook(final PlaceBook p)
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
		timestamp = (Date) p.getTimestamp().clone();

		metadata = new HashMap<String, String>(p.getMetadata());

		for (final PlaceBookItem item : p.getItems())
		{
			addItem(item.deepCopy());
		}

		Log.info("Created copy of PlaceBook; old key = " + p.getKey());
	}

	// Make a new PlaceBook
	public PlaceBook(final User owner, final Geometry geom)
	{
		this();
		this.owner = owner;
		this.geom = geom;
		timestamp = new Date();

		Log.info("Created new PlaceBook: timestamp=" + timestamp.toString());

	}

	public PlaceBook(final User owner, final Geometry geom, final List<PlaceBookItem> items)
	{
		this(owner, geom);
		setItems(items);
	}

	public void addItem(final PlaceBookItem item)
	{
		items.add(item);
		item.setPlaceBook(this);
	}

	public void addMetadataEntry(final String key, String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			// Strip HTML tags
			value = value.replaceAll("<(.|\n)*?>", "");
			metadata.put(key, value);
		}
	}

	public void addMetadataEntryIndexed(final String key, final String value)
	{
		addMetadataEntry(key, value);
		if (placeBookBinder != null)
		{
			placeBookBinder.addSearchIndexedData(value);
		}
	}

	@Override
	public void calcBoundary()
	{
		final Set<Geometry> geoms = new HashSet<Geometry>();
		for (final PlaceBookItem p : getItems())
		{
			geoms.add(p.getGeometry());
		}

		final Geometry geom = calcBoundary(geoms);
		this.geom = geom;
		Log.info("calcBoundary()= " + this.geom);
	}

	public Geometry getGeometry()
	{
		return geom;
	}

	public List<PlaceBookItem> getItems()
	{
		return Collections.unmodifiableList(items);
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

	public final PlaceBookBinder getPlaceBookBinder()
	{
		return placeBookBinder;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
	}

	// Bit of a dirty hack
	public boolean hasPlaceBookItemClass(final Class<?> clazz)
	{
		for (final PlaceBookItem pbi : getItems())
		{
			if (pbi.getClass().getName().equals(clazz.getName())) { return true; }
		}

		return false;
	}

	public boolean removeItem(final PlaceBookItem item)
	{
		item.setPlaceBook(null);
		return items.remove(item);
	}

	public void setGeometry(final Geometry geom)
	{
		this.geom = geom;
	}

	public void setItems(final List<PlaceBookItem> items)
	{
		this.items.clear();
		this.items.addAll(items);
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setPlaceBookBinder(final PlaceBookBinder p)
	{
		placeBookBinder = p;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}
}
