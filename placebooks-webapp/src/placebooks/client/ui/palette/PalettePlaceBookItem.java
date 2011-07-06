package placebooks.client.ui.palette;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookInteractionHandler;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.PlaceBookItemWidgetFactory;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.json.client.JSONObject;

public class PalettePlaceBookItem extends PaletteItem
{
	private final PlaceBookItem item;

	public PalettePlaceBookItem(final PlaceBookItem placeBookItem, final PlaceBookInteractionHandler dragHandler)
	{
		super();

		this.item = placeBookItem;

		text.setText(placeBookItem.getMetadata("title", "Unnamed"));

		panel.getElement().getStyle().setCursor(Cursor.MOVE);

		image.setResource(item.getIcon());
		panel.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				dragHandler.setupDrag(event, createItem(), null);
			}
		});
	}

	private PlaceBookItemWidget createItem()
	{
		final PlaceBookItem newItem = PlaceBookItem.parse(new JSONObject(item).toString());
		if (newItem.getKey() != null)
		{
			newItem.setMetadata("originalItemID", newItem.getKey());
			newItem.setKey(null);
		}
		newItem.setMetadata("tempID", "" + System.currentTimeMillis());
		return PlaceBookItemWidgetFactory.createItemWidget(newItem, true);
	}
}
