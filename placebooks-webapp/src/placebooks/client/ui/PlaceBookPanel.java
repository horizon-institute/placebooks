package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.PlaceBookEditor;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookPanel extends SimplePanel
{
	private final List<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	private int panelIndex;

	public PlaceBookPanel(final int index, final int columns)
	{
		this.panelIndex = index;
		setStyleName(PlaceBookEditor.RESOURCES.placebookpanel().panel());
		final int pos = index % columns;
		if (pos == 0)
		{
			addStyleName(PlaceBookEditor.RESOURCES.placebookpanel().panelleft());
		}
		else if (pos == (columns - 1))
		{
			addStyleName(PlaceBookEditor.RESOURCES.placebookpanel().panelright());
		}
		else
		{
			addStyleName(PlaceBookEditor.RESOURCES.placebookpanel().panelcenter());
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
		reorder();
	}

	public int getIndex()
	{
		return panelIndex;
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		reorder();
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
		reflow(null, 0);
	}

	int reflow(final PlaceBookItemFrame newItem, final int y)
	{
		Collections.sort(items, new Comparator<PlaceBookItemFrame>()
		{
			@Override
			public int compare(final PlaceBookItemFrame o1, final PlaceBookItemFrame o2)
			{
				return o1.getOrder() - o2.getOrder();
			}
		});

		int result = 0;
		int order = 0;
		int height = getElement().getOffsetTop();
		for (final PlaceBookItemFrame item : items)
		{
			if (newItem != null && y > height && y < height + item.getElement().getClientHeight())
			{
				newItem.getElement().getStyle().setWidth(30, Unit.PCT);
				newItem.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
				newItem.getElement().getStyle().setTop(height, Unit.PX);

				height += newItem.getElement().getClientHeight();
				result = order;
			}
			if (!item.isDragging())
			{
				item.getElement().getStyle().setWidth(30, Unit.PCT);
				item.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
				item.getElement().getStyle().setTop(height, Unit.PX);

				height += item.getElement().getClientHeight();
			}
			order++;
		}

		if (newItem != null && y > height)
		{
			newItem.getElement().getStyle().setWidth(30, Unit.PCT);
			newItem.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
			newItem.getElement().getStyle().setTop(height, Unit.PX);

			height += newItem.getElement().getClientHeight();
			result = order;
		}

		return result;
	}

	private void reorder()
	{
		int order = 0;
		for (final PlaceBookItemFrame item : items)
		{
			item.setOrder(order);
			order++;
		}
	}
}