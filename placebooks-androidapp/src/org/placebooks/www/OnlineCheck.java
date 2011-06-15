package org.placebooks.www;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class OnlineCheck {
	

	
	 /*
	  * Check for an Internet connection and return true if there is Internet
	  * Need to pass in the Context
	  */
	 public boolean isOnline(Context c) {
		 ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);  
		 
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}

}
