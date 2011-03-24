package org.placebooks.www;

import android.app.Activity;
import android.os.Bundle;
//import 	java.io.File;
//import 	android.os.Environment;

//import java.io.BufferedReader;
import java.io.FileInputStream;
//import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.placebooks.www.R;

import org.placebooks.www.XMLDataSet;
import org.placebooks.www.XMLHandler;


//import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;


public class Reader extends Activity {
	
	/** Create Object For SiteList Class */
    XMLDataSet dataSet = null;


	private TextView orgXmlTxt;

	
	//we know the xml file will always be called config.xml, so read this from the SD Card
	//File myfile  = new File("/sdcard/package/config.xml"); 
	//String path = Environment.getExternalStorageDirectory() + "/package/config.xml"; 
   // File file = new File(path);
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle
	        
	        /** Create a new layout to display the view */
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);

            /** Create a new textview array to display the results */
            TextView url[];
            TextView path[];
	       	       	        
	      /*  try {
	        	
	        /*	setContentView(R.layout.reader); 
				orgXmlTxt = (TextView) findViewById(R.id.orgXMLTxt);
				orgXmlTxt.setText(getMyXML());
			
				
			} catch (Exception e) {
				orgXmlTxt.setText(e.getMessage());
			} */
            
            
            try {
	            StringBuffer inLine = new StringBuffer();
				/* Get a SAXParser from the SAXPArserFactory. */
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
	
				/* Get the XMLReader of the SAXParser we created. */
				XMLReader xr = sp.getXMLReader();
				/* Create a new ContentHandler and apply it to the XML-Reader */
				XMLHandler myExampleHandler = new XMLHandler();
				xr.setContentHandler(myExampleHandler);
	
				FileInputStream in = new FileInputStream("/sdcard/config.xml");  //text.txt
	
				xr.parse(new InputSource(in));
				
				
            }
            catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
        }
            
            /** Get result from MyXMLHandler SitlesList Object */
			dataSet = XMLHandler.dataSet;

	        	
	        	
	        	
			
	 } //end of onCreate
	 
//			private String getMyXML() throws Exception {
//						
//				StringBuffer inLine = new StringBuffer();
				/* Get a SAXParser from the SAXPArserFactory. */
//				SAXParserFactory spf = SAXParserFactory.newInstance();
//				SAXParser sp = spf.newSAXParser();

				/* Get the XMLReader of the SAXParser we created. */
//				XMLReader xr = sp.getXMLReader();
				/* Create a new ContentHandler and apply it to the XML-Reader */
//				XMLHandler myExampleHandler = new XMLHandler();
//				xr.setContentHandler(myExampleHandler);

//				FileInputStream in = new FileInputStream("/sdcard/config.xml");  //text.txt

//				xr.parse(new InputSource(in));
//				
//				XMLDataSet parsedExampleDataSet = myExampleHandler.getParsedData();
//				inLine.append(parsedExampleDataSet.toString());
//				in.close();
//				return inLine.toString();
				    
				
//			}
	        
			
}


	 