package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.MenuItem;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class PlaceBookItemEditableWidget extends PlaceBookItemWidget
{
	private static PlaceBookItemEditableWidget selected = null;

	static void setSelected(final PlaceBookItemEditableWidget newSelected)
	{
		final PlaceBookItemEditableWidget old = selected;
		selected = newSelected;
		if (selected != null)
		{
			selected.updateFrame();
		}
		if (old != null)
		{
			old.updateFrame();
		}
	}

	private final PlaceBookItemFrame frame = new PlaceBookItemFrame();

	private boolean highlighted = false;

	private final MouseOverHandler highlightFrame = new MouseOverHandler()
	{
		@Override
		public void onMouseOver(final MouseOverEvent event)
		{
			highlighted = true;
			updateFrame();
		}
	};

	private final MouseOutHandler unhighlightFrame = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(final MouseOutEvent event)
		{
			highlighted = false;
			updateFrame();
		}
	};

	PlaceBookItemEditableWidget(final PlaceBookCanvas canvas, final PlaceBookItem item)
	{
		super(canvas, item);
	}

	public void add(final MenuItem menuItem)
	{
		frame.add(menuItem);
	}

	public void addDragStartHandler(final MouseDownHandler mouseDownHandler)
	{
		frame.addDragStartHandler(mouseDownHandler);
	}

	@Override
	public void addToCanvas(final PlaceBookCanvas canvas)
	{
		super.addToCanvas(canvas);
		canvas.add(frame);

		rootPanel.addDomHandler(highlightFrame, MouseOverEvent.getType());
		rootPanel.addDomHandler(unhighlightFrame, MouseOutEvent.getType());

		frame.addMouseOverHandler(highlightFrame);
		frame.addMouseOutHandler(unhighlightFrame);
	}

	@Override
	public void removeFromCanvas(final PlaceBookCanvas canvas)
	{
		super.removeFromCanvas(canvas);
		canvas.remove(frame);
	}

	public void setDropMenu(final DropMenu dropMenu)
	{
		frame.setDropMenu(dropMenu);
	}

	@Override
	public void setPosition(final float left, final int top)
	{
		super.setPosition(left, top);
		frame.getElement().getStyle().setTop(top - 22, Unit.PX);
		frame.getElement().getStyle().setLeft(left, Unit.PCT);
	}

	@Override
	void resize(final String width)
	{
		super.resize(width);
		frame.setWidth(width);
		frame.setHeight((getHeight() + 25) + "px");

	}

	private void updateFrame()
	{
		if (selected == this)
		{
			frame.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setOpacity(1);
			frame.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else if (highlighted)
		{
			rootPanel.getElement().getStyle().setZIndex(20);
			frame.getElement().getStyle().setZIndex(10);
			frame.getElement().getStyle().setOpacity(0.4);
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