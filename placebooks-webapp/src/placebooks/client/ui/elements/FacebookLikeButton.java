package placebooks.client.ui.elements;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

public class FacebookLikeButton extends Composite
{
	private final Frame frame;

	public FacebookLikeButton()
	{
		frame = new Frame();
		initWidget(frame);
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
		frame.setHeight("20px");
		frame.setWidth("90px");
	}

	public void setURL(final String url)
	{
		frame.setUrl("http://www.facebook.com/plugins/like.php?href=" + URL.encodeQueryString(url)
				+ "&layout=button_count");
	}
}
