package placebooks.services.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;


/**
 * Class to encapsulate response from Peoples Collection API for a list of Trails for a user 
 * @author pszmp
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionSearchTrailsResponse
{
	ArrayList<PeoplesCollectionTrailListItem> trails;
	int total_objects;
	int per_page;

	public PeoplesCollectionSearchTrailsResponse()
	{
		trails = new ArrayList<PeoplesCollectionTrailListItem>();
	}


	public PeoplesCollectionSearchTrailsResponse(ArrayList<PeoplesCollectionTrailListItem> mytrails,	ArrayList<PeoplesCollectionTrailListItem> myfavouritetrails,		int total_objects, int per_page,	PeoplesCollectionLoginResponse authenticationresponse)
	{
		this.trails = mytrails;
		this.total_objects = total_objects;
		this.per_page = per_page;
	}

	public ArrayList<PeoplesCollectionTrailListItem> GetTrails()
	{
		return trails;
	}

	public int GetTotalObjects()
	{
		return total_objects;
	}

	public int GetPerPage()
	{
		return per_page;
	}

	public void AddItem(PeoplesCollectionTrailListItem item)
	{
		trails.add(item);
	}

	public void SetTotalObjects(int value)
	{
		total_objects = value;
	}

	public void SetPerPage(int value)
	{
		per_page = value;
	}

	
}
