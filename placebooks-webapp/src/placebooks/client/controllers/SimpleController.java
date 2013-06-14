package placebooks.client.controllers;

public interface SimpleController<T> extends Controller<T>
{
	public void add(final ControllerStateListener listener);

	public ControllerState getState();

	public void markChanged();
	
	public void markChanged(final boolean refresh);	

	public void pause();

	public void remove(final ControllerStateListener listener);
}
