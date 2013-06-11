package placebooks.client;

import placebooks.client.logger.Log;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;

public abstract class JSONResponse<T extends JavaScriptObject> implements RequestCallback
{
	public void handleError(final Request request, final Response response, final Throwable throwable)
	{

	}

	public void handleOther(final Request request, final Response response)
	{
		handleError(request, response, null);
	}

	public abstract void handleResponse(T object);

	@Override
	public void onError(final Request request, final Throwable throwable)
	{
		Log.error("Error: " + throwable.getMessage(), throwable);
		handleError(request, null, throwable);
	}

	@Override
	public void onResponseReceived(final Request request, final Response response)
	{
		Log.info("Response: " + response.getStatusCode() + ": " + response.getText());
		try
		{
			if (response.getStatusCode() == 200)
			{
				final T result = parse(response.getText());
				if (result == null)
				{
					Log.error("Error: 'null' parsed from " + response.getText());
					handleError(request, response, new NullPointerException());
				}
				handleResponse(result);
			}
			else
			{
				handleOther(request, response);
			}
		}
		catch (final Exception e)
		{
			Log.error("Error: " + response.getText(), e);
			handleError(request, response, e);
		}
	}

	private T parse(final String json)
	{
		return JSONParser.parseStrict(json).isObject().getJavaScriptObject().<T> cast();
	}
}
