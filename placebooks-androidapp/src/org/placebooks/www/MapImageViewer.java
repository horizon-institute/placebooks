package org.placebooks.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import android.content.res.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.osmdroid.views.MapView;
import org.osmdroid.*;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;


public class MapImageViewer extends Activity {
	
	// The MapView variable:
    private MapView m_mapView;
    // Default map zoom level:
    private int MAP_DEFAULT_ZOOM = 16;
    // Default map Latitude:
    private double MAP_DEFAULT_LATITUDE = 52.95443;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = -1.157684;
    
	private RelativeLayout rl;
	private MapCanvas mapCanvas;
	private String mapImageFilename;
	private String packagePath;
	
	//The phones current longitude and latitude
	private double longitude;
	private double latitude;

    private Bitmap bm;
    //Map image dimensions
    //private int mapHeight;
    //private int mapWidth;
    private int screenHeight;
    private int screenWidth;
    
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
	private ArrayList<Integer> alGeoImageMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoImageMapMarker = new ArrayList<Integer>();
	
	
	//ArrayLists for the lat/lon pixel values for the geotagged media
	private ArrayList<Integer> alGeoImageLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoImageLonPx = new ArrayList<Integer>();
	
	private int mobilePageNumber;
	private int webPageNumber;
	
	private String gpxFilename;
	private String unzippedDir;
	private ArrayList<Double> gpsLatCoordinates = new ArrayList<Double>();
	private ArrayList<Double> gpsLonCoordinates = new ArrayList<Double>();
	
	
	private ArrayList<String> alGeoVideoFilename = new ArrayList<String>();
	private ArrayList<Double> alGeoVideoLat = new ArrayList<Double>();
	private ArrayList<Double> alGeoVideoLon = new ArrayList<Double>();
	private ArrayList<Integer> alGeoVideoMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoVideoMapMarker = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoVideoLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoVideoLonPx = new ArrayList<Integer>();
	
	private ArrayList<String> alGeoTextData = new ArrayList<String>();
	private ArrayList<Double> alGeoTextLat = new ArrayList <Double>();
	private ArrayList<Double> alGeoTextLon = new ArrayList <Double>();
	private ArrayList<Integer> alGeoTextMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoTextMapMarker = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoTextLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoTextLonPx = new ArrayList<Integer>();
	
	private ArrayList<String> alGeoAudioFilename = new ArrayList<String>();
	private ArrayList<Double> alGeoAudioLat = new ArrayList <Double>();
	private ArrayList<Double> alGeoAudioLon = new ArrayList <Double>();
	private ArrayList<Integer> alGeoAudioMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoAudioMapMarker = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoAudioLatPx = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoAudioLonPx = new ArrayList<Integer>();
	
	private String languageSelected;
	
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
//	        if(intent != null) arrGpsLat = intent.getDoubleArrayExtra("arrLat");
//	        if(intent != null) arrGpsLon = intent.getDoubleArrayExtra("arrLon");
	        //Geotagged media
	        if(intent != null) alGeoImageFilename = intent.getStringArrayListExtra("alGeoImageFilename");
	        if(intent != null) alGeoImageMapPage = intent.getIntegerArrayListExtra("alGeoImageMapPage");
	        if(intent != null) alGeoImageMapMarker = intent.getIntegerArrayListExtra("alGeoImageMapMarker");
	        if(intent != null) mobilePageNumber = intent.getIntExtra("pageNumber", mobilePageNumber);
	        System.out.println("al geo image map page === " + alGeoImageMapPage);
	        System.out.println("page number in map viewer ==== " +mobilePageNumber);
	            
	        if(intent != null) gpxFilename = intent.getStringExtra("gpxFilename");
	        CustomApp appState = ((CustomApp)getApplicationContext());
	        unzippedDir = appState.getUnzippedDir();
	        System.out.println("GPX Filename ===== " + gpxFilename);
	        
	        languageSelected  = appState.getLanguage();  
	        Locale locale = new Locale(languageSelected);   
	        Locale.setDefault(locale);  
	        Configuration config = new Configuration();  
	        config.locale = locale;  
	        getBaseContext().getResources().updateConfiguration(config,   
	        getBaseContext().getResources().getDisplayMetrics());  
	        
	       
	        int len = intent.getIntExtra("coord_size", 0);
	        for(int i = 0; i < len; i++)
	        {
	            double x = intent.getDoubleExtra("coord_x_" + i, 0.0);
	            double y = intent.getDoubleExtra("coord_y_" + i, 0.0);
	            alGeoImageLat.add(y);
	            alGeoImageLon.add(x);
	        }
	        
