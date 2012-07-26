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

public class FormInput extends Composite implements HasText
{

	interface FormElementUiBinder extends UiBinder<Widget, FormInput>
	{
	}

	private static FormElementUiBinder uiBinder = GWT.create(FormElementUiBinder.class);

	@UiField
	Label label;
	
	@UiField
	TextBox input;
	
	public FormInput()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		input.getElement().setAttribute("spellcheck", "false");
	}

	@UiChild(tagname = "widget")
	void addWidget(final Widget widget, final String name)
	{
	}

	@Override
	public String getText()
	{
		return label.getText();
	}

	@Override
	public void setText(String text)
	{
		label.setText(text);		
	}
}
