package placebooks.services.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;


/**
 * Class to encapsulate response from Peoples Collection API for a list of Trails for a user 
 * @author pszmp
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailsResponse {

	ArrayList<PeoplesCollectionTrail> mytrails;
	ArrayList<PeoplesCollectionTrail> myfavouritetrails;
	int total_objects;
	int per_page;
	PeoplesCollectionLoginResponse authenticationresponse;

	public PeoplesCollectionTrailsResponse()
	{

	}


	public PeoplesCollectionTrailsResponse(ArrayList<PeoplesCollectionTrail> mytrails,	ArrayList<PeoplesCollectionTrail> myfavouritetrails,		int total_objects, int per_page,	PeoplesCollectionLoginResponse authenticationresponse)
	{
		this.mytrails = mytrails;
		this.myfavouritetrails = myfavouritetrails;
		this.total_objects = total_objects;
		this.per_page = per_page;
		this.authenticationresponse = authenticationresponse;
	}

	public ArrayList<PeoplesCollectionTrail> GetMyTrails()
	{
		return mytrails;
	}

	public ArrayList<PeoplesCollectionTrail> GetMyFavouriteTrails()
	{
		return myfavouritetrails;
	}

	public int GetTotalObjects()
	{
		return total_objects;
	}

	public int GetPerPage()
	{
		return per_page;
	}

	public PeoplesCollectionLoginResponse GetAuthenticationResponse()
	{
		return authenticationresponse;
	}
}
