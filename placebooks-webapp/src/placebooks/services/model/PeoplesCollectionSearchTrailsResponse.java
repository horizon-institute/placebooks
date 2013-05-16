package placebooks.services.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Class to encapsulate response from Peoples Collection API for a list of Trails for a user
 * 
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

	public PeoplesCollectionSearchTrailsResponse(final ArrayList<PeoplesCollectionTrailListItem> mytrails,
			final ArrayList<PeoplesCollectionTrailListItem> myfavouritetrails, final int total_objects,
			final int per_page, final PeoplesCollectionLoginResponse authenticationresponse)
	{
		trails = mytrails;
		this.total_objects = total_objects;
		this.per_page = per_page;
	}

	public void AddItem(final PeoplesCollectionTrailListItem item)
	{
		trails.add(item);
	}

	public int GetPerPage()
	{
		return per_page;
	}

	public int GetTotalObjects()
	{
		return total_objects;
	}

	public ArrayList<PeoplesCollectionTrailListItem> GetTrails()
	{
		return trails;
	}

	public void SetPerPage(final int value)
	{
		per_page = value;
	}

	public void SetTotalObjects(final int value)
	{
		total_objects = value;
	}

}
