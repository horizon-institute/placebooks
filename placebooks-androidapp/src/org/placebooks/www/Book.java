package org.placebooks.www;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.ListIterator;

//A book is an array list of items (items can be images, video, audio, text or gps trails)
public class Book {
	
	private String key;
	private String owner;
	
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
    
	StringBuilder sb = new StringBuilder();
	
	Iterator<Item> itr = items.iterator();
    while (itr.hasNext()) {
      String element = itr.next().toString();
      sb.append(element);
      sb.append("\n");
      System.out.print(element + " ");
    }
    System.out.println();

    return sb.toString();
   
 
    }

}
