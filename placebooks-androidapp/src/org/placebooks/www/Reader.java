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
import android.view.View;


import android.widget.TextView;
import android.widget.ImageView;	//for images and for the map (i guess) since the map is now going to be just an image
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.ImageButton;


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




//Implement a Listener (added the interface to the base class)
public class Reader extends Activity {
	
	
	//TextView to display error if placebook doesn't display properly	
	private TextView orgXmlTxt;		
	
	//ScrollView and LinearLayout
	private ScrollView sv;		//scroll view that wraps the linear layout
	private ScrollView sv2;		//scroll view for page 2
	private ScrollView sv3;		//scroll view for page 3
	private LinearLayout ll;	//main linear layout page 1
	private LinearLayout ll2;	//page 2
	private LinearLayout ll3;	//page 3
	private LinearLayout llAudio;	//audio layout
	private ViewFlipper flipper;	//flipper for the swipe navigation
	//swipe gesture constants
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
	
	
	//ArrayLists of every Item Type's Filename/Path
	private ArrayList <String> alTextText = new ArrayList <String>();	// arraylist for the text elements in the placebook. Text of the Text Item
	private ArrayList <String> alImageFilename = new ArrayList <String>();	//arraylist of the image path file names 
	private ArrayList <String> alAudioFilename = new ArrayList <String>();	//arraylist of the audio path file names
	private ArrayList <String> alVideoFilename = new ArrayList <String>();	//arraylist of the video path file names

	//Video Variables
	private VideoView video;
	private MediaController ctlr;
	private ArrayList <ImageButton> ibThumb = new ArrayList<ImageButton>();
	
	//Audio Variables
	private MediaPlayer mp = new MediaPlayer();
	private ArrayList <ImageButton> ibAudioPlay = new ArrayList<ImageButton>();
	private ArrayList <ImageButton> ibAudioPause = new ArrayList<ImageButton>();
	private ArrayList <ImageButton> ibAudioStop = new ArrayList<ImageButton>();
	
	

