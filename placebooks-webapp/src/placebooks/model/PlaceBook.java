package placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;
import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PlaceBook
{
	protected static final Logger log = Logger.getLogger(PlaceBook.class.getName());

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
	
	//@JsonIgnore
	//private Permissions readPermissions;

	// Make a new PlaceBook
	public PlaceBook(final User owner, final Geometry geom)
	{
		this.owner = owner;
		if (owner != null)
		{
			this.owner.add(this);
		}
		this.geom = geom;
		this.timestamp = new Date();
		index.setPlaceBook(this);

		log.info("Created new PlaceBook: timestamp=" 
				 + this.timestamp.toString());

	}

	public PlaceBook(final User owner, final Geometry geom, 
					 final List<PlaceBookItem> items)
	{
		this(owner, geom);
		setItems(items);
	}

	PlaceBook()
	{
		index.setPlaceBook(this);		
	}

	public void addItem(final PlaceBookItem item)
	{
		items.add(item);
		item.setPlaceBook(this);
	}

	public void addMetadataEntry(final String key, final String value)
	{
		metadata.put(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public Element createConfigurationRoot(final Document config)
	{
		log.info("PlaceBook.appendConfiguration(), key=" + this.getKey());
		final Element root = config.createElement(PlaceBook.class.getName());
		root.setAttribute("key", this.getKey());
		root.setAttribute("owner", this.getOwner().getKey());

		if (getTimestamp() != null)
		{
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(
				config.createTextNode(this.getTimestamp().toString())
			);
			root.appendChild(timestamp);
		}

		if (getGeometry() != null)
		{
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(
				config.createTextNode(this.getGeometry().toText())
			);
			root.appendChild(geometry);
		}

		if (!metadata.isEmpty())
		{
			final Element sElem = config.createElement("metadata");

			for (final Map.Entry<String, String> e : metadata.entrySet())
			{
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

	public String getPackagePath()
	{
		return PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + getKey();
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
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
	
	//public boolean canRead(final User user)
	//{
	//	return readPermissions.canAccess(owner, user);
	//}
}
