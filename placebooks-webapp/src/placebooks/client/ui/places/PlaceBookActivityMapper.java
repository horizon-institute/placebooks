package placebooks.client.ui.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class PlaceBookActivityMapper implements ActivityMapper
{

	@Override
	public Activity getActivity(Place place)
	{
		if(place instanceof EditorPlace)
		{
			return new EditorActivity(((EditorPlace)place).getKey());
		}
		else if(place instanceof PlaceBookListPlace)
		{
			return new PlaceBookListActivity();
		}
		return null;
	}

}
