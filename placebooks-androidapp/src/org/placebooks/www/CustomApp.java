package org.placebooks.www;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.*;

import android.app.Application;

/*
 * My Custom Application App. Extends android.app.Application
 * This class is used to guarantee that this application context
 * will exist as a single instance across the whole PlaceBoooks app.
 * Therefore this class is ideal for storing variables and methods that
 * need to be accessed across multiple Activities.
 */
public class CustomApp extends Application {

	//Geotagged Images - the key arraylist links with the coordinate arraylists
	private ArrayList<String> alPlacebookKey = new ArrayList<String>();
	private ArrayList<String> alGeoImageFilename = new ArrayList<String>();
	private ArrayList<Coordinate[]> alGeoImageCoordinates = new ArrayList<Coordinate[]>();
	
	 @Override
	    public void onCreate() {
	        super.onCreate();
	    }
	 
	 public ArrayList<String> getAlPlacebookKey(){
		 return alPlacebookKey;
	 }
	 public void setAlPlacebookKey(String key){
		 this.alPlacebookKey.add(key);
	 }
	 public ArrayList<String> getAlGeoImageFilename(){
		 return alGeoImageFilename;
	 }
	 public void setAlGeoImageFilename(String filename){
		 this.alGeoImageFilename.add(filename);
	 }
	 
	 public ArrayList<Coordinate[]> getAlGeoImageCoordinates(){
		 return alGeoImageCoordinates;
	 }
	 public void setAlGeoImageCoordinates(Coordinate[] co){
		 this.alGeoImageCoordinates.add(co);
	 }
	 

	

}
