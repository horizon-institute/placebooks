package org.placebooks.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.placebooks.www.XMLHandler;
import org.placebooks.www.Book;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.Log;
//import android.view.KeyEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
import android.view.View.OnLongClickListener;
import android.widget.ViewFlipper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;

import org.placebooks.www.PageIndicator;
import org.placebooks.www.Pager;
import com.vividsolutions.jts.geom.Coordinate;
import 	android.net.Uri;
import java.io.FileNotFoundException;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import org.apache.commons.lang.StringEscapeUtils;
import android.webkit.*;
import android.graphics.Color;

import java.util.List;
import java.util.Locale;
import java.util.Vector;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;


//Implement a Listener (added the interface to the base class)
public class Reader extends FragmentActivity {
	
	
	
	//private View pageView ;
	private LinearLayout pageView;
	private ScrollView pageScroll;
	private Pager scroller;
	private PageIndicator indicator;
	private PagerAdapter mPagerAdapter;
	
    private LayoutInflater layoutInflater;
	
	private int pageCounter = 0;
	
	private int screenWidth;	//Mobile screen width resolution
	private int screenHeight;	//Mobile screen height resolution
	
	
	//private ScrollView svFrontCover;
	//private LinearLayout llFrontCover;
	//ScrollView and LinearLayout
	//private ScrollView sv;		//Scroll view that wraps the linear layout
	//private ScrollView sv2;		//Scroll view for page 2
	//private ScrollView sv3;	
	//private LinearLayout ll;	//Main linear layout page 1
	//private LinearLayout ll2;	//Linear layout page 2
	//private LinearLayout ll3;
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
    
    private ArrayList<String> pageFilenames = new ArrayList<String>();
	
    private ArrayList<Point> currentPage = new ArrayList<Point>();
    private ArrayList<Point> nextPage = new ArrayList<Point>();
    private ArrayList<Point> previousPage = new ArrayList<Point>();
    private ArrayList<Point> pageCol1 = new ArrayList<Point>();
    private ArrayList<Point> pageCol2 = new ArrayList<Point>();
    private ArrayList<ArrayList> allPages = new ArrayList<ArrayList>();
    
    //Geotagged media
    private ArrayList<String> alGeoTextData = new ArrayList<String>();
	private ArrayList<Integer> alGeoTextMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoTextMapMarker = new ArrayList<Integer>();
	private ArrayList<Coordinate> alGeoTextCoordinate = new ArrayList<Coordinate>();
    
    private ArrayList<String> alGeoImageFilename = new ArrayList<String>();
	private ArrayList<Coordinate> alGeoImageCoordinate = new ArrayList<Coordinate>();
	private ArrayList<Integer> alGeoImageMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoImageMapMarker = new ArrayList<Integer>();
	
	private ArrayList<String> alGeoVideoFilename = new ArrayList<String>();
	private ArrayList<Coordinate> alGeoVideoCoordinate = new ArrayList<Coordinate>();
	private ArrayList<Integer> alGeoVideoMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoVideoMapMarker = new ArrayList<Integer>();

	private ArrayList<String> alGeoAudioFilename = new ArrayList<String>();
	private ArrayList<Coordinate> alGeoAudioCoordinate = new ArrayList<Coordinate>();
	private ArrayList<Integer> alGeoAudioMapPage = new ArrayList<Integer>();
	private ArrayList<Integer> alGeoAudioMapMarker = new ArrayList<Integer>();
	
	//Video Variables
	private ImageButton ibVid;
	
	//Audio Variables
	private MediaPlayer mp = new MediaPlayer();
	private ImageButton ibAudioPlay; 
	private ImageButton ibAudioPause;
	private ImageButton ibAudioStop;
	private boolean audio_included = false;	//audio flag
	
	//Map Image Variables
	//private ImageView mapImgView;
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

	//private ArrayList<Double> gpsLatCoordinates = new ArrayList<Double>();
	//private ArrayList<Double> gpsLonCoordinates = new ArrayList<Double>();
	//private double[] arrGpsLatCoordinates;
	//private double[] arrGpsLonCoordinates;
	
	//public double[][] arrGps = { arrGpsLatCoordinates, arrGpsLonCoordinates };

	
	
	//Application variables
	private String unzippedDir;
	private String unzippedRoot;
	private String configFilename;
	
	//Book page variables
	private boolean hasPreviousPage = false;
	private boolean hasNextPage = true;
	private int pageNumberAt = 0;
	//private FileInputStream in;
	//private FileInputStream inNext;
	//private FileInputStream inPrevious;
	
