package org.placebooks.www;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.LinearLayout;
import android.view.View;


public class WebBundleViewer extends Activity  {
	 
	 private  String wbKey;
	 private String packagePath;
	 private String filename;
	 private String url;
	 private boolean isOnline;
	 private ProgressDialog MyDialog;

	 //download package again - need 1) the key, and 2) the download path, then you want to call the reader class again and pass in the new key variable after the download
	 //this class also needs a loading dialog while the user waits for the website to load..
	 /*
	  * PROBLEM - this example (http://www.nycgo.com/events/) has the filename = events & url = http://www.nycgo.com/events/
	  * However when you web scrape it you do not get events.html..you get index.html so...perhaps check for filename.html
	  * and if that doesn't exist then try index.html?
	  * In this case..events/ was a directory! and inside the events folder will be the index.html!
	  */
	 
	 @Override
 	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	        
	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.webbundleview);

	           	// get the extras (video filename) out of the new intent
		        Intent intent = getIntent();
		        if(intent != null) filename = intent.getStringExtra("filename");
		        if(intent != null) url = intent.getStringExtra("url");
		        if(intent != null) packagePath = intent.getStringExtra("path");
		        if(intent != null) wbKey = intent.getStringExtra("itemKey");

	    	   
		  
		   //check if the mobile client is online..if it is then we can display the live website
		   OnlineCheck oc = new OnlineCheck();
		   if (oc.isOnline(this)){
			   	 
              //live website
              WebView webView = new WebView(WebBundleViewer.this);
              webView.loadUrl(url);
              webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
              webView.getSettings().setBuiltInZoomControls(true);
              webView.getSettings().setJavaScriptEnabled(true);	//allows the webview to be able to handle javascript in websites
                			   	 
              //loading feedback
              // MyDialog = ProgressDialog.show( WebBundleViewer.this, " " , " Loading Website. Please wait .. ", true);
              setContentView(webView);

	   	 
		   }
		   //else {
			   
			/*   TextView tv1 = new TextView(this);
   			tv1.setText("file://" + "sdcard/PlaceBooks/unzipped" + packagePath + "/" + wbKey + "/" + "www.nycgo.com/" + filename + "/index.html");
   			setContentView(tv1);
			  */ 
				   //otherwise if the mobile client is not online we display the cached webstie from the sdcard     
				  /* if (packagePath != null && filename != null && url != null){
					   
					   File directory = new File(Environment.getExternalStorageDirectory()+ "/sdcard/PlaceBooks/unzipped" + packagePath + "/" + filename);
					   //check if the filename is a directory					   
			    		if(directory.exists()){
			    			//if it is a firectory then try getting the web page inside it
			    			 WebView webView = new WebView(WebBundleViewer.this);
					    	 webView.loadUrl("file://" + "/sdcard/PlaceBooks/unzipped" + packagePath + "/" + wbKey + "/" + "www.nycgo.com/" + filename + "/index.html");
						   	 webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
						   	 webView.getSettings().setBuiltInZoomControls(true);
						   	 webView.getSettings().setJavaScriptEnabled(true);
							 setContentView(webView);
				   	  }*/
			    			
			    		
			    	/*	else{
			    			//if it isn't a directory then the filename is the actual html page
			    			 //cached website
					         WebView webView = new WebView(WebBundleViewer.this);
					    	 webView.loadUrl("file://" + "/sdcard/PlaceBooks/unzipped" + packagePath + "/" + wbKey + "/" + "www.nycgo.com/" + filename + "/index.html");
						   	 webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
						   	 webView.getSettings().setBuiltInZoomControls(true);
						   	 webView.getSettings().setJavaScriptEnabled(true);
							 setContentView(webView);

			    			
			    		}
			    	 */
					   	 //loading feedback
					   //  MyDialog = ProgressDialog.show( WebBundleViewer.this, " " , " Loading Website. Please wait .. ", true);				   	 
					   	

	
				   //}
			   
				  
				   else{
						   TextView tv = new TextView(this);
						   //tv.setText("A problem has occurred when trying to access the website information. Package could be damaged." + "filename =" +  filename + " " +  "url = " + url);		//cannot convert a null to string maybe?
						   tv.setText("currently working on the caching of web sites");	
						   tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
						   setContentView(tv);
						   
						   //try downloading package again? click here..
					   }
		   //}
		   
	 
		         
	 } //end of onCreate()

	 
	 
}
