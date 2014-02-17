package org.placebooks.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PlaceBookList extends CellList<Entry>
{
	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookList.css")
		Style style();
	}

	static class PlaceBookCell extends AbstractCell<Entry>
	{
		public PlaceBookCell()
		{
		}

		@Override
		public void render(final Context context, final Entry value, final SafeHtmlBuilder sb)
		{
			// Value can be null, so do a null check..
			if (value == null) { return; }

			sb.appendHtmlConstant("<div class=\"" + STYLES.style().listItem() + "\">");

			// Add the name and address.
			sb.appendHtmlConstant("<div>");
			if (value.getKey().equals("new"))
			{
				sb.appendEscaped(value.getTitle());
			}
			else
			{
				sb.appendEscaped(uiMessages.editPlaceBook(value.getTitle()));
			}
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("<div><small style=\"color: #333;\">");
			if (value.getDescription() == null)
			{
				sb.appendEscaped(uiMessages.noDesc());
			}
			else
			{
				sb.appendEscaped(value.getDescription());
			}

			sb.appendHtmlConstant("</small></div>");
		}
	}

	interface Style extends CssResource
	{
		String listItem();
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final Bundle STYLES = GWT.create(Bundle.class);

	private static final Entry newPlaceBook;

	private static final ProvidesKey<Entry> placebookKeyProvider = new ProvidesKey<Entry>()
	{
		@Override
		public Object getKey(final Entry entry)
		{
			return entry.getKey();
		}
	};

	static
	{
		newPlaceBook = new Entry();
		newPlaceBook.setTitle(uiMessages.createNewPlaceBook());
		newPlaceBook.setDescription(uiMessages.createNewPlaceBookDesc());
		newPlaceBook.setKey("new");
	}

	public PlaceBookList()
	{
		super(new PlaceBookCell(), placebookKeyProvider);
		STYLES.style().ensureInjected();
		setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	}

	public void addSelectionHandler(final SelectionChangeEvent.Handler handler)
	{
		final SingleSelectionModel<Entry> selectionModel = new SingleSelectionModel<Entry>(
				placebookKeyProvider);
		setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(handler);
	}

	@SuppressWarnings("unchecked")
	public Entry getSelection()
	{
		return ((SingleSelectionModel<Entry>) getSelectionModel()).getSelectedObject();
	}

	@SuppressWarnings("unchecked")
	public void setShelf(final Shelf shelf)
	{
		if (shelf == null)
		{
			setRowData(Collections.EMPTY_LIST);
		}
		else
		{
			final List<Entry> entries = new ArrayList<Entry>();
			entries.add(newPlaceBook);
			for (final Entry entry : entries)
			{
				entries.add(entry);
			}
			setRowData(entries);
		}
	}
}