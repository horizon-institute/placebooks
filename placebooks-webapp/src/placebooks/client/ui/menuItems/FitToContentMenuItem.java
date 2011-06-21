package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.PlaceBookItemWidgetFrame;
import placebooks.client.ui.widget.MenuItem;

public class FitToContentMenuItem extends MenuItem
{
	private final PlaceBookItemWidgetFrame item;

	public FitToContentMenuItem(final String title, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		this.item = item;
	}

	@Override
	public void run()
	{
		item.getItem().removeParameter("height");
		item.getPanel().reflow();
		item.markChanged();
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().hasParameter("height");
	}
}
