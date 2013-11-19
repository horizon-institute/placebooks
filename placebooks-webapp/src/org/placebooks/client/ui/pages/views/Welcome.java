package org.placebooks.client.ui.pages.views;

import java.util.Iterator;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.pages.SearchPage;
import org.placebooks.client.ui.views.PlaceBookEntryPreview;
import org.placebooks.client.ui.views.PlaceBookToolbar;
import org.wornchaos.client.server.AsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Welcome extends PageView
{
	interface PlaceBookAccountUiBinder extends UiBinder<Widget, Welcome>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	TextBox search;

	@UiField
	SimplePanel preview1;

	@UiField
	PlaceBookToolbar toolbar;
	
	@UiField
	SimplePanel preview2;

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooks());

		PlaceBooks.getServer().getFeaturedPlaceBooks(2, new AsyncCallback<Shelf>()
		{
			@Override
			public void onSuccess(Shelf shelf)
			{
				final Iterator<Entry> entries = shelf.getEntries().iterator();
				if (entries.hasNext())
				{
					preview1.setWidget(new PlaceBookEntryPreview(entries.next()));
				}
				else
				{
					preview1.setVisible(false);
				}
				if (entries.hasNext())
				{
					preview2.setWidget(new PlaceBookEntryPreview(entries.next()));
				}
				else
				{
					preview2.setVisible(false);
				}		
			}
		});

		return widget;
	}

	@UiHandler("search")
	void handleBlur(final BlurEvent event)
	{
		if (search.getText().equals(""))
		{
			search.setText(uiMessages.searchPlaceBooks());
			search.getElement().getStyle().clearColor();
		}
	}

	@UiHandler("search")
	void handleFocus(final FocusEvent event)
	{
		if (search.getText().equals(uiMessages.searchPlaceBooks()))
		{
			search.setText("");
			search.getElement().getStyle().setColor("#000");
		}
	}

	@UiHandler("searchButton")
	void handleSearch(final ClickEvent event)
	{
		search();
	}

	@UiHandler("search")
	void handleSearchEnter(final KeyPressEvent event)
	{
		if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
		{
			search();
		}
	}

	private void search()
	{
		if (search.getText().equals(uiMessages.searchPlaceBooks()))
		{
			PlaceBooks.goTo(new SearchPage());
		}
		else
		{
			PlaceBooks.goTo(new SearchPage(search.getText()));
		}
	}
}