package placebooks.client.ui;

import placebooks.client.model.Shelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookAppInstall extends PlaceBookPlace
{
	@Prefix("appinstall")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookAppInstall>
	{
		@Override
		public PlaceBookAppInstall getPlace(final String token)
		{
			return new PlaceBookAppInstall(null);
		}

		@Override
		public String getToken(final PlaceBookAppInstall place)
		{
			return "";
		}
	}

	interface PlaceBookAppInstallUiBinder extends UiBinder<Widget, PlaceBookAppInstall>
	{
	}

	private static PlaceBookAppInstallUiBinder uiBinder = GWT.create(PlaceBookAppInstallUiBinder.class);

	public PlaceBookAppInstall(final Shelf shelf)
	{
		super(shelf);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		toolbar.setPlace(this);

		Window.setTitle("PlaceBooks App Install Instructions");
		panel.setWidget(widget);
	}
}