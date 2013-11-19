package org.placebooks.client.ui.palette;

import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.items.PlaceBookItemView;
import org.placebooks.client.ui.items.PlaceBookItemViewFactory;
import org.placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Widget;

public class PalettePlaceBookItem extends PaletteItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final Item item;
	private final DragController controller;

	public PalettePlaceBookItem(final Item placeBookItem, final DragController dragHandler)
	{
		super(placeBookItem.getMetadata("title", uiMessages.unnamed()));

		item = placeBookItem;
		controller = dragHandler;
	}
	
	@Override
	public Widget createWidget()
	{
		final Widget result = super.createWidget();

		if (item.is(Type.ImageItem) && item.getId() != null)
		{
			image.setUrl(ItemController.getThumbURL(item));
			image.setHeight("auto");
			image.setWidth("64px");
		}
		else
		{
			image.setResource(ItemController.getIcon(item));
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
		final Item newItem = item.createCopy();
		if (newItem.getId() != null)
		{
			newItem.setId(null);
		}
		if (item.getId() != null)
		{
			newItem.getMetadata().put("originalItemID", item.getId());
		}
		newItem.getMetadata().remove("title");
		newItem.getMetadata().put("tempID", "" + System.currentTimeMillis());
		final ItemController itemController = new ItemController(newItem, controller.getSaveItem(), true);
		return PlaceBookItemViewFactory.createItemWidget(itemController);
	}
}
