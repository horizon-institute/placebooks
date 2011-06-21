package placebooks.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookAccount extends Composite
{

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookAccount>
	{
	}

	public PlaceBookAccount()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	PlaceBookList placebookList;
	
	@UiField
	Label userLabel;

	public PlaceBookAccount(String firstName)
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
}
