package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.widget.RichTextArea;

public class EditableTextItem extends PlaceBookItemWidget
{
	private RichTextArea textPanel = new RichTextArea("");

	EditableTextItem(final PlaceBookItem item)
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
