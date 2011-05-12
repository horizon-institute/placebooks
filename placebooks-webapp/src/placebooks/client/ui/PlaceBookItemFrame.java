package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemFrame extends Composite
{

	interface PlaceBookItemFrameUiBinder extends UiBinder<Widget, PlaceBookItemFrame>
	{
	}

	private static PlaceBookItemFrameUiBinder uiBinder = GWT.create(PlaceBookItemFrameUiBinder.class);

	@UiField
	Panel borderSection;

	@UiField
	MousePanel dragSection;

	final DropMenu dropMenu = new DropMenu();

	@UiField
	MousePanel frame;

	@UiField
	Panel menuButton;

	@UiField
	MousePanel widgetPanel;

	private PlaceBookItem item;
	
	private PlaceBookPanel panel;

	public PlaceBookItemFrame(final SaveTimer timer, final PlaceBookItem item)
	{
		this.item = item;
		initWidget(uiBinder.createAndBindUi(this));
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			final EditablePanel panel = new EditablePanel(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			panel.addKeyUpHandler(new KeyUpHandler()
			{				
				@Override
				public void onKeyUp(KeyUpEvent event)
				{
					item.setText(panel.getElement().getInnerText());
					timer.markChanged();
				}
			});
			widgetPanel.add(panel);
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			final Image image = new Image(item.getSourceURL());
			image.setStyleName(Resources.INSTANCE.style().imageitem());
			widgetPanel.add(image);
		}
	}

	public PlaceBookItemFrame(SaveTimer timer, PaletteItem item)
	{
		this(timer, item.createItem());
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

	public PlaceBookItem getItem()
	{
		return item;
	}
	
	ImageResource getDragImage()
	{
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			return Resources.INSTANCE.text();
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem")) { return Resources.INSTANCE.picture(); }
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
		GWT.log("Menu click");
	}

	void hideFrame()
	{
		frame.getElement().getStyle().setZIndex(0);
		menuButton.getElement().getStyle().setOpacity(0);
		menuButton.getElement().getStyle().setZIndex(0);
		borderSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setZIndex(0);
		dragSection.getElement().getStyle().setCursor(Cursor.DEFAULT);
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