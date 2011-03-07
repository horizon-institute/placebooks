package placebooks.model;

import java.util.*;
import javax.jdo.annotations.*;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public class PlaceBook
{
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
	private ArrayList<PlaceBookItem> items;

	// The PlaceBook's configuration data
	@Persistent
	private HashMap<String, String> parameters;

	public PlaceBook() { }

	// Make a new PlaceBook
	public PlaceBook(int owner, Geometry geom)
	{
		this.owner = owner;
		this.geom = geom;
		parameters = new HashMap<String, String>();
		parameters.put("test", "testing");

		this.timestamp = new Date();
		
		items = new ArrayList<PlaceBookItem>();
	}

	// Restore a PlaceBook from existing one in db
	public PlaceBook(String key)
	{

	}

	// Clone an existing PlaceBook (i.e., make a copy)
	public void clone(String key)
	{

	}

	public void addItem(PlaceBookItem pbi)
	{
		items.add(pbi);
	}

	public ArrayList<PlaceBookItem> getItems()
	{
		return items;
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
