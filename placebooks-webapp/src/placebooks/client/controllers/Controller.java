package placebooks.client.controllers;

import placebooks.client.ui.views.View;

public interface Controller<T>
{
	public boolean hasLoaded();

	public void load();

	public void load(String id);

	public void refresh();

	public void add(final View<T> view);

	public T getItem();

	public void remove(final View<T> view);
}
