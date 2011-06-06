package org.placebooks.www;

import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.ListIterator;


//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;	

	private ArrayList<Point> alPage1 = new ArrayList<Point>();
	private ArrayList<Point> alPage2 = new ArrayList<Point>();
	private ArrayList<Point> alPage3 = new ArrayList<Point>();
	
	//0 -> text item, 1 -> image item, 2-> video item, 4 -> audio item
	private ArrayList<Integer> alPage1Types = new ArrayList<Integer>();
	private ArrayList<Integer> alPage2Types = new ArrayList<Integer>();
	private ArrayList<Integer> alPage3Types = new ArrayList<Integer>();
	


	/*
	 * ArrayList contains all the items in a placebook.
	 * This ArrayList is then used in this class to find instances of each 'type' of item
	 */
	ArrayList<Item> items = new ArrayList<Item>();
	//ArrayList<ArrayList> pages = new ArrayList<ArrayList>();	//ArrayList 0,1,2 (page numbers) 
	

	
	/*
	 * Getter methods
	 */
	
	public ArrayList<Point> getPage1Items(){
		return alPage1;
	}
	public ArrayList<Point> getPage2Items(){
		return alPage2;
	}
	public ArrayList<Point> getPage3Items(){
		return alPage3;
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
    
		for (int i = 0; i< items.size();i++){
	       
			if (items.get(i) instanceof TextItem){
				   
		    	  String textItem = items.get(i).toString();
		    	  int size = textItem.length();	    	  
		    	  
		    	  int start = textItem.indexOf("Text=");
		    	  int fEnd = textItem.indexOf("URL=");
		    	  int urlEnd = textItem.indexOf("Panel=");
		    	  int panelEnd = textItem.indexOf("Order=");
		    	  
		    	  String textText = textItem.substring(start+5, fEnd-1);
		    	  //String textURL = textItem.substring(fEnd+4, urlEnd-1);
		    	  String textPanel = textItem.substring(urlEnd+6, panelEnd-1);
		    	  String textOrder = textItem.substring(panelEnd+6, size);
		    	  
		    	 
		    	  Point pItems = new Point(textText, Integer.parseInt(textPanel), Integer.parseInt(textOrder), "TEXT");
		    	  if (Integer.parseInt(textPanel) == 0){
		    	  alPage1.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(textPanel) == 1) {
		    		  alPage2.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(textPanel) == 2){
		    		  alPage3.add(pItems);
		    	  }
		    	  
		    	
		      }
			
			else if (items.get(i) instanceof ImageItem){
		 		    	  
		    	  //imgItem.add(items.get(i).toString());
		    	  String imgItem = items.get(i).toString();
		    	  int size = imgItem.length();

		    	  int start =imgItem.indexOf("Filename=");
		    	  int fEnd = imgItem.indexOf("URL=");		    	  
		    	  int urlEnd = imgItem.indexOf("Panel=");
		    	  int panelEnd = imgItem.indexOf("Order=");
		    	  
		    	  String imageFilename = imgItem.substring(start+9, fEnd-1);
		    	 // String imageURL = imgItem.substring(fEnd+4, urlEnd-1);
		    	  String imagePanel = imgItem.substring(urlEnd+6, panelEnd-1);
		    	  String imageOrder = imgItem.substring(panelEnd+6, size);
		    	  
		    	  
		    	  Point pItems = new Point(imageFilename, Integer.parseInt(imagePanel), Integer.parseInt(imageOrder), "IMAGE");
		    	  if (Integer.parseInt(imagePanel) == 0){
		    	  alPage1.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(imagePanel) == 1) {
		    		  alPage2.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(imagePanel) == 2){
		    		  alPage3.add(pItems);
		    	  }
		   	
		      }
		      else if (items.get(i) instanceof VideoItem){
		    	  String videoItem = items.get(i).toString();
		    	  int size = videoItem.length();

		    	  int start = videoItem.indexOf("Filename=");
		    	  int fEnd = videoItem.indexOf("Panel=");
		    	  int panelEnd = videoItem.indexOf("Order=");
		    	  
		    	  String videoFilename = videoItem.substring(start+9, fEnd-1);
		    	  String videoPanel = videoItem.substring(fEnd+6, panelEnd-1);
		    	  String videoOrder = videoItem.substring(panelEnd+6, size);
		    	  
		    	  
		    	  Point pItems = new Point(videoFilename, Integer.parseInt(videoPanel), Integer.parseInt(videoOrder), "VIDEO");
		    	  if (Integer.parseInt(videoPanel) == 0){
		    	  alPage1.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(videoPanel) == 1) {
		    		  alPage2.add(pItems);
		    	  }
		    	  else if(Integer.parseInt(videoPanel) == 2){
		    		  alPage3.add(pItems);
		    	  }
		    	  
		      }
		          else if (items.get(i) instanceof AudioItem){
		        	  String audioItem = items.get(i).toString();
			    	  int size = audioItem.length();

			    	  int start = audioItem.indexOf("Filename=");
			    	  int fEnd = audioItem.indexOf("Panel=");
			    	  int panelEnd = audioItem.indexOf("Order=");
			    	  
			    	  String audioFilename = audioItem.substring(start+9, fEnd-1);
			    	  String audioPanel = audioItem.substring(fEnd+6, panelEnd-1);
			    	  String audioOrder = audioItem.substring(panelEnd+6, size);

			    	  
			    	  Point pItems = new Point(audioFilename, Integer.parseInt(audioPanel), Integer.parseInt(audioOrder), "AUDIO"); 
			    	  if (Integer.parseInt(audioPanel) == 0){
			    		  alPage1.add(pItems);
			    	  }
			    	  else if(Integer.parseInt(audioPanel) == 1) {
			    		  alPage2.add(pItems);
			    	  }
			    	  else if(Integer.parseInt(audioPanel) == 2){
			    		  alPage3.add(pItems);
			    	  }
		        	  
		          }
	    	
	}

   return key ;
   
 
    }



}
