package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.EditablePanel;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
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

	private int dragOffsetX = 0;
	private int dragOffsetY = 0;
	
	private int panel = 0;
	private int order = 0;

	private PlaceBookItem item;

	@UiField
	Panel widgetPanel;

	public PlaceBookItemFrame(final PlaceBookItem item)
	{
		this.item = item;
		if(item.getClassName().equals("placebooks.model.TextItem"))
		{
			EditablePanel panel = new EditablePanel(item.getText());
			
			widgetPanel.add(panel);
		}
		initWidget(uiBinder.createAndBindUi(this));

	}

	boolean isDragging()
	{
		return drag;
	}

	@UiHandler("frame")
	void handleMouseOut(final MouseOutEvent event)
	{
		frame.getElement().getStyle().setZIndex(0);			
		menuButton.getElement().getStyle().setOpacity(0);
		borderSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setOpacity(0);
	}

	@UiHandler("frame")
	void handleMouseOver(final MouseOverEvent event)
	{
		frame.getElement().getStyle().setZIndex(2);
		menuButton.getElement().getStyle().setOpacity(1);
		borderSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setOpacity(1);
	}
	
	int getPanel()
	{
		return panel;
	}
	
	int getOrder()
	{
		return order;
	}
	
	void setPanel(int panel)
	{
		this.panel = panel;
	}
	
	void setOrder(int order)
	{
		GWT.log("Order: " + order);
		this.order = order;
	}
	
	@UiHandler("menuButton")
	void handleMenuClick(final ClickEvent event)
	{
		GWT.log("Menu click");
	}
	
	void addDragStartHandler(final MouseDownHandler handler)
	{
		dragSection.addMouseDownHandler(handler);
	}
	
	void startDrag(final MouseDownEvent event)
	{
		frame.getElement().getStyle().setOpacity(0.8);
		frame.getElement().getStyle().setProperty("boxShadow", "2px 2px 5px #666");
		dragOffsetX = event.getRelativeX(frame.getElement());
		dragOffsetY = event.getRelativeY(frame.getElement());		
		drag = true;		
	}

	void stopDrag()
	{
		frame.getElement().getStyle().setOpacity(1);
		frame.getElement().getStyle().setProperty("boxShadow", "none");		
		drag = false;
	}
}