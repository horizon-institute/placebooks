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
	private String myImagePath ;
	private Bitmap bm;
	private ImageView imageView;
	
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle

	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.imageview);
	        
	    	// get the extras (video filename) out of the new intent
	        Intent intent = getIntent();
	       // if(intent != null) image = intent.getStringExtra("image");
	       // if(intent != null) packagePath = intent.getStringExtra("path");
	        if(intent != null) myImagePath = intent.getStringExtra("imagePath");

	        
	        //locate the file path where the images are stored on the SD CARD. 
			//myImagePath = "/sdcard/placebooks/unzipped" + imgPath;// packagePath + "/" + image;
	        
	    	//WebView image = new WebView(this);
			imageView = new ImageView(this);
	        
	        
		    //image.loadUrl("file://" + myImagePath);
            //image.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            //image.getSettings().setBuiltInZoomControls(true);
			//Uri imgUri=Uri.parse(myImagePath);
		    //image.setImageURI(imgUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 1;	//WAS 2 BUT TRYING 1
		    bm = BitmapFactory.decodeFile(myImagePath, options);
		    imageView.setImageBitmap(bm);
		    imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(imageView);
		    
		    
		    imageView.setOnClickListener(new OnClickListener() {
	             @Override
	             public void onClick(View v) {
	            	 
	            	//call the back button method to close the image
	            	 onBackPressed();
    	        
	            	
	             } //end of public void
		 
				}); 
	        
	}
	
	   @Override
       public void onDestroy() {
         super.onDestroy();
          bm.recycle();		//clears the bitmap 
          bm = null;
	      imageView.setImageDrawable(null);    //sets the imageView to null and cleans it
	      imageView = null;
	      packagePath = null;
	      image = null;

           System.gc();	//call the garbage collector
           finish();	//close the activity
         
	   }
	
	 
	
	
}
