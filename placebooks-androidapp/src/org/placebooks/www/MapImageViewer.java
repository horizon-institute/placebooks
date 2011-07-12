package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import android.widget.Toast;
import android.location.*;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.content.Context;
import android.util.Log;
import android.widget.*;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView.ScaleType;
import android.view.*;

public class MapImageViewer extends Activity {
	
	private ScrollView sv;
	private LinearLayout ll;
	private MapCanvas mapCanvas;
	private String mapImage;
	private String packagePath;

	
	//the phones current longitude and latitude
	double longitude;
	double latitude;
	//t-values for our current position on our map image (scaled)
	double t1;
    double t2;
    //map image dimensions
    int mapHeight;
    int mapWidth;
    
    //map image pixel equivalence for the lat/lon
    int pixel_lat;
    int pixel_lon;
    
    String myMapImagePath; 

	
	//private double[] c;
	private double c_x1;	//lat1
	private double c_y1;	//lon1
	private double c_x2;	//lat2
	private double c_y2;	//lon2
	private double c_x3;	//lat3
	private double c_y3;	//lon3
	private double c_x4;	//lat4
	private double c_y4;	//lon4
	private double c_x5;	//lat5
	private double c_y5;	//lon5
	
	private int imageHeight;
	private int imageWidth;
	private int imagePixels;	//number of pixels in the map image
	private int ptl_x;		//top-left-x
	private int ptl_y;		//top-left-y
	private int ptr_x;		//top-right-x
	private int ptr_y;		//top-right-y
	private int pbl_x;		//bottom-left-x
	private int pbl_y;		//bottom-left-y
	private int pbr_x;		//bottom-right-x
	private int pbr_y;		//bottom-right-y
	
	private double lat_test = 52.631111;
	private double long_test = 1.281111;
	private int plat_test = 300;	//mousehold heath in PIXELS
	private int plong_test = 500;


	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        
	        //mapCanvas = new MapCanvas(this);
	        //setContentView(mapCanvas);
	        
	        //setContentView(R.layout.mapimageview);
	        
	    	// get the extraS out of the new intent (get all the coordinates for the map corners)
	        Intent intent = getIntent();
	        if(intent != null) mapImage = intent.getStringExtra("mapImage");
	        if(intent != null) packagePath = intent.getStringExtra("packagePath");
	        if(intent != null) c_x1 = intent.getDoubleExtra("c_x1", c_x1);
	        if(intent != null) c_y1 = intent.getDoubleExtra("c_y1", c_y1);
	        if(intent != null) c_x2 = intent.getDoubleExtra("c_x2", c_x2);
	        if(intent != null) c_y2 = intent.getDoubleExtra("c_y2", c_y2);
	        if(intent != null) c_x3 = intent.getDoubleExtra("c_x3", c_x3);
	        if(intent != null) c_y3 = intent.getDoubleExtra("c_y3", c_y3);
	        if(intent != null) c_x4 = intent.getDoubleExtra("c_x4", c_x4);
	        if(intent != null) c_y4 = intent.getDoubleExtra("c_y4", c_y4);
	        if(intent != null) c_x5 = intent.getDoubleExtra("c_x5", c_x5);
	        if(intent != null) c_y5 = intent.getDoubleExtra("c_y5", c_y5);
	        
	        
	        System.out.println("x= " + c_x1 + " y= " + c_y1);
	        System.out.println("x= " + c_x2 + " y= " + c_y2);
	        System.out.println("x= " + c_x3 + " y= " + c_y3);
	        System.out.println("x= " + c_x4 + " y= " + c_y4);
	        System.out.println("x= " + c_x5 + " y= " + c_y5);

	        
	        myMapImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + mapImage;
	        
	    	//make bitmap of the map image
	    	BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 1;
		    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);	    
		    
		    
		    imageHeight = bm.getHeight();
		    imageWidth = bm.getWidth();
		    
		    // calculate the pixels in the image
		    imagePixels = (bm.getWidth()) * (bm.getHeight());
		    /* now work out the lat/lon for each corner
		     *  E.G
		     *  
		     *  0,400		600,400			(tl_x, tl_y)	(tr_x, tr_y)
		     * 
		     * 	0,0		    600,0			(bl_x, bl_y)	(br_x, br_y)
		     */
		    ptl_x = 0;
		    ptl_y = bm.getHeight();
		    pbl_x = 0;
		    pbl_y = 0;
		    ptr_x = bm.getWidth();
		    ptr_y = bm.getHeight();
		    pbr_x = bm.getWidth();
		    pbr_y = 0;
		    
		    System.out.println("pixel top left x = " + ptl_x);
		    System.out.println("pixel top left y = " + ptl_y);
		    System.out.println("pixel bottom left x = " + pbl_x);
		    System.out.println("pixel bottom left y = " + pbl_y);
		    System.out.println("pixel top right x = " + ptr_x);
		    System.out.println("pixel top right y = " + ptr_y);
		    System.out.println("pixel bottom left x = " + pbr_x);
		    System.out.println("pixel bottom left y = " + pbr_y);
		    
		    
		    
		    	
		    //Now get the mobile's current longitude and latitude
		    //find best location provider that features high accuracy and draws as little power as possible
			LocationManager locationManager;
		    String context = Context.LOCATION_SERVICE;
		    locationManager = (LocationManager)getSystemService(context);	//finds your current location
		    
		    Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_FINE);
		    criteria.setAltitudeRequired(false);
		    criteria.setBearingRequired(false);
		    criteria.setCostAllowed(true);
		    criteria.setPowerRequirement(Criteria.POWER_LOW);
			    
		    String provider = locationManager.getBestProvider(criteria, true);
		    Location location = locationManager.getLastKnownLocation(provider);
		    updateWithNewLocation(location);
		    //locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);	
		    
		    String provider2 = LocationManager.GPS_PROVIDER;
		    int time = 10; //milliseconds
		    int distance = 30;	//meters
		    
		    locationManager.requestLocationUpdates(provider2, time, distance,locationListener);	//method to get updates whenever the current location changes, using a location listener
		    
		    
		    calculatePixelCoordinates();	//call the method to calculate the pixel equivalence for the coordinates

		    
		    
		    //draw the map on the canvas with the location
		    mapCanvas = new MapCanvas(this, myMapImagePath, pixel_lat, pixel_lon);	//context, directory (path+filename),  ,pixel(lat), pixel(long), 
		    
		    BitmapDrawable bmd = new BitmapDrawable(bm);
		    
		    mapCanvas.setImageDrawable(bmd);
	        mapCanvas.setScaleType(ScaleType.CENTER);	//needs to focus on the yah dot


        
		    ll = new LinearLayout(this);
		    
		    ll.addView(mapCanvas, new LinearLayout.LayoutParams(imageWidth, imageHeight));	//sets our custom image view to the same size as our map image
