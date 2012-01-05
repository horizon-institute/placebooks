package placebooks.client.ui.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookConfirmDialog extends PlaceBookDialog
{

	interface ConfirmDialogUiBinder extends UiBinder<Widget, PlaceBookConfirmDialog>
	{
	}

	private static ConfirmDialogUiBinder uiBinder = GWT.create(ConfirmDialogUiBinder.class);
	
	@UiField
	Label textLabel;
	
	@UiField
	Button accept;
	
	public PlaceBookConfirmDialog(final String confirmText)
	{
		setWidget(uiBinder.createAndBindUi(this));
		
		textLabel.setText(confirmText);
	}

	public void setConfirmHandler(final ClickHandler handler)
	{
		accept.addClickHandler(handler);
	}
	
	@UiHandler("cancel")
	void cancel(final ClickEvent event)
	{
		hide();
	}
}
