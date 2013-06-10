package placebooks.client.controllers;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.ui.views.View;

public abstract class ControllerBase<U> implements Controller<U>
{
	private U item;

	private final Collection<View<U>> views = new ArrayList<View<U>>();

	private boolean loaded = false;
	
	@Override
	public void add(final View<U> view)
	{
		views.add(view);
		if (loaded)
		{
			view.itemChanged(item);
		}
	}

	@Override
	public U getItem()
	{
		return item;
	}

	@Override
	public boolean hasLoaded()
	{
		return loaded;
	}

	@Override
	public void load()
	{
		load(null);
	}
	
	protected boolean accept(final U value)
	{
		return true;
	}
	
	protected void fireChange()
	{
		final Collection<View<U>> viewCollection = new ArrayList<View<U>>(views);
		for (final View<U> view : viewCollection)
		{
			view.itemChanged(item);
		}		
	}
	
	protected void setItem(final U value)
	{
		if (!accept(value)) { return; }
		this.item = value;
		loaded = true;
		fireChange();
	}
	
	@Override
	public void remove(final View<U> view)
	{
		views.remove(view);
	}	
}
