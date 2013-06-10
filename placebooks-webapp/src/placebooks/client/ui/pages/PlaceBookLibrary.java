package placebooks.client.ui.pages;

import placebooks.client.PlaceBooks;
import placebooks.client.controllers.CachedController;
import placebooks.client.controllers.UserController;
import placebooks.client.model.Shelf;
import placebooks.client.parser.JavaScriptObjectParser;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.views.PlaceBookShelf;
import placebooks.client.ui.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLibrary extends Page implements View<Shelf>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookLibrary>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final CachedController<Shelf> controller = new CachedController<Shelf>(
			new JavaScriptObjectParser<Shelf>(), "library")
	{
		@Override
		protected void load(final String id, final AsyncCallback<Shelf> callback)
		{
			PlaceBooks.getServer().getShelf(callback);
		}
	};

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	PlaceBookShelf shelf;

	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooksLibrary());

		controller.add(shelf);
		controller.load();

		return library;
	}

	@Override
	public void itemChanged(Shelf item)
	{
		if(item.getUser() != null)	
		{
			UserController.getController().setItem(item.getUser());
		}
	}
}