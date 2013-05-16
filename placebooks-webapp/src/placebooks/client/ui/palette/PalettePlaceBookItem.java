package placebooks.client.ui.palette;

import placebooks.client.PlaceBooks;
import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.DragController;
import placebooks.client.ui.items.PlaceBookItemView;
import placebooks.client.ui.items.PlaceBookItemViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;

public class PalettePlaceBookItem extends PaletteItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final PlaceBookItem item;
	private final DragController controller;

	public PalettePlaceBookItem(final PlaceBookItem placeBookItem, final DragController dragHandler)
	{
		super(placeBookItem.getMetadata("title", uiMessages.unnamed()));

		item = placeBookItem;
		controller = dragHandler;
	}

	@Override
	public Widget createWidget()
	{
		final Widget result = super.createWidget();

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

		if (controller.canAdd(item))
		{
			panel.getElement().getStyle().setCursor(Cursor.MOVE);
		}
		else
		{
			image.getElement().getStyle().setOpacity(0.5);
		}

		image.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				event.preventDefault();
			}
		});

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

	private PlaceBookItemView createItem()
	{
		final PlaceBookItem newItem = PlaceBooks.getServer()
				.parse(PlaceBookItem.class, new JSONObject(item).toString());
		if (newItem.getKey() != null)
		{
			newItem.setKey(null);
		}
		if (item.getKey() != null)
		{
			newItem.setMetadata("originalItemID", item.getKey());
		}
		if (newItem.getMetadata("originalItemID") == null)
		{
			newItem.removeMetadata("originalItemID");
		}
		newItem.setMetadata("tempID", "" + System.currentTimeMillis());
		final PlaceBookItemController itemController = new PlaceBookItemController(newItem, controller.getSaveItem());
		return PlaceBookItemViewFactory.createItemWidget(itemController);
	}
}
