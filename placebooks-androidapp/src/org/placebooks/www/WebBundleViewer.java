package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebBundleViewer extends Activity {
	
	 private String packagePath;
	 private String filename;
	 private String url;
	
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
	  
	       
	        WebView webView = new WebView(WebBundleViewer.this);
		 	
	    	 webView.loadUrl("file://" + "/sdcard/PlaceBooks/unzipped" + packagePath + "/" + filename + ".html");
		   	 webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		   	 webView.getSettings().setBuiltInZoomControls(true);
		   	 webView.getSettings().setJavaScriptEnabled(true);	//allows the webview to be able to handle javascript in websites
		   	 
		   	 setContentView(webView);
	 }
	
}
