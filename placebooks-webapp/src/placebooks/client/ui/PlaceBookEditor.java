package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.places.PlaceBookHomePlace;
import placebooks.client.ui.places.PlaceBookPreviewPlace;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends Composite
{
	public class SaveContext extends Timer
	{
		private static final int saveDelay = 2000;

		// private boolean changed = false;
		// private boolean saving = false;
		// private boolean waiting = false;

		public void markChanged()
		{
			cancel();
			schedule(saveDelay);
			// changed = true;
		}

		@Override
		public void run()
		{
			savingPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

			final PlaceBook placebook = canvas.getPlaceBook();
			placebook.clearItems();
			for (final PlaceBookItemWidget item : canvas.getItems())
			{
				placebook.getItems().push(item.getItem());
			}

			// changed = false;
			// saving = true;
			PlaceBookService.savePlaceBook(placebook, new AbstractCallback()
			{
				@Override
				public void failure(final Request request, final Response response)
				{
					markChanged();
					savingPanel.getElement().getStyle().setDisplay(Display.NONE);
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
	Panel account;

	@UiField
	Label accountLabel;

	@UiField
	Panel backPanel;

	@UiField
	Panel canvasPanel;

	@UiField
	Image dragImage;

	@UiField
	DropMenu dropMenu;

	@UiField
	Panel loadingPanel;

	@UiField
	Palette palette;

	@UiField
	Panel savingPanel;

	@UiField
	EditablePanel title;

	@UiField
	Label zoomLabel;

	private final PlaceBookCanvas canvas;

	private final PlaceBookItemDragHandler dragHandler;

	private final PlaceController placeController;

	private SaveContext saveContext = new SaveContext();

	private int zoom = 100;

	public PlaceBookEditor(final PlaceController placeController)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.placeController = placeController;

		final PlaceBookItemWidgetFactory factory = new PlaceBookItemEditableWidgetFactory(this);
		canvas = new PlaceBookCanvas(placeController, factory, true);
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

		dragHandler = new PlaceBookItemDragHandler(canvas, saveContext);
		backPanel.add(dragHandler.getDragFrame());

		Window.setTitle("PlaceBook Editor");

		updatePalette();
		final Timer timer = new Timer()
		{
			@Override
			public void run()
			{
				updatePalette();
			}
		};
		timer.scheduleRepeating(10000);
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public PlaceBookItemDragHandler getDragHandler()
	{
		return dragHandler;

	}

	public SaveContext getSaveContext()
	{
		return saveContext;
	}

	public void markChanged()
	{
		saveContext.markChanged();
	}

	public void updatePalette()
	{
		PlaceBookService.getPaletteItems(new AbstractCallback()
		{
			@Override
			public void failure(final Request request, final Response response)
			{
				if (response.getStatusCode() == 401)
				{
					placeController.goTo(new PlaceBookHomePlace());
				}
			}

			@Override
			public void success(final Request request, final Response response)
			{
				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
				palette.setPalette(items, dragHandler);
			}
		});
	}

	public void updatePlaceBook(final PlaceBook placebook)
	{
		canvas.updatePlaceBook(placebook);

		if (placebook.hasMetadata("title"))
		{
			Window.setTitle(placebook.getMetadata("title") + " - PlaceBook Editor");
			title.getElement().setInnerText(placebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle("PlaceBook Editor");
			title.getElement().setInnerText("No Title");
		}

		accountLabel.setText(placebook.getOwner().getName());

		loadingPanel.setVisible(false);
	}

	@UiHandler("account")
	void handleAccountMenu(final ClickEvent event)
	{
		final List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem("Print Preview")
		{

			@Override
			public void run()
			{
				placeController.goTo(new PlaceBookPreviewPlace(getCanvas().getPlaceBook()));
			}
		});

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

	@UiHandler("backPanel")
	void handleMouseMove(final MouseMoveEvent event)
	{
		dragHandler.handleDrag(event);
	}

	@UiHandler("backPanel")
	void handleMouseUp(final MouseUpEvent event)
	{
		dragHandler.handleDragEnd(event);
	}

	@UiHandler("title")
	void handleTitleEdit(final KeyUpEvent event)
	{
		canvas.getPlaceBook().setMetadata("title", title.getElement().getInnerText());
		saveContext.markChanged();
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

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		canvas.getElement().getStyle().setWidth(zoom, Unit.PCT);
		canvas.getElement().getStyle().setFontSize(zoom, Unit.PCT);
		zoomLabel.setText(zoom + "%");
		for (final PlaceBookPanel panel : canvas.getPanels())
		{
			panel.reflow();
		}
	}

	// void clearFocus(final PlaceBookItemWidgetFrame oldFocus)
	// {
	// if(currentFocus != null && currentFocus == oldFocus)
	// {
	// currentFocus.hideFrame();
	// currentFocus = null;
	// }
	// }
	//
	// void setFocus(final PlaceBookItemWidget newFocus)
	// {
	// if(currentFocus != null)
	// {
	// currentFocus.hideFrame();
	// }
	// this.currentFocus = newFocus;
	// if(lockFocus == null)
	// {
	// currentFocus.showFrame();
	// }
	// }
	//
	// void lockFocus(final PlaceBookItemWidgetFrame newFocus)
	// {
	// if(lockFocus != null && lockFocus != newFocus)
	// {
	// lockFocus.hideFrame();
	// }
	// lockFocus = newFocus;
	// if(currentFocus != null && currentFocus != lockFocus)
	// {
	// currentFocus.hideFrame();
	// }
	// lockFocus.showFrame();
	// }
	//
	// void clearLock(final PlaceBookItemWidgetFrame oldFocus)
	// {
	// if(lockFocus != null && lockFocus == oldFocus)
	// {
	// lockFocus.hideFrame();
	// lockFocus = null;
	// }
	//
	// if(currentFocus != null)
	// {
	// currentFocus.showFrame();
	// }
	// }
}