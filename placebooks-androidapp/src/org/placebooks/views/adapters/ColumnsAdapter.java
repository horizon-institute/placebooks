package org.placebooks.views.adapters;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.fragments.ColumnFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ColumnsAdapter extends FragmentStatePagerAdapter
{
	private List<ColumnFragment> fragments = new ArrayList<ColumnFragment>();

	public ColumnsAdapter(final FragmentManager fm)
	{
		super(fm);
	}

	public void add(final ColumnFragment fragment)
	{
		fragments.add(fragment);
	}

	@Override
	public int getCount()
	{
		return fragments.size();
	}
	
    @Override
    public CharSequence getPageTitle (int position)
    {
    	ColumnFragment fragment = fragments.get(position);
    	return "Page " + fragment.getPageNumber() + "." +fragment.getColumn();
    }

	@Override
	public Fragment getItem(final int index)
	{
		return fragments.get(index);
	}
}
