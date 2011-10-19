package placebooks.services.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

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