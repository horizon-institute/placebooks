package org.placebooks;

import org.placebooks.client.model.PlaceBookService;

import android.app.Application;
import android.content.SharedPreferences;

public class PlaceBooks extends Application
{
	public PlaceBookService server;

	private static final String PREFS_NAME = "PlaceBooks";

	@Override
	public void onCreate()
	{
		super.onCreate();

		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
		final String serverURL = preferences.getString("server", "http://severn.cs.nott.ac.uk/ktg-servlets/");

		server = PlaceBookServerHandler.createServer(serverURL, getApplicationContext());
	}
}