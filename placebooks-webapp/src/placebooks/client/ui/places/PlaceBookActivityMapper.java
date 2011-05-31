package placebooks.client.ui.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public class PlaceBookActivityMapper implements ActivityMapper
{
	private final PlaceController controller; 
	
	public PlaceBookActivityMapper(PlaceController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public Activity getActivity(Place place)
	{
		if(place instanceof EditorPlace)
		{
			return new EditorActivity(controller, ((EditorPlace)place).getPlaceBook(),((EditorPlace)place).getKey());
		}
		else if(place instanceof PlaceBookListPlace)
		{
			return new PlaceBookListActivity(controller);
		}
		return null;
	}

}