	        //Geotagged video
	        if(intent != null) alGeoVideoFilename = intent.getStringArrayListExtra("alGeoVideoFilename");
	        if(intent != null) alGeoVideoMapPage = intent.getIntegerArrayListExtra("alGeoVideoMapPage");
	        if(intent != null) alGeoVideoMapMarker = intent.getIntegerArrayListExtra("alGeoVideoMapMarker");
	        int vidLen = intent.getIntExtra("vid_coord_size", 0);
	        for(int i = 0; i < vidLen; i++)
	        {
	            double x = intent.getDoubleExtra("vid_coord_x_" + i, 0.0);
	            double y = intent.getDoubleExtra("vid_coord_y_" + i, 0.0);
	            alGeoVideoLat.add(y);
	            alGeoVideoLon.add(x);
	        }
	        //Geotagged text
	        if(intent != null) alGeoTextData = intent.getStringArrayListExtra("alGeoTextData");
	        if(intent != null) alGeoTextMapPage = intent.getIntegerArrayListExtra("alGeoTextMapPage");
	        if(intent != null) alGeoTextMapMarker = intent.getIntegerArrayListExtra("alGeoTextMapMarker");
	        int textLen = intent.getIntExtra("text_coord_size", 0);
	        for(int i = 0; i < textLen; i++)
	        {
	            double x = intent.getDoubleExtra("text_coord_x_" + i, 0.0);
	            double y = intent.getDoubleExtra("text_coord_y_" + i, 0.0);
	            alGeoTextLat.add(y);
	            alGeoTextLon.add(x);
	        }
	        
