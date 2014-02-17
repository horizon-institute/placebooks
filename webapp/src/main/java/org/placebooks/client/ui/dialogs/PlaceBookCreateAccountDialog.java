package org.placebooks.client.ui.dialogs;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;
import org.wornchaos.client.server.AsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookCreateAccountDialog extends PlaceBookDialog
{
	interface CreateAccountUiBinder extends UiBinder<Widget, PlaceBookCreateAccountDialog>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	@UiField
	Button createAccount;

	@UiField
	TextBox email;

	@UiField
	TextBox name;

	@UiField
	PasswordTextBox password;

	@UiField
	PasswordTextBox passwordConfirm;

	private AsyncCallback<Shelf> callback;

	public PlaceBookCreateAccountDialog()
	{
		setTitle(uiMessages.createAccount());
		setWidget(uiBinder.createAndBindUi(this));
		createAccount.setEnabled(false);
	}

	public String getEmail()
	{
		return email.getText();
	}

	public String getPassword()
	{
		return password.getText();
	}

	public void setCallback(final AsyncCallback<Shelf> callback)
	{
		this.callback = callback;
	}

	@UiHandler(value = { "name", "email", "password", "passwordConfirm" })
	void checkValid(final KeyUpEvent event)
	{
		if (name.getText().trim().equals(""))
		{
			createAccount.setEnabled(false);
			return;
		}

		if (email.getText().trim().equals(""))
		{
			createAccount.setEnabled(false);
			return;
		}

		if (password.getText().trim().equals(""))
		{
			createAccount.setEnabled(false);
			return;
		}

		if (!password.getText().equals(passwordConfirm.getText()))
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
		PlaceBooks.getServer().registerAccount(name.getText(), email.getText(), password.getText(), callback);
	}
}
