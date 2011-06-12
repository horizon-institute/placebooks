package org.placebooks.www;

import android.app.Activity;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/*
 * For now this class just loads the web version for 'creating an account'
 * Later on I will need to create my own 'register form' and pass the values
 * to the server to register
 */
public class Register extends Activity {
	
		
		 @Override
	 	 public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);	//icicle
		        
		        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
		        setContentView(R.layout.register);

				OnlineCheck oc = new OnlineCheck();
				boolean ioc = oc.isOnline(Register.this);
				//if the mobile is online then go to the web address to register
				if(ioc){
					//Open up a WebViewer
					WebView wv = new WebView(Register.this);
					wv.loadUrl("http://horizab1.miniserver.com:8080/placebooks/register.html");
				   	wv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
				   	wv.getSettings().setBuiltInZoomControls(true);
				   	wv.getSettings().setJavaScriptEnabled(true);	//allows the webview to be able to handle javascript in websites
				   	setContentView(wv);
				}
				//otherwise tell the user that the mobile cannot connect to the Internet and cannot register
				else{
					TextView tv = new TextView(Register.this);
					tv.setText("Cannot connect to the Internet");
					setContentView(tv);
				}
		 }
}
