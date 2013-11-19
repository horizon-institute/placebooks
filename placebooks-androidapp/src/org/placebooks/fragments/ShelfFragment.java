package org.placebooks.fragments;

import org.placebooks.activities.PlaceBookActivity;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.PlaceBookService;
import org.placebooks.client.model.Shelf;
import org.placebooks.views.adapters.ShelfAdapter;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class ShelfFragment extends ListFragment
{
	protected PlaceBookService server;
	protected ShelfAdapter adapter;
	
	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id)
	{
		final Entry entry = (Entry) adapter.getItem(position);
		final Intent intent = new Intent(getActivity().getApplicationContext(), PlaceBookActivity.class);
		intent.putExtra("id", entry.getKey());
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}
	
	public void setShelf(Shelf shelf)
	{
		if(adapter == null)
		{
			adapter = new ShelfAdapter(getView().getContext());
			adapter.setModel(shelf);
			setListAdapter(adapter);			
		}
		else
		{
			adapter.setModel(shelf);
			adapter.notifyDataSetChanged();			
		}
	}
}
