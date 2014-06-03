package org.placebooks.fragments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.support.v4.app.ListFragment;
import org.placebooks.activities.PlaceBookActivity;
import org.placebooks.client.model.PlaceBook;
import org.placebooks.views.adapters.PlaceBooksAdapter;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.parser.gson.GsonParser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class DownloadsFragment extends ListFragment
{
	static public boolean deleteDirectory(final File path)
	{
		if (path.exists())
		{
			final File[] files = path.listFiles();
			if (files == null) { return true; }
			for (final File file : files)
			{
				if (file.isDirectory())
				{
					deleteDirectory(file);
				}
				else
				{
					file.delete();
				}
			}
		}
		return (path.delete());
	}

	private Parser parser = new GsonParser();

	private PlaceBooksAdapter adapter;

	public void displayCachedShelf(final Context context)
	{
		final File directory = context.getExternalFilesDir(null);
		try
		{
			if (adapter == null)
			{
				adapter = new PlaceBooksAdapter(context);
				setListAdapter(adapter);
			}

			findDownloaded(adapter, directory);
		}
		catch (final Exception e)
		{
			Log.error(e);
		}
	}

	public void findDownloaded(final PlaceBooksAdapter adapter, final File file) throws IOException
	{
		final File jsonFile = new File(file, "data.json");
		if (jsonFile.exists())
		{
			final FileReader reader = new FileReader(jsonFile);
			final PlaceBook placebook = parser.parse(PlaceBook.class, reader);

			adapter.add(placebook);
			adapter.notifyDataSetChanged();
			return;
		}

		for (final File subFile : file.listFiles())
		{
			if (subFile.isDirectory())
			{
				findDownloaded(adapter, subFile);
			}
		}
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id)
	{
		final PlaceBook entry = (PlaceBook) adapter.getItem(position);
		final Intent intent = new Intent(getActivity().getApplicationContext(), PlaceBookActivity.class);
		intent.putExtra("id", entry.getId());
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setEmptyText("No Placebooks downloaded");

		displayCachedShelf(view.getContext());
	}
}