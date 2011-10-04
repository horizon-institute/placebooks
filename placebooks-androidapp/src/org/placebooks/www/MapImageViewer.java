package org.placebooks.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;

import android.widget.TextView;
import android.widget.Toast;
import android.location.*;
import android.content.Context;
import android.widget.*;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView.ScaleType;
import android.view.*;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.util.*;
import android.view.View.OnClickListener;
import android.content.Intent;


public class MapImageViewer extends Activity {
    
	private RelativeLayout rl;
	private MapCanvas mapCanvas;
	private String mapImageFilename;
	private String packagePath;
	
	//The phones current longitude and latitude
	private double longitude;
	private double latitude;

    private Bitmap bm;
    //Map image dimensions
    private int mapHeight;
    private int mapWidth;
    
    //Map image pixel equivalence for the lat/lon
    private int pixel_lat;
    private int pixel_lon;
    
    String myMapImagePath; //imagePath+mapImage


    //mapImage lat/lon coordinates for the gps
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
	//mapImage height, width, pixels
	private int imageHeight;
	private int imageWidth;
	//mapImage pixel values
	private int ptl_x;		//top-left-x
	private int ptl_y;		//top-left-y
	private int ptr_x;		//top-right-x
	private int ptr_y;		//top-right-y
	private int pbl_x;		//bottom-left-x
	private int pbl_y;		//bottom-left-y
	private int pbr_x;		//bottom-right-x
	private int pbr_y;		//bottom-right-y
	
	//Arrays for storing the lat/lons of the gps trail
	private double[] arrGpsLat;
	private double[] arrGpsLon;
	//Arraylists for the lat/lon pixls values for the gps trail
	private ArrayList<Integer> alGpsLat = new ArrayList<Integer>();
	private ArrayList<Integer> alGpsLon = new ArrayList<Integer>();
	
	//ArrayLists for geo-tagged media
	private ArrayList<String> alGeoImageFilename = new ArrayList<String>();
	private ArrayList<Double> alGeoImageLat = new ArrayList<Double>();
	private ArrayList<Double> alGeoImageLon = new ArrayList<Double>();
	
	//ArrayLists for the lat/lon pixel values for the geotagged media
	private ArrayList<Integer> alGeoImageLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoImageLonPx = new ArrayList<Integer>();
	
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen
	        
	        
	   try{     
	    	//Get the extras out of the new intent (get all the coordinates for the map corners)
	        Intent intent = getIntent();
	        if(intent != null) mapImageFilename = intent.getStringExtra("mapImageFilename");
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
	        //Gps trail data
	        if(intent != null) arrGpsLat = intent.getDoubleArrayExtra("arrLat");
	        if(intent != null) arrGpsLon = intent.getDoubleArrayExtra("arrLon");
	        //Geotagged media
	        if(intent != null) alGeoImageFilename = intent.getStringArrayListExtra("alGeoImageFilename");
	        
	        int len = intent.getIntExtra("coord_size", 0);
	        for(int i = 0; i < len; i++)
	        {
	            double x = intent.getDoubleExtra("coord_x_" + i, 0.0);
	            double y = intent.getDoubleExtra("coord_y_" + i, 0.0);
	            alGeoImageLat.add(y);
	            alGeoImageLon.add(x);
	        }
	   }
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   		
	        
	        System.out.println("x= " + c_x1 + " y= " + c_y1);
	        System.out.println("x= " + c_x2 + " y= " + c_y2);
	        System.out.println("x= " + c_x3 + " y= " + c_y3);
	        System.out.println("x= " + c_x4 + " y= " + c_y4);
	        System.out.println("x= " + c_x5 + " y= " + c_y5);
	        
	        myMapImagePath = packagePath + File.separator + mapImageFilename;
	        
	        try {
	        	 //Get the mobile's current longitude and latitude
			    //Find best location provider that features high accuracy and draws as little power as possible
				LocationManager locationManager;
			    String context = Context.LOCATION_SERVICE;
			    locationManager = (LocationManager)getSystemService(context);	//Finds your current location
			    
			    Criteria criteria = new Criteria();
			    criteria.setAccuracy(Criteria.ACCURACY_FINE);
			    criteria.setAltitudeRequired(false);
			    criteria.setBearingRequired(false);
			    criteria.setCostAllowed(true);
			    criteria.setPowerRequirement(Criteria.POWER_LOW);
	        	
	            String provider = locationManager.getBestProvider(criteria, true);
	            
	            if (provider != null){
			            	
					    //String provider2 = LocationManager.GPS_PROVIDER;
					    //int time = 10; //milliseconds
					    //int distance = 30;	//meters
					    Location location = locationManager.getLastKnownLocation(provider);
					    //.requestLocationUpdates(provider, time, distance,locationListener);	//Method to get updates whenever the current location changes, using a location listener
					    updateWithNewLocation(location);
					    locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);	
	            }
	            else{
	            	//Alert user that GPS is turned off and they will not be able to see tracking
	            	Toast msg = Toast.makeText(this, "Please turn on GPS for live tracking", Toast.LENGTH_LONG);
	    			msg.show();
	            }
			        	
