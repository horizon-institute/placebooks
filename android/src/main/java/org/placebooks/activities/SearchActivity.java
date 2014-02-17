package org.placebooks.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.placebooks.PlaceBooks;
import org.placebooks.R;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.fragments.ShelfFragment;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.logger.Log;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SearchActivity extends ActionBarActivity
{
	class Search implements Runnable
	{
		private String searchTerms;

		public Search(final String searchTerms)
		{
			this.searchTerms = searchTerms;
		}

		@Override
		public void run()
		{
			((PlaceBooks)getApplicationContext()).server.search(searchTerms, new AsyncCallback<Shelf>()
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
								final Collection<Entry> removals = new ArrayList<Entry>();
								for (final Entry entry : result.getEntries())
								{
									if (entry.getScore() == 0)
									{
										removals.add(entry);
									}
								}

								result.getEntries().removeAll(removals);

								Collections.sort(result.getEntries(), new Comparator<Entry>()
								{
									@Override
									public int compare(final Entry lhs, final Entry rhs)
									{
										return rhs.getScore() - lhs.getScore();
									}
								});

								shelfFragment.setEmptyText("No Placebooks found");
								shelfFragment.setShelf(result);
							}
						});
					}
					else
					{
						shelfFragment.setEmptyText("No Placebooks found");
						shelfFragment.setShelf(null);
					}
				}
			});
		}
	}

	private String search;
	private ShelfFragment shelfFragment;
	private android.support.v7.widget.SearchView searchView;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame);

		handleIntent(getIntent());

		shelfFragment = new ShelfFragment();
		final FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().add(R.id.frame, shelfFragment).commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);

		// Get the SearchView and set the searchable configuration
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		if (search != null)
		{
			searchView.setQuery(search, false);
			searchView.requestFocusFromTouch();
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleIntent(intent);
	}

	private void handleIntent(final Intent intent)
	{
		Log.info("Intent " + intent.getAction());
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			search = intent.getStringExtra(SearchManager.QUERY);
			if (searchView != null)
			{
				searchView.setQuery(search, false);
				searchView.requestFocusFromTouch();
			}

			Log.info("Searching for " + search);
			new Thread(new Search(search)).start();
		}
	}
}