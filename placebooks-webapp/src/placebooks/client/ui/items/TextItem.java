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

	@Override
	public String resize()
	{
		super.resize();
		if (getParent() != null)
		{
			double panelWidth = 300;
			if(getParent().getParent() != null && getParent().getParent().getParent() != null)
			{
				final String panelWidthString = getParent().getParent().getParent().getElement().getStyle().getWidth();				
				if(panelWidthString != null && panelWidthString.endsWith("%"))
				{
					double percent = Double.parseDouble(panelWidthString.substring(0, panelWidthString.length() - 1));
					panelWidth = (900d * percent) / 100d;
				}
			}
			final double scale = getParent().getOffsetWidth() / panelWidth;
			textPanel.getElement().setAttribute("style",
												"width: "+ panelWidth +"px; -webkit-transform-origin: 0% 0%; -webkit-transform: scale("
														+ scale + ")");
			return (getOffsetHeight() * scale) + "px";
		}
		return null;
	}
}
