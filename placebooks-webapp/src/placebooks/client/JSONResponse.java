package placebooks.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;

public abstract class JSONResponse<T extends JavaScriptObject> implements RequestCallback
{
	public void handleError(final Throwable throwable)
	{

	}

	public abstract void handleResponse(T object);

	@Override
	public void onError(final Request request, final Throwable throwable)
	{
		GWT.log("Error: " + request.toString(), throwable);
		handleError(throwable);
	}

	@Override
	public void onResponseReceived(final Request request, final Response response)
	{
		GWT.log("Response: " + response.getStatusCode() + ": " + response.getText());
		try
		{
			if (response.getStatusCode() == 200)
			{
				handleResponse(parse(response.getText()));
			}
		}
		catch (final Exception e)
		{
			GWT.log("Error: " + request.toString(), e);
			handleError(e);
		}
	}

	private T parse(final String json)
	{
		return JSONParser.parseStrict(json).isObject().getJavaScriptObject().<T> cast();
	}
}
