package org.placebooks.www;

import android.app.Activity;
import android.os.Bundle;
//import 	java.io.File;
//import 	android.os.Environment;
//import java.io.BufferedReader;
import java.io.FileInputStream;
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
import android.view.MotionEvent;
import android.view.View;


import android.widget.TextView;
import android.widget.ImageView;	//for images and for the map (i guess) since the map is now going to be just an image
import android.widget.VideoView;

//import android.media.MediaRecorder;
//import android.media.MediaPlayer;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import java.util.*;	



public class Reader extends Activity {
	
	private TextView orgXmlTxt;		//textView to display error if placebook doesn't display properly
	//private TextView txtTitle;
	//private ImageView pngImage;
	//private TextView txtText1;
	//private TextView txtPage1;
	
	private String pbkey;	//this is the placebook key that defines the ID for each placebook. The contents of each placebook is stored in the folder "key" e.g folder /1234567/contents in here
	private String uName;	//this is the users actual name that they used to register with placebooks. Note - this is not their email address.
		
	private ArrayList <String> alTextText = new ArrayList <String>();	// arraylist for the text elements in the placebook	
	private ArrayList <String> alImageFilename = new ArrayList <String>();	//arraylist of the image path file names 
	private ArrayList <String> alAudioFilename = new ArrayList <String>();	//arraylist of the audio path file names
	private ArrayList <String> alVideoFilename = new ArrayList <String>();	//arraylist of the video path file names

	private ScrollView sv;
	private LinearLayout ll;

	
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	        
	        sv = new ScrollView(this);
			ll = new LinearLayout(this);
			 // WEIGHT = 1f, GRAVITY = center
			ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,1));	
			ll.setOrientation(LinearLayout.VERTICAL);
			sv.setBackgroundColor(0xFFFFFFFF);
			ll.setGravity(android.view.Gravity.CENTER);
			sv.addView(ll);
			
			//uName = "stuart"; 	//need to GET user name...how is this known? - case sensitive?
			//pbkey = "pack123";
		
			this.setContentView(sv);
			
			
	      	       	        
	        try {
	        	//setContentView(R.layout.reader); 	 //Doing it dynamically and programmatically now instead of .xml views		        		        	
		        getMyXML();		//call method to parse XML		
		        
		        //need to work out the order in which the items are displayed i.e could be image, text, image.. so I will need to calculate the order and call each display() method a number of times by order
				displayText();
		        displayImage(); //once XML is parsed and variables are set, call the methods to display content
		        displayVideo();
					
			} catch (Exception e) {
				orgXmlTxt.setText(e.getMessage());
			} 
			
			/*
			//need to work out the order in which the items are displayed i.e could be image, text, image.. so I will need to calculate the order and call each display() method a number of times by order
			displayImage(); //once XML is parsed and variables are set, call the methods to display content
			displayText();
			*/
			
			
	 } //end of onCreate
	 
			 private void displayImage(){
				 				 
				 /*
				  * Dynamically creating 'x' amount of image views for each image in the placebook 
				  * -- need to work on ordering next -- e.g could have img, txt, img, whereas right now it will always display the list of all images one after each other
				  */
	
				 for (int i=0;i<alImageFilename.size();i++ ){
					 
					 ImageView image = new ImageView(this);
					 String myImagePath = "/sdcard/placebooks/unzipped/" + "stuart" + "/placebook-data/packages/" + pbkey + "/" + alImageFilename.get(i);
					   
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
			 
			 private void displayText(){
				  
				 for (int i=0; i<alTextText.size(); i++){
					 TextView tv = new TextView(this);
					 tv.setText(alTextText.get(i));
					 tv.setTextColor(0xFF000000);
					 ll.addView(tv);
				 } 
			 }
			 
			 private void displayVideo(){
				 
				 for (int i=0; i<alVideoFilename.size(); i++){
					 VideoView vv = new VideoView(this);
					 vv.setVideoURI(alVideoFilename.get(i));
					 
					 
				 }
				 
				 
			 }

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
				 */
				FileInputStream in = new FileInputStream("/sdcard/placebooks/unzipped/package/config.xml");  //text.txt
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
				
				
				in.close();
				
			
				return inLine.toString();    
				
			}
	        
			
}


	 