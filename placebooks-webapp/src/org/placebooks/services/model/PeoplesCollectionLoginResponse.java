package org.placebooks.services.model;


/**
 * Class to encapsulate response from Peoples Collection API for a log in response
 * 
 * @author pszmp
 */
public class PeoplesCollectionLoginResponse
{
	protected boolean isvalid;
	protected String reason;

	public PeoplesCollectionLoginResponse()
	{

	}

	public PeoplesCollectionLoginResponse(final boolean isvalid, final String reason)
	{
		this.isvalid = isvalid;
		this.reason = reason;
	}

	public boolean GetIsValid()
	{
		return isvalid;
	}

	public String GetReason()
	{
		return reason;
	}
}