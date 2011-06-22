package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.LoginDetails;
import placebooks.client.model.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookUserDetails extends Composite
{

	private static PlaceBookUserDetailsUiBinder uiBinder = GWT.create(PlaceBookUserDetailsUiBinder.class);

	interface PlaceBookUserDetailsUiBinder extends UiBinder<Widget, PlaceBookUserDetails>
	{
	}

	@UiField
	Label userName;
	
	@UiField
	Anchor everytrail;
	
	private AbstractCallback logoutCallback;
	
	public PlaceBookUserDetails(User user, AbstractCallback logoutCallback)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		this.logoutCallback = logoutCallback;
		
		userName.setText("Logged is as " + user.getName());
		
		for(int index = 0; index < user.getLoginDetails().length(); index++)
		{
			LoginDetails loginDetails = user.getLoginDetails().get(index);
			if(loginDetails.getService().equals("everytrails"))
			{
				everytrail.getElement().getStyle().setDisplay(Display.NONE);
			}
		}
	}
	
	@UiHandler("logout")
	void logout(ClickEvent event)
	{
		PlaceBookService.logout(logoutCallback);
	}
}
