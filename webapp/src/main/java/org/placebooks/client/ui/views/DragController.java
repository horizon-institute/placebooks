package org.placebooks.client.ui.views;

import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.controllers.PlaceBookController;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.model.Page;
import org.placebooks.client.ui.items.PlaceBookItemView;
import org.placebooks.client.ui.items.frames.PlaceBookItemDragFrame;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;
import org.placebooks.client.ui.menuItems.MenuItem;
import org.wornchaos.logger.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class DragController
{
	public interface DragStartHandler
	{
		public void handleDragStart();
	}

	public enum DragState
	{
		dragging, dragInit, resizeInit, resizing, waiting,
	}

	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookController.css")
		Style style();
	}

	interface Style extends CssResource
	{
		String dropMenu();

		String insert();

		String insertInner();
	}

	private static final Bundle STYLES = GWT.create(Bundle.class);

	private final static int DRAG_DISTANCE = 20;
	private final static int RESIZE_DISTANCE = 5;

	private final PagesView pages;

	private final PlaceBookItemDragFrame dragFrame = new PlaceBookItemDragFrame();

	private PlaceBookItemView dragItem;
	private PlaceBookItemFrame dragItemFrame;

	private DragState dragState = DragState.waiting;
	private final FlowPanel dropMenu = new FlowPanel();

	private final PlaceBookItemFrameFactory factory;
	private final SimplePanel insert = new SimplePanel();

	private int offsetx;
	private int offsety;

	private ColumnView oldColumn = null;

	private int originx;
	private int originy;

	private final PlaceBookController controller;

	private PlaceBookItemFrame selected;

	private final boolean editable;

	public DragController(final PagesView pages, final PlaceBookItemFrameFactory factory,
			final PlaceBookController controller, final boolean editable)
	{
		STYLES.style().ensureInjected();
		this.pages = pages;
		this.controller = controller;
		this.factory = factory;
		this.editable = editable;

		dropMenu.setStyleName(STYLES.style().dropMenu());

		final SimplePanel innerPanel = new SimplePanel();
		innerPanel.setStyleName(STYLES.style().insertInner());
		insert.add(innerPanel);
		insert.setStyleName(STYLES.style().insert());
	}

	public boolean canAdd(final Item addItem)
	{
		if (pages instanceof PagesView)
		{
			final PagesView bookPages = pages;
			final Page page = bookPages.getCurrentPage();
			return canAdd(page, addItem);
		}
		return true;
	}

	public boolean canEdit()
	{
		return editable;
	}

	public PlaceBookItemFrame createFrame(final Item item)
	{
		final ItemController itemController = new ItemController(item, controller, editable);
		return factory.createFrame(itemController, this);
	}

	public PagesView getPages()
	{
		return pages;
	}

	public PlaceBookController getSaveItem()
	{
		return controller;
	}

	// public void refreshMap()
	// {
	// for (final PlaceBookItemFrame itemFrame : pages.getItems())
	// {
	// if (itemFrame.getItemWidget() instanceof MapItem)
	// {
	// ((MapItem) itemFrame.getItemWidget()).refreshMarkers();
	// }
	// }
	// }

	public PlaceBookItemFrame getSelected()
	{
		return selected;
	}

	public DragState getState()
	{
		return dragState;
	}

	public void goToPage(final int page)
	{
		pages.goToPage(page);
	}

	public void goToPage(final Page page)
	{
		pages.goToPage(page);
	}

	public void markChanged()
	{
		controller.markChanged();
	}

	public void setSelected(final PlaceBookItemFrame selectedItem)
	{
		final PlaceBookItemFrame oldSelection = selected;
		selected = selectedItem;
		if (oldSelection != null)
		{
			oldSelection.updateFrame();
		}
		if (selected != null)
		{
			selected.updateFrame();
		}
		hideMenu();
	}

	/**
	 * On mouse down
	 */
	public void setupDrag(final MouseEvent<?> event, final PlaceBookItemView item, final PlaceBookItemFrame itemFrame)
	{
		if (item == null) { return; }
		if (dragState == DragState.waiting)
		{
			// pages.add(insert);
			dragState = DragState.dragInit;
			dragItem = item;
			originx = event.getClientX();
			originy = event.getClientY();
			dragItemFrame = itemFrame;
		}
	}

	public void setupResize(final MouseEvent<?> event, final PlaceBookItemFrame frame)
	{
		if (dragState == DragState.waiting)
		{
			dragState = DragState.resizeInit;
			dragItemFrame = frame;
			originx = event.getClientX();
			originy = event.getClientY();
		}
	}

	public void setupUIElements(final Panel panel)
	{
		panel.add(dragFrame.getRootPanel());
		panel.add(dropMenu);

		panel.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				hideMenu();
				setSelected(null);
			}
		}, ClickEvent.getType());

		panel.addDomHandler(new MouseMoveHandler()
		{
			@Override
			public void onMouseMove(final MouseMoveEvent event)
			{
				handleDrag(event);
			}
		}, MouseMoveEvent.getType());

		panel.addDomHandler(new MouseUpHandler()
		{
			@Override
			public void onMouseUp(final MouseUpEvent event)
			{
				handleDragEnd(event);
			}
		}, MouseUpEvent.getType());
	}

	public void showMenu(final Iterable<? extends MenuItem> items, final int x, final int y, final boolean alignRight)
	{
		dropMenu.clear();
		for (final MenuItem item : items)
		{
			if (item.isEnabled())
			{
				dropMenu.add(item);
			}
		}

		int left = x;
		if (alignRight)
		{
			left -= dropMenu.getOffsetWidth();
		}
		dropMenu.getElement().getStyle().setTop(y, Unit.PX);
		dropMenu.getElement().getStyle().setLeft(left, Unit.PX);
		dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dropMenu.getElement().getStyle().setOpacity(1);
	}

	private boolean canAdd(final Page page, final Item addItem)
	{
		if(page == null)
		{
			return false;
		}
		if (addItem.is(Type.GPSTraceItem))
		{
			for (final Item item : page.getItems())
			{
				if (item.is(Type.GPSTraceItem)) { return false; }
			}
		}
		return true;
	}

	private boolean canDrop(final ColumnView column, final Item item)
	{
		if (column == null) { return false; }
		if (!canAdd(column.getPage().getPage(), item)) { return false; }
		if (column.getRemainingHeight() < (column.getOffsetHeight() * 0.05)) { return false; }
		return true;
	}

	private ColumnView getColumn(final MouseEvent<?> event)
	{
		final int canvasx = event.getX();// RelativeX(canvas.getElement());
		final int canvasy = event.getY();// RelativeY(canvas.getElement());
		for (final PageView page : pages.getPages())
		{
			for (final ColumnView column : page.getColumns())
			{
				if (column.isIn(canvasx, canvasy)) { return column; }
			}
		}
		return null;
	}

	private void handleDrag(final MouseEvent<?> event)
	{
		if (dragState == DragState.dragInit)
		{
			final int distance = Math.abs(event.getClientX() - originx) + Math.abs(event.getClientY() - originy);
			if (distance > DRAG_DISTANCE)
			{
				Log.info("Drag Start");
				if (dragItemFrame != null)
				{
					dragItemFrame.getColumn().getPage().remove(dragItemFrame);
					// TODO pages.remove(dragItemFrame);
				}
				dragFrame.setItemWidget(dragItem);
				if (dragItem.getOffsetHeight() == 0)
				{
					if (dragItem.getItem().getParameters().containsKey("height"))
					{
						final int heightPX = (int) (dragItem.getItem().getParameters().get("height") * pages.getOffsetHeight() / PlaceBookItemView.HEIGHT_PRECISION);
						dragItem.setHeight(heightPX + "px");
					}
					else
					{
						dragItem.setHeight("300px");
					}
				}
				dragState = DragState.dragging;
				dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragFrame.getRootPanel().setWidth((pages.getOffsetWidth() / 3) + "px");

				offsetx = dragFrame.getRootPanel().getOffsetWidth() / 2;
				offsety = 10;
			}
		}
		else if (dragState == DragState.resizeInit)
		{
			final int distance = Math.abs(event.getClientX() - originx) + Math.abs(event.getClientY() - originy);
			if (distance > RESIZE_DISTANCE)
			{
				Log.info("Resize Start");
				setSelected(dragItemFrame);
				dragState = DragState.resizing;
			}
		}

		if (dragState == DragState.dragging)
		{
			dragFrame.getRootPanel().getElement().getStyle().setLeft(event.getClientX() - offsetx, Unit.PX);
			dragFrame.getRootPanel().getElement().getStyle().setTop(event.getClientY() - offsety, Unit.PX);

			final ColumnView newColumn = getColumn(event);
			if (oldColumn != newColumn && oldColumn != null)
			{
				oldColumn.reflow();
			}
			oldColumn = newColumn;

			if (canDrop(newColumn, dragFrame.getItem()))
			{
				newColumn.reflow(insert, event.getRelativeY(newColumn.getElement()), dragFrame.getItemWidget()
						.getOffsetHeight() + 14);
			}
			else
			{
				insert.removeFromParent();
			}
		}
		else if (dragState == DragState.resizing)
		{
			final int y = event.getClientY();
			final int heightPX = y - dragItemFrame.getRootPanel().getElement().getAbsoluteTop() - 10;
			final int maxHeight = dragItemFrame.getColumn().getRemainingHeight()
					+ dragItemFrame.getRootPanel().getOffsetHeight();
			ColumnView.setHeight(dragItemFrame, Math.min(heightPX, maxHeight));
			dragItemFrame.getColumn().reflow();
		}
		event.stopPropagation();
	}

	private void handleDragEnd(final MouseEvent<?> event)
	{
		if (dragState == DragState.dragging)
		{
			Log.info("Drag End");
			// TODO Move to dragFrame detach
			dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.HIDDEN);
			insert.removeFromParent();

			final ColumnView newColumn = getColumn(event);
			if (oldColumn != newColumn && oldColumn != null)
			{
				oldColumn.reflow();
			}
			oldColumn = newColumn;

			if (canDrop(newColumn, dragFrame.getItem()))
			{
				Log.info("Dropped into column " + newColumn.getIndex());
				final int maxHeight = newColumn.getRemainingHeight();

				newColumn.reflow(dragItem, event.getRelativeY(newColumn.getElement()));
				newColumn.getPage().getPage().getItems().add(dragItem.getItem());
				dragItem.getItem().getParameters().put("column", newColumn.getIndex());
				final PlaceBookItemFrame frame = factory.createFrame(this);
				frame.setItemWidget(dragItem);
				newColumn.getPage().add(frame);

				if (frame.getItemWidget().getOffsetHeight() > maxHeight)
				{
					ColumnView.setHeight(frame, maxHeight);
				}
				newColumn.reflow();

				controller.markChanged();
			}
			dragFrame.clearItemWidget();
		}
		else if (dragState == DragState.resizing)
		{
			controller.markChanged();
		}
		dragState = DragState.waiting;
	}

	private void hideMenu()
	{
		dropMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dropMenu.getElement().getStyle().setOpacity(0);
	}
}