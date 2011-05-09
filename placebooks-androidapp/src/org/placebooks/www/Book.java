package org.placebooks.www;

import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.ListIterator;

//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;
	//private String imageFilename;
	//private String imageURL;
	//private String audioFilename;
	//private String videoFilename;
	//private String textText;
	//private String textURL;
	// need to have a private String uName (e.g stuart) so I know what the folder name is going to be unless the package structure is changed
	
	private ArrayList <String> alTextText = new ArrayList <String>();
	private ArrayList <String> alTextURL = new ArrayList <String>();
	private ArrayList <String> alImageFilename = new ArrayList <String>();
	private ArrayList <String> alImageURL = new ArrayList <String>();
	
	private ArrayList <String> alAudioFilename = new ArrayList <String>();
	private ArrayList <String> alVideoFilename = new ArrayList <String>();

//	private  ArrayList<String> alImgItem = new ArrayList<String>();
	


	/*
	 * ArrayList contains all the items in a placebook.
	 * This ArrayList is then used in this class to find instances of each 'type' of item
	 */
	ArrayList<Item> items = new ArrayList<Item>();
	
	
	/*
	 * Getter methods
	 */
	
		//Text Items
		public ArrayList<String> getAlTextText(){
		    return alTextText;  		
		}
		public ArrayList<String> getAlTextURL(){
			return alTextURL;
		}
		
		//Image Items
		public ArrayList<String> getAlImageFilename(){
		    return alImageFilename;  		
		}
		public ArrayList<String> getAlImageURL(){
		    return alImageURL;  		
		}		
		public int getAlImageFilenameSize(){	
			return alImageFilename.size();	
		}
		
		//Audio Items
		public ArrayList<String> getAlAudioFilename(){
			return alAudioFilename;
		}
		
		//Video Items
		public ArrayList<String> getAlVideoFilename(){
			return alVideoFilename;
		}
	
	/*
	 * Setter methods
	 */
	
		//Add a Text Item
		public void setAlTextText(String textText) {
			this.alTextText.add(textText);
		}
		public void setAlTextURL(String textURL) {
			this.alTextURL.add(textURL);
		}
		
		
		//Add an Image Item
		public void setAlImageFilename(String imageFilename) {
			this.alImageFilename.add(imageFilename);
		}
		public void setAlImageURL(String imageURL) {
			this.alImageURL.add(imageURL);
		}
		
		//Add an Audio Item to the audio item arraylist
		public void setAlAudioFilename(String audioFilename){
			this.alAudioFilename.add(audioFilename);
		}
		
		
		//Add a Video Item to the video item arraylist
		public void setAlVideoFilename(String videoFilename){
			this.alVideoFilename.add(videoFilename);
		}
		
		
		//get the placebook key (ID itself)
		public String getKey() {
			return key;
		}
		//set the placebook key
		public void setKey(String key) {
			this.key = key;
		}
		//get the placebook owner name
		public String getOwner() {
			return owner;
		}
		//set placebook owner name
		public void setOwner(String owner) {
			this.owner = owner;
		}
		//Return the items in the placebook arraylist
		public ArrayList<Item> getItems() {
			return items;
		}
		//Add items to the placebook arraylist (arraylist of items of different types)
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
	       
			// can have multiple image items..so need to check how many
		      if (items.get(i) instanceof ImageItem){
		  		    	  
		    	  //imgItem.add(items.get(i).toString());
		    	  String imgItem = items.get(i).toString();
		    	  int start =imgItem.indexOf("Filename=");
		    	  int end = imgItem.indexOf("URL=");
		    	  String imageFilename = imgItem.substring(start+9, end-1);
		    	  
		    	  int size = imgItem.length();
		    	  String imageURL = imgItem.substring(end+4, size);
		    	  setAlImageFilename(imageFilename);	// testing this out - calls the set image filename arraylist setter that will add each image instance 
		    	  setAlImageURL(imageURL);

		    	
		      }
		      else if (items.get(i) instanceof VideoItem){
		    	  String videoItem = items.get(i).toString();
		    	  int start = videoItem.indexOf("Filename=");
		    	  int size = videoItem.length();
		    	  String videoFilename = videoItem.substring(start+9, size);
		    	  
		    	  setAlVideoFilename(videoFilename);
		    	  
		    	  
		      }
		          else if (items.get(i) instanceof AudioItem){
		        	  String audioItem = items.get(i).toString();
			    	  int start = audioItem.indexOf("Filename=");
			    	  int size = audioItem.length();
			    	  String audioFilename = audioItem.substring(start+9, size); 
			    	  
			    	  setAlAudioFilename(audioFilename);
		        	  
		          }
		      else if (items.get(i) instanceof TextItem){
		   
		    	  String textItem = items.get(i).toString();
		    	  int start = textItem.indexOf("Text=");
		    	  int end = textItem.indexOf("URL=");
		    	  int size = textItem.length();	    	  
		    	  String textText = textItem.substring(start+5, end-1);
		    	  String textURL = textItem.substring(end+4, size);	// need to add to some alTextUrl
		    	  
		    	  setAlTextText(textText);	// testing this out - calls the set image filename arraylist setter that will add each image instance 
		    	  setAlTextURL(textURL);
		    	  
		      }
	     	
	}
     

   // return key + "\n" + imageFilename + "\n" + imageURL + "\n" + videoFilename + "\n" +audioFilename + "\n" +textText + "\n" + textURL;
   // return sb.toString();
		
   return key + "\n" + alImageFilename + "\n" + alImageURL + "\n" + alVideoFilename + "\n" +alAudioFilename + "\n" +alTextText + "\n" + alTextURL;
   
 
    }



}
