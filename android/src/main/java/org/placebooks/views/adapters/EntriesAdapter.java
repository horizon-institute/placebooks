package org.placebooks.views.adapters;

import java.util.List;

import org.placebooks.R;
import org.placebooks.client.model.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EntriesAdapter extends BaseAdapter
{
	private List<Entry> entries;
	private LayoutInflater mInflater;

	/*
	 * Constructor takes in the context from the shelf class and set the LayoutInflater
	 */
	public EntriesAdapter(final Context context)
	{
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		if (entries == null) { return 0; }
		return entries.size();
	}

	@Override
	public Object getItem(final int position)
	{
		if (entries == null) { return null; }
		return entries.get(position);
	}

	@Override
	public long getItemId(final int position)
	{
		if (entries == null) { return 0; }
		return entries.get(position).getKey().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup) Method
	 * overrides the view in each row of the listview We use the position to get the items
	 */
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.placebooklistitem, null);
		}
		final Entry item = entries.get(position);
		final TextView titleLabel = (TextView) convertView.findViewById(R.id.placebookTitle);
		titleLabel.setText(item.getTitle());
		final TextView authorLabel = (TextView) convertView.findViewById(R.id.placebookAuthor);
		authorLabel.setText(item.getOwnerName());

		return convertView;
	}

	/*
	 * Method for setting the list of items Call it when there is data ready and want to display it
	 */
	public void setModel(final List<Entry> entries)
	{
		this.entries = entries;
		notifyDataSetChanged();
	}
}