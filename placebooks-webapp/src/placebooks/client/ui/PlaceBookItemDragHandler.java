package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor.SaveContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemDragHandler
{
	public interface DragStartHandler
	{
		public void handleDragStart();
	}

	private enum DragState
	{
		dragging, init, waiting,
	}

	private final static int DRAG_DISTANCE = 10;

	private final PlaceBookCanvas canvas;

	private final PlaceBookItemFrame dragFrame = new PlaceBookItemFrame();

	private DragStartHandler dragStartHandler;
	private DragState dragState = DragState.waiting;

	private final SimplePanel insert = new SimplePanel();
	private PlaceBookItem item;

	private int offsetx;
	private int offsety;

	private PlaceBookPanel oldPanel = null;
	private int originx;

	private int originy;

	private final SaveContext saveContext;

	private final SimplePanel widgetPanel = new SimplePanel();

	public PlaceBookItemDragHandler(final PlaceBookCanvas canvas, final SaveContext saveContext)
	{
		this.canvas = canvas;

		widgetPanel.setStyleName(Resources.INSTANCE.style().frameWidgetPanel());
		final SimplePanel innerPanel = new SimplePanel();
		insert.add(innerPanel);
		insert.setStyleName(Resources.INSTANCE.style().insert());
		innerPanel.setStyleName(Resources.INSTANCE.style().insertInner());

		dragFrame.frame.add(widgetPanel);
		dragFrame.frame.setStyleName(Resources.INSTANCE.style().dragFrame());

		this.saveContext = saveContext;
	}

	public Widget getDragFrame()
	{
		return dragFrame;
	}

	void handleDrag(final MouseEvent<?> event)
	{
		if (dragState == DragState.init)
		{
			final int distance = Math.abs(event.getClientX() - originx) + Math.abs(event.getClientY() - originy);
			if (distance > DRAG_DISTANCE)
			{
				if (dragStartHandler != null)
				{
					dragStartHandler.handleDragStart();
				}
				final Widget widget = canvas.getItemFactory().createWidget(item);
				widgetPanel.clear();
				widgetPanel.add(widget);
				dragState = DragState.dragging;
				dragFrame.frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragFrame.setWidth(canvas.getPanels().iterator().next().getOffsetWidth() + "px");

				offsetx = dragFrame.getOffsetWidth() / 2;
				offsety = 10;
			}
		}

		if (dragState == DragState.dragging)
		{
			dragFrame.frame.getElement().getStyle().setLeft(event.getClientX() - offsetx, Unit.PX);
			dragFrame.frame.getElement().getStyle().setTop(event.getClientY() - offsety, Unit.PX);

			final PlaceBookPanel newPanel = getPanel(event);
			if (oldPanel != newPanel && oldPanel != null)
			{
				oldPanel.reflow();
			}
			oldPanel = newPanel;

			if (newPanel != null)
			{
				newPanel.reflow(insert, event.getRelativeY(canvas.getElement()), widgetPanel.getOffsetHeight() + 14);
			}
			else
			{
				insert.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			}
		}
		event.stopPropagation();
	}

	void handleDragEnd(final MouseEvent<?> event)
	{
		if (dragState == DragState.dragging)
		{
			GWT.log("Drag End");
			dragFrame.frame.getElement().getStyle().setVisibility(Visibility.HIDDEN);
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
				newPanel.reflow(item, event.getRelativeY(canvas.getElement()), widgetPanel.getOffsetHeight());
				final PlaceBookItemWidget itemWidget = canvas.add(item);
				itemWidget.getPanel().reflow();
				saveContext.markChanged();
			}
			else
			{
				insert.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			}
		}
		dragState = DragState.waiting;
	}

	/**
	 * On mouse down
	 */
	public void handleDragInitialization(final MouseEvent<?> event, final PlaceBookItem item,
			final DragStartHandler dragStartHandler)
	{
		if (dragState == DragState.waiting)
		{
			canvas.add(insert);
			dragState = DragState.init;
			this.item = item;
			originx = event.getClientX();
			originy = event.getClientY();
			this.dragStartHandler = dragStartHandler;
		}
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
}