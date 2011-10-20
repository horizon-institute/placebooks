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
		if(place instanceof PlaceBookPlace)
		{
			PlaceBookPlace placebookPlace = (PlaceBookPlace)place;
			placebookPlace.setPlaceController(controller);
			return (PlaceBookPlace)place;
		}
		return null;
	}

}
