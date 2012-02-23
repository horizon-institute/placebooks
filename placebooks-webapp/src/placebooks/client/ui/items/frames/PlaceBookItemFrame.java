package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.items.PlaceBookItemWidget;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class PlaceBookItemFrame
{
	protected PlaceBookItemWidget itemWidget;

	protected PlaceBookColumn column;
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

	public PlaceBookColumn getColumn()
	{
		return column;
	}

	public Panel getRootPanel()
	{
		return rootPanel;
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

	public void setPanel(final PlaceBookColumn newPanel)
	{
		if (column == newPanel) { return; }
		if (column != null)
		{
			column.remove(this);
			column.getInnerPanel().remove(rootPanel);
		}
		column = newPanel;
		if (column != null)
		{
			column.add(this);
			column.getInnerPanel().add(rootPanel);
			getItem().setParameter("column", column.getIndex());
		}
	}

	public void updateFrame()
	{

	}

	protected void itemWidgetResized()
	{
		if (column != null)
		{
			column.reflow();
		}
	}
}
