package placebooks;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceBook
{
	private int id;
	private int owner;
	private Date timestamp;
	private Geometry geometry; // Pertaining to the PlaceBook

	private ArrayList<PlaceBookItem> items;

	// The PlaceBook's configuration data
	private HashMap<String, String> parameters;


	public PlaceBook()
	{
		// Make a new PlaceBook
	}

	public PlaceBook(int id)
	{
		// Restore a PlaceBook from existing one in db, i.e., id
	}

	public void clone(int id)
	{
		// Clone an existing PlaceBook (i.e., make a copy of id)
	}

	public void persistPlaceBook()
	{
		// Persist / update this PlaceBook object's data, excluding items
		// id, owner, timestamp, geometry, etc...
		...

		// Persist config
		...
	
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
		...

		for (PlaceBookItem p : items) 
		{
			// do something with p that dumps p to disk
			p.toMobilePackage();
		}

		// Compress package and return location

	}
}
