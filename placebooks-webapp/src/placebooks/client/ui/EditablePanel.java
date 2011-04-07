package placebooks.client.ui;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class EditablePanel extends HTMLPanel
{
	public EditablePanel(SafeHtml safeHtml)
	{
		super(safeHtml);
		getElement().setAttribute("contentEditable", "true");
	}

	public EditablePanel(String tag, String html)
	{
		super(tag, html);
		getElement().setAttribute("contentEditable", "true");
	}

	public EditablePanel(String html)
	{
		super(html);
		getElement().setAttribute("contentEditable", "true");
	}	
}
