package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Frame;

public class WebBundleItem extends PlaceBookItemWidget
{
	private final Frame frame = new Frame("http://www.google.co.uk");
	private String url;

	WebBundleItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);
		initWidget(frame);
		frame.setWidth("100%");
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
		frame.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				GWT.log("Loaded: " + frame.getElement());			
				GWT.log("Loaded: " + getContentDocument(frame.getElement()));
				GWT.log("Loaded: " + getURL(frame.getElement()));
				GWT.log("Loaded: " + getURL2(frame.getElement()));				
				GWT.log("Loaded: " + frame.getElement().getPropertyJSO("contentDocument"));
				GWT.log("Loaded: " + frame.getElement().getPropertyJSO("contentWindow"));
			}
		});
	}

	private final native String getURL2(Element element)
	/*-{
		return element.contentDocument.url;
	}-*/;	
	
	
	private final native String getURL(Element element)
	/*-{
		return element.contentWindow.location.href;
	}-*/;	
	
	private final native Document getContentDocument(Element element)
	/*-{
		return element.contentDocument;
	}-*/;

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
