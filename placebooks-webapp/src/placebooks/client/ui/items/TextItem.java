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
	public String resize()
	{
		super.resize();
		if(getParent() != null)
		{
			final double scale = getParent().getOffsetWidth() / 300d;
			textPanel.getElement().setAttribute("style", "width: 300px; -webkit-transform-origin: 0% 0%; -webkit-transform: scale(" + scale + ")");
			return (getOffsetHeight() * scale) + "px";
		}
		return null;
	}
	
	@Override
	public void refresh()
	{
		textPanel.getElement().setInnerHTML(item.getText());
	}
}
