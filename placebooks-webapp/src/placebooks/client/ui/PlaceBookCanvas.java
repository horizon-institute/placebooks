package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookCanvas extends Composite
{
	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookCanvas>
	{
	}

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	Panel canvas;

	private static final int columns = 3;

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	// private final List<PlaceBookItemFrame> paletteItems = new ArrayList<PlaceBookItemFrame>();

	private PlaceBookItemFrame dragItem = null;
	private int dragPanel = 0;
	private int dragOrder = 0;

	@UiField
	Panel palette;

	// private PlaceBook placebook;

	private final Collection<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	public PlaceBookCanvas()
	{
		initWidget(uiBinder.createAndBindUi(this));

		for (int index = 0; index < columns; index++)
		{
			final PlaceBookPanel panel = new PlaceBookPanel(index, columns);
			panels.add(panel);
			canvas.add(panel);
		}

		// add(new PlaceBookItemFrame(
		// null,
		// new EditablePanel(
		// "Lorem ipsum dolor sit amet, <b>consectetur</b> adipisicing elit, sed do <i>eiusmod</i> tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.<ul><li>Sed ut perspiciatis, unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt, explicabo</li></ul>")));
		// add(new PlaceBookItemFrame(null, new
		// Image("http://farm4.static.flickr.com/3229/2476270026_87b4f3e236.jpg")));

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				reflow();
			}
		});
	}

	public void reflow()
	{
		for (final PlaceBookPanel panel : panels)
		{
			panel.reflow();
		}
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		// this.placebook = placebook;
		items.clear();

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			final PlaceBookItemFrame frame = new PlaceBookItemFrame(item);
			add(frame);
		}
	}

	@UiHandler("canvas")
	void handleDrag(final MouseMoveEvent event)
	{
		if (dragItem != null)
		{
			final int x = event.getRelativeX(canvas.getElement());
			final int y = event.getRelativeY(canvas.getElement());
			GWT.log(x + ", " + y);
			for (final PlaceBookPanel panel : panels)
			{
				if (panel.isIn(x, y))
				{
					if (panel.getIndex() != dragPanel)
					{
						final PlaceBookPanel lastPanel = panels.get(dragPanel);
						lastPanel.reflow();
						dragPanel = panel.getIndex();
					}
					dragOrder = panel.reflow(dragItem, y);
					break;
				}
			}
		}
	}

	@UiHandler("canvas")
	void handleMouseUp(final MouseUpEvent event)
	{
		if (dragItem != null)
		{
			GWT.log("Stop drag");
			setPanel(dragItem, dragPanel, dragOrder);
			dragItem.stopDrag();
			dragItem = null;
			reflow();
		}
	}

	private void add(final PlaceBookItemFrame item)
	{
		items.add(item);
		canvas.add(item);

		final PlaceBookPanel panel = panels.get(item.getPanel());
		panel.add(item);

		item.addDragStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				startDrag(item, event);
			}
		});
	}

	private void setPanel(final PlaceBookItemFrame item, final int panelIndex, final int order)
	{
		if (panelIndex == item.getPanel() && order == item.getOrder()) { return; }
		PlaceBookPanel panel = panels.get(item.getPanel());
		panel.remove(item);
		panel = panels.get(panelIndex);
		item.setPanel(panelIndex);
		item.setOrder(order);
		panel.add(item);
	}

	private void startDrag(final PlaceBookItemFrame itemFrame, final MouseDownEvent event)
	{
		GWT.log("Start drag");
		if (dragItem != null)
		{
			dragItem.stopDrag();
		}
		dragItem = itemFrame;
		dragItem.startDrag(event);
	}
}