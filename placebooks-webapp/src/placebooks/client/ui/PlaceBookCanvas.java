package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookCanvas extends Composite
{
	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookCanvas>
	{
	}

	private static final int columns = 3;

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	Panel backPanel;

	@UiField
	Panel canvas;

	@UiField
	Image dragImage;

	@UiField
	Panel palette;

	private PlaceBookPanel dragPanel = null;

	private PlaceBookItemFrame dragItem = null;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PaletteItem> paletteItems = new ArrayList<PaletteItem>();

	// private PlaceBook placebook;

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	public PlaceBookCanvas()
	{
		initWidget(uiBinder.createAndBindUi(this));

		Event.addNativePreviewHandler(new Event.NativePreviewHandler()
		{
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event)
			{
				if ((event.getTypeInt() == Event.ONMOUSEDOWN || event.getTypeInt() == Event.ONMOUSEMOVE) && event.getNativeEvent().getEventTarget().toString().startsWith("<img"))
				{
					event.getNativeEvent().preventDefault();
				}
			}
		});

		for (int index = 0; index < columns; index++)
		{
			final PlaceBookPanel panel = new PlaceBookPanel(index, columns);
			panels.add(panel);
			canvas.add(panel);
		}

		add(new PaletteItem(
				"{\"@class\":\"placebooks.model.TextItem\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"New Text Block\"}",
				"New Text"));
		add(new PaletteItem(
				"{\"@class\":\"placebooks.model.ImageItem\",\"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\",\"metadata\":{},\"parameters\":{}}",
				"New Image"));

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
			frame.setPanel(panels.get(0));
		}
	}

	@UiHandler("backPanel")
	void handleMouseMove(final MouseMoveEvent event)
	{
		handleDrag(event, false);
	}

	void handleDrag(final MouseEvent<?> event, final boolean finished)
	{
		if (dragItem != null)
		{
			final int x = event.getRelativeX(backPanel.getElement());
			final int y = event.getRelativeY(backPanel.getElement());
			for (final PlaceBookPanel panel : panels)
			{
				if (panel.isIn(x, y))
				{
					if (!panel.equals(dragPanel))
					{
						if (dragPanel != null)
						{
							dragPanel.reflow();
						}
						dragPanel = panel;
					}
					dragItem.getElement().getStyle().setDisplay(Display.BLOCK);
					dragImage.getElement().getStyle().setVisibility(Visibility.HIDDEN);
					panel.reflow(dragItem, y, finished);
					if (finished)
					{
						dragItem.setPanel(panel);
						dragItem.stopDrag();
						dragItem = null;
					}
					return;
				}
			}

			if (finished)
			{
				items.remove(dragItem);
				canvas.remove(dragItem);
				dragImage.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				dragItem = null;
			}
			else
			{
				dragItem.getElement().getStyle().setDisplay(Display.NONE);
				dragImage.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragImage.getElement().getStyle().setLeft(x - 16, Unit.PX);
				dragImage.getElement().getStyle().setTop(y - 16, Unit.PX);
			}
		}
	}

	@UiHandler("backPanel")
	void handleMouseUp(final MouseUpEvent event)
	{
		handleDrag(event, true);
	}

	private void add(final PaletteItem item)
	{
		paletteItems.add(item);
		palette.add(item);
		item.addDragStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				final PlaceBookItemFrame frame = new PlaceBookItemFrame(item.getPlaceBookItem());
				add(frame);
				startDrag(frame, event);
			}
		});
	}

	private void add(final PlaceBookItemFrame item)
	{
		items.add(item);
		canvas.add(item);

		item.addDragStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				startDrag(item, event);
			}
		});

		item.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(MouseOverEvent event)
			{
				if (dragItem == null)
				{
					item.showFrame();
				}
			}
		});

		item.addMouseOutHandler(new MouseOutHandler()
		{
			@Override
			public void onMouseOut(MouseOutEvent event)
			{
				if (dragItem == null)
				{
					item.hideFrame();
				}
			}
		});
	}

	private void startDrag(final PlaceBookItemFrame itemFrame, final MouseDownEvent event)
	{
		GWT.log("Start drag");
		if(dragItem != null)
		{
			return;
		}

		dragItem = itemFrame;
		dragItem.startDrag(event);
		dragImage.setResource(dragItem.getDragImage());
		dragImage.setStyleName(Resources.INSTANCE.style().dragImage());

		handleDrag(event, false);
	}
}