/**
 * 
 */
package placebooks.model;

import java.util.Vector;

/**
 * @author pszmp
 * Object to hold the response from an Everrytraul tracks request
 */
public class EverytrailTracksResponse
{
	private String status;
	private Vector<String> tracks;
	
	/**
	 * 
	 */
	public EverytrailTracksResponse(String status, Vector<String> tracks)
	{
		this.status = status;
		tracks = new Vector<String>();
		this.tracks = tracks;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public Vector<String> getTrips()
	{
		return this.tracks;
	}
}
