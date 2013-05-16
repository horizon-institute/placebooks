package placebooks.client.ui.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FormInputPanel extends Composite implements HasText
{

	interface FormElementUiBinder extends UiBinder<Widget, FormInputPanel>
	{
	}

	private static FormElementUiBinder uiBinder = GWT.create(FormElementUiBinder.class);

	@UiField
	Label label;

	@UiField
	TextBox input;

	public FormInputPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public FormInputPanel(final String firstName)
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText()
	{
		return label.getText();
	}

	@Override
	public void setText(final String text)
	{
		label.setText(text);
	}

	@UiChild(tagname = "widget")
	void addWidget(final Widget widget, final String name)
	{
	}
}
