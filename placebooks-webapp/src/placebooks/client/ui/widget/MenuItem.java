package placebooks.client.ui.widget;

import placebooks.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public abstract class MenuItem extends Label
{
	public MenuItem(final String title)
	{
		super(title);
		setStyleName(Resources.INSTANCE.style().menuItem());		
		addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				run();
			}
		});
	}
	
	public abstract void run();
}	
