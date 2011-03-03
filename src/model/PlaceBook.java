package placebooks.model;

import java.util.*;

import javax.jdo.annotations.*;

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

//	private Geometry geometry; // Pertaining to the PlaceBook

	//private ArrayList<PlaceBookItem> items;

	// The PlaceBook's configuration data
	private HashMap<String, String> parameters;


	// Make a new PlaceBook
	public PlaceBook(int owner)
	{
		this.owner = owner;
		this.timestamp = new Date();
	}

	// Restore a PlaceBook from existing one in db, i.e., id
	public PlaceBook(Object pb)
	{

	}

	public String getKey() { return key; }

	public void setOwner(int owner) { this.owner = owner; }
	public int getOwner() { return owner; }

	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public Date getTimestamp() { return timestamp; }

	public void clone(int id)
	{
		// Clone an existing PlaceBook (i.e., make a copy of id)
	}

	/*public void persistPlaceBook()
	{
		// Persist / update this PlaceBook object's data, excluding items
		// id, owner, timestamp, geometry, etc...

		// Persist config
	
		// Persist / update the contents of this PlaceBook with database
		for (PlaceBookItem p : items) 
		{
			// some persistence logic
		}

	}

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
}
