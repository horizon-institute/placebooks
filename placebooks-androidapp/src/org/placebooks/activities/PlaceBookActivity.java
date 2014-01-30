package org.placebooks.activities;

import java.io.File;

import org.placebooks.PlaceBookServerHandler;
import org.placebooks.PlaceBooks;
import org.placebooks.R;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Page;
import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.model.PlaceBookService;
import org.placebooks.fragments.ColumnFragment;
import org.placebooks.fragments.ColumnFragment.GotoItemListener;
import org.placebooks.views.adapters.ColumnsAdapter;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.logger.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class PlaceBookActivity extends ActionBarActivity
{
	private String id;
	private ColumnsAdapter adapter;
	private PlaceBookService server;
	private ViewPager pager;

	private final AsyncCallback<PlaceBook> callback = new AsyncCallback<PlaceBook>()
	{
		@Override
		public void onSuccess(final PlaceBook placebook)
		{
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run()
				{
					setTitle(placebook.getMetadata("title", "PlaceBook " + placebook.getId()));

					adapter.clear();
					
					int pageNumber = 1;
					for (final Page page : placebook.getPages())
					{
						for (int column = 0; column < 2; column++)
						{
							final ColumnFragment fragment = new ColumnFragment();
							fragment.setPage(placebook, page, pageNumber, column);
							fragment.setGotoItemListener(new GotoItemListener()
							{
								@Override
								public void gotoItem(Item item)
								{
									int pageNumber = 0;
									for (final Page page : placebook.getPages())
									{
										for(final Item pageItem: page.getItems())
										{
											if(pageItem == item)
											{
												pager.setCurrentItem(pageNumber + item.getParameter("column", 0));
												return;
											}
										}
										pageNumber+=2;
									}
								}
							});
							adapter.add(fragment);
						}
						pageNumber++;
					}

					adapter.notifyDataSetChanged();
				}
			});
		}
	};
	
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set the content view to the xml reader file
		setContentView(R.layout.placebookview);

		adapter = new ColumnsAdapter(getSupportFragmentManager());

		pager = ((ViewPager) findViewById(R.id.pager));
		pager.setAdapter(adapter);

		handleIntent(getIntent());
	}

	private void handleIntent(final Intent intent)
	{
		if (intent != null)
		{
			Log.info(intent.toString());
			if(intent.getAction().equals(Intent.ACTION_VIEW) && intent.getData() != null)
			{
				if(intent.getData().getScheme().equals("content"))
				{
					final File file = new File(getApplicationContext().getExternalFilesDir(null), intent.getData().getPath());					
					try
					{
						PlaceBookServerHandler.unzip(getContentResolver().openInputStream(intent.getData()), file);
						PlaceBookServerHandler.getPlaceBook(file, callback);
					}
					catch (Exception e)
					{
						Log.error(e);
					}
				}
				else if(intent.getData().getScheme().equals("placebooks"))
				{
					id = intent.getData().getLastPathSegment();
					final String url = intent.getDataString();
					if(url.contains("placebooks/group"))
					{
						final Intent newIntent = new Intent(getApplicationContext(), GroupActivity.class);
						newIntent.putExtra("id", id);
						newIntent.putExtra("host", url.substring(0, url.indexOf("placebooks/group")).replace("placebooks://", "http://"));
						newIntent.setAction(Intent.ACTION_VIEW);
						startActivity(newIntent);
						return;						
					}
					else if(!url.contains("placebooks/placebook"))
					{
						return;
					}
					
					final String host = url.substring(0, url.indexOf("placebooks/placebook")).replace("placebooks://", "http://");
					Log.info(host);
					Log.info(id);
					
					server = PlaceBookServerHandler.createServer(host, getApplicationContext());
					
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							server.placebookPackage(id, callback);
						}
					}).start();					
				}
			}
			else
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
						server.placebookPackage(id, callback);
					}
				}).start();
			}
		}
	}
}