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
	
	
	//ArrayLists for each book PAGE
	private ArrayList<Point> page1 = new ArrayList<Point>();
	private ArrayList<Point> page2 = new ArrayList<Point>();
	private ArrayList<Point> page3 = new ArrayList<Point>();
	private ArrayList<Point> page4 = new ArrayList<Point>();
	private ArrayList<Point> page5 = new ArrayList<Point>();
	private ArrayList<Point> page6 = new ArrayList<Point>();


	
	//ArrayLists for each item type
	ArrayList<TextItem> textItems = new ArrayList<TextItem>();
	ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
	ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
	ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
	ArrayList<MapImageItem> mapImageItems = new ArrayList<MapImageItem>();
	ArrayList<WebBundleItem> webBundleItems = new ArrayList<WebBundleItem>();
	ArrayList<GPSTraceItem> gpsTraceItems = new ArrayList<GPSTraceItem>();
	
	
	/*
	 * Getter methods
	 */
	
	public ArrayList<Point> getPage1(){
		return page1;
	}
	
	public ArrayList<Point> getPage2(){
		return page2;
	}
	
	public ArrayList<Point> getPage3(){
		return page3;
	}
	
	public ArrayList<Point> getPage4(){
		return page4;
	}
	
	public ArrayList<Point> getPage5(){
		return page5;
	}
	
	public ArrayList<Point> getPage6(){
		return page6;
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
			int panel = item.getPanel();
			int order = item.getOrder();
			String textKey = item.getKey();
			
			if (item.getGeometry() != null){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		
		  	  	pItems = new Point(text, panel, order, type, textKey, arrCoordinates);
			}
			else{
				//Else it does not have coordinates
		  	  	//Point pItems = new Point(text, panel, order, type, textKey, arrCoordinates);
		  	  	pItems = new Point(text, panel, order, type, textKey);
			}
			
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
	
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
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
			int panel = item.getPanel();
			int order = item.getOrder();
			//String url = item.getURL();
			String imageKey = item.getKey();
			int height = item.getImageHeight();
			
			if (item.getGeometry() != null){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array	
				
				pItems = new Point(filename, panel, order, type, imageKey, arrCoordinates, height);//, url
			}
			else{
				//Else it has no coordinates
				pItems = new Point(filename, panel, order, type, imageKey, height);//, arrCoordinates);//, url	
			}
	
	
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
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
			int panel = item.getPanel();
			int order = item.getOrder();
			String videoKey = item.getKey();
			
			if (item.getGeometry() != null){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		

		  	    pItems = new Point(filename, panel, order, type, videoKey, arrCoordinates);
			}
			else{
				//It has no coordinates
				//Point pItems = new Point(filename, panel, order, type, videoKey, arrCoordinates);
		  	    pItems = new Point(filename, panel, order, type, videoKey);//, arrCoordinates);
			}
			
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
	
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
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
			int panel = item.getPanel();
			int order = item.getOrder();
			String audioKey = item.getKey();
			
			if (item.getGeometry() != null){
				//Then it has coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array		
		
	  	  		//Point pItems = new Point(filename, panel, order, type, audioKey, arrCoordinates);
	  	  		pItems = new Point(filename, panel, order, type, audioKey, arrCoordinates);
			}
			else{
				//It has no coordinates
		  	  	pItems = new Point(filename, panel, order, type, audioKey);
			}
			
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
	
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
	
	for(MapImageItem item: mapImageItems){
		try{
			
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;
			
			String filename = item.getFilename();
			String type = item.getType();
			String mapKey = item.getKey();

			int panel = gpsTraceItems.get(0).getPanel();		//there is only ever ONE map/trail item so we can hard-code in getting the 0ith gpsItem
			int order = gpsTraceItems.get(0).getOrder();
			String gpxFilename = gpsTraceItems.get(0).getGpxFilename();//"/5555.gpx";
			//gpxCounter++; //Increment the counter
			
			//Now it will always have coordinates
			if (item.getGeometry() != null){
				//Then it is a map with coordinates
				geom = item.getGeometry();
				arrCoordinates = geom.getCoordinates();		//Gets the lon/lat coordinates from the geometry and stores them into an array, ignores the Z-Value , so its just xy		
		
				pItems = new Point(filename, panel, order, type, mapKey, arrCoordinates, gpxFilename);

			}
			else{
				//It is a map without coordinates
				pItems = new Point(filename, panel, order, type, mapKey);

			}
			//System.out.println("TCoordinates ARE = " + arrCoordinates[0].toString());
			
	
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
				
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
				
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
				
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
	
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
			
			
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
				
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
			
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
			
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}

/*

	for(GPSTraceItem item: gpsTraceItems){
		try{
			Geometry geom;
			Coordinate[] arrCoordinates;
			Point pItems;

			String filename = item.getGpxFilename();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String key = item.getKey();
			
			
			pItems = new Point(filename, panel, order, type, key);
			
			//Add to page 1
			if(panel == 0){
				page1.add(pItems);
				
			}
			//Add to page 2
			else if(panel == 1){
				page2.add(pItems);
			
			}
			//Add to page 3
			else if(panel == 2){
				page3.add(pItems);
			
			}
			//Add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//Add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//Add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //End of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
*/	
	//Finally sort the items of each page by their Order Number
	Collections.sort(page1);
	Collections.sort(page2);
	Collections.sort(page3);
	Collections.sort(page4);
	Collections.sort(page5);
	Collections.sort(page6);
	
   return key;	//Placebooks key id
   
 
    }



}
