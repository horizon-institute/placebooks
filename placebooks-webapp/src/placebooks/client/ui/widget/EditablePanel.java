package placebooks.client.ui.widget;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class EditablePanel extends HTMLPanel implements HasKeyUpHandlers, HasFocusHandlers, HasBlurHandlers
{
	public EditablePanel(final SafeHtml safeHtml)
	{
		super(safeHtml);
		getElement().setAttribute("contentEditable", "true");
	}

	public EditablePanel(final String html)
	{
		super(html);
		getElement().setAttribute("contentEditable", "true");
		getElement().getStyle().setProperty("textAlign", "justify");
	}

	public EditablePanel(final String tag, final String html)
	{
		super(tag, html);
		getElement().setAttribute("contentEditable", "true");
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
}
