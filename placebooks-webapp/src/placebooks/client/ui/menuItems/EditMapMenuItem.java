package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.ui.dialogs.PlaceBookMapsDialog;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookPage;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class EditMapMenuItem extends MenuItem
{
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public EditMapMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super("Locate on Map");
		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return !item.getItem().getClassName().equals("placebooks.model.GPSTraceItem") && !getMaps().isEmpty();
	}

	private List<PlaceBookItemWidget> getMaps()
	{
		final List<PlaceBookItemWidget> mapItems = new ArrayList<PlaceBookItemWidget>();
		for (final PlaceBookPage page : controller.getPages().getPages())
		{
			for(final PlaceBookItemFrame item : page.getItems()) 		
			{
				if (item.getItem().getClassName().equals("placebooks.model.GPSTraceItem"))
				{
					mapItems.add(item.getItemWidget());
				}
			}
		}
		
		return mapItems;
	}
	
	@Override
	public void run()
	{
		final List<PlaceBookItemWidget> mapItems = getMaps();
		if(mapItems.isEmpty())
		{
			return;
		}
		else
		{
			PlaceBookMapsDialog mapDialog = new PlaceBookMapsDialog(item.getItem(), mapItems, controller);
			mapDialog.show();
		}
	}
}