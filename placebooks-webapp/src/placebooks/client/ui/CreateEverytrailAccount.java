package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateEverytrailAccount extends Composite
{

	interface CreateEverytrailAccountUiBinder extends UiBinder<Widget, CreateEverytrailAccount>
	{
	}

	private static CreateEverytrailAccountUiBinder uiBinder = GWT.create(CreateEverytrailAccountUiBinder.class);

	@UiField
	Button createAccount;

	@UiField
	PasswordTextBox password;

	@UiField
	TextBox username;

	private AbstractCallback callback;

	public CreateEverytrailAccount()
	{
		initWidget(uiBinder.createAndBindUi(this));
		createAccount.setEnabled(false);
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
	void checkValid(final KeyPressEvent event)
	{
		if (username.getText().trim().equals(""))
		{
			createAccount.setEnabled(false);
			return;
		}

		if (password.getText().trim().equals(""))
		{
			createAccount.setEnabled(false);
			return;
		}

		createAccount.setEnabled(true);
	}

	@UiHandler("createAccount")
	void createAccount(final ClickEvent event)
	{
		createAccount.setEnabled(false);
		PlaceBookService.linkAccount(username.getText(), password.getText(), "Everytrail", callback);
	}
}
