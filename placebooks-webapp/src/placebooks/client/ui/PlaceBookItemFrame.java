package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemFrame extends Composite
{

	private static PlaceBookItemFrameUiBinder uiBinder = GWT.create(PlaceBookItemFrameUiBinder.class);

	interface PlaceBookItemFrameUiBinder extends UiBinder<Widget, PlaceBookItemFrame>
	{
	}

	@UiField
	Panel dragSection;
	
	@UiField
	Panel expandSection;

	@UiField
	Panel menuButton;
	
	@UiField
	Panel borderSection;
	
	@UiField
	Panel frame;
	
	private boolean move = false;
	private boolean resize = false;	

	private int dragOffsetX = 0;
	private int dragOffsetY = 0;	
	
	private PlaceBookItem item;
	
	@UiField
	Widget widget;
	
	boolean acceptMouseMove()
	{
		return move || resize;
	}
	
	void handleMouseMove(int x, int y)
	{
		if(move)
		{
			frame.getElement().getStyle().setLeft(x - dragOffsetX, Unit.PX);
			frame.getElement().getStyle().setTop(y - dragOffsetY, Unit.PX);			
		}
		else if(resize)
		{
			frame.getElement().getStyle().setWidth(x - dragOffsetX, Unit.PX);
			frame.getElement().getStyle().setHeight(y - dragOffsetY, Unit.PX);			
		}
	}
	
	public PlaceBookItemFrame(PlaceBookItem item)
	{
		this.item = item;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("frame")
	void handleMouseOver(MouseOverEvent event)
	{
		GWT.log("Mouse Over");
		menuButton.getElement().getStyle().setOpacity(1);
		borderSection.getElement().getStyle().setOpacity(1);
		expandSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setOpacity(1);
	}
	
	@UiHandler("frame")
	void handleMouseOut(MouseOutEvent event)
	{
		GWT.log("Mouse Out");
		if(!resize)
		{
			menuButton.getElement().getStyle().setOpacity(0);
			borderSection.getElement().getStyle().setOpacity(0);
			expandSection.getElement().getStyle().setOpacity(0);
			dragSection.getElement().getStyle().setOpacity(0);
		}
	}
	
	@UiHandler("dragSection")
	void handleDragMouseDown(MouseDownEvent event)
	{
		GWT.log("Drag started");
		move = true;
		dragOffsetX = event.getRelativeX(frame.getElement());
		dragOffsetY = event.getRelativeY(frame.getElement());
	}
	
	@UiHandler("expandSection")
	void handleExpandMouseDown(MouseDownEvent event)
	{
		GWT.log("Drag started");
		resize = true;
		dragOffsetX = event.getRelativeX(frame.getElement());
		dragOffsetY = event.getRelativeY(frame.getElement());
	}
	
	void stopDrag()
	{
		move = false;
		resize = false;
	}
}