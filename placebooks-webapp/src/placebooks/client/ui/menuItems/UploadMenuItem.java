package placebooks.client.ui.menuItems;

import placebooks.client.ui.dialogs.PlaceBookUploadDialog;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class UploadMenuItem extends MenuItem
{
	private final PlaceBookItemFrame item;

	public UploadMenuItem(final PlaceBookItemFrame item)
	{
		super("Upload");

		this.item = item;
	}

	@Override
	public void run()
	{
		final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(item);
		dialog.show();
	}
}
