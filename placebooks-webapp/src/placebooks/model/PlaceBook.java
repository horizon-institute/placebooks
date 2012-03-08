package placebooks.model;

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

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;


@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PlaceBook extends BoundaryGenerator
{
	
	protected static final Logger log = 
		Logger.getLogger(PlaceBook.class.getName());

	@JsonSerialize(using = placebooks.model.json.GeometryJSONSerializer.class)
	@JsonDeserialize(using = placebooks.model.json.GeometryJSONDeserializer.class)
	private Geometry geom; // Pertaining to the PlaceBook

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	// TODO: Cascading deletes: not sure about this
	@OneToMany(mappedBy = "placebook", cascade = ALL)
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// Searchable metadata attributes, e.g., title, description, etc.
	@ElementCollection @Column(columnDefinition="LONGTEXT")
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
		this.owner = p.getOwner();

		if (p.getGeometry() != null)
			this.geom = (Geometry)p.getGeometry().clone();
		else
			this.geom = null;
		this.timestamp = (Date)p.getTimestamp().clone();

		this.metadata = new HashMap<String, String>(p.getMetadata());

        for (PlaceBookItem item : p.getItems())
		{
			this.addItem(item.deepCopy());
		}

        log.info("Created copy of PlaceBook; old key = " + p.getKey());
    }

	// Make a new PlaceBook
	public PlaceBook(final User owner, final Geometry geom)
	{
		this();
		this.owner = owner;
		this.geom = geom;
		this.timestamp = new Date();

		log.info("Created new PlaceBook: timestamp=" 
				 + this.timestamp.toString());

	}

	public PlaceBook(final User owner, final Geometry geom, 
					 final List<PlaceBookItem> items)
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
			placeBookBinder.addSearchIndexedData(value);
	}

	public void calcBoundary()
	{
		final Set<Geometry> geoms = new HashSet<Geometry>();
		for (final PlaceBookItem p : getItems())
			geoms.add(p.getGeometry());

		final Geometry geom = calcBoundary(geoms);
		this.geom = geom;
		log.info("calcBoundary()= " + this.geom);
	}

	public Element createConfigurationRoot(final Document config)
	{
		log.info("PlaceBook.createConfigurationRoot(), key=" 
				 + this.getKey());
		final Element root = config.createElement(PlaceBook.class.getName());
		root.setAttribute("key", this.getKey());
		root.setAttribute("owner", this.getOwner().getKey());
		if (this.getOwner() == null)
		{
			log.error("Fatal error: PlaceBook " + this.getKey() + 
					  " has no owner");
		}

		if (getTimestamp() != null)
		{
			log.debug("Setting timestamp=" + this.getTimestamp().toString());
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(
				config.createTextNode(this.getTimestamp().toString())
			);
			root.appendChild(timestamp);
		}

		if (getGeometry() != null)
		{
			log.debug("Setting geometry=" + this.getGeometry().toText());
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(
				config.createTextNode(this.getGeometry().toText())
			);
			root.appendChild(geometry);
		}

		if (!metadata.isEmpty())
		{
			log.debug("Writing metadata to config");
			final Element sElem = config.createElement("metadata");
			log.info("metadata set size = " + metadata.size());
			for (final Map.Entry<String, String> e : metadata.entrySet())
			{
				log.info("Metadata element key, value=" + e.getKey().toString()
						 + ", " + e.getValue().toString());
				final Element elem = 	
					config.createElement(e.getKey().toString());
				elem.appendChild(config.createTextNode(
					e.getValue().toString())
				);
				sElem.appendChild(elem);
			}

			root.appendChild(sElem);
		}

		return root;
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
		for (PlaceBookItem pbi : getItems())
		{
			if (pbi.getClass().getName().equals(clazz.getName()))
				return true;
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

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setPlaceBookBinder(final PlaceBookBinder p)
	{
		this.placeBookBinder = p;
	}

	public final PlaceBookBinder getPlaceBookBinder()
	{
		return placeBookBinder;
	}
}
