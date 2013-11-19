package org.placebooks.client.ui.menuItems;

import org.placebooks.client.controllers.PlaceBookController;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.dialogs.PlaceBookUploadDialog;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;

public class UploadMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public UploadMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.upload());

		this.controller = controller;
		this.item = item;
	}

	@Override
	public void run()
	{
		final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(controller, item.getItemWidget());
		dialog.show();
	}
}
