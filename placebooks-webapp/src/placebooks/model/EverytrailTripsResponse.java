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
public class EverytrailTripsResponse
{
	private String status;
	private Vector<Node> trips;
	
	/**
	 * 
	 */
	public EverytrailTripsResponse(String status, Vector<Node> trips)
	{
		this.status = status;
		this.trips = trips;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public Vector<Node> getTrips()
	{
		return this.trips;
	}
}
