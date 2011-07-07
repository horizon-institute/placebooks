package placebooks.client.ui.items;

import com.google.gwt.user.client.ui.Frame;

import placebooks.client.model.PlaceBookItem;

public class WebBundleItem extends PlaceBookItemWidget
{
	private final Frame frame = new Frame("http://www.google.co.uk");
	private String url;	
	
	WebBundleItem(PlaceBookItem item)
	{
		super(item);
		initWidget(frame);
	}

	@Override
	public void refresh()
	{
		if(url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			frame.setUrl(url);
		}
	}
}