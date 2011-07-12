package org.placebooks.www;

import android.view.View;
import android.widget.ScrollView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.graphics.BitmapFactory;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
//import android.view.SurfaceHolder;


public class MapCanvas extends ImageView {
	
	Context mContext;
	String directory;
	int px_lat;
	int px_lon;

	
	// Constructor (context, map image path, pixel for lat, pixel  for long) - these pixels will keep changing in relation to the gps
	public MapCanvas(Context context, String dir, int px_lat, int px_lon){
		super(context);	
		
		mContext = context;
		this.directory = dir;
		this.px_lat = px_lat;
		this.px_lon = px_lon;	
		//Toast msg = Toast.makeText(mContext, "values passed are: \n" + px_lat + "\n and: " + px_lon, Toast.LENGTH_LONG);
		//msg.show();
		
	}
	
	
	public void setLat(int lat){
		px_lat = lat;
		//Toast msg = Toast.makeText(mContext, "hey i'm the lat and ive been changed to: \n" + px_lat, Toast.LENGTH_LONG);
		//msg.show();
	}
	public void setLon(int lon){
		px_lon = lon;
	}
	

	
	//doDrawing (draw when changes have been made)
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
	/*	
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 1;
	    Bitmap bm = BitmapFactory.decodeFile(directory, options);
	 */   
	    
	    
		//drawPoint(float x, float y, Paint paint)
		
		// custom drawing code here
        // y increases from top to bottom
        // x increases from left to right
      
		Paint myPaint = new Paint();
		myPaint.setStrokeWidth(3);
		myPaint.setColor(0xFF097286);
		//canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon), 184, 184, null);
		
		//set canvas size to the size of the map image
		//canvas.drawBitmap(bm,0, 0, null);
		
		canvas.drawCircle(px_lat, px_lon, 10, myPaint);
		//canvas.drawPoint(300, 444, myPaint);
		invalidate();	
		
		
		
	}

}




	
	
