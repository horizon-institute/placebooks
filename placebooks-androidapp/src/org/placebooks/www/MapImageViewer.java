package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;

public class MapImageViewer extends Activity {
	
	private String mapImage;
	private String packagePath;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle

	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.mapimageview);
	        
	    	// get the extras (video filename) out of the new intent
	        Intent intent = getIntent();
	        if(intent != null) mapImage = intent.getStringExtra("mapImage");
	        if(intent != null) packagePath = intent.getStringExtra("packagePath");

	        
	      //locate the file path where the images are stored on the SD CARD. 
			String myMapImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + mapImage;
	        
	    	WebView image = new WebView(this);

	        
	        //image.setImageBitmap(bm); 
		    //image.setAdjustViewBounds(true);
		    //image.setScaleType(ScaleType.FIT_CENTER);
		    image.loadUrl("file://" + myMapImagePath);
            image.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            image.getSettings().setBuiltInZoomControls(true);
		    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(image);
	        
	}
	
	
}
