package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.places.EditorPlace;
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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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

public class PlaceBookCanvas extends Composite
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
			PlaceBookService.savePlaceBook(placebook, new AbstractCallback()
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

	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookCanvas>
	{
	}

	private static final int columns = 3;

	private static final String NEW_AUDIO_ITEM = "{\"@class\":\"placebooks.model.AudioItem\",\"sourceURL\":\"http://www.tonycuffe.com/mp3/tailtoddle_lo.mp3\",\"metadata\":{\"title\":\"Test Audio\"},\"parameters\":{}}";

	private static final String NEW_GPS_ITEM = "{\"@class\":\"placebooks.model.GPSTraceItem\",\"sourceURL\":\"http://www.topografix.com/fells_loop.gpx\",\"metadata\":{\"title\":\"Test Route\"},\"parameters\":{}}";

	private static final String NEW_IMAGE_ITEM = "{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\",\"metadata\":{\"title\":\"Image Item\"},\"parameters\":{}}";
	private static final String NEW_TEXT_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Text Item\"},\"parameters\":{},\"text\":\"New Text Block\"}";
	private static final String NEW_VIDEO_ITEM = "{\"@class\":\"placebooks.model.VideoItem\",\"sourceURL\":\"http://www.cs.nott.ac.uk/~ktg/sample_iPod.mp4\",\"metadata\":{\"title\":\"Video Item\"},\"parameters\":{}}";
	private static final String NEW_WEB_ITEM = "{\"@class\":\"placebooks.model.WebBundleItem\",\"sourceURL\":\"http://www.google.com/\",\"metadata\":{\"title\":\"Web Bundle\"},\"parameters\":{}}";
	private static final int pageMargin = 5;
	private static final int pages = 2;

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	HTML account;

	@UiField
	Panel backPanel;

	@UiField
	Panel canvas;

	@UiField
	Image dragImage;

	@UiField
	DropMenu dropMenu;

	@UiField
	Panel loadingPanel;

	@UiField
	Panel palette;

	@UiField
	Panel savingPanel;

	@UiField
	EditablePanel title;

	@UiField
	Label zoomLabel;

	private PlaceBookItemFrame dragItem = null;

	private PlaceBookPanel dragPanel = null;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	private PlaceBook placebook;

	private final PlaceController placeController;

	private PlaceBookItemFrame resizeItem;

	private SaveTimer saveTimer = new SaveTimer();

	private int zoom = 100;

	public PlaceBookCanvas(final PlaceController placeController)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.placeController = placeController;

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

		for (int index = 0; index < (pages * columns); index++)
		{
			final PlaceBookPanel panel = new PlaceBookPanel(index, columns, pageMargin);
			panels.add(panel);
			canvas.add(panel);
		}

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				reflow();
			}
		});
	}

	private void addFolderItem(final PaletteFolderItem folder, final PaletteItem item)
	{
		folder.add(item);
		if (item instanceof PalettePlaceBookItem)
		{
			item.addDragStartHandler(new MouseDownHandler()
			{
				@Override
				public void onMouseDown(final MouseDownEvent event)
				{
					final PlaceBookItemFrame frame = new PlaceBookItemFrame(saveTimer, PlaceBookCanvas.this,
							(PalettePlaceBookItem) item);
					add(frame);
					placebook.getItems().push(frame.getItem());
					startDrag(frame, event);
				}
			});
		}
		else if (item instanceof PaletteFolderItem)
		{
			item.addClickHander(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					setPaletteFolder((PaletteFolderItem) item);
				}
			});
		}
	}

	public Iterable<PlaceBookItemFrame> getItems()
	{
		return items;
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}

	public void markChanged()
	{
		saveTimer.markChanged();
	}

	public void reflow()
	{
		for (final PlaceBookPanel panel : panels)
		{
			panel.reflow();
		}
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		canvas.remove(item);
		placebook.removeItem(item.getItem());
		saveTimer.markChanged();
	}

	public void setPalette(final JsArray<PlaceBookItem> items)
	{
		palette.clear();

		final PaletteFolderItem root = new PaletteFolderItem("root", null);

		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_TEXT_ITEM)));
		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_IMAGE_ITEM)));
		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_VIDEO_ITEM)));
		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_WEB_ITEM)));
		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_GPS_ITEM)));
		addFolderItem(root, new PalettePlaceBookItem(PlaceBookItem.parse(NEW_AUDIO_ITEM)));

		for (int index = 0; index < items.length(); index++)
		{
			// TODO Organize
			addFolderItem(root, new PalettePlaceBookItem(items.get(index)));
		}

		//addFolderItem(root, new PaletteFolderItem("Test", root));
		
		setPaletteFolder(root);
	}
	
	private void hideGUI()
	{
		canvas.getElement().getStyle().setTop(0, Unit.PX);
		canvas.getElement().getStyle().setRight(0, Unit.PX);
		palette.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void updatePlaceBook(final PlaceBook newPlacebook)
	{
		if (this.placebook != null
				&& (this.placebook.getKey() == null || !this.placebook.getKey().equals(newPlacebook.getKey())))
		{
			placeController.goTo(new EditorPlace(newPlacebook));
		}

		this.placebook = newPlacebook;

		account.setHTML(placebook.getOwner().getName() + " <small>▼</small>");

		final Map<String, PlaceBookItemFrame> kept = new HashMap<String, PlaceBookItemFrame>();
		final Collection<PlaceBookItemFrame> removals = new ArrayList<PlaceBookItemFrame>();
		for (final PlaceBookItemFrame item : items)
		{
			final PlaceBookItem newItem = getItem(newPlacebook, item.getItem().getKey());
			if (newItem == null)
			{
				item.removeFromParent();
				removals.add(item);
			}
			else
			{
				final PlaceBookPanel panel = item.getPanel();
				final int index = newItem.hasParameter("panel") ? newItem.getParameter("panel") : 0;
				if (panel.getIndex() != index)
				{
					item.setPanel(panels.get(index));
				}
				item.setPlaceBookItem(newItem);
				kept.put(newItem.getKey(), item);
			}
		}
		items.removeAll(removals);

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			if (!kept.containsKey(item.getKey()))
			{
				final PlaceBookItemFrame frame = new PlaceBookItemFrame(saveTimer, this, item);
				add(frame);
				if (item.hasParameter("panel"))
				{
					frame.setPanel(panels.get(item.getParameter("panel")));
				}
				else
				{
					frame.setPanel(panels.get(0));
				}
			}
		}

		if (placebook.hasMetadata("title"))
		{
			title.getElement().setInnerText(placebook.getMetadata("title"));
		}
		else
		{
			title.getElement().setInnerText("No Title");
		}

		reflow();

		loadingPanel.setVisible(false);
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
		reflow();
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
						saveTimer.markChanged();
					}
					return;
				}
			}

			if (finished)
			{
				remove(dragItem);
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

	@UiHandler("title")
	void handleTitleEdit(final KeyUpEvent event)
	{
		placebook.setMetadata("title", title.getElement().getInnerText());
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

	private void add(final PlaceBookItemFrame item)
	{
		items.add(item);
		canvas.add(item);
		item.setDropMenu(dropMenu);

		item.addDragStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				startDrag(item, event);
			}
		});

		item.addResizeStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				startResize(item, event);
			}
		});

		item.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(final MouseOverEvent event)
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
			public void onMouseOut(final MouseOutEvent event)
			{
				if (dragItem == null)
				{
					item.hideFrame();
				}
			}
		});
	}

	private PlaceBookItem getItem(final PlaceBook placebook, final String key)
	{
		if (key == null) { return null; }
		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			if (key.equals(item.getKey())) { return item; }
		}
		return null;
	}

	private boolean isInWidget(final Widget widget, final int x, final int y)
	{
		final int left = widget.getElement().getOffsetLeft();
		final int width = widget.getElement().getOffsetWidth();
		final int top = widget.getElement().getOffsetTop();
		final int height = widget.getElement().getOffsetHeight();
		return left < x && x < (left + width) && top < y && y < (top + height);
	}

	private void setPaletteFolder(final PaletteFolderItem folder)
	{
		palette.clear();
		if (folder.getParent() != null)
		{
			// TODO Show back button
		}

		for (final PaletteItem paletteItem : folder)
		{
			palette.add(paletteItem);
		}
	}

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		final float panelWidth = (zoom - (pageMargin * 2)) / columns;
		zoomLabel.setText(zoom + "%");
		for (final PlaceBookPanel panel : panels)
		{
			panel.setWidth(panelWidth);
			panel.reflow();
		}
	}

	private void startDrag(final PlaceBookItemFrame itemFrame, final MouseDownEvent event)
	{
		GWT.log("Start drag");
		if (dragItem != null) { return; }

		dragItem = itemFrame;
		dragItem.startDrag(event);
		dragImage.setResource(dragItem.getDragImage());
		dragImage.setStyleName(Resources.INSTANCE.style().dragImage());

		handleDrag(event, false);
	}

	private void startResize(final PlaceBookItemFrame itemFrame, final MouseDownEvent event)
	{
		GWT.log("Start resize");
		if (resizeItem != null) { return; }

		resizeItem = itemFrame;

		handleDrag(event, false);
	}
}
