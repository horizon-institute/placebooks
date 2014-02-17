package org.placebooks.client;


public class GoogleUniversalAnalytics
{
	public GoogleUniversalAnalytics(final String userAccount)
	{
		init(userAccount);
	}

	public native void trackPageview()
	/*-{
		$wnd.ga('send', 'pageview');
	}-*/;

	public native void trackPageview(String pageName)
	/*-{
		if (!pageName.match("^/") == "/")
		{
			pageName = "/" + pageName;
		}
		$wnd.ga('send', 'pageview', pageName);
	}-*/;


	private void init(final String userAccount)
	{
//		final Element firstScript = Document.get().getElementsByTagName("script").getItem(0);
//
//		final ScriptElement config = Document.get()
//				.createScriptElement("(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)})(window,document,'script','//www.google-analytics.com/analytics.js','ga'); \n\n  ga('create', '" + userAccount + "', 'nott.ac.uk');");
//
//		firstScript.getParentNode().insertBefore(config, firstScript);
	}
}
