package placebooks.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.HyperlinkImpl;

public class ToolbarLink extends Widget implements HasText
{
	private static final HyperlinkImpl impl = GWT.create(HyperlinkImpl.class);

	private Image image = null;
	private String text = null;
	private String url = null;
	private boolean enabled = true;

	public ToolbarLink()
	{
		setElement(DOM.createDiv());
	}

	@Override
	public String getText()
	{
		return text;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void onBrowserEvent(final Event event)
	{
		super.onBrowserEvent(event);
		if (DOM.eventGetType(event) == Event.ONCLICK && impl.handleAsClick(event))
		{
			if(url.startsWith("#"))
			{
				History.newItem(url.substring(1));
				DOM.eventPreventDefault(event);
			}
		}
	}

	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
		update();
	}

	public void setResource(final ImageResource imageResource)
	{
		image = new Image(imageResource);
		update();
	}

	@Override
	public void setText(final String text)
	{
		this.text = text;
		update();
	}

	public void setURL(final String url)
	{
		this.url = url;
		update();
	}

	private String getInnerHTML()
	{
		if (image != null)
		{
			if (text != null)
			{
				return image.getElement().getString() + text;
			}
			else
			{
				return image.getElement().getString();
			}
		}
		else if (text != null) { return text; }
		return "";
	}

	private void update()
	{
		if (url != null && enabled)
		{
			getElement().setInnerHTML("<a href=\"" + url + "\">" + getInnerHTML() + "</a>");
		}
		else
		{
			getElement().setInnerHTML(getInnerHTML());
		}
	}
}