	private String languageSelected;
	
	 		 @Override
	     	 public void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);
			        getWindow().setFormat(PixelFormat.TRANSLUCENT);
			        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen

			        CustomApp appState = ((CustomApp)getApplicationContext());
			        unzippedDir = appState.getUnzippedDir();
			        unzippedRoot = appState.getUnzippedRoot();
			        configFilename = appState.getConfigFilename();
			        
			        languageSelected  = appState.getLanguage();  
			        Locale locale = new Locale(languageSelected);   
			        Locale.setDefault(locale);  
			        Configuration config = new Configuration();  
			        config.locale = locale;  
			        getBaseContext().getResources().updateConfiguration(config,   
			        getBaseContext().getResources().getDisplayMetrics()); 
			        
			        //Get mobile screen resolution
			        DisplayMetrics dm = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(dm);
			        
			        //Assign the resolution to the variables
			        
			        screenWidth = dm.widthPixels;	//320px on the LG phone
			        screenHeight = dm.heightPixels;	//480px on the LG phone
			        System.out.println("Screen width = "+screenWidth + " ,Screen height = " +screenHeight);
			        
			        //Get the extras (package path) out of the new intent
			        //Retrieve the packagePath.
			        Intent intent = getIntent();
			        if(intent != null) packagePath = intent.getStringExtra("packagePath");
			        
			        //Set the content view to the xml reader file
			        setContentView(R.layout.reader);
			        
			        
			        scroller = ((Pager)findViewById(R.id.scrollView));
			        //indicator = ((PageIndicator)findViewById(R.id.indicator));
			        //indicator.setPager(scroller);
			        
			        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        

			        
			        try {
				       //AsyncTask thread processes the xml and then populates the pages
			        new RenderPage().execute();			        	
				    
					} 

					catch (Exception e) {
						TextView tv = new TextView(this);
						tv.setText(e.getMessage());
						setContentView(tv);
					} 
					
												
			

	 		 } //End of onCreate() Method
	 		 
	 		 
	 
	 		 /*
	 		 * Method for displaying the Image Items
	 		 */
			 private void displayImage(final String img, final int height, final int marker){
				 
				    ImageView imgView = new ImageView(this);
			    	RelativeLayout rLayout = new RelativeLayout(this);
			    	ImageView imgMarker = new ImageView(this);
			    	TextView text=new TextView(this); 


				    if (marker != -1){
				    	//RelativeLayout rLayout = new RelativeLayout(this);
				    	LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
				    	        ,LayoutParams.FILL_PARENT); 
				    	rLayout.setLayoutParams(rlParams);
				    	
				        Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
				    	//ImageView imgMarker = new ImageView(this);
				    	imgMarker.setImageBitmap(bmpMarker);
				    	imgMarker.setLayoutParams(rlParams);
				    	
				    	RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
				        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				    	//TextView text=new TextView(this); 
				    	text.setText(Integer.toString(marker)); 
				    	text.setTextColor(Color.BLACK);  
				    	text.setLayoutParams(tParams);

				    	
				    	//rLayout.addView(imgMarker);
				    	//rLayout.addView(text);
				    	//pageView.addView(rLayout);
				    	
				    }
				 				  
					String myImagePath = unzippedDir + packagePath + File.separator + img;
					
					File imageFile = new File(myImagePath);
					if (imageFile.exists()){
					
						try {
	
								BitmapFactory.Options options = new BitmapFactory.Options();
							    options.inSampleSize = 2;	//WAS 4 (STABLE) TRYING 2 TO SEE WHAT PERFORMANCE IS LIKE
							    Bitmap bm = BitmapFactory.decodeFile(myImagePath, options);
							    
							    //imgView.setImageDrawable(Drawable.createFromPath(myImagePath));
						    	imgView.setImageBitmap(bm);
						    	

							   
							    if(bm.getHeight() >0 && bm.getWidth() >0 && screenWidth >0){

								    /*
								    if( height > 6000){
								    	imgView.setLayoutParams(new LayoutParams(screenWidth-35, screenHeight-55));
								    }
								   
								    else{
								    	//imgView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
								    	//imgView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));								    	
								    	//imgView.setLayoutParams(new LayoutParams(screenWidth-35, LayoutParams.WRAP_CONTENT));
								    	
								    }*/
							    	imgView.setLayoutParams(new LayoutParams(350, 350));	

							    	//imgView.setLayoutParams(new LayoutParams(screenWidth-35, LayoutParams.WRAP_CONTENT));

							    	//rLayout.addView(imgView);
								    if(marker != -1){
								    	rLayout.addView(imgMarker);
								    	rLayout.addView(text);
								    	pageView.addView(rLayout);
										pageView.addView(imgView);
								    }
								    else{
								    	pageView.addView(imgView);
								    }

							    }
							    
						}
					
							   catch (OutOfMemoryError E) {
								       // Release some (all) of the above objects
										System.out.println("Out of Memory Exception");
										TextView txtView = new TextView(Reader.this);
										txtView.setText("Error: cannot load image. Out of memory!");
										pageView.addView(txtView);
									}
						}
						else{
							//TextView t = new TextView(this);
					    	//t.setText("Error: image was not downloaded properly");
					    	//pageView.addView(t);
						}
					
					//New custom view that adds a bit of spacing to the end of image items
				    View view = new View(this);
				    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				    pageView.addView(view);    
				    
				    //imgView.setLongClickable(true);
					imgView.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
			            	 //Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
			            	 //vib.vibrate(300);
			            	 
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.ImageViewer");
	        	        	 //intent.putExtra("image", img);
	        	        	 //intent.putExtra("path", packagePath);
	        	        	 intent.putExtra("imagePath",  unzippedDir + packagePath + File.separator + img);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	         
	        	        	 //return true;
			            	
			             } //End of public void
				 
						}); 
								
			 }
			 
			/*
			* Method for displaying the Text Items
			*/			 
			private void displayText(final String text, final int marker){
				  
				System.out.println("Marker === " + marker);
				 if (marker != -1){
					 RelativeLayout rLayout = new RelativeLayout(this);
				     LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
				     ,LayoutParams.FILL_PARENT); 
				     rLayout.setLayoutParams(rlParams);
				    	
				     Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
				   	 ImageView imgMarker = new ImageView(this);
				     imgMarker.setImageBitmap(bmpMarker);
				     imgMarker.setLayoutParams(rlParams);
				     
				     RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
				        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				    	TextView text2=new TextView(this); 
				    	text2.setText(Integer.toString(marker)); 
				    	text2.setTextColor(Color.BLACK);  
				    	text2.setLayoutParams(tParams);
				    	
				    	rLayout.addView(imgMarker);
				    	rLayout.addView(text2);
				    	pageView.addView(rLayout);
				 }
				 
					 String escapedHtml = StringEscapeUtils.escapeHtml(text);
					 WebView wv = new WebView(this);
					 wv.loadData(text, "text/html", "utf-8");
					 wv.setBackgroundColor(0x00000000);
					 pageView.addView(wv);
					 //TextView tv = new TextView(this);
					 //tv.setText(escapedHtml);
					 //pageView.addView(tv);


			}
			 
			/*
			 * Method for displaying the Video Items		 
			 */
			private void displayVideo(final String video, final int marker){
				
				if (marker != -1){			    	
			    	RelativeLayout rLayout = new RelativeLayout(this);
			    	LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
			    	        ,LayoutParams.FILL_PARENT); 
			    	rLayout.setLayoutParams(rlParams);
			    	
			        Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
			    	ImageView imgMarker = new ImageView(this);
			    	imgMarker.setImageBitmap(bmpMarker);
			    	imgMarker.setLayoutParams(rlParams);
			    	
			    	RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
			        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			    	TextView text=new TextView(this); 
			    	text.setText(Integer.toString(marker)); 
			    	text.setTextColor(Color.BLACK);  
			    	text.setLayoutParams(tParams);

			    	
			    	rLayout.addView(imgMarker);
			    	rLayout.addView(text);
			    	pageView.addView(rLayout);
			    }
				
				//Make the button and thumbnail look like it is a TV (simple metaphor for users to understand it is a video clip)
				String myVideoPath = unzippedDir + packagePath + File.separator + video;
				File videoFile = new File(myVideoPath);
				if (videoFile.exists()){
				 					 
					 //Locate the video and get the thumbnail image of the video file
					 try{
						 Bitmap thumb = android.media.ThumbnailUtils.createVideoThumbnail(unzippedDir + packagePath + File.separator + video, MediaStore.Images.Thumbnails.MINI_KIND);
						 double w = screenWidth/1.33;	//ratio
						 double h = screenHeight/3;		//ratio
						 
						 if(thumb != null){
							 Bitmap scaledThumb = Bitmap.createScaledBitmap(thumb, (int) w, (int) h, true);	//240, 160 scale the bitmap to the right size of the button
							 ibVid = new ImageButton(Reader.this);
							 
							 if(scaledThumb != null){
							    //Assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
							    ibVid.setImageBitmap(scaledThumb);
							    //ibVid.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
								 double w2 = screenWidth/1.23;
								 double h2 = screenHeight/2.66;
								 ibVid.setLayoutParams(new LayoutParams((int) w2,(int) h2));
								 pageView.addView(ibVid); 
							 }
						 }
						 else{
							 ibVid = new ImageButton(Reader.this);
							 //ibVid.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
							 ibVid.setImageDrawable(this.getResources().getDrawable(R.drawable.videocontent));
							 double w2 = screenWidth/1.23;
						 	 double h2 = screenHeight/2.66;
						 	 ibVid.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));//(int) w2,(int) h2));
						 	 pageView.addView(ibVid); 
						 }
						 
					 } catch (OutOfMemoryError E) {
						    //Release some (all) of the above objects
								System.out.println("Out of Memory Exception");
								TextView txtView = new TextView(Reader.this);
								txtView.setText("Error: cannot load video. Out of memory!");
								pageView.addView(txtView);
					 	}
					 
					//New custom view that adds a bit of spacing to the end of image items
					View view = new View(this);
					view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
					pageView.addView(view);
					
					/*
					* Differentiate between which Listener was pressed by comparing the View reference.
					*/
					ibVid.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
				            //Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
				            //vib.vibrate(300);
			            	 
			            	 //Play the video - calls VideoViewer class
			            	 //Create a new intent based on the VideoViewer class
			            	 Intent intent = new Intent();
		     				 	overridePendingTransition(0, 0);
		     				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.VideoViewer");
	        	        	 intent.putExtra("video", video);
	        	        	 intent.putExtra("path", unzippedDir+packagePath);
		     				    overridePendingTransition(0, 0);
	        	        	 startActivity(intent);	
	        	        	 
	        	        	 //return true;
			            	
			             } //end of public void
				 
						});
					
				}//End of if videoFile exists
				
			} //End of displayVideo method
			
	
			
			
			//Start of audio methods
			  /*
			   * Method for displaying audio items
			   */
		    public void displayAudio(final String audio, final int marker){
		    	
		    	System.out.println("Marker === " + marker);
				 if (marker != -1){
					 RelativeLayout rLayout = new RelativeLayout(this);
				     LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
				     ,LayoutParams.FILL_PARENT); 
				     rLayout.setLayoutParams(rlParams);
				    	
				     Bitmap bmpMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker);
				   	 ImageView imgMarker = new ImageView(this);
				     imgMarker.setImageBitmap(bmpMarker);
				     imgMarker.setLayoutParams(rlParams);
				     
				     RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
				        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				    	tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				    	tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				    	TextView text2=new TextView(this); 
				    	text2.setText(Integer.toString(marker)); 
				    	text2.setTextColor(Color.BLACK);  
				    	text2.setLayoutParams(tParams);
				    	
				    	rLayout.addView(imgMarker);
				    	rLayout.addView(text2);
				    	pageView.addView(rLayout);
				 }
		    	
		    	String myAudioPath = unzippedDir + packagePath + File.separator + audio;

		    	File audioFile = new File(myAudioPath);
				if (audioFile.exists()){
		    	
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
		    	 
		    	 pageView.addView(llAudio);	//Add the audio linear layout to the main linear layout

		    	 
		    	
					
		    	/* 
		    	 TextView tv = new TextView(this);
				 tv.setText("Audio File: " + audio);
				 tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				 pageView.addView(tv);
		    	 */
				 
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
				}//end of if file exists 
				         		    	
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
		    public void displayMapImage(final String mapImageFilename, final Coordinate[] mapCoordinates, final String gpxFilename, final int pageNumber){
		    	
			    //Locate the file path where the images are stored on the SD CARD. 
				String myMapImagePath = unzippedDir + packagePath + File.separator + mapImageFilename;
				ImageView mapImgView = new ImageView(this);
				
				
				File mapFile = new File(myMapImagePath);
				if (mapFile.exists()){
				
				
					//String filename = "file://"+ myMapImagePath;
					//String mapImagePath = "<img height='230' width='230' src=\""+filename+"\">";
					//htmlPageContents.append(mapImagePath);
				//}
									    
				 	
				    //(1) Display map thumbnail and get the map coordinates
				    if(mapImageFilename != null && myMapImagePath != null){
						try{ 
							
							BitmapFactory.Options options = new BitmapFactory.Options();
						    options.inSampleSize = 4;
						    Bitmap bm = BitmapFactory.decodeFile(myMapImagePath, options);
							Uri imgUri=Uri.parse(myMapImagePath);
						    mapImgView.setImageURI(imgUri);
						    //mapImgView.setImageBitmap(bm);
						    /*
						    double w = screenWidth/1.28;	//ratio
						    double h = screenHeight/1.92;	//ratio
							mapImgView.setLayoutParams(new LayoutParams((int) w, (int) h));	//350, 350 htc hd
							*/
						 
							mapImgView.setLayoutParams(new LayoutParams(screenWidth-75, screenWidth-75));
							pageView.addView(mapImgView);
							
							System.out.println("MapCoordinates == " + mapCoordinates);
							if(mapCoordinates != null){
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
							}
							else{
								Log.d("Reader.class","mapCoordinates = null");
							}
							
							
				    	} catch (OutOfMemoryError E) {
					    //Release some (all) of the above objects
							System.out.println("Out of Memory Exception");
							TextView txtView = new TextView(Reader.this);
							txtView.setText("Error: cannot load map file. Out of memory!");
							//page.addView(txtView);
							pageView.addView(txtView);
						}
				    }
				    
				    else{
				    	TextView t = new TextView(this);
				    	t.setText("Error: cannot read map");
				    	//page.addView(t);
				    	pageView.addView(t);
				    }
				}
				else{
					TextView t = new TextView(this);
			    	t.setText("Error: map image was not downloaded properly");
			    	//page.addView(t);
			    	pageView.addView(t);
				}
			    		    
				 
				//New custom view that adds a bit of spacing to the end of image items
				View view = new View(this);
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				pageView.addView(view);
				
				
				 // Differentiate between which Listener was pressed by comparing the View reference.
				 
				 //Has a map
					mapImgView.setOnClickListener(new OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 
				 		if(mapImageFilename!=null){

				            //Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
				            //vib.vibrate(300);
	
				            	 
			            	 Intent intent = new Intent();
		     				 overridePendingTransition(0, 0);
		     				 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	
	       	        	 intent.setClassName("org.placebooks.www", "org.placebooks.www.MapImageViewer");
	       	        	 intent.putExtra("mapImageFilename", mapImageFilename);
	       	        	 intent.putExtra("packagePath", unzippedDir + packagePath);
	       	        	 System.out.println("PackagePath+mapImage = " + unzippedDir + packagePath +mapImageFilename );
	
	       	        	 
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
	       	        	 
	       	        	 intent.putExtra("gpxFilename", gpxFilename);
	       /*	        	 
	       	        	 if (gpxFilename != null){
		       	        	 //Pass the gps trail lat/lons
		       	        	 intent.putExtra("arrLat", arrGpsLatCoordinates);
		       	        	 intent.putExtra("arrLon", arrGpsLonCoordinates);
	       	        	 }
	       */	        	 
	       	        	 //Pass the geotagged media
	       	        	intent.putExtra("alGeoTextData", alGeoTextData);
	       	        	 intent.putExtra("alGeoTextMapPage", alGeoTextMapPage);
	       	        	 intent.putExtra("alGeoTextMapMarker",alGeoTextMapMarker);
	       	        	 int numTextCoords = alGeoTextCoordinate.size();
		       	         intent.putExtra("text_coord_size", numTextCoords);
		       	         intent.putExtra("pageNumber", pageNumber);
		       	         for (int i = 0; i < numTextCoords; i++)
		       	         {
		       	             Coordinate coord = alGeoTextCoordinate.get(i);
		       	             intent.putExtra("text_coord_x_" + i, coord.x);
		       	             intent.putExtra("text_coord_y_" + i, coord.y);
		       	         }
		       	         
		       	         intent.putExtra("alGeoAudioFilename", alGeoAudioFilename);
	       	        	 intent.putExtra("alGeoAudioMapPage", alGeoAudioMapPage);
	       	        	 intent.putExtra("alGeoAudioMapMarker",alGeoAudioMapMarker);
	       	        	 int numAudioCoords = alGeoAudioCoordinate.size();
		       	         intent.putExtra("audio_coord_size", numAudioCoords);
		       	         intent.putExtra("pageNumber", pageNumber);
		       	         for (int i = 0; i < numAudioCoords; i++)
		       	         {
		       	             Coordinate coord = alGeoAudioCoordinate.get(i);
		       	             intent.putExtra("audio_coord_x_" + i, coord.x);
		       	             intent.putExtra("audio_coord_y_" + i, coord.y);
		       	         }
	       	        	 
	       	        	 
	       	        	 intent.putExtra("alGeoImageFilename", alGeoImageFilename);
	       	        	 intent.putExtra("alGeoImageMapPage", alGeoImageMapPage);
	       	        	 intent.putExtra("alGeoImageMapMarker",alGeoImageMapMarker);
	       	        	 int numCoords = alGeoImageCoordinate.size();
		       	         intent.putExtra("coord_size", numCoords);
		       	         intent.putExtra("pageNumber", pageNumber);
		       	         for (int i = 0; i < numCoords; i++)
		       	         {
		       	             Coordinate coord = alGeoImageCoordinate.get(i);
		       	             intent.putExtra("coord_x_" + i, coord.x);
		       	             intent.putExtra("coord_y_" + i, coord.y);
		       	         }
		       	         
		       	         intent.putExtra("alGeoVideoFilename", alGeoVideoFilename);
	       	        	 intent.putExtra("alGeoVideoMapPage", alGeoVideoMapPage);
	       	        	 intent.putExtra("alGeoVideoMapMarker",alGeoVideoMapMarker);
	       	        	 int numVidCoords = alGeoVideoCoordinate.size();
	       	        	 intent.putExtra("vid_coord_size", numVidCoords);
	       	        	 System.out.println("vid_coord_size ======== " + numVidCoords);
	       	        	for (int i = 0; i < numVidCoords; i++)
		       	         {
		       	             Coordinate coord = alGeoVideoCoordinate.get(i);
		       	             intent.putExtra("vid_coord_x_" + i, coord.x);
		       	             intent.putExtra("vid_coord_y_" + i, coord.y);
		       	        	 System.out.println("vid_coord_x_ ======== " + coord.x);
		       	        	 System.out.println("vid_coord_y_ ======== " + coord.y);

		       	         }
	
		     		     overridePendingTransition(0, 0);
		 					//start activity
		     		     startActivity(intent);	
		 				} //end of if mapImageFilename!=null
		 				else{
						  Toast msg = Toast.makeText(Reader.this, "Error: No Map Trail", Toast.LENGTH_LONG);
		 				  msg.show();

		 				}
	       	        	 
	       	        	 //return true;
			            	
			             } //End of public void
				 
						});

			    	
			    }
		    
		 
		    
		    /*
		     * Web Bundle Item
		     * Method for displaying the web bundle
		     */
		    public void displayWebBundle(final String filename, final String url, final String itemKey){
		    	
			 	//ImageButton thumb = new ImageButton(this);
			 	ImageButton thumb = new ImageButton(this);
			 	
			 	
			 	Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.web_bundle_thumbnail); 
			 	thumb.setImageBitmap(bmp);
			 	thumb.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
			 	
			 	double w = screenWidth/1.43;
			 	double h = screenHeight/2.2;
			 	
			 	//Leave web bundle size as it is for now
			 	thumb.setLayoutParams(new LayoutParams((int) w, (int) h));	//240x240
			 	//Thumb.setText("WEB BUNDLE");
			 	pageView.addView(thumb);
			 	
			 	//New custom view that adds a bit of spacing to the end of image items
				View view = new View(this);
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
				pageView.addView(view);
			 	
			 	thumb.setOnClickListener(new OnClickListener() {
		             @Override
		             public void onClick(View v) {
		                 
			            //Vibrator vib = (Vibrator) getSystemService(Reader.this.VIBRATOR_SERVICE);
			            //vib.vibrate(300);
		            	 
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
			            	
	     	        	 //return true;
		            	
		             } //end of public void
		 
		         });
		    	
		    }
		    
		    private void getConfigXML() throws Exception{
		    	
		    	try{

                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    SAXParser sp = spf.newSAXParser();

                    //Get the XMLReader of the SAXParser we created
                    XMLReader xr = sp.getXMLReader();
                    //Create a new ContentHandler and apply it to the XML-Reader
                    XMLConfigHandler xmlHandler = new XMLConfigHandler();
                    xr.setContentHandler(xmlHandler);
                   
                    // Parse the xml-data from the file
    				FileInputStream in = new FileInputStream(unzippedRoot + packagePath + File.separator + configFilename);
                    xr.parse(new InputSource(in));
                    //Parsing has finished.

                    Binder parsedExampleDataSet = xmlHandler.getParsedData();
                    
                    ArrayList<String> al = parsedExampleDataSet.getAlPages();
                    
                    for(int i=0; i< al.size(); i++)
                    {   
                    	pageFilenames.add(al.get(i));
                    	System.out.println(al.get(i));
                    }
		    	
		    	}
				catch(Exception e){
                    Log.e("Reader->getConfigXML", "Exception", e);	
				}
				
		    }
		    
		    /*
		    * Method to Parse the config.xml file and store the different types of Items into the ArrayList Variables
		    */
			private String getMyXML() throws Exception {
									
				
				StringBuffer inLine = new StringBuffer();
		//		StringBuffer inLinePrevious = new StringBuffer();
		//		StringBuffer inLineNext = new StringBuffer();
				//Get a SAXParser from the SAXPArserFactory
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				
				//SAXParserFactory spf2 = SAXParserFactory.newInstance();
				//SAXParser sp2 = spf2.newSAXParser();
				
				//SAXParserFactory spf3 = SAXParserFactory.newInstance();
				//SAXParser sp3 = spf3.newSAXParser();

				//Get the XMLReader of the SAXParser we created
				XMLReader xr = sp.getXMLReader();
				//XMLReader xrNext = sp2.getXMLReader();
				//XMLReader xrPrevious = sp3.getXMLReader();
				
				//Create a new ContentHandler and apply it to the XML-Reader
				XMLHandler myExampleHandler = new XMLHandler();
				xr.setContentHandler(myExampleHandler);
				//XMLHandler myExampleHandlerNext = new XMLHandler();
				//xrNext.setContentHandler(myExampleHandlerNext);
				//XMLHandler myExampleHandlerPrevious = new XMLHandler();
				//xrPrevious.setContentHandler(myExampleHandlerPrevious);
				
				
				for (int i=0; i<pageFilenames.size(); i++){
				
					System.out.println("page filename === " + pageFilenames.get(i));
					FileInputStream in = new FileInputStream(unzippedRoot + packagePath + File.separator + pageFilenames.get(i));
					xr.parse(new InputSource(in));
					
					
					Book thePage = myExampleHandler.getParsedData();
					
						try{
							inLine.append(thePage.toString());	

						}
						catch(NullPointerException npe){
							Log.e("TRACE = ",npe.getMessage());
							System.out.println("Null pointer exception has been caught");
							TextView textView = new TextView(Reader.this);
							textView.setText("Error: Null Pointer Exception");
							setContentView(textView);
						}
						
						
						pageCol1 = (ArrayList<Point>)thePage.getPage();
						System.out.println("pageCol1 ===== " + pageCol1);
						pageCol2 = (ArrayList<Point>)thePage.getPage2();
						System.out.println("pageCol2 ===== " + pageCol2);
						allPages.add(pageCol1);
						allPages.add(pageCol2);
						
						in.close();
						
				}
				
				
				
				return "";//inLine.toString();    
				
			}
			
			
			public void populatePage(ArrayList<Point> page, int pageNumber){
		
				
				for(Point item: page) {
					
					String type = item.getType();	//Types - Text, Audio, Video, Images..etc
					String data = item.getData();	//Can be Text, Filename (e.g 123.jpg, song.mp3)..etc 
		        	String itemKey = item.getItemKey();		//Unique ID key for each item
		        	String url = item.getUrl();			//Sometimes an item can have a url
		        	Coordinate[] geomCo = item.getGeometryCoordinates();	//Some items are geotagged and have geometries
		        	String gpxFilename = item.getGpxFilename();				//Trails have a gpx file name. In this case data = map filename
		        	int height = item.getImageHeight();
		        	int mapPage = item.getMapPage();
		        	int marker = item.getMapMarker();
		        	System.out.println("Map Marker === " + marker);
		        	
					if (type.equalsIgnoreCase("Text")){
						//displayText(data);
						if(geomCo!=null && data!=null && mapPage != -1 && marker != -1){
							try{
								alGeoTextData.add(data);
								alGeoTextMapPage.add(mapPage);
								alGeoTextMapMarker.add(marker);
								for (int i=0; i<geomCo.length; i++){
									alGeoTextCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
					if(data != null){	
					displayText(data, marker);
					}
						
					}
					else if (type.equalsIgnoreCase("Image")){
						
							if(geomCo!=null && data!=null && mapPage != -1 && marker != -1){
								try{
									alGeoImageFilename.add(data);
									alGeoImageMapPage.add(mapPage);
									alGeoImageMapMarker.add(marker);
									for (int i=0; i<geomCo.length; i++){
										alGeoImageCoordinate.add(geomCo[i]);
									}
	
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
						if(data != null){	
						displayImage(data.toString(),height, marker);
						}
					}
					else if (type.equalsIgnoreCase("Video")){
						
						if(geomCo!=null && data!=null && mapPage != -1 && marker !=-1){
							try{
								alGeoVideoFilename.add(data);
								alGeoVideoMapPage.add(mapPage);
								alGeoVideoMapMarker.add(marker);
								for (int i=0; i<geomCo.length; i++){
									alGeoVideoCoordinate.add(geomCo[i]);
								}

							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						if(data != null){	
							displayVideo(data, marker);
						}
					}
					else if (type.equalsIgnoreCase("Audio")){
						
						if(geomCo!=null && data!=null && mapPage != -1 && marker !=-1){
							try{
								alGeoAudioFilename.add(data);
								alGeoAudioMapPage.add(mapPage);
								alGeoAudioMapMarker.add(marker);
								for (int i=0; i<geomCo.length; i++){
									alGeoAudioCoordinate.add(geomCo[i]);
								}
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						if(data != null){	
							displayAudio(data, marker);	
						}
					}
					
					else if (type.equalsIgnoreCase("WebBundle")){
						 displayWebBundle(data,url, itemKey);
					}
					else if (type.equalsIgnoreCase("MapImage")){
						if (data != null){
							displayMapImage(data, geomCo, gpxFilename, pageNumber);
							System.out.println("page number ==== " + pageNumber);
						}
					}

					
				}
				
				
			
				
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
			 
			 

			   private class RenderPage extends AsyncTask<Void, Void, Void> {
					
				   ProgressDialog bookDialog;

				   @Override
			        protected void onPreExecute() {
			            super.onPreExecute();
			            bookDialog = new ProgressDialog(Reader.this);
			            bookDialog.setMessage(getResources().getString(R.string.rendering_pages));
			            bookDialog.setIndeterminate(true);
			            bookDialog.setCancelable(false);
			            bookDialog.show();
			        }
					 
					@Override 
					protected Void doInBackground(Void... strings) {
												
						        try{	
						        	//call a method that parses the config.xml file in order to get an arraylist of xml filenames
						        	getConfigXML();
						        	getMyXML();	//call method to parse XML from a page in the book
					             }
					             catch(Exception e){
					            	 e.printStackTrace();
					            	 bookDialog.dismiss();
					             }
					             
					    
						return null;
				 	}
				 	
					 
					   @Override
				        protected void onPostExecute(Void unused) {
						   

				            
						   for (int i = 0; i < allPages.size(); i++) {
							  
							   pageView = new LinearLayout(Reader.this);
							   pageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
							   //pageView.setBackgroundResource(R.drawable.book_background);//Color(Color.WHITE);
							   pageView.setGravity(Gravity.CENTER_HORIZONTAL);  
							   pageView.setOrientation(LinearLayout.VERTICAL);
							   pageScroll = new ScrollView(Reader.this);
							   pageScroll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							   //pageScroll.setBackgroundResource(R.drawable.book_background);//Color(Color.WHITE);//setBackgroundColor(Color.WHITE);
							   
							    //add a bit of space between the top of the page and the pagination
							   	View view = new View(Reader.this);
							    view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 10));
							    pageView.addView(view);  
							   
					            //final TextView t = (TextView)findViewById(R.id.pageText);
					            TextView t = new TextView(Reader.this);
					            t.setText(getResources().getString(R.string.page) + " " +(i+1) + " " + getResources().getString(R.string.of) + " " + allPages.size());
					            t.setTextColor(Color.BLACK);
					            t.setGravity(Gravity.CENTER_HORIZONTAL);
					            pageView.addView(t);
					            
					            populatePage(allPages.get(i), i);
					            System.out.println("allPages ====" + allPages.get(i));
								///((WebView) pageView.findViewById(R.id.pageWeb)).loadDataWithBaseURL("",htmlPage.get(i).toString(), "text/html", "utf-8", "");
					            			         
					            pageScroll.addView(pageView);
					            scroller.addPage(pageScroll);
					        }
				           //populatePage(currentPage);									
				           bookDialog.dismiss();
				        }
				 
				
				     }
			   
			   @Override
			   public boolean onKeyDown(int keyCode, KeyEvent event) {
			       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			           Log.d(this.getClass().getName(), "back button pressed");
			          finish();
			          
			       }
			       return super.onKeyDown(keyCode, event);
			   }
			   

			
} //End of class



	 