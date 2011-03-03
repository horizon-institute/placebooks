package placebooks;

import java.net.URI;
import java.util.HashMap;

/**
 * @author pszmp
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

	/*  'RenderBodyHTML' will return a stream containing the item's body content in html 
	 *  format, suitable for including in the placebook view. (@TODO with GUI programmer
	 *   define/document how this will be used in GUI)
	 */
	public abstract Stream renderToHTML();
	// Each PlaceBookItem must output some HTML representation of itself(?). Not
	// sure whether this functionality should be here.
	

	// Each PlaceBookItem must implement a way of adding relevant content to a
	// mobile app package
	public abstract void toMobilePackage();
}
