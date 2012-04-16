package org.placebooks.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.placebooks.www.R;
import org.placebooks.www.XMLHandler;
import org.placebooks.www.Book;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.Log;
//import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
//import android.widget.MediaController;
import android.widget.ImageButton;
//import android.widget.Button;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.*;	
import android.media.MediaPlayer;
//import android.util.Log;
import android.provider.MediaStore;
import android.view.View.OnClickListener; 
import android.view.Gravity;
import android.widget.Toast;
import android.content.Intent;
import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
import android.view.View.OnLongClickListener;
import android.widget.ViewFlipper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import com.vividsolutions.jts.geom.Coordinate;
import 	android.net.Uri;
import java.io.FileNotFoundException;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import org.apache.commons.lang.StringEscapeUtils;
import android.webkit.*;


//Implement a Listener (added the interface to the base class)
public class Reader extends Activity {
	
	private int screenWidth;	//Mobile screen width resolution
	private int screenHeight;	//Mobile screen height resolution
	
	//ScrollView and LinearLayout
	private ScrollView sv;		//Scroll view that wraps the linear layout
	private ScrollView sv2;		//Scroll view for page 2
	private ScrollView sv3;		//Scroll view for page 3
	private ScrollView sv4;		//Scroll view for page 4
	private ScrollView sv5;		//Scroll view for page 5
	private ScrollView sv6;		//Scroll view for page 6
	private LinearLayout ll;	//Main linear layout page 1
	private LinearLayout ll2;	//Linear layout page 2
	private LinearLayout ll3;	//Linear layout page 3
	private LinearLayout ll4;	//Linear layout page 4
	private LinearLayout ll5;	//Linear layout page 5
	private LinearLayout ll6;	//Linear layout page 6
	private LinearLayout llAudio;  //Audio layout
	
	//Page flipper and swipe gesture constants
	private ViewFlipper flipper;	//Flipper for the swipe navigation
	private static final int SWIPE_MIN_DISTANCE = 80;	//120
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
	private String pbtimestamp;
	//private String uName;	//this is the users actual name that they used to register with placebooks. Note - this is not their email address.
    private String packagePath;
	
    private ArrayList<Point> page1 = new ArrayList<Point>();
	private ArrayList<Point> page2 = new ArrayList<Point>();
	private ArrayList<Point> page3 = new ArrayList<Point>();
	private ArrayList<Point> page4 = new ArrayList<Point>();
	private ArrayList<Point> page5 = new ArrayList<Point>();
	private ArrayList<Point> page6 = new ArrayList<Point>();
	
	//Image Variables
	private ImageView imgView;
	private ArrayList<String> alGeoImageFilename = new ArrayList<String>();
	private ArrayList<Coordinate> alGeoImageCoordinate = new ArrayList<Coordinate>();
	
	//Video Variables
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

	private ArrayList<Double> gpsLatCoordinates = new ArrayList<Double>();
	private ArrayList<Double> gpsLonCoordinates = new ArrayList<Double>();
	private double[] arrGpsLatCoordinates;
	private double[] arrGpsLonCoordinates;
	
