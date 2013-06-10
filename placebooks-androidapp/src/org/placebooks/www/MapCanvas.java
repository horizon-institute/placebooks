package org.placebooks.www;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.BitmapFactory;

import android.widget.*;

import java.util.*;



public class MapCanvas extends ImageView {
	
	private Context mContext;
	//The phones current longitude and latitude
	private double longitude;
	private double latitude;

	private int px_lat;
	private int px_lon;
	//Arraylists for pixel lat/lons for the gps trail
	private ArrayList<Integer> gpsLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> gpsLonPx = new ArrayList<Integer>();
	
	
	public MapCanvas(Context c){
		super(c);
		mContext = c.getApplicationContext();
	}

	
	public void setLatitude(double la){
		this.latitude = la;
	}
	
	public void setLongitude(double lo){
		this.longitude = lo;
	}
	
	public void setLat(int lat){
		px_lat = lat;
		//Toast msg = Toast.makeText(mContext, "hey i'm the lat and ive been changed to: \n" + px_lat, Toast.LENGTH_LONG);
		//msg.show();
	}
	public void setLon(int lon){
		px_lon = lon;
	}
	public void setGpsLat(ArrayList<Integer> alLat){
		this.gpsLatPx = alLat;
	}
	public void setGpsLon(ArrayList<Integer> alLon){
		this.gpsLonPx = alLon;
	}		

	
	//doDrawing (draw when changes have been made)
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

        // y increases from top to bottom
        // x increases from left to right
      
		
		Paint yahPen = new Paint();
		yahPen.setStrokeWidth(3);
		yahPen.setColor(0xFF097286);		
		
		Paint trailPen = new Paint(Paint.ANTI_ALIAS_FLAG);
		trailPen.setStyle(Paint.Style.STROKE); 
		//calculate stroke with for current density
		//trailPen.setStrokeWidth(1 /getResources().getDisplayMetrics().density);
		trailPen.setStrokeWidth(6);
		trailPen.setColor(Color.BLUE);	//color.RED 0xffff0000
		
		Paint mediaPen = new Paint();
		mediaPen.setStyle(Paint.Style.STROKE);
		mediaPen.setStrokeWidth(5);
		mediaPen.setColor(Color.RED);

		
		//draw out the gps trail
		for(int i=1; i<gpsLatPx.size(); i++){
			canvas.drawLine(gpsLonPx.get(i), gpsLatPx.get(i), gpsLonPx.get(i-1), gpsLatPx.get(i-1), trailPen);			
		}
		
		
		if(longitude != 0.00 && latitude != 0.00){
			//draw the YAH dot
			//canvas.drawCircle(px_lon, px_lat, 10, yahPen);
			//System.out.println("pixel lon== " +px_lon + "pixel lat== " +px_lat);
			Bitmap yah = BitmapFactory.decodeResource(getResources(), R.drawable.yah_marker_30) ;
			canvas.drawBitmap(yah ,px_lon-20, px_lat-50,  null) ; //marker is 30px and drawn downwards so we need to take this into account

			//-10 and -30 seem to be the best for plotting our marker (taking into account it is 30x30px and how android draws it on the screen
		}
		
		invalidate();	
		
				
	}	//end of onDraw()
	
	
}




	
	
