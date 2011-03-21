/**
 * 
 */
package placebooks.model;

import java.util.Vector;

/**
 * @author pszmp
 *
 */
public class EverytrailTripsResponse
{
	private String status;
	private Vector<String> trips;
	
	/**
	 * 
	 */
	public EverytrailTripsResponse(String status, Vector<String> trips)
	{
		this.status = status;
		trips = new Vector<String>();
		this.trips = trips;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public Vector<String> getTrips()
	{
		return this.trips;
	}
}
