package placebooks.client.ui.elements;

import placebooks.client.JavaScriptInjector;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class GooglePlusOne extends Composite
{
	private final HTML html;

	public GooglePlusOne()
	{
		html = new HTML();
		initWidget(html);
	}

	public void setURL(final String url)
	{
		html.setHTML("<g:plusone size=\"medium\" annotation=\"bubble\" href=\"" + url + "\"></g:plusone>");

		JavaScriptInjector.add("https://apis.google.com/js/plusone.js");
	}
}
