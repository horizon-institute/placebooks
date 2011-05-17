/**
 * 
 */
package placebooks.model;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * @author pszmp Object to hold the response from an Everrytraul tracks request
 */
public class EverytrailTracksResponse
{
	private String status;
	private Vector<Node> tracks;

	/**
	 * 
	 */
	public EverytrailTracksResponse(final String status, final Vector<Node> tracks)
	{
		this.status = status;
		this.tracks = tracks;
	}

	public String getStatus()
	{
		return this.status;
	}

	public Vector<Node> getTracks()
	{
		return this.tracks;
	}
}
