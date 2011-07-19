package placebooks.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;

public class JavaScriptInjector
{
	private static HeadElement head;

	public static void add(final String url)
	{
		final HeadElement head = getHead();
		final ScriptElement element = createScriptElement();
		element.setSrc(url);
		head.appendChild(element);
	}

	public static void inject(final String javascript)
	{
		final HeadElement head = getHead();
		final ScriptElement element = createScriptElement();
		element.setText(javascript);
		head.appendChild(element);
	}

	private static ScriptElement createScriptElement()
	{
		final ScriptElement script = Document.get().createScriptElement();
		script.setAttribute("language", "javascript");
		return script;
	}

	private static HeadElement getHead()
	{
		if (head == null)
		{
			final Element element = Document.get().getElementsByTagName("head").getItem(0);
			assert element != null : "HTML Head element required";
			final HeadElement head = HeadElement.as(element);
			JavaScriptInjector.head = head;
		}
		return JavaScriptInjector.head;
	}
}
