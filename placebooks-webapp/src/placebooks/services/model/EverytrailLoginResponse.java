/**
 * 
 */
package placebooks.services.model;

/**
 * @author pszmp Response for a login attempt using EverytrailHelper
 * 
 */
public class EverytrailLoginResponse
{
	private String status;
	private String value;

	public EverytrailLoginResponse(final String status, final String value)
	{
		this.status = status;
		this.value = value;
	}

	/**
	 * Status should either be success or error On success, value will be the user id on failure it
	 * will be an error code 10 or 11
	 */
	public String getStatus()
	{
		return this.status;
	}

	/*
	 * When login has succeeded, the value will be the user id when there is an error, the error
	 * code will be as in the everytrail api - 10 is wrong password/username and 11 if unknown
	 * error.
	 */
	public String getValue()
	{
		return this.value;
	}
}
