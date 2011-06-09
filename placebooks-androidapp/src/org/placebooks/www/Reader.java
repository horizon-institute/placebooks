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
import java.util.Collections; //for sorting the Order ArrayLists




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
	
	ArrayList<String> page1Type = new ArrayList<String>();
	ArrayList<String> page2Type = new ArrayList<String>();
	ArrayList<String> page3Type = new ArrayList<String>();
	
	ArrayList<String> page1Data = new ArrayList<String>();
	ArrayList<String> page2Data = new ArrayList<String>();
	ArrayList<String> page3Data = new ArrayList<String>();

		
	//Video Variables
	private VideoView video;
	private MediaController ctlr;
	private ImageButton ibThumb;
	
	//Audio Variables
	private MediaPlayer mp = new MediaPlayer();
	private ImageButton ibAudioPlay; 
	private ImageButton ibAudioPause;
	private ImageButton ibAudioStop;
	
	

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
			        	        
			        	
			        //set the content view to the xml reader file
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

				
					      	       	        
			        try {
				        getMyXML();		//call method to parse XML		
				        
				     /*   
				        for(String a: page1Data) {
				        	//String s = a;
				            //a.getType();
				            TextView tv = new TextView(this);
							  tv.setText(a);
							  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
							  ll.addView(tv);
				        }
				     */
	       
					  
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
						 //displayMapImage(page1Items.get(i).toString(), ll);
						 
						 TextView tv = new TextView(this);
						  tv.setText(page1.get(i).toString());
						  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
						  ll.addView(tv);
						 
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
						 }
						 else if (page2Type.get(i).toString().equalsIgnoreCase("AUDIO")){
							 displayAudio(page2Data.get(i).toString(), ll2);	
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
					  
					  
				//	  TextView tv = new TextView(this);
				//	  tv.setText(page3Items.get(i));
				//	  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			   //     ll3.addView(tv);
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
			 private void displayImage(String img, LinearLayout page){
				 				 
				 /*
				  * Dynamically creating 'x' amount of image views for each image in the placebook 
				  * -- need to work on ordering next -- e.g could have img, txt, img, whereas right now it will always display the list of all images one after each other
				  */
				 
					ImageView image = new ImageView(this);
				    //locate the file path where the images are stored on the SD CARD. 
					String myImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + img;
				 
				    BitmapFactory.Options options = new BitmapFactory.Options();
				    options.inSampleSize = 1;
				    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
				        
				    image.setImageBitmap(bm); 
				    image.setAdjustViewBounds(true);
				    image.setScaleType(ScaleType.FIT_CENTER);
				    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				    page.addView(image);
				    
				    //New custom view that adds a bit of spacing to the end of image items
				    View view = new View(this);
				    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
				    page.addView(view);
			
			 }
			 /*
			  * Method for displaying the Text Items
			  */			 
			private void displayText(String text, LinearLayout page){
				  
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
			private void displayVideo(String video, LinearLayout page){
				 					 
					 //Locate the video and get the thumbnail image of the video file
					 Bitmap thumb = android.media.ThumbnailUtils.createVideoThumbnail("/sdcard/placebooks/unzipped" + packagePath + "/" + video, MediaStore.Images.Thumbnails.MINI_KIND);
			 
					 //ArrayList of ImageButtons. Same view
					 //ibThumb.add(new ImageButton(this));
					 ibThumb = new ImageButton(this);
					 
					 
					//assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
					/*ibThumb.get(i).setImageBitmap(thumb);
					ibThumb.get(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					ll3.addView(ibThumb.get(i)); 
						*/
					 ibThumb.setImageBitmap(thumb);
					 ibThumb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					 page.addView(ibThumb); 
					 
					//New custom view that adds a bit of spacing to the end of image items
					View view = new View(this);
					view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
					page.addView(view);
					
					/*
					 * Differentiate between which Listener was pressed by comparing the View reference.
					 */
					final String vi = video;
					ibThumb.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
			            	 playVideo(vi);
			            	
			             } //end of public void
				 
						});
					
			} //end of displayVideo method
			
	
			 
			/*
			 * Call this Method to play a Video Item
			 */
		   // public void playVideo(int id){
			  public void playVideo(String videoFile){
				  File clip=new File(Environment.getExternalStorageDirectory(), "/placebooks/unzipped" + packagePath + "/" + videoFile); //alVideoFilename.get(id));

					if (clip.exists()) {

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
		    
			//start of audio methods
			  /*
			   * Method for displaying audio items
			   */
		    public void displayAudio(String audio, LinearLayout page){
		    	
		    		
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
				 		 final String au = audio; 
				         ibAudioPlay.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 playAudio(au);
				            	
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
		    
		  		    
		    
		    public void playAudio(String audioFile){
		       		     
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
		    	
		    		stopAudio();
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
		    public void displayMapImage(String mapImage, LinearLayout page){
		    	
		    	ImageView image = new ImageView(this);
			    //locate the file path where the images are stored on the SD CARD. 
				String myImagePath = "/sdcard/placebooks/unzipped" + packagePath + "/" + mapImage;
			 
			    BitmapFactory.Options options = new BitmapFactory.Options();
			    options.inSampleSize = 1;
			    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
			        
			    image.setImageBitmap(bm); 
			    image.setAdjustViewBounds(true);
			    image.setScaleType(ScaleType.FIT_CENTER);
			    image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			    page.addView(image);
			    
			    //New custom view that adds a bit of spacing to the end of image items
			    View view = new View(this);
			    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 20));
			    page.addView(view);
		    	
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
				
				pbkey = book.getKey();		//the book key (folder name) is also stored in the config.xml file - so we can pull it out from that
				
				page1 = (ArrayList<Point>)book.getPage1();
				page2 = (ArrayList<Point>)book.getPage2();
				page3 = (ArrayList<Point>)book.getPage3();
				
				
			    //Pass the data into the data ArrayLists
				for(Point item: page1) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	page1Data.add(data);
		        	page1Type.add(type);
				}
				for(Point item: page2) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	page2Data.add(data);
		        	page2Type.add(type);
				}
				for(Point item: page3) {
		        	String data = item.getData();
		        	String type = item.getType();
		        	page3Data.add(data);
		        	page3Type.add(type);
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


			 
	
			
} //end of class



	 