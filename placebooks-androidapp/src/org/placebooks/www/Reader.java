package org.placebooks.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
//import 	java.io.File;
//import 	android.os.Environment;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//import java.util.ArrayList;
//import java.io.InputStreamReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.placebooks.www.R;
import org.placebooks.www.XMLHandler;
import org.placebooks.www.Book;
//import java.util.Iterator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;


import android.widget.TextView;
import android.widget.ImageView;	//for images and for the map (i guess) since the map is now going to be just an image
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.ImageButton;
import android.widget.Button;


import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import java.util.*;	
//import android.net.Uri;
import android.media.MediaPlayer;
//import android.util.Log;
//import android.app.Dialog;
import android.widget.Gallery;
import android.provider.MediaStore;
import android.view.View.OnClickListener; 
import android.view.Gravity;
import android.widget.Toast;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation.AnimationListener;
import android.text.Html;
import android.webkit.WebView;
import android.webkit.WebSettings;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import 	android.net.Uri;
import java.io.InputStream;
import java.io.FileNotFoundException;
import android.os.Parcelable;


//Implement a Listener (added the interface to the base class)
public class Reader extends Activity {//implements Parcelable {
	
	
	//TextView to display error if placebook doesn't display properly	
	private TextView orgXmlTxt;		
	
	//ScrollView and LinearLayout
	private ScrollView sv;		//scroll view that wraps the linear layout
	private ScrollView sv2;		//scroll view for page 2
	private ScrollView sv3;		//scroll view for page 3
	private ScrollView sv4;		//scroll view for page 4
	private ScrollView sv5;		//scroll view for page 5
	private ScrollView sv6;		//scroll view for page 6
	private LinearLayout ll;	//main linear layout page 1
	private LinearLayout ll2;	//page 2
	private LinearLayout ll3;	//page 3
	private LinearLayout ll4;	//page 4
	private LinearLayout ll5;	//page 5
	private LinearLayout ll6;	//page 6
	private LinearLayout llAudio;	//audio layout
	
	//Page flipper and swipe gesture constants
	private ViewFlipper flipper;	//flipper for the swipe navigation
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector; 
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	View.OnTouchListener gestureListener;

	
	//Variables for placebook key (the id of the book) and the User's Name (unsure if it is staying)
	private String pbkey;	//this is the placebook key that defines the ID for each placebook. The contents of each placebook is stored in the folder "key" e.g folder /1234567/contents in here
	//private String uName;	//this is the users actual name that they used to register with placebooks. Note - this is not their email address.
    private String packagePath;
	
	
	ArrayList<Point> page1 = new ArrayList<Point>();
	ArrayList<Point> page2 = new ArrayList<Point>();
	ArrayList<Point> page3 = new ArrayList<Point>();
	ArrayList<Point> page4 = new ArrayList<Point>();	//added 3 new pages
	ArrayList<Point> page5 = new ArrayList<Point>();
	ArrayList<Point> page6 = new ArrayList<Point>();
	
	ArrayList<String> page1Type = new ArrayList<String>();
	ArrayList<String> page2Type = new ArrayList<String>();
	ArrayList<String> page3Type = new ArrayList<String>();
	ArrayList<String> page4Type = new ArrayList<String>();
	ArrayList<String> page5Type = new ArrayList<String>();
	ArrayList<String> page6Type = new ArrayList<String>();
	
	ArrayList<String> page1Data = new ArrayList<String>();
	ArrayList<String> page2Data = new ArrayList<String>();
	ArrayList<String> page3Data = new ArrayList<String>();
	ArrayList<String> page4Data = new ArrayList<String>();
	ArrayList<String> page5Data = new ArrayList<String>();
	ArrayList<String> page6Data = new ArrayList<String>();
	
	ArrayList<String> page1Url = new ArrayList<String>();
	ArrayList<String> page2Url = new ArrayList<String>();
	ArrayList<String> page3Url = new ArrayList<String>();
	ArrayList<String> page4Url = new ArrayList<String>();
	ArrayList<String> page5Url = new ArrayList<String>();
	ArrayList<String> page6Url = new ArrayList<String>();
	
	ArrayList<String> page1Keys = new ArrayList<String>();
	ArrayList<String> page2Keys = new ArrayList<String>();
	ArrayList<String> page3Keys = new ArrayList<String>();
	ArrayList<String> page4Keys = new ArrayList<String>();
	ArrayList<String> page5Keys = new ArrayList<String>();
	ArrayList<String> page6Keys = new ArrayList<String>();
	
