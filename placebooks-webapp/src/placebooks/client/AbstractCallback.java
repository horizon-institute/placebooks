package placebooks.client;

import placebooks.client.logger.Log;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public abstract class AbstractCallback implements RequestCallback
{
	public void failure(final Request request, final Response response)
	{
	}

	@Override
	public void onError(final Request request, final Throwable throwable)
	{
		Log.error("Error: " + request.toString(), throwable);
		failure(request, null);
	}

	@Override
	public void onResponseReceived(final Request request, final Response response)
	{
		Log.info("Response " + response.getStatusCode() + ": " + response.getText());
		if (response.getStatusCode() == 200)
		{
			success(request, response);
		}
		else
		{
			failure(request, response);
		}
	}

	public abstract void success(final Request request, final Response response);
}
