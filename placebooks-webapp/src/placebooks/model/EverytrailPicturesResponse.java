/**
 * 
 */
package placebooks.model;

import java.util.Collection;
import java.util.HashMap;
import org.w3c.dom.Node;

/**
 * @author pszmp This is a class to contain the response from the everytrail api for a list of users
 *         pictures
 */
public class EverytrailPicturesResponse
{
	private HashMap<String, Node> picturesMap;
	private HashMap<String, String> pictureTrips;
	private HashMap<String, String> tripNames;
	private String status;

	/**
	 * 
	 */
	public EverytrailPicturesResponse(final String status, final HashMap<String, Node> picturesMap, 
			 final HashMap<String, String> pictureTrips,  final HashMap<String, String> tripNames)
	{
		this.status = status;
		this.picturesMap = picturesMap;
		this.pictureTrips = pictureTrips;
		this.tripNames = tripNames;
	}

	public Collection<Node> getPictures()
	{
		return this.picturesMap.values();
	}

	/**
	 * Get a map listing pictures with key of trip_id
	 * @return Map<String, Node>
	 */
	public HashMap<String, Node> getPicturesMap()
	{
		return this.picturesMap;
	}
	
	public HashMap<String, String> getPictureTrips()
	{
		return this.pictureTrips;
	}
	
	
	public String getStatus()
	{
		return this.status;
	}
	
	public HashMap<String, String> getTripNames()
	{
		return this.tripNames;
	}
}
