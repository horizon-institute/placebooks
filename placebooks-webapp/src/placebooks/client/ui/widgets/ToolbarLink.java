package placebooks.client.ui.widgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ToolbarLink extends Widget implements HasText
{
	private Image image = null;
	private String text = null;
	private String url = null;
	private boolean enabled = true;
	
	public ToolbarLink()
	{
		setElement(DOM.createDiv());
	}

	public void setResource(ImageResource imageResource)
	{
		this.image = new Image(imageResource);
		update();
	}
	
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
		update();
		if (enabled)
		{
			//setStylePrimaryName(style.enabled());
		}
		else
		{
			//setStylePrimaryName(style.disabled());
		}
	}
	
	public void setURL(String url)
	{
		this.url = url;
		update();
	}
	
	public void setText(String text)
	{
		this.text = text;
		update();
	}
	
	private String getInnerHTML()
	{
		if(image != null)
		{
			if(text != null)
			{
				return image.getElement().getString() + text;
			}
			else
			{
				return image.getElement().getString();
			}
		}
		else if(text != null)
		{
			return text;
		}
		return "";
	}
	
	private void update()
	{
		if(url != null && enabled)
		{
			getElement().setInnerHTML("<a href=\"" + url + "\">" + getInnerHTML() + "</a>");			
		}
		else
		{
			getElement().setInnerHTML(getInnerHTML());			
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public String getText()
	{
		return text;
	}
}