package placebooks.client.server;

public class HTTPException extends Exception
{
	private int code;

	public HTTPException(final int code, final String message)
	{
		super(message);
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}
}
