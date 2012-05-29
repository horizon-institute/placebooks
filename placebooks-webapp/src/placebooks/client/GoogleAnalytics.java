package placebooks.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Window;

public class GoogleAnalytics
{
	public GoogleAnalytics(final String userAccount)
	{
		init(userAccount);
	}
	
	public final native void trackPage()
	/*-{
		$wnd._gaq.push(['_trackPageview', $wnd.location.pathname + $wnd.location.search + $wnd.location.hash]);
	}-*/;

	private final native void trackPage(final String page)
	/*-{
		$wnd._gaq.push(['_trackPageview', page]);
	}-*/;

	public native void addAccount(String trackerName, String userAccount) /*-{
																			$wnd._gaq.push([ '" + trackerName + "._setAccount', '" + userAccount + "' ]);
																			}-*/;

	private void init(final String userAccount)
	{
		final Element firstScript = Document.get().getElementsByTagName("script").getItem(0);

		final ScriptElement config = Document.get()
				.createScriptElement(	"var _gaq = _gaq || [];_gaq.push(['_setAccount', '" + userAccount
												+ "']);");

		firstScript.getParentNode().insertBefore(config, firstScript);

		final ScriptElement script = Document.get().createScriptElement();

		// Add the google analytics script.
		script.setSrc(("https:".equals(Window.Location.getProtocol()) ? "https://ssl" : "http://www")
				+ ".google-analytics.com/ga.js");
		script.setType("text/javascript");
		script.setAttribute("async", "true");

		firstScript.getParentNode().insertBefore(script, firstScript);
	}

	public native void trackEvent(String category, String action) /*-{
																	$wnd._gaq.push([ '_trackEvent', category, action ]);
																	}-*/;

	public native void trackEvent(String category, String action, String optLabel) /*-{
																					$wnd._gaq.push([ '_trackEvent', category, action, optLabel ]);
																					}-*/;

	public native void trackEvent(String category, String action, String optLabel, int optValue) /*-{
																									$wnd._gaq.push([ '_trackEvent', category, action, optLabel, optValue ]);
																									}-*/;

	public native void trackEvent(String category, String action, String optLabel, int optValue,
			boolean optNonInteraction) /*-{
										$wnd._gaq.push([ '_trackEvent', category, action, optLabel, optValue, optNonInteraction ]);
										}-*/;

	public native void trackEventWithTracker(String trackerName, String category, String action) /*-{
																									$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action ]);
																									}-*/;

	public native void trackEventWithTracker(String trackerName, String category, String action, String optLabel) /*-{
																													$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action, optLabel ]);
																													}-*/;

	public native void trackEventWithTracker(String trackerName, String category, String action, String optLabel,
			int optValue) /*-{
							$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action, optLabel, optValue ]);
							}-*/;

	public native void trackEventWithTracker(String trackerName, String category, String action, String optLabel,
			int optValue, boolean optNonInteraction) /*-{
														$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action, optLabel, optValue, optNonInteraction ]);
														}-*/;

	public native void trackPageview() /*-{
										$wnd._gaq.push([ '_trackPageview' ]);
										}-*/;

	public native void trackPageview(String pageName) /*-{
														if (!pageName.match("^/") == "/") {
														pageName = "/" + pageName;
														}

														$wnd._gaq.push([ '_trackPageview', pageName ]);
														}-*/;

	public native void trackPageview(String trackerName, String pageName) /*-{
																			if (!pageName.match("^/") == "/") {
																			pageName = "/" + pageName;
																			}

																			$wnd._gaq.push([ '" + trackerName + "._trackPageview', pageName ]);
																			}-*/;
}
