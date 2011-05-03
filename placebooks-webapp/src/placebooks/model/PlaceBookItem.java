package placebooks.model;

import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable(identityType=IdentityType.DATASTORE)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE)
@JsonTypeInfo(include=JsonTypeInfo.As.PROPERTY, use=JsonTypeInfo.Id.CLASS)
public abstract class PlaceBookItem 
{
	@NotPersistent
	protected static final Logger log = 
		Logger.getLogger(PlaceBookItem.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	@JsonSerialize(using=placebooks.model.json.PlaceBookKeyJSONSerializer.class)
	private PlaceBook placebook; // PlaceBook this PlaceBookItem belongs to

	@Persistent
	@JsonSerialize(using=placebooks.model.json.UserKeyJSONSerializer.class)	
	private User owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	@JsonSerialize(using=placebooks.model.json.GeometryJSONSerializer.class)
	private Geometry geom;

	@Persistent
	private URL sourceURL; // The original internet resource string if it exists

	// Searchable metadata attributes, e.g., title, description, etc.
	@Persistent
	private Map<String, String> metadata = new HashMap<String, String>();

	// The PlaceBookItem's configuration data
	@Persistent
	private Map<String, Integer> parameters = new HashMap<String, Integer>();

	// Make a new PlaceBookItem
	public PlaceBookItem(User owner, Geometry geom, URL sourceURL)
	{
		this.owner = owner;
		this.geom = geom;
		this.timestamp = new Date();
		this.sourceURL = sourceURL;
		log.info("Created new PlaceBookItem, concrete name: " 
				 + getEntityName() + ", timestamp=" 
				 + this.timestamp.toString());
	}

	/** Each class must provide a method for deleting any data sitting on disk
	 */
	public abstract void deleteItemData();
	
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

		// Write metadata and parameters
		if (this.hasMetadata())
		{
			item.appendChild(setToConfig(config,
										 this.getMetadata(), 
										 "metadata")
							);
		}
		if (this.hasParameters())
		{
			item.appendChild(setToConfig(config, 
										 this.getParameters(), 
							 			 "parameters")
							);
		}

		return item;
	}


	private Element setToConfig(Document config, Map<String,?> m, String name)
	{
		if (!m.isEmpty())
		{
			Element sElem = config.createElement(name);
			for (Map.Entry<String,?> e: m.entrySet())
			{
				Element elem = config.createElement(e.getKey());
				elem.appendChild(config.createTextNode(e.getValue().toString()));
				sElem.appendChild(elem);
			}

			return sElem;
		}

		return null;
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

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
	}

	public void addParameterEntry(String key, Integer value)
	{
		parameters.put(key, value);
	}

	public Integer getParameterValue(String key)
	{
		return parameters.get(key);
	}

	public Map<String, Integer> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public boolean hasParameters()
	{
		return (!parameters.isEmpty());
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
