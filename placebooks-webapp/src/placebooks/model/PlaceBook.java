package placebooks.model;

import java.util.*;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable(identityType=IdentityType.DATASTORE)
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public class PlaceBook
{
  	//private static final Logger log = 
	//	Logger.getLogger(PlaceBook.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private User owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	private Geometry geom; // Pertaining to the PlaceBook

	@Persistent(mappedBy="placebook")
	@Element(dependent = "true") // TODO: Cascading deletes: not sure about this
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// Searchable metadata attributes, e.g., title, description, etc.
	@Persistent
	private Map<String, String> metadata = new HashMap<String, String>();

	// The PlaceBook's configuration data
	@Persistent
	private Map<String, String> parameters = new HashMap<String, String>();

	@Persistent(dependent="true")
	private PlaceBookIndex index;

	// Make a new PlaceBook
	public PlaceBook(User owner, Geometry geom)
	{
		this.owner = owner;
		this.owner.add(this);
		this.geom = geom;
		this.timestamp = new Date();
	}
	
	public PlaceBook(User owner, Geometry geom, List<PlaceBookItem> items)
	{
		this(owner, geom);
		setItems(items);
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

	public void addMetadata(String key, String value)
	{
		metadata.put(key, value);
	}

	public String getMetadata(String key)
	{
		return metadata.get(key);
	}
	public void addParameter(String key, String value)
	{
		parameters.put(key, value);
	}

	public String getParameter(String key)
	{
		return parameters.get(key);
	}

	public String getKey() { return key; }

	public void setOwner(User owner) { this.owner = owner; }
	public User getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void setGeometry(Geometry geom) { this.geom = geom; }
	public Geometry getGeometry() { return geom; }
}
