package org.placebooks.client.ui.items.frames;

import java.util.ArrayList;
import java.util.Collection;

import org.placebooks.client.Resources;
import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.ui.items.PlaceBookItemView;
import org.placebooks.client.ui.menuItems.DeleteItemMenuItem;
import org.placebooks.client.ui.menuItems.EditMapMenuItem;
import org.placebooks.client.ui.menuItems.FitToContentMenuItem;
import org.placebooks.client.ui.menuItems.HideTrailMenuItem;
import org.placebooks.client.ui.menuItems.MenuItem;
import org.placebooks.client.ui.menuItems.ShowTrailMenuItem;
import org.placebooks.client.ui.menuItems.UploadMenuItem;
import org.placebooks.client.ui.views.ColumnView;
import org.placebooks.client.ui.views.DragController;
import org.placebooks.client.ui.views.DragController.DragState;

import com.google.gwt.dom.client.Style.Position;
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
		@Override
		public PlaceBookItemFrame createFrame(final DragController handler)
		{
			return new PlaceBookItemPopupFrame(handler);
		}
	}

	private final PlaceBookItemView.FocusHandler focusHandler = new PlaceBookItemView.FocusHandler()
	{
		@Override
		public void itemFocusChanged(final boolean focussed)
		{
			if (focussed)
			{
				controller.setSelected(PlaceBookItemPopupFrame.this);
			}
			else
			{
				controller.setSelected(null);
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

	private final DragController controller;

	private Collection<MenuItem> menuItems = new ArrayList<MenuItem>();

	public PlaceBookItemPopupFrame(final DragController controller)
	{
		super();
		rootPanel = widgetPanel;
		widgetPanel.setStyleName(Resources.STYLES.style().widgetPanel());
		createFrame();
		this.controller = controller;
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
				controller.setSelected(PlaceBookItemPopupFrame.this);
				controller.showMenu(menuItems, x, y, true);
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		dragSection.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				controller.setupDrag(event, getItemWidget(), PlaceBookItemPopupFrame.this);
			}
		}, MouseDownEvent.getType());

		resizeSection.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				controller.setupResize(event, PlaceBookItemPopupFrame.this);
			}
		}, MouseDownEvent.getType());

		menuItems.add(new EditMapMenuItem(controller, this));
		menuItems.add(new DeleteItemMenuItem(controller.getSaveItem(), this));
		menuItems.add(new FitToContentMenuItem(controller, this));
		menuItems.add(new HideTrailMenuItem(controller, this));
		menuItems.add(new ShowTrailMenuItem(controller, this));
		menuItems.add(new UploadMenuItem(controller.getSaveItem(), this));

		frame.getElement().getStyle().setProperty("left", "0px");
		frame.getElement().getStyle().setProperty("width", "100%");

		markerImage.getElement().getStyle().setPosition(Position.ABSOLUTE);
		markerImage.getElement().getStyle().setZIndex(2);
		markerImage.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				controller.goToPage(getItem().getParameters().get("mapPage"));
			}
		});
	}

	@Override
	public void resize(final String height)
	{
		super.resize(height);
		resize();
	}

	@Override
	public void setColumn(final ColumnView newColumn)
	{
		if (column == newColumn) { return; }
		if (column != null)
		{
			column.remove(frame);
			column.remove(markerImage);
		}
		super.setColumn(newColumn);
		if (column != null)
		{
			column.add(frame);
		}
	}

	@Override
	public void setItemWidget(final PlaceBookItemView itemWidget)
	{
		super.setItemWidget(itemWidget);
		itemWidget.setFocusHandler(focusHandler);
	}

	@Override
	public void updateFrame()
	{
		dragSection.setText(itemWidget.getItem().getType().name());

		if (getItem().getParameters().containsKey("mapPage") && column != null)
		{
			markerImage.setResource(ItemController.getMarkerImage(getItem()));
			column.add(markerImage);
			markerImage.getElement().getStyle().setPosition(Position.ABSOLUTE);
			markerImage.getElement().getStyle().setZIndex(2);
			markerImage.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					controller.goToPage(getItem().getParameters().get("mapPage"));
				}
			});
			markerImage.setVisible(true);

		}
		else
		{
			markerImage.setVisible(false);
		}

		if (controller.getSelected() == this)
		{
			resize();
			frame.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setOpacity(1);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else if (highlighted && controller.getState() == DragState.waiting)
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

	void add(final MenuItem menuItem)
	{
		menuItems.add(menuItem);
	}

	private void resize()
	{
		frame.getElement().getStyle().setTop(rootPanel.getElement().getOffsetTop() - 22, Unit.PX);
		frame.getElement().getStyle().setHeight(rootPanel.getOffsetHeight() + 37, Unit.PX);

		markerImage.getElement().getStyle().setTop(rootPanel.getElement().getOffsetTop() + 3, Unit.PX);
	}

	private void setHighlight(final boolean highlight)
	{
		if (highlighted != highlight)
		{
			highlighted = highlight;
			updateFrame();
		}
	}
}
