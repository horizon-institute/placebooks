package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
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

	//private PlaceBookItem item;

	@UiField
	Panel widgetPanel;

	public PlaceBookItemFrame(final PlaceBookItem item, final Widget widget)
	{
		//this.item = item;
		initWidget(uiBinder.createAndBindUi(this));
		widgetPanel.add(widget);
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setTop(0, Unit.PX);
		widget.getElement().getStyle().setBottom(0, Unit.PX);
		widget.getElement().getStyle().setLeft(0, Unit.PX);
		widget.getElement().getStyle().setRight(0, Unit.PX);	
	}

	boolean acceptMouseMove()
	{
		return move || resize;
	}

	@UiHandler("dragSection")
	void handleDragMouseDown(final MouseDownEvent event)
	{
		GWT.log("Drag started");
		move = true;
		dragOffsetX = event.getRelativeX(frame.getElement());
		dragOffsetY = event.getRelativeY(frame.getElement());
	}

	@UiHandler("expandSection")
	void handleExpandMouseDown(final MouseDownEvent event)
	{
		GWT.log("Drag started");
		resize = true;
		dragOffsetX = frame.getAbsoluteLeft() - (frame.getOffsetWidth() - event.getRelativeX(frame.getElement()));
		dragOffsetY = frame.getAbsoluteTop() - (frame.getOffsetHeight() - event.getRelativeY(frame.getElement()));
	}

	void handleMouseMove(final int x, final int y)
	{
		if (move)
		{
			frame.getElement().getStyle().setLeft(x - dragOffsetX, Unit.PX);
			frame.getElement().getStyle().setTop(y - dragOffsetY, Unit.PX);
		}
		else if (resize)
		{
			frame.getElement().getStyle().setWidth(x - dragOffsetX, Unit.PX);
			frame.getElement().getStyle().setHeight(y - dragOffsetY, Unit.PX);
		}
	}

	@UiHandler("frame")
	void handleMouseOut(final MouseOutEvent event)
	{
		GWT.log("Mouse Out");
		if (!resize)
		{
			frame.getElement().getStyle().setZIndex(0);			
			menuButton.getElement().getStyle().setOpacity(0);
			borderSection.getElement().getStyle().setOpacity(0);
			expandSection.getElement().getStyle().setOpacity(0);
			dragSection.getElement().getStyle().setOpacity(0);
		}
	}

	@UiHandler("frame")
	void handleMouseOver(final MouseOverEvent event)
	{
		GWT.log("Mouse Over");
		frame.getElement().getStyle().setZIndex(2);
		menuButton.getElement().getStyle().setOpacity(1);
		borderSection.getElement().getStyle().setOpacity(1);
		expandSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setOpacity(1);
	}

	void stopDrag()
	{
		move = false;
		resize = false;
	}
}