package org.placebooks.www;

import java.util.*;
//import java.util.Iterator;
//import java.util.ListIterator;
import java.util.Collections;

import android.util.Log;
import android.widget.TextView;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;


//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;	
	private String timestamp;	//need a timestamp on the book so that we can compare timestamps for any book updates

	
	private ArrayList<Point> page1 = new ArrayList<Point>();
	private ArrayList<Point> page2 = new ArrayList<Point>();
	private ArrayList<Point> page3 = new ArrayList<Point>();
	private ArrayList<Point> page4 = new ArrayList<Point>();
	private ArrayList<Point> page5 = new ArrayList<Point>();
	private ArrayList<Point> page6 = new ArrayList<Point>();

	private ArrayList<Long> coordinates = new ArrayList<Long>();


	/*
	 * ArrayLists for each item type
	 */
	//ArrayList<ArrayList> pages = new ArrayList<ArrayList>();	//ArrayList 0,1,2 (page numbers) 
	ArrayList<TextItem> textItems = new ArrayList<TextItem>();
	ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
	ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
	ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
	ArrayList<MapImageItem> mapImageItems = new ArrayList<MapImageItem>();
	ArrayList<WebBundleItem> webBundleItems = new ArrayList<WebBundleItem>();
	

	public ArrayList<WebBundleItem> getWebBundle(){
		return this.webBundleItems;
	}
	
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
	
	
	//get the placebook key (ID itself)
	public String getKey() {
		return key;
	}
	//get the placebook owner name
	public String getOwner() {
		return owner;
	}
	//get the placebook timestamp
	public String getTimestamp(){
		return timestamp;
	}

	
	/*
	 * Setter methods
	 */
		
		//set the placebook key
		public void setKey(String key) {
			this.key = key;
		}
		//set placebook owner name
		public void setOwner(String owner) {
			this.owner = owner;
		}
		//set placebook timestamp
		public void setTimestamp(String timestamp){
			this.timestamp = timestamp;
		}

		
		
	public String toString(){
		
	for(TextItem item: textItems) {

		try{
			
			Geometry geom;
			Coordinate[] arrCoordinates;

			String text = item.getText();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String textKey = item.getKey();
			
			//if (item.getGeometry() != null){}
			
			
			
			
			
			
			
			
			//Geometry geom = item.getGeometry();
			//Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array		
	
			
	  	  	//Point pItems = new Point(text, panel, order, type, textKey, arrCoordinates);
	  	  	Point pItems = new Point(text, panel, order, type, textKey);//, arrCoordinates);
	
			
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
	
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
		
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	for(ImageItem item: imageItems) {
		
		try{
			String filename = item.getFilename();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			//String url = item.getURL();
			String imageKey = item.getKey();
			//Geometry geom = item.getGeometry();
			//Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array		
	
			
	  	  	//Point pItems = new Point(filename, panel, order, type, imageKey, arrCoordinates);//, url
	  	  	Point pItems = new Point(filename, panel, order, type, imageKey);//, arrCoordinates);//, url
	
	
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	for(VideoItem item: videoItems) {
		
		try{
			String filename = item.getFilename();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String videoKey = item.getKey();
			//try{
			//Geometry geom = item.getGeometry();
			//Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array		
			//}
			//catch (NullPointerException e){
				//Log.
			//}
	  	  	//Point pItems = new Point(filename, panel, order, type, videoKey, arrCoordinates);
	  	  	Point pItems = new Point(filename, panel, order, type, videoKey);//, arrCoordinates);
	
			
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
	
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	for(AudioItem item: audioItems) {
		
		try{
			String filename = item.getFilename();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String audioKey = item.getKey();
			//Geometry geom = item.getGeometry();
			//Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array		
	
			
	  	  	//Point pItems = new Point(filename, panel, order, type, audioKey, arrCoordinates);
	  	  	Point pItems = new Point(filename, panel, order, type, audioKey);//, arrCoordinates);
	
			
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
	
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
	
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
	
	for(MapImageItem item: mapImageItems){
		
		try{
			String filename = item.getFilename();
			//GET GEOMETRY
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String mapKey = item.getKey();
			Geometry geom = item.getGeometry();
			Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array, ignores the Z-Value , so its just xy		
	
			//System.out.println("TCoordinates ARE = " + arrCoordinates[0].toString());
			
			/*
			for (int i=0; i<arrCoordinates.length; i++){
				coordinates.add(arrCoordinates[i]);
			}*/
			/*
			for(int i=0; i<arrCoordinates.length; i++){
				test.add(arrCoordinates[i].toString());
			}*/
			
	
			
			//Point pItems = new Point(filename, panel, order, type, mapKey, arrCoordinates);
			Point pItems = new Point(filename, panel, order, type, mapKey, arrCoordinates);
	
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
				
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
				
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
				
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
	}
	
	for(WebBundleItem item: webBundleItems){
		
		try{
			String filename = item.getFilename();
			String url = item.getURL();
			String type = item.getType();
			int panel = item.getPanel();
			int order = item.getOrder();
			String wbKey = item.getKey();
			Geometry geom = item.getGeometry();
			Coordinate[] arrCoordinates = geom.getCoordinates();		//gets the lon/lat coordinates from the geometry and stores them into an array		
			
	
			//Point pItems = new Point(filename, panel, order, type, wbKey, arrCoordinates, url);
			Point pItems = new Point(filename, panel, order, type, wbKey, arrCoordinates, url);
			
			//add to page 1
			if(panel == 0){
				page1.add(pItems);
				
			}
			//add to page 2
			else if(panel == 1){
				page2.add(pItems);
			
			}
			//add to page 3
			else if(panel == 2){
				page3.add(pItems);
			
			}
			//add to page 4
			else if(panel == 3){
				page4.add(pItems);
				
			}
			//add to page 5
			else if(panel == 4){
				page5.add(pItems);
				
			}
			//add to page 6
			else if(panel == 5){
				page6.add(pItems);
				
			}
			
		} //end of try
		catch(NullPointerException npe){
			Log.e("TRACE = ",npe.getMessage());
			System.out.println("Null pointer exception has been caught");
		}
		
	}
	
	//Finally sort the items of each page by their Order Number
	Collections.sort(page1);
	Collections.sort(page2);
	Collections.sort(page3);
	Collections.sort(page4);
	Collections.sort(page5);
	Collections.sort(page6);
	
   return key;	//placebooks key id
   
 
    }



}
