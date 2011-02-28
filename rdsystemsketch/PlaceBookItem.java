package placebooks;

import java.net.URI;
import java.util.HashMap;

// PlaceBookItem represents data and methods all kinds of media making up an 
// individual PlaceBook provide, in common
public abstract class PlaceBookItem 
{

	// TODO: Explicit types are probably unnecessary: we should be able to do 
	// reflection
	public enum ItemType
	{
		TEXT, VIDEO, AUDIO, GPS, IMAGE, WEBBUNDLE
	}
		
	private int id;
	private int owner;
	private URI uri; // The original internet resource string if it exists
	private int type;
	private Geometry geometry; // Some representation of MySQL Geometry
	private Date timestamp;
	private int placebookId; // PlaceBook this PlaceBookItem belongs to, might 
							 // actually be object ref

	// The PlaceBookItem's parameters:
	// 		Layout on GUI
	// 		State data, etc.
	private HashMap<String, String> parameters;

	public PlaceBookItem()
	{
		// Make a new empty PlaceBookItem
	}

	public PlaceBookItem(int id)
	{
		// Recreate this PlaceBookItem from existing item in db, i.e., id
	}

	public void clone(int id)
	{
		// Clone an existing PlaceBookItem (i.e., make a copy of id)
	}

	// Each PlaceBookItem must implement a way of adding or updating itself as a
	// persistent database object
	public abstract PersistentItem toPersistentItem();

	// Each PlaceBookItem must output some HTML representation of itself(?). Not
	// sure whether this functionality should be here.
	public abstract Stream renderToHTML();

	// Each PlaceBookItem must implement a way of adding relevant content to a
	// mobile app package
	public abstract void toMobilePackage();
}
