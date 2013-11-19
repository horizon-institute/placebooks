package org.placebooks.client.ui.items;

import org.placebooks.client.controllers.ItemController;

import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Widget;

public class VideoItem extends MediaItem
{
	private final Video video;

	VideoItem(final ItemController controller)
	{
		super(controller);

		video = Video.createIfSupported();
		video.setControls(true);
	}

	@Override
	protected int getMediaHeight()
	{
		return video.getVideoHeight();
	}

	@Override
	protected Widget getMediaWidget()
	{
		return video;
	}

	@Override
	protected void setURL(final String url)
	{
		video.setSrc(url);
	}
}
