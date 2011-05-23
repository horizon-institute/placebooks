package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookCanvas.SaveTimer;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MapPanel;
import placebooks.client.ui.widget.MenuItem;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
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

	private static final MenuItem deleteItem = new MenuItem("Delete")
	{
		@Override
		public void run()
		{
			// TODO
		}
	};

	private static PlaceBookItemFrameUiBinder uiBinder = GWT.create(PlaceBookItemFrameUiBinder.class);

	@UiField
	Panel borderSection;

	@UiField
	MousePanel dragSection;

	@UiField
	MousePanel frame;

	@UiField
	Panel menuButton;

	@UiField
	MousePanel widgetPanel;

	private DropMenu dropMenu;

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
						saveTimer.markChanged();
						updateItemWidget();
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
			if (item.getClassName().equals("placebooks.model.ImageItem"))
			{
				upload.setName("image." + item.getKey());
			}
			else if (item.getClassName().equals("placebooks.model.AudioItem"))
			{
				upload.setName("audio." + item.getKey());
			}
			else if (item.getClassName().equals("placebooks.model.VideoItem"))
			{
				upload.setName("video." + item.getKey());
			}

			final Button closeButton = new Button("Upload", new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					dialogBox.hide();
					form.submit();
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
		    form.addSubmitCompleteHandler(new SubmitCompleteHandler()
			{
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event)
				{
					GWT.log("Submit complete: " + event.getResults());
					updateItemWidget();
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

	public PlaceBookItemFrame(final SaveTimer timer, final PaletteItem item)
	{
		this(timer, item.createItem());
	}

	public PlaceBookItemFrame(final SaveTimer timer, final PlaceBookItem item)
	{
		this.item = item;
		this.saveTimer = timer;
		initWidget(uiBinder.createAndBindUi(this));
		menuItems.add(deleteItem);
		if (item.getClassName().equals("placebooks.model.TextItem"))
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
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
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
		else if (item.getClassName().equals("placebooks.model.AudioItem"))
		{
			final Audio audio = Audio.createIfSupported();
			audio.setStyleName(Resources.INSTANCE.style().imageitem());
			audio.setControls(true);
			if (audio != null)
			{
				widgetPanel.add(audio);
			}
			menuItems.add(setItemSourceURL);
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			final Video video = Video.createIfSupported();
			video.setStyleName(Resources.INSTANCE.style().imageitem());
			video.setControls(true);
			if (video != null)
			{
				widgetPanel.add(video);
			}
			menuItems.add(setItemSourceURL);
		}
		else if (item.getClassName().equals("placebooks.model.GPSTraceItem"))
		{
			final MapPanel panel = new MapPanel("mapPanel" + item.getKey());
			panel.setHeight("500px");
			menuItems.add(setItemSourceURL);
			widgetPanel.add(panel);
		}
		else if (item.getClassName().equals("placebooks.model.WebBundleItem"))
		{
			final Frame frame = new Frame(item.getSourceURL());
			frame.setStyleName(Resources.INSTANCE.style().imageitem());
			widgetPanel.add(frame);
			menuItems.add(setItemSourceURL);
		}
		updateItemWidget();
	}

	public PlaceBookItem getItem()
	{
		return item;
	}

	public void setPlaceBookItem(final PlaceBookItem item)
	{
		this.item = item;
		updateItemWidget();
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

	ImageResource getDragImage()
	{
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			return Resources.INSTANCE.text();
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			return Resources.INSTANCE.picture();
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			return Resources.INSTANCE.movies();
		}
		else if (item.getClassName().equals("placebooks.model.AudioItem"))
		{
			return Resources.INSTANCE.music();
		}
		else if (item.getClassName().equals("placebooks.model.GPSTraceItem"))
		{
			return Resources.INSTANCE.map();
		}
		else if (item.getClassName().equals("placebooks.model.WebBundleItem")) { return Resources.INSTANCE.web_page(); }
		return null;
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

	private void updateItemWidget()
	{
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			// final EditablePanel panel = (EditablePanel)widgetPanel.getWidget(0);
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			final Image image = (Image) widgetPanel.getWidget(0);
			if (item.getKey() == null)
			{
				image.setUrl(item.getSourceURL());
			}
			else
			{
				image.setUrl(GWT.getHostPageBaseURL() + "placebooks/a/admin/serve/imageitem/" + item.getKey() + "?"
						+ System.currentTimeMillis());
			}
		}
		else if (item.getClassName().equals("placebooks.model.AudioItem"))
		{
			final Audio audio = (Audio) widgetPanel.getWidget(0);
			if (item.getKey() == null)
			{
				audio.setSrc(item.getSourceURL());
			}
			else
			{
				audio.setSrc(GWT.getHostPageBaseURL() + "placebooks/a/admin/serve/audioitem/" + item.getKey() + "?"
						+ System.currentTimeMillis());
			}
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			final Video video = (Video) widgetPanel.getWidget(0);
			if (item.getKey() == null)
			{
				video.setSrc(item.getSourceURL());
			}
			else
			{
				video.setSrc(GWT.getHostPageBaseURL() + "placebooks/a/admin/serve/videoitem/" + item.getKey() + "?"
						+ System.currentTimeMillis());
			}
		}
		else if (item.getClassName().equals("placebooks.model.GPSTraceItem"))
		{
			final MapPanel mapPanel = (MapPanel) widgetPanel.getWidget(0);
			if (item.getKey() == null)
			{
				mapPanel.setURL(item.getSourceURL());
			}
			else
			{
				mapPanel.setURL(GWT.getHostPageBaseURL() + "placebooks/a/admin/serve/gpstraceitem/" + item.getKey());
			}
		}
		else if (item.getClassName().equals("placebooks.model.WebBundleItem"))
		{
			final Frame frame = (Frame) widgetPanel.getWidget(0);
			frame.setUrl(item.getSourceURL());
		}
	}
}