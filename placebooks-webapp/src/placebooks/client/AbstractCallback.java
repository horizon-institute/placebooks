package placebooks.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public abstract class AbstractCallback implements RequestCallback
{
	public void failure(final Request request)
	{
	}

	@Override
	public void onError(final Request request, final Throwable throwable)
	{
		GWT.log("Error: " + request.toString(), throwable);
		failure(request);
	}

	@Override
	public void onResponseReceived(final Request request, final Response response)
	{
		GWT.log("Response " + response.getStatusCode() + ": " + response.getText());
		if (response.getStatusCode() == 200)
		{
			success(request, response);
		}
		else
		{
			failure(request);
		}
	}

	public abstract void success(final Request request, final Response response);
}
