package org.placebooks.views.adapters;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.R;
import org.placebooks.client.model.PlaceBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaceBooksAdapter extends BaseAdapter
{
	private List<PlaceBook> books = new ArrayList<PlaceBook>();
	private LayoutInflater inflater;

	/*
	 * Constructor takes in the context from the shelf class and set the LayoutInflater
	 */
	public PlaceBooksAdapter(final Context context)
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		if (books == null) { return 0; }
		return books.size();
	}

	@Override
	public Object getItem(final int position)
	{
		if (books == null) { return null; }
		return books.get(position);
	}

	@Override
	public long getItemId(final int position)
	{
		if (books == null) { return 0; }
		return books.get(position).getId().hashCode();
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
			convertView = inflater.inflate(R.layout.placebooklistitem, null);
		}
		final PlaceBook item = books.get(position);
		final TextView titleLabel = (TextView) convertView.findViewById(R.id.placebookTitle);
		titleLabel.setText(item.getMetadata("title", "PlaceBook " + item.getId()));
		final TextView authorLabel = (TextView) convertView.findViewById(R.id.placebookAuthor);
		authorLabel.setText(item.getOwner().getName());		

		return convertView;
	}

	public void add(final PlaceBook book)
	{
		this.books.add(book);
	}
	
	/*
	 * Method for setting the list of items Call it when there is data ready and want to display it
	 */
	public void setModel(final List<PlaceBook> books)
	{
		this.books = books;
		notifyDataSetChanged();
	}
}