package placebooks.client.ui.elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookToolbarItem extends Composite implements HasClickHandlers, HasMouseOverHandlers, HasMouseOutHandlers, HasHTML
{
	interface PlaceBookToolbarItemUiBinder extends UiBinder<Widget, PlaceBookToolbarItem>
	{
	}

	private static final PlaceBookToolbarItemUiBinder uiBinder = GWT.create(PlaceBookToolbarItemUiBinder.class);

	interface Style extends CssResource
	{
		String enabled();
		String disabled();
	}

	private boolean enabled;

	@UiField
	Image image;
	@UiField
	HTML label;
	
	@UiField
	Style style;

	PlaceBookToolbarItem()
	{
		initWidget(uiBinder.createAndBindUi(this));
		image.setVisible(false);
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
			setStylePrimaryName(style.enabled());
		}
		else
		{
			setStylePrimaryName(style.disabled());
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

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public String getHTML()
	{
		return label.getHTML();
	}

	@Override
	public void setHTML(String html)
	{
		label.setHTML(html);		
	}
}