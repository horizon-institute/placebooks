package placebooks.client.ui.elements;

import java.util.Date;

import placebooks.client.Resources;
import placebooks.client.ui.UIMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

public class PlaceBookSaveItem extends PlaceBookToolbarItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	public enum SaveState
	{
		not_saved, save_error, saved, saving
	}

	private SaveState state = SaveState.saved;

	private static final int saveDelay = 2000;

	private long lastChange;
	private long lastSave;
	private long saveAttempt;

	private Runnable runnable;

	private Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			if (state == SaveState.saved) { return; }
			setState(SaveState.saving);
			if (runnable != null)
			{
				saveAttempt = lastChange;
				runnable.run();
			}
		}
	};

	public SaveState getState()
	{
		return state;
	}

	public void markChanged()
	{
		timer.cancel();
		timer.schedule(saveDelay);
		lastChange = new Date().getTime();
		if (state == SaveState.saved)
		{
			setState(SaveState.not_saved);
		}
		// changed = true;
	}

	public void markSaved()
	{
		lastSave = saveAttempt;
		if (lastSave == lastChange)
		{
			setState(SaveState.saved);
		}
		else
		{
			setState(SaveState.not_saved);
		}
	}

	public void pause()
	{
		timer.cancel();
	}

	public void setRunnable(final Runnable runnable)
	{
		this.runnable = runnable;
	}

	public void setState(final SaveState state)
	{
		this.state = state;
		switch (state)
		{
			case saved:
				setText(uiMessages.saved());
				hideImage();
				setEnabled(false);
				break;

			case not_saved:
				setText(uiMessages.save());
				hideImage();
				setEnabled(true);
				break;

			case saving:
				setText(uiMessages.saving());
				setImage(Resources.IMAGES.progress2());
				setEnabled(false);
				break;

			case save_error:
				setText(uiMessages.saveError());
				setImage(Resources.IMAGES.error());
				setEnabled(true);
				markChanged();
				break;

			default:
				break;
		}
	}
}