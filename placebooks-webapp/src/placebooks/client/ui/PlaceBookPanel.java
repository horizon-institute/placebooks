package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookPanel extends SimplePanel
{
	private final List<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	private final int panelIndex;
	
	private final int column;
	
	private final int row;
	
	private final int pageMargin;
	
	private float panelWidth = 30;

	public PlaceBookPanel(final int index, final int columns, final int pageMargin)
	{
		this.panelIndex = index;
		this.pageMargin = pageMargin;
		setStyleName(Resources.INSTANCE.style().panel());
		column = index % columns;
		row = index / columns; 
		if (column == 0)
		{
			addStyleName(Resources.INSTANCE.style().panelleft());
		}
		else if (column == (columns - 1))
		{
			addStyleName(Resources.INSTANCE.style().panelright());
		}
		else
		{
			addStyleName(Resources.INSTANCE.style().panelcenter());
		}
		setWidth(30);
	}

	public void add(final PlaceBookItemFrame item)
	{
		if (item.getOrder() > items.size())
		{
			items.add(item);
		}
		else
		{
			items.add(item.getOrder(), item);
		}
	}

	public int getIndex()
	{
		return panelIndex;
	}

	public void remove(final PlaceBookItemFrame item)
	{
		GWT.log("Removed");
		items.remove(item);
	}

	boolean isIn(final int x, final int y)
	{
		final int left = getElement().getOffsetLeft();
		final int width = getElement().getOffsetWidth();
		final int top = getElement().getOffsetTop();
		final int height = getElement().getOffsetHeight();
		return left < x && x < (left + width) && top < y && y < (top + height);
	}

	void reflow()
	{
		reflow(null, 0, false);
	}

	void reflow(final PlaceBookItemFrame newItem, final int mousey, final boolean finished)
	{
		Collections.sort(items, new Comparator<PlaceBookItemFrame>()
		{
			@Override
			public int compare(final PlaceBookItemFrame o1, final PlaceBookItemFrame o2)
			{
				return o1.getOrder() - o2.getOrder();
			}
		});

		resize();
		
		int order = 0;
		int height = getElement().getOffsetTop();
		final int y = mousey;
		for (final PlaceBookItemFrame item : items)
		{
			if (newItem != null && y > height && y < height + item.getContentHeight())
			{
				height += layoutItem(newItem, height, order, finished);

				order++;
			}
			height += layoutItem(item, height, order, finished);

			order++;
		}

		if (newItem != null && y > height)
		{
			layoutItem(newItem, height, order, finished);
		}
	}

	private int layoutItem(final PlaceBookItemFrame item, final int height, final int order, final boolean finished)
	{
		item.getElement().getStyle().setWidth(panelWidth, Unit.PCT);
		item.getElement().getStyle().setLeft(column * panelWidth + pageMargin, Unit.PCT);
		item.setTop(height);
		
		item.resize();

		if (finished)
		{
			item.setOrder(order);
		}
		
		return item.getContentHeight();
	}

	public void resize()
	{
		int panelHeight = getElement().getClientWidth() * 2;
		
		int panelTop = ((panelHeight + 20) * row) + 20;
		
		getElement().getStyle().setTop(panelTop, Unit.PX);
		getElement().getStyle().setHeight(panelHeight, Unit.PX);
	}
	
	public void setWidth(float panelWidth)
	{
		this.panelWidth = panelWidth;
		getElement().getStyle().setWidth(panelWidth, Unit.PCT);
		getElement().getStyle().setLeft(column * panelWidth + pageMargin, Unit.PCT);
		
		resize();
	}
}