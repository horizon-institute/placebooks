package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.Widget;

public class AudioItem extends MediaItem
{
	private final Audio audio;

	AudioItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);
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

	// @Override
	// public void refresh()
	// {
	// if (getItem().hasParameter("height"))
	// {
	// audio.setHeight("100%");
	// }
	// else
	// {
	// audio.getElement().getStyle().clearHeight();
	// }
	// if (url == null || !url.equals(getItem().getURL()))
	// {
	// url = getItem().getURL();
	// audio.setSrc(url);
	// checkSize();
	// }
	// }

	@Override
	protected int getMediaHeight()
	{
		return audio.getOffsetHeight();
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