package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.openlayers.MapWidget;

public class MapItem extends PlaceBookItemWidget
{
	private final MapWidget map = new MapWidget();
	
	MapItem(PlaceBookItem item)
	{
		super(item);
		initWidget(map);
	}

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub

	}

}
