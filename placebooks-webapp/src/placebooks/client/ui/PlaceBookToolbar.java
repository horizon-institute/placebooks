package placebooks.client.ui;

import placebooks.client.resources.Resources;

import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookToolbar extends FlowPanel
{
	public PlaceBookToolbar()
	{
		super();
		setStyleName(Resources.INSTANCE.style().toolbar());
		
		add(new PlaceBookToolbarItem("Test1"));
	}	
}
