package org.placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.dialogs.PlaceBookMapsDialog;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.placebooks.client.ui.views.DragController;
import org.placebooks.client.ui.views.PageView;

import com.google.gwt.core.client.GWT;

public class EditMapMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public EditMapMenuItem(final DragController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.editLocation());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().getType() != Type.GPSTraceItem && !getMaps().isEmpty();
	}

	@Override
	public void run()
	{
		final List<PlaceBookItemFrame> mapItems = getMaps();
		if (mapItems.isEmpty())
		{
			return;
		}
		else
		{
			final PlaceBookMapsDialog mapDialog = new PlaceBookMapsDialog(item.getItemWidget().getController(), mapItems);
			mapDialog.show();
		}
	}

	private List<PlaceBookItemFrame> getMaps()
	{
		final List<PlaceBookItemFrame> mapItems = new ArrayList<PlaceBookItemFrame>();
		for (final PageView page : controller.getPages().getPages())
		{
			for (final PlaceBookItemFrame item : page.getItems())
			{
				if (item.getItem().getType() == Type.GPSTraceItem)
				{
					mapItems.add(item);
				}
			}
		}

		return mapItems;
	}
}