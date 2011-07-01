package placebooks.client.ui.items;

import com.google.gwt.media.client.Audio;

import placebooks.client.model.PlaceBookItem;

public class AudioItem extends PlaceBookItemWidget
{
	private final Audio audio;

	AudioItem(PlaceBookItem item)
	{
		super(item);
		audio = Audio.createIfSupported();
		audio.setControls(true);
		audio.setWidth("100%");		
		initWidget(audio);
	}

	@Override
	public void refresh()
	{
		audio.setSrc(getItem().getURL());
	}
}
