package org.placebooks.activities;

import org.placebooks.PlaceBookServerHandler;
import org.placebooks.PlaceBooks;
import org.placebooks.R;
import org.placebooks.client.model.PlaceBookService;
import org.placebooks.client.model.Shelf;
import org.placebooks.fragments.ShelfFragment;
import org.wornchaos.client.server.AsyncCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class GroupActivity extends ActionBarActivity
{
	public static final String ARG_ID = "ID";

	private String id;
	private PlaceBookService server;
	private ShelfFragment shelfFragment;

	public GroupActivity()
	{

	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame);

		shelfFragment = new ShelfFragment();
		final FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().add(R.id.frame, shelfFragment).commit();

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleIntent(intent);
	}

	private void handleIntent(final Intent intent)
	{
		if (intent != null)
		{
			id = intent.getStringExtra("id");
			final String host = intent.getStringExtra("host");
			if (host != null)
			{
				server = PlaceBookServerHandler.createServer(host, getApplicationContext());
			}
			else
			{
				server = ((PlaceBooks) getApplicationContext()).server;
			}

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					server.getGroup(id, new AsyncCallback<Shelf>()
					{
						@Override
						public void onSuccess(final Shelf result)
						{
							if (result != null)
							{
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										shelfFragment.setShelf(result);
									}
								});
							}
							else
							{
								// TODO positionManager.clearPending();
							}
						}
					});
				}
			}).start();
		}
	}
}