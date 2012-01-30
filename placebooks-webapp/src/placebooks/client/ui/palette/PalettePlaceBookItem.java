package placebooks.client.ui.palette;

import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.elements.PlaceBookController;
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
	private final PlaceBookController controller;

	public PalettePlaceBookItem(final PlaceBookItem placeBookItem, final PlaceBookController dragHandler)
	{
		super(placeBookItem.getMetadata("title", "Unnamed"));

		this.item = placeBookItem;
		this.controller = dragHandler;
	}

	@Override
	public Widget createWidget()
	{
		Widget result =super.createWidget();

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
		
		if(controller.canAdd(item))
		{
			panel.getElement().getStyle().setCursor(Cursor.MOVE);
		}
		else
		{
			image.getElement().getStyle().setOpacity(0.5);
		}		
		
		panel.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				if (controller.canAdd(item))
				{
					controller.setupDrag(event, createItem(), null);
				}
			}
		});		
		return result;
	}



	private PlaceBookItemWidget createItem()
	{
		final PlaceBookItem newItem = PlaceBookService.parse(PlaceBookItem.class, new JSONObject(item).toString());
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
		return PlaceBookItemWidgetFactory.createItemWidget(newItem, controller);
	}
}
