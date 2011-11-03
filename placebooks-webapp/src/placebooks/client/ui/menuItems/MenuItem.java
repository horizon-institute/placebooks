package placebooks.client.ui.menuItems;

import placebooks.client.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public abstract class MenuItem extends Label
{
	public MenuItem(final String title)
	{
		super(title);
		setStyleName(Resources.STYLES.style().menuItem());
		addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				run();
			}
		});
	}

	public boolean isEnabled()
	{
		return true;
	}

	public void refresh()
	{
		setVisible(isEnabled());
	}

	public abstract void run();
}
