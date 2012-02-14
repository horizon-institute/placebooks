package placebooks.client.ui.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLoginDialog extends PlaceBookDialog
{

	interface LoginDialogUiBinder extends UiBinder<Widget, PlaceBookLoginDialog>
	{
	}

	private static LoginDialogUiBinder uiBinder = GWT.create(LoginDialogUiBinder.class);

	@UiField
	PasswordTextBox password;

	@UiField
	Button submit;

	@UiField
	TextBox username;

	@UiField
	Label usernameLabel;

	@UiField
	Widget progress;
	
	public PlaceBookLoginDialog(final String title, final String submitText, final String usernameText)
	{
		setWidget(uiBinder.createAndBindUi(this));
		submit.setEnabled(false);
		submit.setText(submitText);
		setTitle(title);
		usernameLabel.setText(usernameText);
	}

	public void addClickHandler(final ClickHandler clickHandler)
	{
		submit.addClickHandler(clickHandler);
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
	
	public void setProgress(boolean showProgress)
	{
		username.setEnabled(!showProgress);
		password.setEnabled(!showProgress);
		if(!showProgress)
		{
			checkValid(null);
		}
		else
		{
			submit.setEnabled(false);
		}
		progress.setVisible(showProgress);
		setAutoHide(!showProgress);
	}
	
	public void focus()
	{
		username.setFocus(true);
	}

	public String getPassword()
	{
		return password.getText();
	}

	public String getUsername()
	{
		return username.getText();
	}

	public void setUsername(final String username)
	{
		this.username.setText(username);
	}

	@UiHandler("password")
	void submitOnReturn(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			submit.click();
		}
	}
}