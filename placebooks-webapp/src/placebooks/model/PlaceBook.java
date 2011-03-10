package placebooks.model;

import java.util.*;
import javax.jdo.annotations.*;

import org.apache.log4j.*;

import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public class PlaceBook
{

  	private static final Logger log = 
		Logger.getLogger(PlaceBook.class.getName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private int owner;
	
	@Persistent
	private Date timestamp;

	@Persistent
	private Geometry geom; // Pertaining to the PlaceBook

	@Persistent
	private List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();

	// The PlaceBook's configuration data
	@Persistent
	private HashMap<String, String> parameters;

	// Make a new PlaceBook
	public PlaceBook(int owner, Geometry geom, List<PlaceBookItem> items)
	{
		this.owner = owner;
		this.geom = geom;
		setItems(items);
		parameters = new HashMap<String, String>();
		parameters.put("test", "testing");

		this.timestamp = new Date();
	}


	// Clone an existing PlaceBook (i.e., make a copy)
	public void clone(String key)
	{

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
	}


	public void setItemKeys()
	{
		for (PlaceBookItem pbi : items) 
			pbi.setPBKey(key);
	}

/*
	private String configToXML()
	{
		// Helper method for converting config to something readable for mobile
		// package - maybe XML??
	}

	public String toMobilePackage()
	{
		// Maps configuration + contents of PlaceBook to something 
		// comprehensible to mobile app. Dumps contents to filesystem for 
		// download to mobile.
		
		// Make config into XML via configToXML()

		for (PlaceBookItem p : items) 
		{
			// do something with p that dumps p to disk
			p.toMobilePackage();
		}

		// Compress package and return location

	}*/


	public String getKey() { return key; }

	public void setOwner(int owner) { this.owner = owner; }
	public int getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void setGeometry(Geometry geom) { this.geom = geom; }
	public Geometry getGeometry() { return geom; }


}
