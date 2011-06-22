package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Login extends Composite
{

	private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class);

	interface LoginUiBinder extends UiBinder<Widget, Login>
	{
	}
	
	@UiField
	TextBox emailBox;
	
	@UiField
	PasswordTextBox passwordBox;
	
	private AbstractCallback callback;
	
	public Login(AbstractCallback callback)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.callback = callback;
	}
	
	@UiHandler("loginButton")
	void handleLogin(ClickEvent event)
	{
		PlaceBookService.login(emailBox.getText(), passwordBox.getText(), callback);
	}
}
