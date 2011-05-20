package org.placebooks.www;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter{

	
	private List<View> views=null;

	/**
		* Constructor creating an empty list of views, but with
		* a specified count. Subclasses must override newView().
    */
	public MyAdapter(int count) {
		super();
		
		views=new ArrayList<View>(count);
		
		for (int i=0;i<count;i++) {
			views.add(null);
		}
	}

	/**
		* Constructor wrapping a supplied list of views.
		* Subclasses must override newView() if any of the elements
		* in the list are null.
    */
	public MyAdapter(List<View> views) {
		super();
		
		this.views=views;
	}

	/**
		* Get the data item associated with the specified
		* position in the data set.
		* @param position Position of the item whose data we want
    */
	@Override
	public Object getItem(int position) {
		return(views.get(position));
	}

	/**
		* How many items are in the data set represented by this
		* Adapter.
    */
	@Override
	public int getCount() {
		return(views.size());
	}

	/**
		* Returns the number of types of Views that will be
		* created by getView().
    */
	@Override
	public int getViewTypeCount() {
		return(getCount());
	}

	/**
		* Get the type of View that will be created by getView()
		* for the specified item.
		* @param position Position of the item whose data we want
    */
	@Override
	public int getItemViewType(int position) {
		return(position);
	}

	/**
		* Are all items in this ListAdapter enabled? If yes it
		* means all items are selectable and clickable.
    */
	@Override
	public boolean areAllItemsEnabled() {
		return(false);
	}

	/**
		* Returns true if the item at the specified position is
		* not a separator.
		* @param position Position of the item whose data we want
    */
	@Override
	public boolean isEnabled(int position) {
		return(false);
	}

	/**
		* Get a View that displays the data at the specified
		* position in the data set.
		* @param position Position of the item whose data we want
		* @param convertView View to recycle, if not null
		* @param parent ViewGroup containing the returned View
    */
	@Override
	public View getView(int position, View convertView,
											ViewGroup parent) {
		View result=views.get(position);
		
		if (result==null) {
			result=newView(position, parent);
			views.set(position, result);
		}
		
		return(result);
	}

	/**
		* Get the row id associated with the specified position
		* in the list.
		* @param position Position of the item whose data we want
    */
	@Override
	public long getItemId(int position) {
		return(position);
	}

	/**
		* Create a new View to go into the list at the specified
		* position.
		* @param position Position of the item whose data we want
		* @param parent ViewGroup containing the returned View
    */
	protected View newView(int position, ViewGroup parent) {
		throw new RuntimeException("You must override newView()!");
	}
	
	
}
