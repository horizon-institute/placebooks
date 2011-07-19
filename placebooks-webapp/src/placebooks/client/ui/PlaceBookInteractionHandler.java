package placebooks.client.ui;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookInteractionHandler
{
	public interface DragStartHandler
	{
		public void handleDragStart();
	}

	private enum DragState
	{
		dragInit, dragging, waiting,
		resizeInit, resizing,
	}

	private final static int DRAG_DISTANCE = 20;
	private final static int RESIZE_DISTANCE = 5;	

	private final PlaceBookCanvas canvas;
	
	private final PlaceBookItemFrameFactory factory;

	private final PlaceBookItemDragFrame dragFrame = new PlaceBookItemDragFrame();
	private PlaceBookItemWidget dragItem;

	private PlaceBookItemFrame dragItemFrame;
	private DragState dragState = DragState.waiting;

	private final FlowPanel dropMenu = new FlowPanel();
	private final SimplePanel insert = new SimplePanel();

	private int offsetx;
	private int offsety;

	private PlaceBookPanel oldPanel = null;
	
	private int originx;
	private int originy;

	private final SaveContext saveContext;

	private PlaceBookItemFrame selected;

	public PlaceBookInteractionHandler(final PlaceBookCanvas canvas, final PlaceBookItemFrameFactory factory, final SaveContext saveContext)
	{
		this.canvas = canvas;
		this.saveContext = saveContext;
		this.factory = factory;

		final SimplePanel innerPanel = new SimplePanel();
		innerPanel.setStyleName(Resources.INSTANCE.style().insertInner());
		
		dropMenu.setStyleName(Resources.INSTANCE.style().dropMenu());

		insert.add(innerPanel);
		insert.setStyleName(Resources.INSTANCE.style().insert());
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}
	
	public SaveContext getContext()
	{
		return saveContext;
	}

	private PlaceBookPanel getPanel(final MouseEvent<?> event)
	{
		final int canvasx = event.getRelativeX(canvas.getElement());
		final int canvasy = event.getRelativeY(canvas.getElement());
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
				dragState = DragState.dragging;
				dragFrame.getRootPanel().getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragFrame.getRootPanel().setWidth(canvas.getPanels().iterator().next().getOffsetWidth() + "px");

				offsetx = dragFrame.getRootPanel().getOffsetWidth() / 2;
				offsety = 10;
			}
		}
		else if(dragState == DragState.resizeInit)
		{
			final int distance = Math.abs(event.getClientX() - originx) + Math.abs(event.getClientY() - originy);
			if (distance > RESIZE_DISTANCE)
			{
				GWT.log("Resize Start");
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
				newPanel.reflow(insert, event.getRelativeY(canvas.getElement()), dragFrame.getItemWidget().getOffsetHeight() + 14);
			}
			else
			{
				insert.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			}
		}
		else if(dragState == DragState.resizing)
		{
			final int y = event.getClientY();
			final int heightPX = y - dragItemFrame.getRootPanel().getElement().getAbsoluteTop();
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
			insert.getElement().getStyle().setVisibility(Visibility.HIDDEN);

			final PlaceBookPanel newPanel = getPanel(event);
			if (oldPanel != newPanel && oldPanel != null)
			{
				oldPanel.reflow();
			}
			oldPanel = newPanel;

			if (newPanel != null)
			{
				GWT.log("Add item");
				newPanel.reflow(dragItem, event.getRelativeY(canvas.getElement()), dragFrame.getItemWidget().getOffsetHeight());
				PlaceBookItemFrame frame = factory.createFrame();
				frame.setItemWidget(dragItem);
				canvas.add(frame);
				newPanel.reflow();
				saveContext.markChanged();
			}
			else
			{
				insert.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			}
		}
		else if(dragState == DragState.resizing)
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

	public void setSelected(PlaceBookItemFrame selectedItem)
	{
		PlaceBookItemFrame oldSelection = this.selected;
		selected = selectedItem;
		if(oldSelection != null)
		{
			oldSelection.updateFrame();
		}
		if(selected != null)
		{
			selected.updateFrame();
		}
		hideMenu();
	}

	/**
	 * On mouse down
	 */
	public void setupDrag(final MouseEvent<?> event, final PlaceBookItemWidget item,
			final PlaceBookItemFrame itemFrame)
	{
		if (dragState == DragState.waiting)
		{
			canvas.add(insert);
			dragState = DragState.dragInit;
			this.dragItem = item;
			originx = event.getClientX();
			originy = event.getClientY();
			this.dragItemFrame = itemFrame;
		}
	}

	public void setupResize(final MouseEvent<?> event, final PlaceBookItemFrame frame)
	{
		if(dragState == DragState.waiting)
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
			public void onClick(ClickEvent event)
			{
				hideMenu();
				setSelected(null);
			}
		}, ClickEvent.getType());
		
		panel.addDomHandler(new MouseMoveHandler()
		{
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				handleDrag(event);				
			}
		}, MouseMoveEvent.getType());
		
		panel.addDomHandler(new MouseUpHandler()
		{
			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				handleDragEnd(event);				
			}
		}, MouseUpEvent.getType());	
	}

	public void showMenu(final Iterable<? extends MenuItem> items, final int x, final int y, boolean alignRight)
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
		if(alignRight)
		{
			left -= dropMenu.getOffsetWidth();
		}
		dropMenu.getElement().getStyle().setTop(y, Unit.PX);
		dropMenu.getElement().getStyle().setLeft(left, Unit.PX);
		dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dropMenu.getElement().getStyle().setOpacity(1);
	}
}