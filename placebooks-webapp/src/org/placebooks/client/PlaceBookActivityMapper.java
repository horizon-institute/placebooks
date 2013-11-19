package org.placebooks.client;

import org.placebooks.client.ui.pages.PlaceBookPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public class PlaceBookActivityMapper implements ActivityMapper
{
	public PlaceBookActivityMapper(final PlaceController controller)
	{
		super();
	}

	@Override
	public Activity getActivity(final Place place)
	{
		if (place instanceof PlaceBookPlace)
		{
			final PlaceBookPlace placebookPlace = (PlaceBookPlace) place;
			return placebookPlace.createActivity();
		}
		return null;
	}

}
