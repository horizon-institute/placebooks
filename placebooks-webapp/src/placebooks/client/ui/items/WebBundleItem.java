package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.logger.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Frame;

public class WebBundleItem extends PlaceBookItemView
{
	private final Frame frame = new Frame("http://www.google.co.uk");
	private String url;

	WebBundleItem(final PlaceBookItemController controller)
	{
		super(controller);
		initWidget(frame);
		frame.setWidth("100%");
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
		frame.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(final LoadEvent event)
			{
				Log.info("Loaded: " + frame.getElement());
				Log.info("Loaded: " + getContentDocument(frame.getElement()));
				Log.info("Loaded: " + getURL(frame.getElement()));
				Log.info("Loaded: " + getURL2(frame.getElement()));
				Log.info("Loaded: " + frame.getElement().getPropertyJSO("contentDocument"));
				Log.info("Loaded: " + frame.getElement().getPropertyJSO("contentWindow"));
			}
		});
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

	private final native Document getContentDocument(Element element)
	/*-{
		return element.contentDocument;
	}-*/;

	private final native String getURL(Element element)
	/*-{
		return element.contentWindow.location.href;
	}-*/;

	private final native String getURL2(Element element)
	/*-{
		return element.contentDocument.url;
	}-*/;
}
