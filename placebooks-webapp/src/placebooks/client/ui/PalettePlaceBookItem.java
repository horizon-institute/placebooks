package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.HTMLPanel;

public class PalettePlaceBookItem extends PaletteItem
{
	private final PlaceBookItem item;

	public PalettePlaceBookItem(final PlaceBookItem placeBookItem, final PlaceBookItemDragHandler dragHandler)
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
				dragHandler.handleDragInitialization(event, createItem(), null);
			}
		});
	}

	private PlaceBookItem createItem()
	{
		final PlaceBookItem newItem = PlaceBookItem.parse(new JSONObject(item).toString());
		if (newItem.getKey() != null)
		{
			newItem.setMetadata("originalItemID", newItem.getKey());
			newItem.setKey(null);
		}
		newItem.setMetadata("tempID", HTMLPanel.createUniqueId());
		return newItem;
	}
}
