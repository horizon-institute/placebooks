package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

public class WebBundleItem extends PlaceBookItemWidget
{
	private final Frame frame = new Frame("http://www.google.co.uk");
	private String url;

	WebBundleItem(final PlaceBookItem item)
	{
		super(item);
		initWidget(frame);
		frame.setWidth("100%");
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
	}

	@Override
	public void refresh()
	{
		if (url == null || !url.equals(getItem().getURL()))
		{
			url = getItem().getURL();
			frame.setUrl(url);
		}
		if (getItem().hasParameter("height"))
		{
			frame.setWidth("auto");
			frame.setHeight("100%");
		}
		else
		{
			frame.setWidth("100%");
			frame.setHeight("auto");
		}	
	}
}