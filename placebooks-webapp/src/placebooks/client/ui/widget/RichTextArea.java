package placebooks.client.ui.widget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class RichTextArea extends Widget implements HasKeyPressHandlers, HasKeyUpHandlers, HasFocusHandlers, HasBlurHandlers
{
	public RichTextArea()
	{
		super();
		final Element div = DOM.createDiv();
		setElement(div);
		div.setAttribute("contentEditable", "true");
		//div.getStyle().setProperty("textAlign", "justify");
		sinkEvents(Event.ONPASTE);
	}

	public RichTextArea(final String html)
	{
		this();
		getElement().setInnerHTML(html);
	}

	@Override
	public HandlerRegistration addBlurHandler(final BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(final FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler keyUpHandler)
	{
		return addDomHandler(keyUpHandler, KeyUpEvent.getType());
	}
	
	@Override
	public HandlerRegistration addKeyPressHandler(final KeyPressHandler keyPressHandler)
	{
		return addDomHandler(keyPressHandler, KeyPressEvent.getType());
	}	
}
