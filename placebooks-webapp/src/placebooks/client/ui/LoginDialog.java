package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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

	private AbstractCallback callback;

	public LoginDialog(String title, String submitText, String usernameText, String loginName, String passName)
	{
		initWidget(uiBinder.createAndBindUi(this));
		submit.setEnabled(false);
		submit.setText(submitText);
		titleLabel.setText(title);
		username.setName(loginName);
		password.setName(passName);
		usernameLabel.setText(usernameText);
	}

	public String getPassword()
	{
		return password.getText();
	}

	public String getUsername()
	{
		return username.getText();
	}

	public void setCallback(final AbstractCallback callback)
	{
		this.callback = callback;
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

	@UiHandler("submit")
	void createAccount(final ClickEvent event)
	{
		submit.setEnabled(false);
		PlaceBookService.linkAccount(username.getText(), password.getText(), "Everytrail", callback);
	}
}
