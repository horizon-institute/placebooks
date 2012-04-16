package placebooks.client.ui;

import placebooks.client.JSONResponse;
import placebooks.client.model.DataStore;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.ui.elements.PlaceBookShelf;
import placebooks.client.ui.elements.PlaceBookShelf.ShelfControl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLibrary extends PlaceBookPlace
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookLibrary>
	{
		@Override
		public PlaceBookLibrary getPlace(final String token)
		{
			return new PlaceBookLibrary();
		}

		@Override
		public String getToken(final PlaceBookLibrary place)
		{
			return "library";
		}
	}

	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookLibrary>
	{
	}

	private final DataStore<Shelf> libraryStore = new DataStore<Shelf>()
	{
		@Override
		protected String getRequestURL(final String id)
		{
			return getHostURL() + "placebooks/a/shelf";
		}

		@Override
		protected String getStorageID(final String id)
		{
			return "library.shelf";
		}
	};

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	PlaceBookShelf shelf;

	public PlaceBookLibrary()
	{
		super(null);
	}

	public PlaceBookLibrary(final User user)
	{
		super(user);
	}

	private String getState(PlaceBookEntry entry)
	{
		String result = entry.getState();
		if(result == null)
		{
			result = "0";
		}
		return result;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle("PlaceBooks Library");

		toolbar.setPlace(this);

		panel.setWidget(library);

		shelf.setShelfControl(new ShelfControl(this)
		{
			@Override
			public int compare(final PlaceBookEntry o1, final PlaceBookEntry o2)
			{
				String state1 = getState(o1);
				String state2 = getState(o2);				
				
				if(!state1.equals(state2))
				{
					return state2.compareTo(state1);
				}
				return 0;
			}

			@Override
			public void getShelf(final JSONResponse<Shelf> callback)
			{
				libraryStore.get("", callback);
			}

			@Override
			public boolean include(final PlaceBookEntry entry)
			{
				return true;
			}
		});
	}
}