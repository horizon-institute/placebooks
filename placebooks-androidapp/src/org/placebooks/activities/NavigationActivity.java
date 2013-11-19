package org.placebooks.activities;

import org.placebooks.R;
import org.placebooks.fragments.DownloadsFragment;
import org.placebooks.fragments.NearestFragment;
import org.placebooks.fragments.WelcomeFragment;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//import android.content.res.Resources;

public class NavigationActivity extends ActionBarActivity
{
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
		{
			selectItem(position);
		}
	}

	public static final boolean isOnline(final Context c)
	{
		return true;
	}

	protected Dialog mSplashDialog;

	private DrawerLayout drawerLayout;

	private ActionBarDrawerToggle drawerToggle;

	private CharSequence drawerTitle;

	private String[] mPlanetTitles;
	private ListView drawerList;
	
	private View drawerInner;

	private CharSequence mTitle;

	@Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPlanetTitles = getResources().getStringArray(R.array.menu_array);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerInner = findViewById(R.id.left_drawer);
		drawerList = (ListView) findViewById(R.id.list);
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		mTitle = drawerTitle = getTitle();

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(final View view)
			{
				getSupportActionBar().setTitle(mTitle);
			}

			@Override
			public void onDrawerOpened(final View drawerView)
			{
				getSupportActionBar().setTitle(drawerTitle);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		selectItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// Inflate the menu items for use in the action bar
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		// Associate searchable configuration with the SearchView
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchActivity.class)));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item)) { return true; }
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(final Menu menu)
	{
		// final boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(final CharSequence title)
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	/** Swaps fragments in the main content view */
	private void selectItem(final int position)
	{
		setTitle(mPlanetTitles[position]);
		Fragment fragment = null;
		if (position == 0)
		{
			fragment = new WelcomeFragment();
			setTitle("Placebooks");
		}
		else if (position == 1)
		{
			fragment = new NearestFragment();
		}
		else if (position == 2)
		{
			fragment = new DownloadsFragment();
		}

		if (fragment != null)
		{
			final FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}

		drawerList.setItemChecked(position, true);
		drawerLayout.closeDrawer(drawerInner);
	}
}