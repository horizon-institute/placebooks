/**
 * 
 */
package placebooks.model;

import java.util.Vector;

/**
 * @author pszmp
 * This is a class to contain the response from the everytrail api for a list of users
 * pictures
 */
public class EverytrailPicturesResponse
{
	private String status;
	private Vector<String> pictures;
	
	/**
	 * 
	 */
	public EverytrailPicturesResponse(String status, Vector<String> pictures)
	{
		this.status = status;
		pictures = new Vector<String>();
		this.pictures = pictures;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public Vector<String> getPictures()
	{
		return this.pictures;
	}
}
