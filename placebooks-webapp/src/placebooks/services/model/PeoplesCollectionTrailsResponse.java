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
public class PeoplesCollectionTrailsResponse
{

	ArrayList<PeoplesCollectionTrailListItem> mytrails;
	ArrayList<PeoplesCollectionTrailListItem> myfavouritetrails;
	int total_objects;
	int per_page;
	PeoplesCollectionLoginResponse authenticationresponse;

	public PeoplesCollectionTrailsResponse()
	{

	}

	public PeoplesCollectionTrailsResponse(final ArrayList<PeoplesCollectionTrailListItem> mytrails,
			final ArrayList<PeoplesCollectionTrailListItem> myfavouritetrails, final int total_objects,
			final int per_page, final PeoplesCollectionLoginResponse authenticationresponse)
	{
		this.mytrails = mytrails;
		this.myfavouritetrails = myfavouritetrails;
		this.total_objects = total_objects;
		this.per_page = per_page;
		this.authenticationresponse = authenticationresponse;
	}

	public PeoplesCollectionLoginResponse GetAuthenticationResponse()
	{
		return authenticationresponse;
	}

	public ArrayList<PeoplesCollectionTrailListItem> GetMyFavouriteTrails()
	{
		return myfavouritetrails;
	}

	public ArrayList<PeoplesCollectionTrailListItem> GetMyTrails()
	{
		return mytrails;
	}

	public int GetPerPage()
	{
		return per_page;
	}

	public int GetTotalObjects()
	{
		return total_objects;
	}
}
