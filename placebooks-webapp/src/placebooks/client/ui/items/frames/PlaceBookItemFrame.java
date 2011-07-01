package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookPanel;
import placebooks.client.ui.items.PlaceBookItemWidget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class PlaceBookItemFrame extends Composite
{
	protected final SimplePanel widgetPanel = new SimplePanel();
	
	protected PlaceBookPanel panel;
	protected PlaceBookItemWidget itemWidget;
	
	public void setItemWidget(PlaceBookItemWidget itemWidget)
	{
		this.itemWidget = itemWidget;

		widgetPanel.clear();
		widgetPanel.add(itemWidget);
		itemWidget.refresh();
		itemWidget.setResizeHandler(new PlaceBookItemWidget.ResizeHandler()
		{
			@Override
			public void itemResized()
			{
				itemWidgetResized();
			}
		});
	}
	
	protected void itemWidgetResized()
	{
		if(panel != null)
		{
			panel.reflow();
		}
	}
	
	public PlaceBookItem getItem()
	{
		return itemWidget.getItem();
	}
	
	public PlaceBookItemWidget getItemWidget()
	{
		return itemWidget;
	}
	
	public PlaceBookPanel getPanel()
	{
		return panel;
	}
	
	public void setPanel(PlaceBookPanel newPanel)
	{
		if(panel == newPanel)
		{
			return;
		}
		if(this.panel != null)
		{
			panel.remove(this);
		}
		panel = newPanel;
		if(panel != null)
		{
			panel.add(this);
			getItem().setParameter("panel", panel.getIndex());
		}
	}
	
	public void resize(final String left, final String top, final String width, final String height)
	{
		if(left.equals(getElement().getStyle().getLeft()) && top.equals(getElement().getStyle().getTop()) && width.equals(getElement().getStyle().getWidth()) && height.equals(itemWidget.getElement().getStyle().getHeight()))
		{
			return;
		}
		getElement().getStyle().setProperty("left", left);
		getElement().getStyle().setProperty("top", top);
		getElement().getStyle().setProperty("width", width);
		itemWidget.getElement().getStyle().setProperty("height", height);
	}	
}
