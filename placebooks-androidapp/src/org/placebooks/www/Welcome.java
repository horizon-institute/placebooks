package org.placebooks.www;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.Locale;


public class Welcome extends Activity {
	
	private CustomApp appState;
	private String strUserName;
	private String strPassword;
    protected Dialog mSplashDialog;

	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        //setContentView(R.layout.splash);
		        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       
		        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		        
		        setContentView(R.layout.welcome);
		        MyStateSaver data = (MyStateSaver) getLastNonConfigurationInstance();


		        appState = ((CustomApp)getApplicationContext());
		        
		        Button btnEnglish = (Button) findViewById(R.id.btnEnglish);
		        btnEnglish.setOnClickListener(new OnClickListener() {
		        	public void onClick(View v){
		        		
		        		appState.setLanguage("en");
		        		
		        		Intent intent = new Intent();
    	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.PlaceBooks");
    	        		startActivity(intent);
    	        		endThisActivity();
		        		    }
		        });
		        
		        
		        Button btnCymraeg = (Button) findViewById(R.id.btnCymraeg);
		        btnCymraeg.setOnClickListener(new OnClickListener() {
		        	public void onClick(View v){
		        		
		        		appState.setLanguage("cy");
		        		
		        		Intent intent = new Intent();
    	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.PlaceBooks");
    	        		startActivity(intent);
    	        		endThisActivity();
		        		    }
		        });
		        
		        
		        
		        
		      //Get the app's shared preferences - check if a user connected their account to the app
		        SharedPreferences loginAppPreferences = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
		        strUserName = loginAppPreferences.getString("username", "");
		        strPassword = loginAppPreferences.getString("password", "");
		        
		        if (data != null) {
			            // Show splash screen if still loading
			            if (data.showSplashScreen) {
			                showSplashScreen();
			            }
			            if (strUserName != ""){
			            	
						     Intent intent = new Intent();
						     //intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
						     intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
						     intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
						     //intent.putExtra("password", password);
						     startActivity(intent);	
						     mSplashDialog.dismiss();
						     endThisActivity();
				
				        }
				        
				        else{	
				         //First time using the app on the phone so let user log in..
				        
				        //do nothing and let them select a language and log in..
				        
				        } //end of else         
			     
			            // Rebuild your UI with your saved state here
		        } else {
		            showSplashScreen();
			            if (strUserName != ""){
			            	
						     Intent intent = new Intent();
						     //intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
						     intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
						     intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
						     //intent.putExtra("password", password);
						     startActivity(intent);	
						     mSplashDialog.dismiss();
						     endThisActivity();
				
				        }
				        
				        else{	
				         //First time using the app on the phone so let user log in..
				        
				        //do nothing and let them select a language to log in
				        
				        } //end of else 
			            // Do your heavy loading here on a background thread
		        }
		        
		        
		        
		        
		        
	  }
	  
	  
	  /**
	   * Simple class for storing important data across config changes
	   */
	  private class MyStateSaver {
	      public boolean showSplashScreen = false;
	      // Your other important fields here
	  }
	  
	  @Override
	  public Object onRetainNonConfigurationInstance() {
	      MyStateSaver data = new MyStateSaver();
	      // Save your important data here
	   
	      if (mSplashDialog != null) {
	          data.showSplashScreen = true;
	          removeSplashScreen();
	      }
	      return data;
	  }
	   
	  /**
	   * Removes the Dialog that displays the splash screen
	   */
	  protected void removeSplashScreen() {
	      if (mSplashDialog != null) {
	          mSplashDialog.dismiss();
	          mSplashDialog = null;
	      }
	  }
	   
	  /**
	   * Shows the splash screen over the full Activity
	   */
	  protected void showSplashScreen() {
	      mSplashDialog = new Dialog(this, R.style.SplashScreen);
	      mSplashDialog.setContentView(R.layout.splash);
	      mSplashDialog.setCancelable(false);
	      mSplashDialog.show();
	   
	      // Set Runnable to remove splash screen just in case
	      final Handler handler = new Handler();
	      handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	          removeSplashScreen();
	        }
	      }, 3000);
	  }
	  
	 
	  
	  @Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	           Log.d(this.getClass().getName(), "back button pressed");
	          finish();
	          
	       }
	       return super.onKeyDown(keyCode, event);
	   }
	  
	  public void endThisActivity(){
		  this.finish();  
	  }

}
