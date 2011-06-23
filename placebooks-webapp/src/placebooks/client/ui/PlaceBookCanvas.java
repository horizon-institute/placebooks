package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookEditorPlace;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookCanvas extends FlowPanel
{
	private static final int DEFAULT_COLUMNS = 3;
	private static final int DEFAULT_PAGES = 2;

	private final PlaceBookItemWidgetFactory itemFactory;

	private final Collection<PlaceBookItemWidget> items = new HashSet<PlaceBookItemWidget>();

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	private final boolean panelsVisible;

	private PlaceBook placebook;

	private final PlaceController placeController;

	public PlaceBookCanvas(final PlaceController placeController, final PlaceBookItemWidgetFactory itemFactory,
			final boolean panelsVisible)
	{
		this.placeController = placeController;
		this.itemFactory = itemFactory;
		this.panelsVisible = panelsVisible;

		itemFactory.setCanvas(this);

		setStyleName(Resources.INSTANCE.style().canvas());

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				reflow();
			}
		});
	}

	public PlaceBookItemWidget add(final PlaceBookItem item)
	{
		placebook.add(item);
		return addToCanvas(item);
	}

	public Iterable<PlaceBookItemWidget> getItems()
	{
		return items;
	}

	public Iterable<PlaceBookPanel> getPanels()
	{
		return panels;
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}

	public void reflow()
	{
		for (final PlaceBookPanel panel : panels)
		{
			panel.reflow();
		}
	}

	public void remove(final PlaceBookItemWidget item)
	{
		items.remove(item);
		super.remove(item);
		placebook.removeItem(item.getItem());
	}

	public void updatePlaceBook(final PlaceBook newPlacebook)
	{
		if (this.placebook != null
				&& (this.placebook.getKey() == null || !this.placebook.getKey().equals(newPlacebook.getKey())))
		{
			placeController.goTo(new PlaceBookEditorPlace(newPlacebook));
		}

		if (this.placebook == null)
		{
			clear();
			// TODO Check placebook for num pages & columns
			for (int index = 0; index < (DEFAULT_PAGES * DEFAULT_COLUMNS); index++)
			{
				final PlaceBookPanel panel = new PlaceBookPanel(index, DEFAULT_COLUMNS, panelsVisible);
				panels.add(panel);
				add(panel);
			}
		}

		this.placebook = newPlacebook;

		// account.setHTML(placebook.getOwner().getName() + " <small>▼</small>");

		final Map<String, PlaceBookItemWidget> kept = new HashMap<String, PlaceBookItemWidget>();
		final Collection<PlaceBookItemWidget> removals = new ArrayList<PlaceBookItemWidget>();
		for (final PlaceBookItemWidget item : items)
		{
			final PlaceBookItem newItem = getItem(newPlacebook, item.getItem().getKey());
			if (newItem == null)
			{
				item.removeFromParent();
				removals.add(item);
			}
			else
			{
				final PlaceBookPanel panel = item.getPanel();
				final int index = newItem.hasParameter("panel") ? newItem.getParameter("panel") : 0;
				if (panel != null)
				{
					if (panel.getIndex() != index)
					{
						item.setPanel(panels.get(index));
					}
				}
				else
				{
					item.setPanel(panels.get(index));
				}
				item.setItem(newItem);
				kept.put(newItem.getKey(), item);
			}
		}
		items.removeAll(removals);

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			if (!kept.containsKey(item.getKey()))
			{
				addToCanvas(item);
			}
		}

		reflow();
	}

	private PlaceBookItemWidget addToCanvas(final PlaceBookItem item)
	{
		final PlaceBookItemWidget itemWidget = itemFactory.createItemWidget(item);
		add(itemWidget);
		items.add(itemWidget);
		if (item.hasParameter("panel"))
		{
			itemWidget.setPanel(panels.get(item.getParameter("panel")));
		}
		else
		{
			itemWidget.setPanel(panels.get(0));
		}
		return itemWidget;
	}

	private PlaceBookItem getItem(final PlaceBook placebook, final String key)
	{
		if (key == null) { return null; }
		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			if (key.equals(item.getKey())) { return item; }
		}
		return null;
	}
}