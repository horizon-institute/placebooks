package placebooks.client.controllers;

public interface SomethingController<T> extends Controller<T>
{
	public void add(final ControllerStateListener listener);

	public ControllerState getState();

	public void markChanged();

	public void pause();

	public void remove(final ControllerStateListener listener);
}
