package placebooks.client.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import placebooks.client.parser.JSONParser;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractController<U> extends CachedController<U> implements AsyncCallback<U>,
		SimpleController<U>
{
	private ControllerState state = ControllerState.saved;

	private static final int saveDelay = 2000;

	private final Collection<ControllerStateListener> listeners = new ArrayList<ControllerStateListener>();

	private long lastChange;
	private long lastSave;

	private Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			if (state != ControllerState.not_saved && state != ControllerState.save_error) { return; }
			setState(ControllerState.saving);
			lastSave = lastChange;
			save(getItem(), AbstractController.this);
		}
	};

	public AbstractController()
	{
		super();
		setState(ControllerState.saved);
	}

	public AbstractController(final JSONParser<U> parser, final String prefix)
	{
		super(parser, prefix);
		setState(ControllerState.saved);
	}

	@Override
	public void add(final ControllerStateListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public ControllerState getState()
	{
		return state;
	}

	@Override
	public void markChanged()
	{
		markChanged(true);
	}

	public void markChanged(final boolean refresh)
	{
		timer.cancel();
		timer.schedule(saveDelay);
		lastChange = new Date().getTime();
		if (state == ControllerState.saved)
		{
			setState(ControllerState.not_saved);
		}
		if(refresh)
		{
			setItem(getItem());
		}
		// changed = true;		
	}
	
	@Override
	public void onFailure(final Throwable throwable)
	{
		setState(ControllerState.save_error);
	}

	@Override
	public void onSuccess(final U response)
	{
		if (lastSave == lastChange)
		{
			setState(ControllerState.saved);
		}
		else
		{
			setState(ControllerState.not_saved);
		}
		setItem(response);
	}

	@Override
	public void pause()
	{
		timer.cancel();
	}

	@Override
	public void remove(final ControllerStateListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	protected boolean accept(final U item)
	{
		return true;
	}

	protected abstract void save(U value, AsyncCallback<U> callback);

	private void setState(final ControllerState state)
	{
		if (this.state == state) { return; }
		this.state = state;

		final List<ControllerStateListener> listenerCopies = new ArrayList<ControllerStateListener>(listeners);
		for (final ControllerStateListener listener : listenerCopies)
		{
			listener.stateChanged(state);
		}
	}
}