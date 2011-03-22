package placebooks.model;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A PlacebookItem represents an item that appears in a Placebook, e.g. photo, map, bit 
 * of text or web url.  This is an Abstract class that defines the general funcitonality
 *  of PlacebookItems.  Decendant classes will be created that implement the specific 
 *  functionality for each content type (image, map, url, etc).
 *  
 * PlacebookItems consist of the 'PlacebookItemID' field (a unique ID) and a number of 
 * ItemParameters that are used to store the individual bits of data for an item (for 
 * example, if it's a text item, the text; if it's an external web page, it's URL).  
 * These will be accessed by the GetParameter and SetParameter methods, etc.
 * Every PlacebookItem _must_ have the two parameters "Name" and "Type".  
 *
 * Name is the user specified title of the Item in their Placebook (e.g. 'A photo of
 *  the sea') which will be plain text (n.b. should be parsed for HTML/bad things 
 *  by database and view methods).
 *  For packaging purposes the method 'GetData' is defined that will return the 
 *  PlacebookItem and it's associated ItemParameters in the format suitable for 
 *  packaging. 
 *  @TODO with MarkD; define/document this format)
 * 
 *  The methods 'RenderBodyHTML' and 'CreateCache' are both abstract methods, to be 
 *  implemented by the 'real' PlacebookItem classes. 
 *  
 *  'CreateCache' will generate a stream containing the data required for the 
 *  cached version of this page suitable for inclusion in a downloadable package 
 *  version of the Placebook. (@TODO with MarkD; define/document this format)
 */
@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public abstract class PlaceBookItem 
{
	protected static final Logger log = 
		Logger.getLogger(PlaceBookItem.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private String pbKey; // PlaceBook this PlaceBookItem belongs to

	@Persistent
	private int owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	private Geometry geom;

	@Persistent
	private URL sourceURL; // The original internet resource string if it exists

	// The PlaceBookItem's parameters:
	// 		Layout on GUI
	// 		State data, etc.
	@Persistent
	private HashMap<String, String> parameters;

	// Make a new PlaceBookItem
	public PlaceBookItem(int owner, Geometry geom, URL sourceURL)
	{
		this.owner = owner;
		this.geom = geom;
		this.pbKey = pbKey;
		this.sourceURL = sourceURL;

		parameters = new HashMap<String, String>();
		parameters.put("Testing this", "blah blah");

		this.timestamp = new Date();

		log.info("Created new PlaceBookItem, concrete name: " 
				 + getEntityName());
	}

	/** Restore this PlaceBookItem from existing item in db
	 * @param key
	 */
	public PlaceBookItem(String key)
	{

	}

	/** Clone an existing PlaceBookItem (i.e., make a copy)
	 * @param key
	 */
	public void clone(String key)
	{

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
		item.setAttribute("owner", Integer.toString(getOwner()));

		Element timestamp = config.createElement("timestamp");
		timestamp.appendChild(config.createTextNode(getTimestamp().toString()));
		item.appendChild(timestamp);

		Element geometry = config.createElement("geometry");
		geometry.appendChild(config.createTextNode(getGeometry().toText()));
		item.appendChild(geometry);

		Element url = config.createElement("url");
		url.appendChild(config.createTextNode(getSourceURL().toString()));
		item.appendChild(url);

		return item;
	}


	/**
	 *   'GteHTML' will return a String containing the item's body content in html 
	 *  format, suitable for including in the placebook view. (@TODO with GUI programmer
	 *   define/document how this will be used in GUI)
	 * @return String containing the HTML data for the content of the placebook item
	 */
	public abstract String GetHTML();

	/**
	 * Along with GetHTML this method is used to generate a string containing
	 * the CSS styles for the Placebook item, suitable for use in the Placebook page header
	 * @return String of CSS style data
	 */
	public abstract String GetCSS();
	
	/**
	 * Along with GetHTML this method is used to generate a string containing
	 * any required Javascript for the Placebook item
	 * @return String of Javascript code
	 */
	public abstract String GetJavaScript();
	

	public String getKey() { return key; }

	public void setPBKey(String pbKey) { this.pbKey = pbKey; }
	public String getPBKey() { return pbKey; }

	public void setOwner(int owner) { this.owner = owner; }
	public int getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void setGeometry(Geometry geom) { this.geom = geom; }
	public Geometry getGeometry() { return geom; }

	public URL getSourceURL() { return sourceURL; }
	public void setSourceURL(URL sourceURL) { this.sourceURL = sourceURL; }

}

