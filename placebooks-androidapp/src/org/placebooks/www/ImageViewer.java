package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import java.awt.*;

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
	        
	    	//WebView image = new WebView(this);
			ImageView image = new ImageView(this);
	        
	        
		    //image.loadUrl("file://" + myImagePath);
            //image.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            //image.getSettings().setBuiltInZoomControls(true);
			//Uri imgUri=Uri.parse(myImagePath);
		    //image.setImageURI(imgUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 2;
		    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
		    image.setImageBitmap(bm);
		    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(image);
		    
		    
		    image.setOnClickListener(new OnClickListener() {
	             @Override
	             public void onClick(View v) {
	            	 
	            	//call the back button method to close the image
	            	 onBackPressed();
    	        
	            	
	             } //end of public void
		 
				}); 
	        
	}
	
	 
	
	
}
