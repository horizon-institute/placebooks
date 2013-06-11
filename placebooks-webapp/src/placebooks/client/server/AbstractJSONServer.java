package placebooks.client.server;

import java.util.Date;

import placebooks.client.logger.Log;
import placebooks.client.parser.JSONParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractJSONServer implements JSONServer
{

	protected class JSONCallback<U> implements RequestCallback
	{
		private final AsyncCallback<U> callback;
		private final JSONParser<U> parser;

		public JSONCallback(final AsyncCallback<U> receiver, final JSONParser<U> parser)
		{
			this.callback = receiver;
			this.parser = parser;
		}

		@Override
		public void onError(final com.google.gwt.http.client.Request request, final Throwable throwable)
		{
			Log.info("Request Error", throwable);
			callback.onFailure(throwable);
		}

		@Override
		public void onResponseReceived(final Request request, final Response response)
		{
			Log.info("Response " + response.getStatusCode() + ": \"" + response.getText() + "\"");
			try
			{
				if (response.getStatusCode() == 200)
				{
					if (response.getText().trim().isEmpty())
					{
						callback.onSuccess(null);
					}
					else
					{
						final JSONValue value = com.google.gwt.json.client.JSONParser.parseStrict(response.getText());
						callback.onSuccess(parser.parse(value));
					}
				}
				else
				{
					callback.onFailure(new HTTPException(response.getStatusCode(), response.getText()));
				}
			}
			catch (final Exception e)
			{
				onError(request, e);
			}
		}
	}

	private static final DateTimeFormat format = DateTimeFormat.getFormat("EEEE, MMMM dd, yyyy");

	private boolean offline = false;

	@Override
	public boolean isOffline()
	{
		return offline;
	}

	@Override
	public void setOffline(final boolean offline)
	{
		this.offline = offline;
	}

	public String getHostURL()
	{
		if (GWT.getModuleBaseURL().endsWith(GWT.getModuleName() + "/")) { return GWT.getModuleBaseURL()
				.substring(0, (GWT.getModuleBaseURL().length() - GWT.getModuleName().length() - 1)); }

		return GWT.getModuleBaseURL();
	}

	protected void serverRequest(final String url, final Date modified, final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.GET, null, modified, callback);
	}

	protected void serverRequest(final String url, final RequestBuilder.Method method, final String data,
			final Date modified, final RequestCallback callback)
	{
		if (isOffline()) { return; }
		Log.info(method.toString() + " " + url);
		final RequestBuilder builder = new RequestBuilder(method, URL.encode(url));
		if (modified != null)
		{
			// TODO Format time!
			builder.setHeader("If-Modified-Since", format.format(modified));
		}
		if (data != null && !data.isEmpty())
		{
			Log.info(method.toString() + " Request: " + URL.decodePathSegment(data));
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			builder.setRequestData(data);
		}
		try
		{
			builder.setCallback(callback);
			builder.send();
		}
		catch (final Exception e)
		{
			callback.onError(null, e);
		}
	}

	protected void serverRequest(final String url, final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.GET, null, null, callback);
	}

	protected void serverRequest(final String url, final String data, final Date modified,
			final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.POST, data, modified, callback);
	}

	protected void serverRequest(final String url, final String data, final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.POST, data, null, callback);
	}
}
