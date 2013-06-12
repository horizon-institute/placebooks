package placebooks.client.controllers;


public abstract class DelegateController<T> extends ControllerBase<T> implements SimpleController<T>
{
	protected final SimpleController<?> controller;

	public DelegateController(final SimpleController<?> controller)
	{
		this.controller = controller;
	}

	@Override
	public void add(final ControllerStateListener listener)
	{
		controller.add(listener);
	}

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
}