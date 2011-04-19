package placebooks.client.ui.widget;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class EditablePanel extends HTMLPanel
{
	public EditablePanel(final SafeHtml safeHtml)
	{
		super(safeHtml);
		getElement().setAttribute("contentEditable", "true");
	}

	public EditablePanel(final String html)
	{
		super(html);
		getElement().setAttribute("contentEditable", "true");
		getElement().getStyle().setProperty("textAlign", "justify");
	}

	public EditablePanel(final String tag, final String html)
	{
		super(tag, html);
		getElement().setAttribute("contentEditable", "true");
	}
}
