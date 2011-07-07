package placebooks.client.ui.items;

import com.google.gwt.media.client.Video;

import placebooks.client.model.PlaceBookItem;

public class VideoItem extends PlaceBookItemWidget
{
	private final Video video;
	private String url;

	VideoItem(PlaceBookItem item)
	{
		super(item);
		video = Video.createIfSupported();
		video.setControls(true);
		video.setWidth("100%");
		initWidget(video);
	}

	@Override
	public void refresh()
	{
		if(url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			video.setSrc(url);
		}
	}
}
