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
		if(place instanceof PlaceBookEditorPlace)
		{
			return new PlaceBookEditorActivity(controller, ((PlaceBookEditorPlace)place).getPlaceBook(),((PlaceBookEditorPlace)place).getKey());
		}
		else if(place instanceof PlaceBookAccountPlace)
		{
			return new PlaceBookAccountActivity(controller);
		}
		else if(place instanceof PlaceBookPreviewPlace)
		{
			return new PlaceBookPreviewActivity(controller, ((PlaceBookPreviewPlace)place).getPlaceBook(),((PlaceBookPreviewPlace)place).getKey());
		}
		return null;
	}

}
