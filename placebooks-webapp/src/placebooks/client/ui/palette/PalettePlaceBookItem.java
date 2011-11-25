package placebooks.client.ui.palette;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.elements.PlaceBookInteractionHandler;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.PlaceBookItemWidgetFactory;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;

public class PalettePlaceBookItem extends PaletteItem
{
	private final PlaceBookItem item;
	private final PlaceBookInteractionHandler handler;

		public PalettePlaceBookItem(final PlaceBookItem placeBookItem, final PlaceBookInteractionHandler dragHandler)
	{
		super(placeBookItem.getMetadata("title", "Unnamed"));
		
		this.item = placeBookItem;
		this.handler = dragHandler;
	}
	
	@Override
	public Widget createWidget()
	{
		Widget result =super.createWidget();
		
		panel.getElement().getStyle().setCursor(Cursor.MOVE);

		if (item.is(ItemType.IMAGE) && item.getKey() != null)
		{
			image.setUrl(item.getThumbURL());
			image.setHeight("auto");
			image.setWidth("64px");
		}
		else
		{
			image.setResource(item.getIcon());
		}
		panel.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				if (handler.canAdd(item))
				{
					handler.setupDrag(event, createItem(), null);
				}
			}
		});		
		return result;
	}



	private PlaceBookItemWidget createItem()
	{
		final PlaceBookItem newItem = PlaceBookItem.parse(new JSONObject(item).toString());
		if (newItem.getKey() != null)
		{
			newItem.setKey(null);
		}
		if( item.getKey()!=null)
		{
			newItem.setMetadata("originalItemID", item.getKey());
		}
		if(newItem.getMetadata("originalItemID")==null)
		{
			newItem.removeMetadata("originalItemID");
		}
		newItem.setMetadata("tempID", "" + System.currentTimeMillis());
		return PlaceBookItemWidgetFactory.createItemWidget(newItem, true);
	}
}
