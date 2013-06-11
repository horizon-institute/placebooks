package placebooks.client.ui.pages;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public abstract class Page implements Activity
{
	protected Page()
	{
	}

	@Override
	public String mayStop()
	{
		return null;
	}

	@Override
	public void onCancel()
	{
	}

	@Override
	public void onStop()
	{
	}

	protected abstract Widget createView();
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		panel.setWidget(createView());
	}
}