package placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;
import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;


@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PlaceBook
{
	
	public enum Permission
	{
		R("r"), W("w"), R_W("r+w"); 
		
		private String perms;
		
		private Permission(final String perms)
		{
			this.perms = perms;
		}

		public final String toString()
		{
			return perms;
		}
	}

	public enum State
	{
		UNPUBLISHED(0),
		PUBLISHED(1);
		
		private int value;
		
		private static final Map<Integer, State> lu = 
			new HashMap<Integer, State>();

		static
		{
			for (State s : EnumSet.allOf(State.class))
				lu.put(s.getValue(), s);
		}

		private State(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public static State get(int value)
		{
			return lu.get(value);
		}

	}

	protected static final Logger log = 
		Logger.getLogger(PlaceBook.class.getName());

	@JsonSerialize(using = placebooks.model.json.GeometryJSONSerializer.class)
	@JsonDeserialize(using = placebooks.model.json.GeometryJSONDeserializer.class)
	private Geometry geom; // Pertaining to the PlaceBook

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@JsonIgnore
	@OneToOne(cascade = ALL, mappedBy = "placebook")
	private PlaceBookSearchIndex index = new PlaceBookSearchIndex();

	// TODO: Cascading deletes: not sure about this
	@OneToMany(mappedBy = "placebook", cascade = ALL)
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// Searchable metadata attributes, e.g., title, description, etc.
	@ElementCollection
	private Map<String, String> metadata = new HashMap<String, String>();

	@ManyToOne
	private User owner;

	@Temporal(TIMESTAMP)
	private Date timestamp;

	private State state = State.UNPUBLISHED;
	
	@JsonIgnore
	private Map<User, Permission> perms = new HashMap<User, Permission>();
	

	public PlaceBook()
	{
		index.setPlaceBook(this);		
	}

	// Copy constructor
	public PlaceBook(final PlaceBook p)
	{
		this.owner = p.getOwner();
		if (this.owner != null)
			this.owner.add(this);

		perms.putAll(p.getPermissions());

		if(p.getGeometry() != null)
		{
			this.geom = (Geometry)p.getGeometry().clone();
		}
		else
		{
			this.geom = null;
		}
		this.timestamp = (Date)p.getTimestamp().clone();

		this.metadata = new HashMap<String, String>(p.getMetadata());

		index.setPlaceBook(this);
		this.index.addAll(p.getSearchIndex().getIndex());

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
		this.state = State.UNPUBLISHED;
		this.owner = owner;
		if (owner != null)
		{
			this.owner.add(this);
			perms.put(owner, Permission.R_W);		
		}
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

	public void addMetadataEntry(final String key, final String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			metadata.put(key, value);
		}
	}

	public void addMetadataEntryIndexed(final String key, final String value)
	{
		addMetadataEntry(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public void calcBoundary()
	{
		Geometry bounds = null;
		float minLat = Float.POSITIVE_INFINITY;
		float maxLat = Float.NEGATIVE_INFINITY;
		float minLon = Float.POSITIVE_INFINITY;
		float maxLon = Float.NEGATIVE_INFINITY;
		boolean emptySet = false;

		for (PlaceBookItem item : getItems())
		{
			final Geometry g = item.getGeometry();
			if (g != null)
			{
				// A Geometry with no dimensions has to be handled
				System.out.println("Inclunding item " + item.getClass().getSimpleName() + ":"+ item.getKey() + " = " + g.toText());
				if (g.getBoundary().isEmpty()) 
				{
					Coordinate[] cs = g.getCoordinates();
					for (Coordinate c : cs)
					{
						minLat = Math.min(minLat, (float)c.x);
						maxLat = Math.max(maxLat, (float)c.x);
						minLon = Math.min(minLon, (float)c.y);
						maxLon = Math.max(maxLon, (float)c.y);
						emptySet = true;
					}
				}
				else
				{
					if (bounds != null)
						bounds = g.union(bounds);
					else
						bounds = g;
				}
			}
		}

		if (emptySet)
		{
			try
			{
				Geometry empty = new WKTReader().read(
								"POLYGON ((" + minLat + " " + minLon + ", "
											 + minLat + " " + maxLon + ", "
											 + maxLat + " " + maxLon + ", "
											 + maxLat + " " + minLon + ", "
											 + minLat + " " + minLon + "))");
				log.info("empty=" + empty);
				if (bounds != null)
					bounds = empty.union(bounds);
				else
					bounds = empty;
			}
			catch (final Throwable e)
			{
				log.error(e.toString());
			}

		}

		if (bounds != null)
		{
			geom = bounds.getBoundary();
		}
		else
		{
			geom = null;
		}
		log.info("calcBoundary()= " + geom);
	}

	public Element createConfigurationRoot(final Document config)
	{
		log.info("PlaceBook.appendConfiguration(), key=" + this.getKey());
		final Element root = config.createElement(PlaceBook.class.getName());
		root.setAttribute("key", this.getKey());
		root.setAttribute("owner", this.getOwner().getKey());

		if (getTimestamp() != null)
		{
			log.info("Setting timestamp=" + this.getTimestamp().toString());
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(
				config.createTextNode(this.getTimestamp().toString())
			);
			root.appendChild(timestamp);
		}

		if (getGeometry() != null)
		{
			log.info("Setting geometry=" + this.getGeometry().toText());
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(
				config.createTextNode(this.getGeometry().toText())
			);
			root.appendChild(geometry);
		}

		if (!metadata.isEmpty())
		{
			log.info("Writing metadata to config");
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

	public void setPermission(final User user, final Permission p)
	{
		if (perms.get(user) != null)
			perms.remove(user);
		perms.put(user, p);
	}

	public final Permission getPermission(final User user)
	{
		return perms.get(user);
	}

	public final Map<User, Permission> getPermissions()
	{
		return Collections.unmodifiableMap(perms);
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

	public String getPackagePath()
	{
		return PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + "/" + getKey();
	}

	public State getState()
	{
		return state;
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

	public void setState(State state)
	{
		this.state = state;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public final PlaceBookSearchIndex getSearchIndex()
	{
		return index;
	}
}
