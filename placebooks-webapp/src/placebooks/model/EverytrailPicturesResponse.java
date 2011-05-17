/**
 * 
 */
package placebooks.model;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * @author pszmp This is a class to contain the response from the everytrail api for a list of users
 *         pictures
 */
public class EverytrailPicturesResponse
{
	private Vector<Node> pictures;
	private String status;

	/**
	 * 
	 */
	public EverytrailPicturesResponse(final String status, final Vector<Node> pictures)
	{
		this.status = status;
		this.pictures = pictures;
	}

	public Vector<Node> getPictures()
	{
		return this.pictures;
	}

	public String getStatus()
	{
		return this.status;
	}
}
