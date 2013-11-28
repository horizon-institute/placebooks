package org.placebooks;

import org.wornchaos.client.server.AsyncCallback;

import android.view.View;

public abstract class UICallback<T> extends AsyncCallback<T>
{
	private View view;
	
	public UICallback(final View view)
	{
		this.view = view;
	}

	@Override
	public void onSuccess(final T item)
	{
		if(view == null)
		{
			return;
		}
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				onPostSuccess(item);
			}
		});
	}
	

	
	@Override
	public void onFailure(final Throwable caught)
	{
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				onPostFailure(caught);
			}
		});
	}

	public abstract void onPostSuccess(T item);
	
	public void onPostFailure(final Throwable caught)
	{
		
	}
}
