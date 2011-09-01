package placebooks.client.ui.items.frames;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookInteractionHandler;
import placebooks.client.ui.PlaceBookInteractionHandler.DragState;
import placebooks.client.ui.PlaceBookPanel;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.menuItems.AddMapMenuItem;
import placebooks.client.ui.menuItems.DeleteItemMenuItem;
import placebooks.client.ui.menuItems.EditTitleMenuItem;
import placebooks.client.ui.menuItems.FitToContentMenuItem;
import placebooks.client.ui.menuItems.HideTrailMenuItem;
import placebooks.client.ui.menuItems.MenuItem;
import placebooks.client.ui.menuItems.MoveMapMenuItem;
import placebooks.client.ui.menuItems.RemoveMapMenuItem;
import placebooks.client.ui.menuItems.SetSourceURLMenuItem;
import placebooks.client.ui.menuItems.ShowTrailMenuItem;
import placebooks.client.ui.menuItems.UploadMenuItem;

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

public class PlaceBookItemPopupFrame extends PlaceBookItemFrameWidget
{
	public static class Factory extends PlaceBookItemFrameFactory
	{
		private PlaceBookInteractionHandler interactionHandler;

		public Factory()
		{

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

		public void setInteractionHandler(final PlaceBookInteractionHandler interactionHandler)
		{
			this.interactionHandler = interactionHandler;
		}
	}

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
		public void itemFocusChanged(final boolean focussed)
		{
			if (focussed)
			{
				interactionHandler.setSelected(PlaceBookItemPopupFrame.this);
			}
			else
			{
				interactionHandler.setSelected(null);
			}
		}
	};

	private boolean highlighted = false;

	private final MouseOutHandler highlightOff = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(final MouseOutEvent event)
		{
			setHighlight(false);
		}
	};

	private final MouseOverHandler highlightOn = new MouseOverHandler()
	{
		@Override
		public void onMouseOver(final MouseOverEvent event)
		{
			setHighlight(true);
		}
	};

	private final PlaceBookInteractionHandler interactionHandler;

	private Collection<MenuItem> menuItems = new ArrayList<MenuItem>();

	public PlaceBookItemPopupFrame(final PlaceBookInteractionHandler interactHandler)
	{
		super();
		rootPanel = widgetPanel;
		widgetPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
		createFrame();
		this.interactionHandler = interactHandler;
		widgetPanel.addDomHandler(highlightOn, MouseOverEvent.getType());
		widgetPanel.addDomHandler(highlightOff, MouseOutEvent.getType());

		frame.addDomHandler(highlightOn, MouseOverEvent.getType());
		frame.addDomHandler(highlightOff, MouseOutEvent.getType());

		menuButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
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
			public void onMouseDown(final MouseDownEvent event)
			{
				interactionHandler.setupDrag(event, getItemWidget(), PlaceBookItemPopupFrame.this);
			}
		}, MouseDownEvent.getType());

		resizeSection.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				interactionHandler.setupResize(event, PlaceBookItemPopupFrame.this);
			}
		}, MouseDownEvent.getType());

		menuItems.add(new AddMapMenuItem(interactionHandler.getContext(), interactionHandler.getCanvas(), this));
		menuItems.add(new DeleteItemMenuItem(interactionHandler.getContext(), interactionHandler.getCanvas(), this));
		menuItems.add(new FitToContentMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new HideTrailMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new EditTitleMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new MoveMapMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new RemoveMapMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new SetSourceURLMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new ShowTrailMenuItem(interactionHandler.getContext(), this));
		menuItems.add(new UploadMenuItem(this));

		frame.getElement().getStyle().setProperty("left", "0px");
		frame.getElement().getStyle().setProperty("width", "100%");
	}

	void add(final MenuItem menuItem)
	{
		menuItems.add(menuItem);
	}

	private void resize()
	{
		frame.getElement().getStyle().setTop(rootPanel.getElement().getOffsetTop() - 22, Unit.PX);
		frame.getElement().getStyle().setHeight(rootPanel.getOffsetHeight() + 37, Unit.PX);
	}

	@Override
	public void resize(final String height)
	{
		super.resize(height);

		frame.getElement().getStyle().setTop(rootPanel.getElement().getOffsetTop() - 22, Unit.PX);
		frame.getElement().getStyle().setHeight(rootPanel.getOffsetHeight() + 37, Unit.PX);
	}

	private void setHighlight(final boolean highlight)
	{
		if (highlighted != highlight)
		{
			highlighted = highlight;
			updateFrame();
		}
	}

	@Override
	public void setItemWidget(final PlaceBookItemWidget itemWidget)
	{
		super.setItemWidget(itemWidget);
		itemWidget.setFocusHandler(focusHandler);
		itemWidget.setChangeHandler(changeHandler);
	}

	@Override
	public void setPanel(final PlaceBookPanel newPanel)
	{
		if (panel == newPanel) { return; }
		if (panel != null)
		{
			panel.remove(frame);
		}
		super.setPanel(newPanel);
		if (panel != null)
		{
			panel.add(frame);
		}
	}

	@Override
	public void updateFrame()
	{
		if (interactionHandler.getSelected() == this)
		{
			resize();
			frame.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setOpacity(1);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else if (highlighted && interactionHandler.getState() == DragState.waiting)
		{
			resize();
			rootPanel.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setZIndex(10);
			frame.getElement().getStyle().setOpacity(0.8);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else
		{
			rootPanel.getElement().getStyle().setZIndex(1);
			frame.getElement().getStyle().setZIndex(0);
			frame.getElement().getStyle().setOpacity(0);
			frame.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}
}