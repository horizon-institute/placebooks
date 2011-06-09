package org.placebooks.www;

import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.ListIterator;
import java.util.Collections;


//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;	
	//private int timestamp;	//need a timestamp on the book so that we can compare timestamps for any book updates

	
	private ArrayList<Point> page1 = new ArrayList<Point>();
	private ArrayList<Point> page2 = new ArrayList<Point>();
	private ArrayList<Point> page3 = new ArrayList<Point>();


	/*
	 * ArrayList contains all the items in a placebook.
	 * This ArrayList is then used in this class to find instances of each 'type' of item
	 */
	ArrayList<Item> items = new ArrayList<Item>();
	//ArrayList<ArrayList> pages = new ArrayList<ArrayList>();	//ArrayList 0,1,2 (page numbers) 
	ArrayList<TextItem> textItems = new ArrayList<TextItem>();
	ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
	ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
	ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
	ArrayList<MapImageItem> mapImageItems = new ArrayList<MapImageItem>();

	
	//page1.add(textItems)....add all the types of items
	//collections.sort(page1) sort by order
	
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
	
	//get the placebook key (ID itself)
	public String getKey() {
		return key;
	}
	//get the placebook owner name
	public String getOwner() {
		return owner;
	}
	//Return the items in the placebook arraylist
	public ArrayList<Item> getItems() {
		return items;
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
		//Add items to the placebook arraylist (arraylist of items of different types)
		public void setItems(ArrayList<Item> items) {
			this.items = items;
		}
		
		
		
	public String toString(){
		
	for(TextItem item: textItems) {
		String text = item.getText();
		String type = item.getType();
		int panel = item.getPanel();
		int order = item.getOrder();
		
  	  	Point pItems = new Point(text, panel, order, type);

		
		//add to page 1
		if(panel == 0){
			page1.add(pItems);
		}
		//add to page 2
		if(panel == 1){
			page2.add(pItems);

		}
		//add to page 3
		if(panel == 2){
			page3.add(pItems);

		}
		
	}
	
	for(ImageItem item: imageItems) {
		String filename = item.getFilename();
		String type = item.getType();
		int panel = item.getPanel();
		int order = item.getOrder();
		
  	  	Point pItems = new Point(filename, panel, order, type);

		//add to page 1
		if(panel == 0){
			page1.add(pItems);
//			page1Types.add(type);
		}
		//add to page 2
		if(panel == 1){
			page2.add(pItems);
//			page2Types.add(type);
		}
		//add to page 3
		if(panel == 2){
			page3.add(pItems);
//			page3Types.add(type);
		}
		
	}
	
	for(VideoItem item: videoItems) {
		String filename = item.getFilename();
		String type = item.getType();
		int panel = item.getPanel();
		int order = item.getOrder();
		
  	  	Point pItems = new Point(filename, panel, order, type);
		
		//add to page 1
		if(panel == 0){
			page1.add(pItems);
//			page1Types.add(type);

		}
		//add to page 2
		if(panel == 1){
			page2.add(pItems);
//			page2Types.add(type);

		}
		//add to page 3
		if(panel == 2){
			page3.add(pItems);
//			page3Types.add(type);
		}
		
	}
	
	for(AudioItem item: audioItems) {
		String filename = item.getFilename();
		String type = item.getType();
		int panel = item.getPanel();
		int order = item.getOrder();
		
  	  	Point pItems = new Point(filename, panel, order, type);
		
		//add to page 1
		if(panel == 0){
			page1.add(pItems);

		}
		//add to page 2
		if(panel == 1){
			page2.add(pItems);

		}
		//add to page 3
		if(panel == 2){
			page3.add(pItems);
		}
		
	}
	
	//Finally sort the items of each page by their Order Number
	Collections.sort(page1);
	Collections.sort(page2);
	Collections.sort(page3);
	
   return key ;
   
 
    }



}
