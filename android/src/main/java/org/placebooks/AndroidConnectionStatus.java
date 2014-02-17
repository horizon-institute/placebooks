package org.placebooks;

import org.wornchaos.client.server.ConnectionStatus;
import org.wornchaos.logger.Log;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidConnectionStatus implements ConnectionStatus
{
	private final ConnectivityManager connectivityManager;

	public AndroidConnectionStatus(final Context context)
	{
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public boolean isOnline()
	{
		final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

		Log.info("Connected: " + (netInfo != null && netInfo.isConnected()));
		
		return netInfo != null && netInfo.isConnected();
	}
}
