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
//import java.util.Iterator;

import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.MotionEvent;
import android.view.View;



//import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
//import android.widget.LinearLayout;


public class Reader extends Activity {
	
	private TextView orgXmlTxt;
	private TextView txtTitle;
	private ImageView pngView;
	private TextView txtText1;
	private TextView txtPage1;
	
	private String textText;
	private String textURL;
	private String imageFilename;
	private String imageURL;
	private String videoFilename;
	private String audioFilename;

	
	//we know the xml file will always be called config.xml, so read this from the SD Card
	//File myfile  = new File("/sdcard/package/config.xml"); 
	//String path = Environment.getExternalStorageDirectory() + "/package/config.xml"; 
   // File file = new File(path);
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	       	       	        
	        try {
	        	
	        	setContentView(R.layout.reader); 
				//orgXmlTxt = (TextView) findViewById(R.id.orgXMLTxt);
				//orgXmlTxt.setText(getMyXML());
	        	txtTitle = (TextView) findViewById(R.id.txtTitle);
	        	//txtTitle.setText(titleVar);
	        	txtPage1 = (TextView) findViewById(R.id.txtPage1);
	        		        	
		        getMyXML();		//call method to parse XML			
			
				
			} catch (Exception e) {
				orgXmlTxt.setText(e.getMessage());
			} 
			
			displayImage(); //once XML is parsed and variables are set, call the methods to display content
			displayText();
			
	 } //end of onCreate
	 
			 private void displayImage(){
				 
				 //TextView pngName = (TextView)findViewById(R.id.pngname);		// textview for the file path for the image
				 pngView = (ImageView)findViewById(R.id.pngview);
					
					//ImageItem imgItem = new ImageItem(); //make a new object of this class to access its method
									
			        String myPngPath = "/sdcard/package/" + imageFilename;
			        //"/sdcard/package/" + "0073a3b22ede9a5b012ede9a5c070002.png";
			        //pngName.setText(myPngPath);		// display the text for the file path
				        
				    BitmapFactory.Options options = new BitmapFactory.Options();
				    options.inSampleSize = 1;
				    Bitmap bm = BitmapFactory.decodeFile(myPngPath, options);
				    pngView.setImageBitmap(bm); 
				 
			 }
			 
			 private void displayText(){
				 
				txtText1 = (TextView) findViewById(R.id.txtText1);
		        txtText1.setText(textText);
								 
			 }
			 
			/* 
			 @Override
			    public boolean onTouchEvent(MotionEvent event) {
			       switch (event.getAction()) {
			      case MotionEvent.ACTION_DOWN:
			         ImageView img =(ImageView)findViewById(R.id.pngview);
			           img.setVisibility(View.VISIBLE);
			         return true;
			      case MotionEvent.ACTION_UP:
			        return false; // something else
			                default:
			                        return false;// all others
			      }
			    }*/
	
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

				FileInputStream in = new FileInputStream("/sdcard/package/config.xml");  //text.txt

				xr.parse(new InputSource(in));
				
			//	ArrayList<Book> parsedExampleDataSet = myExampleHandler.getParsedData();
			//  Book parsedExampleDataSet = myExampleHandler.getParsedData();
				Book book = myExampleHandler.getParsedData();
				
				inLine.append(book.toString());
				
				textText = book.textText;
				//textText.append(book.toString());
				textURL = book.textURL;
				imageFilename = book.imageFilename;
				imageURL = book.imageURL;
				videoFilename = book.videoFilename;
				audioFilename = book.audioFilename;
				
				in.close();
				
			
				return inLine.toString();
				    
				/*	Iterator<Book> itr = parsedExampleDataSet.iterator();
			    while (itr.hasNext()) {
			      String element = itr.next().toString();
					inLine.append("hello");
			    }
			  */  
			}
	        
			
}


	 