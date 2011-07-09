package org.placebooks.www;

import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.graphics.BitmapFactory;
import android.webkit.WebView;
import android.webkit.WebSettings;


public class MapCanvas extends View {
	
	Context mContext;
	String directory;
	int px_lat;
	int px_long;
	
	
	public MapCanvas(Context context, String dir, int px_lat, int px_long){
		super(context);
		mContext = context;
		this.directory = dir;
		this.px_lat = px_lat;
		this.px_long = px_long;		

	}
	

	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 1;
	    Bitmap bm = BitmapFactory.decodeFile(directory, options);
	    
	    
		
		//drawPoint(float x, float y, Paint paint)
		
		// custom drawing code here
        // y increases from top to bottom
        // x increases from left to right
      
		Paint myPaint = new Paint();
		myPaint.setStrokeWidth(3);
		myPaint.setColor(0xFF097286);
		//canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon), 184, 184, null);
		canvas.drawBitmap(bm,0, 0, null);
		canvas.drawCircle(px_lat, px_long, 10, myPaint);
		invalidate();	
		
		
		
  /*      Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        // make the entire canvas white
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
      
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();//start
        height = display.getHeight();//end

      
        xpos = width / 7;
        ypos = height/7;
        for (int i = 0; i < 7; i++) {                  
                   
            paint.setColor(Color.WHITE);
            canvas.drawLine(xpos +(xpos*i), 0, xpos +(xpos*i), height, paint);              
            //canvas.drawLine(startX, startY, stopX, stopY, paint)              

        }                  
         paint.setStyle(Style.STROKE);
            for (int i = 0; i < 7; i++) {                                  
                paint.setColor(Color.WHITE);
                canvas.drawLine(0, (ypos*pass)+ 5, width, (ypos*pass)+5, paint);      
                pass++;
                              
            }               
    	}
	}
	*/	
	}

}




	
	
