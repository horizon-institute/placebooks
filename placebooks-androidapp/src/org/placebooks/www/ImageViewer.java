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
	
	private String myImagePath ;
	private Bitmap bm;
	private ImageView imageView;
	
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	

	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.imageview);
	        
	        Intent intent = getIntent();
	        if(intent != null) myImagePath = intent.getStringExtra("imagePath");

			imageView = new ImageView(this);
	        /*
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 1;	//WAS 2 BUT TRYING 1
		    bm = BitmapFactory.decodeFile(myImagePath, options);
		    imageView.setImageBitmap(bm);
		    imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(imageView);
		    */
			
		    BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			/*bm =*/ BitmapFactory.decodeFile(myImagePath, options);	 
			options.inJustDecodeBounds = false;
			if(options.outWidth>1000){ 
                options.inSampleSize = 4; 
                bm = BitmapFactory.decodeFile(myImagePath, options); 
			} 
			else bm = BitmapFactory.decodeFile(myImagePath, options); 
			
			imageView.setImageBitmap(bm);
		    imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(imageView);
		    
		    
		    imageView.setOnClickListener(new OnClickListener() {
	             @Override
	             public void onClick(View v) {
	            	 
	            	//Call the back button method to close the image
	            	 onBackPressed();
	            	
	             } 
				}); 
	        
	}
	
	   @Override
       public void onDestroy() {
         super.onDestroy();
          bm.recycle();		//Clears the bitmap 
          bm = null;
	      imageView.setImageDrawable(null);    //Sets the imageView to null and cleans it
	      imageView = null;

           System.gc();	//Call the garbage collector
           finish();	//Close the activity
         
	   }
	
	 
	
	
}
