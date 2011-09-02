package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookPanel;
import placebooks.client.ui.items.PlaceBookItemWidget;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class PlaceBookItemFrame
{
	protected PlaceBookItemWidget itemWidget;

	protected PlaceBookPanel panel;
	protected Panel rootPanel;
	protected final SimplePanel widgetPanel = new SimplePanel();

	public void clearItemWidget()
	{
		widgetPanel.clear();
		this.itemWidget = null;
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

	public Panel getRootPanel()
	{
		return rootPanel;
	}

	protected void itemWidgetResized()
	{
		if (panel != null)
		{
			panel.reflow();
		}
	}

	public void resize(final String height)
	{
		final String clientHeight = itemWidget.resize();
		if (clientHeight != null && height.equals(""))
		{
			rootPanel.getElement().getStyle().setProperty("height", clientHeight);
			return;
		}
		if (height.equals(rootPanel.getElement().getStyle().getHeight())) { return; }
		rootPanel.getElement().getStyle().setProperty("height", height);
	}

	public void setItemWidget(final PlaceBookItemWidget itemWidget)
	{
		this.itemWidget = itemWidget;

		widgetPanel.clear();
		if (itemWidget.getParent() != null)
		{
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
}
