package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.dialogs.PlaceBookMapsDialog;
import placebooks.client.ui.elements.DragController;
import placebooks.client.ui.elements.PlaceBookPage;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;

public class EditMapMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public EditMapMenuItem(final DragController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.editLocation());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return !item.getItem().getClassName().equals("placebooks.model.GPSTraceItem") && !getMaps().isEmpty();
	}

	@Override
	public void run()
	{
		final List<PlaceBookItemFrame> mapItems = getMaps();
		if (mapItems.isEmpty())
		{
			return;
		}
		else
		{
			final PlaceBookMapsDialog mapDialog = new PlaceBookMapsDialog(item.getItemWidget(), mapItems, controller);
			mapDialog.show();
		}
	}

	private List<PlaceBookItemFrame> getMaps()
	{
		final List<PlaceBookItemFrame> mapItems = new ArrayList<PlaceBookItemFrame>();
		for (final PlaceBookPage page : controller.getPages().getPages())
		{
			for (final PlaceBookItemFrame item : page.getItems())
			{
				if (item.getItem().getClassName().equals("placebooks.model.GPSTraceItem"))
				{
					mapItems.add(item);
				}
			}
		}

		return mapItems;
	}
}