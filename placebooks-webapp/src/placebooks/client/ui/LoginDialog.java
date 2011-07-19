package placebooks.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginDialog extends Composite
{

	interface LoginDialogUiBinder extends UiBinder<Widget, LoginDialog>
	{
	}

	private static LoginDialogUiBinder uiBinder = GWT.create(LoginDialogUiBinder.class);

	@UiField
	Label titleLabel;
	
	@UiField
	Button submit;

	@UiField
	PasswordTextBox password;

	@UiField
	TextBox username;
	
	@UiField
	Label usernameLabel;

	public LoginDialog(String title, String submitText, String usernameText)
	{
		initWidget(uiBinder.createAndBindUi(this));
		submit.setEnabled(false);
		submit.setText(submitText);
		titleLabel.setText(title);
		usernameLabel.setText(usernameText);
	}
	
	public void addClickHandler(final ClickHandler clickHandler)
	{
		submit.addClickHandler(clickHandler);
	}

	public String getPassword()
	{
		return password.getText();
	}

	public String getUsername()
	{
		return username.getText();
	}

	public void focus()
	{
		username.setFocus(true);
	}
	
	@UiHandler("password")
	void submitOnReturn(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			submit.click();
		}
	}
	
	@UiHandler(value = { "username", "password" })
	void checkValid(final KeyUpEvent event)
	{
		if (username.getText().trim().equals(""))
		{
			submit.setEnabled(false);
			return;
		}

		if (password.getText().trim().equals(""))
		{
			submit.setEnabled(false);
			return;
		}

		submit.setEnabled(true);
	}
//
//	@UiHandler("submit")
//	void createAccount(final ClickEvent event)
//	{
//		submit.setEnabled(false);
//
//	}
}