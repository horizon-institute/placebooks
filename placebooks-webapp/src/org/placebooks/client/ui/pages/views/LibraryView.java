package org.placebooks.client.ui.pages.views;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.controllers.UserController;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.views.PlaceBookShelf;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class LibraryView extends PageView implements View<Shelf>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, LibraryView>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);


	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	PlaceBookShelf shelf;

	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooksLibrary());

		PlaceBooks.getServer().getShelf(new AsyncCallback<Shelf>()
		{
			@Override
			public void onSuccess(Shelf item)
			{
				itemChanged(item);				
			}
		});
		
		return library;
	}

	@Override
	public void itemChanged(Shelf item)
	{
		shelf.itemChanged(item);
		if(item.getUser() != null)	
		{
			UserController.getController().setItem(item.getUser());
		}
	}
}