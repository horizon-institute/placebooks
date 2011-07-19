package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;

public class AudioItem extends PlaceBookItemWidget
{
	private final Audio audio;
	private String url;
	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();			
		}
	};	
	
	AudioItem(PlaceBookItem item)
	{
		super(item);
		audio = Audio.createIfSupported();
		audio.setControls(true);
		audio.setWidth("100%");		
		audio.addCanPlayThroughHandler(new CanPlayThroughHandler()
		{
			@Override
			public void onCanPlayThrough(CanPlayThroughEvent event)
			{
				fireResized();				
			}
		});
		initWidget(audio);
	}	
	

	private void checkSize()
	{
		if(audio.getOffsetHeight() == 0)
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
			audio.setSrc(url);
			checkSize();
		}
	}
}
