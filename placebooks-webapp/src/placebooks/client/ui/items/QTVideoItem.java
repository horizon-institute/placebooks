package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;

import com.google.gwt.user.client.ui.Widget;

public class QTVideoItem extends MediaItem
{
	private QTVideo video;

	QTVideoItem(final PlaceBookItemController controller)
	{
		super(controller);

		video = new QTVideo();

		initWidget(video);
	}

	@Override
	protected int getMediaHeight()
	{
		return 10;
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