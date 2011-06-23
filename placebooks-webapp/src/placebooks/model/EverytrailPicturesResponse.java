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
	private String status;

	/**
	 * 
	 */
	public EverytrailPicturesResponse(final String status, final HashMap<String, Node> picturesMap)
	{
		this.status = status;
		this.picturesMap = picturesMap;
	}

	/**
	 * Get a map listing pictures with key of trip_id
	 * @return Map<String, Node>
	 */
	public HashMap<String, Node> getPicturesMap()
	{
		return this.picturesMap;
	}

	public Collection<Node> getPictures()
	{
		return this.picturesMap.values();
	}
	
	public String getStatus()
	{
		return this.status;
	}
}
