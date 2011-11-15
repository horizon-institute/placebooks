package placebooks.client.ui.elements;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.items.MapItem;
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

public class PlaceBookInteractionHandler
{
	interface Style extends CssResource
	{
		String insert();
	    String insertInner();
	    String dropMenu();
	}

	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookInteractionHandler.css")
		Style style();
	}
	
	private static final Bundle STYLES = GWT.create(Bundle.class);
	
	public interface DragStartHandler
	{
		public void handleDragStart();
	}

	public enum DragState
	{
		dragging, dragInit, resizeInit, resizing, waiting,
	}

	private final static int DRAG_DISTANCE = 20;
	private final static int RESIZE_DISTANCE = 5;

	private final PlaceBookCanvas canvas;

	private final PlaceBookItemDragFrame dragFrame = new PlaceBookItemDragFrame();

	private PlaceBookItemWidget dragItem;
	private PlaceBookItemFrame dragItemFrame;

	private DragState dragState = DragState.waiting;
	private final FlowPanel dropMenu = new FlowPanel();

	private final PlaceBookItemFrameFactory factory;
	private final SimplePanel insert = new SimplePanel();

	private int offsetx;
	private int offsety;

	private PlaceBookPanel oldPanel = null;

	private int originx;
	private int originy;

	private final PlaceBookSaveItem saveContext;

	private PlaceBookItemFrame selected;

	public PlaceBookInteractionHandler(final PlaceBookCanvas canvas, final PlaceBookItemFrameFactory factory,
			final PlaceBookSaveItem saveContext)
	{
		STYLES.style().ensureInjected();
		this.canvas = canvas;
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
		if (addItem.is(ItemType.GPS))
		{
			for (final PlaceBookItemFrame item : canvas.getItems())
			{
				if (item.getItem().is(ItemType.GPS)) { return false; }
			}
		}
		return true;
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public PlaceBookSaveItem getContext()
	{
		return saveContext;
	}
	
	public void refreshMap()
	{
		for (final PlaceBookItemFrame itemFrame : canvas.getItems())
		{
			if (itemFrame.getItemWidget() instanceof MapItem)
			{
				((MapItem) itemFrame.getItemWidget()).refreshMarkers();
			}
		}
	}

	private PlaceBookPanel getPanel(final MouseEvent<?> event)
	{
		final int canvasx = event.getX();// RelativeX(canvas.getElement());
		final int canvasy = event.getY();// RelativeY(canvas.getElement());
		for (final PlaceBookPanel panel : canvas.getPanels())
		{
			if (panel.isIn(canvasx, canvasy)) { return panel; }
		}
		return null;
	}

	public PlaceBookItemFrame getSelected()
	{
		return selected;
	}

	public DragState getState()
	{
		return dragState;
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
					canvas.remove(dragItemFrame);
				}
				dragFrame.setItemWidget(dragItem);
				if (dragItem.getOffsetHeight() == 0)
				{
					if (dragItem.getItem().hasParameter("height"))
					{
						final int heightPX = (int) (dragItem.getItem().getParameter("height")
								* canvas.getPanels().iterator().next().getOffsetHeight() / PlaceBookItemWidget.HEIGHT_PRECISION);
						dragItem.setHeight(heightPX + "px");
					}
					else
					{
						dragItem.setHeight("300px");
					}
				}
				dragState = DragState.dragging;
				dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragFrame.getRootPanel().setWidth(canvas.getPanels().iterator().next().getOffsetWidth() + "px");

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

			final PlaceBookPanel newPanel = getPanel(event);
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
			final int canvasHeight = canvas.getPanels().iterator().next().getOffsetHeight();
			final int heightPCT = (int) ((heightPX * PlaceBookItemWidget.HEIGHT_PRECISION) / canvasHeight);

			dragItemFrame.getItem().setParameter("height", heightPCT);
			dragItemFrame.getItemWidget().refresh();
			dragItemFrame.getPanel().reflow();
		}
		event.stopPropagation();
	}

	private void handleDragEnd(final MouseEvent<?> event)
	{
		if (dragState == DragState.dragging)
		{
			GWT.log("Drag End");
			// TODO Move to dragFrame detach
			dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.HIDDEN);
			insert.removeFromParent();

			final PlaceBookPanel newPanel = getPanel(event);
			if (oldPanel != newPanel && oldPanel != null)
			{
				oldPanel.reflow();
			}
			oldPanel = newPanel;

			if (newPanel != null)
			{
				GWT.log("Dropped into panel " + newPanel.getIndex());
				newPanel.reflow(dragItem, event.getRelativeY(canvas.getElement()), dragFrame.getItemWidget()
						.getOffsetHeight());
				final PlaceBookItemFrame frame = factory.createFrame();
				frame.setItemWidget(dragItem);
				canvas.add(frame);
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
			// canvas.add(insert);
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
}