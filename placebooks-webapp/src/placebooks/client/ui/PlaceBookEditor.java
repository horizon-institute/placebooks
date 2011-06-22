package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends Composite
{
	public class SaveTimer extends Timer
	{
		private static final int saveDelay = 2000;

		public void markChanged()
		{
			cancel();
			schedule(saveDelay);
		}

		@Override
		public void run()
		{
			savingPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			PlaceBookService.savePlaceBook(canvas.getPlaceBook(), new AbstractCallback()
			{
				@Override
				public void failure(final Request request)
				{
					markChanged();
				}

				@Override
				public void success(final Request request, final Response response)
				{
					updatePlaceBook(PlaceBook.parse(response.getText()));
					savingPanel.getElement().getStyle().setDisplay(Display.NONE);
				}
			});
		}
	}

	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	HTML account;

	@UiField
	Panel backPanel;

	@UiField
	Panel canvasPanel;
	
	private final PlaceBookCanvas canvas;

	@UiField
	Image dragImage;

	@UiField
	DropMenu dropMenu;

	@UiField
	Panel loadingPanel;

	@UiField
	PlaceBookPalette palette;

	@UiField
	Panel savingPanel;

	@UiField
	EditablePanel title;

	@UiField
	Label zoomLabel;

	PlaceBookItemWidgetFrame dragItem = null;

	private PlaceBookPanel dragPanel = null;

	private PlaceBookItemWidgetFrame resizeItem;

	private SaveTimer saveTimer = new SaveTimer();

	private int zoom = 100;

	public PlaceBookEditor(final PlaceController placeController)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		canvas = new PlaceBookCanvas(placeController, new PlaceBookItemWidgetFrameFactory(this));
		canvasPanel.add(canvas);

		Event.addNativePreviewHandler(new Event.NativePreviewHandler()
		{
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event)
			{
				if ((event.getTypeInt() == Event.ONMOUSEDOWN || event.getTypeInt() == Event.ONMOUSEMOVE)
						&& event.getNativeEvent().getButton() == NativeEvent.BUTTON_LEFT
						&& event.getNativeEvent().getEventTarget().toString().startsWith("<img"))
				{
					event.getNativeEvent().preventDefault();
				}
			}
		});
		
		Window.setTitle("PlaceBook Editor");
		
		updatePalette();
	}

	public SaveTimer getSaveTimer()
	{
		return saveTimer;
	}

	public void markChanged()
	{
		saveTimer.markChanged();
	}

	public void updatePlaceBook(final PlaceBook placebook)
	{
		canvas.updatePlaceBook(placebook);
		
		if (placebook.hasMetadata("title"))
		{
			Window.setTitle(placebook.getMetadata("title") + " - PlaceBook Editor" );			
			title.getElement().setInnerText(placebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle("PlaceBook Editor" );			
			title.getElement().setInnerText("No Title");
		}
		
		account.setText(placebook.getOwner().getName());
		
		loadingPanel.setVisible(false);
	}
	
	
	public void updatePalette()
	{
		PlaceBookService.getPaletteItems(new AbstractCallback()
		{
			@Override
			public void success(Request request, Response response)
			{
				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
				palette.setPalette(items, PlaceBookEditor.this);
			}
		});			
	}

	@UiHandler("account")
	void handleAccountMenu(final ClickEvent event)
	{
		final List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem("Logout")
		{
			@Override
			public void run()
			{
				Window.open(GWT.getHostPageBaseURL() + "j_spring_security_logout", "_self", "");
			}
		});
		dropMenu.show(items, account.getAbsoluteLeft(), account.getAbsoluteTop() + account.getOffsetHeight());
		event.stopPropagation();
	}

	@UiHandler("backPanel")
	void handleAttach(final AttachEvent event)
	{
		canvas.reflow();
	}

	@UiHandler("backPanel")
	void handleClick(final ClickEvent event)
	{
		dropMenu.hide();
	}

	void handleDrag(final MouseEvent<?> event, final boolean finished)
	{
		if (dragItem != null)
		{
			final int x = event.getRelativeX(canvas.getElement());
			final int y = event.getRelativeY(canvas.getElement());
			for (final PlaceBookPanel panel : canvas.getPanels())
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
						saveTimer.markChanged();
					}
					return;
				}
			}

			if (finished)
			{
				canvas.remove(dragItem);
				dragImage.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				if (isInWidget(dragItem, x, y))
				{
					dragItem.showFrame();
				}
				else
				{
					dragItem.hideFrame();
				}
				dragItem = null;
				saveTimer.markChanged();
			}
			else
			{
				dragItem.getElement().getStyle().setDisplay(Display.NONE);
				dragImage.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				dragImage.getElement().getStyle().setLeft(x - 16, Unit.PX);
				dragImage.getElement().getStyle().setTop(y - 16, Unit.PX);
			}
		}
		else if (resizeItem != null)
		{
			final int y = event.getRelativeY(backPanel.getElement());
			final int heightPX = y - resizeItem.getElement().getAbsoluteTop() - 23;

			resizeItem.setContentHeight(heightPX);
			resizeItem.getPanel().reflow();

			if (finished)
			{
				resizeItem = null;
			}
		}
	}

	void handleDragStart(final PlaceBookItemWidgetFrame item, final MouseDownEvent event)
	{
		GWT.log("Start drag");
		if (dragItem != null) { return; }

		dragItem = item;
		dragItem.startDrag(event);
		dragImage.setResource(dragItem.getDragImage());
		dragImage.setStyleName(Resources.INSTANCE.style().dragImage());

		handleDrag(event, false);
	}

	@UiHandler("backPanel")
	void handleMouseMove(final MouseMoveEvent event)
	{
		handleDrag(event, false);
	}

	@UiHandler("backPanel")
	void handleMouseUp(final MouseUpEvent event)
	{
		handleDrag(event, true);
	}

	void handleResizeStart(final PlaceBookItemWidgetFrame itemFrame, final MouseDownEvent event)
	{
		GWT.log("Start resize");
		if (resizeItem != null) { return; }

		resizeItem = itemFrame;

		handleDrag(event, false);
	}

	@UiHandler("title")
	void handleTitleEdit(final KeyUpEvent event)
	{
		canvas.getPlaceBook().setMetadata("title", title.getElement().getInnerText());
		saveTimer.markChanged();
	}

	@UiHandler("zoomIn")
	void handleZoomIn(final ClickEvent event)
	{
		setZoom(zoom + 20);
	}

	@UiHandler("zoomOut")
	void handleZoomOut(final ClickEvent event)
	{
		setZoom(zoom - 20);
	}

	// private void add(final PlaceBookItemWidget item)
	// {
	// items.add(item);
	// canvas.add(item);
	// item.setDropMenu(dropMenu);
	//
	// item.addDragStartHandler(new MouseDownHandler()
	// {
	// @Override
	// public void onMouseDown(final MouseDownEvent event)
	// {
	// handleDragStart(item, event);
	// }
	// });
	//
	// item.addResizeStartHandler(new MouseDownHandler()
	// {
	// @Override
	// public void onMouseDown(final MouseDownEvent event)
	// {
	// handleResizeStart(item, event);
	// }
	// });
	//
	// item.addMouseOverHandler(new MouseOverHandler()
	// {
	// @Override
	// public void onMouseOver(final MouseOverEvent event)
	// {
	// if (dragItem == null)
	// {
	// item.showFrame();
	// }
	// }
	// });
	//
	// item.addMouseOutHandler(new MouseOutHandler()
	// {
	// @Override
	// public void onMouseOut(final MouseOutEvent event)
	// {
	// if (dragItem == null)
	// {
	// item.hideFrame();
	// }
	// }
	// });
	// }

	private boolean isInWidget(final Widget widget, final int x, final int y)
	{
		final int left = widget.getElement().getOffsetLeft();
		final int width = widget.getElement().getOffsetWidth();
		final int top = widget.getElement().getOffsetTop();
		final int height = widget.getElement().getOffsetHeight();
		return left < x && x < (left + width) && top < y && y < (top + height);
	}

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		canvas.getElement().getStyle().setWidth(zoom, Unit.PCT);
		zoomLabel.setText(zoom + "%");
		for (final PlaceBookPanel panel : canvas.getPanels())
		{
			panel.reflow();
		}
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}
}