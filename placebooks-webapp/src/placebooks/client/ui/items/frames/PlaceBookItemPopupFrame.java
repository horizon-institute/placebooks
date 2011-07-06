package placebooks.client.ui.items.frames;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookInteractionHandler;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.menuItems.AddMapMenuItem;
import placebooks.client.ui.menuItems.DeletePlaceBookMenuItem;
import placebooks.client.ui.menuItems.FitToContentMenuItem;
import placebooks.client.ui.menuItems.HideTrailMenuItem;
import placebooks.client.ui.menuItems.MenuItem;
import placebooks.client.ui.menuItems.RemoveMapMenuItem;
import placebooks.client.ui.menuItems.SetSourceURLMenuItem;
import placebooks.client.ui.menuItems.ShowTrailMenuItem;
import placebooks.client.ui.menuItems.UploadMenuItem;

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

	private final PlaceBookItemWidget.ChangeHandler changeHandler = new PlaceBookItemWidget.ChangeHandler()
	{
		@Override
		public void itemChanged()
		{
			interactionHandler.getContext().markChanged();			
		}
	};
	
	private final PlaceBookItemWidget.FocusHandler focusHandler = new PlaceBookItemWidget.FocusHandler()
	{
		@Override
		public void itemFocusChanged(boolean focussed)
		{
			if(focussed)
			{
				interactionHandler.setSelected(PlaceBookItemPopupFrame.this);
			}
			else
			{
				interactionHandler.setSelected(null);				
			}
		}
	};
	
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
		
		frame.addDomHandler(highlightOn, MouseOverEvent.getType());
		frame.addDomHandler(highlightOff, MouseOutEvent.getType());
		
		menuButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				final int x = menuButton.getElement().getAbsoluteLeft() + menuButton.getElement().getClientWidth();
				final int y = menuButton.getElement().getAbsoluteTop() + menuButton.getElement().getClientHeight();
				interactionHandler.setSelected(PlaceBookItemPopupFrame.this);				
				interactionHandler.showMenu(menuItems, x, y, true);
				event.stopPropagation();				
			}
		}, ClickEvent.getType());
		
		dragSection.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				interactionHandler.setupDrag(event, getItemWidget(), PlaceBookItemPopupFrame.this);			
			}
		}, MouseDownEvent.getType());
		
		resizeSection.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				interactionHandler.setupResize(event, PlaceBookItemPopupFrame.this);			
			}
		}, MouseDownEvent.getType());		
		rootPanel.add(widgetPanel);
		
		menuItems.add(new AddMapMenuItem(interactionHandler.getContext(), interactionHandler.getCanvas(), this));
		menuItems.add(new DeletePlaceBookMenuItem(interactionHandler.getContext(), interactionHandler.getCanvas(), this));
		menuItems.add(new FitToContentMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new HideTrailMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new RemoveMapMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new SetSourceURLMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new ShowTrailMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new UploadMenuItem(this));
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
	protected void doAttachChildren()
	{
		super.doAttachChildren();
		((Panel) getParent()).add(frame);		
	}

	@Override
	protected void doDetachChildren()
	{
		super.doDetachChildren();
		frame.removeFromParent();
	}

	@Override
	public void setItemWidget(PlaceBookItemWidget itemWidget)
	{
		super.setItemWidget(itemWidget);
		itemWidget.setFocusHandler(focusHandler);
		itemWidget.setChangeHandler(changeHandler);
	}

	@Override
	public void resize(String left, String top, String width, String height)
	{
		super.resize(left, top, width, height);
		frame.getElement().getStyle().setProperty("left", left);
		frame.getElement().getStyle().setProperty("width", width);

		frame.getElement().getStyle().setTop(getWidget().getElement().getOffsetTop() - 22, Unit.PX);
		frame.getElement().getStyle().setHeight(getWidget().getOffsetHeight() + 25, Unit.PX);
	}	

	@Override
	public void updateFrame()
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
			frame.getElement().getStyle().setOpacity(0.8);
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