	 		 @Override
	     	 public void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);	//icicle
			        getWindow().setFormat(PixelFormat.TRANSLUCENT);
			        
			        /*
			         * get the extras (package path) out of the new intent
			         * retrieve the packagePath.
			         */
			        Intent intent = getIntent();
			        if(intent != null) packagePath = intent.getStringExtra("packagePath");
			        	        
			        	
		
			        setContentView(R.layout.reader);
			        flipper=(ViewFlipper)findViewById(R.id.flipper); 
			        sv = (ScrollView)findViewById(R.id.scroller);
			        ll = (LinearLayout)findViewById(R.id.linearLayout);
			        
			        
			        sv2 = (ScrollView)findViewById(R.id.scroller2);
			        ll2 = (LinearLayout)findViewById(R.id.linearLayout2);
			        sv3 = (ScrollView)findViewById(R.id.scroller3);	          
			        ll3 = (LinearLayout)findViewById(R.id.linearLayout3);
			        gestureDetector = new GestureDetector(new MyGestureDetector());
			        
			        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
			        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
			        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
			        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

			        
			      /*
			       * This was completely dynamic without using any xml file for the view. Now I am using
			       * an xml file view for a template and adding dynamic content to that file.  
				        sv = new ScrollView(this);
						ll = new LinearLayout(this);
						 // WEIGHT = 1f, GRAVITY = center
						ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,1));	
						ll.setOrientation(LinearLayout.VERTICAL);
						sv.setBackgroundColor(0xFFFFFFFF);
						ll.setGravity(android.view.Gravity.CENTER);
						sv.addView(ll);
					*
					*/	
			        
					//uName = "stuart"; 	//need to GET user name...how is this known? - case sensitive?
					//pbkey = "pack123";

					//this.setContentView(sv);
				
					      	       	        
			        try {
			        	//setContentView(R.layout.reader); 	 //Doing it dynamically and programmatically now instead of .xml views		        		        	
				        getMyXML();		//call method to parse XML		
				        
				        /* need to work out the order in which the items are displayed 
				         * i.e could be image, text, image.. so I will need to calculate the order and call each
				         * display() method a number of times by order
				         */
				       displayImage(); //once XML is parsed and variables are set, call the methods to display content
					   displayText();
				       displayVideo();
				       displayAudio();
							
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
			 private void displayImage(){
				 				 
				 /*
				  * Dynamically creating 'x' amount of image views for each image in the placebook 
				  * -- need to work on ordering next -- e.g could have img, txt, img, whereas right now it will always display the list of all images one after each other
				  */
	
				 for (int i=0;i<alImageFilename.size();i++ ){
					 
					 ImageView image = new ImageView(this);
					 //locate the file path where the images are stored on the SD CARD. 
					 String myImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + alImageFilename.get(i);
					 
					    BitmapFactory.Options options = new BitmapFactory.Options();
					    options.inSampleSize = 1;
					    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
					        
					    image.setImageBitmap(bm); 
					    image.setAdjustViewBounds(true);
					    image.setScaleType(ScaleType.FIT_CENTER);
					    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					    ll.addView(image);				    
				 } 
			 }
			 /*
			  * Method for displaying the Text Items
			  */			 
			private void displayText(){
				  
				 for (int i=0; i<alTextText.size(); i++){
					 TextView tv = new TextView(this);
					 tv.setText(Html.fromHtml(alTextText.get(i)));	//create its HTML layout
					 tv.setTextColor(0xFF000000);
					 ll.addView(tv);
				 } 
			}
			 
			/*
			 * Method for displaying the Video Items		 
			 */
			private void displayVideo(){
				 
				 for ( int i=0; i<alVideoFilename.size(); i++){
					 
					 //Locate the video and get the thumbnail image of the video file
					 Bitmap thumb = android.media.ThumbnailUtils.createVideoThumbnail("/sdcard/placebooks/unzipped" + packagePath + "/" + alVideoFilename.get(i), MediaStore.Images.Thumbnails.MINI_KIND);
			 
					 //ArrayList of ImageButtons. Same view
					 ibThumb.add(new ImageButton(this));
			
					//assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
					ibThumb.get(i).setImageBitmap(thumb);
					ibThumb.get(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					ll3.addView(ibThumb.get(i)); 
					
					TextView tv = new TextView(this);
					tv.setText("Video file: " + alVideoFilename.get(i));
					tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
					ll3.addView(tv);
					
					
					/*
					 * Differentiate between which Listener was pressed by comparing the View reference.
					 * This method is not "great" but it does work for now. 
					 */
					ibThumb.get(i).setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
			            	 if(v==ibThumb.get(0)) {				 
								 playVideo(0);
							 }
							 else if(v==ibThumb.get(1)){
								 playVideo(1); 
							 }
							 else if(v==ibThumb.get(2)){
								 playVideo(2); 	 
							 }
							 else if(v==ibThumb.get(3)){
								 playVideo(3); 
							 }
							 else if(v==ibThumb.get(4)){
								 playVideo(4); 
							 }
							 else if(v==ibThumb.get(5)){
								 playVideo(5); 
							 }
							 else{
								 // do nothing
							 }			 

				 	 		
			             } //end of public void
				 
						});
					
				 } //end of for loop
			} //end of displayVideo method
			
	
			 
			/*
			 * Call this Method to play a Video Item
			 */
		    public void playVideo(int id){
				  
				  File clip=new File(Environment.getExternalStorageDirectory(), "/placebooks/unzipped" + packagePath + "/" + alVideoFilename.get(id)); //alVideoFilename.get(id));

					if (clip.exists()) {

							//video=(VideoView)findViewById(R.id.video);
							video = new VideoView(Reader.this);
							video.setVideoPath(clip.getAbsolutePath());

							video.setLayoutParams(new Gallery.LayoutParams(
									LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

							ctlr=new MediaController(Reader.this);
							ctlr.setMediaPlayer(video);
							video.setMediaController(ctlr);
							video.requestFocus();
							video.start();
		                    setContentView(video);

						} 
  
			}
		    
		    public void displayAudio(){
		    	
		    	for ( int i=0; i<alAudioFilename.size(); i++){
		    		
		    	 ibAudioPlay.add(new ImageButton(this));
		    	 ibAudioPlay.get(i).setImageResource(R.drawable.play);
		    	 
		    	 ibAudioStop.add(new ImageButton(this));
		    	 ibAudioStop.get(i).setImageResource(R.drawable.stop);
		    	 
		    	 ibAudioPause.add(new ImageButton(this));
		    	 ibAudioPause.get(i).setImageResource(R.drawable.pause);
	
		    	 
				 llAudio = new LinearLayout(this);	//create a new linear layout for the Audio buttons (play/pause/stop)
				 llAudio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 	// this wraps the new linear layout to the original and centres it
		    	 
		    	 //assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
		    	 ibAudioPlay.get(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPlay.get(i)); 
		    	 
		    	 ibAudioPause.get(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPause.get(i));
		    	 
		    	 ibAudioStop.get(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioStop.get(i));
		    	 
		    	 ll2.addView(llAudio);	//add the audio linear layout to the main linear layout

		    	 
		    	 TextView tv = new TextView(this);
				 tv.setText("Audio File: " + alAudioFilename.get(i));
				 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				 ll2.addView(tv);
		    		
				 
						/*
						 *  Set Click Listener for each of the buttons
						 *  (again..just like the video onClick listener this is not the best way of doing it but it will work for now
						 *  
						 */
				         ibAudioPlay.get(i).setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 if(v==ibAudioPlay.get(0)){ 
				 					 playAudio(0);					 
				 				 }
				            	 else if (v==ibAudioPlay.get(1)){
				            		 playAudio(1);
				            	 }
				            	 else if (v==ibAudioPlay.get(2)){
				            		 playAudio(2);
				            	 }
				            	 else if (v==ibAudioPlay.get(3)){
				            		 playAudio(3);
				            	 }
				            	 else if (v==ibAudioPlay.get(4)){
				            		 playAudio(4);
				            	 }
				            	 else if (v==ibAudioPlay.get(5)){
				            		 playAudio(5);
				            	 }
				            	 else {
				            		//do nothing
				            	 }  
				             } //end of public void
				 
				         });
				         
				         ibAudioPause.get(i).setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 if(v==ibAudioPause.get(0)){ 
				 					 pauseAudio(0);					 
				 				 }
				            	 else if (v==ibAudioPause.get(1)){
				            		 pauseAudio(1);
				            	 }
				            	 else if (v==ibAudioPause.get(2)){
				            		 pauseAudio(2);
				            	 }
				            	 else if (v==ibAudioPause.get(3)){
				            		 pauseAudio(3);
				            	 }
				            	 else if (v==ibAudioPause.get(4)){
				            		 pauseAudio(4);
				            	 }
				            	 else if (v==ibAudioPause.get(5)){
				            		 pauseAudio(5);
				            	 }
				            	 else {
				            		//do nothing
				            	 }  
				             } //end of public void
				 
				         });
				         
				         ibAudioStop.get(i).setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 if(v==ibAudioStop.get(0)){ 
				 					 stopAudio(0);					 
				 				 }
				            	 else if (v==ibAudioStop.get(1)){
				            		 stopAudio(1);
				            	 }
				            	 else if (v==ibAudioStop.get(2)){
				            		 stopAudio(2);
				            	 }
				            	 else if (v==ibAudioStop.get(3)){
				            		 stopAudio(3);
				            	 }
				            	 else if (v==ibAudioStop.get(4)){
				            		 stopAudio(4);
				            	 }
				            	 else if (v==ibAudioStop.get(5)){
				            		 stopAudio(5);
				            	 }
				            	 else {
				            		//do nothing
				            	 }  
				             } //end of public void
				 
				         });
				         
				         
		    	} //end of for loop
		    	
		    } //end of displayAudio() method
		    
		  		    
		    
		    public void playAudio(int id){
		       		     
		        try {
		            mp.setDataSource("sdcard/placebooks/unzipped" + packagePath + "/" + alAudioFilename.get(id));
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
		        ibAudioPlay.get(id).setEnabled(false);
		    	ibAudioPause.get(id).setEnabled(true);
		    	ibAudioStop.get(id).setEnabled(true);
		    }
		    
		    public void pauseAudio(int id){
		    	
		    	mp.pause();
		    	
		    	ibAudioPlay.get(id).setEnabled(true);
		    	ibAudioPause.get(id).setEnabled(false);
		    	ibAudioStop.get(id).setEnabled(true);
		    	
		    }
		    
		    public void stopAudio(int id){
		    	
		    	mp.stop();
		    	
		    	ibAudioPause.get(id).setEnabled(false);
		    	ibAudioStop.get(id).setEnabled(false);
		    	
		    	try{
		    		//mp.prepare();	//prepare it again so it can play again if you want (restarts it)
		    		mp.seekTo(0);	//seek to the start of the audio file
		    		ibAudioPlay.get(id).setEnabled(true);
		    	}
		    	catch (Throwable t){
		    		goBlooey(t);
		    	}
		    	
		    }
		    
		    @Override
		    public void onDestroy(){
		    	super.onDestroy();
		    
		    for (int i =0; i<ibAudioStop.size(); i++) {
		    	
		    	if(ibAudioStop.get(i).isEnabled())
		    	{
		    		stopAudio(i);
		    	}
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
			//	FileInputStream in = new FileInputStream("/sdcard/PlaceBooks/unzipped/stuart/placebook-data/packages/pack123/config.xml"); 

				xr.parse(new InputSource(in));
				
			//	ArrayList<Book> parsedExampleDataSet = myExampleHandler.getParsedData();
			//  Book parsedExampleDataSet = myExampleHandler.getParsedData();
				Book book = myExampleHandler.getParsedData();
				
				inLine.append(book.toString());
				
				// Needs to read every element from each type of array i.e you could have x amount of textItems, videoItems, audioItems..etc
				pbkey = book.getKey();		//the book key (folder name) is also stored in the config.xml file - so we can pull it out from that
							
				alTextText = (ArrayList <String>)book.getAlTextText();
				alImageFilename = (ArrayList <String>)book.getAlImageFilename();
				alAudioFilename = (ArrayList <String>)book.getAlAudioFilename();
				alVideoFilename = (ArrayList <String>)book.getAlVideoFilename();			
				
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


			 
	
			
} //end of class



	 