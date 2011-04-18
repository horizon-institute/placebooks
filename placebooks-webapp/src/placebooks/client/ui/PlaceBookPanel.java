package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.PlaceBookEditor;

import com.google.gwt.core.client.GWT;
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
		if(item.getOrder() > items.size())
		{
			items.add(item);
		}
		else
		{
			items.add(item.getOrder(), item);
		}
		reorder();
	}
	
	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		reorder();
	}

	private void reorder()
	{
		int order = 0;
		for(PlaceBookItemFrame item: items)
		{
			item.setOrder(order);
			order++;
		}
	}

	public int getIndex()
	{
		return panelIndex;
	}
	
	void reflow()
	{
		reflow(null, 0);
	}
	
	boolean isIn(final int x, final int y)
	{
		int left = getElement().getOffsetLeft();
		int width = getElement().getOffsetWidth();
		int top = getElement().getOffsetTop();
		int height = getElement().getOffsetHeight();
		return left < x && x < (left + width) && top < y && y < (top + height); 
	}

	int reflow(final PlaceBookItemFrame newItem, final int y)
	{
		Collections.sort(items, new Comparator<PlaceBookItemFrame>()
		{
			@Override
			public int compare(PlaceBookItemFrame o1, PlaceBookItemFrame o2)
			{
				return o1.getOrder() - o2.getOrder();
			}
		});

		int result = 0;
		int order = 0;
		int height = getElement().getOffsetTop();
		for (PlaceBookItemFrame item : items)
		{
			if(newItem != null && y > height && y < height + item.getElement().getClientHeight())
			{
				newItem.getElement().getStyle().setWidth(30, Unit.PCT);
				newItem.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
				newItem.getElement().getStyle().setTop(height, Unit.PX);

				height += newItem.getElement().getClientHeight();
				result = order;
			}
 			if(!item.isDragging())
			{
				item.getElement().getStyle().setWidth(30, Unit.PCT);
				item.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
				item.getElement().getStyle().setTop(height, Unit.PX);
	
				height += item.getElement().getClientHeight();
			}
 			order ++;
		}
		
		
		if(newItem != null && y > height)
		{	
			newItem.getElement().getStyle().setWidth(30, Unit.PCT);
			newItem.getElement().getStyle().setLeft(panelIndex * 30 + 5, Unit.PCT);
			newItem.getElement().getStyle().setTop(height, Unit.PX);

			height += newItem.getElement().getClientHeight();
			result = order;
		}
		
		return result;
	}
}