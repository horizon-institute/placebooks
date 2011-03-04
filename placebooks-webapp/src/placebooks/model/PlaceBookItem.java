package placebooks.model;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import javax.jdo.annotations.*;

import com.vividsolutions.jts.geom.Geometry;

// PlaceBookItem represents data and methods all kinds of media making up an 
// individual PlaceBook provide, in common
@PersistenceCapable
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public abstract class PlaceBookItem 
{

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

	public PlaceBookItem() { }

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

	}

	// Restore this PlaceBookItem from existing item in db
	public PlaceBookItem(String key)
	{

	}

	// Clone an existing PlaceBookItem (i.e., make a copy)
	public void clone(String key)
	{

	}

/*
	// Each PlaceBookItem must output some HTML representation of itself(?). Not
	// sure whether this functionality should be here.
	public abstract Stream renderToHTML();

	// Each PlaceBookItem must implement a way of adding relevant content to a
	// mobile app package
	public abstract void toMobilePackage();
	*/


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
