package placebooks.client.ui.items;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

public class MediaItem extends PlaceBookItemWidget
{
	private final Panel panel;

	MediaItem(PlaceBookItem item, PlaceBookController handler)
	{
		super(item, handler);
		panel = new SimplePanel();
		initWidget(panel);
	}

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub

	}

}
