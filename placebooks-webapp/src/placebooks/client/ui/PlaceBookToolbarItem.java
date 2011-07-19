package placebooks.client.ui;

import placebooks.client.resources.Resources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class PlaceBookToolbarItem extends FlowPanel
{
	private ClickHandler clickHandler;
	private boolean enabled;
	private final Image image = new Image();
	private final InlineLabel label = new InlineLabel();

	public PlaceBookToolbarItem(final String text, final ImageResource imageRes, final ClickHandler clickHandler)
	{
		this();
		setResource(imageRes);
		label.setText(text);
		setClickHandler(clickHandler);
	}

	PlaceBookToolbarItem()
	{
		addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(final ClickEvent event)
			{
				if (enabled && clickHandler != null)
				{
					clickHandler.onClick(event);
				}
			}
		}, ClickEvent.getType());

		add(image);
		add(label);
		setEnabled(true);
	}

	public void hideImage()
	{
		image.setVisible(false);
	}

	public void setClickHandler(final ClickHandler clickHandler)
	{
		this.clickHandler = clickHandler;
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

	public void setResource(final ImageResource imageResource)
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

	public void setText(final String text)
	{
		label.setText(text);
	}

}
