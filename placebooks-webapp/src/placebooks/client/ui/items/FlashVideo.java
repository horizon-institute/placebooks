package placebooks.client.ui.items;

import placebooks.client.PlaceBooks;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class FlashVideo extends Widget
{
	private static void addParam(final Element element, final String name, final String value)
	{
		final Element param = DOM.createElement("param");
		param.setAttribute("name", name);
		param.setAttribute("value", value);

		element.appendChild(param);
	}

	public FlashVideo()
	{
		final Element element = DOM.createElement("object");
		setElement(element);
	}

	public void setSrc(final String url)
	{
		getElement().setAttribute("type", "application/x-shockwave-flash");
		getElement().setAttribute("data", PlaceBooks.getServer().getHostURL() + "FLVPlayer.swf");

		// addParam(getElement(), "movie", PlaceBookService.getHostURL() + "flowplayer-3.2.7.swf");
		addParam(getElement(), "allowScriptAccess", "always");
		addParam(getElement(), "wmode", "opaque");
		addParam(getElement(), "bgcolor", "#000000");

		addParam(getElement(), "flashvars", "videoURL=" + url);

	}
}
