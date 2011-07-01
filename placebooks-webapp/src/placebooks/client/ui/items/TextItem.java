package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.user.client.ui.SimplePanel;

public class TextItem extends PlaceBookItemWidget
{
	private final SimplePanel textPanel = new SimplePanel();

	TextItem(final PlaceBookItem item)
	{
		super(item);
		
		initWidget(textPanel);
	}

	@Override
	public void refresh()
	{
		textPanel.getElement().setInnerHTML(item.getText());
	}
}
