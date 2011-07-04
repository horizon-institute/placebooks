package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.ui.PlaceBookEditor;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookEditorNewActivity extends AbstractActivity
{
	private final PlaceBook placebook;
	private final PlaceController placeController;

	public PlaceBookEditorNewActivity(final PlaceController controller, final PlaceBook placebook)
	{
		this.placebook = placebook;
		this.placeController = controller;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookEditor editor = new PlaceBookEditor(placeController);
		editor.setPlaceBook(placebook);
		panel.setWidget(editor);
	}
}
