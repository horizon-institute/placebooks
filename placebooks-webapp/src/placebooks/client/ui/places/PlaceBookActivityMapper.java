package placebooks.client.ui.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public class PlaceBookActivityMapper implements ActivityMapper
{
	private final PlaceController controller;

	public PlaceBookActivityMapper(final PlaceController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public Activity getActivity(final Place place)
	{
		if (place instanceof PlaceBookEditorPlace)
		{
			return new PlaceBookEditorActivity(controller, (PlaceBookEditorPlace) place);
		}
		else if (place instanceof PlaceBookHomePlace)
		{
			return new PlaceBookHomeActivity(controller, ((PlaceBookHomePlace) place).getShelf());
		}
		else if (place instanceof PlaceBookLibraryPlace)
		{
			return new PlaceBookLibraryActivity(controller, ((PlaceBookLibraryPlace) place).getShelf());
		}
		else if (place instanceof PlaceBookSearchPlace)
		{
			return new PlaceBookSearchActivity(controller, (PlaceBookSearchPlace) place);
		}
		else if (place instanceof PlaceBookEditorNewPlace)
		{
			return new PlaceBookEditorNewActivity(controller, ((PlaceBookEditorNewPlace) place).getPlaceBook(), ((PlaceBookEditorNewPlace) place).getShelf());
		}
		else if (place instanceof PlaceBookPreviewPlace) { return new PlaceBookPreviewActivity((PlaceBookPreviewPlace) place); }
		return null;
	}

}
