package placebooks.client.ui;

import placebooks.client.PlaceBookEditor;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
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
	MousePanel dragSection;

	@UiField
	Panel menuButton;

	@UiField
	Panel borderSection;

	@UiField
	Panel frame;

	final DropMenu dropMenu = new DropMenu();

	private boolean drag = false;

	// private int dragOffsetX = 0;
	// private int dragOffsetY = 0;

	private int panel = 0;
	private int order = 0;

	private PlaceBookItem item;

	@UiField
	Panel widgetPanel;

	public PlaceBookItemFrame(final PlaceBookItem item)
	{
		this.item = item;
		initWidget(uiBinder.createAndBindUi(this));
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			final EditablePanel panel = new EditablePanel(item.getText());
			panel.addStyleName(PlaceBookEditor.RESOURCES.placebookpanel().textitem());
			widgetPanel.add(panel);
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			final Image image = new Image(item.getSourceURL());
			widgetPanel.add(image);
		}
	}

	void addDragStartHandler(final MouseDownHandler handler)
	{
		dragSection.addMouseDownHandler(handler);
	}

	int getOrder()
	{
		if (item.hasParameter("order")) { return item.getParameter("order"); }
		return order;
	}

	int getPanel()
	{
		return panel;
	}

	@UiHandler("menuButton")
	void handleMenuClick(final ClickEvent event)
	{
		GWT.log("Menu click");
	}

	@UiHandler("frame")
	void handleMouseOut(final MouseOutEvent event)
	{
		menuButton.getElement().getStyle().setOpacity(0);
		borderSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setOpacity(0);
	}

	@UiHandler("frame")
	void handleMouseOver(final MouseOverEvent event)
	{
		menuButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		menuButton.getElement().getStyle().setOpacity(1);
		borderSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);		
		borderSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);		
		dragSection.getElement().getStyle().setOpacity(1);
	}

	boolean isDragging()
	{
		return drag;
	}

	void setOrder(final int order)
	{
		GWT.log("Order: " + order);
		this.order = order;
	}

	void setPanel(final int panel)
	{
		this.panel = panel;
	}

	void startDrag(final MouseDownEvent event)
	{
		frame.getElement().getStyle().setProperty("boxShadow", "2px 2px 5px #666");
		// dragOffsetX = event.getRelativeX(frame.getElement());
		// dragOffsetY = event.getRelativeY(frame.getElement());
		drag = true;
	}

	void stopDrag()
	{
		frame.getElement().getStyle().setProperty("boxShadow", "none");
		drag = false;
	}
}