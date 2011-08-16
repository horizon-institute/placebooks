package placebooks.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
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
	PasswordTextBox password;

	@UiField
	Button submit;

	@UiField
	Label titleLabel;
	
	@UiField
	Label errorLabel;

	@UiField
	TextBox username;

	@UiField
	Label usernameLabel;

	public LoginDialog(final String title, final String submitText, final String usernameText)
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

	public void focus()
	{
		username.setFocus(true);
	}

	public String getPassword()
	{
		return password.getText();
	}
	
	public void setUsername(String username)
	{
		this.username.setText(username);
	}
	
	public void setErrorText(String error)
	{
		errorLabel.setText(error);
		errorLabel.getElement().getStyle().setDisplay(Display.BLOCK);
	}
	
	public String getUsername()
	{
		return username.getText();
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
	// @UiHandler("submit")
	// void createAccount(final ClickEvent event)
	// {
	// submit.setEnabled(false);
	//
	// }

	@UiHandler("password")
	void submitOnReturn(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			submit.click();
		}
	}
}