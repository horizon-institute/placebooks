package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;

public class ImageViewer extends Activity {
	
	private String image;
	private String packagePath;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle

	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.imageview);
	        
	    	// get the extras (video filename) out of the new intent
	        Intent intent = getIntent();
	        if(intent != null) image = intent.getStringExtra("image");
	        if(intent != null) packagePath = intent.getStringExtra("path");

	        
	      //locate the file path where the images are stored on the SD CARD. 
			String myImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + image;
	        
	    	WebView image = new WebView(this);

	        
	        //image.setImageBitmap(bm); 
		    //image.setAdjustViewBounds(true);
		    //image.setScaleType(ScaleType.FIT_CENTER);
		    image.loadUrl("file://" + myImagePath);
            image.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            image.getSettings().setBuiltInZoomControls(true);
		    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(image);
	        
	}
	
	
	
	
}
