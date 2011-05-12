/**
 * 
 */
package placebooks.model;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * @author pszmp
 * 
 */
public class EverytrailVideosResponse
{
	private String status;
	private Vector<Node> trips;

	/**
	 * 
	 */
	public EverytrailVideosResponse(final String status, final Vector<Node> trips)
	{
		this.status = status;
		this.trips = trips;
	}

	public String getStatus()
	{
		return this.status;
	}

	public Vector<Node> getVideos()
	{
		return this.trips;
	}
}
