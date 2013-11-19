package org.placebooks;

import org.wornchaos.client.server.ConnectionStatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidConnectionStatus implements ConnectionStatus
{
	private Context context;

	public AndroidConnectionStatus(final Context context)
	{
		this.context = context;
	}

	@Override
	public boolean isOnline()
	{
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnected()) { return true; }
		return false;
	}
}
