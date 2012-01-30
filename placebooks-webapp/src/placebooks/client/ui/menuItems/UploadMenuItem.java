package placebooks.client.ui.menuItems;

import placebooks.client.ui.dialogs.PlaceBookUploadDialog;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class UploadMenuItem extends MenuItem
{
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public UploadMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super("Upload");

		this.controller = controller;
		this.item = item;
	}

	@Override
	public void run()
	{
		final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(controller, item);
		dialog.show();
	}
}
