package placebooks.client.ui.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

class PlaceBookDialogFrame extends PopupPanel
{
	interface PlaceBookDialogUiBinder extends UiBinder<Widget, PlaceBookDialogFrame>
	{
	}

	interface Style extends CssResource
	{
		String dialog();

		String dialogGlass();
	}

	private static PlaceBookDialogUiBinder uiBinder = GWT.create(PlaceBookDialogUiBinder.class);

	@UiField
	Label titleLabel;

	@UiField
	Label errorLabel;

	@UiField
	Panel content;
	
	PlaceBookDialogFrame()
	{	
		setWidget(uiBinder.createAndBindUi(this));
		
		errorLabel.setVisible(false);		
	}
	
	void setContent(Widget contentWidget)
	{
		content.clear();
		content.add(contentWidget);
	}
	
	public void setTitle(final String title)
	{
		titleLabel.setText(title);
	}
	
	void setError(final String error)
	{
		errorLabel.setText(error);
		errorLabel.setVisible(true);
	}
}
