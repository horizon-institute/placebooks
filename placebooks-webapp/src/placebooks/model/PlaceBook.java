package placebooks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import placebooks.controller.PropertiesSingleton;
import placebooks.controller.SearchHelper;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class PlaceBook
{
	@NotPersistent
	protected static final Logger log = Logger.getLogger(PlaceBook.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private User owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	@JsonSerialize(using=placebooks.model.json.GeometryJSONSerializer.class)	
	@JsonDeserialize(using=placebooks.model.json.GeometryJSONDeserializer.class)
	private Geometry geom; // Pertaining to the PlaceBook

	// TODO: Cascading deletes via dependent=true: not sure about this
	@Persistent(mappedBy="placebook")
	@javax.jdo.annotations.Element(dependent = "true") 
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// Searchable metadata attributes, e.g., title, description, etc.
	@Persistent
	private Map<String, String> metadata = new HashMap<String, String>();

	@Persistent(mappedBy = "placebook", dependent = "true")
	private PlaceBookSearchIndex index = new PlaceBookSearchIndex();

	// Make a new PlaceBook
	public PlaceBook(User owner, Geometry geom)
	{
		this.owner = owner;
		if (owner != null)
			this.owner.add(this);
		this.geom = geom;
		this.timestamp = new Date();
		index.setPlaceBook(this);

		log.info("Created new PlaceBook: timestamp=" 
				 + this.timestamp.toString());

	}
	
	public PlaceBook(User owner, Geometry geom, List<PlaceBookItem> items)
	{
		this(owner, geom);
		setItems(items);
	}

	public String getPackagePath()
	{
		return PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") 
					+ getKey();
	}

	public Element createConfigurationRoot(Document config)
	{
		log.info("PlaceBook.appendConfiguration(), key=" + this.getKey());
		Element root = config.createElement(PlaceBook.class.getName());
		root.setAttribute("key", this.getKey());
		root.setAttribute("owner", this.getOwner().getKey());
		
		Element timestamp = config.createElement("timestamp");
		timestamp.appendChild(config.createTextNode(
								this.getTimestamp().toString()));
		root.appendChild(timestamp);

		Element geometry = config.createElement("geometry");
		geometry.appendChild(config.createTextNode(
								this.getGeometry().toText()));
		root.appendChild(geometry);

		if (!metadata.isEmpty())
		{
			Element sElem = config.createElement("metadata");

			for (Map.Entry<String, String> e: metadata.entrySet())
			{
				Element elem = config.createElement(e.getKey().toString());
				elem.appendChild(
					config.createTextNode(e.getValue().toString()));
				sElem.appendChild(elem);
			}

			root.appendChild(sElem);
		}

		return root;
	}

	public void setItems(List<PlaceBookItem> items)
	{
		this.items.clear();
		this.items.addAll(items);
	}

	public List<PlaceBookItem> getItems()
	{
		return Collections.unmodifiableList(items);
	}

	public void addItem(PlaceBookItem item) 
	{
  		items.add(item);
  		item.setPlaceBook(this);
	}

	public boolean removeItem(PlaceBookItem item)
	{
		item.setPlaceBook(null);
		return items.remove(item);
	}
	
	public void addMetadataEntry(String key, String value)
	{
		metadata.put(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public String getMetadataValue(String key)
	{
		return metadata.get(key);
	}

	public Map<String, String> getMetadata()
	{
		return Collections.unmodifiableMap(metadata);
	}

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
	}

	public String getKey() { return key; }

	public void setOwner(User owner) { this.owner = owner; }
	public User getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void setGeometry(Geometry geom) { this.geom = geom; }
	public Geometry getGeometry() { return geom; }
}
