package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookPanel;
import placebooks.client.ui.items.PlaceBookItemWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class PlaceBookItemFrame
{
	protected PlaceBookItemWidget itemWidget;

	protected PlaceBookPanel panel;
	protected Panel rootPanel;
	protected final SimplePanel widgetPanel = new SimplePanel();

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

	public Panel getRootPanel()
	{
		return rootPanel;
	}

	public void resize(final String left, final String top, final String width, final String height)
	{
		if (left.equals(rootPanel.getElement().getStyle().getLeft())
				&& top.equals(rootPanel.getElement().getStyle().getTop())
				&& width.equals(rootPanel.getElement().getStyle().getWidth())
				&& height.equals(itemWidget.getElement().getStyle().getHeight())) { return; }
		rootPanel.getElement().getStyle().setProperty("left", left);
		rootPanel.getElement().getStyle().setProperty("top", top);
		rootPanel.getElement().getStyle().setProperty("width", width);
		itemWidget.getElement().getStyle().setProperty("height", height);
	}

	public void setItemWidget(final PlaceBookItemWidget itemWidget)
	{
		this.itemWidget = itemWidget;

		widgetPanel.clear();
		if (itemWidget.getParent() != null)
		{
			GWT.log("Item has parent");
			itemWidget.removeFromParent();
		}
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

	public void setPanel(final PlaceBookPanel newPanel)
	{
		if (panel == newPanel) { return; }
		if (panel != null)
		{
			panel.remove(this);
			panel.getInnerPanel().remove(rootPanel);
		}
		panel = newPanel;
		if (panel != null)
		{
			panel.add(this);
			panel.getInnerPanel().add(rootPanel);
			getItem().setParameter("panel", panel.getIndex());
		}
	}

	public void updateFrame()
	{

	}

	protected void itemWidgetResized()
	{
		if (panel != null)
		{
			panel.reflow();
		}
	}
}
