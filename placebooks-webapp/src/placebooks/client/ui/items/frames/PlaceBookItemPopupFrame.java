package placebooks.client.ui.items.frames;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookInteractionHandler;
import placebooks.client.ui.widget.MenuItem;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookItemPopupFrame extends PlaceBookItemFrameWidget
{
	public static class Factory extends PlaceBookItemFrameFactory
	{
		private PlaceBookInteractionHandler interactionHandler;
		
		public Factory()
		{

		}
		
		public void setInteractionHandler(PlaceBookInteractionHandler interactionHandler)
		{
			this.interactionHandler = interactionHandler;
		}
		
		@Override
		public PlaceBookItemFrame createFrame()
		{
			return new PlaceBookItemPopupFrame(interactionHandler);
		}

		@Override
		public boolean getEditable()
		{
			return true;
		}
		
	}
	
	private final MouseOverHandler highlightOn = new MouseOverHandler()
	{
		@Override
		public void onMouseOver(MouseOverEvent event)
		{
			setHighlight(true);
		}
	};

	private final MouseOutHandler highlightOff = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(MouseOutEvent event)
		{
			setHighlight(false);
		}
	};
	
	private boolean highlighted = false;

	private final PlaceBookInteractionHandler interactionHandler;

	private Collection<MenuItem> menuItems = new ArrayList<MenuItem>();

	public PlaceBookItemPopupFrame(PlaceBookInteractionHandler interactHandler)
	{
		super();
		this.interactionHandler = interactHandler;
		final SimplePanel rootPanel = new SimplePanel();
		initWidget(rootPanel);
		rootPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
		widgetPanel.getElement().getStyle().setMargin(5, Unit.PX);
		widgetPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		widgetPanel.addDomHandler(highlightOn, MouseOverEvent.getType());
		widgetPanel.addDomHandler(highlightOff, MouseOutEvent.getType());
		
		frame.addMouseOverHandler(highlightOn);
		frame.addMouseOutHandler(highlightOff);
		
		menuButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				final int x = menuButton.getElement().getAbsoluteLeft();
				final int y = menuButton.getElement().getAbsoluteTop() + menuButton.getElement().getClientHeight();
				interactionHandler.showMenu(menuItems, x, y);
				event.stopPropagation();				
			}
		}, ClickEvent.getType());
		
		dragSection.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				interactionHandler.setupDrag(event, getItemWidget(), PlaceBookItemPopupFrame.this);			
			}
		});
		
		resizeSection.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				interactionHandler.setupResize(event, PlaceBookItemPopupFrame.this);			
			}
		});		
		rootPanel.add(widgetPanel);
	}

	void add(final MenuItem menuItem)
	{
		menuItems.add(menuItem);
	}

	private void setHighlight(final boolean highlight)
	{
		if(highlighted != highlight)
		{
			highlighted = highlight;
		
			updateFrame();
		}
	}

	@Override
	protected void onAttach()
	{
		super.onAttach();
		((Panel) getParent()).add(frame);
	}

	@Override
	protected void onDetach()
	{
		super.onDetach();
		frame.removeFromParent();
	}
	
	@Override
	public void resize(String left, String top, String width, String height)
	{
		super.resize(left, top, width, height);
		frame.getElement().getStyle().setProperty("left", left);
		frame.getElement().getStyle().setProperty("width", width);

		frame.getElement().getStyle().setTop(getWidget().getElement().getOffsetTop() - 22, Unit.PX);
		frame.getElement().getStyle().setHeight(getWidget().getOffsetHeight() + 26, Unit.PX);
	}	

	private void updateFrame()
	{
		if (interactionHandler.getSelected() == this)
		{
			frame.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setOpacity(1);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else if (highlighted)
		{
			getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setZIndex(10);
			frame.getElement().getStyle().setOpacity(0.4);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else
		{
			getElement().getStyle().setZIndex(1);
			frame.getElement().getStyle().setZIndex(0);
			frame.getElement().getStyle().setOpacity(0);
			frame.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}
}