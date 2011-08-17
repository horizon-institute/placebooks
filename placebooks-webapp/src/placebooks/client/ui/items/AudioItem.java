package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;

public class AudioItem extends PlaceBookItemWidget
{
	private final Audio audio;
	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();
		}
	};
	private String url;

	AudioItem(final PlaceBookItem item)
	{
		super(item);
		audio = Audio.createIfSupported();
		audio.setControls(true);
		audio.setWidth("100%");
		audio.addCanPlayThroughHandler(new CanPlayThroughHandler()
		{
			@Override
			public void onCanPlayThrough(final CanPlayThroughEvent event)
			{
				fireResized();
			}
		});
		initWidget(audio);
	}

	@Override
	public void refresh()
	{
		if (getItem().hasParameter("height"))
		{
			audio.setHeight("100%");
		}
		else
		{
			audio.setHeight("auto");			
		}			
		if (url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			audio.setSrc(url);
			checkSize();
		}
	}

	private void checkSize()
	{
		if (audio.getOffsetHeight() == 0)
		{
			loadTimer.schedule(1000);
		}
		else
		{
			loadTimer.cancel();
			fireResized();
		}
	}
}