	//Application variables
	private String unzippedDir;
	private String unzippedRoot;
	private String configFilename;
	
	
	 		 @Override
	     	 public void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);
			        getWindow().setFormat(PixelFormat.TRANSLUCENT);
			        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen

			        CustomApp appState = ((CustomApp)getApplicationContext());
			        unzippedDir = appState.getUnzippedDir();
			        unzippedRoot = appState.getUnzippedRoot();
			        configFilename = appState.getConfigFilename();
			        
			        //Get mobile screen resolution
			        DisplayMetrics dm = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(dm);
			        
			        //Assign the resolution to the variables
			        screenWidth = dm.widthPixels;	//320px on the LG phone
			        screenHeight = dm.heightPixels;	//480px on the LG phone
			        
			        
			        //Get the extras (package path) out of the new intent
			        //Retrieve the packagePath.
			        Intent intent = getIntent();
			        if(intent != null) packagePath = intent.getStringExtra("packagePath");
			        
			        //Set the content view to the xml reader file
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
			        
			        
			        try {
				        getMyXML();		 //Call method to parse XML		
				        populatePages(); //Populate each page

					} 
					catch(FileNotFoundException fnf){
						TextView tv = new TextView(this);
						tv.setText(fnf.getMessage());
						setContentView(tv);
					}
					catch (Exception e) {
						TextView tv = new TextView(this);
						tv.setText(e.getMessage());
						setContentView(tv);
					} 
					
								
					//Set up action listeners for the scroll views
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
					
					sv4.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					
					sv5.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });

					sv6.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					
			
	 		 } //End of onCreate() Method
	 		 
	 		 
	 
	 		 /*
	 		 * Method for displaying the Image Items
	 		 */
			 private void displayImage(final String img, final int height, final LinearLayout page){
				 				  
				imgView = new ImageView(this);
				//Now register the gestureListener to the imageView
				imgView.setOnTouchListener(gestureListener);
				//Set the onTouchListener for the imgView
				imgView.setOnTouchListener(new View.OnTouchListener() {
					@Override
			           public boolean onTouch(View v, MotionEvent event) {
			               if (gestureDetector.onTouchEvent(event)) {
			                   return true;
			               }
			               return false;
			          }
			       });
					
				    //Locate the file path where the images are stored on the SD CARD. 
					String myImagePath = unzippedDir + packagePath + File.separator + img;
					
					try {

							BitmapFactory.Options options = new BitmapFactory.Options();
						    options.inSampleSize = 2;	//WAS 4 (STABLE) TRYING 2 TO SEE WHAT PERFORMANCE IS LIKE
						    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
						    int imageHeight=bm.getHeight();
						    int imageWidth=bm.getWidth();
						    double widthRatio = screenWidth/imageWidth;
						    //double heightRatio = screenHeight/imageHeight;
						    double heightScale = imageHeight*widthRatio;
							
						    if(imageWidth<=100 && imageHeight<=100){
							    bm = Bitmap.createScaledBitmap(bm, imageWidth*2, imageHeight*2, true);	
						    }
						    else if (imageWidth<=200 && imageHeight<=200){
							    bm = Bitmap.createScaledBitmap(bm, imageWidth, imageHeight, true);	
						    }
						    else{
							    bm = Bitmap.createScaledBitmap(bm, screenWidth, (int) heightScale, true);	

						    }
						    
						    	imgView.setImageBitmap(bm);
						    	
								page.addView(imgView); 	

						   
						} catch (OutOfMemoryError E) {
					       // Release some (all) of the above objects
							System.out.println("Out of Memory Exception");
							TextView txtView = new TextView(Reader.this);
							txtView.setText("Error: cannot load image. Out of memory!");
							page.addView(txtView);
						}
					
				    				    	
					//New custom view that adds a bit of spacing to the end of image items
				    View view = new View(this);
				    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				    page.addView(view);
				    				    
				    
				    //imgView.setLongClickable(true);
					imgView.setOnLongClickListener(new OnLongClickListener() {
			             @Override
			             public boolean onLongClick(View v) {
			            	 
			            	//Vibration to alert users
			            	// Get instance of Vibrator from current Context
			            	 Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
			            	 // Vibrate for 300 milliseconds
			            	 vib.vibrate(300);
			            	 
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.ImageViewer");
	        	        	 //intent.putExtra("image", img);
	        	        	 //intent.putExtra("path", packagePath);
	        	        	 intent.putExtra("imagePath",  unzippedDir + packagePath + File.separator + img);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	         
	        	        	 return true;
			            	
			             } //End of public void
				 
						}); 
								
			 }
			 
			/*
			* Method for displaying the Text Items
			*/			 
			private void displayText(final String text, final LinearLayout page){
				  
				 //TextView tv = new TextView(this);
				 String escapedHtml = StringEscapeUtils.escapeHtml(text);
				 //String test = "<div style='font-size:25px; font-weight:bold;'>Header5</div>";
				 //String unescapedHtml = StringEscapeUtils.unescapeHtml(escapedHtml);
				 
				 WebView wv = new WebView(this);
				 wv.loadData(text, "text/html", "utf-8");
				 page.addView(wv);
				 
				 //tv.setText(Html.fromHtml(escapedHtml));
				 //tv.setTextColor(0xFF000000);
				 //page.addView(tv);
				 
				 //Allow users to swipe to next screen if the swipe action is across the text
				 wv.setOnTouchListener(gestureListener);
					//set the onTouchListener for the ibView
					wv.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
				 
				//New custom view that adds a bit of spacing to the end of image items
				//View view = new View(this);
				//view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				//page.addView(view);
				
			}
			 
			/*
			 * Method for displaying the Video Items		 
			 */
			private void displayVideo(final String video, LinearLayout page){
				//Make the button and thumbnail look like it is a TV (simple metaphor for users to understand it is a video clip)
				 					 
					 //Locate the video and get the thumbnail image of the video file
					 try{
						 Bitmap thumb = android.media.ThumbnailUtils.createVideoThumbnail(unzippedDir + packagePath + File.separator + video, MediaStore.Images.Thumbnails.MINI_KIND);
						 double w = screenWidth/1.33;	//ratio
						 double h = screenHeight/3;		//ratio
						 Bitmap scaledThumb = Bitmap.createScaledBitmap(thumb, (int) w, (int) h, true);	//240, 160 scale the bitmap to the right size of the button
						 
						 ibVid = new ImageButton(this);
						 
						 ibVid.setOnTouchListener(gestureListener);
							//Set the onTouchListener for the ibView
							ibVid.setOnTouchListener(new View.OnTouchListener() {
								@Override
					            public boolean onTouch(View v, MotionEvent event) {
					                if (gestureDetector.onTouchEvent(event)) {
					                    return true;
					                }
					                return false;
					            }
					        });
						 
						//Assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
						 ibVid.setImageBitmap(scaledThumb);
						 //ibVid.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						 double w2 = screenWidth/1.23;
						 double h2 = screenHeight/2.66;
						 ibVid.setLayoutParams(new LayoutParams((int) w2,(int) h2));
						 page.addView(ibVid); 
						 
					 } catch (OutOfMemoryError E) {
						    //Release some (all) of the above objects
								System.out.println("Out of Memory Exception");
								TextView txtView = new TextView(Reader.this);
								txtView.setText("Error: cannot load video. Out of memory!");
								page.addView(txtView);
							}
					 
					//New custom view that adds a bit of spacing to the end of image items
					//View view = new View(this);
					//view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
					//page.addView(view);
					
					/*
					* Differentiate between which Listener was pressed by comparing the View reference.
					*/
					ibVid.setOnLongClickListener(new OnLongClickListener() {
			             @Override
			             public boolean onLongClick(View v) {
			            	 
			            	//Vibration to alert users
				            // Get instance of Vibrator from current Context
				            Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
				            // Vibrate for 300 milliseconds
				            vib.vibrate(300);
			            	 
			            	 //Play the video - calls VideoViewer class
			            	 //Create a new intent based on the VideoViewer class
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.VideoViewer");
	        	        	 intent.putExtra("video", video);
	        	        	 intent.putExtra("path", packagePath);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	        	 
	        	        	 return true;
			            	
			             } //end of public void
				 
						});
					
				
			} //End of displayVideo method
			
	
			
			
			//Start of audio methods
			  /*
			   * Method for displaying audio items
			   */
		    public void displayAudio(final String audio, LinearLayout page){
		    	
		    	 //Audio exists so set the audio flag to true
		    	 audio_included = true;
		    	
		    	 ibAudioPlay = new ImageButton(this);
		    	 ibAudioPlay.setImageResource(R.drawable.play);
		    	 
		    	 ibAudioStop = new ImageButton(this);
		    	 ibAudioStop.setImageResource(R.drawable.stop);
		    	 
		    	 ibAudioPause = new ImageButton(this);
		    	 ibAudioPause.setImageResource(R.drawable.pause);
		    	 
				 llAudio = new LinearLayout(this);	//Create a new linear layout for the Audio buttons (play/pause/stop)
				 llAudio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 	//This wraps the new linear layout to the original and centres it
		    	 
		    	 //Assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
		    	 ibAudioPlay.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPlay); 
		    	 
		    	 ibAudioPause.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioPause);
		    	 
		    	 ibAudioStop.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    	 llAudio.addView(ibAudioStop);
		    	 
		    	 page.addView(llAudio);	//Add the audio linear layout to the main linear layout

		    	 
		    	 ibAudioPlay.setOnTouchListener(gestureListener);
		    	 ibAudioPause.setOnTouchListener(gestureListener);
		    	 ibAudioStop.setOnTouchListener(gestureListener);

					//Set the onTouchListeners for each of the audio buttons
					ibAudioPlay.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					ibAudioPause.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					ibAudioStop.setOnTouchListener(new View.OnTouchListener() {
						@Override
			            public boolean onTouch(View v, MotionEvent event) {
			                if (gestureDetector.onTouchEvent(event)) {
			                    return true;
			                }
			                return false;
			            }
			        });
					
		    	 
		    	 TextView tv = new TextView(this);
				 tv.setText("Audio File: " + audio);
				 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				 page.addView(tv);
				 
				//New custom view that adds a bit of spacing to the end of image items
			    //View view = new View(this);
			    //view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				//page.addView(view);
				 		    		
				 
						/*
						 *  Set Click Listener for the buttons
						 */
				         ibAudioPlay.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 playAudio(audio);
				            	
				             } //End of public void
				 
				         });
				         
				         ibAudioPause.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				            	 pauseAudio();
				            	   
				             } //End of public void
				 
				         });
				         
				         ibAudioStop.setOnClickListener(new OnClickListener() {
				             @Override
				             public void onClick(View v) {
				                 
				 					 stopAudio();					 
				            	  
				             } //End of public void
				 
				         });
				         
				         		    	
		    } //End of displayAudio() method
		    
		  		    
		    
		    public void playAudio(final String audioFile){
		       		     
		        try {
		            mp.setDataSource(unzippedDir + packagePath + File.separator + audioFile);
		        } catch (IllegalArgumentException e) {

		        	e.printStackTrace();
		        	
		        } catch (IllegalStateException e) {
		        	
		            e.printStackTrace();
		            
		        } catch (IOException e) {
		        	
		            e.printStackTrace();
		        }
		        try {
		            mp.prepare();  //This method is synchronous; as soon as it returns the clip is ready to play. There is also prepareAsync() which is asynchronous
		        } catch (IllegalStateException e) {

		        	e.printStackTrace();
		        	
		        } catch (IOException e) {
		        	
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
		    		//mp.prepare();	//Prepare it again so it can play again if you want (restarts it)
		    		mp.seekTo(0);	//Seek to the start of the audio file
		    		ibAudioPlay.setEnabled(true);
		    	}
		    	catch (Throwable t){
		    		goBlooey(t);
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
		    //End of audio methods
		    
		    
		    /*
		     * Map Image Item
		     * Method for displaying the map tile image. Every map has a trail!
		     */
		    public void displayMapImage(final String mapImageFilename, final Coordinate[] mapCoordinates, final String gpxFilename, final LinearLayout page){
		    	
			    //Locate the file path where the images are stored on the SD CARD. 
				String myMapImagePath = unzippedDir + packagePath + File.separator + mapImageFilename;
				mapImgView = new ImageView(this);
			    
			    mapImgView.setOnTouchListener(gestureListener);
				//Set the onTouchListener for the mapImgView
				mapImgView.setOnTouchListener(new View.OnTouchListener() {
					@Override
		            public boolean onTouch(View v, MotionEvent event) {
		                if (gestureDetector.onTouchEvent(event)) {
		                    return true;
		                }
		                return false;
		            }
		        });
			    

			    //(1) Display map thumbnail and get the map coordinates
			    if(mapImageFilename != null){
					try{ 
						
						BitmapFactory.Options options = new BitmapFactory.Options();
					    options.inSampleSize = 2;
					    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);
						Uri imgUri=Uri.parse(myMapImagePath);
					    mapImgView.setImageURI(imgUri);
					    //mapImgView.setImageBitmap(bm);
					    double w = screenWidth/1.28;	//ratio
					    double h = screenHeight/1.92;	//ratio
						mapImgView.setLayoutParams(new LayoutParams((int) w, (int) h));	//350, 350 htc hd
						page.addView(mapImgView); 

						//Map images are polygons with 5 coordinates (of lat/long)
						c_x1 = mapCoordinates[0].x;
						c_y1 = mapCoordinates[0].y;
						c_x2 = mapCoordinates[1].x;
						c_y2 = mapCoordinates[1].y;
						c_x3 = mapCoordinates[2].x;
						c_y3 = mapCoordinates[2].y;
						c_x4 = mapCoordinates[3].x;
						c_y4 = mapCoordinates[3].y;
						c_x5 = mapCoordinates[4].x;
						c_y5 = mapCoordinates[4].y;
						
						
			    	} catch (OutOfMemoryError E) {
				    //Release some (all) of the above objects
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
			    
			    
			    //(2) A Map will always have a Trail
			    //Unmarshall the map trail coordinates

			    try {
					Serializer serializer = new Persister(); 
					//Used the simple framework to convert the gpx data from xml file to java objects
					File source = new File(unzippedDir + packagePath + File.separator + gpxFilename);
					Gpx gpx = serializer.read(Gpx.class, source);

					System.out.println("UNMARSHALLING!!!!!!!");
					
					 //Quick hack for the deadline -- will improve later
					 for(int i=0; i<gpx.trk.trkseg.size(); i++){
						
						 String gpxLatLon =  gpx.trk.trkseg.get(i).toString();
						 //Cut the lat and lon out of the string
						 int start = gpxLatLon.indexOf("latitude=");
						 int size = gpxLatLon.length();
						 int middle = gpxLatLon.indexOf("longitude=");

						 gpsLatCoordinates.add(Double.parseDouble(gpxLatLon.substring(start+9, middle)));
						 gpsLonCoordinates.add(Double.parseDouble(gpxLatLon.substring(middle+10, size)));

					 
					 }
					 arrGpsLatCoordinates = new double[gpsLatCoordinates.size()];
					 arrGpsLonCoordinates = new double[gpsLonCoordinates.size()];
					 
					 //Copy the lat/lon arraylists to arrays
					for (int i=0; i<gpsLatCoordinates.size(); i++){
						arrGpsLatCoordinates[i] = gpsLatCoordinates.get(i);
						System.out.println("arr gps lat coords = " + arrGpsLatCoordinates[i]);

					}
					for (int i=0; i<gpsLonCoordinates.size(); i++){
						arrGpsLonCoordinates[i] = gpsLonCoordinates.get(i);
						System.out.println("arr gps lon coordinates = " + arrGpsLonCoordinates[i]);

					}
				  //End of the quick hack 
					 					 
			     } catch (Exception e) {
			          e.printStackTrace();
			     }
			     			    
				 
				//New custom view that adds a bit of spacing to the end of image items
				//View view = new View(this);
				//view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				//page.addView(view);
				
				/*
				 * Differentiate between which Listener was pressed by comparing the View reference.
				 */
				 //Has a map
					mapImgView.setOnLongClickListener(new OnLongClickListener() {
			             @Override
			             public boolean onLongClick(View v) {
			            	 
				 		if(mapImageFilename!=null && arrGpsLatCoordinates!= null && arrGpsLonCoordinates!=null){

			            	//Vibration to alert users
				            //Get instance of Vibrator from current Context
				            Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
				            // Vibrate for 300 milliseconds
				            vib.vibrate(300);
	
				            	 
			            	 Intent intent = new Intent();
		     				 overridePendingTransition(0, 0);
		     				 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	
	       	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.MapImageViewer");
	       	        	 intent.putExtra("mapImageFilename", mapImageFilename);
	       	        	 intent.putExtra("packagePath", unzippedDir + packagePath);
	       	        	 System.out.println("mapImage+PackagePath = " +mapImageFilename + unzippedDir + packagePath);
	
	       	        	 
	       	        	 //Pass the map image lat/lon corner value
	       	        	 intent.putExtra("c_x1", c_x1);
	       	        	 intent.putExtra("c_y1", c_y1);
	       	        	 intent.putExtra("c_x2", c_x2);
	       	        	 intent.putExtra("c_y2", c_y2);
	       	        	 intent.putExtra("c_x3", c_x3);
	       	        	 intent.putExtra("c_y3", c_y3);
	       	        	 intent.putExtra("c_x4", c_x4);
	       	        	 intent.putExtra("c_y4", c_y4);
	       	        	 intent.putExtra("c_x5", c_x5);
	       	        	 intent.putExtra("c_y5", c_y5);
	       	        	 
	       	        	 //Pass the gps trail lat/lons
	       	        	 intent.putExtra("arrLat", arrGpsLatCoordinates);
	       	        	 intent.putExtra("arrLon", arrGpsLonCoordinates);
	       	        	 
	       	        	 //Pass the geotagged media
	       	        	 intent.putExtra("alGeoImageFilename", alGeoImageFilename);
	       	        	 int numCoords = alGeoImageCoordinate.size();
		       	         intent.putExtra("coord_size", numCoords);
		       	         for (int i = 0; i < numCoords; i++)
		       	         {
		       	             Coordinate coord = alGeoImageCoordinate.get(i);
		       	             intent.putExtra("coord_x_" + i, coord.x);
		       	             intent.putExtra("coord_y_" + i, coord.y);
		       	         }
	
		     		     overridePendingTransition(0, 0);
		 					//start activity
		     		     startActivity(intent);	
		 				} //end of if mapImageFilename!=null
		 				else{
						  Toast msg = Toast.makeText(Reader.this, "Error: No Map Trail", Toast.LENGTH_LONG);
		 				  msg.show();

		 				}
	       	        	 
	       	        	 return true;
			            	
			             } //End of public void
				 
						});
			    	
			    }
		    
		 
		    
		    /*
		     * Web Bundle Item
		     * Method for displaying the web bundle
		     */
		    public void displayWebBundle(final String filename, final String url, final String itemKey, final LinearLayout page){
		    	
			 	//ImageButton thumb = new ImageButton(this);
			 	ImageButton thumb = new ImageButton(this);
			 	thumb.setOnTouchListener(gestureListener);
				//Set the onTouchListener for the thumb
				thumb.setOnTouchListener(new View.OnTouchListener() {
					@Override
		            public boolean onTouch(View v, MotionEvent event) {
		                if (gestureDetector.onTouchEvent(event)) {
		                    return true;
		                }
		                return false;
		            }
		        });
			 	
			 	Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.web_bundle_thumbnail); 
			 	thumb.setImageBitmap(bmp);
			 	thumb.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
			 	
			 	double w = screenWidth/1.43;
			 	double h = screenHeight/2.2;
			 	
			 	//Leave web bundle size as it is for now
			 	thumb.setLayoutParams(new LayoutParams((int) w, (int) h));	//240x240
			 	//Thumb.setText("WEB BUNDLE");
			 	page.addView(thumb);
			 	
			 	//New custom view that adds a bit of spacing to the end of image items
				//View view = new View(this);
				//view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				//page.addView(view);
			 	
			 	thumb.setOnLongClickListener(new OnLongClickListener() {
		             @Override
		             public boolean onLongClick(View v) {
		                 
		            	//Vibration to alert users
			            //Get instance of Vibrator from current Context
			            Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
			            //Vibrate for 300 milliseconds
			            vib.vibrate(300);
		            	 
		            	 //Display the web site in a new view (call the WebBundleViewer)
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
			            	
	     	        	 return true;
		            	
		             } //end of public void
		 
		         });
		    	
		    }
		    
				
		    
		    /*
		    * Method to Parse the config.xml file and store the different types of Items into the ArrayList Variables
		    */
			private String getMyXML() throws Exception {
						
				StringBuffer inLine = new StringBuffer();
				//Get a SAXParser from the SAXPArserFactory
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();

				//Get the XMLReader of the SAXParser we created
				XMLReader xr = sp.getXMLReader();
				//Create a new ContentHandler and apply it to the XML-Reader
				XMLHandler myExampleHandler = new XMLHandler();
				xr.setContentHandler(myExampleHandler);
								
				FileInputStream in = new FileInputStream(unzippedRoot + packagePath + File.separator + configFilename);
				xr.parse(new InputSource(in));

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
				pbkey = book.getKey();		//The book key (folder name) is also stored in the config.xml file - so we can pull it out from that
				pbtimestamp = book.getTimestamp();	//The book's DOB
				
				page1 = (ArrayList<Point>)book.getPage1();
				page2 = (ArrayList<Point>)book.getPage2();
				page3 = (ArrayList<Point>)book.getPage3();
				page4 = (ArrayList<Point>)book.getPage4();
				page5 = (ArrayList<Point>)book.getPage5();
				page6 = (ArrayList<Point>)book.getPage6();
			
				in.close();
				
				return inLine.toString();    
				
			}
			
			public void populatePages(){
				//Pass the data into the data ArrayLists
				for(Point item: page1) {
					
					String type = item.getType();	//Types - Text, Audio, Video, Images..etc
					String data = item.getData();	//Can be Text, Filename (e.g 123.jpg, song.mp3)..etc 
		        	String itemKey = item.getItemKey();		//Unique ID key for each item
		        	String url = item.getUrl();			//Sometimes an item can have a url
		        	Coordinate[] geomCo = item.getGeometryCoordinates();	//Some items are geotagged and have geometries
		        	String gpxFilename = item.getGpxFilename();				//Trails have a gpx file name. In this case data = map filename
		        	int height = item.getImageHeight();
		        	
					if (type.equalsIgnoreCase("Text")){
						displayText(data, ll);
					}
					else if (type.equalsIgnoreCase("Image")){
						
							if(geomCo!=null && data!=null){
								try{
									alGeoImageFilename.add(data);
									for (int i=0; i<geomCo.length; i++){
										alGeoImageCoordinate.add(geomCo[i]);
									}
	
								}
								catch(Exception e){
									e.printStackTrace();
								}
								displayImage(data.toString(),height,ll);
							}
							
						//displayImage(data.toString(),height,ll);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll);	
					}
					
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll );
					}
					else if (type.equalsIgnoreCase("MapImage")){

							displayMapImage(data, geomCo, gpxFilename, ll);
							
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
					
					}*/
					
				}
				
				for(Point item: page2) {
		        	String type = item.getType();
		        	String data = item.getData();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	Coordinate[] geomCo = item.getGeometryCoordinates();
		        	String gpxFilename = item.getGpxFilename();
		        	int height = item.getImageHeight();

		        	if (type.equalsIgnoreCase("Text")){
						displayText(data, ll2);
					}
					else if (type.equalsIgnoreCase("Image")){

						if(geomCo!=null && data!=null){
							try{
								alGeoImageFilename.add(data);
								for (int i=0; i<geomCo.length; i++){
									alGeoImageCoordinate.add(geomCo[i]);
								}
								
							}
							catch(Exception e){
								e.printStackTrace();
							}
							displayImage(data.toString(), height, ll2);

						}
						//displayImage(data.toString(), height, ll2);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll2);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll2);	
					}
					else if (type.equalsIgnoreCase("MapImage")){
						displayMapImage(data, geomCo, gpxFilename, ll2);
					}
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll2);
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
						//Toast msg = Toast.makeText(Reader.this, "data= \n" + gpxData + "\n key= " + itemKey, Toast.LENGTH_LONG);
						//msg.show();
					}*/
		        

				}
				for(Point item: page3) {
		        	String type = item.getType();
		        	String data = item.getData();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	Coordinate[] geomCo = item.getGeometryCoordinates();
		        	String gpxFilename = item.getGpxFilename();
		        	int height = item.getImageHeight();

		        	if (type.equalsIgnoreCase("Text")){
						displayText(data, ll3);
					}
					else if (type.equalsIgnoreCase("Image")){
						
						if(geomCo!=null && data!=null){
							try{
								alGeoImageFilename.add(data);
								for (int i=0; i<geomCo.length; i++){
									alGeoImageCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
							displayImage(data.toString(),height, ll3);

						}
						//displayImage(data.toString(),height, ll3);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll3);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll3);	
					}
					else if (type.equalsIgnoreCase("MapImage")){
						displayMapImage(data, geomCo, gpxFilename, ll3);
					}
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll3);
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
						//Toast msg = Toast.makeText(Reader.this, "data= \n" + gpxData + "\n key= " + itemKey, Toast.LENGTH_LONG);
						//msg.show();
					}*/		        	
		        	

				}
				for(Point item: page4) {
		        	String type = item.getType();
		        	String data = item.getData();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	Coordinate[] geomCo = item.getGeometryCoordinates();
		        	String gpxFilename = item.getGpxFilename();
		        	int height = item.getImageHeight();

		        	if (type.equalsIgnoreCase("Text")){
						displayText(data, ll4);
					}
					else if (type.equalsIgnoreCase("Image")){
						
						if(geomCo!=null && data!=null){
							try{
								alGeoImageFilename.add(data);
								for (int i=0; i<geomCo.length; i++){
									alGeoImageCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
							displayImage(data.toString(),height, ll4);
						}
						//displayImage(data.toString(),height, ll4);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll4);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll4);	
					}
					else if (type.equalsIgnoreCase("MapImage")){
						displayMapImage(data, geomCo, gpxFilename, ll4);
					}
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll4);
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
						//Toast msg = Toast.makeText(Reader.this, "data= \n" + gpxData + "\n key= " + itemKey, Toast.LENGTH_LONG);
						//msg.show();
					}*/	
		        	

				}
				for(Point item: page5) {
		        	String type = item.getType();
		        	String data = item.getData();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	Coordinate[] geomCo = item.getGeometryCoordinates();
		        	String gpxFilename = item.getGpxFilename();
		        	int height = item.getImageHeight();

		        	if (type.equalsIgnoreCase("Text")){
						displayText(data, ll5);
					}
					else if (type.equalsIgnoreCase("Image")){
						
						if(geomCo!=null && data!=null){
							try{
								alGeoImageFilename.add(data);
								for (int i=0; i<geomCo.length; i++){
									alGeoImageCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
							displayImage(data.toString(),height, ll5);
						}
						//displayImage(data.toString(),height, ll5);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll5);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll5);	
					}
					else if (type.equalsIgnoreCase("MapImage")){
						displayMapImage(data, geomCo, gpxFilename, ll5);
					}
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll5);
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
						//Toast msg = Toast.makeText(Reader.this, "data= \n" + gpxData + "\n key= " + itemKey, Toast.LENGTH_LONG);
						//msg.show();
					}*/	

				}
				for(Point item: page6) {
		        	String type = item.getType();
					String data = item.getData();
		        	String itemKey = item.getItemKey();
		        	String url = item.getUrl();
		        	//Geometry geom = item.getGeometryCoordinates();
		        	Coordinate[] geomCo = item.getGeometryCoordinates();
		        	String gpxFilename = item.getGpxFilename();
		        	int height = item.getImageHeight();

		        	if (type.equalsIgnoreCase("Text")){
						displayText(data, ll6);
					}
					else if (type.equalsIgnoreCase("Image")){
						
						if(geomCo!=null && data!=null){
							try{
								alGeoImageFilename.add(data);
								for (int i=0; i<geomCo.length; i++){
									alGeoImageCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
							displayImage(data.toString(),height, ll6);
						}
						//displayImage(data.toString(),height, ll6);
					}
					else if (type.equalsIgnoreCase("Video")){
						displayVideo(data, ll6);
					}
					else if (type.equalsIgnoreCase("Audio")){
						displayAudio(data, ll6);	
					}
					else if (type.equalsIgnoreCase("MapImage")){
						displayMapImage(data, geomCo, gpxFilename, ll6);
					}
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey, ll6);
					}
					/*else if (type.equalsIgnoreCase("GPSTrace")){
						//Toast msg = Toast.makeText(Reader.this, "data= \n" + gpxData + "\n key= " + itemKey, Toast.LENGTH_LONG);
						//msg.show();
					}*/

				}
				
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
			                //Right to left swipe
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
			 
		//Important. makes sure that in the activity, it is catching the gesture event by overriding the onTouch() method	 
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
			    public void onDestroy(){
			    	super.onDestroy();
			    	
			    	//Stop audio only if audio exists
			    	if (audio_included){
			    		stopAudio();
			    		//Reset the audio flag
			    		audio_included = false;
			    		
			    	}
			       
			    	System.gc();	//Call the garbage collector

		    	
			    }
			 

			
} //End of class



	 