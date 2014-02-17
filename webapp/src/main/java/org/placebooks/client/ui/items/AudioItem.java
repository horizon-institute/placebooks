package org.placebooks.client.ui.items;

import org.placebooks.client.controllers.ItemController;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.Widget;

public class AudioItem extends MediaItem
{
	private final Audio audio;

	AudioItem(final ItemController controller)
	{
		super(controller);
		audio = Audio.createIfSupported();
		audio.setControls(true);
		audio.addCanPlayThroughHandler(new CanPlayThroughHandler()
		{
			@Override
			public void onCanPlayThrough(final CanPlayThroughEvent event)
			{
				fireResized();
			}
		});
	}

	@Override
	protected void checkHeightParam()
	{
		if (getItem().showMarker())
		{
			final Style style = getMediaWidget().getElement().getStyle();
			style.setPosition(Position.RELATIVE);
			style.setLeft(ItemController.getMarkerImage(getItem()).getWidth() + 3, Unit.PX);
			style.setRight(0, Unit.PX);
		}
		else
		{
			getMediaWidget().setWidth("100%");
			getMediaWidget().getElement().getStyle().clearPosition();
		}

		if (getItem().getParameters().containsKey("height"))
		{
			getMediaWidget().setHeight("100%");
		}
	}

	@Override
	protected int getMediaHeight()
	{
		return -1;
	}

	@Override
	protected Widget getMediaWidget()
	{
		return audio;
	}

	@Override
	protected void setURL(final String url)
	{
		audio.setSrc(url);
	}
}