	      //Geotagged audio
	        if(intent != null) alGeoAudioFilename = intent.getStringArrayListExtra("alGeoAudioFilename");
	        if(intent != null) alGeoAudioMapPage = intent.getIntegerArrayListExtra("alGeoAudioMapPage");
	        if(intent != null) alGeoAudioMapMarker = intent.getIntegerArrayListExtra("alGeoAudioMapMarker");
	        int audioLen = intent.getIntExtra("audio_coord_size", 0);
	        for(int i = 0; i < audioLen; i++)
	        {
	            double x = intent.getDoubleExtra("audio_coord_x_" + i, 0.0);
	            double y = intent.getDoubleExtra("audio_coord_y_" + i, 0.0);
	            alGeoAudioLat.add(y);
	            alGeoAudioLon.add(x);
	        }
	        
	        
	        
	        
	   }
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   
	   
	   try{
       	new RenderMap().execute();	        
       }
       catch(Exception e){
       	e.printStackTrace();
       }
	   		
	        
	       
	} //End of onCreate
	
	
	
		public void unmarshallTrail(){
			//Map Trail
		    //Unmarshall the map trail coordinates
		    if (gpxFilename != null){
		    try {
				Serializer serializer = new Persister(); 
				//Used the simple framework to convert the gpx data from xml file to java objects
				File source = new File(packagePath + File.separator + gpxFilename);
				Gpx gpx = serializer.read(Gpx.class, source);
	
				System.out.println("UNMARSHALLING!!!!!!!");
				
				 //Quick method for the deadline -- will improve later
				 for(int i=0; i<gpx.trk.trkseg.size(); i++){
					
					 String gpxLatLon =  gpx.trk.trkseg.get(i).toString();
					 //Cut the lat and lon out of the string
					 int start = gpxLatLon.indexOf("latitude=");
					 int size = gpxLatLon.length();
					 int middle = gpxLatLon.indexOf("longitude=");
	
					 gpsLatCoordinates.add(Double.parseDouble(gpxLatLon.substring(start+9, middle)));
					 gpsLonCoordinates.add(Double.parseDouble(gpxLatLon.substring(middle+10, size)));
	
				 
				 }
				 arrGpsLat = new double[gpsLatCoordinates.size()];
				 arrGpsLon = new double[gpsLonCoordinates.size()];
				 
				 //Copy the lat/lon arraylists to arrays
				for (int i=0; i<gpsLatCoordinates.size(); i++){
					arrGpsLat[i] = gpsLatCoordinates.get(i);
					System.out.println("arr gps lat coords = " + arrGpsLat[i]);
	
				}
				for (int i=0; i<gpsLonCoordinates.size(); i++){
					arrGpsLon[i] = gpsLonCoordinates.get(i);
					System.out.println("arr gps lon coordinates = " + arrGpsLon[i]);
	
				}
			  //End of the quick method 
				 					 
		     } catch (Exception e) {
		          e.printStackTrace();
		     }
		    } 
		     //End of if gpxFilename != null
			
		}
		
		public void createMap(){
			
			 System.out.println("x= " + c_x1 + " y= " + c_y1);
		        System.out.println("x= " + c_x2 + " y= " + c_y2);
		        System.out.println("x= " + c_x3 + " y= " + c_y3);
		        System.out.println("x= " + c_x4 + " y= " + c_y4);
		        System.out.println("x= " + c_x5 + " y= " + c_y5);
		        
		        myMapImagePath = packagePath + File.separator + mapImageFilename;
		        
		        
		        //LOAD MAP IMAGE UP
		        BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 1;
				options.inJustDecodeBounds = true;
				//bm =
		        BitmapFactory.decodeFile(myMapImagePath, options);	 
				options.inJustDecodeBounds = false;
				if(options.outWidth>1000){ 
	                options.inSampleSize = 2; 
	                bm = BitmapFactory.decodeFile(myMapImagePath, options); 
				} 
				else bm = BitmapFactory.decodeFile(myMapImagePath, options); 
				

			    imageHeight = bm.getHeight();
			    imageWidth = bm.getWidth();
			    System.out.println("map image height ====" +imageHeight);
			    System.out.println("map image width ====" +imageWidth);

		        
		        //Get mobile screen resolution
		        DisplayMetrics dm = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(dm);
		        screenWidth = dm.widthPixels;
		        screenHeight = dm.heightPixels;
		        //If the map image is smaller than the screen's height size then we want to change it to fit 'fullscreen'
		        //and since map images are always perfect squares, we can take the screen height value and assign it to both the
		        //image width and height
		        if (imageHeight < screenHeight){
			    	//set the image size to the new size (screen size)
			    	imageHeight = screenHeight;
			    	imageWidth = screenHeight;
		        }
		        
		        
		        
		        //START
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
		            	Toast msg = Toast.makeText(this, getResources().getString(R.string.gps_tracking), Toast.LENGTH_LONG);
		    			msg.show();
		            }
		            		//InputStream is = new ByteArrayInputStream(myMapImagePath.getBytes("UTF-8"));
						    //Make bitmap of the map image
		/*				    BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 1;
							//bm = BitmapFactory.decodeStream(is, null, options);
							bm = BitmapFactory.decodeFile(myMapImagePath, options);	 
							//File f =  new File(myMapImagePath);
							//bm = decodeFile(f);
						    
						    imageHeight = bm.getHeight();
						    imageWidth = bm.getWidth();
						
		*/				    
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
						    	if(arrGpsLat != null && arrGpsLon != null){
						    		calculateGpsTrail();	//Call the method to calculate the gps trail
						    	}
						    	
							    //System.out.println("Map Width = " + mapWidth);
							   //System.out.println("Map Height = " + mapHeight);
							    //System.out.println("Number of pixels = " + (mapWidth * mapHeight));
							    
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
								    calculateMediaPixelCoordinates("Image");
						    
						        //Now add the geotagged media to the view
								if((alGeoImageLatPx != null && alGeoImageLonPx != null) && alGeoImageFilename != null && alGeoImageMapPage != null){
									
									if(mobilePageNumber == 0 || mobilePageNumber == 1){
										webPageNumber = 0;
									}
									else if(mobilePageNumber % 2 == 0){
										webPageNumber = mobilePageNumber/2;
									}
									else{
										webPageNumber = (mobilePageNumber-1) /2;
									}
									
										for (int i=0; i< alGeoImageMapPage.size(); i++){
									
											if(webPageNumber == alGeoImageMapPage.get(i)){
											//for(int i=0; i<alGeoImageFilename.size(); i++){
												//System.out.println("IMAGE PIXELS (HEIGHT X WIDTH) === " +imageWidth*imageHeight);
												//System.out.println("alGeoImageLatPx == " + alGeoImageLatPx.get(i));
												//System.out.println("alGeoImageLonPx == " + alGeoImageLonPx.get(i));
												  if((alGeoImageLatPx.get(i) <=(imageWidth*imageHeight) && alGeoImageLatPx.get(i) >0) && (alGeoImageLonPx.get(i) <=(imageWidth*imageHeight) && alGeoImageLonPx.get(i) >0 )){
													final int j = i;
													
													//Get mobile screen resolution
											        //DisplayMetrics dm = new DisplayMetrics();
											        //getWindowManager().getDefaultDisplay().getMetrics(dm);
											        //Assign the resolution to the variables
											        //screenWidth = dm.widthPixels;	//320 on the LG phone
											        //screenHeight = dm.heightPixels;	//480 on the LG phone
											        //double w = screenWidth/11;
											        //double h = screenHeight/22.82;
													
													 ImageButton b = new ImageButton(this);
													 b.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
					
													 //Marker and Text
													    	RelativeLayout rLayout = new RelativeLayout(this);
													    	LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
													    	        ,LayoutParams.FILL_PARENT); 
													    	rLayout.setLayoutParams(rlParams);
													 
														 Bitmap bmCam = BitmapFactory.decodeResource(getResources(),R.drawable.marker);  //camera_icon
														 b.setImageBitmap(bmCam);
														 b.setLayoutParams(rlParams);
														 
														 RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
													        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
													    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
													    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
													    	TextView text=new TextView(this); 
													    	text.setText(Integer.toString(alGeoImageMapMarker.get(i))); 
													    	text.setTextColor(Color.BLACK);  
													    	text.setLayoutParams(tParams);
														
														 rLayout.addView(b);
													     rLayout.addView(text);

														 
										//				 RelativeLayout rl2 = new RelativeLayout(this);
														 //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);//32, 20); //Button size , was 35x25 then 32x22
														 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);//32, 20); //Button size , was 35x25 then 32x22
														 layoutParams.setMargins(alGeoImageLonPx.get(i), alGeoImageLatPx.get(i), 0, 50);
												         System.out.println("geo-image pixels are ====  " +alGeoImageLonPx.get(i) + alGeoImageLatPx.get(i));
												         //layoutParams.setMargins(alGeoImageLatPx.get(i), alGeoImageLonPx.get(i), 0, 50);
					
														 
														 rl.addView(/*b*/rLayout, layoutParams);
														 //ll.addView(b, layoutParams);
													 
													//Toast msg = Toast.makeText(MapImageViewer.this,  "icon pos=" + alGeoImageLonPx.get(i).toString() + " " + alGeoImageLatPx.get(i).toString(), Toast.LENGTH_LONG);
													//msg.show();
													 
													//Create an onClick event for the button
													b.setOnClickListener(new OnClickListener() {
											             @Override
											             public void onClick(View v) {
											            	
													        //Vibrator vib = (Vibrator) getSystemService(MapImageViewer.this.VIBRATOR_SERVICE);
													        //vib.vibrate(300); 
											            	 
											            	Intent intent = new Intent();
											            	overridePendingTransition(0, 0);
										     				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							
									        	        	intent.setClassName("org.placebooks.www", "org.placebooks.www.ImageViewer");
									        	        	intent.putExtra("imagePath", packagePath + File.separator + alGeoImageFilename.get(j));
										     				overridePendingTransition(0, 0);
									        	        	startActivity(intent);	
											             } //End of public void
													});
												  }//end of if
											}//end of new if
											//}//end of for
										
										}//end of for alGeoImageMapPage
									}
										
					        }
					        else{
					        	
					        	//No geotagged photos so no need to do anything here
					        	
					        }		
						    
					        
					      //If there are geotagged videos
					        if(alGeoVideoFilename != null && alGeoVideoLat.size() != 0 && alGeoVideoLon.size() != 0){
							        //Call the calculateMediaPixelCoordinates() method to convert the lat/lons to pixel values
					        	System.out.println("AL GEO VIDEO FILENAME = " + alGeoVideoFilename);
					        	System.out.println("AL GEO VIDEO LAT = " + alGeoVideoLat);
					        	System.out.println("AL GEO VIDEO LON = " + alGeoVideoLon);
					        	
								    calculateMediaPixelCoordinates("Video");
						    
						        //Now add the geotagged media to the view
								if((alGeoVideoLatPx != null && alGeoVideoLonPx != null) && alGeoVideoFilename != null && alGeoVideoMapPage != null){
									
									if(mobilePageNumber == 0 || mobilePageNumber == 1){
										webPageNumber = 0;
									}
									else if(mobilePageNumber % 2 == 0){
										webPageNumber = mobilePageNumber/2;
									}
									else{
										webPageNumber = (mobilePageNumber-1) /2;
									}
									
										for (int i=0; i< alGeoVideoMapPage.size(); i++){
									
											if(webPageNumber == alGeoVideoMapPage.get(i)){

												  if((alGeoVideoLatPx.get(i) <=(imageWidth*imageHeight) && alGeoVideoLatPx.get(i) >0) && (alGeoVideoLonPx.get(i) <=(imageWidth*imageHeight) && alGeoVideoLonPx.get(i) >0 )){
													final int j = i;
													/*
													 ImageButton b = new ImageButton(this);
													 b.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
					
													 
														 Bitmap bmCam = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
														 b.setImageBitmap(bmCam);
														 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
														 layoutParams.setMargins(alGeoVideoLonPx.get(i), alGeoVideoLatPx.get(i), 0, 50);
												         System.out.println("geo-video pixels are ====  " +alGeoVideoLonPx.get(i) + alGeoVideoLatPx.get(i));
													 */
													 RelativeLayout rLayout = new RelativeLayout(this);
												     RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT
												     ,LayoutParams.FILL_PARENT); 
												     rlParams.setMargins(alGeoVideoLonPx.get(i), alGeoVideoLatPx.get(i), 0, 50);
												     //rLayout.setLayoutParams(rlParams);
												    	
												     Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
												   	 ImageButton b = new ImageButton(this);
												   	 b.setBackgroundColor(android.R.color.transparent);
												     b.setImageBitmap(bmpMarker);
												     
												     //b.setLayoutParams(rlParams);
													// rLayout.addView(b, rlParams);

												     RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
												        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
												    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
												    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
												    	TextView text2=new TextView(this); 
												    	text2.setText(Integer.toString(alGeoVideoMapMarker.get(i))); 
												    	text2.setTextColor(Color.BLACK);  
												    	text2.setLayoutParams(tParams);
												    	
												    	rLayout.addView(b);
												    	rLayout.addView(text2);
												    	rl.addView(rLayout, rlParams);
														 
												    	//rl.addView(b, layoutParams);
													 													 
													//Create an onClick event for the button
													b.setOnClickListener(new OnClickListener() {
											             @Override
											             public void onClick(View v) {
											            	
													        //Vibrator vib = (Vibrator) getSystemService(MapImageViewer.this.VIBRATOR_SERVICE);
													        //vib.vibrate(300); 
											            	 
											            	Intent intent = new Intent();
											            	overridePendingTransition(0, 0);
										     				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							
									        	        	intent.setClassName("org.placebooks.www", "org.placebooks.www.VideoViewer");
									        	        	System.out.println("VIDEO ==== " +alGeoVideoFilename.get(j));
									        	        	System.out.println("Path ==== " + packagePath);
									        	        	intent.putExtra("video", alGeoVideoFilename.get(j));
									        	        	intent.putExtra("path", packagePath);
										     				overridePendingTransition(0, 0);
									        	        	startActivity(intent);	
											             } //End of public void
													});
												  }//end of if
											}//end of new if
											//}//end of for
										
										}//end of for alGeoImageMapPage
									}
										
					        }
					        else{
					        	
					        	//No geotagged photos so no need to do anything here
					        	
					        }
					        
					      //If there is geotagged TEXT
					        if(alGeoTextData != null && alGeoTextLat.size() != 0 && alGeoTextLon.size() != 0){
							        //Call the calculateMediaPixelCoordinates() method to convert the lat/lons to pixel values
					        	System.out.println("AL GEO TEXT DATA = " + alGeoTextData);
					        	System.out.println("AL GEO TEXT LAT = " + alGeoTextLat);
					        	System.out.println("AL GEO TEXT LON = " + alGeoTextLon);
					        	
								    calculateMediaPixelCoordinates("Text");
						    
						        //Now add the geotagged media to the view
								if((alGeoTextLatPx != null && alGeoTextLonPx != null) && alGeoTextData != null && alGeoTextMapPage != null){
									
									if(mobilePageNumber == 0 || mobilePageNumber == 1){
										webPageNumber = 0;
									}
									else if(mobilePageNumber % 2 == 0){
										webPageNumber = mobilePageNumber/2;
									}
									else{
										webPageNumber = (mobilePageNumber-1) /2;
									}
									
										for (int i=0; i< alGeoTextMapPage.size(); i++){
									
											if(webPageNumber == alGeoTextMapPage.get(i)){

												  if((alGeoTextLatPx.get(i) <=(imageWidth*imageHeight) && alGeoTextLatPx.get(i) >0) && (alGeoTextLonPx.get(i) <=(imageWidth*imageHeight) && alGeoTextLonPx.get(i) >0 )){
													final int j = i;
													/*
													 ImageButton b = new ImageButton(this);
													 b.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
					
													 
														 Bitmap bmCam = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
														 b.setImageBitmap(bmCam);
														 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
														 layoutParams.setMargins(alGeoTextLonPx.get(i), alGeoTextLatPx.get(i), 0, 50);
												         System.out.println("geo-text pixels are ====  " +alGeoTextLonPx.get(i) + alGeoTextLatPx.get(i));
													 */
													
													RelativeLayout rLayout = new RelativeLayout(this);
												     RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT
												     ,LayoutParams.FILL_PARENT); 
												     rlParams.setMargins(alGeoTextLonPx.get(i), alGeoTextLatPx.get(i), 0, 50);
												     //rLayout.setLayoutParams(rlParams);
												    	
												     Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
												   	 ImageButton b = new ImageButton(this);
												   	 b.setBackgroundColor(android.R.color.transparent);
												     b.setImageBitmap(bmpMarker);
												     
												     //b.setLayoutParams(rlParams);
													// rLayout.addView(b, rlParams);

												     
												     RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
												        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
												    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
												    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
												    	TextView text2=new TextView(this); 
												    	text2.setText(Integer.toString(alGeoTextMapMarker.get(i))); 
												    	text2.setTextColor(Color.BLACK);  
												    	text2.setLayoutParams(tParams);
												    	
												    	rLayout.addView(b);
												    	rLayout.addView(text2);
												    	rl.addView(rLayout, rlParams);
														 
														//rl.addView(b, layoutParams);
													 													 
													//Create an onClick event for the button
													b.setOnClickListener(new OnClickListener() {
											             @Override
											             public void onClick(View v) {
											            	
													        //Vibrator vib = (Vibrator) getSystemService(MapImageViewer.this.VIBRATOR_SERVICE);
													        //vib.vibrate(300); 
											            	 
											            	Intent intent = new Intent();
											            	overridePendingTransition(0, 0);
										     				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							
									        	        	intent.setClassName("org.placebooks.www", "org.placebooks.www.TextViewer");
									        	        	System.out.println("TEXT ==== " +alGeoTextData.get(j));
									        	        	intent.putExtra("text", alGeoTextData.get(j));
										     				overridePendingTransition(0, 0);
									        	        	startActivity(intent);	
											             } //End of public void
													});
												  }//end of if
											}//end of new if
											//}//end of for
										
										}//end of for alGeoTextMapPage
									}
										
					        }
					        
					      //If there is geotagged AUDIO
					        if(alGeoAudioFilename != null && alGeoAudioLat.size() != 0 && alGeoAudioLon.size() != 0){

					        	System.out.println("AL GEO AUDIO FILENAME = " + alGeoAudioFilename);
					        	System.out.println("AL GEO AUDIO LAT = " + alGeoAudioLat);
					        	System.out.println("AL GEO AUDIO LON = " + alGeoAudioLon);
					        	
								    calculateMediaPixelCoordinates("Audio");
						    
						        //Now add the geotagged media to the view
								if((alGeoAudioLatPx != null && alGeoAudioLonPx != null) && alGeoAudioFilename != null && alGeoAudioMapPage != null){
									
									if(mobilePageNumber == 0 || mobilePageNumber == 1){
										webPageNumber = 0;
									}
									else if(mobilePageNumber % 2 == 0){
										webPageNumber = mobilePageNumber/2;
									}
									else{
										webPageNumber = (mobilePageNumber-1) /2;
									}
									
										for (int i=0; i< alGeoAudioMapPage.size(); i++){
									
											if(webPageNumber == alGeoAudioMapPage.get(i)){

												  if((alGeoAudioLatPx.get(i) <=(imageWidth*imageHeight) && alGeoAudioLatPx.get(i) >0) && (alGeoAudioLonPx.get(i) <=(imageWidth*imageHeight) && alGeoAudioLonPx.get(i) >0 )){
													final int j = i;
													
											/*	
													ImageButton b = new ImageButton(this);
													 b.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
					
													 
														 Bitmap bmCam = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
														 b.setImageBitmap(bmCam);
														 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
														 layoutParams.setMargins(alGeoAudioLonPx.get(i), alGeoAudioLatPx.get(i), 0, 50);
												         System.out.println("geo-audio pixels are ====  " +alGeoAudioLonPx.get(i) + alGeoAudioLatPx.get(i));
					
														 
														 rl.addView(b, layoutParams);
												*/
													RelativeLayout rLayout = new RelativeLayout(this);
												     RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT
												     ,LayoutParams.FILL_PARENT); 
												     rlParams.setMargins(alGeoAudioLonPx.get(i), alGeoAudioLatPx.get(i), 0, 50);
												     //rLayout.setLayoutParams(rlParams);
												    	
												     Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
												   	 ImageButton b = new ImageButton(this);
												   	 b.setBackgroundColor(android.R.color.transparent);
												     b.setImageBitmap(bmpMarker);
												     
												     //b.setLayoutParams(rlParams);
													// rLayout.addView(b, rlParams);

												     
												     RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
												        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
												    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
												    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
												    	TextView text2=new TextView(this); 
												    	text2.setText(Integer.toString(alGeoAudioMapMarker.get(i))); 
												    	text2.setTextColor(Color.BLACK);  
												    	text2.setLayoutParams(tParams);
												    	
												    	rLayout.addView(b);
												    	rLayout.addView(text2);
												    	rl.addView(rLayout, rlParams);
													
													//Create an onClick event for the button
													b.setOnClickListener(new OnClickListener() {
											             @Override
											             public void onClick(View v) {
											            	
													        //Vibrator vib = (Vibrator) getSystemService(MapImageViewer.this.VIBRATOR_SERVICE);
													        //vib.vibrate(300); 
											            	 
											            	Intent intent = new Intent();
											            	overridePendingTransition(0, 0);
										     				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							
									        	        	intent.setClassName("org.placebooks.www", "org.placebooks.www.AudioViewer");
									        	        	System.out.println("AUDIO ==== " +alGeoAudioFilename.get(j));
									        	        	intent.putExtra("filename", alGeoAudioFilename.get(j));
									        	        	intent.putExtra("path", packagePath);
										     				overridePendingTransition(0, 0);
									        	        	startActivity(intent);	
											             } //End of public void
													});
												  }//end of if
											}//end of new if
											//}//end of for
										
										}//end of for alGeoTextMapPage
									}
										
					        }
					        
					        else{
					        	
					        	//No geotagged text so no need to do anything here
					        	
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
		
		        
		      /*  
		        MapView mapView = new MapView(this, 256);	//tile size of 256px
		        mapView.setClickable(true);
		        mapView.setBuiltInZoomControls(true);	//set zoom in/out
		        mapView.getController().setZoom(10);
		        //mapView.getController().setCenter(new GeoPoint(39.461078, 2.856445));
		        setContentView(mapView);
		        */
		        
		        //OSMDROID VERSION
		        /*	        
		        //Specify the XML layout to use:
		        setContentView(R.layout.mapimageview);

		        // Find the MapView controller in that layout:
		        m_mapView = (MapView) findViewById(R.id.mapview);
		        m_mapView.setTileSource(TileSourceFactory.MAPNIK);   

		        // Setup the mapView controller:
		        m_mapView.setBuiltInZoomControls(true);
		        m_mapView.setMultiTouchControls(true);
		        m_mapView.setClickable(true);
		        m_mapView.setUseDataConnection(false);
		        m_mapView.getController().setZoom(MAP_DEFAULT_ZOOM);
		        m_mapView.getController().setCenter(new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
		         */	        
		        
			
		}
	
	 
		   private class RenderMap extends AsyncTask<Void, Void, Void> {
				
			   ProgressDialog dialog;

			   @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            dialog = new ProgressDialog(MapImageViewer.this);
		            dialog.setMessage(getResources().getString(R.string.rendering_map));
		            dialog.setIndeterminate(true);
		            dialog.setCancelable(false);
		            dialog.show();
		        }
				 
				@Override 
				protected Void doInBackground(Void... strings) {
											
					        try{	
					        	unmarshallTrail();
					        	
				             }
				             catch(Exception e){
				            	 e.printStackTrace();
				            	 dialog.dismiss();
				             }
				             
				    
					return null;
			 	}
			 	
				 
				   @Override
			        protected void onPostExecute(Void unused) {
					   createMap();		
			           dialog.dismiss();
			           
			        }
			 
			
			     }
		   
		   
		   private void updateWithNewLocation(Location location){
				
				String latLongString;
				
				if(location!=null){
					//update the latitude and longitude variables
				    longitude = location.getLongitude();
				    latitude = location.getLatitude();
				    latLongString = "Lat: " +latitude + "\nLong: " + longitude;
		
				}
				else{
					latLongString = getResources().getString(R.string.no_location_found);
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
				Toast msg = Toast.makeText(this, getResources().getString(R.string.gps_location), Toast.LENGTH_LONG);
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
			
			public void calculateMediaPixelCoordinates(String type){
				
				if (type.equalsIgnoreCase("Image")){
					for(int i=0; i<alGeoImageFilename.size(); i++){
						
						System.out.println("al Geo Image filename ===" + alGeoImageFilename.get(i));
						System.out.println("al Geo Image Lon === "+alGeoImageLon.get(i));
						System.out.println("al Geo Image Lat === "+alGeoImageLat.get(i));
	
										
						try{
							
							//lon x-axis
						 	double top = (alGeoImageLon.get(i)-c_x1);
							double bottomp1 = (c_x4-c_x1);
							float bottomp2 = (float)(bottomp1) / (float)imageWidth;
							float answer1 = (float)(top)/(bottomp2);
							alGeoImageLatPx.add(imageHeight - (int)(answer1));
							System.out.println("geoImageLatPx , imageheight=== " + imageHeight + " minus answer 1 which ==" + (int)(answer1));
	
							
							//lat y-axis
							double top2 = (alGeoImageLat.get(i)-c_y1);
							double bottomp3 = (c_y3-c_y1);
							float bottomp4 = (float) bottomp3 / (float)imageHeight;
							float answer2 = (float)(top2) / (bottomp4);
							alGeoImageLonPx.add(imageWidth - (int)(answer2));
							System.out.println("geoImageLonPx , imageWidth=== " + imageWidth + " minus answer 2 which ==" + (int)(answer2));
	
						}
						catch(Exception e){
							e.printStackTrace();
						}				
					 
						
					}
				}
				
				else if (type.equalsIgnoreCase("Video")){
					System.out.println("AL GEO VIDEO FILENAME == " + alGeoVideoFilename);
					for(int i=0; i<alGeoVideoFilename.size(); i++){
						
						System.out.println("al Geo Video filename ===" + alGeoVideoFilename.get(i));
						System.out.println("al Geo Video Lon === "+alGeoVideoLon.get(i));
						System.out.println("al Geo Video Lat === "+alGeoVideoLat.get(i));
	
										
						try{
							
							//lon x-axis
						 	double top = (alGeoVideoLon.get(i)-c_x1);
							double bottomp1 = (c_x4-c_x1);
							float bottomp2 = (float)(bottomp1) / (float)imageWidth;
							float answer1 = (float)(top)/(bottomp2);
							alGeoVideoLatPx.add(imageHeight - (int)(answer1));
							System.out.println("geoVideoLatPx , imageheight=== " + imageHeight + " minus answer 1 which ==" + (int)(answer1));
	
							
							//lat y-axis
							double top2 = (alGeoVideoLat.get(i)-c_y1);
							double bottomp3 = (c_y3-c_y1);
							float bottomp4 = (float) bottomp3 / (float)imageHeight;
							float answer2 = (float)(top2) / (bottomp4);
							alGeoVideoLonPx.add(imageWidth - (int)(answer2));
							System.out.println("geoVideoLonPx , imageWidth=== " + imageWidth + " minus answer 2 which ==" + (int)(answer2));
	
						}
						catch(Exception e){
							e.printStackTrace();
						}				
					 
						
					}
				}
				
				else if (type.equalsIgnoreCase("Text")){
					System.out.println("AL GEO TEXT DATA == " + alGeoTextData);
					for(int i=0; i<alGeoTextData.size(); i++){
						
						System.out.println("al Geo Text data ===" + alGeoTextData.get(i));
						System.out.println("al Geo Text Lon === "+alGeoTextLon.get(i));
						System.out.println("al Geo Text Lat === "+alGeoTextLat.get(i));
	
										
						try{
							
							//lon x-axis
						 	double top = (alGeoTextLon.get(i)-c_x1);
							double bottomp1 = (c_x4-c_x1);
							float bottomp2 = (float)(bottomp1) / (float)imageWidth;
							float answer1 = (float)(top)/(bottomp2);
							alGeoTextLatPx.add(imageHeight - (int)(answer1));
							System.out.println("geoTextLatPx , imageheight=== " + imageHeight + " minus answer 1 which ==" + (int)(answer1));
	
							
							//lat y-axis
							double top2 = (alGeoTextLat.get(i)-c_y1);
							double bottomp3 = (c_y3-c_y1);
							float bottomp4 = (float) bottomp3 / (float)imageHeight;
							float answer2 = (float)(top2) / (bottomp4);
							alGeoTextLonPx.add(imageWidth - (int)(answer2));
							System.out.println("geoTextLonPx , imageWidth=== " + imageWidth + " minus answer 2 which ==" + (int)(answer2));
	
						}
						catch(Exception e){
							e.printStackTrace();
						}				
					 
						
					}
				}
				
				else if (type.equalsIgnoreCase("Audio")){
					System.out.println("AL GEO AUDIO DATA == " + alGeoAudioFilename);
					for(int i=0; i<alGeoAudioFilename.size(); i++){
						
						System.out.println("al Geo Audio filename ===" + alGeoAudioFilename.get(i));
						System.out.println("al Geo Audio Lon === "+alGeoAudioLon.get(i));
						System.out.println("al Geo Audio Lat === "+alGeoAudioLat.get(i));
	
										
						try{
							
							//lon x-axis
						 	double top = (alGeoAudioLon.get(i)-c_x1);
							double bottomp1 = (c_x4-c_x1);
							float bottomp2 = (float)(bottomp1) / (float)imageWidth;
							float answer1 = (float)(top)/(bottomp2);
							alGeoAudioLatPx.add(imageHeight - (int)(answer1));
							System.out.println("geoAudioLatPx , imageheight=== " + imageHeight + " minus answer 1 which ==" + (int)(answer1));
	
							
							//lat y-axis
							double top2 = (alGeoAudioLat.get(i)-c_y1);
							double bottomp3 = (c_y3-c_y1);
							float bottomp4 = (float) bottomp3 / (float)imageHeight;
							float answer2 = (float)(top2) / (bottomp4);
							alGeoAudioLonPx.add(imageWidth - (int)(answer2));
							System.out.println("geoAudioLonPx , imageWidth=== " + imageWidth + " minus answer 2 which ==" + (int)(answer2));
	
						}
						catch(Exception e){
							e.printStackTrace();
						}				
					 
						
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
				 
			   
			   @Override
			   public boolean onKeyDown(int keyCode, KeyEvent event) {
			       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			           Log.d(this.getClass().getName(), "back button pressed");
			          //onDestroy();
			           finish();
			       }
			       return super.onKeyDown(keyCode, event);
			   }
			 
		
}
