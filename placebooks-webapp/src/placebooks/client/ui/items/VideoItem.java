package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.Timer;

public class VideoItem extends PlaceBookItemWidget
{
	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();
		}
	};
	private String url;
	private final Video video;
	
	VideoItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);

		video = Video.createIfSupported();
		video.setControls(true);
		video.setWidth("100%");

		initWidget(video);
	}

	private void checkSize()
	{
		if (video.getVideoHeight() == 0)
		{
			loadTimer.schedule(1000);
		}
		else
		{
			loadTimer.cancel();
			fireResized();
		}
	}

	@Override
	public void refresh()
	{
		if (getItem().hasParameter("height"))
		{
			video.setHeight("100%");
		}
		else
		{
			video.setHeight("auto");
		}
		if (url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			video.setSrc(url);
			checkSize();
		}
	}
}
