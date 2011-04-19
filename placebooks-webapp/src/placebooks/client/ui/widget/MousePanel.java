package placebooks.client.ui.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class MousePanel extends ComplexPanel implements HasMouseOverHandlers, HasMouseMoveHandlers, HasClickHandlers,
		HasMouseDownHandlers, HasMouseUpHandlers, HasMouseOutHandlers
{
	public MousePanel()
	{
		setElement(DOM.createDiv());
	}

	@Override
	public void add(final Widget widget)
	{
		add(widget, getElement());
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler)
	{
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(final MouseMoveHandler handler)
	{
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(final MouseUpHandler handler)
	{
		return addDomHandler(handler, MouseUpEvent.getType());
	}
}