	ArrayList<Coordinate[]> page1Geometries = new ArrayList<Coordinate[]>();
	ArrayList<Coordinate[]> page2Geometries = new ArrayList<Coordinate[]>();
	ArrayList<Coordinate[]> page3Geometries = new ArrayList<Coordinate[]>();
	ArrayList<Coordinate[]> page4Geometries = new ArrayList<Coordinate[]>();
	ArrayList<Coordinate[]> page5Geometries = new ArrayList<Coordinate[]>();
	ArrayList<Coordinate[]> page6Geometries = new ArrayList<Coordinate[]>();
	

	

	//Image Variables
	private ImageView imgView;
	
	//Video Variables
	private VideoView video;
	private MediaController ctlr;
	private ImageButton ibVid;
	
	//Audio Variables
	private MediaPlayer mp = new MediaPlayer();
	private ImageButton ibAudioPlay; 
	private ImageButton ibAudioPause;
	private ImageButton ibAudioStop;
	private boolean audio_included = false;	//audio flag
	
	//Map Image Variables
	private ImageView mapImgView;
	private double c_x1;
	private double c_y1;
	private double c_x2;
	private double c_y2;
	private double c_x3;
	private double c_y3;
	private double c_x4;
	private double c_y4;
	private double c_x5;
	private double c_y5;
	

