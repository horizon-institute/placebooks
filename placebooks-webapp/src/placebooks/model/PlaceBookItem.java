package placebooks.model;

import java.net.URL;
import java.util.*;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable(identityType=IdentityType.DATASTORE)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public abstract class PlaceBookItem 
{
	@NotPersistent
	protected static final Logger log = 
		Logger.getLogger(PlaceBookItem.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private PlaceBook placebook; // PlaceBook this PlaceBookItem belongs to

	@Persistent
	private User owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	private Geometry geom;

	@Persistent
	private URL sourceURL; // The original internet resource string if it exists

	// Searchable metadata attributes, e.g., title, description, etc.
	@Persistent
	private HashMap<String, String> metadata = new HashMap<String, String>();

	// The PlaceBookItem's configuration data
	@Persistent
	private HashMap<String, String> parameters = new HashMap<String, String>();

	// Make a new PlaceBookItem
	public PlaceBookItem(User owner, Geometry geom, URL sourceURL)
	{
		this.owner = owner;
		this.geom = geom;
		this.sourceURL = sourceURL;
		this.timestamp = new Date();

		log.info("Created new PlaceBookItem, concrete name: " 
				 + getEntityName() + ", timestamp=" 
				 + this.timestamp.toString());
	}

	/** Each class must append relevant configuration data
	 * @param config
	 * @param root
	 */
	public abstract void appendConfiguration(Document config, Element root);

	/** Provide the concrete entity name for this class, for XML mapping
	 * @return
	 */
	public abstract String getEntityName();

	/** Header common to all items
	 * @param config
	 * @return
	 */
	protected Element getConfigurationHeader(Document config)
	{
		log.info(getEntityName() + ": getConfigurationHeader");
		Element item = config.createElement(getEntityName());
		item.setAttribute("key", getKey());
		item.setAttribute("owner", getOwner().getKey());

		Element timestamp = config.createElement("timestamp");
		timestamp.appendChild(config.createTextNode(getTimestamp().toString()));
		item.appendChild(timestamp);

		// Geometry and sourceURL are acceptable as null

		if (getGeometry() != null)
		{
			Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(getGeometry().toText()));
			item.appendChild(geometry);
		}

		if (getSourceURL() != null)
		{
			Element url = config.createElement("url");
			url.appendChild(config.createTextNode(getSourceURL().toString()));
			item.appendChild(url);
		}

		return item;
	}


	/**
	 * 'GetHTML' will return a String containing the item's body content in 
	 * html format, suitable for including in the placebook view. (@TODO with 
	 * GUI programmer define/document how this will be used in GUI)
	 * @return String containing the HTML data for the content of the placebook
	 * item
	 */
	public abstract String GetHTML();

	/**
	 * Along with GetHTML this method is used to generate a string containing
	 * the CSS styles for the Placebook item, suitable for use in the Placebook
	 * page header
	 * @return String of CSS style data
	 */
	public abstract String GetCSS();
	
	/**
	 * Along with GetHTML this method is used to generate a string containing
	 * any required Javascript for the Placebook item
	 * @return String of Javascript code
	 */
	public abstract String GetJavaScript();


	public void addMetadataEntry(String key, String value)
	{
		metadata.put(key, value);
	}

	public String getMetadataValue(String key)
	{
		return metadata.get(key);
	}

	public Map<String, String> getMetadata()
	{
		return Collections.unmodifiableMap(metadata);
	}

	public void addParameterEntry(String key, String value)
	{
		parameters.put(key, value);
	}

	public String getParameterValue(String key)
	{
		return parameters.get(key);
	}

	public Map<String, String> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public String getKey() { return key; }

	public void setPlaceBook(PlaceBook placebook) { this.placebook = placebook; }
	public PlaceBook getPlaceBook() { return placebook; }

	public void setOwner(User owner) { this.owner = owner; }
	public User getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void setGeometry(Geometry geom) { this.geom = geom; }
	public Geometry getGeometry() { return geom; }

	public URL getSourceURL() { return sourceURL; }
	public void setSourceURL(URL sourceURL) { this.sourceURL = sourceURL; }

}

