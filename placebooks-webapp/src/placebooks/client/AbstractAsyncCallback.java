package placebooks.client;

import placebooks.client.logger.Log;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractAsyncCallback<T> implements AsyncCallback<T>
{

	@Override
	public void onFailure(final Throwable caught)
	{
		Log.error("Request: Error: ", caught);
	}

	@Override
	public abstract void onSuccess(final T response);
}
