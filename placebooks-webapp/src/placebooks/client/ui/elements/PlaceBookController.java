package placebooks.client.ui.elements;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.frames.PlaceBookItemDragFrame;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;
import placebooks.client.ui.menuItems.MenuItem;

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

public class PlaceBookController
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

	private final PlaceBookPages pages;

	private final PlaceBookItemDragFrame dragFrame = new PlaceBookItemDragFrame();

	private PlaceBookItemWidget dragItem;
	private PlaceBookItemFrame dragItemFrame;

	private DragState dragState = DragState.waiting;
	private final FlowPanel dropMenu = new FlowPanel();

	private final PlaceBookItemFrameFactory factory;
	private final SimplePanel insert = new SimplePanel();

	private int offsetx;
	private int offsety;

	private PlaceBookColumn oldPanel = null;

	private int originx;
	private int originy;

	private final PlaceBookSaveItem saveContext;

	private PlaceBookItemFrame selected;

	public PlaceBookController(final PlaceBookPages pages, final PlaceBookItemFrameFactory factory)
	{
		this(pages, factory, null);
	}
	
	public PlaceBookController(final PlaceBookPages pages, final PlaceBookItemFrameFactory factory,
			final PlaceBookSaveItem saveContext)
	{
		STYLES.style().ensureInjected();
		this.pages = pages;
		this.saveContext = saveContext;
		this.factory = factory;

		dropMenu.setStyleName(STYLES.style().dropMenu());

		final SimplePanel innerPanel = new SimplePanel();
		innerPanel.setStyleName(STYLES.style().insertInner());
		insert.add(innerPanel);
		insert.setStyleName(STYLES.style().insert());
	}

	public boolean canAdd(final PlaceBookItem addItem)
	{
		// TODO

		// if (addItem.is(ItemType.GPS))
		// {
		// for (final PlaceBookItemFrame item : pages.getItems())
		// {
		// if (item.getItem().is(ItemType.GPS)) { return false; }
		// }
		// }
		return true;
	}
	
	public void markChanged()
	{
		saveContext.markChanged();
	}

	public PlaceBookPages getPages()
	{
		return pages;
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

	public boolean canEdit()
	{
		return saveContext != null;
	}
	
	public DragState getState()
	{
		return dragState;
	}

	public void setSelected(final PlaceBookItemFrame selectedItem)
	{
		final PlaceBookItemFrame oldSelection = this.selected;
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
	public void setupDrag(final MouseEvent<?> event, final PlaceBookItemWidget item, final PlaceBookItemFrame itemFrame)
	{
		if (item == null) { return; }
		if (dragState == DragState.waiting)
		{
			// pages.add(insert);
			dragState = DragState.dragInit;
			this.dragItem = item;
			originx = event.getClientX();
			originy = event.getClientY();
			this.dragItemFrame = itemFrame;
		}
	}

	public void setupResize(final MouseEvent<?> event, final PlaceBookItemFrame frame)
	{
		if (dragState == DragState.waiting)
		{
			dragState = DragState.resizeInit;
			this.dragItemFrame = frame;
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

	private PlaceBookColumn getColumn(final MouseEvent<?> event)
	{
		final int canvasx = event.getX();// RelativeX(canvas.getElement());
		final int canvasy = event.getY();// RelativeY(canvas.getElement());
		for (final PlaceBookPage page : pages.getPages())
		{
			for (final PlaceBookColumn panel : page.getColumns())
			{
				if (panel.isIn(canvasx, canvasy)) { return panel; }
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
				GWT.log("Drag Start");
				if (dragItemFrame != null)
				{
					// TODO pages.remove(dragItemFrame);
				}
				dragFrame.setItemWidget(dragItem);
				if (dragItem.getOffsetHeight() == 0)
				{
					if (dragItem.getItem().hasParameter("height"))
					{
						final int heightPX = (int) (dragItem.getItem().getParameter("height") * pages.getOffsetHeight() / PlaceBookItemWidget.HEIGHT_PRECISION);
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
				GWT.log("Resize Start");
				setSelected(dragItemFrame);
				dragState = DragState.resizing;
			}
		}

		if (dragState == DragState.dragging)
		{
			dragFrame.getRootPanel().getElement().getStyle().setLeft(event.getClientX() - offsetx, Unit.PX);
			dragFrame.getRootPanel().getElement().getStyle().setTop(event.getClientY() - offsety, Unit.PX);

			final PlaceBookColumn newPanel = getColumn(event);
			if (oldPanel != newPanel && oldPanel != null)
			{
				oldPanel.reflow();
			}
			oldPanel = newPanel;

			if (newPanel != null)
			{
				newPanel.reflow(insert, event.getRelativeY(newPanel.getElement()), dragFrame.getItemWidget()
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
			final int heightPX = y - dragItemFrame.getRootPanel().getElement().getAbsoluteTop() - 13;
			final int canvasHeight = pages.getOffsetHeight();
			final int heightPCT = (int) ((heightPX * PlaceBookItemWidget.HEIGHT_PRECISION) / canvasHeight);

			dragItemFrame.getItem().setParameter("height", heightPCT);
			dragItemFrame.getItemWidget().refresh();
			dragItemFrame.getPanel().reflow();
		}
		event.stopPropagation();
	}

	public PlaceBookItemFrame createFrame(PlaceBookItem item)
	{
		return factory.createFrame(item, this);
	}
	
	private void handleDragEnd(final MouseEvent<?> event)
	{
		if (dragState == DragState.dragging)
		{
			GWT.log("Drag End");
			// TODO Move to dragFrame detach
			dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.HIDDEN);
			insert.removeFromParent();

			final PlaceBookColumn newPanel = getColumn(event);
			if (oldPanel != newPanel && oldPanel != null)
			{
				oldPanel.reflow();
			}
			oldPanel = newPanel;

			if (newPanel != null)
			{
				GWT.log("Dropped into panel " + newPanel.getIndex());
				newPanel.reflow(dragItem, event.getRelativeY(newPanel.getElement()), dragFrame.getItemWidget()
						.getOffsetHeight());
				newPanel.getPage().getPlaceBook().add(dragItem.getItem());				
				dragItem.getItem().setParameter("panel", newPanel.getIndex());
				final PlaceBookItemFrame frame = factory.createFrame(this);
				frame.setItemWidget(dragItem);
				newPanel.getPage().add(frame);				
				newPanel.reflow();
				saveContext.markChanged();
			}
			dragFrame.clearItemWidget();
		}
		else if (dragState == DragState.resizing)
		{
			saveContext.markChanged();
		}
		dragState = DragState.waiting;
	}

	private void hideMenu()
	{
		dropMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dropMenu.getElement().getStyle().setOpacity(0);
	}
}