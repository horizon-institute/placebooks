package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookEditor.SaveState;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookEditorPlace extends PlaceBookPlace
{
	@Prefix("edit")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditorPlace>
	{
		@Override
		public PlaceBookEditorPlace getPlace(final String token)
		{
			return new PlaceBookEditorPlace(token, null);
		}

		@Override
		public String getToken(final PlaceBookEditorPlace place)
		{
			return place.getKey();
		}
	}
	
//	private final String key;
//	private final PlaceBook placebook;
//	private final PlaceController placeController;
//
	private PlaceBookEditor editor;	

	private final PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookEditorPlace(final PlaceBook placebook, final Shelf shelf)
	{
		super(shelf);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookEditorPlace(final String placebookKey, final Shelf shelf)
	{
		super(shelf);
		this.placebookKey = placebookKey;
		this.placebook = null;
	}

	String getKey()
	{
		return placebookKey;
	}
	
	@Override
	public String mayStop()
	{
		if (editor != null && editor.getSaveContext().getState() != SaveState.saved) { return "The current PlaceBook has unsaved changes. Are you sure you want to leave?"; }
		return super.mayStop();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		editor = new PlaceBookEditor(getPlaceController(), getShelf());
		if (placebook != null)
		{
			editor.setPlaceBook(placebook);
		}
		else
		{
			PlaceBookService.getPlaceBook(placebookKey, new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					editor.setPlaceBook(placebook);
				}
			});
		}

		panel.setWidget(editor);
	}	
}