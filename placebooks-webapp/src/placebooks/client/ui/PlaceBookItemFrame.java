package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookCanvas.SaveTimer;
import placebooks.client.ui.openlayers.MapWidget;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MenuItem;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemFrame extends Composite
{

	interface PlaceBookItemFrameUiBinder extends UiBinder<Widget, PlaceBookItemFrame>
	{
	}

	private static final double HEIGHT_PRECISION = 10000;

	private static final PlaceBookItemFrameUiBinder uiBinder = GWT.create(PlaceBookItemFrameUiBinder.class);

	@UiField
	Panel borderSection;

	@UiField
	MousePanel dragSection;

	@UiField
	MousePanel frame;

	@UiField
	Panel menuButton;

	@UiField
	MousePanel resizeSection;

	@UiField
	MousePanel widgetPanel;

	private DropMenu dropMenu;

	private final MenuItem hideTrail = new MenuItem("Hide Trail")
	{
		@Override
		public boolean isEnabled()
		{
			return item.getClassName().equals("placebooks.model.GPSTraceItem")
					&& item.getMetadata("routeVisible", "true").equals("true");
		}

		@Override
		public void run()
		{
			item.setMetadata("routeVisible", "false");
			markChanged();
			refresh();
		}
	};

	private PlaceBookItem item;

	private final List<MenuItem> menuItems = new ArrayList<MenuItem>();

	private PlaceBookPanel panel;

	private SaveTimer saveTimer;

	private final MenuItem setItemSourceURL = new MenuItem("Set URL")
	{
		@Override
		public void run()
		{
			final TextBox textBox = new TextBox();
			textBox.setWidth("300px");
			textBox.setValue(item.getSourceURL());

			final PopupPanel dialogBox = new PopupPanel(false, true);

			final Button closeButton = new Button("Set URL", new ClickHandler()
			{

				@Override
				public void onClick(final ClickEvent event)
				{
					dialogBox.hide();
					if (!textBox.getValue().equals(item.getSourceURL()))
					{
						item.setSourceURL(textBox.getValue());
						markChanged();
						refresh();
					}
				}
			});

			final Panel panel = new FlowPanel();
			panel.add(textBox);
			panel.add(closeButton);

			dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
			dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
			dialogBox.setGlassEnabled(true);
			dialogBox.setAnimationEnabled(true);
			dialogBox.setWidget(panel);
			dialogBox.center();
			dialogBox.show();
		}
	};

	private final MenuItem showTrail = new MenuItem("Show Trail")
	{
		@Override
		public boolean isEnabled()
		{
			return item.getClassName().equals("placebooks.model.GPSTraceItem")
					&& item.getMetadata("routeVisible", "true").equals("false");
		}

		@Override
		public void run()
		{
			item.removeMetadata("routeVisible");
			markChanged();
			refresh();

		}
	};

	private final MenuItem upload = new MenuItem("Upload")
	{
		@Override
		public void run()
		{
			final Panel panel = new FlowPanel();
			final FormPanel form = new FormPanel();
			final FileUpload upload = new FileUpload();
			final Hidden hidden = new Hidden("itemKey", item.getKey());
			final PopupPanel dialogBox = new PopupPanel(false, true);
			final String type = item.getClassName().substring(17, item.getClassName().length() - 4).toLowerCase();
			upload.setName(type + "." + item.getKey());

			final Button closeButton = new Button("Upload", new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					// dialogBox.hide();
					form.submit();
					// TODO Working indicator
				}
			});

			final Button cancelButton = new Button("Cancel", new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					dialogBox.hide();
				}
			});

			form.setAction(GWT.getHostPageBaseURL() + "/placebooks/a/admin/add_item/upload");
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setWidget(panel);
			form.addSubmitHandler(new SubmitHandler()
			{
				@Override
				public void onSubmit(final SubmitEvent event)
				{
					GWT.log("Submitted");

				}
			});
			form.addSubmitCompleteHandler(new SubmitCompleteHandler()
			{
				@Override
				public void onSubmitComplete(final SubmitCompleteEvent event)
				{
					GWT.log("Submit complete: " + event.getResults());
					refresh();
					dialogBox.hide();
				}
			});

			panel.add(upload);
			panel.add(hidden);
			panel.add(closeButton);
			panel.add(cancelButton);

			dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
			dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
			dialogBox.setGlassEnabled(true);
			dialogBox.setAnimationEnabled(true);
			dialogBox.setWidget(form);
			dialogBox.center();
			dialogBox.show();
		}
	};

	public PlaceBookItemFrame(final SaveTimer timer, final PlaceBookCanvas canvas, final PalettePlaceBookItem item)
	{
		this(timer, canvas, item.createItem());
	}

	public PlaceBookItemFrame(final SaveTimer timer, final PlaceBookCanvas canvas, final PlaceBookItem item)
	{
		this.item = item;
		this.saveTimer = timer;
		initWidget(uiBinder.createAndBindUi(this));
		menuItems.add(new DeletePlaceBookMenuItem("Delete", canvas, this));
		menuItems.add(new AddMapMenuItem("App to Map", canvas, this));
		menuItems.add(new RemoveMapMenuItem("Remove from Map", this));
		menuItems.add(showTrail);
		menuItems.add(hideTrail);
		if (item.is(ItemType.TEXT))
		{
			final EditablePanel panel = new EditablePanel(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			panel.addKeyUpHandler(new KeyUpHandler()
			{
				@Override
				public void onKeyUp(final KeyUpEvent event)
				{
					item.setText(panel.getElement().getInnerHTML());
					saveTimer.markChanged();
				}
			});
			widgetPanel.add(panel);
		}
		else if (item.is(ItemType.IMAGE))
		{
			final Image image = new Image();
			image.setStyleName(Resources.INSTANCE.style().imageitem());
			image.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(final LoadEvent event)
				{
					if (panel != null)
					{
						panel.reflow();
					}
				}
			});
			widgetPanel.add(image);
			menuItems.add(setItemSourceURL);
			menuItems.add(upload);
		}
		else if (item.is(ItemType.AUDIO))
		{
			final Audio audio = Audio.createIfSupported();
			audio.setStyleName(Resources.INSTANCE.style().imageitem());
			audio.setControls(true);
			if (audio != null)
			{
				widgetPanel.add(audio);
			}
			menuItems.add(setItemSourceURL);
			menuItems.add(upload);
		}
		else if (item.is(ItemType.VIDEO))
		{
			final Video video = Video.createIfSupported();
			video.setStyleName(Resources.INSTANCE.style().imageitem());
			video.setControls(true);
			if (video != null)
			{
				widgetPanel.add(video);
			}
			menuItems.add(setItemSourceURL);
			menuItems.add(upload);
		}
		else if (item.is(ItemType.GPS))
		{
			// TODO Handle null key
			final MapWidget panel = new MapWidget(item.getKey(), canvas);
			panel.setWidth("100%");
			panel.setHeight("100%");
			menuItems.add(setItemSourceURL);
			menuItems.add(upload);
			widgetPanel.add(panel);
		}
		else if (item.is(ItemType.WEB))
		{
			final Frame frame = new Frame(item.getSourceURL());
			frame.setStyleName(Resources.INSTANCE.style().imageitem());
			menuItems.add(setItemSourceURL);
			widgetPanel.add(frame);
		}
		refresh();
	}

	public int getContentHeight()
	{
		return widgetPanel.getElement().getClientHeight();
	}

	public PlaceBookItem getItem()
	{
		return item;
	}

	public void markChanged()
	{
		saveTimer.markChanged();
	}

	public void refresh()
	{
		if (item.hasParameter("height"))
		{
			widgetPanel.getWidget(0).setHeight("100%");
		}

		if (item.is(ItemType.TEXT))
		{
			// final EditablePanel panel = (EditablePanel)widgetPanel.getWidget(0);
		}
		else if (item.is(ItemType.IMAGE))
		{
			final Image image = (Image) widgetPanel.getWidget(0);
			if (item.hasParameter("height"))
			{
				image.setWidth("auto");
			}
			image.setUrl(item.getURL());
		}
		else if (item.is(ItemType.AUDIO))
		{
			final Audio audio = (Audio) widgetPanel.getWidget(0);
			audio.setSrc(item.getURL());
		}
		else if (item.is(ItemType.VIDEO))
		{
			final Video video = (Video) widgetPanel.getWidget(0);
			video.setSrc(item.getURL());
		}
		else if (item.is(ItemType.GPS))
		{
			final MapWidget mapPanel = (MapWidget) widgetPanel.getWidget(0);
			mapPanel.setURL(item.getURL(), item.getMetadata("routeVisible", "true").equals("true"));
		}
		else if (item.is(ItemType.WEB))
		{
			final Frame frame = (Frame) widgetPanel.getWidget(0);
			frame.setUrl(item.getSourceURL());
		}
	}

	public void setPlaceBookItem(final PlaceBookItem item)
	{
		this.item = item;
		refresh();
	}

	public void setTop(final int top)
	{
		getElement().getStyle().setTop(top - 20, Unit.PX);
	}

	void addDragStartHandler(final MouseDownHandler handler)
	{
		dragSection.addMouseDownHandler(handler);
	}

	void addMouseOutHandler(final MouseOutHandler handler)
	{
		frame.addMouseOutHandler(handler);
	}

	void addMouseOverHandler(final MouseOverHandler handler)
	{
		widgetPanel.addMouseOverHandler(handler);
	}

	void addResizeStartHandler(final MouseDownHandler handler)
	{
		resizeSection.addMouseDownHandler(handler);
	}

	ImageResource getDragImage()
	{
		return item.getIcon();
	}

	int getOrder()
	{
		if (item.hasParameter("order")) { return item.getParameter("order"); }
		return 0;
	}

	PlaceBookPanel getPanel()
	{
		return panel;
	}

	@UiHandler("menuButton")
	void handleMenuClick(final ClickEvent event)
	{
		final int x = menuButton.getElement().getAbsoluteLeft();
		final int y = menuButton.getElement().getAbsoluteTop() + menuButton.getElement().getClientHeight();
		dropMenu.show(menuItems, x, y);
		event.stopPropagation();
	}

	void hideFrame()
	{
		frame.getElement().getStyle().setZIndex(0);
		menuButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		menuButton.getElement().getStyle().setOpacity(0);
		menuButton.getElement().getStyle().setZIndex(0);
		borderSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		borderSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dragSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setZIndex(0);
		dragSection.getElement().getStyle().setCursor(Cursor.DEFAULT);
		resizeSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		resizeSection.getElement().getStyle().setOpacity(0);
		resizeSection.getElement().getStyle().setZIndex(0);
		resizeSection.getElement().getStyle().setCursor(Cursor.DEFAULT);
	}

	void resize()
	{
		if (item.hasParameter("height") && panel != null)
		{
			final int height = item.getParameter("height");
			final double heightPCT = height / HEIGHT_PRECISION;
			final int heightPX = (int) (panel.getOffsetHeight() * heightPCT);

			widgetPanel.getElement().getStyle().setHeight(heightPX, Unit.PX);
		}
	}

	void setContentHeight(final int heightPX)
	{
		final int heightPCT = (int) ((heightPX * HEIGHT_PRECISION) / panel.getOffsetHeight());
		item.setParameter("height", heightPCT);
		saveTimer.markChanged();
		// Assuming resize() will be called (via reflow on the panel) so don't set height here
	}

	void setDropMenu(final DropMenu dropMenu)
	{
		this.dropMenu = dropMenu;
	}

	void setOrder(final int order)
	{
		GWT.log("Order: " + order);
		item.setParameter("order", order);
	}

	void setPanel(final PlaceBookPanel panel)
	{
		if (this.panel != null)
		{
			this.panel.remove(this);
		}
		this.panel = panel;
		if (panel != null)
		{
			item.setParameter("panel", panel.getIndex());
			panel.add(this);
		}
	}

	void showFrame()
	{
		frame.getElement().getStyle().setZIndex(5);
		menuButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		menuButton.getElement().getStyle().setOpacity(1);
		menuButton.getElement().getStyle().setZIndex(5);
		borderSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		borderSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dragSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setZIndex(5);
		dragSection.getElement().getStyle().setCursor(Cursor.MOVE);
		resizeSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		resizeSection.getElement().getStyle().setOpacity(1);
		resizeSection.getElement().getStyle().setZIndex(5);
		resizeSection.getElement().getStyle().setCursor(Cursor.S_RESIZE);
	}

	void startDrag(final MouseDownEvent event)
	{
		frame.addStyleName(Resources.INSTANCE.style().dragShadow());
		frame.getElement().getStyle().setZIndex(20);
		menuButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		menuButton.getElement().getStyle().setOpacity(0.6);
		borderSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		borderSection.getElement().getStyle().setOpacity(0.6);
		dragSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dragSection.getElement().getStyle().setOpacity(0.6);

		setPanel(null);

		// dragOffsetX = event.getRelativeX(frame.getElement());
		// dragOffsetY = event.getRelativeY(frame.getElement());
	}

	void stopDrag()
	{
		frame.removeStyleName(Resources.INSTANCE.style().dragShadow());
		frame.getElement().getStyle().setZIndex(0);
	}
}