package org.placebooks.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.placebooks.PlaceBooks;
import org.placebooks.UICallback;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

public class NearestFragment extends ShelfFragment
{
	class Search implements Runnable
	{
		private Location location;

		public Search(final Location location)
		{
			this.location = location;
		}

		@Override
		public void run()
		{
			final String locationData = "POINT(" + location.getLatitude() + " " + location.getLongitude() + ")";
			server.searchLocation(locationData, new UICallback<Shelf>(getView())
			{
				@Override
				public void onPostSuccess(Shelf result)
				{
					if(result == null)
					{
						return;
					}

					final Collection<Entry> removals = new ArrayList<Entry>();
					for (final Entry entry : result.getEntries())
					{
						if (entry.getDistance() > 100)
						{
							removals.add(entry);
						}
					}

					result.getEntries().removeAll(removals);

					
					Collections.sort(result.getEntries(), new Comparator<Entry>()
					{
						@Override
						public int compare(final Entry lhs, final Entry rhs)
						{
							return (int) (rhs.getDistance() - lhs.getDistance() * 1000);
						}
					});


					setShelf(result);
				}
			});
		}
	}

	private LocationManager locationManager;
	private LocationListener locationListener = new LocationListener()
	{
		public void onLocationChanged(Location location)
		{
			// Called when a new location is found by the network location provider.
			Thread thread = new Thread(new Search(location));
			thread.start();
			locationManager.removeUpdates(locationListener);
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}

		public void onProviderEnabled(String provider)
		{
		}

		public void onProviderDisabled(String provider)
		{
		}
	};

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setEmptyText("No Placebooks found nearby");
		server =((PlaceBooks) getActivity().getApplicationContext()).server;		

		locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
}