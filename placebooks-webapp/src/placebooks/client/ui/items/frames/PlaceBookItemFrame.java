package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.items.PlaceBookItemView;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class PlaceBookItemFrame
{
	protected PlaceBookItemView itemWidget;

	protected PlaceBookColumn column;
	protected Panel rootPanel;
	protected final SimplePanel widgetPanel = new SimplePanel();

	protected final Image markerImage = new Image();

	public void clearItemWidget()
	{
		widgetPanel.clear();
		itemWidget = null;
	}

	public PlaceBookColumn getColumn()
	{
		return column;
	}

	public PlaceBookItem getItem()
	{
		return itemWidget.getItem();
	}

	public PlaceBookItemView getItemWidget()
	{
		return itemWidget;
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

	public void setColumn(final PlaceBookColumn newColumn)
	{
		if (column == newColumn) { return; }
		if (column != null)
		{
			column.remove(this);
			column.getInnerPanel().remove(rootPanel);
		}
		column = newColumn;
		if (column != null)
		{
			column.add(this);
			column.getInnerPanel().add(rootPanel);
			getItem().setParameter("column", column.getIndex());
		}
	}

	public void setItemWidget(final PlaceBookItemView itemWidget)
	{
		this.itemWidget = itemWidget;

		widgetPanel.clear();
		if (itemWidget.getParent() != null)
		{
			itemWidget.removeFromParent();
		}
		widgetPanel.add(itemWidget);
		itemWidget.refresh();
		itemWidget.setResizeHandler(new PlaceBookItemView.ResizeHandler()
		{
			@Override
			public void itemResized()
			{
				itemWidgetResized();
			}
		});
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
