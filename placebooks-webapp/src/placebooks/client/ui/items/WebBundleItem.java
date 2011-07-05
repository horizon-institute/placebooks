package placebooks.client.ui.items;

import com.google.gwt.user.client.ui.Frame;

import placebooks.client.model.PlaceBookItem;

public class WebBundleItem extends PlaceBookItemWidget
{
	Frame frame = new Frame("http://www.google.co.uk");
	
	WebBundleItem(PlaceBookItem item)
	{
		super(item);
		initWidget(frame);
	}

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub

	}

}
