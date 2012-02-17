package placebooks.client.model;

import placebooks.client.JSONResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;

public abstract class DataStore<T extends JavaScriptObject>
{
	private class CallbackWrapper implements RequestCallback
	{
		public CallbackWrapper(String id, String cached, JSONResponse<T> callback)
		{
			this.id = id;
			this.callback = callback;
			this.cached = cached;
		}
		
		private String id;
		private String cached;
		private JSONResponse<T> callback;
		
		@Override
		public void onResponseReceived(Request request, Response response)
		{
			try
			{
				if (response.getStatusCode() == 200)
				{
					GWT.log("Get " + getStorageID(id) + ": " + response.getText());					
					if(response.getText().equals(cached))
					{
						//GWT.log("Get " + getStorageID(id) + ": Server version matches cached version");
						return;
					}			
					T result = parse(response.getText());
					if(result == null)
					{
						GWT.log("Get " + getStorageID(id) + " Error: 'null' parsed from " + response.getText());
						callback.handleError(request, response, new NullPointerException());
					}
					putLocal(id, response.getText());					
					callback.handleResponse(result);
				}
				else
				{
					GWT.log("Get " + getStorageID(id) + " " + response.getStatusCode() + ": " + response.getText());					
					callback.handleOther(request, response);
				}
			}
			catch (final Exception e)
			{
				GWT.log("Get " + getStorageID(id) + " Error: " + response.getText(), e);
				callback.handleError(request, response, e);
			}
		}

		@Override
		public void onError(Request request, Throwable exception)
		{
			callback.onError(request, exception);			
		}
	}
	
	private Storage stockStore = Storage.getLocalStorageIfSupported();

	public void get(final String id, final JSONResponse<T> callback)
	{
		get(id, callback, false);
	}
	
	public void get(final String id, final JSONResponse<T> callback, final boolean ignoreCache)
	{
		final String localID = getStorageID(id);		
		String cached = null;
		if(!ignoreCache && stockStore != null)
		{
			cached = stockStore.getItem(localID);
			GWT.log("Get " + localID + ": " + cached);
			if (cached != null)
			{
				callback.handleResponse(parse(cached));
			}
		}
		
		final String url = getRequestURL(id);
		if (url != null)
		{
			serverRequest("Get " + localID, url, new CallbackWrapper(id, cached, callback));
		}
	}

	public Shelf getLibrary()
	{
		return null;
	}

	public void put(final String id, final T object, final JSONResponse<T> callback)
	{
		putLocal(id, object);

		final String url = getStoreURL(id);
		if (url != null)
		{
			serverRequest("Put " + getStorageID(id), url, getStoreData(object), callback);
		}
	}

	protected String getHostURL()
	{
		if (GWT.getModuleBaseURL().endsWith(GWT.getModuleName() + "/")) { return GWT.getModuleBaseURL()
				.substring(0, (GWT.getModuleBaseURL().length() - GWT.getModuleName().length() - 1)); }

		return GWT.getModuleBaseURL();
	}

	protected abstract String getRequestURL(final String id);

	protected abstract String getStorageID(final String id);

	protected String getStoreURL(final String id)
	{
		return null;
	}

	protected String getStoreData(final T object)
	{
		return new JSONObject(object).toString();
	}
	
	private T parse(final String json)
	{
		return JSONParser.parseStrict(json).isObject().getJavaScriptObject().<T> cast();
	}

	private void putLocal(final String id, final String object)
	{
		String storeId = getStorageID(id);
		if (stockStore != null && storeId != null)
		{
			stockStore.setItem(getStorageID(id), object);
		}		
	}
	
	private void putLocal(final String id, final T object)
	{
		String storeId = getStorageID(id);
		if (stockStore != null && storeId != null)
		{
			GWT.log("Put " + getStorageID(id) + ": " + new JSONObject(object).toString());
			stockStore.setItem(getStorageID(id), new JSONObject(object).toString());
		}
	}

	private void serverRequest(final String loginfo, final String url, final RequestBuilder.Method method, final String data,
			final RequestCallback callback)
	{
		GWT.log(loginfo + ": " + url);
		final RequestBuilder builder = new RequestBuilder(method, URL.encode(url));
		if (data != null)
		{
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			GWT.log(loginfo + ": " + URL.decodePathSegment(data));
		}
		try
		{
			builder.sendRequest(data, callback);
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	private void serverRequest(final String loginfo, final String url, final RequestCallback callback)
	{
		serverRequest(loginfo, url, RequestBuilder.GET, null, callback);
	}

	private void serverRequest(final String loginfo, final String url, final String data, final RequestCallback callback)
	{
		serverRequest(loginfo, url, RequestBuilder.POST, data, callback);
	}
}