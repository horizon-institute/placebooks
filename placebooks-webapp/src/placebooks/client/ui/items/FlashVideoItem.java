package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;

import com.google.gwt.user.client.ui.Widget;

public class FlashVideoItem extends MediaItem
{
	private FlashVideo video;

	FlashVideoItem(final PlaceBookItemController controller)
	{
		super(controller);

		video = new FlashVideo();

		initWidget(video);
	}

	@Override
	protected int getMediaHeight()
	{
		return 1;
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