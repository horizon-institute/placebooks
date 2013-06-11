package placebooks.client.controllers;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.ui.views.View;

public abstract class DelegateController<T> implements SomethingController<T>
{
	protected final SomethingController<?> controller;

	private final Collection<View<T>> views = new ArrayList<View<T>>();

	public DelegateController(final SomethingController<?> controller)
	{
		this.controller = controller;
	}

	@Override
	public void add(final ControllerStateListener listener)
	{
		controller.add(listener);
	}

	@Override
	public void add(final View<T> view)
	{
		views.add(view);
		if (getItem() != null)
		{
			view.itemChanged(getItem());
		}
	}

	@Override
	public abstract T getItem();

	@Override
	public ControllerState getState()
	{
		return controller.getState();
	}

	@Override
	public boolean hasLoaded()
	{
		return controller.hasLoaded();
	}

	@Override
	public void load()
	{
		controller.load();
	}

	@Override
	public void load(final String id)
	{
		controller.load(id);
	}

	@Override
	public void markChanged()
	{
		controller.markChanged();
	}

	@Override
	public void pause()
	{
		controller.pause();
	}

	@Override
	public void refresh()
	{
		controller.refresh();
	}

	@Override
	public void remove(final ControllerStateListener listener)
	{
		controller.add(listener);
	}

	@Override
	public void remove(final View<T> view)
	{
		views.remove(view);
	}
}