	 		 @Override
	     	 public void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);	//icicle
			        getWindow().setFormat(PixelFormat.TRANSLUCENT);
			        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen

			        /*
			         * get the extras (package path) out of the new intent
			         * retrieve the packagePath.
			         */
			        Intent intent = getIntent();
			        if(intent != null) packagePath = intent.getStringExtra("packagePath");
			        
			        	
			        //set the content view to the xml reader file
			        setContentView(R.layout.reader);
			        flipper=(ViewFlipper)findViewById(R.id.flipper); 
			        sv = (ScrollView)findViewById(R.id.scroller);
			        ll = (LinearLayout)findViewById(R.id.linearLayout);
			        sv2 = (ScrollView)findViewById(R.id.scroller2);
			        ll2 = (LinearLayout)findViewById(R.id.linearLayout2);
			        sv3 = (ScrollView)findViewById(R.id.scroller3);	          
			        ll3 = (LinearLayout)findViewById(R.id.linearLayout3);
			        sv4 = (ScrollView)findViewById(R.id.scroller4);
			        ll4 = (LinearLayout)findViewById(R.id.linearLayout4);
			        sv5 = (ScrollView)findViewById(R.id.scroller5);
			        ll5 = (LinearLayout)findViewById(R.id.linearLayout5);
			        sv6 = (ScrollView)findViewById(R.id.scroller6);
			        ll6 = (LinearLayout)findViewById(R.id.linearLayout6); 
			        
			        
			        gestureDetector = new GestureDetector(new MyGestureDetector());
			        gestureListener = new View.OnTouchListener() {
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        };
			        
			        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
			        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
			        slideLeftIn.setDuration(350);

			        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
			        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

			        slideRightIn.setDuration(350);
			        slideRightOut.setDuration(400);
			        
			        sv.setOnTouchListener(gestureListener);
			        sv2.setOnTouchListener(gestureListener);
			        sv3.setOnTouchListener(gestureListener);
			        sv4.setOnTouchListener(gestureListener);
			        sv5.setOnTouchListener(gestureListener);
			        sv6.setOnTouchListener(gestureListener);
			        
					      	
			        
			        //System.out.println("reader coordinates = " + arr);
				    //Log.v("MyActivity", "page 1coordinates =" + arr );
			        
			        try {
				        getMyXML();		//call method to parse XML		
				        
					        /*for (int i = 0; i<coordinates.size(); i++){
						         TextView tv = new TextView(this);
								 //tv.setText(coordinates.get(i).toString());
						         System.out.println(coordinates.get(i));
								 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
								 ll.addView(tv);
					        	
					        }*/
						  
						  for (int i=0;i<page1Data.size();i++ ){
							 
							  
							 if(page1Type.get(i).toString().equalsIgnoreCase("Text")){
								 
								 displayText(page1Data.get(i).toString(), ll);		//display text (stored in next element of array) on page 1 (ll)
								
							 }
							 else if (page1Type.get(i).toString().equalsIgnoreCase("Image")){
								 displayImage(page1Data.get(i).toString(), ll);
								 
							 }
							 else if (page1Type.get(i).toString().equalsIgnoreCase("Video")){
								 displayVideo(page1Data.get(i).toString(), ll);		
							 }
							 else if (page1Type.get(i).toString().equalsIgnoreCase("Audio")){
								 displayAudio(page1Data.get(i).toString(), ll);	
								 
							 }
							 else if(page1Type.get(i).toString().equalsIgnoreCase("MapImage")){
								 //displayMapImage(page1Data.get(i).toString(), page1Geometries.get(i), ll);
								 displayMapImage(page1Data.get(i).toString(), ll);
								 
								 //Coordinate[] arrCo = page1Geometries.get(i);
							     //System.out.println("page1Coordinates x value= " + arrCo[0].x);
							     //System.out.println("page1Coordinates y value= " + arrCo[0].y);


								/* TextView tv = new TextView(this);
								 tv.setText(page1Data.get(i).toString());
								 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
								 ll.addView(tv);
								 */
							 }
							 else if (page1Type.get(i).toString().equalsIgnoreCase("WebBundle")){
							      displayWebBundle(page1Data.get(i),page1Url.get(i), page1Keys.get(i), ll ); //filename, url, page
							 }
							 
						  	 
						//	  TextView tv = new TextView(this);
						//	  tv.setText(page1Types.get(i).toString());
						//	  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
						//	  ll.addView(tv);
							 
							 
						  } 
						  		  
						  for (int i=0;i<page2Data.size();i++ ){
							  
							  if(page2Type.get(i).toString().equalsIgnoreCase("TEXT")){
									 displayText(page2Data.get(i).toString(), ll2);		//display text (stored in next element of array) on page 2 (ll2)
								 }
								else if (page2Type.get(i).toString().equalsIgnoreCase("IMAGE")){
									 displayImage(page2Data.get(i).toString(), ll2);
									
								 }
								 else if (page2Type.get(i).toString().equalsIgnoreCase("VIDEO")){
									 displayVideo(page2Data.get(i).toString(), ll2);	
									 TextView tv = new TextView(this);
									 tv.setText("page 2 data = " + page2Data.get(i));
								     tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
									 ll2.addView(tv);
								 }
								 else if (page2Type.get(i).toString().equalsIgnoreCase("AUDIO")){
									 displayAudio(page2Data.get(i).toString(), ll2);	
								 }
								 else if(page2Type.get(i).toString().equalsIgnoreCase("MapImage")){
									 //displayMapImage(page2Data.get(i).toString(), page2Geometries.get(i), ll2);
									 displayMapImage(page2Data.get(i).toString(), ll2);
								 }
								 else if (page2Type.get(i).toString().equalsIgnoreCase("WebBundle")){
								      displayWebBundle(page2Data.get(i),page2Url.get(i), page2Keys.get(i), ll2 ); //filename, url, page
								 }
							  
							  
						//	  TextView tv = new TextView(this);
						//	  tv.setText(page2Types.get(i));
						//	  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
						//	  ll2.addView(tv);
							  
						  }
				         for (int i=0;i<page3Data.size();i++ ){
							 
							  if(page3Type.get(i).toString().equalsIgnoreCase("TEXT")){
									 displayText(page3Data.get(i).toString(), ll3);		//display text (stored in next element of array) on page 3 (ll3)
								 }
								 else if (page3Type.get(i).toString().equalsIgnoreCase("IMAGE")){
									 displayImage(page3Data.get(i).toString(), ll3);
								 }
								 else if (page3Type.get(i).toString().equalsIgnoreCase("VIDEO")){
									 displayVideo(page3Data.get(i).toString(), ll3);		
								 }
								 else if (page3Type.get(i).toString().equalsIgnoreCase("AUDIO")){
									 displayAudio(page3Data.get(i).toString(), ll3);	
								 }
								 else if(page3Type.get(i).toString().equalsIgnoreCase("MapImage")){
									 //displayMapImage(page3Data.get(i).toString(),page3Geometries.get(i), ll3);
									displayMapImage(page3Data.get(i).toString(), ll3);
								 }
								 else if (page3Type.get(i).toString().equalsIgnoreCase("WebBundle")){
								      displayWebBundle(page3Data.get(i),page3Url.get(i), page3Keys.get(i), ll3 ); //filename, url, page
								 }
							  
							  
						//	  TextView tv = new TextView(this);
						//	  tv.setText(page3Items.get(i));
						//	  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
					   //     ll3.addView(tv);
						  }
				         for (int i=0;i<page4Data.size();i++ ){
							 
							  if(page4Type.get(i).toString().equalsIgnoreCase("TEXT")){
									 displayText(page4Data.get(i).toString(), ll4);		//display text (stored in next element of array) on page 3 (ll3)
								 }
								 else if (page4Type.get(i).toString().equalsIgnoreCase("IMAGE")){
									 displayImage(page4Data.get(i).toString(), ll4);
								 }
								 else if (page4Type.get(i).toString().equalsIgnoreCase("VIDEO")){
									 displayVideo(page4Data.get(i).toString(), ll4);		
								 }
								 else if (page4Type.get(i).toString().equalsIgnoreCase("AUDIO")){
									 displayAudio(page4Data.get(i).toString(), ll4);	
								 }
								 else if(page4Type.get(i).toString().equalsIgnoreCase("MapImage")){
									// displayMapImage(page4Data.get(i).toString(), page4Geometries.get(i), ll4);
									 displayMapImage(page4Data.get(i).toString(), ll4);
		
								 }
								 else if (page4Type.get(i).toString().equalsIgnoreCase("WebBundle")){
								      displayWebBundle(page4Data.get(i),page4Url.get(i), page4Keys.get(i), ll4 ); //filename, url, page
								 }
				         }
				         
				         for (int i=0;i<page5Data.size();i++ ){
							 
							  if(page5Type.get(i).toString().equalsIgnoreCase("TEXT")){
									 displayText(page5Data.get(i).toString(), ll5);		//display text (stored in next element of array) on page 3 (ll3)
								 }
								 else if (page5Type.get(i).toString().equalsIgnoreCase("IMAGE")){
									 displayImage(page5Data.get(i).toString(), ll5);
								 }
								 else if (page5Type.get(i).toString().equalsIgnoreCase("VIDEO")){
									 displayVideo(page5Data.get(i).toString(), ll5);		
								 }
								 else if (page5Type.get(i).toString().equalsIgnoreCase("AUDIO")){
									 displayAudio(page5Data.get(i).toString(), ll5);	
								 }
								 else if(page5Type.get(i).toString().equalsIgnoreCase("MapImage")){
									 //displayMapImage(page5Data.get(i).toString(), page5Geometries.get(i), ll5);
									 displayMapImage(page5Data.get(i).toString(), ll5);
		
								 }
								 else if (page5Type.get(i).toString().equalsIgnoreCase("WebBundle")){
								      displayWebBundle(page5Data.get(i),page5Url.get(i), page5Keys.get(i), ll5 ); //filename, url, page
								 }
				         }
				         
				         for (int i=0;i<page6Data.size();i++ ){
							 
							  if(page6Type.get(i).toString().equalsIgnoreCase("TEXT")){
									 displayText(page6Data.get(i).toString(), ll6);		//display text (stored in next element of array) on page 3 (ll3)
								 }
								 else if (page6Type.get(i).toString().equalsIgnoreCase("IMAGE")){
									 displayImage(page6Data.get(i).toString(), ll6);
								 }
								 else if (page6Type.get(i).toString().equalsIgnoreCase("VIDEO")){
									 displayVideo(page6Data.get(i).toString(), ll6);		
								 }
								 else if (page6Type.get(i).toString().equalsIgnoreCase("AUDIO")){
									 displayAudio(page6Data.get(i).toString(), ll6);	
								 }
								 else if(page6Type.get(i).toString().equalsIgnoreCase("MapImage")){
									 //displayMapImage(page6Data.get(i).toString(),page6Geometries.get(i), ll6);
									 displayMapImage(page6Data.get(i).toString(),ll6);
								 }
								 else if (page6Type.get(i).toString().equalsIgnoreCase("WebBundle")){
								      displayWebBundle(page6Data.get(i),page6Url.get(i), page6Keys.get(i), ll6 ); //filename, url, page
								 }
				         }
					 

					} catch (Exception e) {
						
						orgXmlTxt.setText(e.getMessage());
					} 
					
								
					//set up action listeners for the scroll views
					sv.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					
					sv2.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					
					sv3.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });

					
			
	 		 } //end of onCreate() Method
	 		 
	 		 
	 		
	 
	 		 /*
	 		  * Method for displaying the Image Items
	 		  */
			 private void displayImage(final String img, final LinearLayout page){
				 				 
				 /*
				  * Dynamically creating 'x' amount of image views for each image in the placebook 
				  * -- need to work on ordering next -- e.g could have img, txt, img, whereas right now it will always display the list of all images one after each other
				  */
				 
					imgView = new ImageView(this);
				    //locate the file path where the images are stored on the SD CARD. 
					String myImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + img;
					
					try {

							BitmapFactory.Options options = new BitmapFactory.Options();
						    options.inSampleSize = 4;
						    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
						
						    
						    //if(bm.getHeight() >300 & bm.getWidth() >400){
						
							    //Uri imgUri=Uri.parse(myImagePath);
							    //imgView.setImageURI(imgUri);
						    	imgView.setImageBitmap(bm);
								imgView.setLayoutParams(new LayoutParams(400, 300));		//this will use seam carving later on. For now we are saying load the image in a 400x300 container
								page.addView(imgView); 	
						   // }
						   /* else{
						    	//small images
							    imgView.setImageBitmap(bm);
								imgView.setLayoutParams(new LayoutParams(bm.getWidth(), bm.getHeight()));
								page.addView(imgView); 
						    }*/
						   
						} catch (OutOfMemoryError E) {
					    // release some (all) of the above objects
							System.out.println("Out of Memory Exception");
							TextView txtView = new TextView(Reader.this);
							txtView.setText("Error: cannot load image. Out of memory!");
							page.addView(txtView);
						}
					
				    
				    
				    
				    
	
					//New custom view that adds a bit of spacing to the end of image items
				    View view = new View(this);
				    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				    page.addView(view);
				    
				    
					imgView.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.ImageViewer");
	        	        	 intent.putExtra("image", img);
	        	        	 intent.putExtra("path", packagePath);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	        	 
	        	        
			            	
			             } //end of public void
				 
						}); 
				    	    
			
			 }
			 /*
			  * Method for displaying the Text Items
			  */			 
			private void displayText(final String text, final LinearLayout page){
				  
				 TextView tv = new TextView(this);
				 tv.setText(Html.fromHtml(text));	//create its HTML layout
				 tv.setTextColor(0xFF000000);
				 page.addView(tv);
				 
				//New custom view that adds a bit of spacing to the end of image items
				View view = new View(this);
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				page.addView(view);
				
			}
			 
			/*
			 * Method for displaying the Video Items		 
			 */
			private void displayVideo(final String video, LinearLayout page){
				// make the button and thumbnail look like it is a TV (simple metaphor for users to understand it is a video clip)
				 					 
					 //Locate the video and get the thumbnail image of the video file
					 try{
						 Bitmap thumb = android.media.ThumbnailUtils.createVideoThumbnail("/sdcard/placebooks/unzipped" + packagePath + "/" + video, MediaStore.Images.Thumbnails.MINI_KIND);
						 Bitmap scaledThumb = Bitmap.createScaledBitmap(thumb, 360, 270, true);	//scale the bitmap to the right size of the button
				 
						 ibVid = new ImageButton(this);
						 
						//assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
						
						 ibVid.setImageBitmap(scaledThumb);
						 //ibVid.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						 ibVid.setLayoutParams(new LayoutParams(400,300));
						 page.addView(ibVid); 
						 
					 } catch (OutOfMemoryError E) {
						    // release some (all) of the above objects
								System.out.println("Out of Memory Exception");
								TextView txtView = new TextView(Reader.this);
								txtView.setText("Error: cannot load video. Out of memory!");
								page.addView(txtView);
							}
					 
					//New custom view that adds a bit of spacing to the end of image items
					View view = new View(this);
					view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
					page.addView(view);
					
					/*
					 * Differentiate between which Listener was pressed by comparing the View reference.
					 */
					ibVid.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
			            	 //Play the video - calls VideoViewer class
			            	 //create a new intent based on the VideoViewer class
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.VideoViewer");
	        	        	 intent.putExtra("video", video);
	        	        	 intent.putExtra("path", packagePath);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	        	 
	        	        
			            	
			             } //end of public void
				 
						});
					
			} //end of displayVideo method
			
	
			
			
			//start of audio methods
			  /*
			   * Method for displaying audio items
			   */
		    public void displayAudio(final String audio, LinearLayout page){
		    	
		    	 //audio exists so set the audio flag to true
		    	 audio_included = true;
		    	
		    	 ibAudioPlay = new ImageButton(this);
		    	 ibAudioPlay.setImageResource(R.drawable.play);
		    	 
		    	 ibAudioStop = new ImageButton(this);
		    	 ibAudioStop.setImageResource(R.drawable.stop);
		    	 
		    	 ibAudioPause = new ImageButton(this);
		    	 ibAudioPause.setImageResource(R.drawable.pause);
	
		    	 
				 llAudio = new LinearLayout(this);	//create a new linear layout for the Audio buttons (play/pause/stop)
				 llAudio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 	// this wraps the new linear layout to the original and centres it
		    	 
		    	 //assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
		    	 ibAudioPlay.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPlay); 
		    	 
		    	 ibAudioPause.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPause);
		    	 
		    	 ibAudioStop.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioStop);
		    	 
		    	 page.addView(llAudio);	//add the audio linear layout to the main linear layout

		    	 
		    	 TextView tv = new TextView(this);
				 tv.setText("Audio File: " + audio);
				 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				 page.addView(tv);
				 
				//New custom view that adds a bit of spacing to the end of image items
			    View view = new View(this);
			    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				page.addView(view);
				 		    		
				 
						/*
						 *  Set Click Listener for the buttons
						 *  
						 */
				         ibAudioPlay.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 playAudio(audio);
				            	
				             } //end of public void
				 
				         });
				         
				         ibAudioPause.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 pauseAudio();
				            	   
				             } //end of public void
				 
				         });
				         
				         ibAudioStop.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				 					 stopAudio();					 
				            	  
				             } //end of public void
				 
				         });
				         
				         		    	
		    } //end of displayAudio() method
		    
		  		    
		    
		    public void playAudio(final String audioFile){
		       		     
		        try {
		            mp.setDataSource("sdcard/placebooks/unzipped" + packagePath + "/" + audioFile);
		        } catch (IllegalArgumentException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } catch (IllegalStateException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        try {
		            mp.prepare();  //this method is synchronous; as soon as it returns the clip is ready to play. There is also prepareAsync() which is asynchronous
		        } catch (IllegalStateException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        mp.start();
		        ibAudioPlay.setEnabled(false);
		    	ibAudioPause.setEnabled(true);
		    	ibAudioStop.setEnabled(true);
		    }
		    
		    public void pauseAudio(){
		    	
		    	mp.pause();
		    	
		    	ibAudioPlay.setEnabled(true);
		    	ibAudioPause.setEnabled(false);
		    	ibAudioStop.setEnabled(true);
		    	
		    }
		    
		    public void stopAudio(){
		    	
		    	mp.stop();
		    	
		    	ibAudioPause.setEnabled(false);
		    	ibAudioStop.setEnabled(false);
		    	
		    	try{
		    		//mp.prepare();	//prepare it again so it can play again if you want (restarts it)
		    		mp.seekTo(0);	//seek to the start of the audio file
		    		ibAudioPlay.setEnabled(true);
		    	}
		    	catch (Throwable t){
		    		goBlooey(t);
		    	}
		    	
		    }
		    
		    @Override
		    public void onDestroy(){
		    	super.onDestroy();
		    	
		    	//stop audio only if audio exists
		    	if (audio_included){
		    		stopAudio();
		    		//reset the audio flag
		    		audio_included = false;
		    	}
		    }
		    
		    private void goBlooey(Throwable t){
		    	
		    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
		    	
		    	builder
		    	.setTitle("Exception!")
		    	.setMessage(t.toString())
		    	.setPositiveButton("OK", null)
		    	.show();
		    }
		    //end of audio methods
		    
		    
		    /*
		     * Map Image Item
		     * Method for displaying the map tile image
		     */
		    public void displayMapImage(final String mapImage,/* final Coordinate[] c,*/ final LinearLayout page){
		    	
			    //locate the file path where the images are stored on the SD CARD. 
				String myMapImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + mapImage;
			    mapImgView = new ImageView(this);

			    if(mapImage != null){
					try{ 
												
						BitmapFactory.Options options = new BitmapFactory.Options();
					    options.inSampleSize = 2;
					    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);
						Uri imgUri=Uri.parse(myMapImagePath);
					    mapImgView.setImageURI(imgUri);
					    //mapImgView.setImageBitmap(bm);
						mapImgView.setLayoutParams(new LayoutParams(500, 500));	
						page.addView(mapImgView); 
					/*	
						c_x1 = c[0].x;
						c_y1 = c[0].y;
						c_x2 = c[1].x;
						c_y2 = c[1].y;
						c_x3 = c[2].x;
						c_y3 = c[2].y;
						c_x4 = c[3].x;
						c_y4 = c[3].y;
						c_x5 = c[4].x;
						c_y5 = c[4].y;
						
					*/	
						
			    	} catch (OutOfMemoryError E) {
				    // release some (all) of the above objects
						System.out.println("Out of Memory Exception");
						TextView txtView = new TextView(Reader.this);
						txtView.setText("Error: cannot load map file. Out of memory!");
						page.addView(txtView);
					}
			    }
			    else{
			    	TextView t = new TextView(this);
			    	t.setText("Error: cannot read map");
			    	page.addView(t);
			    }
				 
				//New custom view that adds a bit of spacing to the end of image items
				View view = new View(this);
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				page.addView(view);
				
				/*
				 * Differentiate between which Listener was pressed by comparing the View reference.
				 */
				mapImgView.setOnClickListener(new OnClickListener() {
		             @Override
		             public void onClick(View v) {
		            	 
		            	 Intent intent = new Intent();
	     				 	overridePendingTransition(0, 0);
	     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

       	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.MapImageViewer");
       	        	 intent.putExtra("mapImage", mapImage);
       	        	 intent.putExtra("packagePath", packagePath);
       	        /*	 intent.putExtra("c_x1", c_x1);
       	        	 intent.putExtra("c_y1", c_y1);
       	        	 intent.putExtra("c_x2", c_x2);
       	        	 intent.putExtra("c_y2", c_y2);
       	        	 intent.putExtra("c_x3", c_x3);
       	        	 intent.putExtra("c_y3", c_y3);
       	        	 intent.putExtra("c_x4", c_x4);
       	        	 intent.putExtra("c_y4", c_y4);
       	        	 intent.putExtra("c_x5", c_x5);
       	        	 intent.putExtra("c_y5", c_y5);
       	         */

	     				    overridePendingTransition(0, 0);
       	        	 startActivity(intent);	
       	        	 
		            	
		             } //end of public void
			 
					});
		    	
		    }
		    
		    /*
		     * Web Bundle Item
		     * Method for displaying the web bundle
		     */
		    public void displayWebBundle(final String filename, final String url, final String itemKey, final LinearLayout page){
		    	
			 	//ImageButton thumb = new ImageButton(this);
			 	Button thumb = new Button(this);
		    	thumb.setLayoutParams(new LayoutParams(400, 250));
			 	thumb.setText("WEB BUNDLE");
			 	page.addView(thumb);
			 	
			 	//New custom view that adds a bit of spacing to the end of image items
				View view = new View(this);
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				page.addView(view);
			 	
			 	thumb.setOnClickListener(new OnClickListener() {
		             @Override
		             public void onClick(View v) {
		                 
		            	 //display the web site in a new view (call the WebBundleViewer)
		            	 Intent intent = new Intent();
	     				 	overridePendingTransition(0, 0);
	     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	     	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.WebBundleViewer");
	     	        	 intent.putExtra("filename", filename);
	     	        	 intent.putExtra("url", url);
	     	        	 intent.putExtra("path", packagePath);
	     	        	 intent.putExtra("itemKey", itemKey);
	     	        	 overridePendingTransition(0, 0);
	     	        	 startActivity(intent);	
			            	
		            	
		             } //end of public void
		 
		         });
		    	
 
		    }
		    
				
		    
		    /*
		    * Method to Parse the config.xml file and store the different types of Items into the ArrayList Variables
		    */
			private String getMyXML() throws Exception {
						
				StringBuffer inLine = new StringBuffer();
				/* Get a SAXParser from the SAXPArserFactory. */
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();

				/* Get the XMLReader of the SAXParser we created. */
				XMLReader xr = sp.getXMLReader();
				/* Create a new ContentHandler and apply it to the XML-Reader */
				XMLHandler myExampleHandler = new XMLHandler();
				xr.setContentHandler(myExampleHandler);
								
				/*
				 * Needs to be changed to be dynamic and not static
				 * try and catch because it might not exist..etc
				 * The pbkey for this will be inserting upon a user clicking a placebook..depending on which one is clicked, it's corresponding placebook will get fetched
				 */
			//	FileInputStream in = new FileInputStream("/sdcard/placebooks/unzipped/packages/home/" + username + "/placebooks-data/packages/" + key + "/config.xml");    /* 0001/config.xml");  //text.txt*/
				FileInputStream in = new FileInputStream("/sdcard/placebooks/unzipped/" + packagePath + "/config.xml");
				//FileInputStream in = new FileInputStream("/sdcard/placebooks/unzipped/var/lib/placebooks-media/packages/64/config.xml");

			//	FileInputStream in = new FileInputStream("/sdcard/PlaceBooks/unzipped/stuart/placebook-data/packages/pack123/config.xml"); 

				xr.parse(new InputSource(in));
				
			//	ArrayList<Book> parsedExampleDataSet = myExampleHandler.getParsedData();
			//  Book parsedExampleDataSet = myExampleHandler.getParsedData();
				Book book = myExampleHandler.getParsedData();
				
				try{
				inLine.append(book.toString());	
				}
				catch(NullPointerException npe){
					Log.e("TRACE = ",npe.getMessage());
					System.out.println("Null pointer exception has been caught");
					TextView textView = new TextView(Reader.this);
					textView.setText("Error: Null Pointer Exception");
					setContentView(textView);
				}
				pbkey = book.getKey();		//the book key (folder name) is also stored in the config.xml file - so we can pull it out from that
				
				page1 = (ArrayList<Point>)book.getPage1();
				page2 = (ArrayList<Point>)book.getPage2();
				page3 = (ArrayList<Point>)book.getPage3();
				page4 = (ArrayList<Point>)book.getPage4();
				page5 = (ArrayList<Point>)book.getPage5();
				page6 = (ArrayList<Point>)book.getPage6();
				
				
			    //Pass the data into the data ArrayLists
				for(Point item: page1) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page1Data.add(data);
		        	page1Type.add(type);
		        	page1Url.add(url);
		        	page1Keys.add(itemKey);
		        	//page1Geometries.add(geomCo);

				}
				for(Point item: page2) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page2Data.add(data);
		        	page2Type.add(type);
		        	page2Url.add(url);
		        	page2Keys.add(itemKey);
		        	//page2Geometries.add(geomCo);

				}
				for(Point item: page3) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page3Data.add(data);
		        	page3Type.add(type);
		        	page3Url.add(url);
		        	page3Keys.add(itemKey);
		        	//page3Geometries.add(geomCo);

				}
				for(Point item: page4) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page4Data.add(data);
		        	page4Type.add(type);
		        	page4Url.add(url);
		        	page4Keys.add(itemKey);
		        	//page4Geometries.add(geomCo);

				}
				for(Point item: page5) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page5Data.add(data);
		        	page5Type.add(type);
		        	page5Url.add(url);
		        	page5Keys.add(itemKey);
		        	//page5Geometries.add(geomCo);

				}
				for(Point item: page6) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	//Coordinate[] geomCo = item.getGeometryCoordinates();
		        	page6Data.add(data);
		        	page6Type.add(type);
		        	page6Url.add(url);
		        	page6Keys.add(itemKey);
		        	//page6Geometries.add(geomCo);

				}
				
			
				in.close();
				
				return inLine.toString();    
				
			}
			
			/*
			 *  Extends SimpleOnGestureListener for implementing my own handling on swipe/fling action
			 */
			 class MyGestureDetector extends SimpleOnGestureListener {


			        @Override
			        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			            try {
			                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
			                    return false;
			                // right to left swipe
			                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			                	flipper.setInAnimation(slideLeftIn);
			                    flipper.setOutAnimation(slideLeftOut);
			                	flipper.showNext();
			                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			                	flipper.setInAnimation(slideRightIn);
			                    flipper.setOutAnimation(slideRightOut);
			                	flipper.showPrevious();
			                }
			            } catch (Exception e) {
			                // nothing
			            }
			            return false;
			        }
			    }

			 
			 public boolean canFlipRight() {
					// TODO Auto-generated method stub
					return true;
				}
			 
			 public boolean canFlipLeft() {
					// TODO Auto-generated method stub
					return true;
				}
			 
		//important. makes sure that in the activity, it is catching the gesture event by overriding the onTouch() method	 
			 @Override
			 public boolean onTouchEvent(MotionEvent event) {
			 if (gestureDetector.onTouchEvent(event))
			 return true;
			 else
			 return false;
			 }

			 
		/*	 @Override
			 public void onResume(){
			        getWindow().setWindowAnimations(0);	//do not animate the view when the activity resumes
				 
			 }
		*/	  
			 
			//decodes image and scales it to reduce memory consumption
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
			 
			 
			 
			
} //end of class



	 