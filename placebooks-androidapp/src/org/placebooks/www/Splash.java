package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.res.Configuration;
import android.content.pm.ActivityInfo;


public class Splash extends Activity {

private final int SPLASH_DISPLAY_LENGHT = 1000;


	@Override
	public void onCreate(Bundle icicle) {
	
	        super.onCreate(icicle);
	
	        setContentView(R.layout.splash);
	
	       
	
	        /* New Handler to start the Menu-Activity
	
	         * and close this Splash-Screen after some seconds.*/
	
	        new Handler().postDelayed(new Runnable(){
	
	                @Override
	
	                public void run() {
	
	                        /* Create an Intent that will start the Menu-Activity. */
	
	                        Intent mainIntent = new Intent(Splash.this,Welcome.class);
	
	                        Splash.this.startActivity(mainIntent);
	
	                        Splash.this.finish();
	
	                }
	
	        }, SPLASH_DISPLAY_LENGHT);
	
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}


