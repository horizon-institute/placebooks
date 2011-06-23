package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.LoginDetails;
import placebooks.client.model.User;
import placebooks.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookUserDetails extends Composite
{

	interface PlaceBookUserDetailsUiBinder extends UiBinder<Widget, PlaceBookUserDetails>
	{
	}

	private static PlaceBookUserDetailsUiBinder uiBinder = GWT.create(PlaceBookUserDetailsUiBinder.class);

	@UiField
	Anchor everytrail;

	@UiField
	Panel syncPanel;

	@UiField
	Label userName;

	private AbstractCallback logoutCallback;

	public PlaceBookUserDetails(final User user, final AbstractCallback logoutCallback)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.logoutCallback = logoutCallback;

		userName.setText("Logged is as " + user.getName());

		syncPanel.getElement().getStyle().setDisplay(Display.NONE);

		for (int index = 0; index < user.getLoginDetails().length(); index++)
		{
			final LoginDetails loginDetails = user.getLoginDetails().get(index);
			if (loginDetails.getService().equals("Everytrail"))
			{
				hasEverytrail();
				break;
			}
		}
	}

	@UiHandler("everytrail")
	void handlerLinkEverytrail(final ClickEvent event)
	{
		final PopupPanel dialogBox = new PopupPanel();
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		final CreateEverytrailAccount account = new CreateEverytrailAccount();
		account.setCallback(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				dialogBox.hide();
				hasEverytrail();
			}
		});
		dialogBox.add(account);
		dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
		dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
		dialogBox.setAutoHideEnabled(true);

		dialogBox.center();
		dialogBox.show();
	}

	@UiHandler("logout")
	void logout(final ClickEvent event)
	{
		PlaceBookService.logout(logoutCallback);
	}

	private void hasEverytrail()
	{
		everytrail.getElement().getStyle().setDisplay(Display.NONE);
		syncPanel.getElement().getStyle().setDisplay(Display.BLOCK);

		PlaceBookService.everytrail(new AbstractCallback()
		{
			@Override
			public void failure(final Request request, final Response response)
			{
				syncPanel.getElement().getStyle().setDisplay(Display.NONE);
			}

			@Override
			public void success(final Request request, final Response response)
			{
				syncPanel.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
	}
}