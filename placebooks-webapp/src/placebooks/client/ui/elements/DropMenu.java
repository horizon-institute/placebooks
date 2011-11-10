package placebooks.client.ui.elements;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

public class DropMenu extends FlowPanel implements HasMouseOverHandlers, HasMouseOutHandlers
{
	private static DropMenu current = null;
	
	private final Timer hideMenuTimer = new Timer()
	{
		@Override
		public void run()
		{
			hideMenu();
		}
	};
	

	@Override
	public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	public void hideMenu()
	{
		getElement().getStyle().setVisibility(Visibility.HIDDEN);
		getElement().getStyle().setOpacity(0);
		hideMenuTimer.cancel();
	}	
	
	public void showMenu(final int x, final int y)
	{
		if(this != current)
		{
			if(current != null)
			{
				current.hideMenu();
			}
		}
		current = this;
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
		getElement().getStyle().setVisibility(Visibility.VISIBLE);
		getElement().getStyle().setOpacity(1);
		hideMenuTimer.cancel();				
	}
	
	public void startHideMenu()
	{
		hideMenuTimer.schedule(500);		
	}
}
