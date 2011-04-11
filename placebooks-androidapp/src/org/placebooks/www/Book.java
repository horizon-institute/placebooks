package org.placebooks.www;

import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.ListIterator;

//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;
	String imageFilename;
	String imageURL;
	String audioFilename;
	String videoFilename;
	String textText;
	String textURL;
	
	
	
	
	// Data Members
	ArrayList<Item> items = new ArrayList<Item>(); //java.util.ArrayList(); 

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	
	

	
public String toString(){
    
//	StringBuilder sb = new StringBuilder();
	
//	Iterator<Item> itr = items.iterator();
//    while (itr.hasNext()) {
//      String element = itr.next().toString();
//      sb.append(element);
//      sb.append("\n");
      
		
	for (int i = 0; i< items.size();i++){
       
	      if (items.get(i) instanceof ImageItem){
	    	  String imgItem = items.get(i).toString();
	    	  int start =imgItem.indexOf("Filename=");
	    	  int end = imgItem.indexOf("URL=");
	    	  imageFilename = imgItem.substring(start+9, end-1);
	    	  
	    	  int size = imgItem.length();
	    	  imageURL = imgItem.substring(end+4, size);
	    	  
	      }
	      else if (items.get(i) instanceof VideoItem){
	    	  String videoItem = items.get(i).toString();
	    	  int start = videoItem.indexOf("Filename=");
	    	  int size = videoItem.length();
	    	  videoFilename = videoItem.substring(start+9, size);
	      }
	          else if (items.get(i) instanceof AudioItem){
	        	  String audioItem = items.get(i).toString();
		    	  int start = audioItem.indexOf("Filename=");
		    	  int size = audioItem.length();
		    	  audioFilename = audioItem.substring(start+9, size);  
	        	  
	          }
	      else if (items.get(i) instanceof TextItem){
	    	  String textItem = items.get(i).toString();
	    	  int start = textItem.indexOf("Text=");
	    	  int end = textItem.indexOf("URL=");
	    	  int size = textItem.length();
	    	  
	    	  textText = textItem.substring(start+5, end-1);
	    	  	    	  
	    	  textURL = textItem.substring(end+4, size);

	      }
	     
	
	}
     
       
//      System.out.print(element + " ");
//    }
//    System.out.println();
    
   

    return imageFilename + "\n" + imageURL + "\n" + videoFilename + "\n" +audioFilename + "\n" +textText + "\n" + textURL;
   // return sb.toString();
   
 
    }



}
