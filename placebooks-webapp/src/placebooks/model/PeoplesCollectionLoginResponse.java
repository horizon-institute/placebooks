package placebooks.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Class to encapsulate response from Peoples Collection API for a log in response
 * @author pszmp
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionLoginResponse 
{
	protected boolean isvalid;
	protected String reason;

	public PeoplesCollectionLoginResponse()
	{

	}

	public PeoplesCollectionLoginResponse(boolean isvalid, String reason)
	{
		this.isvalid = isvalid;
		this.reason = reason;
	}

	public boolean GetIsValid()
	{
		return this.isvalid;
	}

	public String GetReason()
	{
		return this.reason;
	}
}