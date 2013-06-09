package org.placebooks.www;

import android.app.Application;
import java.util.*;
import java.util.Collections;
import android.util.Log;
import android.widget.TextView;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;

//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book extends Application{
	
	private String key;		//unique book key
	private String owner;	//name of the book owner or the creator
	private String timestamp;	//need a timestamp on the book so that we can compare timestamps for any book updates

	//private int gpxCounter = 0;
	
	
	//ArrayLists for book PAGES
	private ArrayList<Point> pageCol1 = new ArrayList<Point>();
	private ArrayList<Point> pageCol2 = new ArrayList<Point>();


	
	//ArrayLists for each item type
	ArrayList<TextItem> textItems = new ArrayList<TextItem>();
	ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
	ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
	ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
	ArrayList<MapImageItem> mapImageItems = new ArrayList<MapImageItem>();
	ArrayList<WebBundleItem> webBundleItems = new ArrayList<WebBundleItem>();
	ArrayList<GPSTraceItem> gpsTraceItems = new ArrayList<GPSTraceItem>();
	
	private int mapItemNumber = 0;
	
	/*
	 * Getter methods
	 */
	
	public ArrayList<Point> getPage(){
		return pageCol1;
	}
	public ArrayList<Point> getPage2(){
		return pageCol2;
	}

	
	
	public String getKey() {
		return key;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getTimestamp(){
		return timestamp;
	}

	
	/*
	 * Setter methods
	 */
		
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

		
		
	public String toString(){
		
	for(TextItem item: textItems) {
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;

			String text = item.getText();
			String type = item.getType();
			int column = item.getColumn();
			int order = item.getOrder();
			String textKey = item.getKey();
			
			int mapPage = item.getMapPage();
			int marker = item.getMapMarker();
			int height = 0;

			
			if (item.getGeometry() != null && mapPage != -1 && marker != -1){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		
		  	  	pItems = new Point(text, column, order, type, textKey, arrCoordinates, height, mapPage, marker);
			}
			else{
				//Else it does not have coordinates
		  	  	//Point pItems = new Point(text, panel, order, type, textKey, arrCoordinates);
		  	  	pItems = new Point(text, column, order, type, textKey);
			}
			
			//Add to page
			if(column == 0){
				pageCol1.add(pItems);
			}
			//Add to page
			else if(column == 1){
				pageCol2.add(pItems);
			}
	
	
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	
	
	for(ImageItem item: imageItems) {
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String type = item.getType();
			int column = item.getColumn();
			int order = item.getOrder();
			int mapPage = item.getMapPage();
			//String url = item.getURL();
			String imageKey = item.getKey();
			int height = item.getImageHeight();
			int marker = item.getMapMarker();
			
			System.out.println("map page ====== " + mapPage);
			
			if (item.getGeometry() != null && mapPage != -1 && marker != -1){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array	
				
				pItems = new Point(filename, column, order, type, imageKey, arrCoordinates, height, mapPage, marker);
			}
			else{
				//Else it has no coordinates
				pItems = new Point(filename, column, order, type, imageKey, height);//, arrCoordinates);//, url	
			}
	
	
			//Add to page 
			if(column == 0){
				pageCol1.add(pItems);
			}
			//Add to page
			else if(column == 1){
				pageCol2.add(pItems);
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	for(VideoItem item: videoItems) {
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String type = item.getType();
			int column = item.getColumn();
			int order = item.getOrder();
			int mapPage = item.getMapPage();
			String videoKey = item.getKey();
			int height = item.getVideoHeight();
			int marker = item.getMapMarker();
			System.out.println("Video Marker === " +marker);
			System.out.println("item.getGeometry ==== " + item.getGeometry());
			
			if (item.getGeometry() != null && mapPage != -1 && marker != -1){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		

		  	    pItems = new Point(filename, column, order, type, videoKey, arrCoordinates, height, mapPage, marker);
			}
			else{
				//It has no coordinates
				//Point pItems = new Point(filename, panel, order, type, videoKey, arrCoordinates);
		  	    pItems = new Point(filename, column, order, type, videoKey, height);//, arrCoordinates);
			}
			
			//Add to page 
			if(column == 0){
				pageCol1.add(pItems);
	
			}
			//Add to page
			else if(column == 1){
				pageCol2.add(pItems);
			}
						
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	for(AudioItem item: audioItems) {
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String type = item.getType();
			int column = item.getColumn();
			int order = item.getOrder();
			String audioKey = item.getKey();
			
			int mapPage = item.getMapPage();
			int marker = item.getMapMarker();
			int height = 0;

			
			if (item.getGeometry() != null && mapPage != -1 && marker != -1){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		
		
	  	  		//Point pItems = new Point(filename, panel, order, type, audioKey, arrCoordinates);
	  	  		pItems = new Point(filename, column, order, type, audioKey, arrCoordinates, height, mapPage, marker);
			}
			else{
				//It has no coordinates
		  	  	pItems = new Point(filename, column, order, type, audioKey);
			}
			
			//Add to page
			if(column == 0){
				pageCol1.add(pItems);
	
			}
			//Add to page
			else if(column == 1){
				pageCol2.add(pItems);
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
	
	for(MapImageItem item: mapImageItems){
		try{
			mapItemNumber++;
			//need to know if the number of maps will be the same as the number of trails, otherwise how do we know what trails go to what maps?
			
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String type = item.getType();
			String mapKey = item.getKey();
			String gpxFilename = null;
			int column = 0;
			int order = 0;
			
			System.out.println("GPS TRACE ITEMS SIZE "+gpsTraceItems.size());
			
			if (gpsTraceItems.size() > 0){
				column = gpsTraceItems.get(0).getColumn();		//there is only ever ONE map/trail item so we can hard-code in getting the 0ith gpsItem
				order = gpsTraceItems.get(0).getOrder();
				gpxFilename = gpsTraceItems.get(0).getGpxFilename();//"/5555.gpx";
				System.out.println("gpxFilename === " + gpxFilename);
				//gpxCounter++; //Increment the counter
			}
			
			
			//Now it will always have coordinates
			if (item.getGeometry() != null){
				//Then it is a map with coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array, ignores the Z-Value , so its just xy		
		
				pItems = new Point(filename, column, order, type, mapKey, arrCoordinates, gpxFilename);

			}
			else{
				//It is a map without coordinates
				pItems = new Point(filename, column, order, type, mapKey);

			}
			//System.out.println("TCoordinates ARE = " + arrCoordinates[0].toString());
			
	
			//Add to page 
			if(column == 0){
				pageCol1.add(pItems);
				
			}
			//Add to page
			else if(column == 1){
				pageCol2.add(pItems);
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
/*	
	for(WebBundleItem item: webBundleItems){
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String url = item.getURL();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String wbKey = item.getKey();
			
			if (item.getGeometry() != null){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		
				
				pItems = new Point(filename, panel, order, type, wbKey, arrCoordinates, url);
			}
			else{
				//No coordinates
				pItems = new Point(filename, panel, order, type, wbKey, url);
			}
			
			
			//Add to page 
			if(panel == 0){
				pageCol1.add(pItems);
				
			}
			//Add to page
			else if(panel == 1){
				pageCol2.add(pItems);
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
*/

	//Finally sort the items of each page by their Order Number
	Collections.sort(pageCol1);
	Collections.sort(pageCol2);

   return key;	//Placebooks key id
   
 
    }



}
