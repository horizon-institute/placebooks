package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Login extends Composite
{

	interface LoginUiBinder extends UiBinder<Widget, Login>
	{
	}

	private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class);

	@UiField
	TextBox emailBox;

	@UiField
	PasswordTextBox passwordBox;

	private AbstractCallback callback;

	public Login(final AbstractCallback callback)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.callback = callback;
	}

	@UiHandler("loginButton")
	void handleLogin(final ClickEvent event)
	{
		PlaceBookService.login(emailBox.getText(), passwordBox.getText(), callback);
	}

	@UiHandler("createAccount")
	void handlerCreateAccount(final ClickEvent event)
	{
		final PopupPanel dialogBox = new PopupPanel();
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		final CreateAccount account = new CreateAccount();
		account.setCallback(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				dialogBox.hide();
				PlaceBookService.login(account.getEmail(), account.getPassword(), callback);
			}
		});
		dialogBox.add(account);
		dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
		dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
		dialogBox.setAutoHideEnabled(true);

		dialogBox.center();
		dialogBox.show();
	}

	@UiHandler(value = { "emailBox", "passwordBox" })
	void handleReturn(final KeyUpEvent event)
	{
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			PlaceBookService.login(emailBox.getText(), passwordBox.getText(), callback);
		}
	}
}
