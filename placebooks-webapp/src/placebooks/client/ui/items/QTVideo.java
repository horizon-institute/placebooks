package placebooks.client.ui.items;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class QTVideo extends Widget
{
	public QTVideo()
	{
		final Element element = DOM.createElement("embed");
		setElement(element);
	}
	
	public void setSrc(final String url)
	{
//		getElement().setAttribute("codebase", "http://www.apple.com/qtactivex/qtplugin.cab#version=7,3,0,0");
//		getElement().setAttribute("classid", "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B");
//
//		addParam(getElement(), "src", url);
//		addParam(getElement(), "allowScriptAccess", "always");
//		addParam(getElement(), "wmode", "opaque");		
//		addParam(getElement(), "bgcolor", "#000000");

//		Element embed = DOM.createElement("embed");

		getElement().setAttribute("src", url);
		getElement().setAttribute("type", "video/quicktime");
		getElement().setAttribute("wmode", "opaque");
		
//		addParam(getElement(), "showlogo", "false");
//		addParam(getElement(), "scale", "tofit");						
	}

//
//	private static void addParam(final Element element, final String name, final String value)
//	{
//		final Element param = DOM.createElement("param");
//		param.setAttribute("name", name);
//		param.setAttribute("value", value);
//		
//		element.appendChild(param);
//	}
}
