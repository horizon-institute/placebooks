package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.Timer;

public class VideoItem extends PlaceBookItemWidget
{
	private final Video video;
	private String url;
	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();			
		}
	};	

	VideoItem(PlaceBookItem item)
	{
		super(item);
		video = Video.createIfSupported();
		video.setControls(true);
		video.setWidth("100%");
	
		initWidget(video);
	}
	
	private void checkSize()
	{
		if(video.getVideoHeight() == 0)
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
		if(url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			video.setSrc(url);
			checkSize();
		}
	}
}
