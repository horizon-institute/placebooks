package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
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
import com.google.gwt.media.client.Video;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
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
				public void onClick(ClickEvent event)
				{
					dialogBox.hide();
					if(!textBox.getValue().equals(item.getSourceURL()))
					{
						item.setSourceURL(textBox.getValue());
						saveTimer.markChanged();
						updateItemWidget();
					}
				}
			});

			Panel panel = new FlowPanel();
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
			final Image image = new Image(item.getSourceURL());
			image.setStyleName(Resources.INSTANCE.style().imageitem());
			image.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					if(panel != null)
					{
						panel.reflow();
					}	
				}
			});
			widgetPanel.add(image);
			menuItems.add(setItemSourceURL);
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			final Video video = Video.createIfSupported();
			video.setStyleName(Resources.INSTANCE.style().imageitem());
			video.setSrc(item.getSourceURL());
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
			panel.setURL(item.getSourceURL());
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
	}
	
	private void updateItemWidget()
	{
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			//final EditablePanel panel = (EditablePanel)widgetPanel.getWidget(0);
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			final Image image = (Image)widgetPanel.getWidget(0);
			image.setUrl(item.getSourceURL());
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			final Video video = (Video)widgetPanel.getWidget(0);
			video.setSrc(item.getSourceURL());
		}
		else if (item.getClassName().equals("placebooks.model.GPSTraceItem"))
		{

		}
		else if (item.getClassName().equals("placebooks.model.WebBundleItem"))
		{
			final Frame frame = (Frame)widgetPanel.getWidget(0);
			frame.setUrl(item.getSourceURL());
		}
	}
	
	public PlaceBookItem getItem()
	{
		return item;
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
}