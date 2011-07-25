package org.PlaceBookSimpleXmlBind;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.util.*;


public class PlaceBookSimpleXmlBind extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
        	
			//String xmlData = (url);
			Serializer serializer = new Persister(); 
			
			File source = new File("sdcard/placebooks/unzipped/var/lib/placebooks-media/packages/201/gpxdata.xml");
			Gpx gpx = serializer.read(Gpx.class, source);

			
			 System.out.println("HERE THIS IS A TEST");
			 for(int i=0; i<gpx.trk.trkseg.size(); i++){
				 System.out.println(gpx.trk.trkseg.get(i).toString());//trkseg.get(i));
			 }

			 //System.out.println(gpx.getLat());
			 //System.out.println(gpx.getLon());
	     } catch (Exception e) {
	          e.printStackTrace();
	     }
       
    }
}