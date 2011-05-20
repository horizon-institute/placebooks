package placebooks.client.ui.widget;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.FlowPanel;

public class DropMenu extends FlowPanel
{
	public void show(final List<MenuItem> items, final int x, final int y)
	{
		clear();
		for(MenuItem item: items)
		{
			add(item);
		}
			 
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
		getElement().getStyle().setVisibility(Visibility.VISIBLE);		
		getElement().getStyle().setOpacity(1);
	}
	
	public void hide()
	{
		getElement().getStyle().setVisibility(Visibility.HIDDEN);
		getElement().getStyle().setOpacity(0);
	}
}
