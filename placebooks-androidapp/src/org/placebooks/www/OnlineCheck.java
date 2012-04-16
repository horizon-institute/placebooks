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

		    if (netInfo != null && netInfo.isConnected()) {		//isConnectedOrConnecting()
		        return true;
		    }
		    return false;
		    
	 }
	 /*
	 public boolean hasWifi(Context c){
		 ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);  
		 NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		 //NetworkInfo mMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		 
		 if (mWifi.isConnected()) {		
		        return true;
		    }
		    return false;
	 
	 }
	 */
		
}
