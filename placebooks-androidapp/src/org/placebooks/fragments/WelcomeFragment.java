package org.placebooks.fragments;

import org.placebooks.PlaceBooks;
import org.placebooks.R;
import org.placebooks.UICallback;
import org.placebooks.client.model.Shelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeFragment extends ShelfFragment
{
	class Featured implements Runnable
	{
		@Override
		public void run()
		{
			server.getFeaturedPlaceBooks(3, new UICallback<Shelf>(getView())
			{
				@Override
				public void onPostSuccess(Shelf result)
				{
					setShelf(result);
				}
			});
		}
	}

	public WelcomeFragment()
	{
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		server =((PlaceBooks) getActivity().getApplicationContext()).server;
		new Thread(new Featured()).start();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.welcome, null);
	}
}