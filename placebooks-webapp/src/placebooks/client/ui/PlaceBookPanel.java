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

	private int panelIndex;
	
	private final static int offsetY = 20;

	public PlaceBookPanel(final int index, final int columns)
	{
		this.panelIndex = index;
		setStyleName(Resources.INSTANCE.style().panel());
		final int pos = index % columns;
		if (pos == 0)
		{
			addStyleName(Resources.INSTANCE.style().panelleft());
		}
		else if (pos == (columns - 1))
		{
			addStyleName(Resources.INSTANCE.style().panelright());
		}
		else
		{
			addStyleName(Resources.INSTANCE.style().panelcenter());
		}
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
		return left < x && x < (left + width) && top < (y + offsetY) && (y + offsetY) < (top + height);
	}

	void reflow()
	{
		reflow(null, 0, false);
	}

	void reflow(final PlaceBookItemFrame newItem, final int mousey, boolean finished)
	{
		Collections.sort(items, new Comparator<PlaceBookItemFrame>()
		{
			@Override
			public int compare(final PlaceBookItemFrame o1, final PlaceBookItemFrame o2)
			{
				return o1.getOrder() - o2.getOrder();
			}
		});

		int order = 0;
		int height = getElement().getOffsetTop() - offsetY;
		final int y = mousey;
		for (final PlaceBookItemFrame item : items)
		{
			if (newItem != null && y > height && y < height + item.getElement().getClientHeight() - offsetY)
			{
				layoutItem(newItem, height, order, finished);

				height += newItem.getElement().getClientHeight() - offsetY;
				order++;
			}
			layoutItem(item, height, order, finished);

			height += item.getElement().getClientHeight() - offsetY;
			order++;
		}

		if (newItem != null && y > height)
		{
			layoutItem(newItem, height, order, finished);
		}
	}

	private void layoutItem(PlaceBookItemFrame item, int height, int order, boolean finished)
	{
		item.getElement().getStyle().setWidth(30, Unit.PCT);
		item.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
		item.getElement().getStyle().setTop(height, Unit.PX);
		
		if(finished)
		{
			item.setOrder(order);
		}
	}
}