					    //Make bitmap of the map image
					    BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 1;
						//bm = BitmapFactory.decodeStream(is, null, options);
						bm = BitmapFactory.decodeFile(myMapImagePath, options);	 
				        
					    
					    imageHeight = bm.getHeight();
					    imageWidth = bm.getWidth();
					
					    
					    //Calculate the pixels in the image
					    //int imagePixels = (bm.getWidth()) * (bm.getHeight());
					    
					    /*Now work out the lat/lon for each corner
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
					    
					    	if(provider!= null){
					    		calculatePixelYAHCoordinates();	//Call the method to calculate the pixel equivalence for the YAH coordinates
					    	}
					    	calculateGpsTrail();	//Call the method to calculate the gps trail
					    
						    System.out.println("Map Width = " + mapWidth);
						    System.out.println("Map Height = " + mapHeight);
						    System.out.println("Number of pixels = " + (mapWidth * mapHeight));
						    
						    //Draw the map on the canvas with the location
						    mapCanvas = new MapCanvas(this);
					    	if(provider!= null){
							    mapCanvas.setLongitude(longitude);
							    mapCanvas.setLatitude(latitude);
							    //Set the lat/lon pixel values for the YAH marker
							    mapCanvas.setLat(pixel_lat);
							    mapCanvas.setLon(pixel_lon);
					    	}
						    //Now set the lat/lon pixel values for the trail
						    mapCanvas.setGpsLat(alGpsLat);
						    mapCanvas.setGpsLon(alGpsLon);
					    
					    
					    BitmapDrawable bmd = new BitmapDrawable(bm);
					    mapCanvas.setImageDrawable(bmd);
					     
					    //LinearLayout ll = new LinearLayout(MapImageViewer.this);
					    //ll.addView(mapCanvas, new LinearLayout.LayoutParams(imageWidth, imageHeight));	//Sets our custom image view to the same size as our map image
					    rl = new RelativeLayout(MapImageViewer.this);
					    rl.addView(mapCanvas, new RelativeLayout.LayoutParams(imageWidth, imageHeight));
					    
					    
					    //If there are geotagged images
				        if(alGeoImageFilename != null && alGeoImageLat != null && alGeoImageLon != null){
						        //Call the calculateMediaPixelCoordinates() method to convert the lat/lons to pixel values
							    calculateMediaPixelCoordinates();
					    
					        //Now add the geotagged media to the view
							if(alGeoImageLatPx != null && alGeoImageLonPx != null && alGeoImageFilename != null){
								
									for(int i=0; i<alGeoImageFilename.size(); i++){
										final int j = i;
										
										//Get mobile screen resolution
								        DisplayMetrics dm = new DisplayMetrics();
								        getWindowManager().getDefaultDisplay().getMetrics(dm);
								        //Assign the resolution to the variables
								        //screenWidth = dm.widthPixels;	//320 on the LG phone
								        //screenHeight = dm.heightPixels;	//480 on the LG phone
								        //double w = screenWidth/11;
								        //double h = screenHeight/22.82;
										
										 ImageButton b = new ImageButton(this);
										 b.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
		
										 
											 Bitmap bmCam = BitmapFactory.decodeResource(getResources(),R.drawable.camera_icon); //photo_marker_50x32
											 b.setImageBitmap(bmCam);
											 RelativeLayout rl2 = new RelativeLayout(this);
											 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(32, 20); //Button size , was 35x25 then 32x22
									         layoutParams.setMargins(alGeoImageLonPx.get(i), alGeoImageLatPx.get(i), 0, 50);
									         //layoutParams.setMargins(alGeoImageLatPx.get(i), alGeoImageLonPx.get(i), 0, 50);
		
											 
											 rl.addView(b, layoutParams);
											 //ll.addView(b, layoutParams);
										 
										//Toast msg = Toast.makeText(MapImageViewer.this,  "icon pos=" + alGeoImageLonPx.get(i).toString() + " " + alGeoImageLatPx.get(i).toString(), Toast.LENGTH_LONG);
										//msg.show();
										 
										//Create an onClick event for the button
										b.setOnClickListener(new OnClickListener() {
								             @Override
								             public void onClick(View v) {
								            	
								            	//Vibration to alert users
										        //Get instance of Vibrator from current Context
										        Vibrator vib = (Vibrator) getSystemService(MapImageViewer.this.VIBRATOR_SERVICE);
										        // Vibrate for 300 milliseconds
										        vib.vibrate(300); 
								            	 
								            	Intent intent = new Intent();
								            	overridePendingTransition(0, 0);
							     				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				
						        	        	intent.setClassName("org.placebooks.www", "org.placebooks.www.ImageViewer");
						        	        	intent.putExtra("imagePath", packagePath + File.separator + alGeoImageFilename.get(j));
							     				overridePendingTransition(0, 0);
						        	        	startActivity(intent);	
								             } //End of public void
										});
									}
								}
									
				        }
				        else{
				        	
				        	//No geotagged photos so no need to do anything here
				        	
				        }		
					    
					    
					    ScrollView sv = new ScrollView(MapImageViewer.this);
				        sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));//, LayoutParams.WRAP_CONTENT));
				        sv.addView(rl);
				        
				        HorizontalScrollView hsv = new HorizontalScrollView(MapImageViewer.this);
				        hsv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));//, LayoutParams.WRAP_CONTENT));
				        hsv.addView(sv);
		
				        
				        setContentView(hsv);
				        mapCanvas.invalidate();
	            
			   
		        
	        }
	        catch(Exception e){
	        	
	        	System.out.println("Exception " + e.toString());
				TextView txtView = new TextView(MapImageViewer.this);
				txtView.setText("Exception " +e.toString());
				setContentView(txtView);
	        }
	        catch (OutOfMemoryError E){
	        	System.gc();
	        	
	        	System.out.println("Out of Memory Exception");
				TextView txtView = new TextView(MapImageViewer.this);
				txtView.setText("Error: cannot load image. Out of memory! \nException " +E.toString());
				setContentView(txtView);
	       
	        }
	        
	        
	} //End of onCreate
	
	
	
	
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
			
		//	Toast msg = Toast.makeText(this, "Your current position is: \n" + latLongString, Toast.LENGTH_LONG);
		//	msg.show();

		}
		
		//listens out for changes in gps coordinates		
		private final LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location){
				//update application based on new location
				updateWithNewLocation(location);

				//Update out map canvas with the new latitude and longitude				
				longitude = location.getLongitude();
			    latitude = location.getLatitude();
			    			//do the calculations
						    calculatePixelYAHCoordinates();
						    //set the lat and lon
						    mapCanvas.setLat(pixel_lat);
						    mapCanvas.setLon(pixel_lon);
						    //refresh the onDraw
						    mapCanvas.invalidate();
				
				
			}
			
			public void onProviderDisabled(String provider){
				//Update application if provider disabled
				updateWithNewLocation(null);
			}
			
			public void onProviderEnabled(String provider){
				//Update application if provider enabled
			}
			public void onStatusChanged(String provider, int status, Bundle extras){
				//Update application if provider hardware status changed
			}
		};
		
		// Method for calculating the pixel coordinates of the YAH marker	
		public void calculatePixelYAHCoordinates(){
						
		    // Now that we have obtained the current gps location of the phone,
		    // we now need to do some maths to get the pixel value for this coordinate.
		     
		    //using the formula x^ - x1 / p1 where p=side length/pixel
			//x - longitude y-latitude
			
		if(longitude != 0.0000 && latitude != 0.0000){
			//lat y-axis
			double top = (latitude-c_x1);
			double bottomp1 = (c_x4-c_x1);
			float bottomp2 = (float)(bottomp1) / (float)imageWidth;
			float answer1 = (float)(top)/(bottomp2);
			//pixel_lon = imageHeight - (int)(answer1);
			pixel_lat = imageHeight - (int)(answer1);
			
			System.out.println("TOP: Latitude " +latitude + " - c_x1= " + c_x1);
			System.out.println("BOTTOM: c_x4= " + c_x4 + " -c_x1= " +c_x1 + " /by imgWidth = " + (double)imageWidth);
			System.out.println("c_x4-c_x1 = " + bottomp1);
			
			System.out.println("top answer= " +top + " bottom answer= " + bottomp2);
			System.out.println("lat answer1 (600-) = " +answer1);
			System.out.println("pixel_lon = " +pixel_lon);
			
			//lon x-axis
			double top2 = (longitude-c_y1);
			double bottomp3 = (c_y3-c_y1);
			float bottomp4 = (float) bottomp3 / (float)imageHeight;
			float answer2 = (float)(top2) / (bottomp4);
			//pixel_lat = imageWidth - (int)(answer2);
			pixel_lon = imageWidth - (int)(answer2);
			

		    System.out.println("the map pixel lat==" + pixel_lat);
		    System.out.println("the map pixel lon==" + pixel_lon);
		    System.out.println("latitude of where i am now ==" + latitude);
		    System.out.println("longitude of where i am now ==" + longitude);
		}
		else{
			Toast msg = Toast.makeText(this, "Trying to find your GPS location", Toast.LENGTH_LONG);
			msg.show();
			}
	
		}
		
		
		// Method for calulcating the pixel coordinates for the gpx data trail
		public void calculateGpsTrail(){
			
			//arrGpsLat and arrGpsLon have the same lengths
			//so now iterate through each lat/lon and calculate the pixels
			 for (int i=0; i<arrGpsLat.length; i++){

				 double gpsLat = arrGpsLat[i];
				 double gpsLon = arrGpsLon[i];
				 
				 	//lat y-axis
				 	double top = (gpsLat-c_x1);
					double bottomp1 = (c_x4-c_x1);
					float bottomp2 = (float)(bottomp1) / (float)imageWidth;
					float answer1 = (float)(top)/(bottomp2);
					alGpsLat.add(imageHeight - (int)(answer1));
					
					//lon x-axis
					double top2 = (gpsLon-c_y1);
					double bottomp3 = (c_y3-c_y1);
					float bottomp4 = (float) bottomp3 / (float)imageHeight;
					float answer2 = (float)(top2) / (bottomp4);
					alGpsLon.add(imageWidth - (int)(answer2));
				 
					System.out.println("GPS PIXEL CALCULATION LAT = " +alGpsLat.get(i));
					System.out.println("GPS PIXEL CALCULATION LON = " +alGpsLon.get(i));

		      }
						
			
		}
		
		public void calculateMediaPixelCoordinates(){
			
			
			for(int i=0; i<alGeoImageFilename.size(); i++){
								
				try{
					
					//lon x-axis
				 	double top = (alGeoImageLon.get(i)-c_x1);
					double bottomp1 = (c_x4-c_x1);
					float bottomp2 = (float)(bottomp1) / (float)imageWidth;
					float answer1 = (float)(top)/(bottomp2);
					alGeoImageLatPx.add(imageHeight - (int)(answer1));
					
					//lat y-axis
					double top2 = (alGeoImageLat.get(i)-c_y1);
					double bottomp3 = (c_y3-c_y1);
					float bottomp4 = (float) bottomp3 / (float)imageHeight;
					float answer2 = (float)(top2) / (bottomp4);
					alGeoImageLonPx.add(imageWidth - (int)(answer2));
					
				}
				catch(Exception e){
					e.printStackTrace();
				}				
			 
				
			}
			
		}
	
		
		   @Override
	       public void onDestroy() {
	         super.onDestroy();
	          bm.recycle();		//clears the bitmap 
	          //Important to put this code back in when MapCanvas extends imageView
	          mapCanvas.setImageDrawable(null);    //Sets the custom imageView to null and cleans it
		      imageHeight = 0;
		      imageWidth = 0;
		      alGpsLat = null;
		      alGpsLon = null;
		      alGeoImageFilename = null;
		      alGeoImageLat = null;
		      alGeoImageLon = null;
		      alGeoImageLatPx = null;
		      alGeoImageLonPx = null;
		      
	          System.gc();	//Call the garbage collector
	          finish();	//Close the activity
	         
		   }
		   
		   @Override
		   protected void onResume()
		   {
		     System.gc();
		     super.onResume();
		   }
		   
		   @Override
		   protected void onPause()
		   {
		     super.onPause();
		     System.gc();
		   }
	
		   
		 //Decodes image and scales it to reduce memory consumption
			 private Bitmap decodeFile(File f){
			     try {
			         //Decode image size
			         BitmapFactory.Options o = new BitmapFactory.Options();
			         o.inJustDecodeBounds = true;
			         BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			         //The new size we want to scale to
			         final int REQUIRED_SIZE=70;

			         //Find the correct scale value. It should be the power of 2.
			         int width_tmp=o.outWidth, height_tmp=o.outHeight;
			         int scale=1;
			         while(true){
			             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
			                 break;
			             width_tmp/=2;
			             height_tmp/=2;
			             scale*=2;
			         }

			         //Decode with inSampleSize
			         BitmapFactory.Options o2 = new BitmapFactory.Options();
			         o2.inSampleSize=scale;
			         return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			     } catch (FileNotFoundException e) {}
			     return null;
			 }
			 			
			 
		
}
