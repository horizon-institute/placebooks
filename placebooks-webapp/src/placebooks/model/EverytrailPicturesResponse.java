/**
 * 
 */
package placebooks.model;

import java.util.Vector;
import org.w3c.dom.Node;

/**
 * @author pszmp
 * This is a class to contain the response from the everytrail api for a list of users
 * pictures
 */
public class EverytrailPicturesResponse
{
	private String status;
	private Vector<Node> pictures;
	
	/**
	 * 
	 */
	public EverytrailPicturesResponse(String status, Vector<Node> pictures)
	{
		this.status = status;
		this.pictures = pictures;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public Vector<Node> getPictures()
	{
		return this.pictures;
	}
}
