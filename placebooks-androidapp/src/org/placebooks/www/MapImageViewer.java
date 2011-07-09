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
import android.content.Context;



public class MapImageViewer extends Activity {
	
	private MapCanvas mapCanvas;
	private String mapImage;
	private String packagePath;
	
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
	private int tl_x;		//top-left-x
	private int tl_y;		//top-left-y
	private int tr_x;		//top-right-x
	private int tr_y;		//top-right-y
	private int bl_x;		//bottom-left-x
	private int bl_y;		//bottom-left-y
	private int br_x;		//bottom-right-x
	private int br_y;		//bottom-right-y
	
	//private double lat_test = 52.631111;
	//private double long_test = 1.281111;
	private int lat_test = 300;	//mousehold heath in PIXELS
	private int long_test = 500;

	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen
	        
	        //mapCanvas = new MapCanvas(this);
	        //setContentView(mapCanvas);
	        
	        //setContentView(R.layout.mapimageview);
	        
	    	// get the extras (video filename) out of the new intent
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

	        
	        String myMapImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + mapImage;
	        
	    	
	    	BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 1;
		    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);	    
		    
		    
		    // calculate the pixels in the image
		    imagePixels = (bm.getWidth()) * (bm.getHeight());
		    /* now work out the lat/lon for each corner
		     *  E.G
		     *  
		     *  0,400		600,400			(tl_x, tl_y)	(tr_x, tr_y)
		     * 
		     * 	0,0		    600,0			(bl_x, bl_y)	(br_x, br_y)
		     */
		    tl_x = 0;
		    tl_y = bm.getHeight();
		    bl_x = 0;
		    bl_y = 0;
		    tr_x = bm.getWidth();
		    tr_y = bm.getHeight();
		    br_x = bm.getWidth();
		    br_y = 0;
		    
		    System.out.println("top left x = " + tl_x);
		    System.out.println("top left y = " + tl_y);
		    System.out.println("bottom left x = " + bl_x);
		    System.out.println("bottom left y = " + bl_y);
		    System.out.println("top right x = " + tr_x);
		    System.out.println("top right y = " + tr_y);
		    System.out.println("bottom left x = " + br_x);
		    System.out.println("bottom left y = " + br_y);
		    
		    //now map the lat/longs of the corners to the image pixel corners
		    //need to find the "degrees of a pixel" value - lat/long for top/left and bottom/right of the image
		    //var degreesPerPixelX = bottomX - topX / imageWidth;
		    //var degreesPerPixelY = bottomY - topY / imageHeight;
		    //int degreesPerPixelX = (br_x - tr_x) / bm.getWidth();
		    //int degreesPerPixelY = (br_y = tr_y) / bm.getWidth();
		    
		    float diff_lat_x = br_x - tl_x; //bottom-right_x - top-left_x 
		    float diff_lon_y = br_y - tl_y;  //bottom-right_y - top-left_y
		    
		    	
		    //get the current longtitude and latitude of the mobile device
		    //find best location provider that features high accuracy and draws as little power as possible
			LocationManager locationManager;
		    String context = Context.LOCATION_SERVICE;
		    locationManager = (LocationManager)getSystemService(context);
		    
		    Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_FINE);
		    criteria.setAltitudeRequired(false);
		    criteria.setBearingRequired(false);
		    criteria.setCostAllowed(true);
		    criteria.setPowerRequirement(Criteria.POWER_LOW);
		    String provider = locationManager.getBestProvider(criteria, true);
		    
		    Location location = locationManager.getLastKnownLocation(provider);
		    updateWithNewLocation(location);
		    
		    locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);		    
		    
		    //draw the map on the canvas with the location
	        mapCanvas = new MapCanvas(this, myMapImagePath, lat_test, long_test );	//context, directory (path+filename),  ,pixel(lat), pixel(long), 
		    setContentView(mapCanvas);
		    
	    	//WebView image = new WebView(this);

	        //image.setImageBitmap(bm); 
		    //image.setAdjustViewBounds(true);
		    //image.setScaleType(ScaleType.FIT_CENTER);
		    /*image.loadUrl("file://" + myMapImagePath);
		    
            image.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            image.getSettings().setBuiltInZoomControls(true);
		    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    setContentView(image);
		    */
		    
	        
	} //end of onCreate
	
	
	
	
		private void updateWithNewLocation(Location location){
			
			String latLongString;
			
			if(location!=null){
			    double longitude = location.getLongitude();
			    double latitude = location.getLatitude();
			    latLongString = "Lat: " +latitude + "\nLong: " + longitude;
			}
			else{
				latLongString = "No location found";
			}
			
			Toast msg = Toast.makeText(this, "Your current position is: \n" + latLongString, Toast.LENGTH_LONG);
			msg.show();
		}
		
				
		private final LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location){
				updateWithNewLocation(location);
			}
			
			public void onProviderDisabled(String provider){
				updateWithNewLocation(null);
			}
			
			public void onProviderEnabled(String provider){
				
			}
			public void onStatusChanged(String provider, int status, Bundle extras){
				
			}
		};
		
		//need to set a proximity alert for detecting movement into and out of the map area
		//middle co-ordinate is (tr_x - bl_x) /2 (tr_y - bl_y) / 2
		
		

		
		
}
