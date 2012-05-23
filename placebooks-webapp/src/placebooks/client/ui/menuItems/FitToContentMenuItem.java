package placebooks.client.ui.menuItems;

import com.google.gwt.core.client.GWT;

import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class FitToContentMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public FitToContentMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.fitToContent());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().hasParameter("height") && !item.getItem().is(ItemType.GPS);
	}

	@Override
	public void run()
	{
		item.getItem().removeParameter("height");
		item.getItemWidget().refresh();
		item.getColumn().reflow();
		controller.markChanged();
	}
}
