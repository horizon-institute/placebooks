package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.user.client.ui.Widget;

public class QTVideoItem extends MediaItem
{
	private QTVideo video;
	
	QTVideoItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);		
			
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
	protected void setURL(String url)
	{
		video.setSrc(url);		
	}
}