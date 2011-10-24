package placebooks.client.ui.elements;

import placebooks.client.resources.Resources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class PlaceBookToolbarItem extends FlowPanel implements HasClickHandlers, HasText
{
	private boolean enabled;
	private final Image image = new Image();
	private final InlineLabel label = new InlineLabel();

	PlaceBookToolbarItem()
	{
		image.setVisible(false);		
		add(image);
		add(label);
		setEnabled(true);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}	
	
	public void hideImage()
	{
		image.setVisible(false);
	}

	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
		if (enabled)
		{
			setStyleName(Resources.INSTANCE.style().toolbarItem());
		}
		else
		{
			setStyleName(Resources.INSTANCE.style().toolbarItemDisabled());
		}
	}

	public void setImage(final ImageResource imageResource)
	{
		if (imageResource != null)
		{
			image.setVisible(true);
			image.setResource(imageResource);
			image.getElement().getStyle().setMarginRight(5, Unit.PX);
			image.getElement().getStyle().setMarginBottom(-2, Unit.PX);
		}
		else
		{
			image.setVisible(false);
		}
	}

	@Override
	public void setText(final String text)
	{
		label.setText(text);
	}

	@Override
	public String getText()
	{
		return label.getText();
	}

}