//		    ll.addView(mapCanvas, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));//imageWidth, imageHeight));	//sets our custom image view to the same size as our map image
	    
		    
	
		    
		    ScrollView sv = new ScrollView(this);
	        sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        
		    
	        sv.addView(ll);
	        
	        HorizontalScrollView hsv = new HorizontalScrollView(this);
	        hsv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        hsv.addView(sv);
		    
	        setContentView(hsv);
	        mapCanvas.invalidate();

		   // setContentView(mapCanvas);
	    
	        
	} //end of onCreate
	
	
	
	
		private void updateWithNewLocation(Location location){
			
			String latLongString;
			
			if(location!=null){
				//update the latitude and longitude variables
			    longitude = location.getLongitude();
			    latitude = location.getLatitude();
			    latLongString = "Lat: " +latitude + "\nLong: " + longitude;
	
			}
			else{
				latLongString = "No location found";
			}
			
			//Toast msg = Toast.makeText(this, "Your current position is: \n" + latLongString, Toast.LENGTH_LONG);
			//msg.show();

		}
		
		//listens out for changes in gps coordinates		
		private final LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location){
				//update application based on new location
				//updateWithNewLocation(location);

				//Update out map canvas with the new latitude and longitude				
				longitude = location.getLongitude();
			    latitude = location.getLatitude();
			    			//do the calculations
						    calculatePixelCoordinates();
						    //set the lat and lon
						    mapCanvas.setLat(pixel_lat);
						    mapCanvas.setLon(pixel_lon);
						    //refresh the onDraw
						    mapCanvas.invalidate();
				
				
			}
			
			public void onProviderDisabled(String provider){
				//update application if provider disabled
				updateWithNewLocation(null);
			}
			
			public void onProviderEnabled(String provider){
				//update application if provider enabled
			}
			public void onStatusChanged(String provider, int status, Bundle extras){
				//update application if provider hardware status changed
			}
		};
		
		
		public void calculatePixelCoordinates(){
			
				        
	    	//make bitmap of the map image
	    	BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 1;
		    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);	 
			 /*
		     * 
		     * Now that we have obtained the current gps location of the phone,
		     * we now need to do some maths to get the pixel value for this coordinate.
		     * 
		     */
		    //using the formula x(t) = m.t+d      and      y(t) = k.t + c
		    
		    //starting with the bottom right corner of the map image (the first point)
		    double m = c_x1 - c_x4;	//latitude of bottom right corner		c_x4 (is our 0).......c_x1 (is our 1)
		    //x(t) = c_x4 + m.t
		    double k = c_y2 - c_y1;	//longitude of bottom right corner		c_y4 (is our 0).......c_y1 (is our 1)
		    //y(t) = c_y4 + k.t
		    
		    //now substitute in our x coordinate (so this is the latitude of our current gps position
		    //latitude = c_x4 + m.t			<--rearrange our equation and make t the subject
		    t1 = (latitude - c_x4) / m;//latitude;		//x-value
		    
		    //now do the same for the y coordinate (so this is our longitude of our current gps position
		    //longitude = c_y4 + k.t
		    t2 = (longitude - c_y4) / k;//longitude;	//y-value
		    
		    //now we need to know the map image dimensions (height and width in pixels)
		    mapHeight = bm.getHeight();
		    mapWidth = bm.getWidth();
		    
		    //x(t) = mapHeight * t1
		    //y(t) = mapWidth * t2
		    // also cast the answer to int
		    pixel_lat = (int)(mapWidth * t1);
		    pixel_lon = (int)(mapHeight * t2);
			
		    // Toast msg = Toast.makeText(this, "Pixel value for latitude is: \n" + pixel_lat + "\n pixel value for longitude is: \n" + pixel_lon, Toast.LENGTH_LONG);
		    //Toast msg = Toast.makeText(this, "t1 =  \n" + t1 + "\n t2 = \n" + t2, Toast.LENGTH_LONG);
			//msg.show();
			
		}
		
		
		
}
