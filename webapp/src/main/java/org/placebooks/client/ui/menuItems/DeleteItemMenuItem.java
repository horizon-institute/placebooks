package org.placebooks.client.ui.menuItems;

import org.placebooks.client.controllers.PlaceBookController;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.model.Page;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.placebooks.client.ui.views.ColumnView;

import com.google.gwt.core.client.GWT;

public class DeleteItemMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public DeleteItemMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.delete());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public void run()
	{
		final ColumnView panel = item.getColumn();
		panel.getPage().remove(item);
		item.setColumn(null);
		if (panel != null)
		{
			panel.reflow();
		}

		if (item.getItem().is(Type.GPSTraceItem))
		{
			for (final Page placebook : controller.getItem().getPages())
			{
				for (final Item pbItem : placebook.getItems())
				{
					if (pbItem.getMetadata("mapItemID", "").equals(item.getItem().getId()))
					{
						pbItem.getMetadata().remove("mapItemID");
					}
				}
			}
		}

		controller.markChanged();
	}
}
