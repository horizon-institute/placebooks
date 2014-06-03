package org.placebooks.client.ui.widgets;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class AndroidLink extends Composite
{
	private final HTML html;

	public AndroidLink()
	{
		html = new HTML();
		initWidget(html);
	}

	public void setPackage(final String packageName, final String url)
	{
		if(Window.Navigator.getUserAgent().toLowerCase().contains("android"))
		{
			String uri = url;
			if(uri.startsWith("http://"))
			{
				uri = url.substring("http://".length());
			}
			html.setHTML("<a href=\"intent://"+uri+"#Intent;scheme=org;package=" + packageName + ";end\"><img alt=\"Android app on Google Play\" height=\"35\" src=\"https://developer.android.com/images/brand/en_app_rgb_wo_45.png\" /></a>");
		}
		else
		{
			html.setHTML("<a href=\"https://play.google.com/store/apps/details?id=" + packageName + "\"><img alt=\"Android app on Google Play\" height=\"35\" src=\"https://developer.android.com/images/brand/en_app_rgb_wo_45.png\" /></a>");
		}
	}
}
